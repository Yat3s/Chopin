package com.yat3s.kitten.sample.cases;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yat3s.kitten.decoration.StickyHeaderItemDecoration;
import com.yat3s.kitten.sample.Music;
import com.yat3s.kitten.sample.MusicAdapter;
import com.yat3s.kitten.sample.R;

import java.util.ArrayList;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class CaseRecyclerViewActivity extends BaseCaseActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_recycler_view;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Configure adapter.
        MusicAdapter musicAdapter = new MusicAdapter(this, generateMusicData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicAdapter);

        // Add Sticky header item decoration.
        recyclerView.addItemDecoration(new StickyHeaderItemDecoration(this, musicAdapter));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
