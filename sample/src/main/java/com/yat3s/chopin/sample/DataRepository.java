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

    public static ArrayList<MusicAdapter.Music> generateMusicData() {
        final String[] musicNames = ChopinApplication.getContext().getResources().getStringArray(musics);
        ArrayList<MusicAdapter.Music> musics = new ArrayList<>();
        for (String musicName : musicNames) {
            musics.add(new MusicAdapter.Music(musicName, generateRandomImageResId()));
        }
        return musics;
    }

    public static int generateRandomImageResId() {
        int randomIndex = (int) (Math.random() * ABSTRACT_IMAGE_RESOURCE_ID.length);
        return ABSTRACT_IMAGE_RESOURCE_ID[randomIndex];
    }

//    private static List<PostcardAdapter.Postcard> generatePostcardData(int size) {
//        int[] postcardImageIds = {R.mipmap.card_1, R.mipmap.card_2, R.mipmap.card_3,
//                R.mipmap.card_4, R.mipmap.card_5, R.mipmap.card_6, R.mipmap.card_7};
//        List<PostcardAdapter.Postcard> postcards = new ArrayList<>();
//        for (int idx = 0; idx < size; idx++) {
//            postcards.add(new PostcardAdapter.Postcard(postcardImageIds[idx % postcardImageIds.length]));
//        }
//        return postcards;
//    }
}
