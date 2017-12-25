package com.yat3s.chopin.sample;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.indicator.Indicator;
import com.yat3s.chopin.indicator.LottieIndicator;
import com.yat3s.chopin.sample.cases.CaseAnyViewActivity;
import com.yat3s.chopin.sample.cases.CaseCoordinatorLayoutActivity;
import com.yat3s.chopin.sample.cases.CaseFragmentActivity;
import com.yat3s.chopin.sample.cases.CaseIndicatorDemosActivity;
import com.yat3s.chopin.sample.cases.CaseLinearLayoutActivity;
import com.yat3s.chopin.sample.cases.CaseRecyclerViewActivity;
import com.yat3s.chopin.sample.cases.CaseScrollViewActivity;
import com.yat3s.chopin.sample.cases.CaseTextViewActivity;
import com.yat3s.chopin.sample.cases.CaseViewPagerActivity;
import com.yat3s.chopin.sample.cases.CaseWebViewActivity;
import com.yat3s.library.adapter.BaseAdapter;
import com.yat3s.library.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 26/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class MainActivity extends AppCompatActivity {

    private static final int GRID_SPAN_COUNT = 3;
    private ChopinLayout mChopinLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChopinLayout = findViewById(R.id.chopin_layout);
        mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BEHIND);
        mChopinLayout.setRefreshHeaderIndicator(new LottieIndicator(this, "victory.json", 0.1f));
        mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.refreshComplete();
                    }
                }, 2000);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, GRID_SPAN_COUNT));

        // Title header decoration
        View titleView = getLayoutInflater().inflate(R.layout.header_title, null);
        TitleHeaderDecoration decoration = new TitleHeaderDecoration(titleView);
        recyclerView.addItemDecoration(decoration);

        List<CaseDemo> caseDemos = new ArrayList<>();
        caseDemos.add(new CaseDemo("RecyclerView", R.mipmap.abstract_1, CaseRecyclerViewActivity.class));
        caseDemos.add(new CaseDemo("ViewPager", R.mipmap.abstract_4, CaseViewPagerActivity.class));
        caseDemos.add(new CaseDemo("Coordinator\nLayout", R.mipmap.abstract_3, CaseCoordinatorLayoutActivity.class));
        caseDemos.add(new CaseDemo("ScrollView", R.mipmap.abstract_1, CaseScrollViewActivity.class));
        caseDemos.add(new CaseDemo("LinearLayout", R.mipmap.abstract_2, CaseLinearLayoutActivity.class));
        caseDemos.add(new CaseDemo("Fragment", R.mipmap.abstract_4, CaseFragmentActivity.class));
        caseDemos.add(new CaseDemo("WebView", R.mipmap.abstract_3, CaseWebViewActivity.class));
        caseDemos.add(new CaseDemo("TextView", R.mipmap.abstract_2, CaseTextViewActivity.class));
        caseDemos.add(new CaseDemo("AnyView", R.mipmap.abstract_1, CaseAnyViewActivity.class));


        caseDemos.add(new CaseDemo("Victory", R.mipmap.abstract_1,
                new LottieIndicator(this, "victory.json", 0.1f)));

        CaseDemoAdapter caseDemoAdapter = new CaseDemoAdapter(this, caseDemos);
        recyclerView.setAdapter(caseDemoAdapter);
        caseDemoAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<CaseDemo>() {
            @Override
            public void onClick(View view, CaseDemo item, int position) {
                if (item.type == CaseDemo.CASE_TYPE_VIEW_COMPATIBLE) {
                    startActivity(new Intent(MainActivity.this, item.targetActivity));
                } else if (item.indicator instanceof LottieIndicator) {
                    CaseIndicatorDemosActivity.start(MainActivity.this, (LottieIndicator) item.indicator);
                } else {
                    Toast.makeText(MainActivity.this, "Cannot convert this class type!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.advanced_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdvancedSettingActivity.class));
            }
        });
    }

    protected void setupRefreshHeader(String fileName, float scale, final long refreshCompleteDelay) {
        LottieIndicator indicator = new LottieIndicator(this, fileName, scale);
        mChopinLayout.setRefreshHeaderIndicator(indicator);
        mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.refreshComplete();
                    }
                }, refreshCompleteDelay);
            }
        });
    }

    private static class CaseDemo {

        private static final int CASE_TYPE_VIEW_COMPATIBLE = 0x00;

        private static final int CASE_TYPE_INDICATOR_DEMO = 0x01;

        public int type;

        public String title;

        public int backgroundResId;

        public Class<?> targetActivity;

        public Indicator indicator;

        public CaseDemo(String title, int backgroundResId, Class<?> targetActivity) {
            this.type = CASE_TYPE_VIEW_COMPATIBLE;
            this.title = title;
            this.backgroundResId = backgroundResId;
            this.targetActivity = targetActivity;
        }

        public CaseDemo(String title, int backgroundResId, Indicator indicator) {
            this.type = CASE_TYPE_INDICATOR_DEMO;
            this.title = title;
            this.backgroundResId = backgroundResId;
            this.indicator = indicator;
        }
    }

    private static class CaseDemoAdapter extends BaseAdapter<CaseDemo> {

        public CaseDemoAdapter(Context context, List<CaseDemo> dataSource) {
            super(context, dataSource);
        }

        @Override
        protected void bindDataToItemView(BaseViewHolder holder, CaseDemo item, int position) {
            holder.setText(R.id.title_tv, item.title)
                    .setBackgroundResource(R.id.background_iv, item.backgroundResId);

        }

        @Override
        protected int getItemViewLayoutId(int position, CaseDemo caseDemo) {
            return R.layout.item_case;
        }
    }
}
