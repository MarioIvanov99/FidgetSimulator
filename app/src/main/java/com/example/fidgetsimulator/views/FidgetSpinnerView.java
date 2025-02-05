package com.example.fidgetsimulator.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.fidgetsimulator.components.ImageComponent;
import com.example.fidgetsimulator.controllers.SpinnerAnimator;
import com.example.fidgetsimulator.utils.Configuration;

public class FidgetSpinnerView extends View {
    final private ImageComponent spinnerComponent;
    private float initialTouchX = 0f, initialTouchY = 0f;
    final private SpinnerAnimator spinnerAnimator;

    public FidgetSpinnerView(Context context, Bitmap spinnerImage) {
        super(context);
        float centerX = getResources().getDisplayMetrics().widthPixels / 2f;
        float centerY = getResources().getDisplayMetrics().heightPixels / 2f;

        spinnerComponent = new ImageComponent(spinnerImage, centerX, centerY);
        spinnerAnimator = new SpinnerAnimator();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                return true;

            case MotionEvent.ACTION_UP:
                float finalTouchX = event.getRawX();
                float finalTouchY = event.getRawY();

                float velocityX = finalTouchX - initialTouchX;
                float velocityY = finalTouchY - initialTouchY;

                handleSwipe(velocityX, velocityY);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleSwipe(float velocityX, float velocityY) {
        // Calculate the swipe vector relative to the center of the spinner
        float centerX = spinnerComponent.getCenterX();
        float centerY = spinnerComponent.getCenterY();

        float swipeStartX = initialTouchX - centerX;
        float swipeStartY = initialTouchY - centerY;

        // Calculate the angle of the swipe vector
        double startAngle = Math.atan2(swipeStartY, swipeStartX);
        double endAngle = Math.atan2(velocityY, velocityX);
        double deltaAngle = Math.toDegrees(endAngle - startAngle);

        // Normalize deltaAngle to determine direction
        if (deltaAngle > 180) deltaAngle -= 360;
        if (deltaAngle < -180) deltaAngle += 360;

        // Determine rotation direction: clockwise (positive) or counterclockwise (negative)
        int direction = deltaAngle > 0 ? 1 : -1;

        // Calculate the magnitude of the swipe velocity
        float velocityMagnitude = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);

        float velocityModifier = 1f;
        if (velocityMagnitude < Configuration.VELOCITY_THRESHOLD) {
            velocityModifier = velocityMagnitude/1000f;
        }

        // Calculate the final rotation based on swipe speed and direction
        float finalRotation = spinnerAnimator.getCurrentRotation() + direction * velocityMagnitude * (float) Math.pow(velocityModifier, 2) * Configuration.ROTATION_MULTIPLIER;
        System.out.println(velocityMagnitude);
        // Dynamically calculate the duration for deceleration
        float duration = Math.min((float) Math.sqrt(velocityMagnitude) * (velocityModifier * 250f), Configuration.MAX_ANIMATION_DURATION); // Max duration: 18 seconds
        // Start the spinning animation
        spinnerAnimator.startSpin(this, finalRotation, duration);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        spinnerComponent.draw(canvas);
    }
}