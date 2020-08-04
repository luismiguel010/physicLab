package com.example.physiclab.features.theories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.physiclab.R;
import com.github.barteksc.pdfviewer.PDFView;

public class Muc_Theory extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muc__theory);

        pdfView = findViewById(R.id.pdf_muc);
        pdfView.fromAsset("MUC_Theory.pdf")
                .load();
    }
}
