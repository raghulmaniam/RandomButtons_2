package com.mycloset.raghul.randombuttons_2;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class MusicManager {
    private static final MusicManager ourInstance = new MusicManager();
    private MediaPlayer mp;

    public static MusicManager getInstance() {
        return ourInstance;
    }

    public void initalizeMediaPlayer(Context context, int musicId) {
        if(this.mp == null) /*Issue resolved --new music was getting added every time home screen was visited*/
            this.mp = MediaPlayer.create(context, musicId);

    }

    private MusicManager() {
        //do nothing
    }

    public void startPlaying(){
        if(this.mp!= null && !this.mp.isPlaying()) {
            /*try{
                this.mp.prepare();
            }catch (IOException e)
            {
                //do nothing
            }*/

            this.mp.start();
            mp.setLooping(true);
        }

    }

    public void stopPlaying(){
        if(this.mp!=null && this.mp.isPlaying()) {
            //this.mp.stop();
            this.mp.pause();
            //this.mp.reset();
        }
    }
}
