package com.wealthwise.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // 设置 ViewPager2 和 BottomNavigationView
        setupViewPager();

        // 底部导航点击事件
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (itemId == R.id.nav_statistics) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (itemId == R.id.nav_settings) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });
    }

    private void setupViewPager() {
        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // 页面变化监听器
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 更新底部导航选中状态
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.nav_statistics);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.nav_settings);
                        break;
                }
            }
        });
    }

    // ViewPager 适配器
    private static class MainPagerAdapter extends FragmentStateAdapter {
        private static final int NUM_PAGES = 3;

        public MainPagerAdapter(MainActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new BillListFragment();
                case 1:
                    return new StatisticsFragment();
                case 2:
                    return new SettingsFragment();
                default:
                    return new BillListFragment();
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}