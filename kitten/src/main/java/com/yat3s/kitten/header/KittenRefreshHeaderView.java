package com.yat3s.kitten.header;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.yat3s.kitten.KittenRecyclerView;

/**
 * Created by Yat3s on 19/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class KittenRefreshHeaderView extends LottieAnimationView implements KittenRecyclerView.RefreshHeaderViewProvider {

    public KittenRefreshHeaderView(Context context) {
        super(context);
    }

    public KittenRefreshHeaderView(Context context, String animationFileName) {
        super(context);
        setAnimation(animationFileName);
    }

    public KittenRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KittenRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View provideContentView() {
        return this;
    }

    @Override
    public void onStartRefresh() {
        playAnimation();
    }

    @Override
    public void onRefreshComplete() {
        pauseAnimation();
    }

    @Override
    public void onRefreshHeaderViewScrollChange(int progress) {
        setProgress(progress / 100.0f);
    }
}
