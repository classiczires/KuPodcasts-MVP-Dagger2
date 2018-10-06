package ke.topcast.fragments.CategoriesFragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.topcast.data.network.Api;
import ke.topcast.R;
import ke.topcast.activities.MainActivity;
import ke.topcast.interfaces.OnLoadMoreListener;
import ke.topcast.models.Podcast;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryContentFragment extends Fragment {
    private CategoryContentAdapter categoryContentAdapter;
    private RecyclerView categoryContentRecycler;
    List<Podcast> podcastList;
    SwipeRefreshLayout swipeRefreshLayout;
    Context ctx;

    public CategoryContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category_content, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("دسته\u200Cها • " + CategoriesFragment.SelectedCategory);
        ctx = getContext();
        podcastList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.category_content_fragment_swipe_refresh);
        categoryContentRecycler = (RecyclerView) v.findViewById(R.id.category_content_fragment_recycler);
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        categoryContentRecycler.setLayoutManager(mLayoutManager1);
        categoryContentAdapter = new CategoryContentAdapter(podcastList);
        categoryContentRecycler.setAdapter(categoryContentAdapter);
        categoryContentAdapter.notifyDataSetChanged();

        getCategoryPodcasts(0, 10);
        return v;
    }


    public class CategoryContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private OnLoadMoreListener mOnLoadMoreListener;
        private List<Podcast> categoryPodcasts;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;



        public CategoryContentAdapter(List<Podcast> categoryPodcasts) {
            this.categoryPodcasts = categoryPodcasts;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) categoryContentRecycler.getLayoutManager();
            categoryContentRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            return podcastList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        public void setLoaded() {
            isLoading = false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_layout, parent, false);
                return new MainActivity.ListViewHolder(itemView);
            }else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.loading_layout, parent, false);
                return new MainActivity.LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MainActivity.ListViewHolder) {
                final MainActivity.ListViewHolder myViewHolder = (MainActivity.ListViewHolder) holder;
                myViewHolder.purchaseArt.setVisibility(View.GONE);
                Podcast podcast = categoryPodcasts.get(position);

                myViewHolder.title.setText(podcast.getTitle());
                myViewHolder.description.setText(podcast.getDescription());
                myViewHolder.duration.setText(podcast.getDuration());

                if (!podcast.getSku().equals("false"))
                    myViewHolder.purchaseArt.setVisibility(View.VISIBLE);

                RequestOptions options = new RequestOptions();
                options.override(60, 60);
                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(ctx)
                        .load(podcast.getImageUrl())
                        .apply(options)
                        .transition(withCrossFade().crossFade(200))
                        .into(myViewHolder.art);
            } else if (holder instanceof MainActivity.LoadingViewHolder) {
                MainActivity.LoadingViewHolder loadingViewHolder = (MainActivity.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return categoryPodcasts.size();
        }

    }


    private void getCategoryPodcasts(int limitFrom, int limitTo) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("category", CategoriesFragment.SelectedCategory)
                .addFormDataPart("limitFrom", String.valueOf(limitFrom))
                .addFormDataPart("limitTo", String.valueOf(limitTo))
                .build();

        GetCategoryPodcastsNetworkRequest request =
                request = new GetCategoryPodcastsNetworkRequest(Api.URL_CATEGORY_PODCAST, requestBody);

        request.execute();
    }


    private class GetCategoryPodcastsNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        RequestBody requestBody;

        GetCategoryPodcastsNetworkRequest(String url, RequestBody requestBody) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (podcastList.size() > 0 ){
                if (podcastList.get(podcastList.size() - 1) != null) {
                    podcastList.add(null);
                    categoryContentAdapter.notifyItemInserted(podcastList.size() - 1);
                }
                else this.cancel(true);
            }
            else {
                podcastList.add(null);
                categoryContentAdapter.notifyItemInserted(podcastList.size() - 1);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);

                JSONObject object = new JSONObject(s);

                if (podcastList.size() > 0 ){
                    podcastList.remove(podcastList.size() - 1);
                    categoryContentAdapter.notifyItemRemoved(podcastList.size());
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

                    podcastList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                }
                categoryContentAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                        .addHeader("Authorization", MainActivity.token)
                        .addHeader("Cache-Control", "no-cache")
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            }catch (Exception e){
                return null;
            }
        }
    }

}
