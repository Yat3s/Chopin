package com.yat3s.chopin;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yat3s.chopin.indicator.LoadingFooterIndicatorProvider;
import com.yat3s.chopin.indicator.RefreshHeaderIndicatorProvider;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ChopinLayout extends ViewGroup {
    private static final int STATE_IDLE = 0;

    private static final int STATE_DRAGGING_DOWN = 1;

    private static final int STATE_DRAGGING_UP = 2;

    private static final int STATE_REFRESHING = 3;

    private static final int STATE_LOADING = 4;

    private static final String TAG = "ChopinLayout";

    // Support child view count nested in this, NOW only support one child.
    private static final int SUPPORT_CHILD_COUNT = 1;

    // Scroller duration while release to do some action.
    private static final int SCROLLER_DURATION = 800;

    // Scroll resistance, if it equal 0f will scroll no any friction,
    // if it equal 1f will can not scroll.
    private float mIndicatorScrollResistance = 0.32f;

    // The last touch position while intercepted touch event.
    private int mLastTouchX, mLastTouchY;

    // The Scroller for scroll whole view natural.
    private Scroller mScroller;

    /**
     * It is used for check content view whether can be refresh/loading or other action.
     * The default checker is only check whether view has scrolled to top or bottom.
     * <p>
     * {@see} {@link DefaultViewScrollChecker#canDoRefresh(ChopinLayout, View)},
     * {@link DefaultViewScrollChecker#canDoLoading(ChopinLayout, View)}
     */
    private ViewScrollChecker mViewScrollChecker = new DefaultViewScrollChecker();

    // The header indicator.
    private View mHeaderIndicator;

    // The footer indicator.
    private View mFooterIndicator;

    // The provider for provide header indicator and some interfaces with interaction,
    // eg. header indicator animation.
    private RefreshHeaderIndicatorProvider mRefreshHeaderIndicatorProvider;

    // The provider for provide footer indicator and some interfaces with interaction,
    // eg. footer indicator animation.
    private LoadingFooterIndicatorProvider mLoadingFooterIndicatorProvider;

    // Knowing whether recycler view is refreshing.
    private boolean isRefreshing;

    // Knowing whether recycler view is loading more.
    private boolean isLoadingMore;

    // The refresh listener
    private OnRefreshListener mOnRefreshListener;

    // The load more listener
    private OnLoadMoreListener mOnLoadMoreListener;

    // The content view of user set.
    private ContentViewWrapper mContentViewWrapper;

    /**
     * Set visible threshold count while {@link #autoTriggerLoadMore} is true,
     */
    private int mLoadMoreRemainShowItemCount = 2;

    /**
     * If set true, it will auto trigger load more while visible
     * item < {@link #mLoadMoreRemainShowItemCount}
     */
    private boolean autoTriggerLoadMore = false;

    /**
     * If true, it means the content view has scrolled to top and
     * user try to pull down {@link #getScrollY()} < 0,
     */
    private boolean intendToRefresh = false;

    /**
     * If true, it means the content view has scrolled to bottom and
     * user try to pull up {@link #getScrollY()} > 0,
     */
    private boolean intendToLoading = false;

    // The user can drag content over screen, like iOS TableView default scroll effect.
    private boolean enableOverScroll = true;


    private int mStartInterceptTouchY;

    public ChopinLayout(Context context) {
        this(context, null);
    }

    public ChopinLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChopinLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Obtain content view after inflated, and it can be only support nested {@link #SUPPORT_CHILD_COUNT}
     * Setup auto load more if content view is RecyclerView, {@see} {@link #setupRecyclerViewAutoLoadMore(RecyclerView)}
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > SUPPORT_CHILD_COUNT) {
            throw new IllegalArgumentException("It can ONLY set ONE child view!");
        } else if (getChildCount() == SUPPORT_CHILD_COUNT) {
            mContentViewWrapper = new ContentViewWrapper(getChildAt(0));

            // Set up auto load more if content view is RecyclerView.
            if (mContentViewWrapper.getContentView() instanceof RecyclerView) {
                setupRecyclerViewAutoLoadMore((RecyclerView) mContentViewWrapper.getContentView());
            }
        }
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int idx = 0; idx < getChildCount(); idx++) {
            measureChild(getChildAt(idx), widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Layout content view in suitable position.
     * Layout refresh header indicator on top of content view and layout loading footer indicator on
     * the bottom of content view in order to hide in the default status.
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Layout content view.
        mContentViewWrapper.layout();

        // Layout refresh header indicator on top of content view.
        if (null != mHeaderIndicator) {
            mHeaderIndicator.layout(0, -mHeaderIndicator.getMeasuredHeight(),
                    mHeaderIndicator.getMeasuredWidth(), 0);
        }

        // Layout loading footer indicator on the bottom of content view.
        if (null != mFooterIndicator) {
            mFooterIndicator.layout(0,
                    mContentViewWrapper.getContentView().getMeasuredHeight(),
                    mFooterIndicator.getMeasuredWidth(),
                    mContentViewWrapper.getContentView().getMeasuredHeight()
                            + mFooterIndicator.getMeasuredHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!enableOverScroll) {
            return super.dispatchTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "event--> dispatchTouchEvent: DOWN, true");

                // Dispatch ACTION_DOWN event to child for process if child never consume
                // this event.
                super.dispatchTouchEvent(ev);

                // FORCE to dispatch this motion for it's going to process all event while
                // child not consume this event. for example: it nested with a LinearLayout
                // and this LinearLayout never consume this event so parent will return False
                // for disable dispatch this motion.
                // REF: it is a Recursion method, so it will execute the last child dispatch method.
                return true;
        }
        boolean dispatch = super.dispatchTouchEvent(ev);
        String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
                ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
        Log.d(TAG, "event--> dispatchTouchEvent: " + actionName + ", " + dispatch);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!enableOverScroll) {
            return super.onInterceptTouchEvent(ev);
        }
        int x = (int) ev.getX(), y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = x - mLastTouchX;
                int offsetY = y - mLastTouchY;

                if (isRefreshing || isLoadingMore) {
                    Log.d(TAG, "event--> onInterceptTouchEvent: MOVE ,true intercepted while refreshing!");
                    return true;
                }

                // Intercept pull down event when it is scrolling to top.
                if (offsetY > Math.abs(offsetX)) {
                    boolean canDoRefresh = mViewScrollChecker.canDoRefresh(this, mContentViewWrapper.getContentView());
                    if (canDoRefresh) {
                        mStartInterceptTouchY = y;
                        Log.d(TAG, "event--> onInterceptTouchEvent: MOVE ,true intercepted while refreshing!");
                    }
                    return canDoRefresh;
                }

                // Intercept pull up event when it is scrolling to bottom.
                if (-offsetY > Math.abs(offsetX)) {
                    boolean canDoLoading = mViewScrollChecker.canDoLoading(this, mContentViewWrapper.getContentView());
                    if (canDoLoading) {
                        mStartInterceptTouchY = y;
                    }
                    return canDoLoading;
                }
        }

        boolean intercept = super.onInterceptTouchEvent(ev);
        String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
                ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
        Log.d(TAG, "event--> onInterceptTouchEvent: " + actionName + ", " + intercept);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!enableOverScroll) {
            return super.onTouchEvent(ev);
        }

        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetY = y - mStartInterceptTouchY;
                int scrollOffsetY = (int) (offsetY * (1 - mIndicatorScrollResistance));
                mContentViewWrapper.translateVerticalWithOffset(scrollOffsetY);

                if (null != mRefreshHeaderIndicatorProvider && mContentViewWrapper.getTranlatedY() > 0) {
                    // Scroll distance has over refresh header indicator height.
                    int progress = 100 * mContentViewWrapper.getTranlatedY() / mHeaderIndicator.getMeasuredHeight();
                    mRefreshHeaderIndicatorProvider.onRefreshHeaderViewScrollChange(progress);
                    Log.d(TAG, "progressR: " + progress);
                }

                if (null != mLoadingFooterIndicatorProvider && mContentViewWrapper.getTranlatedY() < 0) {
                    int progress = 100 * -mContentViewWrapper.getTranlatedY() / mFooterIndicator.getMeasuredHeight();
                    mLoadingFooterIndicatorProvider.onFooterViewScrollChange(progress);
                    Log.d(TAG, "progressF: " + progress);
                }
                mLastTouchY = y;
                Log.d(TAG, "event--> onTouchEvent: MOVE, true");
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mContentViewWrapper.hasTranslated()) {
                    mContentViewWrapper.releaseToDefaultState();
                }

                if (mContentViewWrapper.hasTranslated()) {
                    if (null != mRefreshHeaderIndicatorProvider) {
                        if (isRefreshing) {
                            releaseViewToRefreshingStatus();
                        } else if (-getScrollY() >= mHeaderIndicator.getMeasuredHeight()) {
                            // Start refreshing while scrollY exceeded refresh header indicator height.
                            releaseViewToRefreshingStatus();
                            startRefresh();
                        } else {
                            // Cancel some move events while it not meet refresh or loading demands.
                            releaseViewToDefaultStatus();
                        }
                    } else {
                        // Abort this scroll "journey" if has some unexpected exceptions.
                        releaseViewToDefaultStatus();
                    }
                } else {
                    if (null != mLoadingFooterIndicatorProvider) {
                        if (isLoadingMore) {
                            releaseViewToLoadingStatus();
                        } else if (getScrollY() >= mFooterIndicator.getMeasuredHeight()) {
                            releaseViewToLoadingStatus();
                            startLoading();
                        } else {
                            releaseViewToDefaultStatus();
                        }
                    } else {
                        releaseViewToDefaultStatus();
                    }
                }
        }
        boolean touch = super.onTouchEvent(ev);
        String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
                ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
        Log.d(TAG, "event--> onTouchEvent: " + actionName + ", " + touch);
        return touch;
    }

    private void releaseViewToRefreshingStatus() {
        mScroller.startScroll(0, getScrollY(), 0, -(mHeaderIndicator.getMeasuredHeight() + getScrollY()),
                SCROLLER_DURATION);
    }

    private void releaseViewToLoadingStatus() {
        mScroller.startScroll(0, getScrollY(), 0, -(getScrollY() - mFooterIndicator.getMeasuredHeight()),
                SCROLLER_DURATION);
    }

    private void releaseViewToDefaultStatus() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY());
    }

    private void startRefresh() {
        isRefreshing = true;
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshing();
        }
    }

    private void startLoading() {
        isLoadingMore = true;
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onLoading();
        }
    }

    public void refreshComplete() {
        releaseViewToDefaultStatus();
        isRefreshing = false;
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshComplete();
        }
    }

    public void loadMoreComplete() {
        releaseViewToDefaultStatus();
        isLoadingMore = false;
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onLoadingComplete();
        }
    }

    private void initialize() {
        mScroller = new Scroller(getContext());
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        Log.d(TAG, "computeScroll: ");
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * Set Header indicator but it can not do refresh and it is hide in default status, you should
     * scroll over screen and will find it.
     * If you want enable refresh you can use {@link #setRefreshHeaderIndicator(RefreshHeaderIndicatorProvider)}
     * to setup refresh effect and tie something like refresh header scroll progress.
     *
     * @param headerIndicator
     */
    public void setHeaderIndicator(View headerIndicator) {
        mHeaderIndicator = headerIndicator;
        addView(mHeaderIndicator);
    }

    public void setFooterIndicator(View footerIndicator) {
        mFooterIndicator = footerIndicator;
    }

    public void setRefreshHeaderIndicator(RefreshHeaderIndicatorProvider refreshHeaderIndicatorProvider) {
        mRefreshHeaderIndicatorProvider = refreshHeaderIndicatorProvider;
        mHeaderIndicator = refreshHeaderIndicatorProvider.getContentView();
        if (null != mHeaderIndicator) {
            mHeaderIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mHeaderIndicator);
        }
    }

    public void setLoadingFooterIndicator(LoadingFooterIndicatorProvider loadingFooterIndicatorProvider) {
        mLoadingFooterIndicatorProvider = loadingFooterIndicatorProvider;
        mFooterIndicator = loadingFooterIndicatorProvider.getContentView();
        if (null != mFooterIndicator) {
            mFooterIndicator.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(mFooterIndicator);
        }

        // You can only choose a load more style.
        autoTriggerLoadMore = false;
    }

    /**
     * Setup auto trigger load more.
     *
     * @param recyclerView
     */
    private void setupRecyclerViewAutoLoadMore(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Auto load more.
                if (autoTriggerLoadMore) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    // if lastVisibleItem >= totalItemCount - mLoadMoreRemainItemCount
                    // and pull down will auto trigger load more.
                    if (lastVisibleItemPosition >= totalItemCount - mLoadMoreRemainShowItemCount
                            && dy > 0
                            && !isLoadingMore) {
                        isLoadingMore = true;
                        mOnLoadMoreListener.onLoadMore();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    /**
     * NOTE: It can ONLY be used for {@link android.support.v7.widget.RecyclerView} and {@link android.widget.AbsListView}
     * {@ref} {@link #setupRecyclerViewAutoLoadMore(RecyclerView)}
     * <p>
     * NOTE: You can ONLY choose one load more style from {@link #setLoadingFooterIndicator(LoadingFooterIndicatorProvider)}
     * and this.
     * Please remove Load Footer View while you set autoTriggerLoadMore is true.
     *
     * @param autoTriggerLoadMore If true will auto trigger load more while
     *                            remain to show item < {@link #mLoadMoreRemainShowItemCount}
     */
    public void setAutoTriggerLoadMore(boolean autoTriggerLoadMore) {
        this.autoTriggerLoadMore = autoTriggerLoadMore;
    }

    /**
     * NOTE: It can ONLY be used for {@link android.support.v7.widget.RecyclerView} and {@link android.widget.AbsListView}
     * NOTE: It can ONLY be used when {@link #autoTriggerLoadMore} is true.
     *
     * @param remainShowItemCount
     */
    public void setLoadMoreRemainItemCount(int remainShowItemCount) {
        mLoadMoreRemainShowItemCount = remainShowItemCount;
    }

    /**
     * Set indicator scroll resistance,
     * as the resistance coefficient increases, it will become increasingly difficult to slide.
     *
     * @param indicatorScrollResistance
     */
    public void setIndicatorScrollResistance(@FloatRange(from = 0, to = 1.0f) float indicatorScrollResistance) {
        mIndicatorScrollResistance = indicatorScrollResistance;
    }

    /**
     * If true user can drag content view over screen, it like iOS default TableView scroll effect.
     * The default value is true.
     *
     * @param enableOverScroll
     */
    public void setEnableOverScroll(boolean enableOverScroll) {
        this.enableOverScroll = enableOverScroll;
    }

    /**
     * It is used for check content view whether can be refresh/loading or other action.
     * You can do some custom edition for do refresh/loading checking.
     *
     * @param viewScrollChecker
     */
    public void setViewScrollChecker(@NonNull com.yat3s.chopin.ViewScrollChecker viewScrollChecker) {
        mViewScrollChecker = viewScrollChecker;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}
