package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class MurTheory extends AppCompatActivity {

    private Button btnGoMurExp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mur_theory);
        btnGoMurExp = (Button) findViewById(R.id.btnGoMurExp);

        btnGoMurExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMurTheory = new Intent(getApplicationContext(), ExperimentsMur.class);
                startActivity(intentMurTheory);
            }
        });
    }
}
