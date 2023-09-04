package my.edu.utar.sweetzmobileapp;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        this.lastUpdate = lastUpdate;
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
