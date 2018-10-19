package ke.topcast.utils;

import android.graphics.Typeface;

import java.util.List;

import ke.topcast.model.Podcast;

public class CommonUtils {
    public static int VIEW_TYPE_ITEM = 0;
    public static int VIEW_TYPE_LOADING = 1;

    public static Typeface typeface;
    public static boolean RegisterPage = false;
    public static String MY_PREFS_NAME = "ZiresPrefsFile";
    public static String token = null;

    public static Podcast selectedPodcast;
    public static List<Podcast> playingListSelected;
    public static int queueCurrentIndex;
}
