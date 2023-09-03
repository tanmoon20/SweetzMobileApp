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
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends HeaderFooterActivity {
    private MusicManager musicManager;
    public MainActivity()
    {
        super("Home");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MainActivity2", "Test pull request");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase start by En Yee
        FirebaseApp.initializeApp(this);

        /*displayRow();*/
        // Make instance MusicManager
        musicManager = MusicManager.getInstance();
        if(!musicManager.isPlaying()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    musicManager.togglePlayback();
                }
            }, 2000); // <-- This is delay the music because the phone need to load first
        }

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

        Log.i("MainActivity2", "Something: Hello ");
        Handler mHandler = new Handler();
        MyThread connectingThread = new MyThread(mHandler);
        connectingThread.start();
    }

    private class MyThread extends Thread {
        private Handler mHandler;

        public MyThread(Handler handler) {
            mHandler = handler;
        }

        public void run() {
            try {
//                URL url = new URL("https://pjfecjvyukkzaqwhpysj.supabase.co/rest/v1/Quiz?QuizId=eq.1");
                URL url = new URL("https://pjfecjvyukkzaqwhpysj.supabase.co/rest/v1/Quiz?select=*");
                HttpURLConnection hc = (HttpURLConnection) url.openConnection();

//                hc.setRequestMethod("GET");
                hc.setRequestProperty("apikey", getString(R.string.SUPABASE_KEY));
                hc.setRequestProperty("Authorization", "Bearer " + getString(R.string.SUPABASE_KEY));

                InputStream input = hc.getInputStream();
                String result = readStream(input);

//                try{
//                    JSONObject jsonObject = new JSONObject(result);
//                    String title = jsonObject.getString("Title");
//                    Log.i("MainActivity2", "Something Title: " + title);
////                    int age = jsonObject.getInt("age");
////                    tv.setText("You have successfully accessed a web API!\n" + intent.getStringExtra("response") + name + "'s age is " + age);
//                }catch(JSONException e){
//                    e.printStackTrace();
//                }



                Log.i("MainActivity2", "Something: " + result);
                if (hc.getResponseCode() == 200) {
                    Log.i("MainActivity2", "Response: success " + result);
//                    Intent myIntent = new Intent(MainActivity2.this, SuccessActivity.class);
                } else {
                    Log.i("MainActivity2", "Response: failed " + hc.getResponseCode());
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

    private class InternetPermission{

    }
}