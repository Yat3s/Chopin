package com.yat3s.kitten.sample.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yat3s.kitten.KittenLayout;
import com.yat3s.kitten.decoration.KittenLoadingFooterIndicator;
import com.yat3s.kitten.decoration.KittenRefreshHeaderIndicator;
import com.yat3s.kitten.sample.R;

/**
 * Created by Yat3s on 05/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public abstract class BaseCaseActivity extends AppCompatActivity {

    protected abstract int getContentLayoutId();

    protected abstract void initialize();

    protected KittenLayout mKittenLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());

        if (null == findViewById(R.id.kitten_layout)) {
            throw new IllegalArgumentException("You should inflate a KittenLayout with id kitten_layout!");
        } else {
            mKittenLayout = (KittenLayout) findViewById(R.id.kitten_layout);
        }
        initialize();
    }

    protected void setupRefreshHeader(String fileName, float scale, final long refreshCompleteDelay) {
        KittenRefreshHeaderIndicator kittenRefreshHeaderView = new KittenRefreshHeaderIndicator(this, fileName);
        kittenRefreshHeaderView.setScale(scale);
        mKittenLayout.setRefreshHeaderIndicator(kittenRefreshHeaderView);
        mKittenLayout.setOnRefreshListener(new KittenLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.refreshComplete();
                    }
                }, refreshCompleteDelay);
            }
        });
    }

    protected void setupLoadingFooter(String fileName, float scale, final long refreshCompleteDelay) {
        KittenLoadingFooterIndicator kittenLoadingFooterView = new KittenLoadingFooterIndicator(this, fileName);
        kittenLoadingFooterView.setScale(scale);
        mKittenLayout.setLoadingFooterIndicator(kittenLoadingFooterView);
        mKittenLayout.setOnLoadMoreListener(new KittenLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.loadMoreComplete();
                    }
                }, refreshCompleteDelay);
            }
        });
    }
}
