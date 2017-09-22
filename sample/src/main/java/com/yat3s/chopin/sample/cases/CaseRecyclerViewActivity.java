package com.yat3s.chopin.sample.cases;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yat3s.chopin.ChopinLayout;
import com.yat3s.chopin.indicator.LottieIndicator;
import com.yat3s.chopin.sample.DataRepository;
import com.yat3s.chopin.sample.MusicAdapter;
import com.yat3s.chopin.sample.R;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseRecyclerViewActivity extends BaseCaseActivity {
    private static final int LOAD_DELAY = 1000;

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_nested_recycler_view;
    }

    @Override
    protected void initialize() {
        // Configure adapter.
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final MusicAdapter musicAdapter = new MusicAdapter(this, DataRepository.generateMusicData(20));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicAdapter);

        // Configure indicator.
        LottieIndicator headerIndicator = new LottieIndicator(this, "xuanwheel_logo.json", 0.2f);
        mChopinLayout.setRefreshHeaderIndicator(headerIndicator);
        mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mChopinLayout.refreshComplete();
                    }
                }, LOAD_DELAY);
            }
        });

        LottieIndicator footerIndicator = new LottieIndicator(this, "loading.json", 0.15f);
        mChopinLayout.setLoadingFooterIndicator(footerIndicator);
        mChopinLayout.setOnLoadMoreListener(new ChopinLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mChopinLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        musicAdapter.addMoreDataSet(DataRepository.generateMusicData(10));
                        mChopinLayout.loadMoreComplete();
                    }
                }, LOAD_DELAY);
            }
        });

        mChopinLayout.setBackgroundResource(R.color.black);
        mChopinLayout.performRefresh();
    }
}
