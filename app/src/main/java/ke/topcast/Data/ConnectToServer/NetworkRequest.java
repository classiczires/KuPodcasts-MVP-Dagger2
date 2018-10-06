package ke.topcast.Data.ConnectToServer;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ke.topcast.models.Podcast;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkRequest extends AsyncTask<Void, Void, String> {

    String url;
    RequestBody requestBody;

    NetworkRequest(String url, RequestBody requestBody) {
        this.requestBody = requestBody;
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (newPodcastsList.size() > 0 ){
            if (newPodcastsList.get(newPodcastsList.size() - 1) != null) {
                newPodcastsList.add(null);
                newHorizontalAdapter.notifyItemInserted(newPodcastsList.size() - 1);
            }
            else this.cancel(true);
        }
        else {
            newPodcastsList.add(null);
            newHorizontalAdapter.notifyItemInserted(newPodcastsList.size() - 1);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            super.onPostExecute(s);
            if (s != null){
                JSONObject object = new JSONObject(s);

                if (newPodcastsList.size() > 0 ){
                    newPodcastsList.remove(newPodcastsList.size() - 1);
                    newHorizontalAdapter.notifyItemRemoved(newPodcastsList.size());
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

                    newPodcastsList.add(new Podcast(title, description, imageUrl, podcastUrl, sku, duration, programBuilder, narrators));
                }
                newHorizontalAdapter.notifyDataSetChanged();

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

