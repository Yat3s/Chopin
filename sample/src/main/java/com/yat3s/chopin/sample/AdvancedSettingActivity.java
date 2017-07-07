package com.yat3s.chopin.sample;

import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

import com.yat3s.chopin.sample.cases.BaseCaseActivity;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class AdvancedSettingActivity extends BaseCaseActivity {
    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_advanced_setting;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);
        AppCompatSeekBar seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mChopinLayout.setIndicatorScrollResistance(seekBar.getProgress() / (float) seekBar.getMax());
            }
        });
    }
}
