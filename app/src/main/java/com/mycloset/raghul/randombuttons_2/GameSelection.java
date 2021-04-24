package com.mycloset.raghul.randombuttons_2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*

Developer: Raghul Subramaniam
Email: raghulmaniam@gmail.com

 */


import java.util.Random;

public class GameSelection extends Activity implements View.OnClickListener {

    Dialog rulesDialog;
    private Handler mHandler = new Handler();
    Random rnd = new Random();
    private FrameLayout mainFrameLayout;
    int width,height,leftMargin,topMargin,dummyButtonCounter ;
    Button game1, game2 , game3;

    MediaPlayer defaultSound = null;
    MediaPlayer exitSound = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        setContentView(R.layout.activity_game_selection);

        game1 = findViewById(R.id.button_game1); //Random Buttons
        game2 = findViewById(R.id.button_game2); //Sequence Buttons
        game3 = findViewById(R.id.button_game3); //Memory Buttons

        mainFrameLayout = findViewById(R.id.dummyButtonLayout2);

        defaultSound = MediaPlayer.create(this, R.raw.default_sound);
        exitSound = MediaPlayer.create(this, R.raw.exit_sound);

        game1.setOnClickListener(this);
        game2.setOnClickListener(this);
        game3.setOnClickListener(this);

        createButtonRunnable.run();
        rotate_right(mainFrameLayout, 70000);

    }

    private Runnable createButtonRunnable = new Runnable() {
        @Override
        public void run() {
            newButton();
            dummyButtonCounter++;

            if(dummyButtonCounter<50)
                mHandler.postDelayed(createButtonRunnable, 800);
        }
    };

    public void rotate_right(FrameLayout layout, int duration) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.rotate_and_zoom);
        anim.setDuration(duration);
        anim.setRepeatCount(Animation.INFINITE);
        layout.startAnimation(anim);
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
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.lefttoright);

        anim.setDuration(4000);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }

    @Override
    public void onBackPressed()
    {
        if(exitSound!= null)
            exitSound.start();

        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }


    @Override
    public void onClick(View view){

        switch (view.getId()) {
            case R.id.button_game1: {

                if(defaultSound!= null)
                    defaultSound.start();

                Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.zoomin_activity);
                break;
            }
            case R.id.button_game2: {
                //showRulesDialog();

                if(defaultSound!= null)
                    defaultSound.start();

                Intent intent = new Intent(getApplicationContext(), SequenceButtons.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.fadein, R.anim.zoomin_activity);
                break;
            }
            case R.id.button_game3: {
                //showRulesDialog();

                if(defaultSound!= null)
                    defaultSound.start();

                Intent intent = new Intent(getApplicationContext(), MemoryButtons.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.fadein, R.anim.zoomin_activity);
                break;
            }
        }
    }
}


