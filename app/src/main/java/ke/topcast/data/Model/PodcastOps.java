package ke.topcast.data.model;

import java.util.List;

public interface PodcastOps {
    public void getPodcasts(int limitFrom, int limitTo);
    public void setPodcasts(List<Podcast> podcasts);
    public Podcast getPodcast(int position);
}
