package com.yat3s.kitten.sample;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

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
    private static final String TAG = "MusicAdapter";

    public MusicAdapter(Context context, List<Music> data) {
        super(context, data);
    }

    @Override
    protected void bindDataToItemView(NimbleViewHolder holder, Music music, final int position) {
        holder.setTextView(R.id.title_tv, music.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getItemViewLayoutId(int position, Music data) {
        return R.layout.item_music;
    }

    @Override
    public void onBindHeaderViewHolder(NimbleViewHolder holder, final int position) {
        holder.setTextView(R.id.header_tv, mDataSource.get(position).name);
        holder.getView(R.id.header_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "StickyHeader" + position + "", Toast.LENGTH_SHORT).show();
            }
        });
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
