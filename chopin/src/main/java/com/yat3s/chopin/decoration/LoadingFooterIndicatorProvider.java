package com.yat3s.chopin.decoration;

import android.support.annotation.IntRange;
import android.view.View;

public interface LoadingFooterIndicatorProvider {
    View getContentView();

    void onLoadingStart();

    void onLoadingComplete();

    void onFooterViewScrollChange(@IntRange(from = 0, to = 100) int progress);
}