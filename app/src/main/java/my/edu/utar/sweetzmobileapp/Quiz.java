package my.edu.utar.sweetzmobileapp;

import android.util.Log;

import androidx.annotation.Px;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quiz implements Serializable {
    private String quizId;
    private String roomCode = null;
    private String title = "N/A";
    private String desc = "N/A";
    private String author = "N/A";
    private String lastUpdate;
    private Integer numPlay = 0;

    public Quiz(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        lastUpdate = formatter.format(date);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        String pattern = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(lastUpdate);
        boolean matches = matcher.matches();
        if (matches) {
            Log.i("Match", "This is from firebase");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            this.lastUpdate = sdf.format(lastUpdate);
        } else {
            Log.i("Match", "This is not from firebase");

        }
    }

    public Integer getNumPlay() {
        return numPlay;
    }

    public void setNumPlay(Integer numPlay) {
        this.numPlay = numPlay;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}
