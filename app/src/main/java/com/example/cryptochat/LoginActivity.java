package com.example.cryptochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText emailText;
    EditText passwordText;
    String email;
    String passw;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("GİRİŞ YAP");


        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn() == true) {

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }


        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.Email);
        passwordText = findViewById(R.id.Password);
    }

    public void login(View view) {
        email = emailText.getText().toString();
        passw = passwordText.getText().toString();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(passw)) {

            Toast.makeText(LoginActivity.this, "Boş Alanları Doldurunuz..!", Toast.LENGTH_SHORT).show();
            return;
        } else if (passw.length() < 6) {
            Toast.makeText(getApplicationContext(), "Parola en az 6 Haneden Oluşmalı..!", Toast.LENGTH_LONG).show();

        } else {
            mAuth.signInWithEmailAndPassword(email, passw).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    session.createLoginSession(email, passw);


                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();


                }
            });

        }

    }

    public void register(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
        finish();
    }


}
