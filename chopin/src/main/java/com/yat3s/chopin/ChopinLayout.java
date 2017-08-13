package com.yat3s.chopin;

import android.content.Context;
import android.graphics.Color;
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

import com.yat3s.chopin.indicator.Indicator;
import com.yat3s.chopin.wrapper.BaseViewWrapper;
import com.yat3s.chopin.wrapper.ContentViewWrapper;
import com.yat3s.chopin.wrapper.IndicatorViewWrapper;

/**
 * Created by Yat3s on 03/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ChopinLayout extends ViewGroup {

    private static final String TAG = "ChopinLayout";

    static final boolean DEBUG = true;

    private static final long DEFAULT_REFRESH_COMPLETE_COLLAPSE_DELAY = 100;
    private static final long DEFAULT_LOAD_MORE_COMPLETE_COLLAPSE_DELAY = 100;

    private static final long DEFAULT_HEADER_NOTIFICATION_VIEW_STAY_DURATION = 1000;
    private static final long DEFAULT_FOOTER_NOTIFICATION_VIEW_STAY_DURATION = 1000;

    public static final int STATE_DEFAULT = 0;

    public static final int STATE_DRAGGING_DOWN = 1;

    public static final int STATE_DRAGGING_UP = 2;

    public static final int STATE_REFRESHING = 3;

    public static final int STATE_LOADING = 4;

    public static final int STATE_BOUNCING_DOWN = 5;

    public static final int STATE_BOUNCING_UP = 6;

    public static final int STATE_SHOWING_HEADER_NOTIFICATION = 7;

    public static final int STATE_SHOWING_FOOTER_NOTIFICATION = 8;

    // Indicator location setting, default is INDICATOR_LOCATION_OUTSIDE
    public static final int INDICATOR_LOCATION_OUTSIDE = 0x100;
    public static final int INDICATOR_LOCATION_BEHIND = 0x101;
    public static final int INDICATOR_LOCATION_FRONT = 0x102;

    // Support child view count nested in this, NOW only support one child.
    private static final int SUPPORT_CHILD_COUNT = 1;

    // Scroll resistance, if it equal 0f will scroll no any friction,
    // if it equal 1f will can not scroll.
    private float mIndicatorScrollResistance = 0.4f;

    // The last action down position while intercepted touch event.
    private int mLastActionDownX, mLastActionDownY;

    /**
     * It is used for check content view whether can be refresh/loading or other action.
     * The default checker is only check whether view has scrolled to top or bottom.
     * <p>
     *
     * @see DefaultViewScrollChecker#canDoRefresh(ChopinLayout, View),
     * @see DefaultViewScrollChecker#canDoLoading(ChopinLayout, View)
     */
    private ViewScrollChecker mViewScrollChecker = new DefaultViewScrollChecker();

    // The provider for provide header indicator and some interfaces with interaction,
    // eg. header indicator animation.
    private Indicator mRefreshHeaderIndicatorProvider;

    // The provider for provide footer indicator and some interfaces with interaction,
    // eg. footer indicator animation.
    private Indicator mLoadingFooterIndicatorProvider;

    // The refresh listener
    private OnRefreshListener mOnRefreshListener;

    // The load more listener
    private OnLoadMoreListener mOnLoadMoreListener;

    // The header indicator.
    private IndicatorViewWrapper mHeaderIndicatorView;

    // The footer indicator.
    private IndicatorViewWrapper mFooterIndicatorView;

    // The content view of user set.
    private ContentViewWrapper mContentViewWrapper;

    private View mHeaderNotificationView;

    private View mFooterNotificationView;

    private long mHeaderNotificationViewStayMills = DEFAULT_HEADER_NOTIFICATION_VIEW_STAY_DURATION;

    private long mFooterNotificationViewStayMills = DEFAULT_FOOTER_NOTIFICATION_VIEW_STAY_DURATION;

    /**
     * Set visible threshold count while {@link #autoTriggerLoadMore} is true,
     */
    private int mLoadMoreRemainShowItemCount = 2;

    /**
     * If set true, it will auto trigger load more while visible
     * item < {@link #mLoadMoreRemainShowItemCount}
     */
    private boolean autoTriggerLoadMore = false;

    // The user can drag content over screen, like iOS TableView default scroll effect.
    private boolean enableOverScroll = true;

    private boolean hasDispatchCancelEvent = false;

    private MotionEvent mLastMoveEvent;

    private int mStartInterceptTouchY, mTranslatedOffsetWhileIntercept;

    private int mHeaderIndicatorLocation = INDICATOR_LOCATION_OUTSIDE;

    private int mFooterIndicatorLocation = INDICATOR_LOCATION_OUTSIDE;

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
     * Setup auto load more if content view is RecyclerView, {@link #setupRecyclerViewAutoLoadMore(RecyclerView)}
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > SUPPORT_CHILD_COUNT) {
            throw new IllegalArgumentException("It can ONLY set ONE child view!");
        } else if (getChildCount() == SUPPORT_CHILD_COUNT) {
            // Setup default background color of content view.
            // It fixed a bug when setting 'behind' indicator location can see behind indicator.
            View contentView = super.getChildAt(0);
            contentView.setBackgroundColor(Color.WHITE);
            mContentViewWrapper = new ContentViewWrapper(contentView);

            // Set up auto load more if content view is RecyclerView.
            if (contentView instanceof RecyclerView) {
                setupRecyclerViewAutoLoadMore((RecyclerView) contentView);
            }
        }
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
        for (int idx = 0; idx < getChildCount(); idx++) {
            measureChild(getChildAt(idx), widthMeasureSpec, heightMeasureSpec);
        }
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onViewMeasured(this, mRefreshHeaderIndicatorProvider.getView());
        }
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onViewMeasured(this, mLoadingFooterIndicatorProvider.getView());
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /**
     * Layout content view in suitable position.
     * Layout refresh header indicator on top of content view and layout loading footer indicator on
     * the bottom of content view in order to hide in the default status.
     */
    @SuppressWarnings("ResourceType")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: ");
        // Layout content view.
        mContentViewWrapper.layout();
        mContentViewWrapper.getView().bringToFront();

        // Layout refresh header indicator view.
        if (null != mHeaderIndicatorView) {
            int top = mHeaderIndicatorLocation == INDICATOR_LOCATION_BEHIND
                    ? 0 : -mHeaderIndicatorView.getHeight();
            int bottom = top + mHeaderIndicatorView.getHeight();
            if (mHeaderIndicatorLocation == INDICATOR_LOCATION_FRONT) {
                mHeaderIndicatorView.getView().bringToFront();
                if (null != mHeaderNotificationView) {
                    mHeaderNotificationView.bringToFront();
                }
            }
            mHeaderIndicatorView.layout(0, top, mHeaderIndicatorView.getWidth(), bottom);
        }

        // Layout loading footer indicator view.
        if (null != mFooterIndicatorView) {
            int top = mFooterIndicatorLocation == INDICATOR_LOCATION_BEHIND
                    ? mContentViewWrapper.getView().getMeasuredHeight()
                    - mFooterIndicatorView.getHeight()
                    : mContentViewWrapper.getView().getMeasuredHeight();
            int bottom = top + mFooterIndicatorView.getHeight();
            if (mFooterIndicatorLocation == INDICATOR_LOCATION_FRONT) {
                mFooterIndicatorView.getView().bringToFront();
                if (null != mFooterNotificationView) {
                    mFooterNotificationView.bringToFront();
                }
            }
            mFooterIndicatorView.layout(0, top, mFooterIndicatorView.getWidth(), bottom);
        }

        // Layout notification view.
        if (null != mHeaderNotificationView) {
            mHeaderNotificationView.layout(0, 0, mHeaderNotificationView.getMeasuredWidth(),
                    mHeaderNotificationView.getMeasuredHeight());
        }
        if (null != mFooterNotificationView) {
            mFooterNotificationView.layout(0,
                    mContentViewWrapper.getView().getMeasuredHeight() - mFooterNotificationView.getMeasuredHeight(),
                    mFooterNotificationView.getMeasuredWidth(),
                    mContentViewWrapper.getView().getMeasuredHeight());
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
                mLastActionDownX = x;
                mLastActionDownY = y;
                hasDispatchCancelEvent = false;

                if (DEBUG) {
                    Log.d(TAG, "event--> dispatchTouchEvent: DOWN, true");
                }

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
                int dx = x - mLastActionDownX;
                int dy = y - mLastActionDownY;

                boolean pullDown = dy > 0;
                boolean pullUp = dy < 0;

                if (mState == STATE_DEFAULT) {
                    if (pullDown && mViewScrollChecker.canDoRefresh(this, mContentViewWrapper.getView())
                            && dy > Math.abs(dx)) {
                        mStartInterceptTouchY = y;
                        setState(STATE_DRAGGING_DOWN);
                        Log.d(TAG, "dispatchTouchEvent: canIntercept pull down");
                        return true;
                    }
                    if (pullUp && mViewScrollChecker.canDoLoading(this, mContentViewWrapper.getView())
                            && -dy > Math.abs(dx)) {
                        mStartInterceptTouchY = y;
                        setState(STATE_DRAGGING_UP);
                        Log.d(TAG, "dispatchTouchEvent: canIntercept pull up");
                        return true;
                    }
                }
                if (mState == STATE_DRAGGING_DOWN || mState == STATE_DRAGGING_UP) {
                    int moveOffsetYAfterIntercepted = y - mStartInterceptTouchY;
                    int actualTranslationOffsetY = (int) (moveOffsetYAfterIntercepted * (1 - mIndicatorScrollResistance));

                    // It should reset intercept event when dragging state has changed.
                    if ((moveOffsetYAfterIntercepted > 0 && mState == STATE_DRAGGING_UP)
                            || (moveOffsetYAfterIntercepted < 0 && mState == STATE_DRAGGING_DOWN)) {
                        resetInterceptEvent(ev);
                        actualTranslationOffsetY = 0;
                    }

                    if (DEBUG) {
                        Log.d(TAG, "Intercepted: \nmoveOffsetYAfterIntercepted--> " + moveOffsetYAfterIntercepted +
                                "\nactualTranslationOffsetY--> " + actualTranslationOffsetY +
                                "\ngetCurrentTranslatedOffsetY--> " + getCurrentTranslatedOffsetY());
                    }
                    translateViewWithTargetOffsetY(actualTranslationOffsetY);

                    // Dispatch cancel event for cancel user click trigger.
                    if (!hasDispatchCancelEvent) {
                        sendCancelEvent();
                        hasDispatchCancelEvent = true;
                    }

                    if (null != mRefreshHeaderIndicatorProvider && actualTranslationOffsetY > 0) {
                        // Scroll distance has over refresh header indicator height.
                        float progress = actualTranslationOffsetY / (float) mHeaderIndicatorView.getHeight();
                        mRefreshHeaderIndicatorProvider.onPositionChange(this, progress, Indicator.STATE.DRAGGING_DOWN, x, y);
                    }

                    if (null != mLoadingFooterIndicatorProvider && actualTranslationOffsetY < 0) {
                        float progress = -actualTranslationOffsetY / (float) mFooterIndicatorView.getHeight();
                        mLoadingFooterIndicatorProvider.onPositionChange(this, progress, Indicator.STATE.DRAGGING_UP, x, y);
                    }

                    return true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int currentTranslatedOffsetY = getCurrentTranslatedOffsetY();

                if (DEBUG) {
                    Log.d(TAG, "currentTranslatedOffsetY: " + currentTranslatedOffsetY);
                }

                if (currentTranslatedOffsetY == 0) {
                    setState(STATE_DEFAULT);
                }
                // Content view has been dragged down.
                if (currentTranslatedOffsetY > 0) {
                    if (null != mRefreshHeaderIndicatorProvider) {
                        // release view to refresh status while is refreshing or scrollY exceeded
                        // refresh header indicator height.
                        if (currentTranslatedOffsetY >= mHeaderIndicatorView.getHeight()) {
                            releaseViewToRefreshingStatus();
                        } else if (mState != STATE_REFRESHING) {
                            // Abort some move events while it not meet refresh or loading demands.
                            abortThisDrag();
                        }
                    } else {
                        // Cancel this scroll "journey" if has some unexpected exceptions.
                        releaseViewToDefaultStatus();
                    }
                }

                // Content view has been dragged up.
                if (currentTranslatedOffsetY < 0) {
                    if (null != mLoadingFooterIndicatorProvider) {
                        if (-currentTranslatedOffsetY >= mFooterIndicatorView.getHeight()) {
                            releaseViewToLoadingStatus();
                        } else if (mState != STATE_LOADING) {
                            abortThisDrag();
                        }
                    } else {
                        releaseViewToDefaultStatus();
                    }
                }
                break;
        }

        boolean dispatch = super.dispatchTouchEvent(ev);
        if (DEBUG) {
            String actionName = ev.getAction() == MotionEvent.ACTION_DOWN ? "DOWN" :
                    ev.getAction() == MotionEvent.ACTION_MOVE ? "MOVE" : "UP";
            Log.d(TAG, "event--> dispatchTouchEvent: " + actionName + ", " + dispatch);
        }
        return dispatch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mState == STATE_REFRESHING || mState == STATE_LOADING) {
            int x = (int) ev.getX(), y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastActionDownX = x;
                    mLastActionDownY = y;

                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = x - mLastActionDownX;
                    int dy = y - mLastActionDownY;

                    boolean pullDown = dy > 0;
                    boolean pullUp = dy < 0;

                    if (pullDown && dy > Math.abs(dx)) {
                        Log.d(TAG, "onInterceptTouchEvent: canIntercept pull down");
                        mStartInterceptTouchY = y;
                        mTranslatedOffsetWhileIntercept = getCurrentTranslatedOffsetY();
                        return true;
                    }
                    if (pullUp && -dy > Math.abs(dx)) {
                        Log.d(TAG, "onInterceptTouchEvent: canIntercept pull up");
                        mStartInterceptTouchY = y;
                        mTranslatedOffsetWhileIntercept = getCurrentTranslatedOffsetY();
                        return true;
                    }
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mState == STATE_REFRESHING || mState == STATE_LOADING) {
            int x = (int) ev.getX(), y = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastActionDownX = x;
                    mLastActionDownY = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveOffsetYAfterIntercepted = y - mStartInterceptTouchY;
                    int actualTranslationOffsetY = mTranslatedOffsetWhileIntercept +
                            (int) (moveOffsetYAfterIntercepted * (1 - mIndicatorScrollResistance));
                    if (DEBUG) {
                        Log.d(TAG, "onTouchEvent: moveOffsetYAfterIntercepted--> " + moveOffsetYAfterIntercepted);

                    }
                    translateViewWithTargetOffsetY(actualTranslationOffsetY);
                    return true;
            }
        }

        return super.onTouchEvent(ev);
    }

    private void translateViewWithTargetOffsetY(int translationOffsetY) {
        if (DEBUG) {
            Log.d(TAG, "translateViewWithTargetOffsetY: " + translationOffsetY);
        }
        // Reset to default.
        if (translationOffsetY == 0) {
            mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
            if (null != mHeaderIndicatorView) {
                mHeaderIndicatorView.translateVerticalWithOffset(translationOffsetY);
            }
            if (null != mFooterIndicatorView) {
                mFooterIndicatorView.translateVerticalWithOffset(translationOffsetY);
            }
        }

        // Pull down.
        if (translationOffsetY > 0) {
            if (null == mHeaderIndicatorView) {
                mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
            } else {
                switch (mHeaderIndicatorLocation) {
                    case INDICATOR_LOCATION_FRONT:
                        mHeaderIndicatorView.translateVerticalWithOffset(translationOffsetY);
                        break;
                    case INDICATOR_LOCATION_BEHIND:
                        mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                        break;
                    case INDICATOR_LOCATION_OUTSIDE:
                        mHeaderIndicatorView.translateVerticalWithOffset(translationOffsetY);
                        mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                        break;
                }
            }

        }
        if (translationOffsetY < 0) {
            if (null == mFooterIndicatorView) {
                mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
            } else {
                switch (mFooterIndicatorLocation) {
                    case INDICATOR_LOCATION_FRONT:
                        mFooterIndicatorView.translateVerticalWithOffset(translationOffsetY);
                        break;
                    case INDICATOR_LOCATION_BEHIND:
                        mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                        break;
                    case INDICATOR_LOCATION_OUTSIDE:
                        mFooterIndicatorView.translateVerticalWithOffset(translationOffsetY);
                        mContentViewWrapper.translateVerticalWithOffset(translationOffsetY);
                        break;
                }
            }
        }
    }

    private int getCurrentTranslatedOffsetY() {
        int currentTranslatedOffsetY = 0;
        if (mContentViewWrapper.getTranslationY() == 0) {
            if (null != mHeaderIndicatorView && mHeaderIndicatorView.getTranslationY() > 0) {
                currentTranslatedOffsetY = mHeaderIndicatorView.getTranslationY();
            }
            if (null != mFooterIndicatorView && mFooterIndicatorView.getTranslationY() < 0) {
                currentTranslatedOffsetY = mFooterIndicatorView.getTranslationY();
            }
        } else {
            currentTranslatedOffsetY = mContentViewWrapper.getTranslationY();
        }
        if (DEBUG) {
            Log.d(TAG, "getCurrentTranslatedOffsetY: " + currentTranslatedOffsetY);
        }
        return currentTranslatedOffsetY;
    }

    /**
     * Refreshing
     */
    private void releaseViewToRefreshingStatus() {
        if (null == mRefreshHeaderIndicatorProvider) {
            return;
        }
        int start = mHeaderIndicatorLocation == INDICATOR_LOCATION_BEHIND
                ? mContentViewWrapper.getTranslationY()
                : mHeaderIndicatorView.getTranslationY();

        int end = mHeaderIndicatorView.getHeight();
        if (start == end) {
            return;
        }
        if (mState != STATE_REFRESHING) {
            setState(STATE_BOUNCING_UP);
        }
        if (mHeaderIndicatorLocation == INDICATOR_LOCATION_BEHIND) {
            mContentViewWrapper.animateTranslationY(start, end, new BaseViewWrapper.AnimateListener() {
                @Override
                public void onAnimate(int value) {
                    float progress = value / (float) mHeaderIndicatorView.getHeight();
                    mRefreshHeaderIndicatorProvider.onPositionChange(ChopinLayout.this, progress, Indicator.STATE
                            .BOUNCING_UP, -1, -1);
                }

                @Override
                public void onFinish() {
                    if (mState != STATE_REFRESHING) {
                        startRefresh();
                    } else {
                        // TODO: 27/07/2017  It may cause wrong state while dragging in refreshing status.
                        setState(STATE_REFRESHING);
                    }
                }
            });
        } else {
            mHeaderIndicatorView.animateTranslationY(mHeaderIndicatorView.getTranslationY(),
                    mHeaderIndicatorView.getHeight(), new BaseViewWrapper.AnimateListener() {
                        @Override
                        public void onAnimate(int value) {
                            float progress = value / (float) mHeaderIndicatorView.getHeight();
                            mRefreshHeaderIndicatorProvider.onPositionChange(ChopinLayout.this, progress, Indicator.STATE
                                    .BOUNCING_UP, -1, -1);
                            if (mHeaderIndicatorLocation != INDICATOR_LOCATION_FRONT) {
                                mContentViewWrapper.translateVerticalWithOffset(value);
                            }
                        }

                        @Override
                        public void onFinish() {
                            if (mState != STATE_REFRESHING) {
                                startRefresh();
                            } else {
                                // TODO: 27/07/2017  It may cause wrong state while dragging in refreshing status.
                                setState(STATE_REFRESHING);
                            }
                        }
                    });
        }
    }

    /**
     * Loading
     */
    private void releaseViewToLoadingStatus() {
        if (null == mLoadingFooterIndicatorProvider) {
            return;
        }

        int start = mFooterIndicatorLocation == INDICATOR_LOCATION_BEHIND
                ? mContentViewWrapper.getTranslationY()
                : mFooterIndicatorView.getTranslationY();

        int end = mFooterIndicatorView.getHeight();
        if (start == end) {
            return;
        }
        if (mState != STATE_LOADING) {
            setState(STATE_BOUNCING_DOWN);
        }
        if (mFooterIndicatorLocation == INDICATOR_LOCATION_BEHIND) {
            mContentViewWrapper.animateTranslationY(mContentViewWrapper.getTranslationY(),
                    -mFooterIndicatorView.getHeight(), new BaseViewWrapper.AnimateListener() {
                        @Override
                        public void onAnimate(int value) {
                            float progress = value / (float) mFooterIndicatorView.getHeight();
                            mLoadingFooterIndicatorProvider.onPositionChange(ChopinLayout.this, progress,
                                    Indicator.STATE.BOUNCING_DOWN, -1, -1);
                        }

                        @Override
                        public void onFinish() {
                            if (mState != STATE_LOADING) {
                                startLoading();
                            } else {
                                // TODO: 27/07/2017  It may cause wrong state while dragging in loading status.
                                setState(STATE_LOADING);
                            }
                        }
                    });
        } else {
            mFooterIndicatorView.animateTranslationY(mFooterIndicatorView.getTranslationY(),
                    -mFooterIndicatorView.getHeight(), new BaseViewWrapper.AnimateListener() {
                        @Override
                        public void onAnimate(int value) {
                            float progress = value / (float) mFooterIndicatorView.getHeight();
                            mLoadingFooterIndicatorProvider.onPositionChange(ChopinLayout.this, progress,
                                    Indicator.STATE.BOUNCING_DOWN, -1, -1);
                            if (mFooterIndicatorLocation != INDICATOR_LOCATION_FRONT) {
                                mContentViewWrapper.translateVerticalWithOffset(value);
                            }
                        }

                        @Override
                        public void onFinish() {
                            if (mState != STATE_LOADING) {
                                startLoading();
                            } else {
                                // TODO: 27/07/2017  It may cause wrong state while dragging in loading status.
                                setState(STATE_LOADING);
                            }
                        }
                    });
        }
    }

    private void releaseViewToDefaultStatus() {
        releaseViewToDefaultStatus(false);
    }

    /**
     * Default
     */
    private void releaseViewToDefaultStatus(final boolean showNotificationView) {
        int currentTranslatedOffsetY = getCurrentTranslatedOffsetY();
        // Process header indicator.
        if (currentTranslatedOffsetY > 0) {
            // ContentView will rebound when it have no HeaderIndicatorView
            // or mHeaderIndicatorLocation != INDICATOR_LOCATION_FRONT
            if (null == mHeaderIndicatorView || mHeaderIndicatorLocation != INDICATOR_LOCATION_FRONT) {
                int start = mContentViewWrapper.getTranslationY();
                int end = showNotificationView ? mHeaderNotificationView.getHeight() : 0;
                setState(start > end ? STATE_BOUNCING_UP : STATE_BOUNCING_DOWN);
                mContentViewWrapper.animateTranslationY(start, end, new BaseViewWrapper.AnimateListener() {
                    @Override
                    public void onAnimate(int value) {
                        if (null != mHeaderIndicatorView) {
                            if (mHeaderIndicatorLocation != INDICATOR_LOCATION_BEHIND) {
                                mHeaderIndicatorView.translateVerticalWithOffset(value);
                            }
                            if (null != mRefreshHeaderIndicatorProvider) {
                                float progress = Math.abs(value) / (float) mHeaderIndicatorView.getHeight();
                                mRefreshHeaderIndicatorProvider.onPositionChange(ChopinLayout.this, progress,
                                        Indicator.STATE.BOUNCING_UP, -1, -1);
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (showNotificationView) {
                            setState(STATE_SHOWING_HEADER_NOTIFICATION);
                            mHeaderNotificationView.setVisibility(VISIBLE);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    releaseViewToDefaultStatus(false);
                                }
                            }, mHeaderNotificationViewStayMills);

                        } else {
                            // Bouncing end.
                            setState(STATE_DEFAULT);
                            if (null != mHeaderNotificationView) {
                                mHeaderNotificationView.setVisibility(GONE);
                            }
                        }
                    }
                });
            } else {
                int start = mHeaderIndicatorView.getTranslationY();
                int end = showNotificationView ? mHeaderNotificationView.getHeight() : 0;
                setState(start > end ? STATE_BOUNCING_UP : STATE_BOUNCING_DOWN);
                mHeaderIndicatorView.animateTranslationY(start, end,
                        new BaseViewWrapper.AnimateListener() {
                            @Override
                            public void onAnimate(int value) {
                                if (mHeaderIndicatorLocation != INDICATOR_LOCATION_BEHIND) {
                                    mHeaderIndicatorView.translateVerticalWithOffset(value);
                                }
                                if (null != mRefreshHeaderIndicatorProvider) {
                                    float progress = Math.abs(value) / (float) mHeaderIndicatorView.getHeight();
                                    mRefreshHeaderIndicatorProvider.onPositionChange(ChopinLayout.this, progress,
                                            Indicator.STATE.BOUNCING_UP, -1, -1);
                                }
                            }

                            @Override
                            public void onFinish() {
                                if (showNotificationView) {
                                    setState(STATE_SHOWING_HEADER_NOTIFICATION);
                                    mHeaderNotificationView.setVisibility(VISIBLE);
                                    postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            releaseViewToDefaultStatus(false);
                                        }
                                    }, mHeaderNotificationViewStayMills);

                                } else {
                                    // Bouncing end.
                                    setState(STATE_DEFAULT);
                                    if (null != mHeaderNotificationView) {
                                        mHeaderNotificationView.setVisibility(GONE);
                                    }
                                }
                            }
                        });
            }

        }

        // Process footer indicator.
        if (currentTranslatedOffsetY < 0) {
            // ContentView will rebound when it have no FooterIndicatorView
            // or mHeaderIndicatorLocation != INDICATOR_LOCATION_FRONT
            if (null == mFooterIndicatorView || mFooterIndicatorLocation != INDICATOR_LOCATION_FRONT) {
                int start = mContentViewWrapper.getTranslationY();
                int end = showNotificationView ? -mFooterNotificationView.getHeight() : 0;
                setState(start > end ? STATE_BOUNCING_UP : STATE_BOUNCING_DOWN);
                mContentViewWrapper.animateTranslationY(start, end,
                        new BaseViewWrapper.AnimateListener() {
                            @Override
                            public void onAnimate(int value) {
                                if (null != mFooterIndicatorView) {
                                    if (mFooterIndicatorLocation != INDICATOR_LOCATION_BEHIND) {
                                        mFooterIndicatorView.translateVerticalWithOffset(value);
                                    }
                                    if (null != mLoadingFooterIndicatorProvider) {
                                        float progress = Math.abs(value) / (float) mFooterIndicatorView.getHeight();
                                        mLoadingFooterIndicatorProvider.onPositionChange(ChopinLayout.this, progress,
                                                Indicator.STATE.BOUNCING_DOWN, -1, -1);
                                    }
                                }
                            }

                            @Override
                            public void onFinish() {
                                if (showNotificationView) {
                                    setState(STATE_SHOWING_FOOTER_NOTIFICATION);
                                    mFooterNotificationView.setVisibility(VISIBLE);
                                    postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            releaseViewToDefaultStatus(false);
                                        }
                                    }, mFooterNotificationViewStayMills);

                                } else {
                                    // Bouncing end.
                                    setState(STATE_DEFAULT);
                                    if (null != mFooterNotificationView) {
                                        mFooterNotificationView.setVisibility(GONE);
                                    }
                                }
                            }
                        });
            } else {
                int start = mFooterIndicatorView.getTranslationY();
                int end = showNotificationView ? -mFooterNotificationView.getHeight() : 0;
                setState(start > end ? STATE_BOUNCING_UP : STATE_BOUNCING_DOWN);
                mFooterIndicatorView.animateTranslationY(start, end,
                        new BaseViewWrapper.AnimateListener() {
                            @Override
                            public void onAnimate(int value) {
                                if (mFooterIndicatorLocation != INDICATOR_LOCATION_BEHIND) {
                                    mFooterIndicatorView.translateVerticalWithOffset(value);
                                }
                                if (null != mLoadingFooterIndicatorProvider) {
                                    float progress = Math.abs(value) / (float) mFooterIndicatorView.getHeight();
                                    mLoadingFooterIndicatorProvider.onPositionChange(ChopinLayout.this, progress, Indicator
                                            .STATE.BOUNCING_DOWN, -1, -1);
                                }
                            }

                            @Override
                            public void onFinish() {
                                if (showNotificationView) {
                                    setState(STATE_SHOWING_FOOTER_NOTIFICATION);
                                    mFooterNotificationView.setVisibility(VISIBLE);
                                    postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            releaseViewToDefaultStatus(false);
                                        }
                                    }, mFooterNotificationViewStayMills);

                                } else {
                                    // Bouncing end.
                                    setState(STATE_DEFAULT);
                                    if (null != mFooterNotificationView) {
                                        mFooterNotificationView.setVisibility(GONE);
                                    }
                                }
                            }
                        });
            }
        }
    }

    /**
     * Abort this action while dragging content view and not reach the demands.
     */
    private void abortThisDrag() {
        releaseViewToDefaultStatus();
        if (getCurrentTranslatedOffsetY() > 0 && null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onCancel(this);
        }
        if (getCurrentTranslatedOffsetY() < 0 && null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onCancel(this);
        }
    }

    private void resetInterceptEvent(MotionEvent event) {
        setState(STATE_DEFAULT);
        int x = (int) event.getX(), y = (int) event.getY();
        long eventTime = System.currentTimeMillis();
        MotionEvent mockDownMotionEvent = MotionEvent.obtain(eventTime,
                eventTime, MotionEvent.ACTION_DOWN, x, y, 0);
        super.dispatchTouchEvent(mockDownMotionEvent);
        mLastActionDownX = x;
        mLastActionDownY = y;
    }

    private void sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent event = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout()
                , MotionEvent.ACTION_CANCEL, mLastActionDownX, mLastActionDownY, last.getMetaState());
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
        Log.d(TAG, "startRefresh: ");
        setState(STATE_REFRESHING);
        if (null != mOnRefreshListener) {
            mOnRefreshListener.onRefresh();
        }
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onStart(this);
        }
    }

    private void startLoading() {
        setState(STATE_LOADING);
        if (null != mOnLoadMoreListener) {
            mOnLoadMoreListener.onLoadMore();
        }
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onStart(this);
        }
    }

    public void refreshComplete() {
        refreshComplete(DEFAULT_REFRESH_COMPLETE_COLLAPSE_DELAY);
    }

    public void refreshComplete(long collapseDelay) {
        if (null != mRefreshHeaderIndicatorProvider) {
            mRefreshHeaderIndicatorProvider.onComplete(this);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                releaseViewToDefaultStatus(mHeaderNotificationView != null);
            }
        }, collapseDelay);
    }

    public void loadMoreComplete() {
        loadMoreComplete(DEFAULT_LOAD_MORE_COMPLETE_COLLAPSE_DELAY);
    }

    public void loadMoreComplete(long collapseDelay) {
        if (null != mLoadingFooterIndicatorProvider) {
            mLoadingFooterIndicatorProvider.onComplete(this);
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                releaseViewToDefaultStatus(mFooterNotificationView != null);
            }
        }, collapseDelay);
    }

    private void initialize() {
    }

    /**
     * Set Header indicator but it can not do refresh and it is hide in default status, you should
     * scroll over screen and will find it.
     * If you want enable refresh you can use {@link #setRefreshHeaderIndicator(Indicator)}
     * to setup refresh effect and tie something like refresh header scroll progress.
     *
     * @param headerIndicatorView
     */
    public void setHeaderIndicatorView(@NonNull View headerIndicatorView) {
        if (null != mHeaderIndicatorView) {
            // Remove last view.
            removeView(mHeaderIndicatorView.getView());
        }
        headerIndicatorView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mHeaderIndicatorView = new IndicatorViewWrapper(headerIndicatorView);
        addView(headerIndicatorView);
    }

    public void setFooterIndicatorView(@NonNull View footerIndicatorView) {
        if (null != mFooterIndicatorView) {
            removeView(mFooterIndicatorView.getView());
        }
        footerIndicatorView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mFooterIndicatorView = new IndicatorViewWrapper(footerIndicatorView);
        addView(footerIndicatorView);
    }

    public void setRefreshHeaderIndicator(@NonNull Indicator refreshHeaderIndicatorProvider) {
        mRefreshHeaderIndicatorProvider = refreshHeaderIndicatorProvider;
        View contentView = refreshHeaderIndicatorProvider.getView();
        setHeaderIndicatorView(contentView);

    }

    public void setLoadingFooterIndicator(@NonNull Indicator loadingFooterIndicatorProvider) {
        mLoadingFooterIndicatorProvider = loadingFooterIndicatorProvider;
        View contentView = loadingFooterIndicatorProvider.getView();
        setFooterIndicatorView(contentView);
        // You can only choose a load more style.
        autoTriggerLoadMore = false;
    }

    public void clearHeaderIndicator() {
        mRefreshHeaderIndicatorProvider = null;
        mHeaderIndicatorView = null;
    }


    public void clearFooterIndicator() {
        mLoadingFooterIndicatorProvider = null;
        mFooterIndicatorView = null;
    }

    public void setHeaderNotificationView(@NonNull View notificationView) {
        mHeaderNotificationView = notificationView;
        addView(notificationView);
        mHeaderNotificationView.setVisibility(GONE);
    }

    public void setFooterNotificationView(@NonNull View notificationView) {
        mFooterNotificationView = notificationView;
        addView(notificationView);
        mFooterNotificationView.setVisibility(GONE);
    }

    public void setHeaderNotificationViewStayMills(long headerNotificationViewStayMills) {
        this.mHeaderNotificationViewStayMills = headerNotificationViewStayMills;
    }

    public void setFooterNotificationViewStayMills(long footerNotificationViewStayMills) {
        this.mFooterNotificationViewStayMills = footerNotificationViewStayMills;
    }

    //    /**
