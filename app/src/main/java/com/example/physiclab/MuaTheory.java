package com.example.physiclab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class MuaTheory extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mua_theory);

        pdfView = findViewById(R.id.pdf_mua);
        pdfView.fromAsset("MUA_Theory.pdf")
                .load();
    }
}
