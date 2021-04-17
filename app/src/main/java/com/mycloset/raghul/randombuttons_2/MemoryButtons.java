package com.mycloset.raghul.randombuttons_2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryButtons extends AppCompatActivity implements View.OnClickListener {

    int curLevel = 1 , buttonCount =5;

    AtomicInteger score = new AtomicInteger();

    Dialog rulesDialog , gameoverDialog;

    private TextView counterValueMain;

    private FrameLayout mainFrameLayout;

    volatile public Boolean gameOver= false;

    int leftMargin, topMargin;

    Random rnd = new Random();

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

        mainFrameLayout.setOnClickListener(this);

        score.set(0);


        showRulesDialog();



    }

    @Override
    public void onClick(View view) {
        view.setVisibility(View.VISIBLE);
        if(view.getTag() == 1)
        {
            score.incrementAndGet();
        }
        else
        {
            score.decrementAndGet();
            score.decrementAndGet();
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
                counterValueMain.setVisibility(View.GONE);
                customToast("Start" , Toast.LENGTH_SHORT);
                startGame();
            }
        }.start();
    }

    public void countdownAfterGame() {
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                Long val = millisUntilFinished / 1000;
                counterValueMain.setText(Integer.toString(val.intValue()));
            }

            public void onFinish() {
                counterValueMain.setVisibility(View.GONE);
                customToast("Start" , Toast.LENGTH_SHORT);
                startGame();
            }
        }.start();
    }

    public void startGame() {

        //curLevel = 1;
        //buttonCount = 5;

        while(!gameOver)
        {
          for(int i = 0 ; i <buttonCount ; i++)
              newButton(Color.WHITE);
        }

        newButton(Color.BLACK);
        newButton(Color.BLACK);

        countdownAfterGame();


    }

    @SuppressLint("ClickableViewAccessibility")
    public void newButton(int color) {
        final Button button = new Button(this);
        button.setOnClickListener(this);

        if(color == Color.WHITE)
            button.setTag(1);
        else if(color == Color.BLACK)
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
