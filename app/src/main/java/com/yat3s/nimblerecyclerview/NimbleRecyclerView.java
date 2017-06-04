package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

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

        addPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIReset(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });
    }

    public void setRefreshHeaderView(RefreshHeaderView headerView) {
        addPtrUIHandler(headerView);
        setHeaderView(headerView);
    }


    public static class RefreshHeaderViewBuilder {
        private RefreshHeaderView mRefreshView;
        private View mContentView;

        public RefreshHeaderViewBuilder(Context context, View refreshView) {
            mRefreshView = new RefreshHeaderView(context);
            mContentView = refreshView;
            mRefreshView.addView(mContentView);
        }

        public RefreshHeaderViewBuilder setHeaderViewRefreshListener(HeaderViewRefreshListener listener) {
            mRefreshView.setHeaderViewRefreshListener(listener);
            return this;
        }

        public RefreshHeaderView build() {
            return mRefreshView;
        }

    }

    private static class RefreshHeaderView extends ViewGroup implements PtrUIHandler {
        private HeaderViewRefreshListener mListener;

        public RefreshHeaderView(Context context) {
            this(context, null);
        }

        public RefreshHeaderView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public RefreshHeaderView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public void setHeaderViewRefreshListener(HeaderViewRefreshListener listener) {
            mListener = listener;
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }

        @Override
        public void onUIReset(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshPrepare(PtrFrameLayout frame) {

        }

        @Override
        public void onUIRefreshBegin(PtrFrameLayout frame) {
            if (null != mListener) {
                mListener.onStartRefresh();
            }
        }

        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {

        }

        @Override
        public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        }
    }

    public interface HeaderViewRefreshListener {
        void onStartRefresh();

    }
}
