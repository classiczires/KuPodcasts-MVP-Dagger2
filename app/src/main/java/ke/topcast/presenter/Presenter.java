package ke.topcast.presenter;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

import ke.topcast.utils.Api;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Presenter {
    public RecyclerPresenter searchRP;
    public RecyclerPresenter newPodcastsRP;
    public RecyclerPresenter suggestedPodcastsRP;
    public RecyclerPresenter popularPodcastsRP;


    public Presenter(RecyclerPresenter searchRP, RecyclerPresenter newPodcastsRP, RecyclerPresenter suggestedPodcastsRP, RecyclerPresenter popularPodcastsRP) {
        this.searchRP = searchRP;
        this.newPodcastsRP = newPodcastsRP;
        this.suggestedPodcastsRP = suggestedPodcastsRP;
        this.popularPodcastsRP = popularPodcastsRP;
    }


    public void initializeRP(){
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("limitFrom", String.valueOf(0));
        data.put("limitTo", String.valueOf(50));

        newPodcastsRP.loadPodcasts(data, Api.URL_GET_NEW_PODCASTS);
        data.put("category", "پیشنهادی");
        suggestedPodcastsRP.loadPodcasts( data, Api.URL_CATEGORY_PODCAST);

        data.remove("category");
        data.put("category", "محبوب ترین ها");
        popularPodcastsRP.loadPodcasts(data, Api.URL_GET_NEW_PODCASTS);
    }

    public void searchPodcasts(String term, int limitFrom, int limitTo) {
        searchRP.adapter.notifyDataSetChanged();
        searchRP.clearList();
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("limitFrom", String.valueOf(0));
        data.put("limitTo", String.valueOf(10));
        data.put("term", term);

        searchRP.loadPodcasts(data, Api.URL_SEARCH);
    }
}
