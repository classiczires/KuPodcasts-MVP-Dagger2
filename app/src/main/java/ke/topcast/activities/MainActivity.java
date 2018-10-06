package ke.topcast.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ke.topcast.Data.ConnectToServer.Api;
import ke.topcast.R;
import ke.topcast.adapters.MostPupolarHorizontalAdapter;
import ke.topcast.adapters.NewHorizontalAdapter;
import ke.topcast.adapters.SuggestedHorizontalAdapter;
import ke.topcast.bottomnavigation.BottomNavigationViewEx;
import ke.topcast.clickitemtouchlistener.ClickItemTouchListener;
import ke.topcast.fragments.AccountFragment.AccountFragment;
import ke.topcast.fragments.CategoriesFragment.CategoriesFragment;
import ke.topcast.fragments.NewPodcastsFragment.NewPodcastsFragment;
import ke.topcast.fragments.PlayerFragment.PlayerFragment;
import ke.topcast.interfaces.OnLoadMoreListener;
import ke.topcast.interfaces.OnPodcastListener;
import ke.topcast.models.Podcast;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity implements OnPodcastListener, PlayerFragment.PlayerFragmentCallbackListener {
    private Context ctx;
    public static Typeface typeface;
    public static boolean RegisterPage = false;
    public static String MY_PREFS_NAME = "ZiresPrefsFile";
    public static String token = null;

    private BottomNavigationViewEx navigation;

    private RecyclerView newRecycler;
    private List<Podcast> newPodcastsList;
    private NewHorizontalAdapter newHorizontalAdapter;
    private RecyclerView suggestedRecycler;
    private List<Podcast> suggestedPodcastsList;
    private SuggestedHorizontalAdapter suggestedHorizontalAdapter;
    private RecyclerView mostPupolarRecycler;
    private List<Podcast> mostPupolarPodcastsList;
    private MostPupolarHorizontalAdapter mostPupolarHorizontalAdapter;
    private Menu menu;

    private RelativeLayout homeContent;
    private ScrollView homeContiner;
    private RecyclerView searchRecycler;
    private SearchPodcastsAdapter searchPodcastsAdapter;
    private List<Podcast> searchPodcastsList;

    SearchView searchView;
    String query;

    public static Podcast selectedPodcast;
    public static List<Podcast> playingListSelected;
    public static int queueCurrentIndex;

    PlayerFragment playerFragment;
    public static boolean isPlayerInView = false;
    public static boolean isPlayerVisible = false;
    View playerContainer;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (isPlayerVisible)
                hidePlayer();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showHomePage();
                    return true;
                case R.id.navigation_categories:
                    showCategories();
                    return true;
                case R.id.navigation_search:
                    showSearchPage();
                    return true;
                case R.id.navigation_user:
                    showAccountPage();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        token = prefs.getString("token", null);
        if (token == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            this.startActivity(mIntent);
            finish();
        }else {
            setContentView(R.layout.activity_main);
            //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            //getSupportActionBar().setCustomView(R.layout.action_bar_style);
            typeface = Typeface.createFromAsset(getAssets(), "fonts/Far_Casablanca.ttf");
            TextView titleText = (TextView) findViewById(R.id.favoritesRecyclerLabel);
            TextView titleText1 = (TextView) findViewById(R.id.newRecyclerLabel);
            TextView titleText2 = (TextView) findViewById(R.id.suggestedRecyclerLabel);
            titleText.setTypeface(typeface);
            titleText1.setTypeface(typeface);
            titleText2.setTypeface(typeface);
            ctx = this;
            searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
            homeContiner = (ScrollView) findViewById(R.id.homeContiner);

            navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.enableAnimation(true);
            navigation.enableShiftingMode(false);
            navigation.enableItemShiftingMode(true);

            playerContainer = findViewById(R.id.player_frag_container);
            homeContent = findViewById(R.id.homeContent);

            TextView newPodcastsShowMore = (TextView) findViewById(R.id.new_podcasts_view_all);

            newPodcastsShowMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                    NewPodcastsFragment newFragment = (NewPodcastsFragment) fm.findFragmentByTag("newPodcasts");
                    if (newFragment == null) {
                        newFragment = new NewPodcastsFragment();
                    }
                    fm.beginTransaction()
                            .add(R.id.fragContainer, newFragment, "newPodcasts")
                            .addToBackStack(newFragment.getClass().getName())
                            .commit();
                }
            });


            suggestedPodcastsList = new ArrayList<>();
            suggestedHorizontalAdapter = new SuggestedHorizontalAdapter(suggestedPodcastsList, ctx);
            suggestedRecycler = (RecyclerView) findViewById(R.id.suggested_podcasts_home);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
            suggestedRecycler.setLayoutManager(mLayoutManager);
            suggestedRecycler.setAdapter(suggestedHorizontalAdapter);
            suggestedHorizontalAdapter.notifyDataSetChanged();

            newPodcastsList = new ArrayList<>();
            newHorizontalAdapter = new NewHorizontalAdapter(newPodcastsList, ctx);
            newRecycler = (RecyclerView) findViewById(R.id.new_podcasts_home);
            LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
            newRecycler.setLayoutManager(mLayoutManager1);
            newRecycler.setAdapter(newHorizontalAdapter);
            newRecycler.addOnItemTouchListener(new ClickItemTouchListener(newRecycler) {
                @Override
                public boolean onClick(RecyclerView parent, View view, int position, long id) {
                    queueCurrentIndex = position;
                    selectedPodcast = newPodcastsList.get(position);
                    onPodcastSelected(selectedPodcast);
                    return false;
                }

                @Override
                public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                    return false;
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
            newHorizontalAdapter.notifyDataSetChanged();

            mostPupolarPodcastsList = new ArrayList<>();
            mostPupolarHorizontalAdapter = new MostPupolarHorizontalAdapter(mostPupolarPodcastsList, ctx);
            mostPupolarRecycler = (RecyclerView) findViewById(R.id.favorites_podcasts_home);
            LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
            mostPupolarRecycler.setLayoutManager(mLayoutManager2);
            mostPupolarRecycler.setAdapter(mostPupolarHorizontalAdapter);
            mostPupolarHorizontalAdapter.notifyDataSetChanged();

            getSuggestedPodcasts(0, 50);
            getNewPodcasts(0, 50);
            getMostPopularPodcasts(0, 50);
        }
    }

    @Override
    public void onBackPressed() {
        if (isPlayerVisible){
            hidePlayer();
        }else {
            if (navigation.getCurrentItem() == 0){
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                android.support.v4.app.Fragment frag = fm.findFragmentByTag("newPodcasts");
                if (frag != null)
                    hideFragment("newPodcasts");
                else
                    startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));

                setTitle(getString(R.string.app_name));
            }else if (navigation.getCurrentItem() == 1){
                android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
                android.support.v4.app.Fragment frag = fm.findFragmentByTag("categoryContent");
                if (frag == null) {
                    navigation.setCurrentItem(0);
                    hideFragment("categories");
                }else {
                    hideFragment("categoryContent");
                    setTitle("دسته\u200Cها");
                }

            }else if (navigation.getCurrentItem() == 2){
                searchRecycler.setVisibility(View.GONE);
                homeContiner.setVisibility(View.VISIBLE);
                menu.findItem(R.id.action_search).setVisible(false);
                ((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).setIconified(true);
                navigation.setCurrentItem(0);
            }else if (navigation.getCurrentItem() == 3) {
                hideFragment("account");
                navigation.setCurrentItem(0);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        ((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).setIconifiedByDefault(false);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            searchView.setIconified(false);
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            setTitle(getString(R.string.app_name));
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void setTitle(String title){
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(title);
    }


    void showCategories(){
        menu.findItem(R.id.action_search).setVisible(false);
        ((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).setIconified(true);
        hideKeyboard(this);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        CategoriesFragment categoriesFragment = (CategoriesFragment) fm.findFragmentByTag("categories");
        if (categoriesFragment == null) {
            hideAllFrags();
            categoriesFragment = new CategoriesFragment();
            fm.beginTransaction()
                    .add(R.id.fragContainer, categoriesFragment, "categories")
                    .addToBackStack(categoriesFragment.getClass().getName())
                    .commit();
            setTitle("دسته\u200Cها");
        }


    }

    void showHomePage(){
        hideAllFrags();
        searchRecycler.setVisibility(View.GONE);
        homeContiner.setVisibility(View.VISIBLE);
        menu.findItem(R.id.action_search).setVisible(false);
        ((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).setIconified(true);
        hideKeyboard(this);
        setTitle(getString(R.string.app_name));
    }

    void showSearchPage(){
        hideAllFrags();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        homeContiner.setVisibility(View.GONE);
        searchRecycler.setVisibility(View.VISIBLE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        searchRecycler.setLayoutManager(mLayoutManager);
        searchPodcastsList = new ArrayList<>();
        searchPodcastsAdapter = new SearchPodcastsAdapter();
        searchRecycler.setAdapter(searchPodcastsAdapter);
        searchPodcastsAdapter.notifyDataSetChanged();

        menu.findItem(R.id.action_search).setVisible(true);
        ((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).setIconified(false);
    }

    void showAccountPage(){

        menu.findItem(R.id.action_search).setVisible(false);
        ((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).setIconified(true);
        hideKeyboard(this);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        AccountFragment accountFragment = (AccountFragment) fm.findFragmentByTag("account");
        if (accountFragment == null) {
            hideAllFrags();
            accountFragment = new AccountFragment();
            fm.beginTransaction()
                    .add(R.id.fragContainer, accountFragment, "account")
                    .addToBackStack(accountFragment.getClass().getName())
                    .commit();
            setTitle("حساب");
        }
    }

    void setContentBottomMargin(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        if (isPlayerInView) {
            params.setMargins(0, 0, 0, navigation.getHeight());
            homeContiner.setLayoutParams(params);
        }else {
            params.setMargins(0, 0, 0, 0 );
            homeContiner.setLayoutParams(params);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideAllFrags() {
        hideFragment("newPodcasts");
        hideFragment("categories");
        hideFragment("account");
        hideFragment("categoryContent");
    }

    public void hideFragment(String type) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment frag = fm.findFragmentByTag(type);
        if (frag != null) {
            fm.beginTransaction()
                    .remove(frag)
                    .commit();
            fm.popBackStack(frag.getClass().getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }



    public void onPodcastSelected(Podcast podcast){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment newFragment = new PlayerFragment();
        if (playerFragment == null) {
            playerFragment = newFragment;

            isPlayerVisible = true;
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.player_frag_container, newFragment, "player")
                    .addToBackStack(null)
                    .commit();
            isPlayerInView = true;
            setContentBottomMargin();
        } else {
        }
    }

    public void onAddedtoBookmark(Podcast podcast){

    }

    public void onSmallPlayerTouched() {
        if (!isPlayerVisible) {
            showPlayer();
        } else {
            hidePlayer();
        }
    }

    public void showPlayer() {
        isPlayerVisible = true;
        if (playerFragment != null && playerFragment.smallPlayer != null) {
            playerFragment.smallPlayer.animate()
                    .alpha(0.0f);
        }
        if (playerFragment != null && playerFragment.spToolbar != null) {
            playerFragment.spToolbar.setVisibility(View.VISIBLE);
            playerFragment.spToolbar.animate().alpha(1.0f);
        }

        playerContainer.animate()
                .setDuration(300)
                .translationY(0);
    }

    public void hidePlayer() {
        isPlayerVisible = false;
        if (playerFragment != null && playerFragment.smallPlayer != null) {
            playerFragment.smallPlayer.setAlpha(0.0f);
            playerFragment.smallPlayer.setVisibility(View.VISIBLE);
            playerFragment.smallPlayer.animate()
                    .alpha(1.0f);
        }

        if (playerFragment != null && playerFragment.spToolbar != null) {
            playerFragment.spToolbar.animate()
                    .alpha(0.0f);
            playerFragment.spToolbar.setVisibility(GONE);
        }

        if (playerFragment != null) {
            playerContainer.animate()
                    .translationY(playerContainer.getHeight() - playerFragment.smallPlayer.getHeight())
                    .setDuration(300);
        } else {
            playerContainer.animate()
                    .translationY(playerContainer.getHeight() - playerFragment.smallPlayer.getHeight())
                    .setDuration(300)
                    .setStartDelay(500);
        }
    }









    public class SearchPodcastsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;


        public SearchPodcastsAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) newRecycler.getLayoutManager();
            newRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }


        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return searchPodcastsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        public void setLoaded() {
            isLoading = false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_layout, parent, false);
                return new ListViewHolder(itemView);
            }else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.loading_layout, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ListViewHolder) {
                final ListViewHolder myViewHolder = (ListViewHolder) holder;
                myViewHolder.purchaseArt.setVisibility(View.GONE);

                Podcast podcast = searchPodcastsList.get(position);

                myViewHolder.title.setText(podcast.getTitle());
                myViewHolder.description.setText(podcast.getDescription());
                myViewHolder.duration.setText(podcast.getDuration());

                if (!podcast.getSku().equals("false"))
                    myViewHolder.purchaseArt.setVisibility(View.VISIBLE);

                RequestOptions options = new RequestOptions();
                options.override(56, 56);
                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(ctx)
                        .load(podcast.getImageUrl())
                        .apply(options)
                        .transition(withCrossFade().crossFade(200))
                        .into(myViewHolder.art);
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return searchPodcastsList.size();
        }

    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        public ImageView art, purchaseArt;
        public TextView title, description, duration;

        public GridViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.backImage);
            purchaseArt = (ImageView) view.findViewById(R.id.card_purchase);
            title = (TextView) view.findViewById(R.id.card_title);
            description = (TextView) view.findViewById(R.id.card_description);
            duration = (TextView) view.findViewById(R.id.card_duration);
        }
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        public ImageView art, purchaseArt;
        public TextView title, description, duration;

        public ListViewHolder(View view) {
            super(view);
            art = (ImageView) view.findViewById(R.id.img);
            purchaseArt = (ImageView) view.findViewById(R.id.item_list_purchase);
            title = (TextView) view.findViewById(R.id.item_list_title);
            description = (TextView) view.findViewById(R.id.item_list_description);
            duration = (TextView) view.findViewById(R.id.item_list_duration);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadingProgressBar);
        }
    }



    //Connect to server database
    private void getSuggestedPodcasts(int limitFrom, int limitTo) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("category", "پیشنهادی")
                .addFormDataPart("limitFrom", String.valueOf(limitFrom))
                .addFormDataPart("limitTo", String.valueOf(limitTo))
                .build();

        GetSugesstedPodcastsNetworkRequest request =
                request = new GetSugesstedPodcastsNetworkRequest(Api.URL_CATEGORY_PODCAST, requestBody);

        request.execute();
    }

    private void getNewPodcasts(int limitFrom, int limitTo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("limitFrom", String.valueOf(limitFrom));
        params.put("limitTo", String.valueOf(limitTo));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("limitFrom", params.get("limitFrom"))
                .addFormDataPart("limitTo", params.get("limitTo"))
                .build();

        GetNewPodcastsNetworkRequest request =
                request = new GetNewPodcastsNetworkRequest(Api.URL_GET_NEW_PODCASTS, requestBody);

        request.execute();
    }

    private void getMostPopularPodcasts(int limitFrom, int limitTo) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("category", "محبوب ترین ها")
                .addFormDataPart("limitFrom", String.valueOf(limitFrom))
                .addFormDataPart("limitTo", String.valueOf(limitTo))
                .build();

        GetMostPapolarPodcastsNetworkRequest request =
                request = new GetMostPapolarPodcastsNetworkRequest(Api.URL_CATEGORY_PODCAST, requestBody);

        request.execute();
    }

    private void searchPodcasts(String term, int limitFrom, int limitTo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("limitFrom", String.valueOf(limitFrom));
        params.put("limitTo", String.valueOf(limitTo));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("term", term)
                .addFormDataPart("limitFrom", params.get("limitFrom"))
                .addFormDataPart("limitTo", params.get("limitTo"))
                .build();

        SearchNetworkRequest request =
                request = new SearchNetworkRequest(Api.URL_SEARCH, requestBody);

        request.execute();
    }



    private class GetNewPodcastsNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        RequestBody requestBody;

        GetNewPodcastsNetworkRequest(String url, RequestBody requestBody) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (newPodcastsList.size() > 0 ){
                if (newPodcastsList.get(newPodcastsList.size() - 1) != null) {
                    newPodcastsList.add(null);
                    newHorizontalAdapter.notifyItemInserted(newPodcastsList.size() - 1);
                }
                else this.cancel(true);
            }
            else {
                newPodcastsList.add(null);
                newHorizontalAdapter.notifyItemInserted(newPodcastsList.size() - 1);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                if (s != null){
                    JSONObject object = new JSONObject(s);

                    if (newPodcastsList.size() > 0 ){
                        newPodcastsList.remove(newPodcastsList.size() - 1);
                        newHorizontalAdapter.notifyItemRemoved(newPodcastsList.size());
                    }
                    JSONArray jsonArray = object.getJSONArray("podcasts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        String title = jso.getString("title");
                        String description = jso.getString("description");
                        String duration = jso.getString("duration");
                        String programBuilder = jso.getString("programBuilder");
                        String narrators = jso.getString("narrators");
                        String sku = jso.getString("sku");
                        String imageUrl = jso.getString("coverPath");
                        String podcastUrl = jso.getString("podcastPath");

                        newPodcastsList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                    }
                    newHorizontalAdapter.notifyDataSetChanged();

                }else
                    Toast.makeText(ctx, "خطا در اتصال", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("Authorization", token)
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            }catch (Exception e){
                return null;
            }
        }
    }

    private class GetSugesstedPodcastsNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        RequestBody requestBody;

        GetSugesstedPodcastsNetworkRequest(String url, RequestBody requestBody) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (suggestedPodcastsList.size() > 0 ){
                if (suggestedPodcastsList.get(suggestedPodcastsList.size() - 1) != null) {
                    suggestedPodcastsList.add(null);
                    suggestedHorizontalAdapter.notifyItemInserted(suggestedPodcastsList.size() - 1);
                }
                else this.cancel(true);
            }
            else {
                suggestedPodcastsList.add(null);
                suggestedHorizontalAdapter.notifyItemInserted(suggestedPodcastsList.size() - 1);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);

                if (s != null){
                    JSONObject object = new JSONObject(s);

                    if (suggestedPodcastsList.size() > 0 ){
                        suggestedPodcastsList.remove(suggestedPodcastsList.size() - 1);
                        suggestedHorizontalAdapter.notifyItemRemoved(suggestedPodcastsList.size());
                    }
                    JSONArray jsonArray = object.getJSONArray("podcasts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        String title = jso.getString("title");
                        String description = jso.getString("description");
                        String duration = jso.getString("duration");
                        String programBuilder = jso.getString("programBuilder");
                        String narrators = jso.getString("narrators");
                        String sku = jso.getString("sku");
                        String imageUrl = jso.getString("coverPath");
                        String podcastUrl = jso.getString("podcastPath");

                        suggestedPodcastsList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                    }
                    suggestedHorizontalAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(ctx, "خطا در اتصال", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("Authorization", token)
                        .addHeader("Cache-Control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            }catch (Exception e){
                return null;
            }
        }
    }

    private class GetMostPapolarPodcastsNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        RequestBody requestBody;

        GetMostPapolarPodcastsNetworkRequest(String url, RequestBody requestBody) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mostPupolarPodcastsList.size() > 0 ){
                if (mostPupolarPodcastsList.get(mostPupolarPodcastsList.size() - 1) != null) {
                    mostPupolarPodcastsList.add(null);
                    mostPupolarHorizontalAdapter.notifyItemInserted(mostPupolarPodcastsList.size() - 1);
                }
                else this.cancel(true);
            }
            else {
                mostPupolarPodcastsList.add(null);
                mostPupolarHorizontalAdapter.notifyItemInserted(mostPupolarPodcastsList.size() - 1);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                if (s != null){
                    JSONObject object = new JSONObject(s);

                    if (mostPupolarPodcastsList.size() > 0 ){
                        mostPupolarPodcastsList.remove(mostPupolarPodcastsList.size() - 1);
                        mostPupolarHorizontalAdapter.notifyItemRemoved(mostPupolarPodcastsList.size());
                    }
                    JSONArray jsonArray = object.getJSONArray("podcasts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        String title = jso.getString("title");
                        String description = jso.getString("description");
                        String duration = jso.getString("duration");
                        String programBuilder = jso.getString("programBuilder");
                        String narrators = jso.getString("narrators");
                        String sku = jso.getString("sku");
                        String imageUrl = jso.getString("coverPath");
                        String podcastUrl = jso.getString("podcastPath");

                        mostPupolarPodcastsList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                    }
                    mostPupolarHorizontalAdapter.notifyDataSetChanged();
                }else
                    Toast.makeText(ctx, "خطا در اتصال", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("Authorization", token)
                        .addHeader("Cache-Control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            }catch (Exception e){
                return null;
            }
        }
    }

    private class SearchNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        RequestBody requestBody;

        SearchNetworkRequest(String url, RequestBody requestBody) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (searchPodcastsList.size() > 0 ){
                if (searchPodcastsList.get(searchPodcastsList.size() - 1) != null) {
                    searchPodcastsList.add(null);
                    searchPodcastsAdapter.notifyItemInserted(searchPodcastsList.size() - 1);
                }
                else this.cancel(true);
            }
            else {
                searchPodcastsList.add(null);
                searchPodcastsAdapter.notifyItemInserted(searchPodcastsList.size() - 1);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                if (s != null){
                    JSONObject object = new JSONObject(s);

                    if (searchPodcastsList.size() > 0 ){
                        searchPodcastsList.remove(searchPodcastsList.size() - 1);
                        searchPodcastsAdapter.notifyItemRemoved(searchPodcastsList.size());
                    }
                    JSONArray jsonArray = object.getJSONArray("podcasts");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jso = jsonArray.getJSONObject(i);
                        String title = jso.getString("title");
                        String description = jso.getString("description");
                        String duration = jso.getString("duration");
                        String programBuilder = jso.getString("programBuilder");
                        String narrators = jso.getString("narrators");
                        String sku = jso.getString("sku");
                        String imageUrl = jso.getString("coverPath");
                        String podcastUrl = jso.getString("podcastPath");

                        searchPodcastsList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                    }
                    searchPodcastsAdapter.notifyDataSetChanged();
                    searchPodcastsAdapter.setLoaded();
                }else
                    Toast.makeText(ctx, "خطا در اتصال", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                        .addHeader("Authorization", token)
                        .addHeader("Cache-Control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            }catch (Exception e){
                return null;
            }
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }

            searchPodcastsList.clear();
            searchPodcastsAdapter.notifyDataSetChanged();
            this.query = query;
            searchPodcasts(query, 0, 10);
        }
    }
}
