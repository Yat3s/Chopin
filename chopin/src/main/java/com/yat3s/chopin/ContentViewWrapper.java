package com.yat3s.chopin;

import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by Yat3s on 17/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ContentViewWrapper {
    private static final String TAG = "ContentViewWrapper";
    private View mContentView;

    public ContentViewWrapper(View contentView) {
        mContentView = contentView;
    }

    public View getContentView() {
        return mContentView;
    }

    public void layout() {
        mContentView.layout(0, 0, mContentView.getMeasuredWidth(), mContentView.getMeasuredHeight());
    }

    public void translateVerticalWithOffset(int offset) {
        Log.d(TAG, "translateVerticalWithOffset: " + offset);
        ViewCompat.setTranslationY(mContentView, offset);
    }

    public boolean hasTranslated() {
        return mContentView.getTranslationY() != 0;
    }

    public void releaseToDefaultState() {
        animateTranslation(0);
    }

    private void animateTranslation(int dy) {
        mContentView.animate().translationY(dy).start();
    }
}
