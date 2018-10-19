package ke.topcast.view.fragments.AccountFragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ke.topcast.R;
import ke.topcast.utils.CommonUtils;
import ke.topcast.view.activities.MainActivity;

import static android.content.Context.MODE_PRIVATE;


public class AccountFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView userNameTeV, userPhoneTeV;
        userNameTeV = (TextView) view.findViewById(R.id.user_name);
        userPhoneTeV = (TextView) view.findViewById(R.id.user_phone);

        SharedPreferences prefs = getActivity().getSharedPreferences(CommonUtils.MY_PREFS_NAME, MODE_PRIVATE);
        userNameTeV.setText(prefs.getString("name", ""));
        userPhoneTeV.setText(prefs.getString("phoneNumber", ""));

        BookmarksFragment bookmarksFragment = new BookmarksFragment();
        PurchasesFragment purchasesFragment = new PurchasesFragment();

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(bookmarksFragment, "ذخیره شده\u200Cها");
        adapter.addFragment(purchasesFragment, "خرید\u200Cها");
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.bookmark_selector);
        tabLayout.getTabAt(0).setContentDescription("ذخیره شده\u200Cها");
        tabLayout.getTabAt(1).setIcon(R.drawable.purchase_selector);
        tabLayout.getTabAt(1).setContentDescription("خرید\u200Cها");

        return view;
    }




    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            // return null to display only the icon
            return null;
        }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
