package com.yat3s.chopin.sample;

import java.util.ArrayList;

import static com.yat3s.chopin.sample.R.array.musics;


/**
 * Created by Yat3s on 7/12/2017.
 * Email: hawkoyates@gmail.com
 * GitHub: https://github.com/yat3s
 */
public class DataRepository {

    private final static int[] ABSTRACT_IMAGE_RESOURCE_ID = {R.mipmap.abstract_1,
            R.mipmap.abstract_2, R.mipmap.abstract_3, R.mipmap.abstract_4};

    public static ArrayList<MusicAdapter.Music> generateMusicData(int count) {
        final String[] musicNames = ChopinApplication.getContext().getResources().getStringArray(musics);
        int[] coverImageIds = {R.mipmap.stamp_1, R.mipmap.stamp_2, R.mipmap.stamp_3, R.mipmap.stamp_4};
        ArrayList<MusicAdapter.Music> musics = new ArrayList<>();

        for (int idx = 0; idx < count; idx++) {
            musics.add(new MusicAdapter.Music(musicNames[idx % musicNames.length], coverImageIds[idx % coverImageIds.length]));
        }
        return musics;
    }

    public static int generateRandomImageResId() {
        int randomIndex = (int) (Math.random() * ABSTRACT_IMAGE_RESOURCE_ID.length);
        return ABSTRACT_IMAGE_RESOURCE_ID[randomIndex];
    }
}
