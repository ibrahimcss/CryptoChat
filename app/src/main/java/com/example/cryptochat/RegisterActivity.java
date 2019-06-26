package com.example.cryptochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText emailText;
    EditText usernameText;
    EditText passwordText;
    EditText nameText;
    EditText surnameText;
    String email;
    String password;
    String userName;
    String name;
    String surname;
    String TAG = "Sonuc";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("KAYIT EKRANI");

        mAuth = FirebaseAuth.getInstance();

        emailText = findViewById(R.id.Email);
        usernameText = findViewById(R.id.UserName);
        passwordText = findViewById(R.id.Password);
        nameText = findViewById(R.id.Name);
        surnameText = findViewById(R.id.SurName);

    }

    public void register(View view) {
        email = emailText.getText().toString();
        password = passwordText.getText().toString();
        userName = usernameText.getText().toString();
        name = nameText.getText().toString();
        surname = surnameText.getText().toString();
        if (password.equals("")) {

            Toast.makeText(this, "Lütfen Parola Alanını Doldurunuz", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "Lütfen Parola 6 Karakterden Küçük Olamaz", Toast.LENGTH_LONG).show();

        } else {

            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users");
            mAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            Toast.makeText(RegisterActivity.this, "Kayıt Başarılı", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                            i.putExtra("username", userName);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            startActivity(i);
                            finish();


                            dbRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                                }
                            });
                            UserPreferences userPreferences = UserPreferences.getInstance(getApplicationContext());
                            userPreferences.saveData("username", userName);

                            Random rand = new Random();
                            int key = rand.nextInt(50);

                            UserKeyPreferences userKeyPreferences= UserKeyPreferences.getInstance(getApplicationContext());
                            userKeyPreferences.saveData("username",key);
                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });


            dbRef.push().setValue(
                    new User(
                            1,
                            emailText.getText().toString(),
                            passwordText.getText().toString(),
                            nameText.getText().toString(),
                            surnameText.getText().toString(),
                            usernameText.getText().toString()

                    )
            );

        }


    }

    public void login(View view) {

        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
