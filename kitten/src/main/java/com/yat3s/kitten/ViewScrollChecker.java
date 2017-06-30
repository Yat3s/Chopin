package com.yat3s.kitten;

import android.view.View;

/**
 * Created by Yat3s on 29/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public interface ViewScrollChecker {

    /**
     * Check content view whether can do refresh,
     * so you can do some edition to control view refresh.
     *
     * @param kittenLayout
     * @param contentView  The View nested in {@link KittenLayout}
     * @return
     */
    boolean canBeRefresh(KittenLayout kittenLayout, View contentView);

    /**
     * Check content view whether can do loading,
     * so you can do some edition to control view loading.
     *
     * @param kittenLayout
     * @param contentView
     * @return
     */
    boolean canBeLoading(KittenLayout kittenLayout, View contentView);
}
