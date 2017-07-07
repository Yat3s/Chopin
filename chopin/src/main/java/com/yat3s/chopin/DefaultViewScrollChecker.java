package com.yat3s.chopin;

import android.view.View;

/**
 * Created by Yat3s on 29/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class DefaultViewScrollChecker implements ViewScrollChecker {

    /**
     * Default can be refresh while content view has scrolled to top.
     *
     * @param chopinLayout
     * @param contentView
     * @return
     */
    @Override
    public boolean canDoRefresh(ChopinLayout chopinLayout, View contentView) {
        return ViewScrollHelper.viewHasScrolledToTop(contentView);
    }

    /**
     * Default can be loading while content view has scrolled to bottom.
     *
     * @param chopinLayout
     * @param contentView
     * @return
     */
    @Override
    public boolean canDoLoading(ChopinLayout chopinLayout, View contentView) {
        return ViewScrollHelper.viewHasScrolledToBottom(contentView);
    }
}
