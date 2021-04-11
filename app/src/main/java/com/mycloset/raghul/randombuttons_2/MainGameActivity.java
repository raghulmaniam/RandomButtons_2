package com.mycloset.raghul.randombuttons_2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.LinearLayout.LayoutParams;
import android.os.Handler;
import android.os.CountDownTimer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.os.Vibrator;
import android.view.animation.AnimationUtils;
import android.graphics.*;
import android.view.animation.Animation;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

public class MainGameActivity extends Activity implements View.OnClickListener {

    /*

Developer: Raghul Subramaniam
Email: raghulmaniam@gmail.com

 */

    private TextView score, timeCounter,counterValue,counterValueMain,buttonSpeedView ;
    TextView dialogText, dialogFixedTest;
    private FrameLayout mainFrameLayout;

    private Boolean stopRandomButtons;
    private Integer counterUpTimer = 0,blinkDelay = 100, counterBeforeGameValue =3 ;
    int counter, totalButtons,buttonsClicked, buttonsClickedForLevelChange,delayInMS , width,height ,leftMargin,topMargin ;
    long speed;
    Bundle bundle;
    int curLevel = 0;
    Random rnd = new Random();

    private ImageView secondTurtle;
    private Handler mHandler = new Handler();
    public Runnable counterUpAfterGame = new Runnable() {
        @Override
        public void run() {
            timeCounter.setText(Integer.toString(++counterUpTimer));
            mHandler.postDelayed(counterUpAfterGame, 1000);
        }
    };
    Runnable counterBeforeGame = new Runnable() {
        @Override
        public void run() {

            counterValueMain.setText(counterBeforeGameValue);
            counterBeforeGameValue--;

            if (counterBeforeGameValue > 0)
                mHandler.postDelayed(counterBeforeGame, 1000);
        }
    };

    Dialog rulesDialog , gameoverDialog;
    ImageView turtle; //to refactor the variable name

    public ProgressBar progressBar;
    public BigDecimal progressInt;

    CountDownTimer timer;

    Button retryButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ImageView start;

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        setContentView(R.layout.activity_main_game);

        bundle = getIntent().getExtras();

        score = findViewById(R.id.scoreTextView);
        timeCounter = findViewById(R.id.timeValueTextView);
        counterValue = findViewById(R.id.CounterTextView);
        counterValueMain = findViewById(R.id.CounterTextViewMain);
        buttonSpeedView = findViewById(R.id.buttonSpeedView);

        secondTurtle = findViewById(R.id.secondTurtleImage);
        secondTurtle.setOnClickListener(this);
        secondTurtle.setTag("closed");
        //secondTurtleBlink.run();

        mainFrameLayout = findViewById(R.id.mainGameLayout);
        progressBar = findViewById(R.id.progressbar);

        //initial values
        height = 50;
        width = 50;
        leftMargin = 1;
        topMargin = 1;
        counter = 0;
        totalButtons = 0;
        buttonsClicked = 0;

        stopRandomButtons = false;
        timeCounter.setTextColor(Color.RED);

        delayInMS = RandomButtonsConstants.INITIALDELAY;
        score.setText(Integer.toString(buttonsClicked));
        buttonSpeedView.setText(Long.toString(0));
        counterValue.setText(Integer.toString(buttonsClicked));

        start = findViewById(R.id.start);
        start.setOnClickListener(this);
        start.setImageResource(R.mipmap.start);

