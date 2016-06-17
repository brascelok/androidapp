/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.model.Exercise;
import com.blackrose.learnobjectivec.utilities.Util;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.droidsonroids.gif.GifTextView;

public class ExerciseDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvQuestion, tvQuestionNumber;
    private Button btnNext;
    private ImageButton ibShowAnswer;
    private ProgressBar progressBar;
    private RadioGroup radioGroup;
    private RadioButton rb1, rb2, rb3, rb4;

    private static int currentQuestion = 0;
    private static int numberQuestion = 0;
    private static int correctPos = 0;
    private int point = 0;
    private List<Exercise> listExercises;
    ProgressDialog asyncDialog;
    private Dialog resultDialog;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises_detail);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        listExercises = new ArrayList<>();
        this.registerReceiver(this.receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        Firebase.setAndroidContext(this);
        initWidgets();
        final String url = "https://learnios.firebaseio.com/exercises";
        Firebase ref = new Firebase(url);

        /*
        * get number package from ExerciseActivity
        * will received one of these values: 10/20/30
        * */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            numberQuestion = bundle.getInt(Util.INTENT_EXERCISE);
        }
        progressBar.setMax(numberQuestion);

        /*
        * initialize list exercise
        * by get from Firebase server
        */
        //new LoadExerciseTask(ExerciseDetailActivity.this).execute();
        asyncDialog = new ProgressDialog(ExerciseDetailActivity.this){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                    ExerciseDetailActivity.this.finish();
                }
            }
        };
        asyncDialog.setMessage("Please Wait");
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setCanceledOnTouchOutside(false);
        asyncDialog.setIndeterminate(false);
        asyncDialog.setCancelable(false);
        asyncDialog.show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Exercise exercise = postSnapshot.getValue(Exercise.class);
                    listExercises.add(exercise);
                }
                Log.d("List exercise size: ", String.valueOf(listExercises.size()));
                Collections.shuffle(listExercises);
                Exercise firstExercise = listExercises.get(currentQuestion); //get first element from list that has mixed by COLLECTIONS.SHUFFLE
                tvQuestion.setText(firstExercise.getQuestion());
                rb1.setText(firstExercise.getAnswer1());
                rb2.setText(firstExercise.getAnswer2());
                rb3.setText(firstExercise.getAnswer3());
                rb4.setText(firstExercise.getAnswer4());
                correctPos = firstExercise.getCorrectAnswerPos();

                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FirebaseError: ", firebaseError.getMessage());
            }
        });

        String textQuestionNumber = (currentQuestion + 1) + "/" + numberQuestion;
        tvQuestionNumber.setText(textQuestionNumber);
        progressBar.setProgress(currentQuestion + 1);
        catchEventButton();

    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        this.unregisterReceiver(this.receiver);
        if (listExercises.size() > 0) {
            listExercises.clear();
        }
        currentQuestion = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           case R.id.btn_next_question:
               if(btnNext.getText().equals("Finish")){
                   resultDialog = new Dialog(ExerciseDetailActivity.this);
                   resultDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                   resultDialog.setCanceledOnTouchOutside(false);
                   resultDialog.setContentView(R.layout.exercise_score);
                   TextView tvScore = (TextView) resultDialog.findViewById(R.id.tv_score);
                   Button btnOk = (Button) resultDialog.findViewById(R.id.btn_ok_score);
                   GifTextView image = (GifTextView) resultDialog.findViewById(R.id.ex_score_image);
                   int percent = point * 100 / numberQuestion;
                   if (percent < 50){
                       image.setBackgroundResource(R.drawable.ic_big_smiley_002);
                   }else{
                       image.setBackgroundResource(R.drawable.ic_big_smiley_001);
                   }
                   String percentStr = String.valueOf(percent) + "%";
                   tvScore.setText(percentStr);
                   btnOk.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           resultDialog.dismiss();
                           ExerciseDetailActivity.this.finish();
                       }
                   });
                   resultDialog.show();
               }
                resetComponent();
                currentQuestion++;
                if (currentQuestion >= numberQuestion) {
                    currentQuestion = numberQuestion - 1;
                    return;
                }
               if(listExercises.size() > 0) {
                   Exercise exercise2 = listExercises.get(currentQuestion);
                   tvQuestion.setText(exercise2.getQuestion());
                   rb1.setText(exercise2.getAnswer1());
                   rb2.setText(exercise2.getAnswer2());
                   rb3.setText(exercise2.getAnswer3());
                   rb4.setText(exercise2.getAnswer4());
                   correctPos = exercise2.getCorrectAnswerPos();
               }
                String textQuestionNumber2 = (currentQuestion + 1) + "/" + numberQuestion;
                tvQuestionNumber.setText(textQuestionNumber2);
                progressBar.setProgress((currentQuestion + 1));
                if (currentQuestion == numberQuestion - 1){
                   btnNext.setText(R.string.finish);
                }
               Log.d("current question: ", String.valueOf(currentQuestion));
                break;
            case R.id.ib_ex_checkanswer:
                int selectedId = radioGroup.getCheckedRadioButtonId();
                View radioSelected = radioGroup.findViewById(selectedId);
                int index = radioGroup.indexOfChild(radioSelected);
                RadioButton rbSelected = (RadioButton) radioGroup.getChildAt(index);
                if(correctPos > -1 && correctPos < 4) {
                    if (index == correctPos) {
                        rbSelected.setTextColor(Color.GREEN);
                        point++;
                    } else {
                        RadioButton rbCorrect = (RadioButton) radioGroup.getChildAt(correctPos);
                        rbSelected.setTextColor(Color.RED);
                        rbCorrect.setTextColor(Color.GREEN);
                    }
                }
                ibShowAnswer.setEnabled(false);
                ibShowAnswer.setImageResource(R.drawable.ic_exercise_check_disable);
                rb1.setClickable(false);
                rb2.setClickable(false);
                rb3.setClickable(false);
                rb4.setClickable(false);
                btnNext.setEnabled(true);
                btnNext.setBackgroundColor(Color.parseColor("#00422f"));
                break;
            default:
                break;
        }
    }

    private void initWidgets(){
        tvQuestion = (TextView) findViewById(R.id.tv_exercise_question);
        tvQuestionNumber = (TextView) findViewById(R.id.tv_exercise_question_number);
        btnNext = (Button) findViewById(R.id.btn_next_question);
        ibShowAnswer = (ImageButton) findViewById(R.id.ib_ex_checkanswer);
        progressBar = (ProgressBar) findViewById(R.id.pb_exercise);
        radioGroup = (RadioGroup) findViewById(R.id.rg_exercise);
        rb1 = (RadioButton) findViewById(R.id.rb_exercise1);
        rb2 = (RadioButton) findViewById(R.id.rb_exercise2);
        rb3 = (RadioButton) findViewById(R.id.rb_exercise3);
        rb4 = (RadioButton) findViewById(R.id.rb_exercise4);

        ibShowAnswer.setEnabled(false);
        ibShowAnswer.setImageResource(R.drawable.ic_exercise_check_disable);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(Color.GRAY);
    }

    private void resetComponent(){
        radioGroup.clearCheck();
        ibShowAnswer.setEnabled(false);
        ibShowAnswer.setImageResource(R.drawable.ic_exercise_check_disable);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(Color.GRAY);
        rb1.setTextColor(Color.BLACK);
        rb2.setTextColor(Color.BLACK);
        rb3.setTextColor(Color.BLACK);
        rb4.setTextColor(Color.BLACK);
        rb1.setClickable(true);
        rb2.setClickable(true);
        rb3.setClickable(true);
        rb4.setClickable(true);
    }

    private void catchEventButton(){
        btnNext.setOnClickListener(this);
        ibShowAnswer.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ibShowAnswer.setEnabled(true);
                ibShowAnswer.setImageResource(R.drawable.ic_exercise_check);
            }
        });
    }

    private void hideAll(){
        RelativeLayout layoutFather = (RelativeLayout) findViewById(R.id.layout_exercise_father);
        layoutFather.setVisibility(View.GONE);
        if(resultDialog != null && resultDialog.isShowing()){
            resultDialog.dismiss();
        }
        if(asyncDialog != null && asyncDialog.isShowing()){
            asyncDialog.dismiss();
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            Log.d("Detect network: ", String.valueOf(currentNetworkInfo.isConnected()));
            if(!currentNetworkInfo.isConnected()){
                hideAll();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ExerciseDetailActivity.this);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Notice");
                alertDialog.setCancelable(false);
                alertDialog.setMessage("No internet connection detected");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ExerciseDetailActivity.this.finish();
                    }
                });
                alertDialog.show();
            }
        }
    };

    /*class LoadExerciseTask extends AsyncTask<Void, List<Exercise>, Void>{

        Context mContext;
        Exercise firstExercise;

        public LoadExerciseTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncDialog = new ProgressDialog(mContext);
            asyncDialog.setMessage("Please Wait");
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.setIndeterminate(false);
            asyncDialog.setCancelable(false);
            asyncDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Exercise exercise = postSnapshot.getValue(Exercise.class);
                        listExercises.add(exercise);
                    }
                    Log.d("List exercise size: ", String.valueOf(listExercises.size()));
                    Collections.shuffle(listExercises);
                    publishProgress(listExercises);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d("FirebaseError: ", firebaseError.getMessage());
                }
            });
            return null;
        }

        @SafeVarargs
        @Override
        protected final void onProgressUpdate(List<Exercise>... values) {
            super.onProgressUpdate(values);
            Log.d("onProgressUpdate:", values[0].get(currentQuestion).toString());
            firstExercise = values[0].get(currentQuestion); //get first element from list that has mixed by COLLECTIONS.SHUFFLE
            tvQuestion.setText(firstExercise.getQuestion());
            rb1.setText(firstExercise.getAnswer1());
            rb2.setText(firstExercise.getAnswer2());
            rb3.setText(firstExercise.getAnswer3());
            rb4.setText(firstExercise.getAnswer4());
            correctPos = firstExercise.getCorrectAnswerPos();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if ((asyncDialog != null) && asyncDialog.isShowing())
                        asyncDialog.dismiss();
                }
            }, 2000);
        }
    }*/
}


