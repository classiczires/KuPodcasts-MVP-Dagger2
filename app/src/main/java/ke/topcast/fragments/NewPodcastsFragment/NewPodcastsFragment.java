package ke.topcast.fragments.NewPodcastsFragment;

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
import java.util.HashMap;
import java.util.List;

import ke.topcast.Data.ConnectToServer.Api;
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


public class NewPodcastsFragment extends Fragment {

    private NewPodcastsAdapter newPodcastsAdapter;
    private RecyclerView newRecycler;
    List<Podcast> podcastList;
    SwipeRefreshLayout swipeRefreshLayout;

    public NewPodcastsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("تازه\u200Cها");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        podcastList = new ArrayList<>();

        View v = inflater.inflate(R.layout.fragment_new_podcasts, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.new_podcasts_fragment_swipe_refresh);
        newRecycler = (RecyclerView) v.findViewById(R.id.new_podcasts_fragment_recycler);
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        newRecycler.setLayoutManager(mLayoutManager1);
        newPodcastsAdapter = new NewPodcastsAdapter(podcastList);
        newRecycler.setAdapter(newPodcastsAdapter);
        newPodcastsAdapter.notifyDataSetChanged();

        newPodcastsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getPodcasts(podcastList.size(), 10);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                podcastList.clear();
                newPodcastsAdapter.notifyDataSetChanged();
                getPodcasts(0, 10);
            }
        });
        getPodcasts(0, 10);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }











    //Connect to server database

    private void getPodcasts(int limitFrom, int limitTo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("limitFrom", String.valueOf(limitFrom));
        params.put("limitTo", String.valueOf(limitTo));

        PerformNetworkRequest request =
                request = new PerformNetworkRequest(Api.URL_GET_NEW_PODCASTS, params);

        request.execute();

    }


    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        //the url where we need to send the request
        String url;

        //the parameters
        final HashMap<String, String> params;

        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params) {
            this.url = url;
            this.params = params;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                if (podcastList.size() > 0 ){
                    if (podcastList.get(podcastList.size() - 1) != null) {
                        podcastList.add(null);
                        newPodcastsAdapter.notifyItemInserted(podcastList.size() - 1);
                    }
                    else this.cancel(true);
                }
                else {
                    podcastList.add(null);
                    newPodcastsAdapter.notifyItemInserted(podcastList.size() - 1);
                }
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                if (s != null){
                    JSONObject object = new JSONObject(s);

                    if (podcastList.size() > 0 ){
                        podcastList.remove(podcastList.size() - 1);
                        newPodcastsAdapter.notifyItemRemoved(podcastList.size());
                    }

                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);

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
                    newPodcastsAdapter.notifyDataSetChanged();
                    newPodcastsAdapter.setLoaded();
                }else
                    Toast.makeText(getContext(), "خطا در اتصال", Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("limitFrom", params.get("limitFrom"))
                        .addFormDataPart("limitTo", params.get("limitTo"))
                        .build();

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
                return e.getMessage();
            }
        }
    }


    public class NewPodcastsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private OnLoadMoreListener mOnLoadMoreListener;
        private List<Podcast> newPodcasts;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;



        public NewPodcastsAdapter(List<Podcast> newPodcasts) {
            this.newPodcasts = newPodcasts;
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
                Podcast podcast = newPodcasts.get(position);

                myViewHolder.title.setText(podcast.getTitle());
                myViewHolder.description.setText(podcast.getDescription());
                myViewHolder.duration.setText(podcast.getDuration());

                if (!podcast.getSku().equals("false"))
                    myViewHolder.purchaseArt.setVisibility(View.VISIBLE);

                RequestOptions options = new RequestOptions();
                options.override(60, 60);
                options.diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getContext())
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
            return newPodcasts.size();
        }

    }
}
