package com.yat3s.chopin.wrapper;

import android.view.View;

/**
 * Created by Yat3s on 17/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ContentViewWrapper extends BaseViewWrapper {
    private static final String TAG = "ContentViewWrapper";

    public void layout() {
        layout(0, 0, getWidth(), getHeight());
    }

    public ContentViewWrapper(View contentView) {
        super(contentView);
    }

    public void releaseToDefaultState() {
        animateTranslationY(getTranslationY(), 0, null);
    }
}
