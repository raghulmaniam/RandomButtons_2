package com.mycloset.raghul.randombuttons_2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.SharedPreferences.Editor;

public class MemoryButtons extends AppCompatActivity implements View.OnClickListener {


    AtomicInteger curLevel = new AtomicInteger();
    AtomicInteger buttonsCount =new AtomicInteger();
    AtomicInteger score = new AtomicInteger();
    Dialog rulesDialog , gameoverDialog;
    private TextView counterValueMain;
    private FrameLayout mainFrameLayout ;
    private  RelativeLayout bulbLayout;
    volatile public Boolean bulbOn = false;
    int leftMargin, topMargin;
    private Handler mHandler = new Handler();
    ImageView bulb;
    List<Button> buttonsList = new ArrayList<>();
    TextView scoreText;

    public ProgressBar progressBarTime;
    public ProgressBar progressBarButtons;
    public BigDecimal timeProgressInt;
    public BigDecimal buttonsProgressInt;

    int timer_seconds = 0;
    AtomicInteger correctButtonsClicked = new AtomicInteger();
    int layoutHeight, layoutWidth;
    TextView dialogText;
    Button retryButton, exitButton;
    TextView curLevelText;
    CountDownTimer timerCheck;
    CountDownTimer countdownTimer;

    int BUTTON_SIZE = 200;
    GradientDrawable whiteshape, redShape;
    volatile ImageView star;

    MediaPlayer clickSound = null;
    MediaPlayer defaultSound = null;
    MediaPlayer exitSound = null;

    MediaPlayer lightOn = null;
    MediaPlayer lightOff = null;

    volatile Boolean gameOver = false;
    volatile Boolean backPressed = false;

    boolean isNewLevel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        setContentView(R.layout.activity_memory_buttons);

        counterValueMain = findViewById(R.id.mem_start_countdown);

        mainFrameLayout = findViewById(R.id.mem_mainGameLayout);
        bulbLayout = findViewById(R.id.mem_mainLayout);

        score.set(0);
        correctButtonsClicked.set(5); //dont make it zero, first time check will fail
        buttonsCount.set(4);
        curLevel.set(1);
        bulb = findViewById(R.id.mem_bulb);
        scoreText = findViewById(R.id.mem_CounterTextView);
        scoreText.setText(Integer.toString(0));

        progressBarButtons = findViewById(R.id.mem_progressbar_buttons);
        progressBarTime = findViewById(R.id.mem_progressbar_time);

        curLevelText = findViewById(R.id.mem_levelText);

        clickSound = MediaPlayer.create(this, R.raw.rand_click_wav);
        defaultSound = MediaPlayer.create(this, R.raw.default_sound);
        exitSound = MediaPlayer.create(this, R.raw.exit_sound);
        lightOn = MediaPlayer.create(this, R.raw.light_on_sound);
        lightOff = MediaPlayer.create(this, R.raw.light_off_sound);

        whiteshape=  new GradientDrawable();
        whiteshape.setCornerRadius( 12 );
        whiteshape.setStroke(5,Color.BLACK);
        whiteshape.setColor(Color.WHITE);
        redShape = new GradientDrawable();
        redShape.setCornerRadius( 12 );
        redShape.setStroke(5,Color.BLACK);
        redShape.setColor(Color.RED);

        star = findViewById(R.id.mem_star);
        star.setVisibility(View.GONE);

