package com.yat3s.kitten.sample;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yat3s.kitten.KittenLayout;
import com.yat3s.kitten.decoration.KittenRefreshHeaderIndicator;
import com.yat3s.kitten.sample.cases.CaseFragmentActivity;
import com.yat3s.kitten.sample.cases.CaseLinearLayoutActivity;
import com.yat3s.kitten.sample.cases.CaseNestedRecyclerViewActivity;
import com.yat3s.kitten.sample.cases.CaseRecyclerViewActivity;
import com.yat3s.kitten.sample.cases.CaseScrollViewActivity;
import com.yat3s.kitten.sample.cases.CaseViewPagerActivity;
import com.yat3s.kitten.sample.cases.CaseWebViewActivity;
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
    private static final int GRID_SPAN_COUNT = 2;
    private KittenLayout mKittenLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKittenLayout = (KittenLayout) findViewById(R.id.kitten_layout);
        setupRefreshHeader("refresh.json", 0.2f, 3000);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, GRID_SPAN_COUNT));

        List<CaseDemo> caseDemos = new ArrayList<>();
        caseDemos.add(new CaseDemo("RecyclerView", R.mipmap.abstract_1, CaseRecyclerViewActivity.class));
        caseDemos.add(new CaseDemo("NestedRecyclerView", R.mipmap.abstract_2, CaseNestedRecyclerViewActivity.class));
        caseDemos.add(new CaseDemo("ScrollView", R.mipmap.abstract_3, CaseScrollViewActivity.class));
        caseDemos.add(new CaseDemo("LinearLayout", R.mipmap.abstract_3, CaseLinearLayoutActivity.class));
        caseDemos.add(new CaseDemo("ViewPager", R.mipmap.abstract_4, CaseViewPagerActivity.class));
        caseDemos.add(new CaseDemo("Fragment", R.mipmap.abstract_1, CaseFragmentActivity.class));
        caseDemos.add(new CaseDemo("WebView", R.mipmap.abstract_2, CaseWebViewActivity.class));

        CaseDemoAdapter caseDemoAdapter = new CaseDemoAdapter(this, caseDemos);
        recyclerView.setAdapter(caseDemoAdapter);
        caseDemoAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<CaseDemo>() {
            @Override
            public void onClick(View view, CaseDemo item, int position) {
                startActivity(new Intent(MainActivity.this, item.targetActivity));
            }
        });
    }

    protected void setupRefreshHeader(String fileName, float scale, final long refreshCompleteDelay) {
        KittenRefreshHeaderIndicator kittenRefreshHeaderView = new KittenRefreshHeaderIndicator(this, fileName);
        kittenRefreshHeaderView.setScale(scale);
        mKittenLayout.setRefreshHeaderIndicator(kittenRefreshHeaderView);
        mKittenLayout.setOnRefreshListener(new KittenLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.refreshComplete();
                    }
                }, refreshCompleteDelay);
            }
        });
    }

    private static class CaseDemo {
        public String title;

        public int backgroundResId;

        public Class<?> targetActivity;

        public CaseDemo(String title, Class<?> targetActivity) {
            this.title = title;
            this.targetActivity = targetActivity;
        }

        public CaseDemo(String title, int backgroundResId, Class<?> targetActivity) {
            this.title = title;
            this.backgroundResId = backgroundResId;
            this.targetActivity = targetActivity;
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
