package com.yat3s.chopin.indicator;

import android.view.View;

public interface LoadingFooterIndicatorProvider {
    View getContentView();

    void onCancel();

    void onLoading();

    void onLoadingComplete();

    void onFooterViewScrollChange(int progress);
}