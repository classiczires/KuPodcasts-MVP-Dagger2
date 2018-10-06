package ke.topcast.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import ke.topcast.R;
import ke.topcast.Utils.Statics;
import ke.topcast.model.data.Podcast;
import ke.topcast.model.network.Api;
import ke.topcast.model.network.NetworkRequest;
import ke.topcast.view.recyclerView.GridViewHolder;
import ke.topcast.view.recyclerView.LoadingViewHolder;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NewPodcastsPresenter implements PodcastOps {
    private List<Podcast> podcastList;

    @Override
    public void setPodcasts(List<Podcast> podcasts) {
        this.podcastList = podcasts;
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
    }

    @Override
    public void remove(int podcastPosition) {
        podcastList.remove(podcastPosition);
    }

    @Override
    public Podcast getPodcast(int position) {
        return podcastList.get(position);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Statics.VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_layout, parent, false);
            return new GridViewHolder(itemView);
        }else if (viewType == Statics.VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_layout, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GridViewHolder) {
            final GridViewHolder myViewHolder = (GridViewHolder) holder;
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
}
