package com.edu.cdp.utils;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

import java.io.IOException;

public class AudioPlayUtils {
    private static AudioPlayUtils INSTANCE;
    private MediaPlayer mMediaPlayer;

    public static synchronized AudioPlayUtils getInstance() {
        if (INSTANCE == null){
            INSTANCE = new AudioPlayUtils();
        }
        return INSTANCE;
    }



    public AudioPlayUtils(){
        mMediaPlayer = new MediaPlayer();
    }

    public void play(String path) throws IOException {
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mMediaPlayer = new MediaPlayer();
        }
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

}
