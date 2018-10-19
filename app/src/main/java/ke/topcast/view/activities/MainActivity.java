package ke.topcast.view.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import javax.inject.Inject;

import ke.topcast.R;
import ke.topcast.di.component.DaggerMainActivityComponent;
import ke.topcast.di.component.MainActivityComponent;
import ke.topcast.di.module.MainActivityModule;
import ke.topcast.presenter.Presenter;
import ke.topcast.presenter.RequiredPresenterOps;
import ke.topcast.utils.CommonUtils;
import ke.topcast.view.bottomnavigation.BottomNavigationViewEx;
import ke.topcast.view.clickitemtouchlistener.ClickItemTouchListener;
import ke.topcast.view.fragments.AccountFragment.AccountFragment;
import ke.topcast.view.fragments.CategoriesFragment.CategoriesFragment;
import ke.topcast.view.fragments.NewPodcastsFragment.NewPodcastsFragment;
import ke.topcast.view.fragments.PlayerFragment.PlayerFragment;
import ke.topcast.interfaces.OnPodcastListener;
import ke.topcast.model.Podcast;

import static android.view.View.GONE;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MainActivity extends AppCompatActivity implements OnPodcastListener
        , PlayerFragment.PlayerFragmentCallbackListener, RequiredPresenterOps {

    private Context ctx;
    private BottomNavigationViewEx navigation;

    private RecyclerView newRecycler;
    private RecyclerView suggestedRecycler;
    private RecyclerView mostPupolarRecycler;

    private Menu menu;
    private RelativeLayout homeContent;
    private ScrollView homeContiner;
    private RecyclerView searchRecycler;
    SearchView searchView;
    String query;

    PlayerFragment playerFragment;
    public static boolean isPlayerInView = false;
    public static boolean isPlayerVisible = false;
    View playerContainer;

    @Inject
    Presenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(CommonUtils.MY_PREFS_NAME, MODE_PRIVATE);
        CommonUtils.token = prefs.getString("token", null);
        if (CommonUtils.token == null) {
            Intent mIntent = new Intent(this, LoginActivity.class);
            this.startActivity(mIntent);
            finish();
        }else {
            setContentView(R.layout.activity_main);
            //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            //getSupportActionBar().setCustomView(R.layout.action_bar_style);
            CommonUtils.typeface = Typeface.createFromAsset(getAssets(), "fonts/Far_Casablanca.ttf");
            TextView titleText = (TextView) findViewById(R.id.favoritesRecyclerLabel);
            TextView titleText1 = (TextView) findViewById(R.id.newRecyclerLabel);
            TextView titleText2 = (TextView) findViewById(R.id.suggestedRecyclerLabel);
            titleText.setTypeface(CommonUtils.typeface);
            titleText1.setTypeface(CommonUtils.typeface);
            titleText2.setTypeface(CommonUtils.typeface);
            ctx = this;

            //MainActivityComponent component = DaggerMainActivityComponent.Build()

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
            searchRecycler = (RecyclerView) findViewById(R.id.searchRecycler);
            searchRecycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false));

            suggestedRecycler = (RecyclerView) findViewById(R.id.suggested_podcasts_home);
            suggestedRecycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

            newRecycler = (RecyclerView) findViewById(R.id.new_podcasts_home);
            newRecycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));
            newRecycler.addOnItemTouchListener(new ClickItemTouchListener(newRecycler) {
                @Override
                public boolean onClick(RecyclerView parent, View view, int position, long id) {
                    CommonUtils.queueCurrentIndex = position;
                    CommonUtils.selectedPodcast = homePresenter.newPodcastsRP.getPodcast(position);
                    onPodcastSelected(CommonUtils.selectedPodcast);
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

            mostPupolarRecycler = (RecyclerView) findViewById(R.id.favorites_podcasts_home);
            mostPupolarRecycler.setLayoutManager(new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false));

            MainActivityComponent component = DaggerMainActivityComponent.builder().
                    mainActivityModule(new MainActivityModule(ctx, searchRecycler)).build();

            component.inject(this);

            newRecycler.setAdapter(homePresenter.newPodcastsRP.adapter);
            suggestedRecycler.setAdapter(homePresenter.suggestedPodcastsRP.adapter);
            mostPupolarRecycler.setAdapter(homePresenter.popularPodcastsRP.adapter);
            searchRecycler.setAdapter(homePresenter.searchRP.adapter);

/*
            homePresenter.popularPodcastsRP.adapter.notifyDataSetChanged();
            homePresenter.newPodcastsRP.adapter.notifyDataSetChanged();
            homePresenter.suggestedPodcastsRP.adapter.notifyDataSetChanged();
            homePresenter.searchRP.adapter.notifyDataSetChanged();
*/
            homePresenter.initializeRP();
        }
    }

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
        homePresenter.searchRP.clearList();

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


    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();
            }

            this.query = query;
            homePresenter.searchPodcasts(query, 0, 10);
        }
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }
}
