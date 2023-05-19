package com.example.greencare.NavigationActivity;

import android.os.Bundle;


import com.example.greencare.Main.ConfiguratorPageActivity;
import com.example.greencare.R;

import androidx.appcompat.app.AppCompatActivity;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);
        startActivity(ConfiguratorPageActivity.getIntent(this));
        finish();
    }
}
