package ke.topcast.view.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import ke.topcast.interfaces.OnLoadMoreListener;
import ke.topcast.utils.CommonUtils;
import ke.topcast.presenter.PodcastOps;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private PodcastOps podcastsPresenter;
    private boolean isGrid = true;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public MyAdapter(PodcastOps podcastsPresenter, boolean isGrid) {
        this.podcastsPresenter = podcastsPresenter;
        this.isGrid = isGrid;
    }

    public MyAdapter(PodcastOps podcastsPresenter, RecyclerView recyclerView, boolean isGrid) {
        this.podcastsPresenter = podcastsPresenter;
        this.isGrid = isGrid;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public int getItemViewType(int position) {
        return podcastsPresenter.getPodcast(position) == null ? CommonUtils.VIEW_TYPE_LOADING : CommonUtils.VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return podcastsPresenter.createViewHolder(parent, viewType, isGrid);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        podcastsPresenter.bindViewHolder(holder, position, isGrid);
    }

    @Override
    public int getItemCount() {
        return podcastsPresenter.getPodcastsCount();
    }


}
