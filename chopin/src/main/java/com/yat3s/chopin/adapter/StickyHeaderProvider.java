package com.yat3s.chopin.adapter;

import android.util.SparseArray;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yat3s on 27/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class StickyHeaderProvider {
    private Map<Object, View> mViews;
    private SparseArray<View> mStickyHeaderViews;

    public StickyHeaderProvider() {
        mStickyHeaderViews = new SparseArray<>();
        mViews = new HashMap<>();
    }

    public View getStickyHeaderView(int position) {
        return mStickyHeaderViews.get(position);
    }
}
