package com.example.physiclab;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ExperimentsMur extends AppCompatActivity {

    private ViewGroup mainLayout;
    private ImageView imageAirplane;
    private int xDelta;
    private int yDelta;
    private int distanceX;
    private int distanceY;
    private double time;
    private float velocityX;
    private float velocityY;
    private float velocity;
    String showVelocity;
    private long startTimestamp;
    private long finishTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiments_mur);
        mainLayout = (RelativeLayout) findViewById(R.id.main);
        imageAirplane = (ImageView) findViewById(R.id.imageAirplane);
        imageAirplane.setOnTouchListener(onTouchListener());
    }

    private OnTouchListener onTouchListener() {
        return new OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        startTimestamp = System.currentTimeMillis();
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        finishTimestamp = System.currentTimeMillis();
                        time = (finishTimestamp - startTimestamp)/1000.0;
                        velocityX = (float) (distanceX/time);
                        velocityY = (float) (distanceY/time);
                        velocity = (float) Math.sqrt(Math.pow(velocityX,2)+Math.pow(velocityY,2));
                        showVelocity = Float.toString(velocity);
                        Toast.makeText(ExperimentsMur.this,
                                "Velocidad="+showVelocity+" pix/s", Toast.LENGTH_SHORT)
                                .show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        distanceX = layoutParams.leftMargin;
                        distanceY = layoutParams.topMargin;
                        view.setLayoutParams(layoutParams);
                        break;
                }

                mainLayout.invalidate();
                return true;
            }
        };
    }

}
