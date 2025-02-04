package com.example.fidgetsimulator.controllers;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.fidgetsimulator.utils.SpinnerConstants;

public class SpinAnimator {
    private ObjectAnimator animator;
    private final View targetView;
    private Animator.AnimatorListener listener;

    public SpinAnimator(View targetView) {
        this.targetView = targetView;
    }

    public void startSpin(float targetRotation, float duration) {
        cancelAnimation();

        animator = ObjectAnimator.ofFloat(targetView, "rotation",
                targetView.getRotation(), targetRotation);
        animator.setDuration((long) duration);
        animator.setInterpolator(new DecelerateInterpolator(
                SpinnerConstants.DECELERATE_FACTOR));

        if (listener != null) {
            animator.addListener(listener);
        }

        animator.start();
    }

    public void setAnimationListener(Animator.AnimatorListener listener) {
        this.listener = listener;
    }

    public void cancelAnimation() {
        if (animator != null) {
            animator.cancel();
            animator.removeAllListeners();
        }
    }
}


