package my.edu.utar.sweetzmobileapp;

import android.os.Bundle;

 public class CreateRoomActivity extends HeaderFooterActivity {

     public CreateRoomActivity()
     {
         super("Create Room");
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);
    }
}