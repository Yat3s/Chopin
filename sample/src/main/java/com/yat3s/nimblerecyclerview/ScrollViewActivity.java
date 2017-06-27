package com.yat3s.nimblerecyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yat3s.kitten.KittenLayout;
import com.yat3s.kitten.decoration.KittenLoadingFooterIndicator;
import com.yat3s.kitten.decoration.KittenRefreshHeaderIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 26/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ScrollViewActivity extends AppCompatActivity {

    private KittenLayout mKittenLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);
        mKittenLayout = (KittenLayout) findViewById(R.id.kitten_layout);

        // Configure refresh header.
        KittenRefreshHeaderIndicator kittenRefreshHeaderView = new KittenRefreshHeaderIndicator(this, "refresh.json");
        kittenRefreshHeaderView.setScale(0.2f);
        mKittenLayout.setRefreshHeaderIndicator(kittenRefreshHeaderView);
        mKittenLayout.setOnRefreshListener(new KittenLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.refreshComplete();
                    }
                }, 1500);
            }
        });

        // Configure loading footer.
        KittenLoadingFooterIndicator kittenLoadingFooterView = new KittenLoadingFooterIndicator(this, "loading.json");
        kittenLoadingFooterView.setScale(0.2f);
        mKittenLayout.setLoadingFooterIndicator(kittenLoadingFooterView);
        mKittenLayout.setOnLoadMoreListener(new KittenLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.loadMoreComplete();
                    }
                }, 1500);
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
