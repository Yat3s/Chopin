package com.yat3s.chopin.sample;

import android.app.Application;
import android.graphics.Typeface;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class App extends Application {
    private static Typeface sTypeface;

    @Override
    public void onCreate() {
        super.onCreate();
        sTypeface = Typeface.createFromAsset(getAssets(), "fonts/chopin.otf");
    }

    public static Typeface getTitleTypeface() {
        return sTypeface;
    }
}
