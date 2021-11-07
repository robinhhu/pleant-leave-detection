package com.example.plantleave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.returnButton)
    public void upload(View view) {
        Intent intent=new Intent(ResultActivity.this,MainActivity.class);
        startActivity(intent);
    }
}