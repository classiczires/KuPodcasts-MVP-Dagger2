package ke.topcast.di.module;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ke.topcast.model.Podcast;
import ke.topcast.presenter.Presenter;
import ke.topcast.presenter.RecyclerPresenter;
import ke.topcast.view.adapters.MyAdapter;

@Module
public class MainActivityModule {
    private RecyclerPresenter searchRP;
    private RecyclerPresenter newPodcastsRP;
    private RecyclerPresenter suggestedPodcastsRP;
    private RecyclerPresenter popularPodcastsRP;

    private MyAdapter searchAdapter;
    private MyAdapter newHorizontalAdapter;
    private MyAdapter suggestedHorizontalAdapter;
    private MyAdapter pupolarHorizontalAdapter;

    private RecyclerView searchRecyclerView;

    Context context;

    public MainActivityModule(Context context, RecyclerView searchRecyclerView) {
        this.context = context;
        this.searchRecyclerView = searchRecyclerView;
    }


    @Provides
    @Singleton
    Presenter providePresenter(){
        searchRP = new RecyclerPresenter(new ArrayList<Podcast>(), context);
        newPodcastsRP = new RecyclerPresenter(new ArrayList<Podcast>(), context);
        suggestedPodcastsRP = new RecyclerPresenter(new ArrayList<Podcast>(), context);
        popularPodcastsRP = new RecyclerPresenter(new ArrayList<Podcast>(), context);

        searchAdapter = new MyAdapter(searchRP, searchRecyclerView, false);
        newHorizontalAdapter = new MyAdapter(newPodcastsRP, true);
        suggestedHorizontalAdapter = new MyAdapter(suggestedPodcastsRP,true);
        pupolarHorizontalAdapter = new MyAdapter(popularPodcastsRP, true);

        searchRP.setAdapter(searchAdapter);
        newPodcastsRP.setAdapter(newHorizontalAdapter);
        suggestedPodcastsRP.setAdapter(suggestedHorizontalAdapter);
        popularPodcastsRP.setAdapter(pupolarHorizontalAdapter);

        return new Presenter(searchRP, newPodcastsRP, suggestedPodcastsRP, popularPodcastsRP);
    }

}
