package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MuaIntroduction extends AppCompatActivity {

    private Button btnGoMurExp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mua_introduction);
        btnGoMurExp2 = (Button) findViewById(R.id.btnGoMurExp2);

        btnGoMurExp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMuaExp = new Intent(getApplicationContext(), ExperimentMua.class);
                startActivity(intentMuaExp);
            }
        });
    }
}
