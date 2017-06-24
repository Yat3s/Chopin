package com.yat3s.kitten;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Yat3s on 03/23/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class ViewScrollHelper {

    private static final int VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_UP = -1;
    private static final int VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_DOWN = 1;

    public static boolean viewScrolledToTop(View view) {
        if (view instanceof RecyclerView) {
            return ((RecyclerView) view).computeVerticalScrollOffset() <= 0;
        }
        return view.canScrollVertically(VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_UP);
    }

    public static boolean viewScrolledToBottom(View view) {
        if (view instanceof RecyclerView) {
            return ((RecyclerView) view).computeVerticalScrollExtent()
                    + ((RecyclerView) view).computeVerticalScrollOffset()
                    >= ((RecyclerView) view).computeVerticalScrollRange();
        }
        return view.canScrollVertically(VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_DOWN);
    }
}
