package com.mycloset.raghul.randombuttons_2;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.graphics.*;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //
    //
    //Developer: Raghul Subramaniam
    //Email: raghulmaniam@gmail.com
    //
    //

    private Handler mHandler = new Handler();
    Random rnd = new Random();
    private FrameLayout mainFrameLayout;
    int width, height, leftMargin,topMargin,dummyButtonCounter;
    Dialog rulesDialog;
    ImageView bulb;
    private  RelativeLayout homeScreen;
    private  Boolean bulbOn;
    private TextView titleText;
    private Integer blinkDelay = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TextView enter,about, highScore ,exit;

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        bulbOn = true;

        setContentView(R.layout.activity_main);
        enter = findViewById(R.id.enter);
        about = findViewById(R.id.about);
        highScore = findViewById(R.id.highscore);
        exit = findViewById(R.id.exit);
        bulb = findViewById(R.id.bulb);
        homeScreen = findViewById(R.id.homescreen);
        mainFrameLayout = findViewById(R.id.dummyButtonLayout);
        titleText = findViewById(R.id.titleText);

        enter.setOnClickListener(this);
        exit.setOnClickListener(this);
        highScore.setOnClickListener(this);
        about.setOnClickListener(this);
        titleText.setOnClickListener(this);
        bulb.setOnClickListener(this);

        createButtonRunnable.run();
        //bulbBlink.run();
        zoom_in(mainFrameLayout, 40000);
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
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fadein);

        anim.setDuration(400);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }

    private Runnable bulbBlink = new Runnable() {
        @Override
        public void run() {

                if (!bulbOn) {
                    bulb.setImageResource(R.drawable.bulb_on);
                    bulbOn = true;
                    homeScreen.setBackgroundResource(R.drawable.curve);
                } else {
                    bulb.setImageResource(R.drawable.bulb_off);
                    bulbOn = false;
                    homeScreen.setBackgroundResource(R.drawable.box_curved);
                }

                //two blinks.. pause.. one blink.. pause..

                if (blinkDelay == 100) {
                    blinkDelay = 110;
                    //secondTurtle.setImageResource(R.mipmap.turtle_ingame_2_1);
                    //secondTurtle.setTag("closed");
                } else if (blinkDelay == 110)
                    blinkDelay = 112;
                else if (blinkDelay == 112 || blinkDelay == 810) {
                    //secondTurtle.setImageResource(R.mipmap.turtle_ingame_2);
                    //secondTurtle.setTag("open");
                    bulbOn = true;
                    blinkDelay = 1800;
                } else if (blinkDelay == 1800)
                    blinkDelay = 101;
                else if (blinkDelay == 101)
                    blinkDelay = 1801;
                else if (blinkDelay == 1801)
                    blinkDelay = 100;

                mHandler.postDelayed(bulbBlink, blinkDelay);
            }


    };



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
            case R.id.bulb: {
                if(bulbOn) {
                    bulb.setImageResource(R.drawable.bulb_off);
                    bulbOn = false;
                    homeScreen.setBackgroundResource(R.drawable.box_curved);
                    titleText.setTextColor(Color.WHITE);
                }
                else {
                    bulb.setImageResource(R.drawable.bulb_on);
                    bulbOn = true;
                    homeScreen.setBackgroundResource(R.drawable.curve);
                    titleText.setTextColor(Color.BLACK);
                }
                break;
            }
            case R.id.titleText:
            case R.id.about: {
                showRulesDialog();
                break;
            }
        }
    }

    public void showRulesDialog()
    {
        rulesDialog = new Dialog(this);
        rulesDialog.setContentView(R.layout.rules_dialog);

        if(rulesDialog.getWindow()!= null)
        rulesDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView dialogText;

        dialogText = rulesDialog.findViewById(R.id.rulesText);
        dialogText.setText("Developed By \nRaghul Subramaniam");

        ImageView dialogImage;

        dialogImage = rulesDialog.findViewById(R.id.dialogImage);
        dialogImage.setImageResource(R.mipmap.ic_launcher_foreground);

        Button button = rulesDialog.findViewById(R.id.dialogOkayButton);
        button.setText(R.string.Back);

        rulesDialog.setCancelable(false);

        Window window = rulesDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        rulesDialog.show();

        button.setOnClickListener(new View.OnClickListener(){
                                          @Override
                                          public void onClick(View view)
                                          {
                                              rulesDialog.dismiss();
                                          }
                                      }
        );
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

            if(dummyButtonCounter<50)
            mHandler.postDelayed(createButtonRunnable, 800);
        }
    };

}
