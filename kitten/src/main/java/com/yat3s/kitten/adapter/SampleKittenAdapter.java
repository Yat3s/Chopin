package com.yat3s.kitten.adapter;

import android.content.Context;

import java.util.List;

/**
 * Created by Yat3s on 04/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public abstract class SampleKittenAdapter<T> extends KittenAdapter<T, KittenViewHolder> {

    public SampleKittenAdapter(Context context) {
        super(context);
    }

    public SampleKittenAdapter(Context context, List<T> dataSource) {
        super(context, dataSource);
    }
}
