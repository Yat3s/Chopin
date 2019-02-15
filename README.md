# Chopin
An Android Refresh layout including some powerful features.

![](https://github.com/Yat3s/Chopin/blob/dev/screenshot/device.png = 360x480)


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

