package com.example.fidgetsimulator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fidgetsimulator.FidgetSpinnerView;
import com.example.fidgetsimulator.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the spinner image from resources
        Bitmap spinnerImage = BitmapFactory.decodeResource(getResources(), R.drawable.fidget_spinner_black_image_2);

        // Create the FidgetSpinnerView
        FidgetSpinnerView spinnerView = new FidgetSpinnerView(this, spinnerImage);

        // Set the spinner view as the content view
        FrameLayout layout = new FrameLayout(this);
        layout.addView(spinnerView);
        setContentView(layout);
    }
}