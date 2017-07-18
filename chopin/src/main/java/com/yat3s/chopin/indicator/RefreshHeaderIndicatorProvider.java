package com.yat3s.chopin.indicator;

import android.view.View;

public interface RefreshHeaderIndicatorProvider {
    View getContentView();

    void onCancel();

    void onRefreshing();

    void onRefreshComplete();

    void onRefreshHeaderViewScrollChange(int progress);
}