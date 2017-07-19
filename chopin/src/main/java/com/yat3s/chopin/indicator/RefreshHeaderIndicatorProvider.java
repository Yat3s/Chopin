package com.yat3s.chopin.indicator;

public interface RefreshHeaderIndicatorProvider extends IBaseIndicator {
    void onRefreshing();

    void onRefreshComplete();

    void onHeaderIndicatorViewScrollChange(int progress);
}