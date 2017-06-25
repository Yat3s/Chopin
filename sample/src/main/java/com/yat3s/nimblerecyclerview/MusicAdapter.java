package com.yat3s.nimblerecyclerview;

import android.content.Context;

import com.yat3s.kitten.adapter.NimbleAdapter;
import com.yat3s.kitten.adapter.NimbleViewHolder;
import com.yat3s.kitten.adapter.StickyHeaderAdapter;

import java.util.List;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */

public class MusicAdapter extends NimbleAdapter<Music, NimbleViewHolder> implements
        StickyHeaderAdapter<NimbleViewHolder> {
    public MusicAdapter(Context context, List<Music> data) {
        super(context, data);
    }

    @Override
    protected void bindDataToItemView(NimbleViewHolder holder, Music music, int position) {
        holder.setTextView(R.id.title_tv, music.name);
    }

    @Override
    protected int getItemViewLayoutId(int position, Music data) {
        return R.layout.item_music;
    }

    @Override
    public void onBindHeaderViewHolder(NimbleViewHolder holder, int position) {
        holder.setTextView(R.id.header_tv, mDataSource.get(position).name);
    }

    @Override
    public int getHeaderViewLayoutId(int position) {
        return R.layout.header_layout;
    }

    @Override
    public boolean hasHeader(int position) {
        return position % 8 == 0 && position != 0;
    }

}
