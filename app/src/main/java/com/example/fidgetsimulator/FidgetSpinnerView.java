package com.example.fidgetsimulator;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.fidgetsimulator.components.ImageComponent;

public class FidgetSpinnerView extends View {
    final private ImageComponent spinnerComponent;
    private float currentRotation = 0f;
    private float initialTouchX = 0f, initialTouchY = 0f;
    private ObjectAnimator spinAnimator;

    private SoundPool soundPool;
    private int spinnerSoundId;
    private int currentStreamId = -1;

    public FidgetSpinnerView(Context context, Bitmap spinnerImage) {
        super(context);
        float centerX = getResources().getDisplayMetrics().widthPixels / 2f;
        float centerY = getResources().getDisplayMetrics().heightPixels / 2f;

        spinnerComponent = new ImageComponent(spinnerImage, centerX, centerY);

        soundPool = new SoundPool.Builder().setMaxStreams(1).build();
        spinnerSoundId = soundPool.load(context, R.raw.fidget_spinner1, 1);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                System.out.println(initialTouchX + " " + initialTouchY);
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
        if (velocityMagnitude < 400) {
            velocityModifier = velocityMagnitude/1000f;
        }

        // Calculate the final rotation based on swipe speed and direction
        float finalRotation = currentRotation + direction * velocityMagnitude * velocityModifier * 10;

        // Dynamically calculate the duration for deceleration
        float duration = Math.min(velocityMagnitude * 50f, 10000); // Max duration: 10 seconds

        // Start the spinning animation
        startSpin(finalRotation, duration);
    }

    private void startSpin(float targetRotation, float duration) {

        // Start playing the sound
        if (currentStreamId != -1) {
            soundPool.stop(currentStreamId); // Stop any currently playing sound
        }
        currentStreamId = soundPool.play(spinnerSoundId, 1f, 1f, 0, -1, 1f); // Loop the sound

        // Cancel any ongoing animation
        if (spinAnimator != null && spinAnimator.isRunning()) {
            spinAnimator.cancel();
        }

        // Create an ObjectAnimator to rotate the spinner
        spinAnimator = ObjectAnimator.ofFloat(this, "rotation", currentRotation, targetRotation);

        // Increase the duration for slower deceleration
        spinAnimator.setDuration((long) duration);

        // Use a DecelerateInterpolator for natural slowing
        spinAnimator.setInterpolator(new DecelerateInterpolator(1.0f)); // Adjust factor for smoother deceleration

        // Update currentRotation on every frame
        spinAnimator.addUpdateListener(animation -> {
            currentRotation = (float) animation.getAnimatedValue() % 360;
        });

        // Start the animation
        spinAnimator.start();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        spinnerComponent.draw(canvas);
    }
}