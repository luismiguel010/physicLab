package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ViewFlipper;


public class MainActivity extends AppCompatActivity {
    ViewFlipper v_flipper;
    private ImageButton btnMur;
    private ImageButton btnMua;
    private ImageButton btnMuc;
    private ImageButton btnMurExp;
    private ImageButton btnMuaExp;
    private ImageButton btnMucExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        btnMur = (ImageButton) findViewById(R.id.btnMur);
        btnMua = (ImageButton) findViewById(R.id.btnMua);
        btnMuc = (ImageButton) findViewById(R.id.btnMcu);
        btnMurExp = (ImageButton) findViewById(R.id.btnMurExp);
        btnMuaExp = (ImageButton) findViewById(R.id.btnMuaExp);
        btnMucExp = (ImageButton) findViewById(R.id.btnMucExp);

        btnMur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMur = new Intent(getApplicationContext(), ProblemsMur.class);
                startActivity(intentMur);
            }
        });

        btnMua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMua = new Intent(getApplicationContext(), ProblemsMua.class);
                startActivity(intentMua);
            }
        });

        btnMuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMuc = new Intent(getApplicationContext(), ProblemsMuc.class);
                startActivity(intentMuc);
            }
        });

        btnMurExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMurExp = new Intent(getApplicationContext(), MurTheory.class);
                startActivity(intentMurExp);
            }
        });

        btnMuaExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMuaExp = new Intent(getApplicationContext(), MuaTheory.class);
                startActivity(intentMuaExp);
            }
        });

        btnMucExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMucExp = new Intent(getApplicationContext(), MucTheory.class);
                startActivity(intentMucExp);
            }
        });




        int images[] = new int[] {R.drawable.slide1main,R.drawable.slide2main, R.drawable.slide3main};

        v_flipper = findViewById(R.id.v_flipper);

        for(int image: images){
            flipperImage(image);
        }

    }

    public void flipperImage(int image){
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(5000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this, android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this, android.R.anim.slide_out_right);


    }
}
