package com.yat3s.kitten.decoration;

import android.support.annotation.IntRange;
import android.view.View;

public interface LoadingFooterViewProvider {
    View provideContentView();

    void onLoadingStart();

    void onLoadingComplete();

    void onFooterViewScrollChange(@IntRange(from = 0, to = 100) int progress);
}