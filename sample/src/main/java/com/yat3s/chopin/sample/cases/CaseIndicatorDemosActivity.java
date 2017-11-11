package com.yat3s.chopin.sample.cases;

import android.content.Context;
import android.content.Intent;

import com.yat3s.chopin.indicator.Indicator;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 12/10/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseIndicatorDemosActivity extends BaseCaseActivity {
    public static final String EXTRA_INDICATOR = "indicator";

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_indicator_demos;
    }

    @Override
    protected void initialize() {
        Indicator indicator = (Indicator) getIntent().getSerializableExtra(EXTRA_INDICATOR);
        mChopinLayout.setRefreshHeaderIndicator(indicator);
        mChopinLayout.performRefresh();
    }

    public static void start(Context context, Indicator indicator) {
        Intent starter = new Intent(context, CaseIndicatorDemosActivity.class);
        starter.putExtra(EXTRA_INDICATOR, indicator);
        context.startActivity(starter);
    }
}
