package com.yat3s.kitten.sample;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yat3s.kitten.sample.cases.CaseFragmentActivity;
import com.yat3s.kitten.sample.cases.CaseLinearLayoutActivity;
import com.yat3s.kitten.sample.cases.CaseRecyclerViewActivity;
import com.yat3s.kitten.sample.cases.CaseScrollViewActivity;
import com.yat3s.kitten.sample.cases.CaseViewPagerActivity;

/**
 * Created by Yat3s on 26/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.recycler_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CaseRecyclerViewActivity.class));
            }
        });

        findViewById(R.id.scroll_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CaseScrollViewActivity.class));
            }
        });

        findViewById(R.id.linear_layout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CaseLinearLayoutActivity.class));
            }
        });

        findViewById(R.id.view_pager_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CaseViewPagerActivity.class));
            }
        });

        findViewById(R.id.fragment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CaseFragmentActivity.class));
            }
        });
    }
}
