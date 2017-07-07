package com.yat3s.chopin.sample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yat3s.chopin.sample.App;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class TitleTextView extends TextView {

    public TitleTextView(Context context) {
        this(context, null);
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        if (!isInEditMode()) {
            setTypeface(App.getTitleTypeface());
        }
    }
}
