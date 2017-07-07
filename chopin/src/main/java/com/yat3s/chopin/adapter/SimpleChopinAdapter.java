package com.yat3s.chopin.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by Yat3s on 04/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public abstract class SimpleChopinAdapter<T> extends ChopinAdapter<T, ChopinViewHolder> {

    public SimpleChopinAdapter(Context context) {
        super(context);
    }

    public SimpleChopinAdapter(Context context, List<T> dataSource) {
        super(context, dataSource);
    }
}
