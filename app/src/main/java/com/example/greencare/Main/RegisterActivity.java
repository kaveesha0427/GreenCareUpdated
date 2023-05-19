package com.example.greencare.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greencare.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText userNameEt, userEmailEt, userPasswordEt, userContactNumberEt;
    private Button userRegisterBtn;
    private TextView UserLoginBtn;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        userEmailEt = findViewById(R.id.emailEt);
        userPasswordEt = findViewById(R.id.passwordEt);
        userRegisterBtn = findViewById(R.id.registerBtn);
        UserLoginBtn = findViewById(R.id.signinTv);
        userContactNumberEt = findViewById(R.id.contactEt);
        userNameEt = findViewById(R.id.nameEt);

        registerPressed();
        loginPressed();
    }

    private void registerPressed() {
        //userAddedConfirmation();
        userRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEt.getText().toString();
                String userEmail = userEmailEt.getText().toString();
                String userPassword = userPasswordEt.getText().toString();
                String userContactNumber = userContactNumberEt.getText().toString();

                if (userEmail.isEmpty()) {
                    userEmailEt.setError("Please enter email id");
                    userEmailEt.requestFocus();
                } else if (userPassword.isEmpty()) {
                    userPasswordEt.setError("Please enter the userPassword");
                    userPasswordEt.requestFocus();
                } else {
                    if (userEmail.isEmpty() && userPassword.isEmpty() && userName.isEmpty() && userContactNumber.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Register unsuccessful,Please try again", Toast.LENGTH_SHORT);
                                } else {

                                    String user_id = firebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                    current_user_db.setValue(true);
                                    DatabaseReference userName1 = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Name");
                                    userName1.setValue(userName);
                                    DatabaseReference email = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Email");
                                    email.setValue(userEmail);
                                    DatabaseReference number = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id).child("Phone");
                                    number.setValue(userContactNumber);
                                    startActivity(new Intent(RegisterActivity.this, ConfiguratorPageActivity.class));
                                    Toast.makeText(RegisterActivity.this, "You are registered", Toast.LENGTH_SHORT);

                                }
                            }
                        });

                    }
                }

            }
        });
    }

    private void loginPressed() {
        UserLoginBtn.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
    }




}
