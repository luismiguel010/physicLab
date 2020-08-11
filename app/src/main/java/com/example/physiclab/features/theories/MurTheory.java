package com.example.physiclab.features.theories;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.physiclab.R;
import com.github.barteksc.pdfviewer.PDFView;

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
