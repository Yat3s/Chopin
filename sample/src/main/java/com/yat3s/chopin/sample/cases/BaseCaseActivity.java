package com.yat3s.chopin.sample.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.indicator.LottieIndicator;
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
            throw new IllegalArgumentException("You should define a ChopinLayout with id chopin_layout in content layout!");
        } else {
            mChopinLayout = (ChopinLayout) findViewById(R.id.chopin_layout);
        }
        initialize();
    }

    protected void setupRefreshHeader(String fileName, float scale, final long refreshCompleteDelay) {
        LottieIndicator indicator = new LottieIndicator(this, fileName, scale);
        mChopinLayout.setRefreshHeaderIndicator(indicator);
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
        mChopinLayout.performRefresh();
    }

    protected void setupLoadingFooter(String fileName, float scale, final long loadingCompleteDelay) {
        LottieIndicator indicator = new LottieIndicator(this, fileName, scale);
        mChopinLayout.setLoadingFooterIndicator(indicator);
        mChopinLayout.setOnLoadMoreListener(new ChopinLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.loadMoreComplete();
                    }
                }, loadingCompleteDelay);
            }
        });
    }
}
