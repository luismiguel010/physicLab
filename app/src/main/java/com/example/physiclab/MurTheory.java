package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import static android.Manifest.permission.RECORD_AUDIO;

public class MurTheory extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mur_theory);
            pdfView = findViewById(R.id.pdf_mur);
            pdfView.fromAsset("MUR_Theory.pdf")
                    .load();
    }
}
