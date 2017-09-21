package com.yat3s.chopin.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yat3s.chopin.ViewScrollHelper;

/**
 * Created by Yat3s on 07/07/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class RecyclerViewFragment extends Fragment {
    public static final String EXTRA_HEADER_BACKGROUND = "header_background";

    public static RecyclerViewFragment newInstance(int headerBackgroundResId) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_HEADER_BACKGROUND, headerBackgroundResId);
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_musician, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // Configure adapter.
        MusicAdapter musicAdapter = new MusicAdapter(getActivity(), DataRepository.generateMusicData());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(musicAdapter);

        ImageView headerView = new ImageView(getActivity());
        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.common_recycler_view_header_height)));
        headerView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        headerView.setImageResource(getArguments().getInt(EXTRA_HEADER_BACKGROUND));
        musicAdapter.addHeaderView(headerView);

        super.onViewCreated(view, savedInstanceState);
    }

    public boolean canDoRefresh() {
        return ViewScrollHelper.viewHasScrolledToTop(mRecyclerView);
    }

    public boolean canDoLoading() {
        return ViewScrollHelper.viewHasScrolledToBottom(mRecyclerView);
    }
}


