package com.yat3s.chopin.sample.cases;

import android.content.Context;
import android.content.Intent;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.yat3s.chopin.indicator.LottieIndicator;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 12/10/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseIndicatorDemosActivity extends BaseCaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_indicator_demos;
    }

    @Override
    protected void initialize() {
    }

    @Subscribe
    public void receiveIndicator(LottieIndicator indicator) {
        mChopinLayout.setRefreshHeaderIndicator(indicator);
        mChopinLayout.performRefresh();
    }

    public static void start(Context context, LottieIndicator indicator) {
        Intent starter = new Intent(context, CaseIndicatorDemosActivity.class);
        context.startActivity(starter);
        RxBus.get().post(indicator);
    }
}
