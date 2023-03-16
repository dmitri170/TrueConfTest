package com.example.trueconftest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private Handler moveHandler = new Handler(Looper.myLooper());
    private ObjectAnimator animation;
    private float initialX, initialY;
    private float initialPosX, initialPosY;
    private int currentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        textView.setText(getString(R.string.hello)); // default text
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(17.0f);
        textView.setAllCaps(true);

        setCurrentColor(getResources().getConfiguration()); // set default color based on the device configuration

        // Set onTouchListener to handle click events
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // store initial touch point and view's position
                        initialX = event.getX();
                        initialY = event.getY();
                        initialPosX = textView.getX();
                        initialPosY = textView.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // update view's position based on touch move
                        textView.setX(event.getX() - initialX + initialPosX);
                        textView.setY(event.getY() - initialY + initialPosY);
                        break;
                    case MotionEvent.ACTION_UP:
                        // calculate distance moved from initial touch point
                        float distance = (float) Math.sqrt(Math.pow(event.getX() - initialX, 2)
                                + Math.pow(event.getY() - initialY, 2));
                        if (distance < 10) {
                            // if distance is small, it's a tap; stop the animation
                            if (animation != null) {
                                animation.cancel();
                                animation = null;
                            }
                        } else {
                            // if distance is large, animate view's movement
                            animateViewMovement(event);
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void animateViewMovement(MotionEvent event) {
        float x = event.getX() - textView.getWidth() / 2;
        float y = event.getY() - textView.getHeight() / 2;
        float finalY = textView.getRootView().getHeight() - textView.getHeight() * 2;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Update the color based on the new configuration (language)
        setCurrentColor(newConfig);
    }

    private void setCurrentColor(Configuration configuration) {
        if (configuration.getLocales().get(0).getLanguage().equals("ru")) {
            currentColor = Color.BLUE;
            textView.setText(getString(R.string.hello_ru));
            textView.setTextColor(currentColor);
        } else {
            currentColor = Color.RED;
        }
    }

    private void startAnimation() {
        animation = ObjectAnimator.ofFloat(textView, "translationY", textView.getY(), textView.getY() + 500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(10000);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                moveHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation();
                    }
                }, 5000);
            }
        });
        animation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the animation when app is paused
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the animation when app is resumed
        if (animation == null) {
            startAnimation();
        }
    }
}