package com.example.fidgetsimulator.views;

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
import com.example.fidgetsimulator.controllers.SoundManager;
import com.example.fidgetsimulator.controllers.SpinAnimator;
import com.example.fidgetsimulator.utils.PhysicsUtils;

public class FidgetSpinnerView extends View {
    private final ImageComponent spinnerComponent;
    private final SpinAnimator spinAnimator;
    private final SoundManager soundManager;
    private float currentRotation = 0f;
    private float initialTouchX, initialTouchY;

    public FidgetSpinnerView(Context context, Bitmap spinnerImage) {
        super(context);
        spinnerComponent = new ImageComponent(spinnerImage, getCenterX(), getCenterY());
        spinAnimator = new SpinAnimator(this);
        soundManager = new SoundManager(context);
        setupAnimationListeners();
    }

    private float getCenterX() {
        return getResources().getDisplayMetrics().widthPixels / 2f;
    }

    private float getCenterY() {
        return getResources().getDisplayMetrics().heightPixels / 2f;
    }

    private void setupAnimationListeners() {
        spinAnimator.setAnimationListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                soundManager.releasePlayer();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                soundManager.releasePlayer();
            }
        });
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

        // Determine rotation direction: clockwise (positive) or counterclockwise (negative)
        int direction = PhysicsUtils.SwipePhysics.calculateDirection(velocityX, velocityY, swipeStartX, swipeStartY);

        // Calculate the magnitude of the swipe velocity
        float velocityMagnitude = (float) Math.hypot(velocityX, velocityY);

        float velocityModifier = 1f;
        if (velocityMagnitude < 1000) {
            velocityModifier = velocityMagnitude/1000f;
        }

        // Calculate the final rotation based on swipe speed and direction
        float finalRotation = PhysicsUtils.SwipePhysics.calculateRotation(velocityMagnitude, velocityModifier, currentRotation, direction);

        // Dynamically calculate the duration for deceleration
        float duration = PhysicsUtils.SwipePhysics.calculateDuration(velocityMagnitude, velocityModifier);
        // Start the spinning animation
        startSpin(finalRotation, duration);
    }

    private Handler fadeOutHandler = new Handler(Looper.getMainLooper());
    private Runnable fadeOutRunnable;

    private void startSpin(float targetRotation, float duration) {
        spinAnimator.startSpin(targetRotation, duration);
        soundManager.playSpinSound(duration);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        spinnerComponent.draw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        soundManager.releasePlayer();
        spinAnimator.cancelAnimation();
    }
}