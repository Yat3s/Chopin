package com.yat3s.chopin.sample.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.decoration.KittenLoadingFooterIndicator;
import com.yat3s.chopin.decoration.KittenRefreshHeaderIndicator;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 05/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public abstract class BaseCaseActivity extends AppCompatActivity {

    protected abstract int getContentLayoutId();

    protected abstract void initialize();

    protected ChopinLayout mChopinLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());

        if (null == findViewById(R.id.chopin_layout)) {
            throw new IllegalArgumentException("You should inflate a KittenLayout with id kitten_layout!");
        } else {
            mChopinLayout = (ChopinLayout) findViewById(R.id.chopin_layout);
        }
        initialize();
    }

    protected void setupRefreshHeader(String fileName, float scale, final long refreshCompleteDelay) {
        KittenRefreshHeaderIndicator kittenRefreshHeaderView = new KittenRefreshHeaderIndicator(this, fileName);
        kittenRefreshHeaderView.setScale(scale);
        mChopinLayout.setRefreshHeaderIndicator(kittenRefreshHeaderView);
        mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.refreshComplete();
                    }
                }, refreshCompleteDelay);
            }
        });
    }

    protected void setupLoadingFooter(String fileName, float scale, final long refreshCompleteDelay) {
        KittenLoadingFooterIndicator kittenLoadingFooterView = new KittenLoadingFooterIndicator(this, fileName);
        kittenLoadingFooterView.setScale(scale);
        mChopinLayout.setLoadingFooterIndicator(kittenLoadingFooterView);
        mChopinLayout.setOnLoadMoreListener(new ChopinLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.loadMoreComplete();
                    }
                }, refreshCompleteDelay);
            }
        });
    }
}