        showRulesDialog();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.mem_dialogOkayButton: {

                rulesDialog.dismiss();

                layoutHeight = mainFrameLayout.getMeasuredHeight() -BUTTON_SIZE;
                layoutWidth = mainFrameLayout.getMeasuredWidth() -BUTTON_SIZE;
                //100 -> to negate button size
                counterBeforeGame();
                break;
            }
            default: {

                if(clickSound!= null)
                    clickSound.start();

                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( 12 );
                shape.setStroke(5,Color.BLACK);

                view.clearAnimation();
                view.bringToFront();
                view.setOnClickListener(null);
                view.setVisibility(View.VISIBLE);

                if ((int) view.getTag() == 1) {
                    //correct button

                    buttonsProgressInt = new BigDecimal(correctButtonsClicked.incrementAndGet()).divide(new BigDecimal(buttonsCount.get()), 2 , RoundingMode.UP).multiply(new BigDecimal(100));
                    progressBarButtons.setProgress(buttonsProgressInt.intValue());

                    score.incrementAndGet();
                    shape.setColor(Color.WHITE);
                } else {
                    //wrong button
                    score.decrementAndGet();
                    score.decrementAndGet();
                    shape.setColor(Color.RED);
                }

                if(correctButtonsClicked.get() >= buttonsCount.get())
                {
                    star.setVisibility(View.VISIBLE);
                    rotate(star);
                    timerCheck.cancel();
                    mHandler.postDelayed(callNextLevel , 1000);
                }

                view.setBackground(shape);
                scoreText.setText(Integer.toString(score.get()));
            }
        }
    }

    public void rotate(View view) {
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.zoomin_fade);
        anim.setDuration(1000);
        anim.setRepeatCount(600);
        view.startAnimation(anim);
    }

    public void showRulesDialog()
    {
        Button dialogOkay;
        rulesDialog = new Dialog(this);
        rulesDialog.setContentView(R.layout.mem_rules_dialog);

        if(rulesDialog.getWindow()!= null)
            rulesDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogOkay = rulesDialog.findViewById(R.id.mem_dialogOkayButton);
        rulesDialog.setCancelable(false);

        Window window = rulesDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        rulesDialog.show();

        dialogOkay.setOnClickListener(this);
    }

    public void counterBeforeGame() {
        countdownTimer =new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                Long val = millisUntilFinished / 1000;
                counterValueMain.setText(Integer.toString(val.intValue()));
            }

            public void onFinish() {
                counterValueMain.setVisibility(View.INVISIBLE);
                startGame();
            }
        }.start();
    }

    public void countdownAfterGame() {
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                counterValueMain.setVisibility(View.VISIBLE);
                Long val = millisUntilFinished / 1000;
                counterValueMain.setText(Integer.toString(val.intValue()));
            }

            public void onFinish() {
                counterValueMain.setVisibility(View.INVISIBLE);
                //customToast("Start" , Toast.LENGTH_SHORT);
                //callNextLevel.run();
            }
        }.start();
    }

    public void startGame() {
        callNextLevel.run();
    }

    private Runnable callNextLevel = new Runnable() {
        @Override
        public void run() {

            star.clearAnimation();
            star.setVisibility(View.GONE);

            if(bulbOn)
            {
                isNewLevel = false;

                if(lightOn!= null)
                    lightOn.start();

                //start to play
                bulb.setImageResource(R.drawable.bulb_off);
                bulbOn = false;
                bulbLayout.setBackgroundResource(R.drawable.box_curved);

                setBaseColor(buttonsList);

                timer_seconds = 0;
                gameTimer(curLevel.get());
            }
            else
            {
                //memorize
                progressBarTime.setProgress(0);
                progressBarButtons.setProgress(0);

                curLevelText.setText(Integer.toString(curLevel.getAndIncrement()));
                buttonsCount.incrementAndGet();

                progressBarTime.setProgress(0);
                progressBarButtons.setProgress(0);

                clearButtons(buttonsList);

                buttonsList.clear();

                showButtons();

                if(lightOn!= null)
                    lightOn.start();

                bulb.setImageResource(R.drawable.bulb_on);
                bulbOn = true;
                bulbLayout.setBackgroundResource(R.drawable.curve2);

                buttonsList.add(newButton(Color.RED));
                buttonsList.add(newButton(Color.RED));

                for (int i = 0; i < buttonsCount.get(); i++)
                    buttonsList.add(newButton(Color.WHITE));

                correctButtonsClicked.set(0);

                if(!gameOver)
                mHandler.postDelayed(callNextLevel, 5000);

                countdownAfterGame();
            }
        }
    };


    public void gameTimer(final int passedLevel) {
        timerCheck = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {

                if(!bulbOn) {
                    timeProgressInt = new BigDecimal(timer_seconds++).multiply(new BigDecimal(6.666)); //for 15 seconds
                    progressBarTime.setProgress(timeProgressInt.intValue());
                }
            }

            public void onFinish() {

                if((correctButtonsClicked.get() < buttonsCount.get()) && !bulbOn && (passedLevel == curLevel.get()))
                {
                    progressBarTime.setProgress(100);

                    mainFrameLayout.setOnClickListener(null);
                    gameover();
                }
            }
        }.start();
    }

    public Runnable timer = new Runnable() {
        @Override
        public void run() {
            timeProgressInt = new BigDecimal(timer_seconds++).multiply(new BigDecimal(20)); //for 10 seconds
            progressBarTime.setProgress(timeProgressInt.intValue());

            if(timer_seconds<=10)
                mHandler.postDelayed(timer, 5000);
        }
    };

    public  void gameover()
    {
        showLayout();
        showGameOverDialog();
    }


    public void showLayout()
    {
        bulb.setImageResource(R.drawable.bulb_on);
        bulbLayout.setBackgroundResource(R.drawable.curve2);

        showButtons();
    }

    public void showButtons()
    {
        whiteshape.setColor(Color.WHITE);
        redShape.setColor(Color.RED);
    }

    public void showGameOverDialog()
    {
        gameoverDialog = new Dialog(this);
        gameoverDialog.setContentView(R.layout.rules_dialog_2);
        if(gameoverDialog.getWindow()!= null)
            gameoverDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogText = gameoverDialog.findViewById(R.id.rulesText);

        retryButton = gameoverDialog.findViewById(R.id.dialogRetryButton);
        exitButton = gameoverDialog.findViewById(R.id.dialogExitButton);

        retryButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);

        gameoverDialog.setCancelable(false);

        Window window = gameoverDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        try {
            gameoverDialog.show();
        }
        catch (WindowManager.BadTokenException e)
        {
            //do nothing
        }

        retryButton.setOnClickListener(new View.OnClickListener(){
                                           @Override
                                           public void onClick(View view)
                                           {

                                               if(defaultSound!= null)
                                                   defaultSound.start();

                                               gameoverDialog.dismiss();
                                               Intent intent = new Intent(getApplicationContext(), MemoryButtons.class);
                                               startActivity(intent);
                                           }
                                       }
        );

        exitButton.setOnClickListener(new View.OnClickListener(){
                                          @Override
                                          public void onClick(View view)
                                          {

                                              if(exitSound!= null)
                                                  exitSound.start();

                                              Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                              startActivity(intent);
                                              overridePendingTransition(R.anim.enter_fron_left, R.anim.exit_out_right);

                                              gameoverDialog.dismiss();
                                          }
                                      }
        );

        counterAfterGame();
    }

    public void counterAfterGame() {
        new CountDownTimer(2000, 500) {

            public void onTick(long millisUntilFinished) {

                if( millisUntilFinished < 1600) {
                    if (millisUntilFinished > 1101 ) {
                        //retry.setVisibility(View.VISIBLE);
                    }
                    else if (millisUntilFinished > 601)
                        retryButton.setVisibility(View.VISIBLE);
                    else if (millisUntilFinished > 1)
                        exitButton.setVisibility(View.VISIBLE);
                }
            }

            public void onFinish() {
                //Just to be sure even if the above missed to make it Visible
                retryButton.setVisibility(View.VISIBLE);
                exitButton.setVisibility(View.VISIBLE);

                if(!backPressed)
                    ScoreDelegator(dialogText);
            }
        }.start();
    }

    public void ScoreDelegator(TextView dialogText){

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int highScoreEasy = prefs.getInt("mem", 0); //0 is the default value
        int finalScore = Integer.parseInt(scoreText.getText().toString());
        String finalScoreString ="Score: " + finalScore +"\n" +"High Score: " +highScoreEasy;

        if(finalScore>highScoreEasy)
        {
            Editor editor = prefs.edit();
            editor.putInt("mem", finalScore);
            editor.apply();
            customToast("Meet the new Champion! High Score! ", Toast.LENGTH_LONG);

            ImageView star = gameoverDialog.findViewById(R.id.mem_star);
            star.setVisibility(View.VISIBLE);
            rotate(star);
        }
        //else
          //  customToast( "I was so close to becoming the world champion.. So close..!" ,Toast.LENGTH_LONG);

        dialogText.setText(finalScoreString);
    }


    public void clearButtons(List<Button> buttons)
    {
        for(Button button: buttons)
        {
            button.clearAnimation();
            button.setVisibility(View.GONE);
        }
    }

    public  void setBaseColor(List<Button> buttons)
    {
        for(Button button: buttons)
        {
            button.setOnClickListener(this);
        }

        whiteshape.setColor(Color.BLACK);
        redShape.setColor(Color.BLACK);

    }

    @SuppressLint("ClickableViewAccessibility")
    public Button newButton(int color) {
        Button button = new Button(this);

        if(color == Color.WHITE) {
            button.setTag(1);
            button.setBackground(whiteshape);
        }
        else if(color == Color.RED) {
            button.setTag(2);
            button.setBackground(redShape);
        }

        animate(button);


        Random randomParam = new Random();

        /*Width
        Minimum: 50 Maximum: 450/250/150
        Height
        Minimum: 50 Maximum: 550/350/250
        Left Margin
        Minimum: 1  Maximum: 300
        Top Margin
        Minimum: 1  Minimum: 400/500
        */

        leftMargin = randomParam.nextInt(layoutWidth);
        topMargin = randomParam.nextInt(layoutHeight-380) +380;

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(BUTTON_SIZE, BUTTON_SIZE);
        layoutparams.setMargins(leftMargin, topMargin, 0, 0);

        mainFrameLayout.addView(button, layoutparams);
        return button;
    }

    @Override
    public void onBackPressed()
    {
        gameOver= true;
        backPressed = true;

        if(exitSound!= null)
            exitSound.start();

        super.onBackPressed();

        if(timerCheck!= null) {
            timerCheck.cancel();
            timerCheck = null;
        }

        if(countdownTimer!= null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        overridePendingTransition(R.anim.enter_fron_left, R.anim.exit_out_right);
    }

    public void customToast(String message, int length ) {
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    public void animate(Button button) {
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.fadein);

        anim.setDuration(400);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }
}


