package com.yat3s.chopin.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.indicator.ClassicRefreshIndicator;
import com.yat3s.chopin.indicator.LottieIndicator;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class AdvancedSettingActivity extends AppCompatActivity {
    private static final String TAG = "AdvancedSettingActivity";

    private static final int COMPLETE_DELAY = 2000;

    private ChopinLayout mChopinLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_setting);
        initialize();
    }

    protected void initialize() {
        mChopinLayout = (ChopinLayout) findViewById(R.id.chopin_layout);

        configureScrollState();
        configureDragResistance();
        configureHeaderIndicatorLocation();
        configureFooterIndicatorLocation();
        configureIndicatorStyle();
        configureNotificationView();
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view);
        findViewById(R.id.perform_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, 0);
                mChopinLayout.performRefresh();
            }
        });
    }

    private void configureScrollState() {
        final TextView stateTv = (TextView) findViewById(R.id.state_tv);
        stateTv.setText("STATE_DEFAULT");
        mChopinLayout.setOnStateChangeListener(new ChopinLayout.OnStateChangeListener() {
            @Override
            public void onStateChanged(ChopinLayout layout, int newState) {
                switch (newState) {
                    case ChopinLayout.STATE_DEFAULT:
                        stateTv.setText("STATE_DEFAULT");
                        break;
                    case ChopinLayout.STATE_BOUNCING_DOWN:
                        stateTv.setText("STATE_BOUNCING_DOWN");
                        break;
                    case ChopinLayout.STATE_BOUNCING_UP:
                        stateTv.setText("STATE_BOUNCING_UP");
                        break;
                    case ChopinLayout.STATE_DRAGGING_DOWN:
                        stateTv.setText("STATE_DRAGGING_DOWN");
                        break;
                    case ChopinLayout.STATE_DRAGGING_UP:
                        stateTv.setText("STATE_DRAGGING_UP");
                        break;
                    case ChopinLayout.STATE_REFRESHING:
                        stateTv.setText("STATE_REFRESHING");
                        break;
                    case ChopinLayout.STATE_LOADING:
                        stateTv.setText("STATE_LOADING");
                        break;
                    case ChopinLayout.STATE_SHOWING_HEADER_NOTIFICATION:
                        stateTv.setText("STATE_SHOWING_HEADER_NOTIFICATION");
                        break;
                    case ChopinLayout.STATE_SHOWING_FOOTER_NOTIFICATION:
                        stateTv.setText("STATE_SHOWING_FOOTER_NOTIFICATION");
                        break;
                }
            }
        });
    }

    private void configureDragResistance() {
        AppCompatSeekBar seekBar = (AppCompatSeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(40);
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

    private void configureIndicatorStyle() {
        setupLottieIndicator();
        findViewById(R.id.indicator_style_lottie_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLottieIndicator();
            }
        });

        findViewById(R.id.indicator_style_custom_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setRefreshHeaderIndicator(new ClassicRefreshIndicator(AdvancedSettingActivity.this));
                mChopinLayout.setLoadingFooterIndicator(new ClassicRefreshIndicator(AdvancedSettingActivity.this));
                mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mChopinLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChopinLayout.refreshComplete();
                            }
                        }, COMPLETE_DELAY);
                    }
                });
                mChopinLayout.setOnLoadMoreListener(new ChopinLayout.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        mChopinLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mChopinLayout.loadMoreComplete();
                            }
                        }, COMPLETE_DELAY);
                    }
                });
            }
        });
        findViewById(R.id.indicator_style_only_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.clearFooterIndicator();
                mChopinLayout.clearHeaderIndicator();

                ImageView headerIndicatorIv = new ImageView(AdvancedSettingActivity.this);
                headerIndicatorIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                headerIndicatorIv.setImageResource(R.mipmap.abstract_1);

                ImageView footerIndicatorIv = new ImageView(AdvancedSettingActivity.this);
                footerIndicatorIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                footerIndicatorIv.setImageResource(R.mipmap.abstract_2);
                mChopinLayout.setHeaderIndicatorView(headerIndicatorIv);
                mChopinLayout.setFooterIndicatorView(footerIndicatorIv);
            }
        });
        findViewById(R.id.indicator_style_null_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.clearFooterIndicator();
                mChopinLayout.clearHeaderIndicator();
            }
        });
    }

    private void configureHeaderIndicatorLocation() {
        findViewById(R.id.header_indicator_location_outside_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_OUTSIDE);
            }
        });
        findViewById(R.id.header_indicator_location_front_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_FRONT);
            }
        });
        findViewById(R.id.header_indicator_location_behind_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BEHIND);
            }
        });
    }

    private void configureFooterIndicatorLocation() {
        findViewById(R.id.footer_indicator_location_outside_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setFooterIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_OUTSIDE);
            }
        });
        findViewById(R.id.footer_indicator_location_front_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setFooterIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_FRONT);
            }
        });
        findViewById(R.id.footer_indicator_location_behind_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setFooterIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BEHIND);
            }
        });
    }

    private void configureNotificationView() {
        SwitchCompat notificationSwitch = (SwitchCompat) findViewById(R.id.notification_view_switch);
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    TextView headerNotificationView = new TextView(AdvancedSettingActivity.this);
                    headerNotificationView.setLayoutParams(lp);
                    headerNotificationView.setText("10 new messages");
                    headerNotificationView.setGravity(Gravity.CENTER);
                    headerNotificationView.setPadding(0, 24, 0, 24);
                    headerNotificationView.setTextColor(Color.WHITE);
                    headerNotificationView.setBackgroundResource(R.color.md_red_500);
                    mChopinLayout.setHeaderNotificationView(headerNotificationView);

                    TextView footerNotificationView = new TextView(AdvancedSettingActivity.this);
                    footerNotificationView.setLayoutParams(lp);
                    footerNotificationView.setText("Load 12 more messages");
                    footerNotificationView.setTextColor(Color.BLACK);
                    footerNotificationView.setGravity(Gravity.CENTER);
                    footerNotificationView.setPadding(0, 24, 0, 24);
                    footerNotificationView.setBackgroundResource(R.color.md_yellow_500);
                    mChopinLayout.setFooterNotificationView(footerNotificationView);
                } else {
                    mChopinLayout.setHeaderNotificationView(null);
                    mChopinLayout.setFooterNotificationView(null);
                }
            }
        });
    }

    private void setupLottieIndicator() {
        LottieIndicator headerLottieIndicator = new LottieIndicator(AdvancedSettingActivity.this, "Plane.json", 0.2f);
        mChopinLayout.setRefreshHeaderIndicator(headerLottieIndicator);
        mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onRefreshRun: ");
                        mChopinLayout.refreshComplete();
                    }
                }, COMPLETE_DELAY);
            }
        });

        LottieIndicator footerLottieIndicator = new LottieIndicator(AdvancedSettingActivity.this, "Plane.json", 0.2f);
        mChopinLayout.setLoadingFooterIndicator(footerLottieIndicator);
        mChopinLayout.setOnLoadMoreListener(new ChopinLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.loadMoreComplete();
                    }
                }, COMPLETE_DELAY);
            }
        });
    }
}
