package com.yat3s.nimblerecyclerview;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Yat3s on 26/05/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class NimbleViewHolder extends RecyclerView.ViewHolder {

    // A sparse array for cache item view.
    private final SparseArray<View> mCachedViews = new SparseArray<>();

    public NimbleViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * Retrieve view for {@link #mCachedViews} while cache is available,
     * otherwise {@link View#findViewById(int)} to find view and cache it.
     *
     * @param id      View resource id.
     * @param <TView> View type for return.
     * @return
     */
    @SuppressWarnings("unchecked")
    public <TView extends View> TView getView(@IdRes int id) {
        View view = mCachedViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mCachedViews.put(id, view);
        }
        return (TView) view;
    }

    public NimbleViewHolder setTextView(@IdRes int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    public NimbleViewHolder setTextSizeInSp(@IdRes int viewId, int sp) {
        TextView view = getView(viewId);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        return this;
    }

    public NimbleViewHolder setBackgroundResource(@IdRes int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public NimbleViewHolder setVisible(@IdRes int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public NimbleViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}
