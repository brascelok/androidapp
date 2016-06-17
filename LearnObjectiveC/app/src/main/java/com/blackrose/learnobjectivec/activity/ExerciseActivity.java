/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.utilities.Util;

public class ExerciseActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initWidgets();
    }

    private void initWidgets(){
        Button btnTenQuestion = (Button) findViewById(R.id.btn_10_question);
        Button btnTwentyQuestion = (Button) findViewById(R.id.btn_20_question);
        Button btnThirtyQuestion = (Button) findViewById(R.id.btn_30_question);
        btnTenQuestion.setOnClickListener(this);
        btnTwentyQuestion.setOnClickListener(this);
        btnThirtyQuestion.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int selectedNumber = 10;
        switch (v.getId()){
            case R.id.btn_10_question:
                selectedNumber = 10;
            break;
            case R.id.btn_20_question:
                selectedNumber = 20;
            break;
            case R.id.btn_30_question:
                selectedNumber = 30;
            break;
        }
        Intent intent = new Intent(ExerciseActivity.this, ExerciseDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Util.INTENT_EXERCISE, selectedNumber);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
