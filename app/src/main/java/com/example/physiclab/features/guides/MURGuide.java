package com.example.physiclab.features.guides;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.physiclab.BuildConfig;
import com.example.physiclab.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MURGuide extends AppCompatActivity {

    PDFView pdfView;
    private Toolbar toolbar;
    private Menu menu;
    private final static String TAG  = MURGuide.class.getSimpleName();
    private static final int PERMISSION_REQUEST = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_u_r_guide);
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUR Guía de laboratorio");
        toolbar.setTitleTextColor(Color.WHITE);
        pdfView = findViewById(R.id.mur_guide);
        pdfView.fromAsset("mur_guide.pdf").load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_mur_guide, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.exportGuide:
                exportGuide();
                Toast.makeText(MURGuide.this, "Exportar guía.", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void exportGuide(){
        String nameFile = "mur_guide.pdf";
        copyAssets();
        Context context = getApplicationContext();
        File pdfFile = new File(getFilesDir(),nameFile);//File path
        if (pdfFile.exists()) //Checking for the file is exist or not
        {
            Uri path = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() +".fileprovider", pdfFile);
            Intent objIntent = new Intent(Intent.ACTION_SEND);
            objIntent.setDataAndType(path, "application/pdf");
            objIntent.putExtra(Intent.EXTRA_SUBJECT, nameFile);
            objIntent.putExtra(Intent.EXTRA_STREAM, path);
            objIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            objIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(Intent.createChooser(objIntent, "Send guide"));
        } else {

            Toast.makeText(MURGuide.this, "The file not exists! ", Toast.LENGTH_SHORT).show();

        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getFilesDir(), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}