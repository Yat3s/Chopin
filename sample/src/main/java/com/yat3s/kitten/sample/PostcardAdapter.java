package com.yat3s.kitten.sample;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.yat3s.kitten.adapter.KittenViewHolder;
import com.yat3s.kitten.adapter.SimpleKittenAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 06/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class PostcardAdapter extends SimpleKittenAdapter<PostcardAdapter.Postcard> {
    private static final int POSITION_RECYCLER_VIEW = 1;

    private StampAdapter mNestedStampAdapter;

    public PostcardAdapter(Context context, List<Postcard> dataSource) {
        super(context, dataSource);
    }

    @Override
    protected void bindDataToItemView(KittenViewHolder holder, Postcard postcard, int position) {
        if (position == POSITION_RECYCLER_VIEW) {
            if (null == mNestedStampAdapter) {
                RecyclerView recyclerView = holder.getView(R.id.recycler_view);
                List<Integer> stampResIds = new ArrayList<>();
                stampResIds.add(R.mipmap.stamp_2);
                stampResIds.add(R.mipmap.stamp_1);
                stampResIds.add(R.mipmap.stamp_3);
                stampResIds.add(R.mipmap.stamp_4);
                mNestedStampAdapter = new StampAdapter(mContext, stampResIds);
                recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(new StampAdapter(mContext, stampResIds));
            }
        } else {
            ImageView img = holder.getView(R.id.card_iv);
            img.setImageResource(postcard.imageResId);
        }
    }

    @Override
    protected int getItemViewLayoutId(int position, Postcard postcard) {
        if (position == POSITION_RECYCLER_VIEW) {
            return R.layout.item_recycler_view;
        }
        return R.layout.item_postcard;
    }

    public static class Postcard {
        public int imageResId;

        public Postcard(int imageResId) {
            this.imageResId = imageResId;
        }
    }

    public static class StampAdapter extends SimpleKittenAdapter<Integer> {

        public StampAdapter(Context context, List<Integer> dataSource) {
            super(context, dataSource);
        }

        @Override
        protected void bindDataToItemView(KittenViewHolder holder, Integer resId, int position) {
            ((ImageView) holder.getView(R.id.stamp_iv)).setImageResource(resId);
        }

        @Override
        protected int getItemViewLayoutId(int position, Integer resId) {
            return R.layout.item_nested_stamp;
        }
    }
}



