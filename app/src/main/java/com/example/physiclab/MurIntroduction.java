package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

public class MurIntroduction extends AppCompatActivity {

    private ImageButton btnbook;
    private ImageButton btnlab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mur_introduction);

        btnlab = (ImageButton) findViewById(R.id.imagebutton_lab);
        btnbook = (ImageButton) findViewById(R.id.imagebutton_book);

        btnlab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ExperimentsMur.class);
                startActivity(intent);
            }
        });

        btnbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MurTheory.class);
                startActivity(intent);
            }
        });


    }
}
