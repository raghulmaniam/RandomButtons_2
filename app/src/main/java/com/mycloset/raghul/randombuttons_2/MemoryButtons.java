package com.mycloset.raghul.randombuttons_2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

public class MemoryButtons extends AppCompatActivity implements View.OnClickListener {

    int curLevel = 1 , buttonsCount =4;

    AtomicInteger score = new AtomicInteger();

    Dialog rulesDialog , gameoverDialog;

    private TextView counterValueMain;

    private FrameLayout mainFrameLayout ;
    private  RelativeLayout bulbLayout;

    volatile public Boolean gameOver= false, bulbOn = false;

    int leftMargin, topMargin;

    Random rnd = new Random();

    private Handler mHandler = new Handler();

    ImageView bulb;

    Button odd1, odd2;

    List<Button> buttonsList = new ArrayList<>();

    TextView scoreText;

    public ProgressBar progressBarTime;
    public ProgressBar progressBarButtons;
    public BigDecimal timeProgressInt;
    public BigDecimal buttonsProgressInt;

    int timer_seconds = 0;
    AtomicInteger correctButtonsClicked = new AtomicInteger();

TextView curLevelText;


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

        mainFrameLayout.setOnClickListener(this);

        score.set(0);
        correctButtonsClicked.set(5); //dont make it zero, first time check will fail

        bulb = findViewById(R.id.mem_bulb);

        scoreText = findViewById(R.id.mem_CounterTextView);

        scoreText.setText(Integer.toString(0));

        progressBarButtons = findViewById(R.id.mem_progressbar_buttons);
        progressBarTime = findViewById(R.id.mem_progressbar_time);

        curLevelText = findViewById(R.id.mem_levelText);

        showRulesDialog();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.mem_mainGameLayout: {
                score.decrementAndGet();
                scoreText.setText(Integer.toString(score.get()));
                break;
            }
            default: {

                GradientDrawable shape =  new GradientDrawable();
                shape.setCornerRadius( 12 );
                shape.setStroke(5,Color.BLACK);

                view.clearAnimation();
                view.bringToFront();
                view.setOnClickListener(null);

                view.setVisibility(View.VISIBLE);

                if ((int) view.getTag() == 1) {
                    //correct button

                    buttonsProgressInt = new BigDecimal(correctButtonsClicked.incrementAndGet()).divide(new BigDecimal(buttonsCount), 2 , RoundingMode.UP).multiply(new BigDecimal(100));
                    progressBarButtons.setProgress(buttonsProgressInt.intValue());

                    score.incrementAndGet();
                    shape.setColor(Color.WHITE);


                } else {
                    //wrong button
                    score.decrementAndGet();
                    score.decrementAndGet();
                    shape.setColor(Color.RED);
                }

                view.setBackground(shape);
                scoreText.setText(Integer.toString(score.get()));

            }
        }
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
                                              counterBeforeGame();
                                          }
                                      }
        );
    }

    public void counterBeforeGame() {
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                Long val = millisUntilFinished / 1000;
                counterValueMain.setText(Integer.toString(val.intValue()));
            }

            public void onFinish() {
                counterValueMain.setVisibility(View.INVISIBLE);
                customToast("Start" , Toast.LENGTH_SHORT);
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
                customToast("Start" , Toast.LENGTH_SHORT);
                startGame();
            }
        }.start();
    }

    public void startGame() {

        //curLevel = 1;
        //buttonCount = 5;

       /* while(!gameOver)
        {
          for(int i = 0 ; i <buttonCount ; i++)
              newButton(Color.WHITE);
        }

        newButton(Color.BLACK);
        newButton(Color.BLACK);

        countdownAfterGame();*/
        createButtonRunnable.run();

    }

    private Runnable createButtonRunnable = new Runnable() {
        @Override
        public void run() {

            if(bulbOn)
            {
                bulb.setImageResource(R.drawable.bulb_off);
                bulbOn = false;
                bulbLayout.setBackgroundResource(R.drawable.box_curved);

                setBaseColor(buttonsList);

                timer_seconds = 0;
                timer.run();


                mHandler.postDelayed(createButtonRunnable, 10000);
            }
            else
            {
                progressBarTime.setProgress(0);
                progressBarButtons.setProgress(0);

                if(correctButtonsClicked.get() < buttonsCount)
                {
                    gameOver = true;

                    mainFrameLayout.setOnClickListener(null);
                    gameover();
                }
                else {
                    curLevelText.setText(Integer.toString(curLevel++));
                    buttonsCount++;

                    progressBarTime.setProgress(0);
                    progressBarButtons.setProgress(0);

                    clearButtons(buttonsList);
                    bulb.setImageResource(R.drawable.bulb_on);
                    bulbOn = true;
                    bulbLayout.setBackgroundResource(R.drawable.curve2);

                    buttonsList.add(newButton(Color.RED));
                    buttonsList.add(newButton(Color.RED));



                    for (int i = 0; i < buttonsCount; i++)
                        buttonsList.add(newButton(Color.WHITE));

                    correctButtonsClicked.set(0);



                    mHandler.postDelayed(createButtonRunnable, 5000);
                    countdownAfterGame();
                }
            }
            //mHandler.postDelayed(createButtonRunnable, 5000);

        }
    };

    public Runnable timer = new Runnable() {
        @Override
        public void run() {
            timeProgressInt = new BigDecimal(timer_seconds++).multiply(new BigDecimal(10)); //for 10 seconds
            progressBarTime.setProgress(timeProgressInt.intValue());

            if(timer_seconds<=10)
                mHandler.postDelayed(timer, 1000);

        }
    };

    public  void gameover()
    {
        customToast("Game Over", Toast.LENGTH_SHORT);
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
            GradientDrawable shape =  new GradientDrawable();
            shape.setCornerRadius( 12 );
            shape.setStroke(5,Color.BLACK);
            shape.setColor(Color.BLACK);

            button.setBackground(shape);

            /*button.clearAnimation();
            button.setVisibility(View.INVISIBLE);*/
            button.setOnClickListener(this);

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    public Button newButton(int color) {
        Button button = new Button(this);

        if(color == Color.WHITE)
            button.setTag(1);
        else if(color == Color.RED)
            button.setTag(2);

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

        //int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        // button.setBackgroundColor(color);

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius( 12 );
        shape.setStroke(5,Color.BLACK);

        shape.setColor(color);

        button.setBackground(shape);

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(250, 250);
        layoutparams.setMargins(leftMargin, topMargin, 0, 0);



        mainFrameLayout.addView(button, layoutparams);

        return button;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

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


