package com.yat3s.chopin.sample.cases;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yat3s.chopin.sample.PostcardAdapter;
import com.yat3s.chopin.sample.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 2017/7/6 0006.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseNestedRecyclerViewActivity extends BaseCaseActivity {
    private static final int MOCK_ITEM_SIZE = 12;

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_nested_recycler_view;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        // Configure adapter.
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        PostcardAdapter postcardAdapter = new PostcardAdapter(this, generatePostcardData(MOCK_ITEM_SIZE));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postcardAdapter);
    }

    private List<PostcardAdapter.Postcard> generatePostcardData(int size) {
        int[] postcardImageIds = {R.mipmap.card_1, R.mipmap.card_2, R.mipmap.card_3,
                R.mipmap.card_4, R.mipmap.card_5, R.mipmap.card_6, R.mipmap.card_7};
        List<PostcardAdapter.Postcard> postcards = new ArrayList<>();
        for (int idx = 0; idx < size; idx++) {
            postcards.add(new PostcardAdapter.Postcard(postcardImageIds[idx % postcardImageIds.length]));
        }
        return postcards;
    }
}
