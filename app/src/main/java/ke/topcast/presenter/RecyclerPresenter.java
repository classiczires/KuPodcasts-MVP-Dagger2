package ke.topcast.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import ke.topcast.R;
import ke.topcast.interfaces.OnLoadMoreListener;
import ke.topcast.utils.CommonUtils;
import ke.topcast.model.Podcast;
import ke.topcast.model.network.NetworkRequest;
import ke.topcast.view.adapters.MyAdapter;
import ke.topcast.view.recyclerView.MyViewHolder;
import ke.topcast.view.recyclerView.LoadingViewHolder;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RecyclerPresenter implements OnNetworkTaskCompleted,  PodcastOps {
    private List<Podcast> podcastList;

    Context context;

    String api;
    HashMap<String, String> data;

    public MyAdapter adapter;

    NetworkRequest networkRequest;

    public RecyclerPresenter(List<Podcast> podcastList, Context context) {
        this.context = context;
        this.podcastList = podcastList;
    }


    public void setAdapter(MyAdapter adapter) {
        this.adapter = adapter;
        this.adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                data.remove("limitFrom");
                data.put("limitFrom", String.valueOf(getPodcastsCount()));
                loadPodcasts(data, api);
            }
        });
    }

    @Override
    public void OnNetworkTaskCompleted(List<Podcast> podcasts) {
        if (getPodcastsCount() > 0) {
            remove(getPodcastsCount() - 1);
            adapter.notifyItemRemoved(getPodcastsCount());
        }
        if (podcasts != null) {
            podcastList.addAll(podcasts);
        } else
            Toast.makeText(context, "خطای سرور", Toast.LENGTH_LONG).show();

        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadPodcasts(HashMap<String, String> data, final String api) {
        this.api = api;
        this.data = data;

        MultipartBody .Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (String key : data.keySet()) {
            builder.addFormDataPart(key, data.get(key));
        }

        if (getPodcastsCount() > 0) {
            if (getPodcast(getPodcastsCount() - 1) != null) {
                insert(null);
                adapter.notifyItemInserted(getPodcastsCount() - 1);
            }
        } else {
            insert(null);
            adapter.notifyItemInserted(getPodcastsCount() - 1);
        }

        networkRequest = new NetworkRequest(api, builder.build(), context, this);
        if (networkRequest.isNetworkConnected())
            networkRequest.execute();
        else
            Toast.makeText(context, "اتصال اینترنت خود را روشن کنید", Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPodcasts(List<Podcast> podcasts, MyAdapter adapter) {
        this.podcastList = podcasts;

    }

    @Override
    public void clearList() {
        podcastList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public int getPodcastsCount() {
        return podcastList.size();
    }

    @Override
    public void insert(Podcast podcast) {
        podcastList.add(podcast);
        adapter.notifyItemInserted(getPodcastsCount() - 1);
    }

    @Override
    public void remove(int podcastPosition) {
        podcastList.remove(podcastPosition);
        adapter.notifyItemRemoved(podcastPosition);
    }

    @Override
    public Podcast getPodcast(int position) {
        return podcastList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType, boolean isGrid) {
        if (viewType == CommonUtils.VIEW_TYPE_ITEM) {
            View itemView;
            if (isGrid){
                 itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_card_layout, parent, false);
            }else {
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_list_layout, parent, false);
            }
            return new MyViewHolder(itemView, isGrid);
        } else if (viewType == CommonUtils.VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_layout, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isGrid) {
        if (holder instanceof MyViewHolder) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;

            myViewHolder.purchaseArt.setVisibility(View.GONE);
            Podcast podcast = podcastList.get(position);

            myViewHolder.title.setText(podcast.getTitle());
            myViewHolder.description.setText(podcast.getDescription());
            myViewHolder.duration.setText(podcast.getDuration());

            if (!podcast.getSku().equals("false"))
                myViewHolder.purchaseArt.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions();
            options.override(115, 115);
            options.diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context)
                    .load(podcast.getImageUrl())
                    .apply(options)
                    .transition(withCrossFade().crossFade(200))
                    .into(myViewHolder.art);

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
}
