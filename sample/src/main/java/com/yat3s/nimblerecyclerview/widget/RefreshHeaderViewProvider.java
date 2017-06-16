package com.yat3s.nimblerecyclerview.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.yat3s.kitten.KittenView;
import com.yat3s.nimblerecyclerview.R;

import java.util.Locale;

/**
 * Package: com.yat3s.nimblerecyclerview.widget
 * User: Zhibin Ye
 * Email: yezhibin3@jd.com
 * Date: 16/06/2017
 * Time: 10:46 PM
 */
public class RefreshHeaderViewProvider implements KittenView.RefreshHeaderViewProvider {

    View headerView;

    public RefreshHeaderViewProvider(Context context) {
        headerView = LayoutInflater.from(context).inflate(R.layout.layout_refresh_header, null, false);
    }

    @Override
    public View provideContentView() {
        return headerView;
    }

    @Override
    public void onStartRefresh() {
        headerView.animate().setDuration(2000).translationX(500).start();
    }

    @Override
    public void onRefreshComplete() {
        headerView.animate().translationX(0).start();
    }

    @Override
    public void onRefreshHeaderViewScrollChange(int progress) {
        ((TextView) headerView).setText(String.format(Locale.getDefault(), "Refresh%d", progress));
    }
}
