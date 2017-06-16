package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.yat3s.kitten.KittenView;
import com.yat3s.kitten.adapter.NimbleAdapter;
import com.yat3s.kitten.adapter.NimbleViewHolder;
import com.yat3s.kitten.adapter.StickyHeaderAdapter;
import com.yat3s.kitten.decoration.HeaderItemDecoration;
import com.yat3s.nimblerecyclerview.widget.ScrollableView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private KittenView mRecyclerView;
    private ScrollableView mScrollableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (KittenView) findViewById(R.id.recycler_view);
        mScrollableView = (ScrollableView) findViewById(R.id.scrollable_view);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScrollableView.starBackHome(200, 200);
            }
        });

        final TextView header = (TextView) getLayoutInflater().inflate(R.layout.layout_refresh_header, null, false);
        TodoAdapter todoAdapter = new TodoAdapter(this, generateMockData());
        mRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.getRecyclerView().setAdapter(todoAdapter);
        mRecyclerView.setRefreshHeaderView(new KittenView.RefreshHeaderViewProvider() {
            @Override
            public View provideContentView() {
                return header;
            }

            @Override
            public void onStartRefresh() {
                header.animate().setDuration(2000).translationX(500).start();
            }

            @Override
            public void onRefreshComplete() {
                header.animate().translationX(0).start();
            }

            @Override
            public void onRefreshHeaderViewScrollChange(int progress) {
                header.setText("Refresh" + progress);
            }
        });
        mRecyclerView.setOnRefreshListener(new KittenView.OnRefreshListener() {
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

        mRecyclerView.getRecyclerView().addItemDecoration(new HeaderItemDecoration(this, header, todoAdapter));
        mRecyclerView.getRecyclerView().addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private ArrayList<Task> generateMockData() {
        String[] taskNames = getResources().getStringArray(R.array.animals);
        ArrayList<Task> tasks = new ArrayList<>();
        for (String taskName : taskNames) {
            tasks.add(new Task(taskName));
        }
        return tasks;
    }

    static class TodoAdapter extends NimbleAdapter<Task, NimbleViewHolder> implements StickyHeaderAdapter<NimbleViewHolder> {
        public TodoAdapter(Context context, List<Task> data) {
            super(context, data);
        }

        @Override
        protected void bindDataToItemView(NimbleViewHolder holder, Task task, int position) {
            holder.setTextView(R.id.title_tv, task.title);
        }

        @Override
        protected int getItemViewLayoutId(int position, Task data) {
            return R.layout.item_task;
        }

        @Override
        public void onBindHeaderViewHolder(NimbleViewHolder holder, int position) {
            holder.setTextView(R.id.header_tv, mDataSource.get(position).title);
        }

        @Override
        public int getHeaderViewLayoutId(int position) {
            return R.layout.header_layout;
        }

        @Override
        public boolean hasHeader(int position) {
            return position % 8 == 0 && position != 0;
        }
    }

    static class Task {
        public String title;

        public Task(String title) {
            this.title = title;
        }
    }
}
