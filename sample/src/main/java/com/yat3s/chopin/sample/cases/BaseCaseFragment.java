package com.yat3s.chopin.sample.cases;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.indicator.ChopinLoadingFooterIndicator;
import com.yat3s.chopin.indicator.ChopinRefreshHeaderIndicator;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public abstract class BaseCaseFragment extends Fragment {

    protected abstract int getContentLayoutId();

    protected abstract void initialize(View rootView);

    protected ChopinLayout mChopinLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getContentLayoutId(), container, false);
        if (null == rootView.findViewById(R.id.chopin_layout)) {
            throw new IllegalArgumentException("You should inflate a ChopinLayout with id chopin_layout!");
        } else {
            mChopinLayout = (ChopinLayout) rootView.findViewById(R.id.chopin_layout);
        }
        initialize(rootView);
        return rootView;
    }

    protected void setupRefreshHeader(String fileName, float scale, final long refreshCompleteDelay) {
        ChopinRefreshHeaderIndicator kittenRefreshHeaderView = new ChopinRefreshHeaderIndicator(getActivity(), fileName);
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
        ChopinLoadingFooterIndicator kittenLoadingFooterView = new ChopinLoadingFooterIndicator(getActivity(), fileName);
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
