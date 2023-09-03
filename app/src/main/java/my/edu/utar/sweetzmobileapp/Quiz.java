package my.edu.utar.sweetzmobileapp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Quiz {
    private String quizId;
    private String title = "N/A";
    private String desc = "N/A";
    private String author = "N/A";
    private String lastUpdate;
    private String numPlay = "0";

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

    public String getNumPlay() {
        return numPlay;
    }

    public void setNumPlay(String numPlay) {
        this.numPlay = numPlay;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
