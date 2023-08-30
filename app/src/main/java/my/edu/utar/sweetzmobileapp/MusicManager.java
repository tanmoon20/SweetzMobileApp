package my.edu.utar.sweetzmobileapp;

import android.app.Application;
import android.media.MediaPlayer;

public class MusicManager extends Application {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private int curSongIndex = 0;
    private int[] playlist = {R.raw.song1, R.raw.song2, R.raw.song3};
    private boolean isPlaying = false;


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
            mediaPlayer.start();
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
            mediaPlayer.start();
        }
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }
}
