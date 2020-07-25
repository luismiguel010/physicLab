package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;

public class MurIntroduction extends AppCompatActivity {

    private ImageButton btnbook;
    private ImageButton btnlab;
    public static final int RequestPermissionCode = 1;

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
                    if(checkPermission()) {
                    Intent intent = new Intent(getApplicationContext(), ExperimentMURTimer.class);
                    startActivity(intent);
                    }else {
                        requestPermission();
                    }
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

    private void requestPermission() {
        ActivityCompat.requestPermissions(MurIntroduction.this, new String[]{RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean RecordPermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (RecordPermission) {
                        Toast.makeText(MurIntroduction.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MurIntroduction.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }
}
