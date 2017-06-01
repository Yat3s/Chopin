package com.yat3s.nimblerecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Yat3s on 27/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public interface StickyHeaderAdapter<VH extends RecyclerView.ViewHolder> {

//    /**
//     * Create a new ViewHolder for a header.  This works the same way onCreateViewHolder in
//     * Recycler.Adapter, ViewHolders can be reused for different views.  This is usually a good place
//     * to inflate the layout for the header.
//     *
//     * @param parent the view to create a header view holder for
//     * @return the view holder
//     */
//    VH onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Bind an existing ViewHolder to the specified adapter position.
     *
     * @param holder   the view holder
     * @param position the adapter position
     */
    void onBindHeaderViewHolder(VH holder, int position);

    int getHeaderViewLayoutId(int position);

    boolean hasHeader(int position);
}
