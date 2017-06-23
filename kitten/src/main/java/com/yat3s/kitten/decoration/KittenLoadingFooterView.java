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
public class KittenLoadingFooterView extends LottieAnimationView implements LoadingFooterViewProvider {

    public KittenLoadingFooterView(Context context) {
        super(context);
    }

    public KittenLoadingFooterView(Context context, String animationFileName) {
        super(context);
        setAnimation(animationFileName);
    }

    public KittenLoadingFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KittenLoadingFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        pauseAnimation();
    }

    @Override
    public void onFooterViewScrollChange(@IntRange(from = 0, to = 100) int progress) {
        setProgress(progress / 100.0f);
    }
}
