package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends HeaderFooterActivity {

    public MainActivity()
    {
        super("Home");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayRow();
        displayRow();
        displayRow();
        displayRow();
        displayRow();
    }

    public void displayRow(){
        LinearLayout ll = findViewById(R.id.quiz_title_container);

        View cardView = getLayoutInflater().inflate(R.layout.quiz_title_card, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,4,30,4);
        cardView.setLayoutParams(params);

        ll.addView(cardView);
    }
}