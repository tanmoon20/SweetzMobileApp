package my.edu.utar.sweetzmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreateQuizActivity extends HeaderFooterActivity {
    TextView editName, editRoomCode, editDesc;
    Button enterBtn;
    String roomCode, quizName, quizDesc, quizCode, author = "user1";
    Integer numPlay = 0;
    FirestoreManager2 firestoreManager2;

    public CreateQuizActivity() {
        super("Create Quiz");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        editRoomCode = findViewById(R.id.edit_enter_code);
        editName = findViewById(R.id.edit_quiz_name);
        editDesc = findViewById(R.id.edit_quiz_desc);
        enterBtn = findViewById(R.id.create_quiz_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roomCode = editRoomCode.getText().toString();
                quizName = editName.getText().toString();
                quizDesc = editDesc.getText().toString();

                Quiz quiz = new Quiz();
                quiz.setQuizId("");
                quiz.setAuthor(author);
                quiz.setDesc(quizDesc);
                quiz.setTitle(quizName);
                quiz.setNumPlay(numPlay);

                firestoreManager2.insertPrivateRoomQuiz(roomCode,"", quizName, quizDesc);
                Intent intent = new Intent(CreateQuizActivity.this, PlayActivity.class);
                intent.putExtra("quiz",quiz);
                startActivityForResult(intent, 0);
            }
        });
        //get current quiz id
        //get room code

    }

}
