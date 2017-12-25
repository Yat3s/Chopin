package com.yat3s.chopin.sample.cases;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.ViewScrollChecker;
import com.yat3s.chopin.sample.R;
import com.yat3s.chopin.sample.RecyclerViewFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 03/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseViewPagerActivity extends BaseCaseActivity {
    private static final String SLOGAN = "CHOPIN";

    private RecyclerViewFragment mCurrentFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_viewpager;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 2000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);
        mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BEHIND);

        final List<RecyclerViewFragment> fragments = new ArrayList<>();
        fragments.add(RecyclerViewFragment.newInstance(R.mipmap.abstract_1));
        fragments.add(RecyclerViewFragment.newInstance(R.mipmap.abstract_2));
        fragments.add(RecyclerViewFragment.newInstance(R.mipmap.abstract_3));
        fragments.add(RecyclerViewFragment.newInstance(R.mipmap.abstract_4));
        fragments.add(RecyclerViewFragment.newInstance(R.mipmap.abstract_3));
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return SLOGAN.subSequence(position, position + 1);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        mCurrentFragment = fragments.get(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentFragment = fragments.get(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mChopinLayout.setViewScrollChecker(new ViewScrollChecker() {
            @Override
            public boolean canDoRefresh(ChopinLayout chopinLayout, View contentView) {
                return mCurrentFragment.canDoRefresh();
            }

            @Override
            public boolean canDoLoading(ChopinLayout chopinLayout, View contentView) {
                return mCurrentFragment.canDoLoading();
            }
        });
    }
}
