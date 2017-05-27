//package com.yat3s.nimblerecyclerview;
//
//import android.content.Context;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.List;
//
//import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
//
///**
// * Created by Yat3s on 26/05/2017.
// * Email: hawkoyates@gmail.com
// * GitHub: https://github.com/yat3s
// */
//public abstract class StickyHeaderAdapter<T> extends NimbleAdapter {
//
//    private SparseArray<View> mStickyHeaderViewCache;
//
//    public StickyHeaderAdapter(Context context) {
//        this(context, null);
//    }
//
//    public StickyHeaderAdapter(Context context, List dataSource) {
//        super(context, dataSource);
//        mStickyHeaderViewCache = new SparseArray<>();
//    }
//
//    protected abstract void bindStickyHeader(NimbleViewHolder holder, T t, int position);
//
//    protected abstract int getStickyHeaderLayoutId(int position, T t);
//
//    protected abstract boolean hasStickyHeader(int position, T t);
//
//    @Override
//    public NimbleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new NimbleViewHolder(mInflater.inflate(R.layout.header_layout, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(NimbleViewHolder holder, int position) {
//        super.onBindViewHolder(holder, position);
//        bindStickyHeader(holder, position);
//    }
//}
