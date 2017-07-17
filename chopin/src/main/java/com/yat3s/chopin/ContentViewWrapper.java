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

    public void scrollVerticalWithOffset(int offset) {
        Log.d(TAG, "scrollVerticalWithOffset: " + offset);
        ViewCompat.offsetTopAndBottom(mContentView, offset);
    }

    public boolean hasTranslated() {
        return mContentView.getTop() != 0 || mContentView.getBottom() != mContentView.getHeight();
    }
}
