package my.edu.utar.sweetzmobileapp;

import android.app.Application;
import android.media.MediaPlayer;

public class MusicManager extends Application {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private int curSongIndex = 0;
    private int[] playlist = {R.raw.song1, R.raw.song2, R.raw.song3};
    private boolean isPlaying = false;
    private boolean isMuted = false;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mediaPlayer = MediaPlayer.create(MusicManager.getInstance(), playlist[curSongIndex]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextSong();
            }
        });
    }

    public static synchronized MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void togglePlayback() {
        if (isPlaying) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
        }
        isPlaying = !isPlaying;
    }

    public void toggleMute() {
        if (isMuted) {
            mediaPlayer.setVolume(1.0f, 1.0f);
        } else {
            mediaPlayer.setVolume(0.0f, 0.0f);
        }
        isMuted = !isMuted;
    }

    private void playNextSong() {
        curSongIndex++;
        if (curSongIndex < playlist.length) {
            mediaPlayer.release(); // Release the current MediaPlayer
            mediaPlayer = MediaPlayer.create(MusicManager.this, playlist[curSongIndex]);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong();
                }
            });
            if (!isMuted) {
                mediaPlayer.start();
            }
        } else {
            curSongIndex = 0; // Reset index for loop
            mediaPlayer.release(); // Release the current MediaPlayer
            mediaPlayer = MediaPlayer.create(MusicManager.this, playlist[curSongIndex]);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playNextSong();
                }
            });
            if (!isMuted) {
                mediaPlayer.start();
            }
        }
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public boolean isMuted() { return isMuted; }


    public void stopOrPlayMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else{
            mediaPlayer.start();
        }
    }
    //This function would be used for everyone
}
