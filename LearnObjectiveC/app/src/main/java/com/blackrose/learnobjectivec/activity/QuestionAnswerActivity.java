/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blackrose.learnobjectivec.R;


public class QuestionAnswerActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Button btnTopic = (Button) findViewById(R.id.btnTopic);
        Button btnCompare = (Button) findViewById(R.id.btnCompare);
        btnTopic.setOnClickListener(this);
        btnCompare.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnTopic:
                Intent intent1 = new Intent(QuestionAnswerActivity.this, QuestionTopicActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnCompare:
                Intent intent2 = new Intent(QuestionAnswerActivity.this, QuestionCompareActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
