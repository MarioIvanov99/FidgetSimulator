package com.example.fidgetsimulator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.example.fidgetsimulator.components.ImageComponent;

public class FidgetSpinnerView extends View {
    private ImageComponent spinnerComponent;
    private float currentRotation = 0f; // Track the current rotation angle
    private ObjectAnimator spinAnimator;
    private boolean isHolding = false;

    public FidgetSpinnerView(Context context, Bitmap spinnerImage) {
        super(context);
        float centerX = getResources().getDisplayMetrics().widthPixels / 2f;
        float centerY = getResources().getDisplayMetrics().heightPixels / 2f;

        spinnerComponent = new ImageComponent(spinnerImage, centerX, centerY);
    }

    private void startSpin() {
        // Cancel any ongoing animation
        if (spinAnimator != null && spinAnimator.isRunning()) {
            spinAnimator.cancel();
        }

        // Create an ObjectAnimator to spin the View
        spinAnimator = ObjectAnimator.ofFloat(this, "rotation", currentRotation, currentRotation + 360);
        spinAnimator.setDuration(1000); // 1 second per rotation
        spinAnimator.setInterpolator(new LinearInterpolator());
        spinAnimator.setRepeatCount(ObjectAnimator.INFINITE);

        // Update currentRotation on every frame
        spinAnimator.addUpdateListener(animation -> {
            currentRotation = (float) animation.getAnimatedValue() % 360;
        });

        // Start the animation
        spinAnimator.start();
    }

    private void stopSpin() {
        // Cancel the spinning animation
        if (spinAnimator != null && spinAnimator.isRunning()) {
            spinAnimator.cancel();
        }

        // Create a deceleration animation
        float finalRotation = currentRotation + 360; // Add one full rotation
        ObjectAnimator decelerateAnimator = ObjectAnimator.ofFloat(this, "rotation", currentRotation, finalRotation);
        decelerateAnimator.setDuration(2000); // Slow down over 2 seconds
        decelerateAnimator.setInterpolator(new DecelerateInterpolator());

        // Update the current rotation angle when the animation ends
        decelerateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                // Update currentRotation to the final angle
                currentRotation = getRotation() % 360;
                System.out.println(currentRotation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        // Update currentRotation on every frame
        decelerateAnimator.addUpdateListener(animation -> {
            currentRotation = (float) animation.getAnimatedValue() % 360;
        });

        // Start the deceleration animation
        decelerateAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isHolding = true;
                startSpin();
                break;

            case MotionEvent.ACTION_UP:
                isHolding = false;
                stopSpin();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        spinnerComponent.draw(canvas);
    }
}