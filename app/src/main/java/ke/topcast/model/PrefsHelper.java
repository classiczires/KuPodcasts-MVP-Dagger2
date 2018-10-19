package ke.topcast.model;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class PrefsHelper  {
    private SharedPreferences mSharedPreferences;

    @Inject
    public PrefsHelper(SharedPreferences mSharedPreferences) {
        this.mSharedPreferences = mSharedPreferences;
    }

    public String getToken() {
        return mSharedPreferences.getString("token", null);
    }

    public void setToken(String token) {
        mSharedPreferences.edit().putString("token" ,token).apply();
    }

    public List<Podcast> getBookmarkedPodcasts(){
        String podcastsJSONString = mSharedPreferences.getString("BookmarkedPodcasts", null);
        if (podcastsJSONString == null)
            return null;

        Type type = new TypeToken<List<Podcast>>() {}.getType();
        List<Podcast> podcasts = new Gson().fromJson(podcastsJSONString, type);
        return podcasts;
    }

    public void setBookmarkedPodcasts(List<Podcast> podcasts){
        Gson gson = new Gson();
        String json = gson.toJson(podcasts);
        mSharedPreferences.edit().putString("BookmarkedPodcasts", json);
        mSharedPreferences.edit().apply();
    }

    public void setString(String key, String value){
        mSharedPreferences.edit().putString(key, value);
        mSharedPreferences.edit().apply();
    }
    public String getString(String key, String defValue){
        return mSharedPreferences.getString(key, defValue);
    }
}
