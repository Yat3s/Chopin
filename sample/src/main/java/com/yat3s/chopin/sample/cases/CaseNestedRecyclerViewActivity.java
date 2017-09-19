package com.yat3s.chopin.sample.cases;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yat3s.chopin.sample.DataRepository;
import com.yat3s.chopin.sample.MusicAdapter;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 2017/7/6 0006.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseNestedRecyclerViewActivity extends BaseCaseActivity {
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
        MusicAdapter musicAdapter = new MusicAdapter(this, DataRepository.generateMusicData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicAdapter);
    }
}
