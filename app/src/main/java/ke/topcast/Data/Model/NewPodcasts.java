package ke.topcast.Data.Model;

import java.util.HashMap;

import ke.topcast.Data.ConnectToServer.Api;
import ke.topcast.activities.MainActivity;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewPodcasts {


    private void getNewPodcasts(int limitFrom, int limitTo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("limitFrom", String.valueOf(limitFrom));
        params.put("limitTo", String.valueOf(limitTo));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("limitFrom", params.get("limitFrom"))
                .addFormDataPart("limitTo", params.get("limitTo"))
                .build();

        MainActivity.GetNewPodcastsNetworkRequest request =
                request = new MainActivity.GetNewPodcastsNetworkRequest(Api.URL_GET_NEW_PODCASTS, requestBody);

        request.execute();
    }
}
