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
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.yat3s.chopin.indicator.LoadingFooterIndicatorProvider;
import com.yat3s.chopin.indicator.RefreshHeaderIndicatorProvider;
import com.yat3s.chopin.wrapper.BaseViewWrapper;
import com.yat3s.chopin.wrapper.ContentViewWrapper;
import com.yat3s.chopin.wrapper.IndicatorViewWrapper;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ChopinLayout extends ViewGroup {
    static final boolean DEBUG = true;

    public static final int STATE_DEFAULT = 0;

    public static final int STATE_DRAGGING_DOWN = 1;

    public static final int STATE_DRAGGING_UP = 2;

    public static final int STATE_REFRESHING = 3;

    public static final int STATE_LOADING = 4;

    public static final int STATE_BOUNCING = 5;

    public static final int INDICATOR_STYLE_BEHIND = 0x100;

    public static final int INDICATOR_STYLE_FRONT = 0x101;

    public static final int INDICATOR_STYLE_BORDER = 0x102;

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
     *
     * @see {@link DefaultViewScrollChecker#canDoRefresh(ChopinLayout, View)},
     * {@link DefaultViewScrollChecker#canDoLoading(ChopinLayout, View)}
     */
    private ViewScrollChecker mViewScrollChecker = new DefaultViewScrollChecker();

    // The header indicator.
    private IndicatorViewWrapper mHeaderIndicatorView;

    // The footer indicator.
    private IndicatorViewWrapper mFooterIndicatorView;

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

    private boolean mHasDispatchCancelEvent = false;

    private boolean canBeDragOver = false;

    private MotionEvent mLastMoveEvent;

    private int mStartInterceptTouchY;

    private int mHeaderIndicatorStyle = INDICATOR_STYLE_BORDER;

    private int mFooterIndicatorStyle = INDICATOR_STYLE_BORDER;

    private int mState = STATE_DEFAULT;

    private OnStateChangeListener mOnStateChangeListener;

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
            if (mContentViewWrapper.getView() instanceof RecyclerView) {
                setupRecyclerViewAutoLoadMore((RecyclerView) mContentViewWrapper.getView());
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
        mContentViewWrapper.getView().bringToFront();

        // Layout refresh header indicator view.
        if (null != mHeaderIndicatorView) {
            int top = 0, bottom = 0;
            switch (mHeaderIndicatorStyle) {
                case INDICATOR_STYLE_FRONT:
                    mHeaderIndicatorView.getView().bringToFront();
                case INDICATOR_STYLE_BORDER:
                    top = -mHeaderIndicatorView.getHeight();
                    bottom = 0;
                    break;
                case INDICATOR_STYLE_BEHIND:
                    top = 0;
                    bottom = mHeaderIndicatorView.getHeight();
                    break;
            }
            mHeaderIndicatorView.layout(0, top, mHeaderIndicatorView.getWidth(), bottom);
        }

        // Layout loading footer indicator view.
        if (null != mFooterIndicatorView) {
            int top = 0, bottom = 0;
            switch (mFooterIndicatorStyle) {
                case INDICATOR_STYLE_FRONT:
                    mFooterIndicatorView.getView().bringToFront();
                case INDICATOR_STYLE_BORDER:
                    top = mContentViewWrapper.getView().getMeasuredHeight();
                    bottom = mContentViewWrapper.getView().getMeasuredHeight() +
                            mFooterIndicatorView.getHeight();
                    break;
                case INDICATOR_STYLE_BEHIND:
                    top = mContentViewWrapper.getView().getMeasuredHeight() -
                            mFooterIndicatorView.getHeight();
                    bottom = mContentViewWrapper.getView().getMeasuredHeight();
                    break;
            }
            mFooterIndicatorView.layout(0, top, mFooterIndicatorView.getWidth(), bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!enableOverScroll) {
            return super.dispatchTouchEvent(ev);
        }

        int x = (int) ev.getX(), y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = x;
                mLastTouchY = y;
                canBeDragOver = false;
                mHasDispatchCancelEvent = false;
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

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                int dx = x - mLastTouchX;
                int dy = y - mLastTouchY;

                Log.d(TAG, "dispatchTouchEvent: dy " + dy);

                // Use intent to pull down
                boolean pullDown = dy > 0;
                if (canBeDragOver) {
                    int offsetY = y - mStartInterceptTouchY;
                    int translationOffsetY = (int) (offsetY * (1 - mIndicatorScrollResistance));
                    translateView(translationOffsetY);

                    if (offsetY > 0) {
                        setState(STATE_DRAGGING_DOWN);
                    } else if (offsetY < 0) {
                        setState(STATE_DRAGGING_UP);
                    }

                    // Dispatch cancel event for cancel user click trigger.
                    if (!mHasDispatchCancelEvent) {
                        sendCancelEvent();
                        mHasDispatchCancelEvent = true;
                    }

                    if (null != mRefreshHeaderIndicatorProvider && translationOffsetY > 0) {
                        // Scroll distance has over refresh header indicator height.
                        int progress = 100 * translationOffsetY / mHeaderIndicatorView.getHeight();
                        mRefreshHeaderIndicatorProvider.onHeaderIndicatorViewScrollChange(progress);
                        Log.d(TAG, "progressR: " + progress);
                    }

                    if (null != mLoadingFooterIndicatorProvider && translationOffsetY < 0) {
                        int progress = 100 * -translationOffsetY / mFooterIndicatorView.getHeight();
                        mLoadingFooterIndicatorProvider.onFooterIndicatorViewScrollChange(progress);
                        Log.d(TAG, "progressF: " + progress);
                    }

                    return true;
                } else {
                    setState(STATE_DEFAULT);
                    if (pullDown && mViewScrollChecker.canDoRefresh(this, mContentViewWrapper.getView())
                            && dy > Math.abs(dx)) {
                        mStartInterceptTouchY = y;
                        canBeDragOver = true;
                        Log.d(TAG, "dispatchTouchEvent: canBeDragOver pull down");
                        return true;
                    } else if (mViewScrollChecker.canDoLoading(this, mContentViewWrapper.getView())
                            && -dy > Math.abs(dx)) {
                        canBeDragOver = true;
                        mStartInterceptTouchY = y;
                        Log.d(TAG, "dispatchTouchEvent: canBeDragOver pull up");
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                canBeDragOver = false;
                int translatedOffsetY = mContentViewWrapper.getTranslationY();
                if (translatedOffsetY > 0) {
                    if (null != mRefreshHeaderIndicatorProvider) {
                        if (isRefreshing) {
                            releaseViewToRefreshingStatus();
                        } else if (translatedOffsetY >= mHeaderIndicatorView.getHeight()) {
                            // Start refreshing while scrollY exceeded refresh header indicator height.
                            releaseViewToRefreshingStatus();
                        } else {
                            // Abort some move events while it not meet refresh or loading demands.
                            abortThisDrag();
                        }
                    } else {
                        // Cancel this scroll "journey" if has some unexpected exceptions.
                        releaseViewToDefaultStatus();
                    }
                } else {
                    if (null != mLoadingFooterIndicatorProvider) {
                        if (isLoadingMore) {
                            abortThisDrag();
                        } else if (-translatedOffsetY >= mFooterIndicatorView.getHeight()) {
                            releaseViewToLoadingStatus();
                        } else {
                            abortThisDrag();
                        }
                    } else {
                        releaseViewToDefaultStatus();
                    }
                }
                break;
        }
        boolean dispatch = super.dispatchTouchEvent(ev);
        String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
                ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
        Log.d(TAG, "event--> dispatchTouchEvent: " + actionName + ", " + dispatch);
        return super.dispatchTouchEvent(ev);
    }

    private void translateView(int translationOffsetY) {
        if (translationOffsetY > 0) {
            switch (mHeaderIndicatorStyle) {
                case INDICATOR_STYLE_FRONT:
                    if (null != mHeaderIndicatorView) {
                        mHeaderIndicatorView.translateVerticalWithOffset(translationOffsetY);
                    }
                    break;
                case INDICATOR_STYLE_BEHIND:
                    mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                    break;
                case INDICATOR_STYLE_BORDER:
                    if (null != mHeaderIndicatorView) {
                        mHeaderIndicatorView.translateVerticalWithOffset(translationOffsetY);
                    }
                    mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                    break;
            }
        } else {
            switch (mFooterIndicatorStyle) {
                case INDICATOR_STYLE_FRONT:
                    if (null != mFooterIndicatorView) {
                        mFooterIndicatorView.translateVerticalWithOffset(translationOffsetY);
                    }
                    break;
                case INDICATOR_STYLE_BEHIND:
                    mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                    break;
                case INDICATOR_STYLE_BORDER:
                    if (null != mFooterIndicatorView) {
                        mFooterIndicatorView.translateVerticalWithOffset(translationOffsetY);
                    }
                    mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                    break;
            }
        }
    }

    /**
     * Refreshing
     */
    private void releaseViewToRefreshingStatus() {
        setState(STATE_BOUNCING);
        if (null != mRefreshHeaderIndicatorProvider) {
            int start = mHeaderIndicatorStyle == INDICATOR_STYLE_BEHIND
                    ? mContentViewWrapper.getTranslationY()
                    : mHeaderIndicatorView.getTranslationY();
            mHeaderIndicatorView.animateTranslationY(start, mHeaderIndicatorView.getHeight(),
                    new BaseViewWrapper.AnimateListener() {
                        @Override
                        public void onAnimate(int value) {
                            int progress = 100 * value / mHeaderIndicatorView.getHeight();
                            Log.d(TAG, "Refresh progress: " + value);
                            mRefreshHeaderIndicatorProvider.onHeaderIndicatorViewScrollChange(progress);
                            mContentViewWrapper.translateVerticalWithOffset(value);
                        }

                        @Override
                        public void onFinish() {
                            startRefresh();
                        }
                    });
        }
    }

    /**
     * Loading
     */
    private void releaseViewToLoadingStatus() {
        setState(STATE_BOUNCING);
        if (null != mLoadingFooterIndicatorProvider) {
            int start = mFooterIndicatorStyle == INDICATOR_STYLE_BEHIND
                    ? mContentViewWrapper.getTranslationY()
                    : mFooterIndicatorView.getTranslationY();
            mFooterIndicatorView.animateTranslationY(start, mFooterIndicatorView.getHeight(),
                    new BaseViewWrapper.AnimateListener() {
                        @Override
                        public void onAnimate(int value) {
                            int progress = 100 * value / mFooterIndicatorView.getHeight();
                            Log.d(TAG, "Refresh progress: " + value);
                            mRefreshHeaderIndicatorProvider.onHeaderIndicatorViewScrollChange(progress);
                            mContentViewWrapper.translateVerticalWithOffset(value);
                        }

                        @Override
                        public void onFinish() {
                            startLoading();
                        }
                    });
        }
    }

    /**
     * Default
     */
    private void releaseViewToDefaultStatus() {
        mContentViewWrapper.releaseToDefaultStatus();
        if ((mState == STATE_DRAGGING_DOWN || mState == STATE_REFRESHING || mState == STATE_LOADING)
                && null != mRefreshHeaderIndicatorProvider) {

            // Bouncing start.
            setState(STATE_BOUNCING);

            int start = mHeaderIndicatorStyle == INDICATOR_STYLE_BEHIND
                    ? mContentViewWrapper.getTranslationY()
                    : mHeaderIndicatorView.getTranslationY();
            mHeaderIndicatorView.animateTranslationY(start, 0,
                    new BaseViewWrapper.AnimateListener() {
                        @Override
                        public void onAnimate(int value) {
                            int progress = 100 * value / mHeaderIndicatorView.getHeight();
                            Log.d(TAG, "Refresh progress: " + value);
                            mRefreshHeaderIndicatorProvider.onHeaderIndicatorViewScrollChange(progress);
                        }

                        @Override
                        public void onFinish() {
                            // Bouncing end.
                            setState(STATE_DEFAULT);
                        }
                    });
            mRefreshHeaderIndicatorProvider.onCancel();
        }

        if ((mState == STATE_DRAGGING_UP || mState == STATE_REFRESHING || mState == STATE_LOADING)
                && null != mLoadingFooterIndicatorProvider) {
            setState(STATE_BOUNCING);

            int start = mHeaderIndicatorStyle == INDICATOR_STYLE_BEHIND
                    ? mContentViewWrapper.getTranslationY()
                    : mHeaderIndicatorView.getTranslationY();
            mFooterIndicatorView.animateTranslationY(0, mFooterIndicatorView.getTranslationY(), new IndicatorViewWrapper
                    .AnimateListener() {
                @Override
                public void onAnimate(int value) {
                    int progress = 100 * value / mFooterIndicatorView.getHeight();
                    mLoadingFooterIndicatorProvider.onFooterIndicatorViewScrollChange(progress);
                }

                @Override
                public void onFinish() {
                    setState(STATE_DEFAULT);
                }
            });
            mLoadingFooterIndicatorProvider.onCancel();
        }
    }

    private void abortThisDrag() {
        releaseViewToDefaultStatus();
        if (mState == STATE_DRAGGING_DOWN && null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onCancel();
        }
        if (mState == STATE_DRAGGING_UP && null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onCancel();
        }
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent event = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout()
                , MotionEvent.ACTION_CANCEL, mLastTouchX, mLastTouchY, last.getMetaState());
        super.dispatchTouchEvent(event);
    }

    private void setState(int state) {
        if (mState == state) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "Setting state from " + mState + " to " + state);
        }
        mState = state;

        if (null != mOnStateChangeListener) {
            mOnStateChangeListener.onStateChanged(this, state);
        }
    }


    private void startRefresh() {
        isRefreshing = true;
        setState(STATE_REFRESHING);
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onRefreshing();
        }
    }

    private void startLoading() {
        isLoadingMore = true;
        setState(STATE_LOADING);
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
     * @param headerIndicatorView
     */
    public void setHeaderIndicatorView(View headerIndicatorView) {
        mHeaderIndicatorView = new IndicatorViewWrapper(headerIndicatorView);
        addView(headerIndicatorView);
    }

    public void setFooterIndicatorView(View footerIndicatorView) {
        mFooterIndicatorView = new IndicatorViewWrapper(footerIndicatorView);
        addView(footerIndicatorView);
    }

    public void setRefreshHeaderIndicator(RefreshHeaderIndicatorProvider refreshHeaderIndicatorProvider) {
        mRefreshHeaderIndicatorProvider = refreshHeaderIndicatorProvider;
        View contentView = refreshHeaderIndicatorProvider.getContentView();
        mHeaderIndicatorView = new IndicatorViewWrapper(contentView);
        if (null != contentView) {
            contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(contentView);
        }
    }

    public void setLoadingFooterIndicator(LoadingFooterIndicatorProvider loadingFooterIndicatorProvider) {
        mLoadingFooterIndicatorProvider = loadingFooterIndicatorProvider;
        View contentView = loadingFooterIndicatorProvider.getContentView();
        mFooterIndicatorView = new IndicatorViewWrapper(contentView);
        if (null != contentView) {
            contentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            addView(contentView);
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

    public void setHeaderIndicatorStyle(int headerIndicatorStyle) {
        mHeaderIndicatorStyle = headerIndicatorStyle;
        requestLayout();
    }

    public void setFooterIndicatorStyle(int footerIndicatorStyle) {
        mFooterIndicatorStyle = footerIndicatorStyle;
        requestLayout();
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        mOnStateChangeListener = onStateChangeListener;
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

    public interface OnStateChangeListener {
        void onStateChanged(ChopinLayout layout, int newState);
    }


    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (!enableOverScroll) {
//            return super.onInterceptTouchEvent(ev);
//        }
//        int x = (int) ev.getX(), y = (int) ev.getY();
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastTouchX = x;
//                mLastTouchY = y;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                int offsetX = x - mLastTouchX;
//                int offsetY = y - mLastTouchY;
//
//                if (isRefreshing || isLoadingMore) {
//                    Log.d(TAG, "event--> onInterceptTouchEvent: MOVE ,true intercepted while refreshing!");
//                    return true;
//                }
//
//                // Intercept pull down event when it is scrolling to top.
//                if (offsetY > Math.abs(offsetX)) {
//                    boolean canDoRefresh = mViewScrollChecker.canDoRefresh(this, mContentViewWrapper.getContentView());
//                    if (canDoRefresh) {
//                        mStartInterceptTouchY = y;
//                        Log.d(TAG, "event--> onInterceptTouchEvent: MOVE ,true intercepted while refreshing!");
//                    }
//                    return canDoRefresh;
//                }
//
//                // Intercept pull up event when it is scrolling to bottom.
//                if (-offsetY > Math.abs(offsetX)) {
//                    boolean canDoLoading = mViewScrollChecker.canDoLoading(this, mContentViewWrapper.getContentView());
//                    if (canDoLoading) {
//                        mStartInterceptTouchY = y;
//                    }
//                    return canDoLoading;
//                }
//        }
//
//        boolean intercept = super.onInterceptTouchEvent(ev);
//        String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
//                ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
//        Log.d(TAG, "event--> onInterceptTouchEvent: " + actionName + ", " + intercept);
//        return intercept;
//    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (!enableOverScroll) {
//            return super.onTouchEvent(ev);
//        }
//
//        int y = (int) ev.getY();
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                int offsetY = y - mStartInterceptTouchY;
//                int scrollOffsetY = (int) (offsetY * (1 - mIndicatorScrollResistance));
//                mContentViewWrapper.translateVerticalWithOffset(scrollOffsetY);
//
//                if (null != mRefreshHeaderIndicatorProvider && mContentViewWrapper.getTranslationY() > 0) {
//                    // Scroll distance has over refresh header indicator height.
//                    int progress = 100 * mContentViewWrapper.getTranslationY() / mHeaderIndicatorView.getMeasuredHeight();
//                    mRefreshHeaderIndicatorProvider.onHeaderIndicatorViewScrollChange(progress);
//                    Log.d(TAG, "progressR: " + progress);
//                }
//
//                if (null != mLoadingFooterIndicatorProvider && mContentViewWrapper.getTranslationY() < 0) {
//                    int progress = 100 * -mContentViewWrapper.getTranslationY() / mFooterIndicatorView.getMeasuredHeight();
//                    mLoadingFooterIndicatorProvider.onFooterIndicatorViewScrollChange(progress);
//                    Log.d(TAG, "progressF: " + progress);
//                }
//                mLastTouchY = y;
//                Log.d(TAG, "event--> onTouchEvent: MOVE, true");
//                return true;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                if (mContentViewWrapper.hasTranslated()) {
//                    mContentViewWrapper.releaseToDefaultStatus();
//                }
//
//                if (mContentViewWrapper.hasTranslated()) {
//                    if (null != mRefreshHeaderIndicatorProvider) {
//                        if (isRefreshing) {
//                            releaseViewToRefreshingStatus();
//                        } else if (-getScrollY() >= mHeaderIndicatorView.getMeasuredHeight()) {
//                            // Start refreshing while scrollY exceeded refresh header indicator height.
//                            releaseViewToRefreshingStatus();
//                            startRefresh();
//                        } else {
//                            // Cancel some move events while it not meet refresh or loading demands.
//                            releaseViewToDefaultStatus();
//                        }
//                    } else {
//                        // Abort this scroll "journey" if has some unexpected exceptions.
//                        releaseViewToDefaultStatus();
//                    }
//                } else {
//                    if (null != mLoadingFooterIndicatorProvider) {
//                        if (isLoadingMore) {
//                            releaseViewToLoadingStatus();
//                        } else if (getScrollY() >= mFooterIndicatorView.getMeasuredHeight()) {
//                            releaseViewToLoadingStatus();
//                            startLoading();
//                        } else {
//                            releaseViewToDefaultStatus();
//                        }
//                    } else {
//                        releaseViewToDefaultStatus();
//                    }
//                }
//        }
//        boolean touch = super.onTouchEvent(ev);
//        String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
//                ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
//        Log.d(TAG, "event--> onTouchEvent: " + actionName + ", " + touch);
//        return touch;
//    }
}
