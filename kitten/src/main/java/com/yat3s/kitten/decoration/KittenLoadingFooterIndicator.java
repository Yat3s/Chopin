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
    }

    public KittenLoadingFooterIndicator(Context context, String animationFileName) {
        super(context);
        setAnimation(animationFileName);
    }

    public KittenLoadingFooterIndicator(Context context, String animationFileName, float scale) {
        this(context, animationFileName);
        setScale(scale);
    }

    public KittenLoadingFooterIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KittenLoadingFooterIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View provideContentView() {
        return this;
    }

    @Override
    public void onLoadingStart() {
        playAnimation();
    }

    @Override
    public void onLoadingComplete() {
        clearAnimation();
    }

    @Override
    public void onFooterViewScrollChange(@IntRange(from = 0, to = 100) int progress) {
        setProgress(progress / 100.0f);
    }
}
