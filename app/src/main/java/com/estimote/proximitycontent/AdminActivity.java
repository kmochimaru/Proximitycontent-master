package com.estimote.proximitycontent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.estimote.proximitycontent.log.MyLog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static android.text.TextUtils.isEmpty;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText confirm_email, confirm_password;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "AdminActivity";
    private View viewSnackBar;
    private ProgressDialog progress;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        viewSnackBar = findViewById(R.id.MainActivity);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnCreate).setOnClickListener(this);

        confirm_email = findViewById(R.id.confirm_email);
        confirm_password = findViewById(R.id.confirm_password);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                    intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                    startActivity(intent);
                    finish();
                }
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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signIn(String email, String password) {

        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    MyLog myLog = new MyLog("Logged In", mAuth.getCurrentUser().getEmail());
                    myLog.addLog();
                    Intent intent = new Intent(AdminActivity.this, DetailActivity.class);
                    intent.putExtra("email", mAuth.getCurrentUser().getEmail());
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(viewSnackBar, "กรุณาตรวจสอบอีเมลกับรหัสผ่านอีกครั้ง", Snackbar.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    private void hideProgressDialog() {
        progress.dismiss();
    }

    private void showProgressDialog() {
        progress = ProgressDialog.show(this, "Authentication",
                "Loading...", true);
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Snackbar.make(viewSnackBar, "ลงทะเบียนอีเมลสำเร็จ", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(viewSnackBar, "ลงทะเบียนไม่สำเร็จ !", Snackbar.LENGTH_LONG).show();
                }
                hideProgressDialog();

            }
        });
    }

    private boolean validateForm() {
        if (TextUtils.isEmpty(confirm_email.getText().toString())) {
            confirm_email.setError("Required.");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(confirm_email.getText().toString()).matches()) {
            confirm_email.setError("Invalid.");
            return false;
        } else if (TextUtils.isEmpty(confirm_password.getText().toString())) {
            confirm_password.setError("Required.");
            return false;
        } else {
            confirm_email.setError(null);
            confirm_password.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreate:
                createAccount(confirm_email.getText().toString(), confirm_password.getText().toString());
                break;
            case R.id.btnLogin:
                signIn(confirm_email.getText().toString(), confirm_password.getText().toString());
                break;
        }
    }
}

