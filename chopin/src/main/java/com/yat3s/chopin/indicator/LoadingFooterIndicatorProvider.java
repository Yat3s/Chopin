package com.yat3s.chopin.indicator;

import android.support.annotation.IntRange;
import android.view.View;

public interface LoadingFooterIndicatorProvider {
    View getContentView();

    void onLoading();

    void onLoadingComplete();

    void onFooterViewScrollChange(@IntRange(from = 0, to = 100) int progress);
}