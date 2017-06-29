package com.yat3s.kitten;

import android.view.View;

/**
 * Created by Yat3s on 29/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public interface ViewScrollChecker {

    /**
     * Check content view whether can refresh, and it can be
     * @param kittenLayout
     * @param contentView
     * @return
     */
    boolean canBeRefresh(KittenLayout kittenLayout, View contentView);

    boolean canBeLoading(KittenLayout kittenLayout, View contentView);
}
