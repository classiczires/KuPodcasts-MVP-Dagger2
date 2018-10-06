package ke.topcast.view.fragments.CategoriesFragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ke.topcast.model.network.Api;
import ke.topcast.R;
import ke.topcast.view.activities.MainActivity;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CategoriesFragment extends Fragment {

    public static String SelectedCategory = null;
    ContentLoadingProgressBar contentLoadingProgressBar;
    ListView listView;
    List<String> categories;
    /*    private static String[] CATEGORIES =
                {"پادکست های ویژه" ,"مدیریت و کارآفرینی","شعر و ادبیات" ,"سلامت و سبک زندگی" ,
                        "کمدی و سرگرمی" ,"زبان های خارجی" ,"دین و زندگی" , "علم و تکنولوژی",
                        "تاریخ و هنر", "ورزش", "گردشگری", "روانشناسی و جامعه", "سایر"};
    */
    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_categories, container, false);
        categories = new ArrayList<>();
        contentLoadingProgressBar = (ContentLoadingProgressBar) mainView.findViewById(R.id.loadingProgressBar) ;
        listView = (ListView) mainView.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                SelectedCategory = textView.getText().toString();

                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                CategoryContentFragment categoryContent = (CategoryContentFragment) fm.findFragmentByTag("categoryContent");
                if (categoryContent == null) {
                    categoryContent = new CategoryContentFragment();
                }
                fm.beginTransaction()
                        .add(R.id.fragContainer, categoryContent, "categoryContent")
                        .addToBackStack(categoryContent.getClass().getName())
                        .commit();
            }});

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Authorization", MainActivity.token)
                .build();

        GetCategoriesNetworkRequest request = request = new GetCategoriesNetworkRequest(Api.URL_GET_CATEGORIES, requestBody);

        request.execute();

        return mainView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private class GetCategoriesNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        RequestBody requestBody;

        GetCategoriesNetworkRequest(String url, RequestBody requestBody) {
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            contentLoadingProgressBar.show();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                super.onPostExecute(s);
                contentLoadingProgressBar.hide();

                JSONObject object = new JSONObject(s);

                JSONArray jsonArray = object.getJSONArray("categories");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jso = jsonArray.getJSONObject(i);
                    String category = jso.getString("category");
                    categories.add(category);
                }
                listView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,categories));

            } catch (JSONException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                        .addHeader("Authorization", MainActivity.token)
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            }catch (Exception e){
                return null;
            }
        }
    }

}