//     * Configure header indicator background.
//     * NOTE: it is ONLY shown in indicator location {@link #INDICATOR_LOCATION_BEHIND}
//     * and {@link #INDICATOR_LOCATION_OUTSIDE}
//     *
//     * @param headerIndicatorBackground The view of background.
//     */
//    public void setHeaderIndicatorBackground(@Nullable View headerIndicatorBackground) {
//        if (null != mHeaderIndicatorBackground) {
//            removeView(mHeaderIndicatorBackground.getView());
//        }
//        if (null != headerIndicatorBackground) {
//            mHeaderIndicatorBackground = new IndicatorViewWrapper(headerIndicatorBackground);
//            addView(headerIndicatorBackground);
//        }
//    }
//
//    public void setFooterIndicatorBackground(@Nullable View footerIndicatorBackground) {
//        if (null != mFooterIndicatorBackground) {
//            removeView(mFooterIndicatorBackground.getView());
//        }
//        if (null != footerIndicatorBackground) {
//            mFooterIndicatorBackground = new IndicatorViewWrapper(footerIndicatorBackground);
//            addView(footerIndicatorBackground);
//        }
//    }

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
                            && mState != STATE_LOADING) {
                        mState = STATE_LOADING;
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
     * {@link #setupRecyclerViewAutoLoadMore(RecyclerView)}
     * <p>
     * NOTE: You can ONLY choose one load more style from {@link #setLoadingFooterIndicator(Indicator)}
     * and this.
     * Please remove Load Footer View while you set autoTriggerLoadMore is true.
     *
     * @param autoTriggerLoadMore If true will auto trigger load more while
     *                            remain to show item less than {@link #mLoadMoreRemainShowItemCount}
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

    public void setHeaderIndicatorLocation(int headerIndicatorLocation) {
        mHeaderIndicatorLocation = headerIndicatorLocation;
        requestLayout();
    }

    public void setFooterIndicatorLocation(int footerIndicatorLocation) {
        mFooterIndicatorLocation = footerIndicatorLocation;
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
}
