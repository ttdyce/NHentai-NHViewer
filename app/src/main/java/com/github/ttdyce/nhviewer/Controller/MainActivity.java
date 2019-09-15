package com.github.ttdyce.nhviewer.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.github.ttdyce.nhviewer.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    void updateView(){
        TextView tvMain = findViewById(R.id.tvMain);
        tvMain.setText("");
    }
}