package com.brascelok.jobmate.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.brascelok.jobmate.R;
import com.brascelok.jobmate.model.ChatMessage;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * Created by Ratan on 7/29/2015.
 */
public class ChatFragment extends Fragment {

    private Firebase mFirebaseRef;
    private FirebaseListAdapter<ChatMessage> mListAdapter;
    private String mEmail = "";
    private static final String TAG = "ChatFragment";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthenListener;
    private FirebaseUser firebaseUser;
    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthenListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    mEmail = firebaseUser.getEmail();
                    Log.d(TAG, "onAuthStateChanged: EMAIL: " + firebaseUser.getEmail());
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout_chat,null);

        Firebase.setAndroidContext(view.getContext());
        mFirebaseRef = new Firebase("https://jobmate.firebaseio.com");
        final EditText etMsg = (EditText) view.findViewById(R.id.text_edit);
        Button btnSend = (Button) view.findViewById(R.id.send_button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = etMsg.getText().toString();
                ChatMessage chatMessage = new ChatMessage(mEmail, text);
                mFirebaseRef.push().setValue(chatMessage);
                etMsg.setText("");
            }
        });
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        mListAdapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class, android.R.layout.two_line_list_item, mFirebaseRef) {
            @Override
            protected void populateView(View view, ChatMessage chatMessage, int i) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(chatMessage.getName());
                ((TextView)view.findViewById(android.R.id.text2)).setText(chatMessage.getText());
            }
        };
        listView.setAdapter(mListAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthenListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthenListener != null){
            mAuth.removeAuthStateListener(mAuthenListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();
    }
}
