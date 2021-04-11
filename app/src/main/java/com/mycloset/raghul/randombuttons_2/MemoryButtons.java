package com.mycloset.raghul.randombuttons_2;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MemoryButtons extends Activity implements View.OnClickListener {

      /*

Developer: Raghul Subramaniam
Email: raghulmaniam@gmail.com

 */

    Dialog rulesDialog , gameoverDialog;
    private FrameLayout mainFrameLayout;


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


        start = findViewById(R.id.start);
        start.setOnClickListener(this);
        start.setImageResource(R.mipmap.start);

        mainFrameLayout = findViewById(R.id.mainGameLayout);

    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.start: {
                removeButton(view);
                showRulesDialog();
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
                                              startGame();
                                          }
                                      }
        );
    }


    public void startGame() {
        mainFrameLayout.setOnClickListener(this);

    }
}



