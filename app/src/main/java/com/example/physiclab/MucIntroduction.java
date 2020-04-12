package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MucIntroduction extends AppCompatActivity {

    private Button btnGoMurExp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_muc_introduction);
        btnGoMurExp3 = (Button) findViewById(R.id.btnGoMurExp3);

        btnGoMurExp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMucExp = new Intent(getApplicationContext(), ExperimentMuc.class);
                startActivity(intentMucExp);
            }
        });
    }
}
