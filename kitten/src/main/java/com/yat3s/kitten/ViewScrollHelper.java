package com.yat3s.kitten;

import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

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
        } else if (view instanceof ScrollView || view instanceof NestedScrollView) {
            return view.getScrollY() <= 0;
        }
        return view.canScrollVertically(VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_UP);
    }

    public static boolean viewScrolledToBottom(View view) {
        if (view instanceof RecyclerView) {
            return ((RecyclerView) view).computeVerticalScrollExtent()
                    + ((RecyclerView) view).computeVerticalScrollOffset()
                    >= ((RecyclerView) view).computeVerticalScrollRange();
        } else if ((view instanceof ScrollView || view instanceof NestedScrollView) && ((ViewGroup) view).getChildCount() > 0) {
            return ((ViewGroup) view).getChildAt(0).getMeasuredHeight() <= view.getScrollY() + view.getHeight();
        }
        return view.canScrollVertically(VIEW_SCROLL_VERTICALLY_DIRECTION_SCROLL_DOWN);
    }
}
