package com.yat3s.chopin.sample.cases;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.yat3s.chopin.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 27/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseLinearLayoutActivity extends BaseCaseActivity {
    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_linearlayout;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        findViewById(R.id.test_dispatch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaseLinearLayoutActivity.this, "LinearLayout Ok!", Toast.LENGTH_SHORT).show();
            }
        });

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
