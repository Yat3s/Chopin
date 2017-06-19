package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.yat3s.kitten.KittenRecyclerView;

/**
 * Created by Yat3s on 19/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class RefreshHeaderViewProvider implements KittenRecyclerView.RefreshHeaderViewProvider {

    View headerView;
    LottieAnimationView loadView;

    public RefreshHeaderViewProvider(Context context) {
        headerView = LayoutInflater.from(context).inflate(R.layout.layout_refresh_header, null,
                false);
        loadView = (LottieAnimationView) headerView.findViewById(R.id.loading_view);
    }

    @Override
    public View provideContentView() {
        return headerView;
    }

    @Override
    public void onStartRefresh() {
        loadView.playAnimation();
    }

    @Override
    public void onRefreshComplete() {
        loadView.pauseAnimation();
    }

    @Override
    public void onRefreshHeaderViewScrollChange(int progress) {
        loadView.setProgress(progress / 100.0f);
    }
}
