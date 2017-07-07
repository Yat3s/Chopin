package com.yat3s.chopin.sample;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.yat3s.chopin.adapter.ChopinViewHolder;
import com.yat3s.chopin.adapter.StickyHeaderAdapter;
import com.yat3s.library.adapter.BaseAdapter;
import com.yat3s.library.adapter.BaseViewHolder;

import java.util.List;

/**
 * Created by Yat3s on 16/06/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */

public class MusicAdapter extends BaseAdapter<MusicAdapter.Music> implements StickyHeaderAdapter<ChopinViewHolder> {
    private static final String TAG = "MusicAdapter";

    public MusicAdapter(Context context, List<Music> data) {
        super(context, data);
    }

    @Override
    protected void bindDataToItemView(BaseViewHolder holder, Music item, final int position) {
        holder.setText(R.id.title_tv, item.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getItemViewLayoutId(int position, Music data) {
        return R.layout.item_music;
    }

    @Override
    public void onBindHeaderViewHolder(ChopinViewHolder holder, final int position) {
        holder.setTextView(R.id.header_tv, getDataSource().get(position).name);
        holder.getView(R.id.header_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "StickyHeader" + position + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getHeaderViewLayoutId(int position) {
        return R.layout.layout_sticky_header;
    }

    @Override
    public boolean hasHeader(int position) {
        return position % 8 == 0 && position != 0;
    }

    public static class Music {
        public String name;

        public Music(String name) {
            this.name = name;
        }
    }
}
