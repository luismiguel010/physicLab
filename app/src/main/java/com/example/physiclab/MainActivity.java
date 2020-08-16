package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.example.physiclab.features.cuestionaries.ProblemsMua;
import com.example.physiclab.features.cuestionaries.ProblemsMuc;
import com.example.physiclab.features.cuestionaries.ProblemsMur;
import com.example.physiclab.features.sensors.environmental.MenuEnvironmental;
import com.example.physiclab.features.sensors.movement.MenuMovementSensor;
import com.example.physiclab.features.sensors.position.MenuPositionSensor;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Menu menu;
    ViewFlipper v_flipper;
    private ImageButton btnMurExp, btnMuaExp, btnMucExp, btnSensorMov, btnSensorPos, btnSensorEnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        btnMurExp = findViewById(R.id.btnMurExp);
        btnMuaExp = findViewById(R.id.btnMuaExp);
        btnMucExp = findViewById(R.id.btnMucExp);
        btnSensorMov = findViewById(R.id.btnSensorMov);
        btnSensorPos = findViewById(R.id.btnSensorPos);
        btnSensorEnv = findViewById(R.id.btnSensorAmb);

        btnMurExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMurExp = new Intent(getApplicationContext(), MurIntroduction.class);
                startActivity(intentMurExp);
            }
        });

        btnMuaExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMuaExp = new Intent(getApplicationContext(), MuaIntroduction.class);
                startActivity(intentMuaExp);
            }
        });

        btnMucExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMucExp = new Intent(getApplicationContext(), MucIntroduction.class);
                startActivity(intentMucExp);
            }
        });

        btnSensorMov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMenuMov = new Intent(getApplicationContext(), MenuMovementSensor.class);
                startActivity(intentMenuMov);
            }
        });

        btnSensorPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMenuPos = new Intent(getApplicationContext(), MenuPositionSensor.class);
                startActivity(intentMenuPos);
            }
        });

        btnSensorEnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMenuAmb = new Intent(getApplicationContext(), MenuEnvironmental.class);
                startActivity(intentMenuAmb);
            }
        });

        int images[] = new int[] {R.drawable.slide1, R.drawable.slide2, R.drawable.slide3,
        R.drawable.slide4, R.drawable.slide5};

        v_flipper = findViewById(R.id.v_flipper);

        for(int image: images){
            flipperImage(image);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void flipperImage(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(15000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this, android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this, android.R.anim.slide_out_right);


    }
}
