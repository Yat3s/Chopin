package com.yat3s.chopin.sample.cases;

import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.ViewScrollChecker;
import com.yat3s.chopin.ViewScrollHelper;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 2017/7/9 0009.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseCoordinatorLayoutActivity extends BaseCaseActivity {

    // To mark appbar layout scroll vertical offset for notify whether
    // content view has scrolled to top.
    private int mCurrentVerticalOffset;

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_coordinator;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 2000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        final AppBarLayout barLayout = (AppBarLayout) findViewById(R.id.appbar);
        barLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // To mark current scroll offset.
                mCurrentVerticalOffset = verticalOffset;
            }
        });

        final NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        mChopinLayout.setViewScrollChecker(new ViewScrollChecker() {
            @Override
            public boolean canDoRefresh(ChopinLayout chopinLayout, View contentView) {
                // It can do refresh while current has none vertical scroll offset.
                return mCurrentVerticalOffset == 0;
            }

            @Override
            public boolean canDoLoading(ChopinLayout chopinLayout, View contentView) {
                // Know scrollable view whether has scrolled to bottom.
                return ViewScrollHelper.viewHasScrolledToBottom(scrollView);
            }
        });

        // Setup navigation back button.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
