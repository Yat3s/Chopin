package com.yat3s.nimblerecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

import static android.R.attr.offset;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class NimbleRecyclerView extends LinearLayout {
    private static final String TAG = "NimbleRecyclerView";
    private static final int mVisibleThreshold = 4;

    private boolean isLoadingMore = false;
    private RecyclerView mRecyclerView;
    private PtrFrameLayout mPtrFrameLayout;

    // Listener
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;

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
        mPtrFrameLayout = new PtrFrameLayout(getContext());

        PtrClassicDefaultHeader ptrClassicHeader = new PtrClassicDefaultHeader(getContext());
        mPtrFrameLayout.setHeaderView(ptrClassicHeader);
        mPtrFrameLayout.addPtrUIHandler(ptrClassicHeader);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (null != mOnRefreshListener) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });

        mPtrFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));

        mPtrFrameLayout.setBackgroundResource(R.color.md_yellow_300);

        mRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT));

        addView(mRecyclerView);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.d(TAG, "onScrolled: " + mRecyclerView.canScrollVertically(RecyclerView.VERTICAL));
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


    }


    private int mLastX, mLastY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX(), y = (int) event.getY();
        Log.d(TAG, "onTouchEvent: " + x + ", " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetX = x - mLastX;
                int offsetY = y - mLastY;
                move(offsetX, offsetY);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void move(int offsetX, int offsetY) {
        Log.d(TAG, "move: " + offsetX + ", " + offsetY);
        mRecyclerView.offsetLeftAndRight(offsetX);
        mRecyclerView.offsetLeftAndRight(offsetY);
    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void loadMoreComplete() {
        isLoadingMore = false;
    }

    public void refreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }

    public void setRefreshHeaderView(RefreshHeaderView headerView) {
        mPtrFrameLayout.setHeaderView(headerView);
        mPtrFrameLayout.addPtrUIHandler(headerView);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // if dy>0 --> pull down
                if (lastVisibleItem >= totalItemCount - mVisibleThreshold && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
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

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
