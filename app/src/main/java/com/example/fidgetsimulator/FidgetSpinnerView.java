package com.example.fidgetsimulator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
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
    private MediaPlayer mediaPlayer;

    public FidgetSpinnerView(Context context, Bitmap spinnerImage) {
        super(context);
        float centerX = getResources().getDisplayMetrics().widthPixels / 2f;
        float centerY = getResources().getDisplayMetrics().heightPixels / 2f;

        spinnerComponent = new ImageComponent(spinnerImage, centerX, centerY);
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
        if (velocityMagnitude < 1000) {
            velocityModifier = velocityMagnitude/1000f;
        }

        // Calculate the final rotation based on swipe speed and direction
        float finalRotation = currentRotation + direction * velocityMagnitude * (float) Math.pow(velocityModifier, 2) * 10;
        System.out.println(velocityMagnitude);
        // Dynamically calculate the duration for deceleration
        float duration = Math.min((float) Math.sqrt(velocityMagnitude) * (velocityModifier * 250f), 18000); // Max duration: 10 seconds
        // Start the spinning animation
        startSpin(finalRotation, duration);
    }

    private Handler fadeOutHandler = new Handler(Looper.getMainLooper());
    private Runnable fadeOutRunnable;

    private void startSpin(float targetRotation, float duration) {
        // Cancel any ongoing animation
        if (spinAnimator != null && spinAnimator.isRunning()) {
            spinAnimator.cancel();
        }

        // Cancel any pending fade-out task
        if (fadeOutRunnable != null) {
            fadeOutHandler.removeCallbacks(fadeOutRunnable);
        }

        // Stop and reset previous sound safely
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        //Duration for a velocity of ~650
        int soundlessLimit = 4143;

        if (duration > soundlessLimit) {
            // Load new sound
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.fidget_spinner1);
            if (mediaPlayer == null) return;  // Ensure mediaPlayer is valid

            // Get sound file duration
            int soundDuration = mediaPlayer.getDuration();
            int skipTime = Math.max(0, soundDuration - (int) duration);

            // Start playback
            mediaPlayer.start();
            mediaPlayer.seekTo(skipTime);

            // Schedule fade-out 2 seconds before animation ends
            long fadeOutStartTime = (long) duration - 2500;
            if (fadeOutStartTime > 0) {
                fadeOutRunnable = () -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        fadeOutSound(1500);
                    }
                };
                fadeOutHandler.postDelayed(fadeOutRunnable, fadeOutStartTime);
            }
        }

        // Create and start the animation
        spinAnimator = ObjectAnimator.ofFloat(this, "rotation", currentRotation, targetRotation);
        spinAnimator.setDuration((long) duration);
        spinAnimator.setInterpolator(new DecelerateInterpolator(1.1f));

        spinAnimator.addUpdateListener(animation -> {
            currentRotation = (float) animation.getAnimatedValue() % 360;
        });

        // Stop sound completely when animation ends
        spinAnimator.addListener(new AnimatorListenerAdapter() {
            private boolean wasCancelled = false;

            @Override
            public void onAnimationStart(Animator animation) {
                wasCancelled = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!wasCancelled && mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCancelled = true;
            }
        });

        spinAnimator.start();
    }

    // Updated fade-out function
    private void fadeOutSound(int fadeDuration) {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) return;

        ValueAnimator fadeAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        fadeAnimator.setDuration(fadeDuration);
        fadeAnimator.addUpdateListener(animation -> {
            if (mediaPlayer != null) {
                float volume = (float) animation.getAnimatedValue();
                mediaPlayer.setVolume(volume, volume);
            }
        });

        fadeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });

        fadeAnimator.start();
    }


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        spinnerComponent.draw(canvas);
    }
}