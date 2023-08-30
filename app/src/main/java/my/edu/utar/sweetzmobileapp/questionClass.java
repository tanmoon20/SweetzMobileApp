package my.edu.utar.sweetzmobileapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class questionClass {
    private String questionSTR;
    private String [] optionList = new String[4];
    private String correctAns;
    Random random = new Random();


    //Default Constructor
    public questionClass(){
        questionSTR = "No question is set ! please use <variableName>.setQuestion(\"QUESTION\") to set a question";
        for(int i = 0; i<4 ; i++){
            optionList[i] = "Option - "+i;
        }
        correctAns = "NONE";
    }

    //Constructor
    public questionClass(String questionSTR, String [] optionList, String correctAns){
        this.questionSTR = questionSTR;
        this.optionList = optionList;
        this.correctAns = correctAns;
    }

    public String getQuestion(){
        return questionSTR;
    }

    public void setQuestion(String questionSTR){
        this.questionSTR = questionSTR;
    }

    public String [] getOptionList(){
        return optionList;
    }

    //This method will require to set all the options string
    public void setOptionList(String [] optionList){
        this.optionList = optionList;
    }

    //This method will allow modifying a specific option string
    public void setSingleQuestion(String singleQuestion, int index){
        optionList[index] = singleQuestion;
    }

    public void setCorrectAns(String correctAns){
        this.correctAns = correctAns;
    }

    public String [] shuffleOption(){
        ArrayList<String> copyOption = new ArrayList<String>(Arrays.asList(optionList));
        int tmp;
        String tmpStr;
        for(int i = 0; i<4; i++){
            tmp = random.nextInt(4)+1;
            tmpStr = copyOption.get(tmp);
            copyOption.remove(tmp);
            copyOption.add(tmpStr);
        }
        optionList = copyOption.toArray(new String[copyOption.size()]);
        return optionList;
    }

}
