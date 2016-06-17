/*
 * Copyright (C) 2016 blackrose
 */
package com.blackrose.learnobjectivec.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.blackrose.learnobjectivec.R;
import com.blackrose.learnobjectivec.adapter.TipsLayoutAdapter;
import com.blackrose.learnobjectivec.gui.TitleFlowIndicator;
import com.blackrose.learnobjectivec.gui.ViewFlow;
import com.blackrose.learnobjectivec.model.Tips;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private ViewFlow viewFlow;
    private List<Tips> listTips;
    List<String> mListText, mListTitles, mListImage;
    ProgressDialog asyncDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listTips = new ArrayList<>();
        mListText = new ArrayList<>();
        mListTitles = new ArrayList<>();
        mListImage = new ArrayList<>();
        viewFlow = (ViewFlow) findViewById(R.id.viewflow);
        Firebase.setAndroidContext(this);
        final String url = "https://learnios.firebaseio.com/tips";
        Firebase ref = new Firebase(url);
        this.registerReceiver(this.receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        /*
        * initialize list tips
        * by get from Firebase server
        */
        //new LoadTipsTask(TipsActivity.this).execute();
        asyncDialog = new ProgressDialog(TipsActivity.this){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                    TipsActivity.this.finish();
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
                    Tips tip = postSnapshot.getValue(Tips.class);
                    listTips.add(tip);
                }
                Log.d("List tip size: ", String.valueOf(listTips.size()));
                Collections.shuffle(listTips);
                for (Tips tip : listTips) {
                    mListImage.add(tip.getImage());
                    mListText.add(tip.getText());
                    mListTitles.add(tip.getTitle());
                }
                TipsLayoutAdapter adapter = new TipsLayoutAdapter(TipsActivity.this, mListTitles, mListText, mListImage);
                viewFlow.setAdapter(adapter, 0);
                adapter.notifyDataSetChanged();
                TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
                indicator.setTitleProvider(adapter);
                viewFlow.setFlowIndicator(indicator);
                if(asyncDialog != null && asyncDialog.isShowing()){
                    asyncDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FirebaseError: ", firebaseError.getMessage());
            }
        });
    }

    /* If your min SDK version is < 8 you need to trigger the onConfigurationChanged in ViewFlow manually, like this */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        viewFlow.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }

    private void hideAll(){
        LinearLayout fatherLayout = (LinearLayout) findViewById(R.id.layout_tips);
        fatherLayout.setVisibility(View.GONE);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TipsActivity.this);
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setTitle("Notice");
                alertDialog.setCancelable(false);
                alertDialog.setMessage("No internet connection detected");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        TipsActivity.this.finish();
                    }
                });
                alertDialog.show();
            }
        }
    };

    /*class LoadTipsTask extends AsyncTask<Void, List<Tips>, Void>{

        Context mContext;

        public LoadTipsTask(Context mContext) {
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
                        Tips tip = postSnapshot.getValue(Tips.class);
                        listTips.add(tip);
                    }
                    Log.d("List tip size: ", String.valueOf(listTips.size()));
                    Collections.shuffle(listTips);
                    publishProgress(listTips);
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
        protected final void onProgressUpdate(List<Tips>... values) {
            super.onProgressUpdate(values);
            //get listTips then setup value for 3 array: titles, image, link
            Log.d("onProgressUpdate:", values[0].get(0).toString());
            if(values[0].size() > 0) {
                for (Tips tip : listTips) {
                    mListImage.add(tip.getImage());
                    mListText.add(tip.getText());
                    mListTitles.add(tip.getTitle());
                }
                TipsLayoutAdapter adapter = new TipsLayoutAdapter(TipsActivity.this, mListTitles, mListText, mListImage);
                viewFlow.setAdapter(adapter, 0);
                adapter.notifyDataSetChanged();
                TitleFlowIndicator indicator = (TitleFlowIndicator) findViewById(R.id.viewflowindic);
                indicator.setTitleProvider(adapter);
                viewFlow.setFlowIndicator(indicator);
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
            }, 2000);
        }
    }*/


}