package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yat3s.nimblerecyclerview.util.BaseAdapter;
import com.yat3s.nimblerecyclerview.util.BaseViewHolder;

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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View header = getLayoutInflater().inflate(R.layout.header_layout, null, false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new TodoAdapter(this, generateMockData()));

        mRecyclerView.addItemDecoration(new HeaderItemDecoration(this, header));

    }

    private List<Task> generateMockData() {
        List<Task> tasks = new ArrayList<>();
        String[] titles = {"Abby", "Romy", "Aran", "Abby", "Romy", "Aran", "Abby", "Romy", "Aran",
                "Abby", "Romy", "Aran", "Abby", "Romy", "Aran", "Abby", "Romy", "Aran",
                "Abby", "Romy", "Aran", "Abby", "Romy", "Aran", "Abby", "Romy", "Aran"};
        for (String title : titles) {
            tasks.add(new Task(title));
        }

        return tasks;
    }

    static class TodoAdapter extends BaseAdapter<Task> {
        public TodoAdapter(Context context, List<Task> data) {
            super(context, data);
        }

        @Override
        protected void bindDataToItemView(BaseViewHolder holder, Task data, int position) {
            if (position % 3 == 0) {
                holder.setText(R.id.header, "Header" + data.title);
            } else {
                holder.setText(R.id.title_tv, data.title);
                holder.itemView.setBackgroundColor((position & 1) == 0 ? Color.parseColor("#DDDDDD") : Color.WHITE);
            }
        }

        @Override
        protected int getItemViewLayoutId(int position, Task data) {
            if (position % 3 == 0) {
                return R.layout.header_layout;
            }
            return R.layout.item_task;
        }
    }

    static class Task {
        public String title;

        public Task(String title) {
            this.title = title;
        }
    }
}
