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

import com.example.fidgetsimulator.utils.Configuration;

public class SoundManager {
    private MediaPlayer mediaPlayer;
    private Handler fadeOutHandler;
    private Runnable fadeOutRunnable;

    public SoundManager() {
        fadeOutHandler = new Handler(Looper.getMainLooper());
    }

    public void playSpinnerSound(Context context, int resourceId, float duration) {
        if (duration > Configuration.SOUNDLESS_DURATION_THRESHOLD) {
            // Load new sound
            mediaPlayer = MediaPlayer.create(context, resourceId);
            if (mediaPlayer == null) return;  // Ensure mediaPlayer is valid

            // Get sound file duration
            int soundDuration = mediaPlayer.getDuration();
            int skipTime = Math.max(0, soundDuration - (int) duration);

            // Start playback
            mediaPlayer.start();
            mediaPlayer.seekTo(skipTime);

            long fadeOutStartTime = (long) duration - Configuration.EARLY_FADE_OFFSET;
            if (fadeOutStartTime > 0) {
                fadeOutRunnable = () -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        fadeOutSound(Configuration.FADE_DURATION);
                    }
                };
                fadeOutHandler.postDelayed(fadeOutRunnable, fadeOutStartTime);
            }
        }
    }

    public void fadeOutSound(int fadeDuration) {
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

    public void cancelFadeOut(){
        // Cancel any pending fade-out task
        if (fadeOutRunnable != null) {
            fadeOutHandler.removeCallbacks(fadeOutRunnable);
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}


