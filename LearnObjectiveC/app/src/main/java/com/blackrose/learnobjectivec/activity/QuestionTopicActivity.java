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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.adapter.ExpandListAdapter;
import com.blackrose.learnobjectivec.model.QuestionTopic;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class QuestionTopicActivity extends AppCompatActivity {

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private List<String> listAnswerDevelopmentBasic, listAnswerAppStateMultitasking, listAnswerAppState, listAnswerCoreApp;
    private List<String> listQuestionDevelopmentBasic, listQuestionAppStateMultitask, listQuestionAppState, listQuestionCoreApp;
    private TextView tvQuestion, tvAnswer;
    private Button btnOK;

    private List<QuestionTopic> listQuestionTopic;
    private ExpandableListView expandableListView;
    ProgressDialog asyncDialog;
    private Dialog popupDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_topic);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAdView = (AdView) findViewById(R.id.adViewQuestionTopic);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        listQuestionTopic = new ArrayList<>();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, List<String>>();
        this.registerReceiver(this.receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        Firebase.setAndroidContext(this);
        final String url = "https://learnios.firebaseio.com/questiontopic";
        Firebase ref = new Firebase(url);
        expandableListView = (ExpandableListView) findViewById(R.id.lv_question_topic);

        // Add Header
        listDataHeader.add("Development Basics");
        listDataHeader.add("App States and Multitasking");
        listDataHeader.add("App States");
        listDataHeader.add("Core App Objects");

        //new LoadQuestionTopicTask(QuestionTopicActivity.this).execute();
        asyncDialog = new ProgressDialog(QuestionTopicActivity.this){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                    QuestionTopicActivity.this.finish();
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
                    QuestionTopic questionTopic = snapshot.getValue(QuestionTopic.class);
                    listQuestionTopic.add(questionTopic);
                }
                Log.d("List question size: ", String.valueOf(listQuestionTopic.size()));
                fillDataToListFrom(listQuestionTopic);
                ExpandListAdapter expandListAdapter = new ExpandListAdapter(QuestionTopicActivity.this, listDataHeader, listDataChild);
                expandableListView.setAdapter(expandListAdapter);
                expandListAdapter.notifyDataSetChanged();
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FirebaseError: ", firebaseError.getMessage());
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String _tempQuestion = "", _tempAnswer = "";
                switch (groupPosition) {
                    case 0:
                        _tempQuestion = listQuestionDevelopmentBasic.get(childPosition);
                        _tempAnswer = listAnswerDevelopmentBasic.get(childPosition);
                        break;
                    case 1:
                        _tempQuestion = listQuestionAppStateMultitask.get(childPosition);
                        _tempAnswer = listAnswerAppStateMultitasking.get(childPosition);
                        break;
                    case 2:
                        _tempQuestion = listQuestionAppState.get(childPosition);
                        _tempAnswer = listAnswerAppState.get(childPosition);
                        break;
                    case 3:
                        _tempQuestion = listQuestionCoreApp.get(childPosition);
                        _tempAnswer = listAnswerCoreApp.get(childPosition);
                        break;
                }
                popupDialog = new Dialog(QuestionTopicActivity.this);
                popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popupDialog.setContentView(R.layout.question_topic_detail);
                popupDialog.setCanceledOnTouchOutside(false);
                tvQuestion = (TextView) popupDialog.findViewById(R.id.tv_questiontopic_question);
                tvAnswer = (TextView) popupDialog.findViewById(R.id.tv_questiontopic_answer);
                btnOK = (Button) popupDialog.findViewById(R.id.btn_ok_topic);
                tvQuestion.setText(_tempQuestion);
                tvAnswer.setText(_tempAnswer);
                btnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupDialog.dismiss();
                    }
                });
                popupDialog.show();
                return false;
            }
        });

    }

    private List<QuestionTopic> filterListBy(List<QuestionTopic> fatherList, final String filterProperty){
        List<QuestionTopic> resultList;
        Predicate<QuestionTopic> filter = new Predicate<QuestionTopic>() {
            public boolean apply(QuestionTopic question) {
                return question.getTopic().equals(filterProperty);
            }
        };
        Collection<QuestionTopic> collection = filter(fatherList, filter);
        resultList = new ArrayList(collection);
        return resultList;
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

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    private void hideAll(){
        RelativeLayout layoutFather = (RelativeLayout) findViewById(R.id.layout_questiontopic);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuestionTopicActivity.this);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Notice");
                alertDialog.setCancelable(false);
                alertDialog.setMessage("No internet connection detected");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        QuestionTopicActivity.this.finish();
                    }
                });
                alertDialog.show();
            }
        }
    };

    public interface Predicate<T> { boolean apply(T type); }

    public static <T> Collection<T> filter(Collection<T> col, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element: col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }
    private void fillDataToListFrom(List<QuestionTopic> listQuestionTopic) {

        // Add Children
        listAnswerDevelopmentBasic = new ArrayList<>();
        listAnswerAppStateMultitasking = new ArrayList<>();
        listAnswerAppState = new ArrayList<>();
        listAnswerCoreApp = new ArrayList<>();
        listQuestionDevelopmentBasic = new ArrayList<>();
        listQuestionAppStateMultitask = new ArrayList<>();
        listQuestionAppState = new ArrayList<>();
        listQuestionCoreApp = new ArrayList<>();

        // temp list, result of filtering data from father listQuestionTopic
        List<QuestionTopic> questionTopicList;

        // Add Child 1
        questionTopicList = filterListBy(listQuestionTopic, "Development_Basics");
        Log.d("SIZE 1:", String.valueOf(questionTopicList.size()));
        for (int i = 0; i < questionTopicList.size(); i++){
            listQuestionDevelopmentBasic.add(questionTopicList.get(i).getTopicquestion());
            listAnswerDevelopmentBasic.add(questionTopicList.get(i).getTopicanswer());
        }
        // Add Child 2
        questionTopicList = filterListBy(listQuestionTopic, "App_States_and_Multitasking");
        Log.d("SIZE 2:", String.valueOf(questionTopicList.size()));
        for (int i = 0; i < questionTopicList.size(); i++){
            listQuestionAppStateMultitask.add(questionTopicList.get(i).getTopicquestion());
            listAnswerAppStateMultitasking.add(questionTopicList.get(i).getTopicanswer());
        }
        // Add Child 3
        questionTopicList = filterListBy(listQuestionTopic, "State_App");
        Log.d("SIZE 3: ", String.valueOf(questionTopicList.size()));
        for (int i = 0; i < questionTopicList.size(); i++){
            listQuestionAppState.add(questionTopicList.get(i).getTopicquestion());
            listAnswerAppState.add(questionTopicList.get(i).getTopicanswer());
        }
        // Add Child 4
        questionTopicList = filterListBy(listQuestionTopic, "Core_App_Objects");
        Log.d("SIZE 4: ", String.valueOf(questionTopicList.size()));
        for (int i = 0; i < questionTopicList.size(); i++){
            listQuestionCoreApp.add(questionTopicList.get(i).getTopicquestion());
            listAnswerCoreApp.add(questionTopicList.get(i).getTopicanswer());
        }

        listDataChild.put(listDataHeader.get(0), listQuestionDevelopmentBasic);
        listDataChild.put(listDataHeader.get(1), listQuestionAppStateMultitask);
        listDataChild.put(listDataHeader.get(2), listQuestionAppState);
        listDataChild.put(listDataHeader.get(3), listQuestionCoreApp);
    }


    /*class LoadQuestionTopicTask extends AsyncTask<Void, List<QuestionTopic>, Void>{
        Context mContext;

        public LoadQuestionTopicTask(Context mContext){
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
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        QuestionTopic questionTopic = snapshot.getValue(QuestionTopic.class);
                        listQuestionTopic.add(questionTopic);
                    }
                    Log.d("List question size: ", String.valueOf(listQuestionTopic.size()));
                    publishProgress(listQuestionTopic);
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
        protected final void onProgressUpdate(List<QuestionTopic>... values) {
            super.onProgressUpdate(values);
            Log.d("onProgressUpdate:", values[0].get(0).toString());
            if(values[0].size() > 0) {
                fillDataToListFrom(values[0]);
                ExpandListAdapter expandListAdapter = new ExpandListAdapter(QuestionTopicActivity.this, listDataHeader, listDataChild);
                expandableListView.setAdapter(expandListAdapter);
                expandListAdapter.notifyDataSetChanged();
            }
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
            }, 5000);
        }
    }*/

}
