package ke.topcast.ConnectToServer;

public class Api {

    private static final String ROOT_URL = "http://10.0.2.2:8000/api/";

    public static final String URL_GET_CATEGORIES = ROOT_URL + "getCategories";

    public static final String URL_GET_NEW_PODCASTS = ROOT_URL + "getNewPodcasts";

    public static final String URL_REGISTER = ROOT_URL + "register";

    public static final String URL_LOGIN = ROOT_URL + "login";

    public static final String URL_SEARCH = ROOT_URL + "searchPodcast";

    public static final String URL_LOGOUT = ROOT_URL + "logout";

    public static final String URL_CATEGORY_PODCAST = ROOT_URL + "getCategoryPodcasts";

    public static final String URL_USER_DETAILS = ROOT_URL + "details";

}