package com.edu.cdp.utils;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

import java.io.IOException;

public class AudioPlayUtils {
    private static AudioPlayUtils INSTANCE;
    private MediaPlayer mMediaPlayer;

    public static synchronized AudioPlayUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AudioPlayUtils();
        }
        return INSTANCE;
    }


    public AudioPlayUtils() {
        mMediaPlayer = new MediaPlayer();
    }

    public void play(String path) throws IOException {
        stop();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }


    public void stop() {
        if (mMediaPlayer != null) {
            if(mMediaPlayer.isPlaying())mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
