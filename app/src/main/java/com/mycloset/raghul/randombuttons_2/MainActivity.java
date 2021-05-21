package com.mycloset.raghul.randombuttons_2;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.graphics.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.appcompat.app.AlertDialog;

import java.util.Random;

import androidx.annotation.NonNull;
import hotchemi.android.rate.AppRate;

public class MainActivity extends Activity implements View.OnClickListener {

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
    private  Boolean bulbOn, stopBulbAnim;
    private TextView titleText;
    private Integer blinkDelay = 3000;

    MediaPlayer defaultSound = null;
    MediaPlayer exitSound = null;

    MediaPlayer lightOn = null;
    MediaPlayer lightOff = null;

    private static final int RC_SIGN_IN = 9001;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = "RandomButtons";

    // Client used to interact with Google Snapshots.
    private SnapshotsClient mSnapshotsClient = null;

    LeaderboardsClient mLeaderboardsClient;

    String dialogMessage;

    boolean internetConnectivity = false;

    boolean startupDone = false;

    boolean googleClicked = false;

    private String DIALOG_SIZE_SMALL="small";
    private String DIALOG_SIZE_LARGE="large";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppRate.with(this)
                .setInstallDays(0)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        ImageView enter,about, highScore ,exit, globalRank, signUp;

        //--- To set Full Screen mode ---
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--- To set Full Screen mode ---

        bulbOn = true;
        stopBulbAnim = false;

        setContentView(R.layout.activity_main);
        enter = findViewById(R.id.enter);
        about = findViewById(R.id.about);
        highScore = findViewById(R.id.score);
        signUp = findViewById(R.id.googleLogin);
        globalRank = findViewById(R.id.globalHighscore);
        exit = findViewById(R.id.exit);
        bulb = findViewById(R.id.bulb);
        homeScreen = findViewById(R.id.homescreen);
        mainFrameLayout = findViewById(R.id.dummyButtonLayout);
        titleText = findViewById(R.id.titleText);

        defaultSound = MediaPlayer.create(this, R.raw.default_sound);
        exitSound = MediaPlayer.create(this, R.raw.exit_sound);

        lightOn = MediaPlayer.create(this, R.raw.light_on_sound);
        lightOff = MediaPlayer.create(this, R.raw.light_off_sound);

        enter.setOnClickListener(this);
        exit.setOnClickListener(this);
        highScore.setOnClickListener(this);
        about.setOnClickListener(this);
        titleText.setOnClickListener(this);
        bulb.setOnClickListener(this);
        signUp.setOnClickListener(this);
        globalRank.setOnClickListener(this);

        createButtonRunnable.run();
        //bulbBlink.run();
        zoom_in(mainFrameLayout, 1000);

        bulb.setImageResource(R.drawable.bulb_off);
        bulbOn = false;
        homeScreen.setBackgroundResource(R.drawable.box_curved);

        mHandler.postDelayed(bulbOnRunnable, 500);

