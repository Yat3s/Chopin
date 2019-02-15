# Chopin
An Android Refresh layout including some powerful features.  


![](https://github.com/Yat3s/Chopin/blob/dev/screenshot/device.png)


#### RecyclerView
![](https://github.com/Yat3s/Chopin/blob/dev/screenshot/recyclerview.gif)

#### ViewPager
![](https://github.com/Yat3s/Chopin/blob/dev/screenshot/viewpager.gif)

#### CoordinatorLayout
![](https://github.com/Yat3s/Chopin/blob/dev/screenshot/coordinatorlayout.gif)

#### Any View & Advance setting
![](https://github.com/Yat3s/Chopin/blob/dev/screenshot/custom.gif)



## Gradle
`implementation 'com.yat3s.android:chopin:0.6.2'`

## How to use
- XML 
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.yat3s.chopin.ChopinLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/chopin_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!--Any view you want-->
    <AnyView
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</com.yat3s.chopin.ChopinLayout>
```

- Java or Kotlin
```java
mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BEHIND);
mChopinLayout.setRefreshHeaderIndicator(new LottieIndicator(this, "victory.json", 0.1f));
mChopinLayout.setOnRefreshListener(new ChopinLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
            mChopinLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mChopinLayout.refreshComplete();
                }
             }, 2000);
        }
    });
```

## Customize & Other advance settings
- NotificationView
```java
TextView headerNotificationView = new TextView(this);
mChopinLayout.setHeaderNotificationView(headerNotificationView);

// Also you can add foot notification view
TextView footerNotificationView = new TextView(this);
mChopinLayout.setFooterNotificationView(footerNotificationView);
```

- IndicatorLacation
```java
// ChopinLayout.INDICATOR_LOCATION_OUTSIDE
// ChopinLayout.INDICATOR_LOCATION_FRONT
// ChopinLayout.INDICATOR_LOCATION_BEHIND
mChopinLayout.setHeaderIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_OUTSIDE); 
mChopinLayout.setFooterIndicatorLocation(ChopinLayout.INDICATOR_LOCATION_BEHIND); 

```

- IndicatorScrollResistance
```java
// 0.0f - 1.0f
mChopinLayout.setIndicatorScrollResistance(0.5f);
```

- OverScroll
```java
mChopinLayout.setEnableOverScroll(true);
```

- ViewScrollChecker
```java
mChopinLayout.setViewScrollChecker(viewScrollChecker);

// You can view this interface
public interface ViewScrollChecker {

    /**
     * Check content view whether can do refresh,
     * so you can do some edition to control view refresh.
     *
     * @param chopinLayout
     * @param contentView  The View nested in {@link ChopinLayout}
     * @return
     */
    boolean canDoRefresh(ChopinLayout chopinLayout, View contentView);

    /**
     * Check content view whether can do loading,
     * so you can do some edition to control view loading.
     *
     * @param chopinLayout
     * @param contentView
     * @return
     */
    boolean canDoLoading(ChopinLayout chopinLayout, View contentView);
}
```

- ScrollState
```java

mChopinLayout.setOnStateChangeListener(new ChopinLayout.OnStateChangeListener() {
            @Override
            public void onStateChanged(ChopinLayout layout, int newState) {
                /*
                    public static final int STATE_DEFAULT = 0;
                
                    public static final int STATE_DRAGGING_DOWN = 1;
                
                    public static final int STATE_DRAGGING_UP = 2;
                
                    public static final int STATE_REFRESHING = 3;
                
                    public static final int STATE_LOADING = 4;
                
                    public static final int STATE_BOUNCING_DOWN = 5;
                
                    public static final int STATE_BOUNCING_UP = 6;
                
                    public static final int STATE_SHOWING_HEADER_NOTIFICATION = 7;
                
                    public static final int STATE_SHOWING_FOOTER_NOTIFICATION = 8;
                 */
            }
```

