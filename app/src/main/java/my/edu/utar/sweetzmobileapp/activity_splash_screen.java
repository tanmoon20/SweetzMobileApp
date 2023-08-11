package my.edu.utar.sweetzmobileapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class activity_splash_screen extends AppCompatActivity {

    ImageView logo;
    int index;
    long delay = 150;
    Handler handler = new Handler();
    private final int SPLASH_DELAY = 3000;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.iv_logo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initializeView();
        animateLogo();

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                logo,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        );

        objectAnimator.setDuration(450);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(activity_splash_screen.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                // Remove the finish() method below
                // finish();
            }
        }, 4000);

    }


    private void initializeView() { imageView = logo; }

    private void animateLogo() {
        Animation fadingInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadingInAnimation.setDuration(SPLASH_DELAY);
        imageView.startAnimation(fadingInAnimation);

    }
}