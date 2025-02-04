package com.example.fidgetsimulator.controllers;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;

import com.example.fidgetsimulator.R;

import com.example.fidgetsimulator.utils.SpinnerConstants;

public class SoundManager {
    private MediaPlayer mediaPlayer;
    private final Context context;
    private final Handler handler;
    private Runnable fadeOutRunnable;

    public SoundManager(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void playSpinSound(float duration) {
        if (duration <= SpinnerConstants.SOUNDLESS_DURATION_THRESHOLD) return;

        releasePlayer(); // Clean up previous instance

        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.fidget_spinner1);
            if (mediaPlayer == null) return;

            int soundDuration = mediaPlayer.getDuration();
            mediaPlayer.seekTo(Math.max(0, soundDuration - (int) duration));
            mediaPlayer.start();

            scheduleFadeOut((long) duration);
        } catch (Exception e) {
            Log.e("SoundManager", "Error playing sound", e);
        }
    }

    private void scheduleFadeOut(long totalDuration) {
        if (fadeOutRunnable != null) {
            handler.removeCallbacks(fadeOutRunnable);
        }

        long fadeOutStartTime = totalDuration - 2500;
        if (fadeOutStartTime > 0) {
            fadeOutRunnable = this::fadeOutSound;
            handler.postDelayed(fadeOutRunnable, fadeOutStartTime);
        }
    }

    private void fadeOutSound() {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) return;

        ValueAnimator fadeAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        fadeAnimator.setDuration(SpinnerConstants.FADE_OUT_DURATION);
        fadeAnimator.addUpdateListener(animation -> {
            if (mediaPlayer != null) {
                float volume = (float) animation.getAnimatedValue();
                mediaPlayer.setVolume(volume, volume);
            }
        });

        fadeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                releasePlayer();
            }
        });
        fadeAnimator.start();
    }

    public void releasePlayer() {
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("SoundManager", "Error releasing player", e);
            }
            mediaPlayer = null;
        }
    }
}

