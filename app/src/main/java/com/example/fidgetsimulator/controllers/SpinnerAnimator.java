package com.example.fidgetsimulator.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.fidgetsimulator.utils.Configuration;

public class SpinnerAnimator {
    private ObjectAnimator spinAnimator;
    private SoundManager soundManager;
    private float currentRotation = 0f;

    public SpinnerAnimator() {
        soundManager = new SoundManager();
    }

    public void startSpin(View view, float targetRotation, float duration) {
        // Cancel any ongoing animation
        cancelAnimation();

        soundManager.cancelFadeOut();

        // Stop and reset previous sound safely
        soundManager.stopSound();

        soundManager.playSpinnerSound(view.getContext(), Configuration.SPINNER_SOUND_SOURCE, duration);

        // Create and start the animation
        spinAnimator = ObjectAnimator.ofFloat(view, "rotation", currentRotation, targetRotation);
        spinAnimator.setDuration((long) duration);
        spinAnimator.setInterpolator(new DecelerateInterpolator(Configuration.DECELERATION_FACTOR));

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
                if (!wasCancelled) {
                    soundManager.stopSound();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                wasCancelled = true;
            }
        });

        spinAnimator.start();
    }

    public void cancelAnimation() {
        if (spinAnimator != null && spinAnimator.isRunning()) {
            spinAnimator.cancel();
        }
    }

    public float getCurrentRotation() {
        return currentRotation;
    }
}



