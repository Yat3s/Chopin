package com.yat3s.nimblerecyclerview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yat3s.kitten.KittenLayout;
import com.yat3s.kitten.decoration.KittenLoadingFooterIndicator;
import com.yat3s.kitten.decoration.KittenRefreshHeaderIndicator;
import com.yat3s.kitten.decoration.StickyHeaderItemDecoration;

import java.util.ArrayList;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private KittenLayout mKittenLayout;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mKittenLayout = (KittenLayout) findViewById(R.id.kitten_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // Configure adapter.
        MusicAdapter musicAdapter = new MusicAdapter(this, generateMusicData());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(musicAdapter);

        // Configure refresh header.
        KittenRefreshHeaderIndicator kittenRefreshHeaderView = new KittenRefreshHeaderIndicator(this, "Plane.json");
        kittenRefreshHeaderView.setScale(0.2f);
        mKittenLayout.setRefreshHeaderIndicator(kittenRefreshHeaderView);
        mKittenLayout.setOnRefreshListener(new KittenLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.refreshComplete();
                    }
                }, 3000);
            }
        });

        // Configure loading footer.
        KittenLoadingFooterIndicator kittenLoadingFooterView = new KittenLoadingFooterIndicator(this, "Plane.json");
        kittenLoadingFooterView.setScale(0.2f);
        mKittenLayout.setLoadingFooterIndicator(kittenLoadingFooterView);
        mKittenLayout.setOnLoadMoreListener(new KittenLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mKittenLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mKittenLayout.loadMoreComplete();
                    }
                }, 3000);
            }
        });

        // Add Sticky header item decoration.
        mRecyclerView.addItemDecoration(new StickyHeaderItemDecoration(this, musicAdapter));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private ArrayList<Music> generateMusicData() {
        String[] musicNames = getResources().getStringArray(R.array.musics);
        ArrayList<Music> musics = new ArrayList<>();
        for (String taskName : musicNames) {
            musics.add(new Music(taskName));
        }
        return musics;
    }
}