        // Create the client used to sign in.
       /* mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        // Since we are using SavedGames, we need to add the SCOPE_APPFOLDER to access Google Drive.
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .build());*/

        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail().build());




        //mGoogleSignInClient = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail().build();

        bulbOn = false;

        /*// Create the client used to sign in to Google services.
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();
    }

    /**
     * Try to sign in without displaying dialogs to the user.
     * <p>
     * If the user has already signed in previously, it will not show dialog.
     */
    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");

                            //dialogMessage = "Sign in Successful";
                            //showDialogBox(dialogMessage, DIALOG_SIZE_SMALL);

                            onConnected(task.getResult());

                            if(startupDone && googleClicked)
                            {
                                googleClicked = false;

                                dialogMessage = "Sign in Successful";
                                showDialogBox(dialogMessage, DIALOG_SIZE_SMALL);
                            }
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            //onDisconnected();

                            if(startupDone) {
                                dialogMessage = "Sign in Cancelled (or) failed. Please check the Network connection or try again later";
                                showDialogBox(dialogMessage, DIALOG_SIZE_SMALL);
                            }
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                //onDisconnected();

                if(message!= null && !message.isEmpty() && !((message.equals("12501: ") || (message.equals("4: ")) ) /*12501 and 4 error message is suppressed*/)) {
                    new AlertDialog.Builder(this)
                            .setMessage(message)
                            .setNeutralButton(android.R.string.ok, null)
                            .show();
                }
            }
        }
    }

    /*private void onDisconnected() {

        Log.d(TAG, "onDisconnected() -- test");

        mSnapshotsClient = null;
        //showSignInBar();
    }*/

    // The currently signed in account, used to check the account has changed outside of this activity when resuming.
    GoogleSignInAccount mSignedInAccount = null;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");

        mLeaderboardsClient = Games.getLeaderboardsClient(this, googleSignInAccount);

        if (mSignedInAccount != googleSignInAccount) {

            mSignedInAccount = googleSignInAccount;

            onAccountChanged(googleSignInAccount);
        } else {
            Toast.makeText(getApplicationContext()," no idea what to print here :)" , Toast.LENGTH_SHORT);
        }
    }

    private void onAccountChanged(GoogleSignInAccount googleSignInAccount) {
        mSnapshotsClient = Games.getSnapshotsClient(this, googleSignInAccount);

        // Sign-in worked!
        log("Sign-in successful! Loading game state from cloud.");

        //showSignOutBar();

        //showSnapshots(getString(R.string.title_load_game), false, false);
    }

    /**
     * Shows the user's snapshots.
     */
    /*void showSnapshots(String title, boolean allowAdd, boolean allowDelete) {
        int maxNumberOfSavedGamesToShow = 5;

        startActivityForResult(task.getResult(), RC_LIST_SAVED_GAMES);

        SnapshotCoordinator.getInstance().getSelectSnapshotIntent(
                mSnapshotsClient, title, allowAdd, allowDelete, maxNumberOfSavedGamesToShow)
                .addOnCompleteListener(new OnCompleteListener<Intent>() {
                    @Override
                    public void onComplete(@NonNull Task<Intent> task) {
                        if (task.isSuccessful()) {
                            startActivityForResult(task.getResult(), RC_LIST_SAVED_GAMES);
                        } else {
                            handleException(task.getException(), getString(R.string.show_snapshots_error));
                        }
                    }
                });
    }*/


    public void onShowLeaderboardsRequested() {

        //Log.d(TAG, "show leader board -- test");

        //Log.d(TAG, "show leader board -- test2");

        mLeaderboardsClient.getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        //Log.d(TAG, "show leader board -- success");
                        startActivityForResult(intent, 5001);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        handleException(e, getString(R.string.leaderboards_exception));
                        //Log.d(TAG, "Exception -- test");
                        //Log.d(TAG, "Exception -- test");
                    }
                });
        //Log.d(TAG, "show leader board -- end");
    }

    private void handleException(Exception e, String details) {
        int status = 0;
        //Log.d(TAG, "Exception -- handleException");
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            status = apiException.getStatusCode();
        }

        String message = getString(R.string.status_exception_error, details, status, e);

        if(message!= null && !message.isEmpty()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(message)
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        }

    }

        /**
         * Prints a log message (convenience method).
         */
    void log(String message) {
        Log.d(TAG, message);
    }

    public void newButton() {
        Button button = new Button(this);

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
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.move);

        anim.setDuration(1000);
        anim.setRepeatCount(1);

        button.startAnimation(anim);
    }

    private Runnable bulbOnRunnable = new Runnable(){
        @Override
        public void run()
        {
            if(!bulbOn)
            {
                if(lightOn!= null)
                    lightOn.start();

                bulb.setImageResource(R.drawable.bulb_on);
                bulbOn = true;
                homeScreen.setBackgroundResource(R.drawable.curve_intro);
            }
        }

    };

    /*private Runnable bulbBlink = new Runnable() {
        @Override
        public void run() {

                if (!bulbOn) {
                    bulb.setImageResource(R.drawable.bulb_on);
                    bulbOn = true;
                    homeScreen.setBackgroundResource(R.drawable.curve_intro);
                } else {
                    bulb.setImageResource(R.drawable.bulb_off);
                    bulbOn = false;
                    homeScreen.setBackgroundResource(R.drawable.curve_intro);
                }

                //two blinks.. pause.. one blink.. pause..

                if (blinkDelay == 3000) {
                    blinkDelay = 1001;
                } else if (blinkDelay == 1001) {
                    blinkDelay = 50;
                }
                 else if (blinkDelay == 50) {
                    blinkDelay = 45;
                }
                 else if (blinkDelay == 45)
                     blinkDelay = 46;
                 else if(blinkDelay == 46)
                {
                    blinkDelay = 1;
                }
                 else if (blinkDelay ==1)
                {
                    stopBulbAnim = true;
                }
                 //else if(blinkDelay == 4200)
                   // mHandler.removeCallbacks(bulbBlink);

            if(!stopBulbAnim)
                mHandler.postDelayed(bulbBlink, blinkDelay);
            }
    };*/


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.enter: {

                if(defaultSound!= null)
                    defaultSound.start();

                Intent intent = new Intent(getApplicationContext(), GameSelection.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_left);

                break;
            }
            case R.id.exit: {

                if(exitSound!= null)
                    exitSound.start();

                finish();
                System.exit(0);
                break;
            }
            case R.id.googleLogin:{

                startupDone = true;
                googleClicked = true;

                if(!isSignedIn())
                    startSignInIntent();
                else
                {
                    dialogMessage="Already Signed in";
                    showDialogBox(dialogMessage, DIALOG_SIZE_SMALL);
                }

                break;
            }
            case R.id.globalHighscore:{

                if(mLeaderboardsClient!= null && isSignedIn() ) {
                    showLeaderboard();
                }
                else
                {
                    dialogMessage = "Sign in to view the Leader board.. ";
                    showDialogBox(dialogMessage, DIALOG_SIZE_SMALL);

                    //showSignInRulesDialog();
                    //startSignInIntent();
                }

                break;

            }
            case R.id.score: {

                if(defaultSound!= null)
                    defaultSound.start();

                calculateScoreAndShowDialog();

                break;
            }
            case R.id.bulb: {
                if(bulbOn) {

                    if(lightOff!= null)
                        lightOff.start();

                    bulb.setImageResource(R.drawable.bulb_off);
                    bulbOn = false;
                    homeScreen.setBackgroundResource(R.drawable.box_curved);
                    titleText.setTextColor(Color.WHITE);
                }
                else {

                    if(lightOn!= null)
                        lightOn.start();

                    bulb.setImageResource(R.drawable.bulb_on);
                    bulbOn = true;
                    homeScreen.setBackgroundResource(R.drawable.curve_intro);
                    titleText.setTextColor(Color.BLACK);
                }
                break;
            }
            case R.id.titleText:
            case R.id.about: {


                if(defaultSound!= null)
                    defaultSound.start();

                dialogMessage = "Developed By \nRaghul Subramaniam";

                showDialogBox(dialogMessage, DIALOG_SIZE_SMALL);

                break;
            }
        }
    }

    private  void calculateScoreAndShowDialog()
    {
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int scoreEasy = prefs.getInt("easy", 0); //0 is the default value
        int scoreSeq = prefs.getInt("seq", 0); //0 is the default value
        int scoreMem = prefs.getInt("mem", 0); //0 is the default value
        int total = scoreEasy + scoreSeq + scoreMem;
        //Toast.makeText(getApplicationContext(), "HighScore: " + scoreEasy , Toast.LENGTH_SHORT).show();

        dialogMessage = "HighScore: \n  RandomButtons(Agility): " +scoreEasy +"\n  SeqeunceButtons(Focus): " +scoreSeq +"\n  MemoryButtons(Memory): "+scoreMem +"\n \n" +"  Total Score: " +total;

        showDialogBox(dialogMessage, DIALOG_SIZE_LARGE);

    }

    private void showSignInRulesDialog()
    {
        rulesDialog = new Dialog(this);
        rulesDialog.setContentView(R.layout.rules_dialog_small);

        if(rulesDialog.getWindow()!= null)
            rulesDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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


    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    /*public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                        } else {
                            handleException(task.getException(), "signOut() failed!");
                        }

                        onDisconnected();
                    }
                });
    }*/

    public void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }


    private void showLeaderboard() {
        //mLeaderboardsClient.submitScore(getString(R.string.leaderboard_id), 250);

        submitScores();

        onShowLeaderboardsRequested();
    }

    public void submitScores()
    {
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int scoreEasy = prefs.getInt("easy", 0); //0 is the default value
        int scoreSeq = prefs.getInt("seq", 0); //0 is the default value
        int scoreMem = prefs.getInt("mem", 0); //0 is the default value
        int total = scoreEasy + scoreSeq + scoreMem;
        //Toast.makeText(getApplicationContext(), "HighScore: " + scoreEasy , Toast.LENGTH_SHORT).show();

        mLeaderboardsClient.submitScore(getString(R.string.leaderboard_random_buttons), total);

        //dialogMessage = "HighScore: \n  RandomButtons(Agility): " +scoreEasy +"\n  SeqeunceButtons(Focus): " +scoreSeq +"\n  MemoryButtons(Memory): "+scoreMem +"\n \n" +"  Total Score: " +total;
    }

    public void showDialogBox(String msg, String size)
    {
        rulesDialog = new Dialog(this);

        if(size.equals(DIALOG_SIZE_SMALL))
            rulesDialog.setContentView(R.layout.rules_dialog_small);
        else
            rulesDialog.setContentView(R.layout.rules_dialog_large);

        if(rulesDialog.getWindow()!= null)
            rulesDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView dialogText;

        dialogText = rulesDialog.findViewById(R.id.rulesText);
        dialogText.setText(msg);

        Button button;

        if(size.equals(DIALOG_SIZE_SMALL))
            button = rulesDialog.findViewById(R.id.dialogOkayButtonSmall);
        else
            button = rulesDialog.findViewById(R.id.dialogOkayButtonLarge);
        //button.setText(R.string.Back);

        //leaderboard_idrulesDialog.setCancelable(false);

        Window window = rulesDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations=R.style.DialogAnimation;
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
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
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
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
