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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.model.QuestionCompare;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class QuestionCompareActivity extends AppCompatActivity {

    private ListView lvAllQuestion;
    private TextView tvQuestion, tvAnswer1, tvAnswer2, tvAnswer3;
    private Button btnOK;
    private List<String> listQuestions, listAnswer1, listAnswer2, listAnswer3;
    private List<QuestionCompare> listQuestionCompare;
    ProgressDialog asyncDialog;
    private Dialog popupDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_compare);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAdView = (AdView) findViewById(R.id.adViewQuestionCompare);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        listQuestionCompare = new ArrayList<>();
        lvAllQuestion = (ListView) findViewById(R.id.lv_all_question);
        this.registerReceiver(this.receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        Firebase.setAndroidContext(this);
        final String url = "https://learnios.firebaseio.com/questioncompare";
        Firebase ref = new Firebase(url);

        asyncDialog = new ProgressDialog(QuestionCompareActivity.this){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                    QuestionCompareActivity.this.finish();
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
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuestionCompare questionCompare = snapshot.getValue(QuestionCompare.class);
                    listQuestionCompare.add(questionCompare);
                }
                Log.d("List question size: ", String.valueOf(listQuestionCompare.size()));
                addToList(listQuestionCompare);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
                        R.layout.question_compare_list_item, R.id.question_title, listQuestions);
                lvAllQuestion.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FirebaseError: ", firebaseError.getMessage());
            }
        });


        lvAllQuestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String tempQuestion = listQuestions.get(i);
                String tempAnswer1 = listAnswer1.get(i);
                String tempAnswer2 = listAnswer2.get(i);
                String tempAnswer3 = listAnswer3.get(i);

                popupDialog = new Dialog(QuestionCompareActivity.this);
                popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupDialog.setContentView(R.layout.question_compare_detail);
                tvQuestion = (TextView) popupDialog.findViewById(R.id.tv_questioncompare_question);
                tvAnswer1 = (TextView) popupDialog.findViewById(R.id.tv_questioncompare_answer1);
                tvAnswer2 = (TextView) popupDialog.findViewById(R.id.tv_questioncompare_answer2);
                tvAnswer3 = (TextView) popupDialog.findViewById(R.id.tv_questioncompare_answer3);
                btnOK = (Button) popupDialog.findViewById(R.id.btn_ok_compare);
                tvQuestion.setText(tempQuestion);
                tvAnswer1.setText(tempAnswer1);
                tvAnswer2.setText(tempAnswer2);
                tvAnswer3.setText(tempAnswer3);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupDialog.dismiss();
                    }
                });
                popupDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

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

    private void hideAll(){
        RelativeLayout layoutFather = (RelativeLayout) findViewById(R.id.layout_questioncompare);
        layoutFather.setVisibility(View.GONE);
        if(popupDialog != null && popupDialog.isShowing()){
            popupDialog.dismiss();
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuestionCompareActivity.this);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Notice");
                alertDialog.setCancelable(false);
                alertDialog.setMessage("No internet connection detected");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        QuestionCompareActivity.this.finish();
                    }
                });
                alertDialog.show();
            }
        }
    };

    private void addToList(List<QuestionCompare> questionCompareList) {
        listQuestions = new ArrayList<>();
        listAnswer1 = new ArrayList<>();
        listAnswer2 = new ArrayList<>();
        listAnswer3 = new ArrayList<>();
        for (int i = 0; i < questionCompareList.size(); i++) {
            listQuestions.add(questionCompareList.get(i).getComparequestion());
            listAnswer1.add(questionCompareList.get(i).getCompareanswer1());
            listAnswer2.add(questionCompareList.get(i).getCompareanswer2());
            listAnswer3.add(questionCompareList.get(i).getCompareanswer3());
        }
    }
}