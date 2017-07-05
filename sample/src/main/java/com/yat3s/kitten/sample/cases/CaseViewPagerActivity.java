package com.yat3s.kitten.sample.cases;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yat3s.kitten.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 03/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseViewPagerActivity extends BaseCaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_viewpager;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        final int[] pagesColors = {R.color.md_blue_grey_100, R.color.md_blue_grey_600, R.color.md_red_300};
        final List<TextView> pageViews = new ArrayList<>();
        for (int pagesColor : pagesColors) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(pagesColor);
            pageViews.add(textView);
        }
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return pageViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return object == view;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = pageViews.get(position);
                container.addView(pageViews.get(position));
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(pageViews.get(position));
            }
        });
    }
}
