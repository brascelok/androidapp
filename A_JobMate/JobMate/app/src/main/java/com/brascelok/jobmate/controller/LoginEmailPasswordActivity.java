package com.brascelok.jobmate.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brascelok.jobmate.R;
import com.brascelok.jobmate.Utilities.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginEmailPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etEmail, etPassword;
    TextView tvDetail, tvStatus;
    LinearLayout mainLayout;

    public static final String TAG = "EmailPasswordActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email_password);

        // init all widgets and register event for buttons
        initWidgets();
        // init firebase authen
        mAuth = FirebaseAuth.getInstance();
        // init authen state listener
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    // user has signed in
                    Log.d(TAG, "onAuthStateChanged:sign_id: UID: " + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:sign_id: EMAIL: " + user.getEmail());
                    String email = user.getEmail();
                    if (email != null) {
                        Intent toMainActivityIntent = new Intent(LoginEmailPasswordActivity.this, MainActivity.class);
                        toMainActivityIntent.putExtra(Util.INTENT_LOGIN_EMAIL, email);
                        startActivity(toMainActivityIntent);
                    }
                }else{
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                //updateUI(user);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void initWidgets(){
        etEmail = (EditText) findViewById(R.id.field_email);
        etPassword = (EditText) findViewById(R.id.field_password);
        tvDetail = (TextView) findViewById(R.id.detail);
        tvStatus = (TextView) findViewById(R.id.status);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        progressDialog = new ProgressDialog(LoginEmailPasswordActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setIndeterminate(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.email_sign_in_button:
                signIn(etEmail.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.email_create_account_button:
                createAccount(etEmail.getText().toString(), etPassword.getText().toString());
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    boolean validateForm(String email, String password){
        boolean valid = true;
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Invalid email");
            valid = false;
        }else{
            etEmail.setError(null);
        }
        if (password.isEmpty()){
            etPassword.setError("Invalid password");
            valid = false;
        }else{
            etPassword.setError(null);
        }
        return valid;
    }

    void signIn(final String email, String password){
        Log.d(TAG, "Signin: " + email);
        if (!validateForm(email, password)){
            return;
        }
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:onComplete" + task.isSuccessful());
                    Intent toMainActivityIntent = new Intent(LoginEmailPasswordActivity.this, MainActivity.class);
                    toMainActivityIntent.putExtra(Util.INTENT_LOGIN_EMAIL, email);
                    startActivity(toMainActivityIntent);
                }else{
                    Log.d(TAG, "signInWithEmail" + task.getException());
                    Snackbar.make(mainLayout, "Authen failed", Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }
        });
    }

    void createAccount(String email, String password){
        Log.d(TAG, "Signin: " + email);
        if (!validateForm(email, password)){
            return;
        }
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmail:onComplete" + task.isSuccessful());
                if (!task.isSuccessful()){
                    Log.d(TAG, "createUserWithEmail" + task.getException());
                    Snackbar.make(mainLayout, "Authen failed", Snackbar.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

    void signOut(){
        mAuth.signOut();
        updateUI(null);
    }

    void updateUI(FirebaseUser user){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        if (user != null){
            tvStatus.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()));
            tvDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
        }else{
            tvStatus.setText(R.string.sign_out);
            tvDetail.setText(null);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        }
    }
}
