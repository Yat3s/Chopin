package com.yat3s.chopin.sample;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.yat3s.chopin.adapter.ChopinViewHolder;
import com.yat3s.chopin.adapter.SimpleChopinAdapter;
import com.yat3s.library.adapter.BaseAdapter;
import com.yat3s.library.adapter.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */

public class MusicAdapter extends BaseAdapter<MusicAdapter.Music> {

    private static final String TAG = "MusicAdapter";

    private static final int POSITION_RECYCLER_VIEW = 0;

    private StampAdapter mNestedStampAdapter;

    public MusicAdapter(Context context, List<Music> data) {
        super(context, data);
    }

    @Override
    protected void bindDataToItemView(BaseViewHolder holder, Music item, final int position) {
        if (position == POSITION_RECYCLER_VIEW) {
            if (null == mNestedStampAdapter) {
                RecyclerView recyclerView = holder.getView(R.id.recycler_view);
                recyclerView.setFocusable(false);
                List<Integer> stampResIds = new ArrayList<>();
                stampResIds.add(R.mipmap.img_cd_4);
                stampResIds.add(R.mipmap.img_cd_1);
                stampResIds.add(R.mipmap.img_cd_3);
                stampResIds.add(R.mipmap.img_cd_1);
                mNestedStampAdapter = new StampAdapter(getContext(), stampResIds);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                recyclerView.setAdapter(new StampAdapter(getContext(), stampResIds));
            }
        } else {
            ImageView img = holder.getView(R.id.card_iv);
            img.setImageResource(item.coverImageResId);
            holder.setText(R.id.name_tv, item.name)
                    .setText(R.id.singer_tv, "Singer");
            holder.setBackgroundResource(R.id.content_layout, (position & 1) == 0 ? R.color.black : R.color.md_grey_900);
        }
    }

    @Override
    protected int getItemViewLayoutId(int position, Music data) {
        if (position == POSITION_RECYCLER_VIEW) {
            return R.layout.item_nested_recycler_view;
        }
        return R.layout.item_music;
    }

    public static class StampAdapter extends SimpleChopinAdapter<Integer> {

        public StampAdapter(Context context, List<Integer> dataSource) {
            super(context, dataSource);
        }

        @Override
        protected void bindDataToItemView(ChopinViewHolder holder, Integer resId, int position) {
            ((ImageView) holder.getView(R.id.cd_iv)).setImageResource(resId);
        }

        @Override
        protected int getItemViewLayoutId(int position, Integer resId) {
            return R.layout.item_nested_cd;
        }
    }

    public static class Music {
        public String name;

        public int coverImageResId;

        public Music(String name, int coverImageResId) {
            this.name = name;
            this.coverImageResId = coverImageResId;
        }
    }
}
