package com.mycloset.raghul.randombuttons_2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/*

Developer: Raghul Subramaniam
Email: raghulmaniam@gmail.com

*/

import java.util.Random;

public class GameSelection extends Activity implements View.OnClickListener {

    private Handler mHandler = new Handler();
    private Random rnd = new Random();
    private FrameLayout mainFrameLayout;
    private int dummyButtonCounter ;


    MediaPlayer defaultSound = null;
    MediaPlayer exitSound = null;

    //boolean groovyModeClicked = false;
    //MediaPlayer bgm = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button game1, game2 , game3 , game1_groovy;

        MusicManager.getInstance().initalizeMediaPlayer(this, R.raw.intro2);
        MusicManager.getInstance().startPlaying();

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
        game1_groovy = findViewById(R.id.button_game1_groovy); //Random Buttons -Groovy

        mainFrameLayout = findViewById(R.id.dummyButtonLayout2);

        defaultSound = MediaPlayer.create(this, R.raw.default_sound);
        exitSound = MediaPlayer.create(this, R.raw.exit_sound);
        //bgm = MediaPlayer.create(this, R.raw.intro);

        game1.setOnClickListener(this);
        game2.setOnClickListener(this);
        game3.setOnClickListener(this);
        game1_groovy.setOnClickListener(this);

        //To create and animate dummy buttons
        createButtonRunnable.run();

        //to rotate the entire layout along with the buttons
        rotate_right(mainFrameLayout, 70000);

    }

    private Runnable createButtonRunnable = new Runnable() {
        @Override
        public void run() {
            newButton();
            dummyButtonCounter++;

            //keeping the count less than 50 to maintain the performance
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

        int width,height,leftMargin,topMargin;
        Button button = new Button(this);
        Random randomParam = new Random();

        animate(button);

        //button dimensions
        height = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(25) + 50) * 0.5) + 0.5f);
        width = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(50) + 50) * 0.5) + 0.5f);

        //button position
        leftMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(260) + 10) * 0.8) + 0.5f);
        topMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(380) + 10) * 0.8) + 0.5f);

        //random color
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius( 8 );
        shape.setStroke(5,Color.BLACK);

        shape.setColor(color);
        button.setBackground(shape);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(leftMargin, topMargin, 0, 0);

        mainFrameLayout.addView(button, layoutParams);
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

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(R.anim.enter_fron_left, R.anim.exit_out_right);

        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //Log.d(TAG, "MYonStop is called");
        MusicManager.getInstance().stopPlaying();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //Log.d(TAG, "MYonStop is called");
        MusicManager.getInstance().startPlaying();
    }


    @Override
    public void onClick(View view){

        switch (view.getId()) {
            case R.id.button_game1: {

                MusicManager.getInstance().stopPlaying();

                if(defaultSound!= null)
                    defaultSound.start();

                Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
                intent.putExtra("mode" , 1); //classic game
                startActivity(intent);

                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                break;
            }
            case R.id.button_game1_groovy: {

                //MusicManager.getInstance().stopPlaying();
                //let the music play in groovy mode as well
                //groovyModeClicked = true;
                if(defaultSound!= null)
                    defaultSound.start();

                Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
                intent.putExtra("mode" , 2); //groovy mode
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                break;
            }
            case R.id.button_game2: {
                //showRulesDialog();
                MusicManager.getInstance().stopPlaying();

                if(defaultSound!= null)
                    defaultSound.start();

                //sequence buttons
                Intent intent = new Intent(getApplicationContext(), SequenceButtons.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                break;
            }
            case R.id.button_game3: {

                MusicManager.getInstance().stopPlaying();

                if(defaultSound!= null)
                    defaultSound.start();

                //memory buttons
                Intent intent = new Intent(getApplicationContext(), MemoryButtons.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);
                break;
            }
        }
    }
}