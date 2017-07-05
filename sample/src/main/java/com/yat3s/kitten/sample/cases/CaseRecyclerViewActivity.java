package com.yat3s.kitten.sample.cases;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

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
    private static final int GRID_SPAN = 3;

    private static final int STYLE_GRID = 0x1001;
    private static final int STYLE_LINEAR = 0x1002;


     RecyclerView recyclerView;
    @Override
    protected int getContentLayoutId() {
        return R.layout.case_activity_recycler_view;
    }

    @Override
    protected void initialize() {
        setupRefreshHeader("refresh.json", 0.2f, 3000);
        setupLoadingFooter("Plane.json", 0.2f, 1500);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final Button styleSwitchBtn = (Button) findViewById(R.id.style_switch_btn);

        // Configure adapter.
        MusicAdapter musicAdapter = new MusicAdapter(this, generateMusicData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(musicAdapter);

        // Add Sticky header item decoration.
//        recyclerView.addItemDecoration(new StickyHeaderItemDecoration(this, musicAdapter));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        styleSwitchBtn.setText("Switch to GridLayoutManager");
        styleSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(CaseRecyclerViewActivity.this));
                    styleSwitchBtn.setText("Switch to GridLayoutManager");
                }  else {
                    recyclerView.setLayoutManager(new GridLayoutManager(CaseRecyclerViewActivity.this, GRID_SPAN));
                    styleSwitchBtn.setText("Switch to LinearLayoutManager");
                }
            }
        });
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
