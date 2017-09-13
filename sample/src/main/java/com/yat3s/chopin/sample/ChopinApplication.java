package com.yat3s.chopin.sample;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ChopinApplication extends Application {
    private static Typeface sTypeface;
    private static ChopinApplication sChopinApplication;
    @Override
    public void onCreate() {
        super.onCreate();
        sTypeface = Typeface.createFromAsset(getAssets(), "fonts/chopin.otf");
        sChopinApplication = this;
    }

    public static Typeface getTitleTypeface() {
        return sTypeface;
    }

    public static Context getContext() {
        return sChopinApplication;
    }
}
