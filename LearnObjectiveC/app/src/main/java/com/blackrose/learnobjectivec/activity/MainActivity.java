/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blackrose.learnobjectivec.R;

public class MainActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btnTheory, btnQuestion, btnTips, btnExercise;
        btnExercise = (Button) findViewById(R.id.btn_exercise);
        btnTheory = (Button) findViewById(R.id.btn_theory);
        btnQuestion = (Button) findViewById(R.id.btn_question);
        btnTips = (Button) findViewById(R.id.btn_tips);

        btnExercise.setOnClickListener(this);
        btnTheory.setOnClickListener(this);
        btnTips.setOnClickListener(this);
        btnQuestion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_theory:
                Intent intent = new Intent(MainActivity.this, TheoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_exercise:
                Intent intent2 = new Intent(MainActivity.this, ExerciseActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_tips:
                Intent intent3 = new Intent(MainActivity.this, TipsActivity.class);
                startActivity(intent3);
                break;
            case R.id.btn_question:
                Intent intent4 = new Intent(MainActivity.this, QuestionAnswerActivity.class);
                startActivity(intent4);
                break;

        }
    }
}
