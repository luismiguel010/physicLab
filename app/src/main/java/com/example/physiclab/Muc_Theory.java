package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class Muc_Theory extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muc__theory);

        pdfView = findViewById(R.id.pdf_mua);
        pdfView.fromAsset("MUC_Theory.pdf")
                .load();
    }
}
