package ke.topcast.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import ke.topcast.model.Podcast;
import ke.topcast.view.adapters.MyAdapter;
import okhttp3.RequestBody;

public interface PodcastOps {
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType, boolean isGrid);
    public void bindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isGrid);
    public void clearList();
    public int getPodcastsCount();
    public void loadPodcasts(HashMap<String, String> data, String api);
    public void remove(int podcastPosition);
    public void insert(Podcast podcast);
    public void setPodcasts(List<Podcast> podcasts, MyAdapter adapter);
    public Podcast getPodcast(int position);
    public void showToast(String message);
}
