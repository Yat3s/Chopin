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
        DRAGGING_DWON, DRAGGING_UP, BOUNCING_DWON, BOUNCING_UP
    }

    @NonNull
    public View getView();

    public void onViewCreated(View indicatorView, ChopinLayout chopinLayout);

    public void onCancel(ChopinLayout chopinLayout);

    public void onStart(ChopinLayout chopinLayout);

    public void onComplete(ChopinLayout chopinLayout);

    public void onPositionChange(float progress);
}
