package com.yat3s.chopin.indicator;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

/**
 * Created by Yat3s on 19/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ChopinRefreshHeaderIndicator extends LottieAnimationView implements RefreshHeaderIndicatorProvider {

    public ChopinRefreshHeaderIndicator(Context context) {
        super(context);
        initialize();
    }

    public ChopinRefreshHeaderIndicator(Context context, String animationFileName) {
        this(context);
        setAnimation(animationFileName);
    }

    public ChopinRefreshHeaderIndicator(Context context, String animationFileName, float scale) {
        this(context, animationFileName);
        setScale(scale);
    }

    public ChopinRefreshHeaderIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChopinRefreshHeaderIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        loop(true);
    }

    @Override
    public View getContentView() {
        return this;
    }

    @Override
    public void onRefreshing() {
        playAnimation();
    }

    @Override
    public void onRefreshComplete() {
        cancelAnimation();
    }

    @Override
    public void onRefreshHeaderViewScrollChange(int progress) {
        setProgress(progress / 100.0f);
    }
}
