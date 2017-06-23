package com.yat3s.nimblerecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.yat3s.kitten.KittenRecyclerView;
import com.yat3s.kitten.decoration.KittenLoadingFooterView;
import com.yat3s.kitten.decoration.KittenRefreshHeaderView;
import com.yat3s.kitten.decoration.StickyHeaderItemDecoration;
import com.yat3s.nimblerecyclerview.widget.ScrollableView;

import java.util.ArrayList;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private KittenRecyclerView mRecyclerView;
    private ScrollableView mScrollableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (KittenRecyclerView) findViewById(R.id.recycler_view);
        mScrollableView = (ScrollableView) findViewById(R.id.scrollable_view);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScrollableView.starBackHome(200, 200);
            }
        });

        // Configure adapter.
        MusicAdapter musicAdapter = new MusicAdapter(this, generateAnimalData());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(musicAdapter);

        // Configure refresh header.
        KittenRefreshHeaderView kittenRefreshHeaderView = new KittenRefreshHeaderView(this, "Plane.json");
        kittenRefreshHeaderView.setScale(0.2f);
        mRecyclerView.setRefreshHeaderView(kittenRefreshHeaderView);
        mRecyclerView.setOnRefreshListener(new KittenRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.refreshComplete();
                    }
                }, 3000);
            }
        });

        // Configure loading footer.
        KittenLoadingFooterView kittenLoadingFooterView = new KittenLoadingFooterView(this, "Plane.json");
        kittenLoadingFooterView.setScale(0.2f);
        mRecyclerView.setLoadingFooterView(kittenLoadingFooterView);
        mRecyclerView.setOnLoadMoreListener(new KittenRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.loadMoreComplete();
                    }
                }, 3000);
            }
        });

        // Add Sticky header item decoration.
        mRecyclerView.addItemDecoration(new StickyHeaderItemDecoration(this, musicAdapter));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private ArrayList<Music> generateAnimalData() {
        String[] musicNames = getResources().getStringArray(R.array.musics);
        ArrayList<Music> musics = new ArrayList<>();
        for (String taskName : musicNames) {
            musics.add(new Music(taskName));
        }
        return musics;
    }
}
