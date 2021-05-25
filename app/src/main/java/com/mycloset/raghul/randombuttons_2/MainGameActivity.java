package com.mycloset.raghul.randombuttons_2;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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

    //private TextView score, timeCounter,counterValue,counterValueMain,buttonSpeedView ;
    //private TextView score;
    TextView dialogText;
    private FrameLayout mainFrameLayout;

    private int counter, totalButtons,delayInMS , width,height ,leftMargin,topMargin ;
    private AtomicInteger totalScore = new AtomicInteger(0);


    private Random rnd = new Random();
    private int layoutHeight, layoutWidth;
    private Handler mHandler = new Handler();
    private Dialog rulesDialog , gameOverDialog;
    private ProgressBar progressBar;

//    private CountDownTimer timer;
    private CountDownTimer countDownBefore;
    private Button retryButton, exitButton;
    private Button scoreButton;
    volatile private Boolean gameOver = false;
    volatile private Boolean backPressed = false;

    MediaPlayer clickSound = null;
    MediaPlayer defaultSound = null;
    MediaPlayer exitSound = null;
    MediaPlayer smallClapsSound = null;
    MediaPlayer largeClapsSound = null;
    MediaPlayer highScoreSound = null;
    MediaPlayer grooveSound = null;


    List<Integer> animList = new ArrayList<>();

    private int animListSize;

    private int grooveSoundListSize;
    List<MediaPlayer> grooveSoundList = new ArrayList<>();

    volatile private Boolean isModeGroovy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        int curMode;
        int INITIAL_DELAY = 1000;
        //Bundle bundle;

        //ImageView start;

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        //initial values
        height = 50;
        width = 50;
        leftMargin = 1;
        topMargin = 1;
        counter = 0;
        totalButtons = 0;


        setContentView(R.layout.activity_main_game);

        //bundle = getIntent().getExtras();

        scoreButton = findViewById(R.id.scoreButton);
        scoreButton.setText(String.format(Locale.getDefault(), "%d" , totalScore.get()));

        scoreButton.setVisibility(View.INVISIBLE);

        mainFrameLayout = findViewById(R.id.mainGameLayout);
        progressBar = findViewById(R.id.progressbar);

        delayInMS = INITIAL_DELAY;

        //bulletSound1 = MediaPlayer.create(this, R.raw.click3);
        clickSound = MediaPlayer.create(this, R.raw.rand_click_wav);
        defaultSound = MediaPlayer.create(this, R.raw.default_sound);
        exitSound = MediaPlayer.create(this, R.raw.exit_sound);
        highScoreSound = MediaPlayer.create(this, R.raw.new_high_score);
        smallClapsSound = MediaPlayer.create(this, R.raw.claps_small);
        largeClapsSound = MediaPlayer.create(this, R.raw.claps_large);

        Intent intent = getIntent();
        curMode = intent.getIntExtra("mode", 1);

        if(curMode == 2)
            isModeGroovy = true;

        if(isModeGroovy) {
            animList.add(R.anim.enter_fron_left);
            animList.add(R.anim.enter_from_right);
            animList.add(R.anim.bounce);
            animList.add(R.anim.fadein);
            animList.add(R.anim.dialog_anim);
            animList.add(R.anim.rotate_right);
            animList.add(R.anim.rotate_left);

            animList.add(R.anim.rotate_right); //adding again to increase the probability
            animList.add(R.anim.rotate_left); //adding again to increase the probability

            animList.add(R.anim.zoomout);
            animList.add(R.anim.dialog_anim_down);

            animListSize = animList.size();

            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove1));
            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove2));
            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove3));
            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove4));
            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove5));
            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove6));
            grooveSoundList.add(MediaPlayer.create(this, R.raw.groove7));

            grooveSoundListSize = grooveSoundList.size();

        }

        /*Width
        Minimum: 50 Maximum: 450
        Height
        Minimum: 50 Maximum: 550
        Left Margin
        Minimum: 1  Maximum: 300
        Right Margin
        Minimum: 1  Minimum: 400
        */

        showRulesDialog();

    }



    private Runnable createButtonRunnable = new Runnable() {

        private int GAME_COUNTER_LIMIT = 15;
        private BigDecimal progressInt;
        //private long speed;

        @Override
        public void run() {

            if (!gameOver) {

                newButton();

                totalButtons++;
                counter++;

                progressInt = new BigDecimal(counter).divide(new BigDecimal(15), 2, RoundingMode.UP).multiply(new BigDecimal(100));
                progressBar.setProgress(progressInt.intValue());

                /*
                1. No Animation
                2. Fade in
                3. Blink Anim
                4. Dialog Animation --
                5. Bounce
                6. Blink Anim
                 */

                if (counter < GAME_COUNTER_LIMIT) {

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
                    //speed = (1000) / (delayInMS);

                    mHandler.postDelayed(createButtonRunnable, delayInMS);
                } else {
                    gameOver = true;
                    gameOver();
                }

            } else {
                if (/*counterValue.getText().toString().equals("0") ||*/ counter < 0) {
                    gameOver = true;
                    gameOver();
                } else
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
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        rulesDialog.show();

        dialogOkay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {

                layoutHeight = mainFrameLayout.getMeasuredHeight();
                layoutWidth = mainFrameLayout.getMeasuredWidth();

                rulesDialog.dismiss();
                scoreButton.setVisibility(View.VISIBLE);
                //counterBeforeGame();
                startGame();
            }
        }
        );
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.mainGameLayout)
        {
            //Negative Score
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if(v!=null)
                v.vibrate(50);
            totalScore.decrementAndGet();

        }
        else
        {
            //Positive Score
            removeButton(view);
            zoomEffectScoreButton(scoreButton);

            if(totalScore.get() == 100)
                smallClapsSound.start();

            scoreButton.setText(String.format(Locale.getDefault(), "%d" , totalScore.get()));
            if(isModeGroovy){
                //anim = AnimationUtils.loadAnimation(this, animList.get(rnd.nextInt(animListSize )));
                grooveSound = grooveSoundList.get(rnd.nextInt(grooveSoundListSize));
                if(grooveSound!= null)
                    grooveSound.start();
            }
            else {
                if (clickSound != null)
                    clickSound.start();
            }

            counter--;
            totalScore.incrementAndGet();
        }
    }

    public void removeButton(View view) {
        view.clearAnimation();
        view.setVisibility(View.GONE);
        ViewGroup parentView = (ViewGroup) view.getParent();
        parentView.removeView(view);

    }

    public void counterAfterGame() {
        new CountDownTimer(2000, 500) {

            public void onTick(long millisUntilFinished) {

                if( millisUntilFinished < 1600) {
                    /*if(millisUntilFinished > 1101 ) {
                        //retry.setVisibility(View.VISIBLE);
                    }
                    else */if (millisUntilFinished > 601)
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
                    delegateScores(dialogText);
            }
        }.start();
    }

    public void gameOver() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(v!=null)
            v.vibrate(200);

        /*if(timer!= null)
            timer.onFinish();*/

        showGameOverDialog();
    }

    public void showGameOverDialog()
    {
        gameOverDialog = new Dialog(this);
        gameOverDialog.setContentView(R.layout.rules_dialog_2);
        if(gameOverDialog.getWindow()!= null)
            gameOverDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogText = gameOverDialog.findViewById(R.id.rulesText);

        retryButton = gameOverDialog.findViewById(R.id.dialogRetryButton);
        exitButton = gameOverDialog.findViewById(R.id.dialogExitButton);

        retryButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);

        gameOverDialog.setCancelable(false);

        Window window = gameOverDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        gameOverDialog.show();

        retryButton.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view)
          {
              gameOverDialog.dismiss();

              if(defaultSound!= null)
                  defaultSound.start();

              Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
              if(isModeGroovy)
                  intent.putExtra("mode" , 2); //groovy mode
              else
                  intent.putExtra("mode" , 1); //classic mode

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

            gameOverDialog.dismiss();
        }
        }
        );

        counterAfterGame();
    }

    @Override
    public void onBackPressed()
    {
        backPressed = true;

        //to stop all the running threads of this game
        if(countDownBefore != null) {
            countDownBefore.cancel();
            countDownBefore= null;
        }

        if(exitSound!= null)
            exitSound.start();

        super.onBackPressed();
        gameOver = true;
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(R.anim.enter_fron_left, R.anim.exit_out_right);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //Log.d(TAG, "MYonStop is called");

        MusicManager.getInstance().stopPlaying();
    }


    public void delegateScores(TextView dialogText){

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int highScoreEasy = prefs.getInt("easy", 0); //0 is the default value
        //int finalScore = totalScore.get();
        String finalScoreString ="Score: " + totalScore.get() +"\n" +"High Score: " +highScoreEasy;

        if(totalScore.get()>highScoreEasy)
        {

            //new high score
            Editor editor = prefs.edit();
            editor.putInt("easy", totalScore.get());
            editor.apply();
            customToast("Meet the new Champion! High Score! ", Toast.LENGTH_LONG);

            if(highScoreSound!= null)
                highScoreSound.start();

            ImageView star = gameOverDialog.findViewById(R.id.mem_star);
            star.setVisibility(View.VISIBLE);
            rotate(star);

            if(largeClapsSound!= null)
                largeClapsSound.start();
        }
        /*else {
            //customToast( "I was so close to becoming the world champion.. So close..!" ,Toast.LENGTH_LONG);
        }*/

        dialogText.setText(finalScoreString);
    }

    public void rotate(View view) {
        Animation anim;
        anim = AnimationUtils.loadAnimation(this, R.anim.zoomin_fade);
        anim.setDuration(1000);
        anim.setRepeatCount(600);
        view.startAnimation(anim);
    }

    public void customToast(String message, int length ) {
        Toast.makeText(getApplicationContext(), message, length).show();
    }

    public void startGame() {
        mainFrameLayout.setOnClickListener(this);

        blink_anim(mainFrameLayout, 3000);

        createButtonRunnable.run();
    }

    public void blink_anim(FrameLayout layout, int duration) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        anim.setDuration(duration);
        anim.setRepeatCount(Animation.INFINITE);
        layout.startAnimation(anim);
    }

    public void zoomEffectScoreButton(Button button) {
        Animation anim;

        anim = AnimationUtils.loadAnimation(this, R.anim.zoomin_fade);

        anim.setDuration(500);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }


    public void animate(Button button) {
        Animation anim;

        if(isModeGroovy)
            anim = AnimationUtils.loadAnimation(this, animList.get(rnd.nextInt(animListSize )));
        else
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

        height = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(220) + 70) * 0.5) + 0.5f);
        width = (int) (((getResources().getDisplayMetrics().density) * (randomParam.nextInt(220) + 70) * 0.5) + 0.5f);

        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(12);
        shape.setStroke(5, Color.BLACK);

        shape.setColor(color);
        button.setBackground(shape);

        leftMargin = randomParam.nextInt(layoutWidth-width);
        topMargin = randomParam.nextInt(layoutHeight-height-240) +240;
        /* 240 - > to negate the height till the progress bar*/

        LayoutParams layoutparams = new LinearLayout.LayoutParams(width, height);
        layoutparams.setMargins(leftMargin, topMargin, 0, 0);

        mainFrameLayout.addView(button, layoutparams);
    }

   /* public  void customAnimation (FrameLayout layout , int animType , int duration ){
        Animation anim = AnimationUtils.loadAnimation(this, animType);
        anim.setDuration(duration);
        anim.setRepeatCount(Animation.INFINITE);
        layout.startAnimation(anim);
    }*/
}




