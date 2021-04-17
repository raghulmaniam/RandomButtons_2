package com.mycloset.raghul.randombuttons_2;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.SharedPreferences.Editor;

public class SequenceButtons extends Activity implements View.OnClickListener {

      /*

Developer: Raghul Subramaniam
Email: raghulmaniam@gmail.com

 */

    Dialog rulesDialog , gameoverDialog;
    private FrameLayout mainFrameLayout;

    private TextView score, timeCounter;

    Button b1,b2,b3;

    //int curSeq = 0;
    AtomicInteger curSeq = new AtomicInteger();

    AtomicInteger buttonSeq = new AtomicInteger();

    int height, width, leftMargin, topMargin;

    Random rnd = new Random();

    public ProgressBar progressBar;
    public BigDecimal progressInt;

    int timer_seconds = 0;
    TextView dialogText;

    Button retryButton, exitButton;

    Vibrator v;

    private Handler mHandler = new Handler();
    public Runnable timer = new Runnable() {
        @Override
        public void run() {
            progressInt = new BigDecimal(timer_seconds++).divide(new BigDecimal(60), 2 , RoundingMode.UP).multiply(new BigDecimal(100));
            progressBar.setProgress(progressInt.intValue());

            timeCounter.setText(Integer.toString(timer_seconds));

            if(timer_seconds<=60)
            mHandler.postDelayed(timer, 1000);
            else
            {
                gameOver();
            }

        }
    };

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ImageView start;

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        setContentView(R.layout.activity_sequence_buttons);

        curSeq.set(1);
        buttonSeq.set(3);


        //start = findViewById(R.id.start);
        //start.setOnClickListener(this);
        //start.setImageResource(R.mipmap.start);


        b1 = findViewById(R.id.seq_button1);
        b2 = findViewById(R.id.seq_button2);
        b3 = findViewById(R.id.seq_button3);


        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);

        b1.setTag(1);
        b2.setTag(2);
        b3.setTag(3);

        score = findViewById(R.id.seq_CounterTextView);
        timeCounter = findViewById(R.id.seq_timeText);

        progressBar = findViewById(R.id.seq_progressbar);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        showRulesDialog();



    }

    @Override
    public void onClick(View view) {

        if(curSeq.get() == (Integer)view.getTag())
        {
            newButton();
            removeButton(view);
            curSeq.incrementAndGet();
            score.setText(Integer.toString(curSeq.get()));
        }
        else
        {
            customToast("GameOver" , Toast.LENGTH_SHORT);
        }
    }

    public void customToast(String message, int length ) {
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void newButton() {
        final Button button = new Button(this);

        button.setTag(buttonSeq.incrementAndGet());
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

        //height = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(220) + 50) * 0.5) + 0.5f);
        //width = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(220) + 50) * 0.5) + 0.5f);

        leftMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(320) + 10) * 0.8) + 0.5f);
        topMargin = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(460) + 10) * 0.8) + 0.5f);

        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        // button.setBackgroundColor(color);

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius( 12 );
        shape.setStroke(5,Color.BLACK);

        shape.setColor(color);

        button.setBackground(shape);

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(250, 250);
        layoutparams.setMargins(leftMargin, topMargin, 0, 0);

        mainFrameLayout.addView(button, layoutparams);
    }

    public void animate(Button button) {
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.fadein);

        anim.setDuration(400);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }

    public void removeButton(View view) {
        view.clearAnimation();
        view.setVisibility(View.GONE);
        ViewGroup parentView = (ViewGroup) view.getParent();
        parentView.removeView(view);

    }

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
                                              timer.run();
                                              startGame();
                                          }
                                      }
        );
    }


    public void startGame() {

        buttonsBeforeGame();

        //mainFrameLayout.setOnClickListener(this);

    }


    public void buttonsBeforeGame() {
        new CountDownTimer(2000, 500) {

            public void onTick(long millisUntilFinished) {

                if( millisUntilFinished < 1600) {
                    if (millisUntilFinished > 1101 )
                        b1.setVisibility(View.VISIBLE);
                    else if (millisUntilFinished > 601)
                        b2.setVisibility(View.VISIBLE);
                    else if (millisUntilFinished > 1)
                        b3.setVisibility(View.VISIBLE);
                }
            }

            public void onFinish() {

                //Just to be sure even if the above missed to make it Visible
                b1.setVisibility(View.VISIBLE);
                b2.setVisibility(View.VISIBLE);
                b3.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void gameOver() {

        if(v!=null)
            v.vibrate(200);

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

        retryButton = gameoverDialog.findViewById(R.id.dialogRetryButton);
        exitButton = gameoverDialog.findViewById(R.id.dialogExitButton);

        retryButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);

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
                                               Intent intent = new Intent(getApplicationContext(), SequenceButtons.class);
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

    public void counterAfterGame() {
        new CountDownTimer(2000, 500) {

            public void onTick(long millisUntilFinished) {

                if( millisUntilFinished < 1600) {
                    if (millisUntilFinished > 1101 ) {
                     //   retry.setVisibility(View.VISIBLE);
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

                ScoreDelegator(dialogText);
            }
        }.start();
    }

    public void ScoreDelegator(TextView dialogText){

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
            int highScoreEasy = prefs.getInt("seq", 0); //0 is the default value
        int finalScore = Integer.parseInt(score.getText().toString());
        String finalScoreString ="Score: " + finalScore +"\n" +"High Score: " +highScoreEasy;

        if(finalScore>highScoreEasy)
        {
            Editor editor = prefs.edit();
            editor.putInt("seq", finalScore);
            editor.apply();
            customToast("Meet the new Champion! High Score! ", Toast.LENGTH_LONG);
        }
        else
            customToast( "I was so close to becoming the world champion.. So close..!" ,Toast.LENGTH_LONG);

        dialogText.setText(finalScoreString);
    }

}


