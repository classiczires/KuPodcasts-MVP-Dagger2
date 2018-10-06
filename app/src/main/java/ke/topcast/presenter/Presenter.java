package ke.topcast.presenter;

import java.util.HashMap;

import ke.topcast.model.network.Api;
import ke.topcast.model.network.NetworkRequest;
import ke.topcast.view.adapters.NewHorizontalAdapter;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Presenter {
    NewPodcastsPresenter newPodcastsPresenter;

    void initializeNewPodcasts(){
        newPodcastsPresenter = new NewPodcastsPresenter();
        newHorizontalAdapter = new NewHorizontalAdapter(newPodcastsPresenter);
    }


    public void getNewPodcasts(int limitFrom, int limitTo) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("limitFrom", String.valueOf(limitFrom))
                .addFormDataPart("limitTo", String.valueOf(limitTo))
                .build();

        NetworkRequest request =
                request = new NetworkRequest(Api.URL_GET_NEW_PODCASTS, requestBody, newPodcastsPresenter, newHorizontalAdapter);

        request.execute();
    }
}
