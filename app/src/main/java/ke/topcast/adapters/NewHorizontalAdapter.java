package ke.topcast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.lang.annotation.Target;
import java.util.List;

import ke.topcast.R;
import ke.topcast.activities.MainActivity;
import ke.topcast.models.Podcast;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NewHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<Podcast> newPodcasts;
    Context ctx;

    public NewHorizontalAdapter(List<Podcast> newPodcasts, Context ctx) {
        this.newPodcasts = newPodcasts;
        this.ctx = ctx;
    }


    public int getItemViewType(int position) {
        return newPodcasts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_layout, parent, false);
            return new MainActivity.GridViewHolder(itemView);
        }else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loading_layout, parent, false);
            return new MainActivity.LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MainActivity.GridViewHolder) {
            final MainActivity.GridViewHolder myViewHolder = (MainActivity.GridViewHolder) holder;
            myViewHolder.purchaseArt.setVisibility(View.GONE);
            Podcast podcast = newPodcasts.get(position);

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
