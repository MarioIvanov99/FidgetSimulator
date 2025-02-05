package com.example.fidgetsimulator.utils;

import com.example.fidgetsimulator.R;

public class Configuration {
    // Physics
    public static final float VELOCITY_THRESHOLD = 1000f;
    public static final float ROTATION_MULTIPLIER = 10f;
    public static final float MIN_VELOCITY_MODIFIER = 0.1f;

    // Animation
    public static final float MAX_ANIMATION_DURATION = 18000f;
    public static final float DECELERATION_FACTOR = 1.1f;
    public static final int SPINNER_IMAGE_SOURCE = R.drawable.fidget_spinner_black_image_2;

    // Sound
    public static final int FADE_DURATION = 1500;
    public static final int EARLY_FADE_OFFSET = 2500;
    public static final int SOUNDLESS_DURATION_THRESHOLD = 4143;
    public static final int SPINNER_SOUND_SOURCE = R.raw.fidget_spinner1;
}

