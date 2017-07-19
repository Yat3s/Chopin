package com.yat3s.chopin.wrapper;

import android.animation.ValueAnimator;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by Yat3s on 19/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class BaseViewWrapper {

    protected View mContentView;

    public BaseViewWrapper(View contentView) {
        mContentView = contentView;
    }

    public View getView() {
        return mContentView;
    }

    public int getTranslationY() {
        return (int) mContentView.getTranslationY();
    }

    public boolean hasTranslated() {
        return mContentView.getTranslationY() != 0;
    }

    public void layout(int l, int t, int r, int b) {
        mContentView.layout(l, t, r, b);
    }

    public int getHeight() {
        return mContentView.getMeasuredHeight();
    }

    public int getWidth() {
        return mContentView.getMeasuredWidth();
    }

    public void translateVerticalWithOffset(int offset) {
        ViewCompat.setTranslationY(mContentView, offset);
    }

    public void animateTranslationY(final int start, final int end,
                                    @Nullable final AnimateListener animateListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(start, end);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                translateVerticalWithOffset(value);
                Log.d("sss", "onAnimationUpdate: " + value + ", " + end);
                if (null != animateListener) {
                    animateListener.onAnimate(value);
                    if (end == value) {
                        animateListener.onFinish();
                    }
                }
            }
        });
        valueAnimator.start();
    }

    public interface AnimateListener {
        void onAnimate(int value);

        void onFinish();
    }
}
