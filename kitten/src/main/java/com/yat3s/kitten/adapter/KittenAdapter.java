package com.yat3s.kitten.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yat3s on 26/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public abstract class KittenAdapter<T, VH extends KittenViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = "KittenAdapter";

    private int mCurrentViewTypeValue = 0x0100;

    // The data source of adapter.
    protected List<T> mDataSource;

    // The context from recycler view.
    protected Context mContext;

    // The layout inflater to inflate all item view.
    protected LayoutInflater mInflater;

    // Save all layout id, key is view type, value is layout id.
    private SparseIntArray mLayoutIdCacheArray;

    // Save all view type, key is layout id, value is view type.
    private SparseIntArray mViewTypeCacheArray;

    public KittenAdapter(Context context) {
        this(context, null);
    }

    public KittenAdapter(Context context, List<T> dataSource) {
        mDataSource = null == dataSource ? new ArrayList<T>() : dataSource;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutIdCacheArray = new SparseIntArray();
        mViewTypeCacheArray = new SparseIntArray();
    }

    protected abstract void bindDataToItemView(VH holder, T t, int position);

    protected abstract int getItemViewLayoutId(int position, T t);

    @Override
    public int getItemViewType(int position) {
        int currentLayoutId = getItemViewLayoutId(position, mDataSource.get(position));

        if (mViewTypeCacheArray.get(currentLayoutId) == 0) {
            mCurrentViewTypeValue++;
            mViewTypeCacheArray.put(currentLayoutId, mCurrentViewTypeValue);
            mLayoutIdCacheArray.put(mCurrentViewTypeValue, currentLayoutId);
        }
        return mViewTypeCacheArray.get(currentLayoutId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return (VH) new KittenViewHolder(mInflater.inflate(mLayoutIdCacheArray.get(viewType), parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        bindDataToItemView(holder, getItem(position), position);
    }

    /**
     * Retrieve item data from {@link #mDataSource}
     *
     * @param position the position of item
     * @return
     */
    private T getItem(int position) {
        return mDataSource.get(position);
    }


    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

}
