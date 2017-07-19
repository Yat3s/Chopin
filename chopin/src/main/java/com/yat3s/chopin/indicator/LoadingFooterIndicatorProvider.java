package com.yat3s.chopin.indicator;

public interface LoadingFooterIndicatorProvider extends IBaseIndicator {
    void onLoading();

    void onLoadingComplete();

    void onFooterIndicatorViewScrollChange(int progress);
}