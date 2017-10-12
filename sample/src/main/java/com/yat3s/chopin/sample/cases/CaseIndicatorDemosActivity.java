package com.yat3s.chopin.sample.cases;

import android.content.Context;
import android.content.Intent;

import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 12/10/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseIndicatorDemosActivity extends BaseCaseActivity {
    public static final String EXTRA_INDICATOR_NAME = "indicator_name";

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_indicator_demos;
    }

    @Override
    protected void initialize() {
        String indicator = getIntent().getStringExtra(EXTRA_INDICATOR_NAME);
    }

    public static void start(Context context, String indicatorName) {
        Intent starter = new Intent(context, CaseIndicatorDemosActivity.class);
        starter.putExtra(EXTRA_INDICATOR_NAME, indicatorName);
        context.startActivity(starter);
    }
}
