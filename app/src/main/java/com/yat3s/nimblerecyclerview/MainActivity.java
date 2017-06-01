package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Button button = (Button) findViewById(R.id.button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        View header = getLayoutInflater().inflate(R.layout.header_layout, null, false);
        TodoAdapter todoAdapter = new TodoAdapter(this, generateMockData());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(todoAdapter);

        mRecyclerView.addItemDecoration(new HeaderItemDecoration(this, header, todoAdapter));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
