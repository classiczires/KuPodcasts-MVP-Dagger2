package ke.topcast.model.network;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ke.topcast.model.data.Podcast;
import ke.topcast.presenter.AdapterOps;
import ke.topcast.presenter.PodcastOps;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkRequest extends AsyncTask<Void, Void, String> {
    String url;
    RequestBody requestBody;
    PodcastOps podcastOps;
    AdapterOps adapterOps;

    public NetworkRequest(String url, RequestBody requestBody, PodcastOps podcastOps, AdapterOps adapterOps) {
        this.requestBody = requestBody;
        this.url = url;
        this.podcastOps = podcastOps;
        this.adapterOps = adapterOps;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (podcastOps.getPodcastsCount() > 0 ){
            if (podcastOps.getPodcast(podcastOps.getPodcastsCount() - 1) != null) {
                podcastOps.insert(null);
                adapterOps.notifyItemInsert(podcastOps.getPodcastsCount() - 1);
            }
            else this.cancel(true);
        }
        else {
            podcastOps.insert(null);
            adapterOps.notifyItemInsert(podcastOps.getPodcastsCount() - 1);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            super.onPostExecute(s);
            if (s != null){
                JSONObject object = new JSONObject(s);

                if (podcastOps.getPodcastsCount() > 0 ){
                    podcastOps.remove(podcastOps.getPodcastsCount() - 1);
                    adapterOps.notifyItemRemove(podcastOps.getPodcastsCount());
                }
                JSONArray jsonArray = object.getJSONArray("podcasts");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jso = jsonArray.getJSONObject(i);
                    String title = jso.getString("title");
                    String description = jso.getString("description");
                    String duration = jso.getString("duration");
                    String programBuilder = jso.getString("programBuilder");
                    String narrators = jso.getString("narrators");
                    String sku = jso.getString("sku");
                    String imageUrl = jso.getString("coverPath");
                    String podcastUrl = jso.getString("podcastPath");

                    podcastOps.insert(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                }
                adapterOps.notifyDataSetChange();

            }else
                Toast.makeText(ctx, "خطا در اتصال", Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //the network operation will be performed in background
    @Override
    protected String doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Authorization", token)
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();

        }catch (Exception e){
            return null;
        }
    }
}

