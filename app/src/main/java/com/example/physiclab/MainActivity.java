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


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Menu menu;
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
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //toolbar.setLogo(R.drawable.ic_logo_without_border);

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

        int images[] = new int[] {R.drawable.slidemain1, R.drawable.slidemain2, R.drawable.slidemain3};

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
        v_flipper.setFlipInterval(5000);
        v_flipper.setAutoStart(true);

        v_flipper.setInAnimation(this, android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(this, android.R.anim.slide_out_right);


    }
}
