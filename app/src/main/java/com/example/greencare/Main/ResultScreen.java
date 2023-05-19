package com.example.greencare.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greencare.Adapters.ControlMeasuresAdapter;
import com.example.greencare.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResultScreen extends AppCompatActivity {


    private TextView mResultTextView, resultStageTV, confidenceTV;
    private Button savePredictionsBtn;
    private ImageView selectedImage;
    private Bitmap bitmap;
    private ListView listView;

    private ControlMeasuresAdapter controlMeasuresAdapter;

    ArrayList<String> suggestionList;
    private static String stage;
    private static DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private BottomNavigationView bottomNav;
    String[] splitString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        firebaseAuth = FirebaseAuth.getInstance();
        resultStageTV = findViewById(R.id.result_stage);
        confidenceTV = findViewById(R.id.prediction_confidence);
        selectedImage = findViewById(R.id.selected_image);
        mResultTextView = findViewById(R.id.result_text_view);
        savePredictionsBtn = findViewById(R.id.saveBtn);
        bottomNav = findViewById(R.id.bottom_nav);
        setUpPredictions();
        listView = findViewById(R.id.expandableLv);


        checkIfUserLogged();
        setUpNav();
        setControlMeasures();

    }

    private void setUpPredictions() {
        String result = getIntent().getStringExtra("result");
        bitmap = getIntent().getParcelableExtra("bitmap");
        String confidence = getIntent().getStringExtra("confidence");

        selectedImage.setImageBitmap(bitmap);
        String currentString = result;
        splitString = currentString.split("_");
        mResultTextView.setText(splitString[0]);
        confidenceTV.setText("Prediction Confidence : " + confidence);
        if (splitString[0].equals("Healthy")) {
            resultStageTV.setText("Plant has no diseases");
            stage = "Healthy";
        } else {
            resultStageTV.setVisibility((View.VISIBLE));
            resultStageTV.setText("Infection stage : " + splitString[1]);
            stage = splitString[1];

        }

    }

    private void setControlMeasures() {
        suggestionList = new ArrayList<>();


        if (stage.equals("stage1")) {
            ref = FirebaseDatabase.getInstance().getReference().child("Suggestions").child("stage01");
        } else if (stage.equals("stage2")) {
            ref = FirebaseDatabase.getInstance().getReference().child("Suggestions").child("stage02");
        } else {
            ref = FirebaseDatabase.getInstance().getReference().child("Suggestions").child("healthy");
        }
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dataChemical = dataSnapshot.child("Chemical").getValue().toString();
                    suggestionList.add(dataChemical);
                    String dataNonChemical = dataSnapshot.child("nonChemical").getValue().toString();
                    suggestionList.add(dataNonChemical);
                    controlMeasuresAdapter = new ControlMeasuresAdapter(getApplicationContext(), suggestionList);
                    listView.setAdapter(controlMeasuresAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void savePredictions() {
        savePredictionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = firebaseAuth.getCurrentUser().getUid();
                if (!user_id.equals(null)) {
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                    String i = timeStamp();

                    current_user_db.child("SavedData").child(i).child("result").setValue(splitString[0]);
                    current_user_db.child("SavedData").child(i).child("bitmap").setValue(BitMapToString(bitmap));

                    if (splitString[0].equals("Healthy")) {
                        current_user_db.child("SavedData").child(i).child("stage").setValue("");

                    } else {
                        current_user_db.child("SavedData").child(i).child("stage").setValue(stage);
                    }
                    Toast.makeText(ResultScreen.this, "Prediction Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResultScreen.this, "Sign up first!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private void checkIfUserLogged() {

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Toast.makeText(ResultScreen.this, "Sign in or Sign up first!", Toast.LENGTH_SHORT).show();
                } else {
                    savePredictions();
                }
            }
        };

    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public String timeStamp() {
        Date date = new Date();
        long time = date.getTime();
        String timeS = Long.toString(time);
        System.out.println("Time in Milliseconds: " + timeS);
        return timeS;

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


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}