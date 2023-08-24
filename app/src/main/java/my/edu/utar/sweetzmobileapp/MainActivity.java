package my.edu.utar.sweetzmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

        ImageView btnPlay = cardView.findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener((v)->{
            Intent intent = new Intent(this, PlayActivity.class);
            startActivity(intent);
        });
        ll.addView(cardView);
    }

    private class MyThread extends Thread {
        private String mName;
        private Handler mHandler;

        public MyThread(String name, Handler handler) {
            mName = name;
            mHandler = handler;
        }

        public void run() {
            try {
                URL url = new URL("https://pjfecjvyukkzaqwhpysj.supabase.co/rest/v1/Quiz");
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Authorization", "Bearer" + getString(R.string.SUPABASE_KEY));

                InputStream input = hc.getInputStream();
                String result = readStream(input);

                Log.i("MainActivity2", "Something: " + result);
                if (hc.getResponseCode() == 200) {
                    Log.i("MainActivity2", "Response: " + result);
//                    Intent myIntent = new Intent(MainActivity2.this, SuccessActivity.class);
                } else {
                    Log.i("MainActivity2", "Response: " + hc.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new
                    ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}