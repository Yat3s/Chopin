package com.yat3s.kitten.sample;

import android.content.Context;
import android.widget.ImageView;

import com.yat3s.kitten.adapter.KittenViewHolder;
import com.yat3s.kitten.adapter.SimpleKittenAdapter;

import java.util.List;

/**
 * Created by Yat3s on 06/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class PostcardAdapter extends SimpleKittenAdapter<PostcardAdapter.Postcard> {

    public PostcardAdapter(Context context, List<Postcard> dataSource) {
        super(context, dataSource);
    }

    @Override
    protected void bindDataToItemView(KittenViewHolder holder, Postcard postcard, int position) {
        ImageView img = holder.getView(R.id.card_iv);
        img.setImageResource(postcard.imageResId);
    }

    @Override
    protected int getItemViewLayoutId(int position, Postcard postcard) {
        return R.layout.item_postcard;
    }

    public static class Postcard {
        public int imageResId;

        public Postcard(int imageResId) {
            this.imageResId = imageResId;
        }
    }
}



