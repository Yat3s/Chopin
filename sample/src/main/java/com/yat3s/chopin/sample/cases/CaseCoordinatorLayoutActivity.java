package com.yat3s.chopin.sample.cases;

import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 2017/7/9 0009.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseCoordinatorLayoutActivity extends BaseCaseActivity {
    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_coordinator;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);
    }
}
