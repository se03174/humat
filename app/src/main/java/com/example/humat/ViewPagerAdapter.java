package com.example.humat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentPage1();
            case 1:
                return new ChatActivity();
//            case 2:
//                return new ChatActivity();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
