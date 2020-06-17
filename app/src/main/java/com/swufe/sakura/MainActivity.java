package com.swufe.sakura;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
   Button checkChina,checekWorld;
    Button btn_2,btn_3;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkChina=(Button) findViewById(R.id.china);
        checekWorld=(Button)findViewById(R.id.world);
        btn_2=(Button) findViewById(R.id.search);
        checkChina.setOnClickListener(this);
        checekWorld.setOnClickListener(this);
        btn_2.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.china){
            Intent intent = new Intent(MainActivity.this, ChinaActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.world){
                Intent intent = new Intent(MainActivity.this,WorldActivity.class);
                startActivity(intent);
        }else if(v.getId()==R.id.search){
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }

    }}

