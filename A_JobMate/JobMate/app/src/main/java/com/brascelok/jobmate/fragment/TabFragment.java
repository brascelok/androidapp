package com.brascelok.jobmate.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brascelok.jobmate.R;


public class TabFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static int item_number = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View tabView = inflater.inflate(R.layout.fragment_tab, container, false);
        tabLayout = (TabLayout) tabView.findViewById(R.id.tabs);
        viewPager = (ViewPager) tabView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });
        return tabView;
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ChatFragment();
                case 1:
                    return new GroupFragment();
                case 2:
                    return new ContactFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return item_number;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Chat";
                case 1:
                    return "Group";
                case 2:
                    return "Contacts";
            }
            return null;
        }
    }

}
