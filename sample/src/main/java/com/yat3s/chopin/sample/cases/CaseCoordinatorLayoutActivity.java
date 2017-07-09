package com.yat3s.chopin.sample.cases;

import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.view.View;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.ViewScrollChecker;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 2017/7/9 0009.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseCoordinatorLayoutActivity extends BaseCaseActivity {
    private static final String TAG = "CaseCoordinatorLayoutAc";

    private int mCurrentVerticalOffset = -1, mTotalScrollRange;

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_coordinator;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
//        setupLoadingFooter("Plane.json", 0.2f, 1500);

        final AppBarLayout barLayout = (AppBarLayout) findViewById(R.id.flexible_example_appbar);
        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mCurrentVerticalOffset = verticalOffset;
                Log.d(TAG, "onOffsetChanged: " + verticalOffset);
                Log.d(TAG, "getTotalScrollRange: " + barLayout.getTotalScrollRange());
            }
        });

        mChopinLayout.setViewScrollChecker(new ViewScrollChecker() {
            @Override
            public boolean canDoRefresh(ChopinLayout chopinLayout, View contentView) {
                return mCurrentVerticalOffset == 0;
            }

            @Override
            public boolean canDoLoading(ChopinLayout chopinLayout, View contentView) {
                return false;
            }
        });

    }
}
