package com.yat3s.chopin.sample.cases;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.sample.DataRepo;
import com.yat3s.chopin.sample.MusicAdapter;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseRecyclerViewActivity extends BaseCaseActivity {
    private static final String TAG = "CaseRecyclerView";
    private static final int SPAN_COUNT = 3;

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_recycler_view;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: " + recyclerView.computeVerticalScrollOffset());
            }
        });

        final TextView stateTv = (TextView) findViewById(R.id.state_tv);
        mChopinLayout.setOnStateChangeListener(new ChopinLayout.OnStateChangeListener() {
            @Override
            public void onStateChanged(ChopinLayout layout, int newState) {
                switch (newState) {
                    case ChopinLayout.STATE_DEFAULT:
                        stateTv.setText("STATE_DEFAULT");
                        break;
                    case ChopinLayout.STATE_BOUNCING:
                        stateTv.setText("STATE_BOUNCING");
                        break;
                    case ChopinLayout.STATE_DRAGGING_DOWN:
                        stateTv.setText("STATE_DRAGGING_DOWN");
                        break;
                    case ChopinLayout.STATE_DRAGGING_UP:
                        stateTv.setText("STATE_DRAGGING_UP");
                        break;
                    case ChopinLayout.STATE_REFRESHING:
                        stateTv.setText("STATE_REFRESHING");
                        break;
                    case ChopinLayout.STATE_LOADING:
                        stateTv.setText("STATE_LOADING");
                        break;
                }
            }
        });

        // Configure adapter.
        MusicAdapter musicAdapter = new MusicAdapter(this, DataRepo.generateMusicData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicAdapter);

        findViewById(R.id.linear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(CaseRecyclerViewActivity.this));


            }
        });
        findViewById(R.id.grid_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(new GridLayoutManager(CaseRecyclerViewActivity.this, SPAN_COUNT));
            }
        });
        findViewById(R.id.staggered_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL));
            }
        });
    }
}
