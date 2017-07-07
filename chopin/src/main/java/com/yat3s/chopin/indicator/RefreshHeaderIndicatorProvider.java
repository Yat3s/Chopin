package com.yat3s.chopin.indicator;

import android.support.annotation.IntRange;
import android.view.View;

public interface RefreshHeaderIndicatorProvider {
    View getContentView();

    void onRefreshing();

    void onRefreshComplete();

    void onRefreshHeaderViewScrollChange(@IntRange(from = 0, to = 100) int progress);
}