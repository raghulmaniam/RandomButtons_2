package com.mycloset.raghul.randombuttons_2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.graphics.*;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView enter;
    private TextView about;
    private TextView highScore;
    private TextView exit;
    private ImageView titleImage;

    private Handler mHandler = new Handler();

    Random rnd = new Random();

    public FrameLayout mainFrameLayout;

    int width;
    int height;

    int leftMargin;
    int topMargin;
    int dummyButtonCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);


        //--- To set Full Screen mode ---


        setContentView(R.layout.activity_main);
        enter = findViewById(R.id.enter);
        about = findViewById(R.id.about);
        highScore = findViewById(R.id.highscore);
        exit = findViewById(R.id.exit);
        titleImage = findViewById(R.id.titleImage);

        mainFrameLayout = findViewById(R.id.dummyButtonLayout);

        enter.setOnClickListener(this);
        exit.setOnClickListener(this);
        highScore.setOnClickListener(this);
        about.setOnClickListener(this);
        titleImage.setOnClickListener(this);

        createButtonRunnable.run();

        zoom_in(mainFrameLayout, 40000);
        //fade_in(mainFrameLayout, 2000);

        //rotate(mainFrameLayout, 80000);

    }

    public void newButton() {
        Button button = new Button(this);
        button.setBackgroundResource(R.drawable.button_selector);

        animate(button);

        Random randomParam = new Random();

        height = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(25) + 50) * 0.5) + 0.5f);
        width = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(50) + 50) * 0.5) + 0.5f);

        leftMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(260) + 10) * 0.8) + 0.5f);
        topMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(380) + 10) * 0.8) + 0.5f);


        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius( 8 );
        shape.setStroke(5,Color.BLACK);

        shape.setColor(color);

        button.setBackground(shape);

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(width, height);
        layoutparams.setMargins(leftMargin, topMargin, 0, 0);

        mainFrameLayout.addView(button, layoutparams);
    }


    public void animate(Button button) {
        //Animation anim = new CircularRotateAnimation(button, 60);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);



        //duration of animation
        anim.setDuration(400);
        anim.setRepeatCount(1);

        //start the animation
        button.startAnimation(anim);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.enter: {
                Intent intent = new Intent(getApplicationContext(), GameSelection.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.zoomin_activity);

                break;
            }
            case R.id.exit: {
                finish();
                System.exit(0);
                break;
            }
            case R.id.highscore: {
                SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                int scoreEasy = prefs.getInt("easy", 0); //0 is the default value
                Toast.makeText(getApplicationContext(), "HighScore: " + scoreEasy , Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.titleImage:
            case R.id.about: {
                Intent intent = new Intent(getApplicationContext(), Infoctivity.class);
                startActivity(intent);

                break;
            }
        }

    }

    public void zoom_in(FrameLayout layout, int duration) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        anim.setDuration(duration);
        anim.setRepeatCount(Animation.INFINITE);
        layout.startAnimation(anim);
    }

    private Runnable createButtonRunnable = new Runnable() {
        @Override
        public void run() {
            newButton();
            dummyButtonCounter++;

            if(dummyButtonCounter<500)
            mHandler.postDelayed(createButtonRunnable, 800);
        }
    };

}
