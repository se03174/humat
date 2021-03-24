package com.example.humat;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView; //바텀 네비게이션 뷰
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager viewPager;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //loadFragment(new FragmentPage1());
        bottomNavigationView = findViewById(R.id.nav_view); //처음화면
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        viewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "onPageSelected");
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_1).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigation_2).setChecked(true);
                        break;
//                    case 2:
//                        bottomNavigationView.getMenu().findItem(R.id.navigation_3).setChecked(true);
//                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.i(TAG,"onNavigationItemSelected()");
        switch (item.getItemId()) {
            case R.id.navigation_1:
                viewPager.setCurrentItem(0);
                break;
            case R.id.navigation_2:
                viewPager.setCurrentItem(1);
                break;
//            case R.id.navigation_3:
//                viewPager.setCurrentItem(2);
//                break;
        }
        return true;
    }
}





