package com.example.greencare.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;


import com.example.greencare.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button userLoginBtn;
    private EditText userEmail, userPassword;
    private TextView userRegisterTv, loginBackBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userPassword = findViewById(R.id.userPasswordEText);
        userEmail = findViewById(R.id.userEmailEText);
        userRegisterTv = findViewById(R.id.userRegisterTv);
        loginBackBtn = findViewById(R.id.loginBackBtn);
        userLoginBtn = findViewById(R.id.userLoginBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        setUpFirebase();
        register();
        login();
        back();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void register() {
        userRegisterTv.setOnClickListener(v -> {
            Intent newIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(newIntent);
        });

    }

    private void login() {
        userLoginBtn.setOnClickListener(v -> {
            String email = userEmail.getText().toString();
            String password = userPassword.getText().toString();
            if (email.isEmpty()) {
                userEmail.setError("Please enter email id");
                userEmail.requestFocus();
            } else if (password.isEmpty()) {
                userPassword.setError("Please enter the password");
                userPassword.requestFocus();
            } else if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();

                        } else {
                            Intent newIntent = new Intent(LoginActivity.this, UserProfilePage.class);
                            startActivity(newIntent);

                        }
                    }
                });

            }
        });
    }

    private void setUpFirebase() {
        authStateListener = firebaseAuth -> {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(LoginActivity.this, UserProfilePage.class);
                startActivity(newIntent);

            } else {
                Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void back() {
        loginBackBtn.setOnClickListener(v -> {
            Intent newIntent = new Intent(LoginActivity.this, ConfiguratorPageActivity.class);
            startActivity(newIntent);
        });
    }

}
