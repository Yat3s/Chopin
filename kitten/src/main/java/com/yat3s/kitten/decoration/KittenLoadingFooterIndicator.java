package com.yat3s.kitten.decoration;

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
public class KittenLoadingFooterIndicator extends LottieAnimationView implements LoadingFooterIndicatorProvider {

    public KittenLoadingFooterIndicator(Context context) {
        super(context);
        initialize();
    }

    public KittenLoadingFooterIndicator(Context context, String animationFileName) {
        this(context);
        setAnimation(animationFileName);
    }

    public KittenLoadingFooterIndicator(Context context, String animationFileName, float scale) {
        this(context, animationFileName);
        setScale(scale);
    }

    public KittenLoadingFooterIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KittenLoadingFooterIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void onLoadingStart() {
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
