package com.example.muzic.model;

import android.media.MediaPlayer;

public class MediaPlayerSingleton {
    private static MediaPlayer instance;

    public static MediaPlayer getInstance(){
        if(instance==null){
            instance=new MediaPlayer();
        }
        return instance;
    }
    public static  int currIdx=-1;
}