        /*Width
        Minimum: 50 Maximum: 450
        Height
        Minimum: 50 Maximum: 550
        Left Margin
        Minimum: 1  Maximum: 300
        Right Margin
        Minimum: 1  Minimum: 400
        */
    }

   /* private Runnable secondTurtleBlink = new Runnable() {
        @Override
        public void run() {
            if (!isSecondTurtleClicked) {
                if (secondTurtle.getTag().equals("open")) {
                    secondTurtle.setImageResource(R.mipmap.turtle_ingame_2_closed);
                    secondTurtle.setTag("closed");
                } else {
                    secondTurtle.setImageResource(R.mipmap.turtle_ingame_2);
                    secondTurtle.setTag("open");
                }

                //two blinks.. pause.. one blink.. pause..

                if (blinkDelay == 100) {
                    blinkDelay = 110;
                    //secondTurtle.setImageResource(R.mipmap.turtle_ingame_2_1);
                    //secondTurtle.setTag("closed");
                } else if (blinkDelay == 110)
                    blinkDelay = 112;
                else if (blinkDelay == 112 || blinkDelay == 810) {
                    secondTurtle.setImageResource(R.mipmap.turtle_ingame_2);
                    secondTurtle.setTag("open");
                    blinkDelay = 1800;
                } else if (blinkDelay == 1800)
                    blinkDelay = 101;
                else if (blinkDelay == 101)
                    blinkDelay = 1801;
                else if (blinkDelay == 1801)
                    blinkDelay = 100;

                mHandler.postDelayed(secondTurtleBlink, blinkDelay);
            }
        }

    };*/

    /*private Runnable wokeUpMessage = new Runnable() {
        @Override
        public void run() {

            if (!isSecondTurtleClicked) {
                isSecondTurtleClicked = true;
                secondTurtle.setImageResource(R.mipmap.turtle_ingame);
                //How can I wake my sleeping friend!
                //secondTurtleText.setText("Hey.. Not me..!");
                mHandler.postDelayed(wokeUpMessage, 2000);
            } else {
                secondTurtle.setImageResource(R.mipmap.turtle_ingame_2);
                //secondTurtleText.setText("How can I wake my sleeping friend.. Hmmm..!");

                isSecondTurtleClicked = false;
                blinkDelay = 810;
                //secondTurtleBlink.run();
            }
        }
    };*/
    private Runnable createButtonRunnable = new Runnable() {
        @Override
        public void run() {

            if (!stopRandomButtons) {
                counterValue.setText(Integer.toString(counter));

                newButton();

                totalButtons++;
                counter++;
                counterValue.setText(Integer.toString(counter));

                progressInt = new BigDecimal(counter).divide(new BigDecimal(15), 2 , RoundingMode.UP).multiply(new BigDecimal(100));
                progressBar.setProgress(progressInt.intValue());

                /*
                1. No Animation
                2. Fade in
                3. Blink Anim
                4. Dialog Animation --
                5. Bounce
                6. Blink Anim
                 */

                switch(curLevel)
                {
                    case 0:
                    {
                        if(buttonsClickedForLevelChange>10) {
                            customAnimation(mainFrameLayout, R.anim.fadein, 20500);
                            callLevelUpText();
                            curLevel = 1;
                        }
                        break;
                    }

                    case 1:
                    {
                        if(buttonsClickedForLevelChange>30) {
                            customAnimation(mainFrameLayout, R.anim.blink_anim, 2000);
                            callLevelUpText();
                            curLevel = 2;
                        }
                        break;
                    }

                    case 2:
                    {

                        if(buttonsClickedForLevelChange>50) {
                            customAnimation(mainFrameLayout, R.anim.fadein, 20500);
                            callLevelUpText();
                            curLevel = 3;
                        }
                        break;
                    }

                    case 3:
                    {
                        if(buttonsClickedForLevelChange>70) {
                            mainFrameLayout.clearAnimation();
                            customAnimation(mainFrameLayout, R.anim.blink_anim, 2000);
                            callLevelUpText();
                            curLevel = 4;
                        }
                        break;
                    }

                    case 4:
                    {
                        if(buttonsClickedForLevelChange>90){
                            customAnimation(mainFrameLayout, R.anim.fadein, 20500);
                            callLevelUpText();
                            curLevel = 5;
                        }
                        break;
                    }
                }


                if (counter < RandomButtonsConstants.GAMECOUNTERLIMIT) {

                /*
                Setting Delay from Counter Value
                if(delayInMS> 700)
                delayInMS = delayInMS-100;
                else if (delayInMS> 500)
                    delayInMS = delayInMS-50;
                else if(delayInMS > 100)
                    delayInMS = delayInMS-5;
                else if (delayInMS > 50)
                    delayInMS = delayInMS-1;
                */

                    //Setting Delay from Total Button Value
                    if (totalButtons < 5) /*900 ,800, 700, 600*/
                        delayInMS = delayInMS - 68;
                    else if (totalButtons < 10) /*550, 500, 450, 400, 350*/
                        delayInMS = delayInMS - 28;
                    else if (totalButtons < 20) /* ~200 (Approx 5 Buttons per second)  */
                        delayInMS = delayInMS - 18;
                    else
                        delayInMS--;

                    //Number of buttons per second
                    speed = (1000) / (delayInMS);

                    buttonSpeedView.setText(Long.toString(speed));
                    mHandler.postDelayed(createButtonRunnable, delayInMS);
                } else {
                    gameOver();
                }

            } else {
                if (counterValue.getText().toString().equals("0") || counter < 0)
                    gameOver();
                else
                    mHandler.postDelayed(createButtonRunnable, 20);
            }
        }
    };


    public void showRulesDialog()
    {

        Button dialogOkay;
        rulesDialog = new Dialog(this);
        rulesDialog.setContentView(R.layout.rules_dialog);

        if(rulesDialog.getWindow()!= null)
        rulesDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogOkay = rulesDialog.findViewById(R.id.dialogOkayButton);
        rulesDialog.setCancelable(false);

        Window window = rulesDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        rulesDialog.show();

        dialogOkay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                rulesDialog.dismiss();
                counterBeforeGame();
                //startGame();
            }
        }
        );
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.start: {
                removeButton(view);
                counterValueMain.setVisibility(View.VISIBLE);
                showRulesDialog();
                break;
            }
            case R.id.mainGameLayout: {
                //Negative Score
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if(v!=null)
                    v.vibrate(50);

                buttonsClicked--;
                score.setText(Integer.toString(buttonsClicked));
                counterValueMain.setText(Integer.toString(buttonsClicked));
                break;
            }
            case R.id.secondTurtleImage: {
                //wokeUpMessage.run();
                levelUp();
                break;
            }
            default: {
                //Positive Score
                removeButton(view);
                counter--;
                counterValue.setText(Integer.toString(counter));
                buttonsClicked++;
                buttonsClickedForLevelChange++;
                score.setText(Integer.toString(buttonsClicked));
                counterValueMain.setText(Integer.toString(buttonsClicked));
                break;
            }
        }
    }

    public void removeButton(View view) {
        view.clearAnimation();
        view.setVisibility(View.GONE);
        ViewGroup parentView = (ViewGroup) view.getParent();
        parentView.removeView(view);

    }

    public void callLevelUpText() {
        customToast("Level Up", Toast.LENGTH_SHORT);
        //wokeUpMessage.run();
        levelUp();
    }

    public void levelUp() {
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
                secondTurtle.setImageResource(R.mipmap.turtle_ingame);
            }

            public void onFinish() {
                secondTurtle.setImageResource(R.drawable.turtle_blink);
            }
        }.start();
    }

    public void counterBeforeGame() {
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                Long val = millisUntilFinished / 1000;
                counterValueMain.setText(Integer.toString(val.intValue()));
            }

            public void onFinish() {
                counterValueMain.setVisibility(View.GONE);
                customToast("Start" , Toast.LENGTH_SHORT);
                startGame();
                counterUpAfterGame.run();
            }
        }.start();
    }

    public void counterAfterGame() {
        new CountDownTimer(2000, 500) {

            public void onTick(long millisUntilFinished) {

                if( millisUntilFinished < 1600) {
                    if (millisUntilFinished > 1101 )
                        dialogFixedTest.setVisibility(View.VISIBLE);
                    else if (millisUntilFinished > 601)
                        retryButton.setVisibility(View.VISIBLE);
                    else if (millisUntilFinished > 1)
                        exitButton.setVisibility(View.VISIBLE);
                }
            }

            public void onFinish() {

                //Just to be sure even if the above missed to make it Visible
                dialogFixedTest.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.VISIBLE);
                exitButton.setVisibility(View.VISIBLE);

                ScoreDelegator(dialogText);
            }
        }.start();
    }

    public void gameOver() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(v!=null)
            v.vibrate(200);

        if(timer!= null)
            timer.onFinish();

        showGameOverDialog();

    }

    public void showGameOverDialog()
    {
        gameoverDialog = new Dialog(this);
        gameoverDialog.setContentView(R.layout.rules_dialog_2);
        if(gameoverDialog.getWindow()!= null)
            gameoverDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //turtle = gameoverDialog.findViewById(R.id.turtle);
        //turtle.setTag("open");
        //turtleBlink.run();



        dialogText = gameoverDialog.findViewById(R.id.rulesText);
        dialogFixedTest = gameoverDialog.findViewById(R.id.gameoverfixed);

        retryButton = gameoverDialog.findViewById(R.id.dialogRetryButton);
        exitButton = gameoverDialog.findViewById(R.id.dialogExitButton);

        retryButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
        dialogFixedTest.setVisibility((View.GONE));

        gameoverDialog.setCancelable(false);

        Window window = gameoverDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        gameoverDialog.show();

        retryButton.setOnClickListener(new View.OnClickListener(){
                                          @Override
                                          public void onClick(View view)
                                          {
                                              gameoverDialog.dismiss();
                                              Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
                                              startActivity(intent);
                                          }
                                      }
        );

        exitButton.setOnClickListener(new View.OnClickListener(){
                                           @Override
                                           public void onClick(View view)
                                           {
                                               Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                               startActivity(intent);
                                               gameoverDialog.dismiss();
                                           }
                                       }
        );

        counterAfterGame();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        stopRandomButtons= true;
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    public void ScoreDelegator(TextView dialogText){

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int highScoreEasy = prefs.getInt("easy", 0); //0 is the default value
        int finalScore = Integer.parseInt(score.getText().toString());
        String finalScoreString ="Score: " + finalScore +"\n" +"High Score: " +highScoreEasy;

        if(finalScore>highScoreEasy)
        {
            Editor editor = prefs.edit();
            editor.putInt("easy", finalScore);
            editor.apply();
            customToast("Meet the new Champion! High Score! ", Toast.LENGTH_LONG);
        }
        else
            customToast( "I was so close to becoming the world champion.. So close..!" ,Toast.LENGTH_LONG);

        dialogText.setText(finalScoreString);
    }

    /*private Runnable turtleBlink = new Runnable() {
        @Override
        public void run() {
            if (turtle.getTag().equals("open")) {
                turtle.setImageResource(R.drawable.turtle_highscore_fullblink);
                turtle.setTag("closed");
            } else {
                turtle.setImageResource(R.drawable.turtle_highscore_halfblink);
                turtle.setTag("open");
            }

            //two blinks.. pause.. one blink.. pause..

            if (blinkDelay == 100) {
                blinkDelay = 110;
            } else if (blinkDelay == 110)
                blinkDelay = 112;
            else if (blinkDelay == 112 || blinkDelay == 810) {
                turtle.setImageResource(R.drawable.turtle_highscore_halfblink);
                turtle.setTag("open");
                blinkDelay = 1800;
            } else if (blinkDelay == 1800)
                blinkDelay = 101;
            else if (blinkDelay == 101)
                blinkDelay = 1801;
            else if (blinkDelay == 1801)
                blinkDelay = 100;

            mHandler.postDelayed(turtleBlink, blinkDelay);

        }
    };*/

    public void customToast(String message, int length ) {
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    public void startGame() {
        mainFrameLayout.setOnClickListener(this);
        createButtonRunnable.run();
    }

    public void animate(Button button) {
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.fadein);

        anim.setDuration(400);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }

    public void anim(Button button) {
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.rotate_and_zoom);

        anim.setDuration(40);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }


    @SuppressLint("ClickableViewAccessibility")
    public void newButton() {
        final Button button = new Button(this);
        button.setOnClickListener(this);

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

        height = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(220) + 50) * 0.5) + 0.5f);
        width = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(220) + 50) * 0.5) + 0.5f);

        leftMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(320) + 10) * 0.8) + 0.5f);
        topMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(460) + 10) * 0.8) + 0.5f);

        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

       // button.setBackgroundColor(color);

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius( 12 );
        shape.setStroke(5,Color.BLACK);

        shape.setColor(color);

        button.setBackground(shape);

        LayoutParams layoutparams = new LinearLayout.LayoutParams(width, height);
        layoutparams.setMargins(leftMargin, topMargin, 0, 0);

        mainFrameLayout.addView(button, layoutparams);
    }



    public  void customAnimation (FrameLayout layout , int animType , int duration ){

        Animation anim = AnimationUtils.loadAnimation(this, animType);
        anim.setDuration(duration);
        anim.setRepeatCount(Animation.INFINITE);
        layout.startAnimation(anim);

    }
}



