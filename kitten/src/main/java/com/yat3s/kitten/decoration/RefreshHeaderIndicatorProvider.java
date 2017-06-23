package com.yat3s.kitten.decoration;

import android.support.annotation.IntRange;
import android.view.View;

public interface RefreshHeaderIndicatorProvider {
    View provideContentView();

    void onRefreshStart();

    void onRefreshComplete();

    void onRefreshHeaderViewScrollChange(@IntRange(from = 0, to = 100) int progress);
}