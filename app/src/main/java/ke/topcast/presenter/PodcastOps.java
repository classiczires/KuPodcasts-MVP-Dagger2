package ke.topcast.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import ke.topcast.model.data.Podcast;

public interface PodcastOps {
    RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType);
    void bindViewHolder(RecyclerView.ViewHolder holder, int position);
    public int getPodcastsCount();
    public void remove(int podcastPosition);
    public void insert(Podcast podcast);
    public void setPodcasts(List<Podcast> podcasts);
    public Podcast getPodcast(int position);
    public void showToast(String message);
}
