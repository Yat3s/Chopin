package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class NimbleRecyclerView extends PtrFrameLayout {

    private RecyclerView mRecyclerView;

    public NimbleRecyclerView(Context context) {
        this(context, null);
    }

    public NimbleRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NimbleRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();

    }

    private void initialize() {
        mRecyclerView = new RecyclerView(getContext());
        addView(mRecyclerView);
    }
}
