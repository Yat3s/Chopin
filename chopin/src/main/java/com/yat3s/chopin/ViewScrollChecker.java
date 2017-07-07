package com.yat3s.chopin;

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
     * @param chopinLayout
     * @param contentView  The View nested in {@link ChopinLayout}
     * @return
     */
    boolean canDoRefresh(ChopinLayout chopinLayout, View contentView);

    /**
     * Check content view whether can do loading,
     * so you can do some edition to control view loading.
     *
     * @param chopinLayout
     * @param contentView
     * @return
     */
    boolean canDoLoading(ChopinLayout chopinLayout, View contentView);
}
