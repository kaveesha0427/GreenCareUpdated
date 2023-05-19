package com.example.greencare.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.greencare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserFeedBackActivity extends AppCompatActivity {
    private EditText userFeedBack;
    private Button submitFeedbackBtn;
    private FirebaseAuth firebaseAuth;
    private BottomNavigationView bottomNav;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_feedback);
        firebaseAuth = FirebaseAuth.getInstance();
        submitFeedbackBtn = findViewById(R.id.feedbackBtn);
        userFeedBack = findViewById(R.id.feedbackEt);
        bottomNav = findViewById(R.id.bottom_nav);
        checkIfUserLogged();
        setUpNav();
    }
    private void checkIfUserLogged() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Toast.makeText(UserFeedBackActivity.this, "Log in or Sign up first !", Toast.LENGTH_SHORT).show();
                } else {
                    submitFeedback();
                }
            }
        };

    }
    private void submitFeedback (){
        submitFeedbackBtn.setOnClickListener(v -> {
            String userFeedback = userFeedBack.getText().toString();
            String userId = firebaseAuth.getCurrentUser().getUid();
            DatabaseReference currentUserDetails = FirebaseDatabase.getInstance().getReference().child("Feedback").child(userId);
            currentUserDetails.setValue(userFeedback);

        });
    }

    private void setUpNav() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(), ConfiguratorPageActivity.class));
                    return true;
                case R.id.nav_account:
                    startActivity(new Intent(getApplicationContext(), UserProfilePage.class));
                    return true;
                case R.id.nav_history:
                    startActivity(new Intent(getApplicationContext(), HistoryPageActivity.class));
                    return true;
                case R.id.nav_feedback:
                    startActivity(new Intent(getApplicationContext(), UserFeedBackActivity.class));
            }
            return false;
        });
    }
}