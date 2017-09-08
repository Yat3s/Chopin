package com.yat3s.chopin.indicator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.R;

/**
 * Created by Yat3s on 08/09/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ClassicRefreshIndicator implements Indicator {
    private View mContentView;

    private ImageView mIndicatorIv;

    private TextView mPromptTv;

    public ClassicRefreshIndicator(Context context) {
        mContentView = LayoutInflater.from(context).inflate(R.layout.layout_classic_indicator, null);
        mIndicatorIv = (ImageView) mContentView.findViewById(R.id.indicator_iv);
        mPromptTv = (TextView) mContentView.findViewById(R.id.prompt_tv);
    }

    @NonNull
    @Override
    public View getView() {
        return mContentView;
    }

    @Override
    public void onViewMeasured(ChopinLayout chopinLayout, View indicatorView) {

    }

    @Override
    public void onCancel(ChopinLayout chopinLayout) {

    }

    @Override
    public void onStart(ChopinLayout chopinLayout) {
        mPromptTv.setText("Refreshing...");
    }

    @Override
    public void onComplete(ChopinLayout chopinLayout) {
        mPromptTv.setText("Refresh complete");
    }

    @Override
    public void onPositionChange(ChopinLayout chopinLayout, float progress, STATE state, int touchX, int touchY) {
        if (state == STATE.DRAGGING_DOWN || state == STATE.DRAGGING_UP) {
            if (progress >= 1.0f) {
                mPromptTv.setText("Release to refresh");
            } else {
                mPromptTv.setText("Pull down to refresh");
            }
        }
    }
}
