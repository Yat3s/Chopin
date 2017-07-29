package com.yat3s.chopin.sample;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.indicator.ChopinLoadingFooterIndicator;
import com.yat3s.chopin.indicator.ChopinRefreshHeaderIndicator;
import com.yat3s.chopin.indicator.LoadingFooterIndicatorProvider;
import com.yat3s.chopin.indicator.RefreshHeaderIndicatorProvider;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class AdvancedSettingActivity extends AppCompatActivity {
    private static final String TAG = "AdvancedSettingActivity";
    private static final int COMPLETE_DELAY = 10000;

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
                    case ChopinLayout.STATE_BOUNCING:
                        stateTv.setText("STATE_BOUNCING");
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
                }
            }
        });
    }

    private void configureDragResistance() {
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
                final View headerIndicatorView = getLayoutInflater().inflate(R.layout.layout_custom_indicator, null);
                final TextView headerProgressTv = (TextView) headerIndicatorView.findViewById(R.id.progress_tv);
                mChopinLayout.setRefreshHeaderIndicator(new RefreshHeaderIndicatorProvider() {
                    @Override
                    public View getContentView() {
                        return headerIndicatorView;
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onRefreshing() {
                        headerProgressTv.setText("Refreshing~");
                    }

                    @Override
                    public void onRefreshComplete() {
                        headerProgressTv.setText("Refresh completed!");
                    }

                    @Override
                    public void onHeaderIndicatorViewScrollChange(@IntRange(from = 0, to = 100) int progress) {
                        if (progress == 100) {
                            headerProgressTv.setText("Release to refresh~");
                        } else {
                            headerProgressTv.setText("You can release to refresh when reach to 100 --> " + progress);
                        }
                    }
                });

                final View footerIndicatorView = getLayoutInflater().inflate(R.layout.layout_custom_indicator, null);
                final TextView footerProgressTv = (TextView) footerIndicatorView.findViewById(R.id.progress_tv);
                mChopinLayout.setLoadingFooterIndicator(new LoadingFooterIndicatorProvider() {
                    @Override
                    public void onLoading() {
                        footerProgressTv.setText("Loadinging~");

                    }

                    @Override
                    public void onLoadingComplete() {
                        footerProgressTv.setText("Load more completed!");

                    }

                    @Override
                    public void onFooterIndicatorViewScrollChange(int progress) {
                        if (progress == 100) {
                            footerProgressTv.setText("Release to load~");
                        } else {
                            footerProgressTv.setText("You can release to load when reach to 100 --> " + progress);
                        }
                    }

                    @Override
                    public View getContentView() {
                        return footerIndicatorView;
                    }

                    @Override
                    public void onCancel() {

                    }
                });
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
        findViewById(R.id.header_indicator_location_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BACK);
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
        findViewById(R.id.footer_indicator_location_back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChopinLayout.setFooterIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BACK);
            }
        });
    }

    private void setupLottieIndicator() {
        ChopinRefreshHeaderIndicator kittenRefreshHeaderView = new ChopinRefreshHeaderIndicator
                (AdvancedSettingActivity.this, "Plane.json");
        kittenRefreshHeaderView.setScale(0.2f);
        mChopinLayout.setRefreshHeaderIndicator(kittenRefreshHeaderView);
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

        ChopinLoadingFooterIndicator kittenLoadingFooterView = new ChopinLoadingFooterIndicator
                (AdvancedSettingActivity.this, "Plane.json");
        kittenLoadingFooterView.setScale(0.2f);
        mChopinLayout.setLoadingFooterIndicator(kittenLoadingFooterView);
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
