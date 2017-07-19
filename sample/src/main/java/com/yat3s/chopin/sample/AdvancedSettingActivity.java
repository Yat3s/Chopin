package com.yat3s.chopin.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yat3s.chopin.ChopinLayout;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class AdvancedSettingActivity extends AppCompatActivity {

    private ChopinLayout mChopinLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_setting);
        initialize();
    }

    protected void initialize() {
        mChopinLayout = (ChopinLayout) findViewById(R.id.chopin_layout);

        // Configure refresh header indicator.
        final View refreshHeaderView = getLayoutInflater().inflate(R.layout.layout_custom_refresh_header, null);
        final TextView progressTv = (TextView) refreshHeaderView.findViewById(R.id.progress_tv);
        mChopinLayout.setHeaderIndicatorView(refreshHeaderView);

//        mChopinLayout.setRefreshHeaderIndicator(new RefreshHeaderIndicatorProvider() {
//            @Override
//            public View getContentView() {
//                return refreshHeaderView;
//            }
//
//            @Override
//            public void onRefreshing() {
//                progressTv.setText("Refreshing~");
//            }
//
//            @Override
//            public void onRefreshComplete() {
//                progressTv.setText("Refresh completed!");
//            }
//
//            @Override
//            public void onHeaderIndicatorViewScrollChange(@IntRange(from = 0, to = 100) int progress) {
//                if (progress == 100) {
//                    progressTv.setText("Release to refresh~");
//                } else {
//                    progressTv.setText("You can release to refresh when reach to 100 --> " + progress);
//                }
//            }
//        });
//
//        mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mChopinLayout.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mChopinLayout.refreshComplete();
//                    }
//                }, 3000);
//            }
//        });

        // Resistance setting.
        AppCompatSeekBar seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(36);
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
