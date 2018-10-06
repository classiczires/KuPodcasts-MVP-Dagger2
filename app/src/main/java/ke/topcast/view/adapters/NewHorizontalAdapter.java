package ke.topcast.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import ke.topcast.Utils.Statics;
import ke.topcast.presenter.AdapterOps;
import ke.topcast.presenter.NewPodcastsPresenter;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class NewHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AdapterOps {

    private NewPodcastsPresenter newPodcastsPresenter;

    public NewHorizontalAdapter(NewPodcastsPresenter newPodcastsPresenter) {
        this.newPodcastsPresenter = newPodcastsPresenter;
    }


    public int getItemViewType(int position) {
        return newPodcastsPresenter.getPodcast(position) == null ? Statics.VIEW_TYPE_LOADING : Statics.VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        newPodcastsPresenter.createViewHolder(parent, viewType);
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        newPodcastsPresenter.bindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return newPodcastsPresenter.getPodcastsCount();
    }


    @Override
    public void notifyDataSetChange() { this.notifyDataSetChange(); }

    @Override
    public void notifyItemInsert(int position) { this.notifyItemInserted(position); }

    @Override
    public void notifyItemRemove(int position) { this.notifyItemRemoved(position); }
}
