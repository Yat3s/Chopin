package com.yat3s.chopin.sample.cases;

import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 26/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseScrollViewActivity extends BaseCaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_scroll_view;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("victory.json", 0.1f, 2000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);
    }
}
