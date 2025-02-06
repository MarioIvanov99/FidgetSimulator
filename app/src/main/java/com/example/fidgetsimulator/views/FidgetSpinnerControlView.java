package com.example.fidgetsimulator.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.example.fidgetsimulator.views.FidgetSpinnerView;

public class FidgetSpinnerControlView extends FrameLayout {
    private final FidgetSpinnerView spinnerView;
    private final SeekBar speedSlider;
    private final Button toggleButton;

    public FidgetSpinnerControlView(Context context, Bitmap spinnerImage) {
        super(context);

        // Add spinner view
        spinnerView = new FidgetSpinnerView(context, spinnerImage);
        addView(spinnerView);

        // Add toggle button
        toggleButton = new Button(context);
        toggleButton.setText("Settings");
        LayoutParams buttonParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        buttonParams.gravity = Gravity.TOP | Gravity.START;
        addView(toggleButton, buttonParams);

        // Add speed slider (initially hidden)
        speedSlider = new SeekBar(context);
        speedSlider.setVisibility(GONE);
        LayoutParams sliderParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        sliderParams.gravity = Gravity.TOP | Gravity.START;
        sliderParams.topMargin = 100; // Position below button
        addView(speedSlider, sliderParams);

        setupBehavior();
    }

    private void setupBehavior() {
        // Toggle slider visibility
        toggleButton.setOnClickListener(v -> {
            speedSlider.setVisibility(
                    speedSlider.getVisibility() == VISIBLE ? GONE : VISIBLE
            );
        });

        // Configure slider
        speedSlider.setMax(100);
        speedSlider.setProgress(50);
    }

    public FidgetSpinnerView getSpinnerView() {
        return spinnerView;
    }
}