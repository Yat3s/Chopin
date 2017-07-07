package com.yat3s.chopin;

import android.view.View;

/**
 * Created by Yat3s on 29/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ViewScrollHelper {

    private static final int VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_UP = -1;
    private static final int VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_DOWN = 1;

    public static boolean viewHasScrolledToTop(View view) {
        return !view.canScrollVertically(VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_UP);
    }

    public static boolean viewHasScrolledToBottom(View view) {
        return !view.canScrollVertically(VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_DOWN);
    }
}
