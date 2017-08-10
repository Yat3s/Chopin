package com.yat3s.chopin.indicator;

import android.support.annotation.NonNull;
import android.view.View;

import com.yat3s.chopin.ChopinLayout;

/**
 * Created by Yat3s on 06/08/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public interface Indicator {
    public enum STATE {
        DRAGGING_DOWN, DRAGGING_UP, BOUNCING_DOWN, BOUNCING_UP
    }

    @NonNull
    public View getView();

    public void onViewMeasured(ChopinLayout chopinLayout, View indicatorView);

    public void onCancel(ChopinLayout chopinLayout);

    public void onStart(ChopinLayout chopinLayout);

    public void onComplete(ChopinLayout chopinLayout);

    public void onPositionChange(ChopinLayout chopinLayout, float progress, STATE state, int touchX, int touchY);
}
