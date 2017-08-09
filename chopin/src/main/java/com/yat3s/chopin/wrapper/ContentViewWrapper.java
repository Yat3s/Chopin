package com.yat3s.chopin.wrapper;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Created by Yat3s on 17/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ContentViewWrapper extends BaseViewWrapper {
    private static final String TAG = "ContentViewWrapper";

    public void layout() {
        layout(0, 0, getWidth(), getHeight());
    }

//    @Override
//    public View getView() {
//        View view = super.getView();
//        ViewParent viewParent = view.getParent();
//        ((ViewGroup)viewParent).removeView(view);
//        FrameLayout frameLayout = new FrameLayout(view.getContext());
//        frameLayout.setBackgroundColor(Color.WHITE);
//        frameLayout.addView(view);
//        ((ViewGroup) viewParent).addView(frameLayout);
//        return view;
//    }

    public ContentViewWrapper(View contentView) {
        super(contentView);
    }
}
