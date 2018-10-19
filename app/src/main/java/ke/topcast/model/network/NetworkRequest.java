package ke.topcast.model.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.topcast.model.Podcast;
import ke.topcast.presenter.OnNetworkTaskCompleted;
import ke.topcast.presenter.PodcastOps;
import ke.topcast.utils.CommonUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkRequest extends AsyncTask<Void, Void, String> {
    String url;
    RequestBody requestBody;
    OnNetworkTaskCompleted listner;
    Context context;

    public NetworkRequest(String url, RequestBody requestBody, Context context, OnNetworkTaskCompleted listner) {
        this.context = context;
        this.requestBody = requestBody;
        this.url = url;
        this.listner = listner;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            super.onPostExecute(s);
            if (s != null){
                JSONObject object = new JSONObject(s);

                JSONArray jsonArray = object.getJSONArray("podcasts");

                List<Podcast> podcastList = new ArrayList<>();

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

                    podcastList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                }
                listner.OnNetworkTaskCompleted(podcastList);

            }else
                listner.OnNetworkTaskCompleted(null);
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
                    .addHeader("Authorization", CommonUtils.token)
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();

        }catch (Exception e){
            return null;
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}

