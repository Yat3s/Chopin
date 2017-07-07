package com.yat3s.chopin.indicator;

import android.content.Context;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

/**
 * Created by Yat3s on 23/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ChopinLoadingFooterIndicator extends LottieAnimationView implements LoadingFooterIndicatorProvider {

    public ChopinLoadingFooterIndicator(Context context) {
        super(context);
        initialize();
    }

    public ChopinLoadingFooterIndicator(Context context, String animationFileName) {
        this(context);
        setAnimation(animationFileName);
    }

    public ChopinLoadingFooterIndicator(Context context, String animationFileName, float scale) {
        this(context, animationFileName);
        setScale(scale);
    }

    public ChopinLoadingFooterIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChopinLoadingFooterIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void onLoading() {
        playAnimation();
    }

    @Override
    public void onLoadingComplete() {
        cancelAnimation();
    }

    @Override
    public void onFooterViewScrollChange(@IntRange(from = 0, to = 100) int progress) {
        setProgress(progress / 100.0f);
    }
}
