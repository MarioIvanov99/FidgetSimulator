package com.example.fidgetsimulator.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fidgetsimulator.R;
import com.example.fidgetsimulator.utils.Configuration;
import com.example.fidgetsimulator.views.FidgetSpinnerView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the spinner image from resources
        Bitmap spinnerImage = BitmapFactory.decodeResource(getResources(), Configuration.SPINNER_IMAGE_SOURCE);

        // Create the FidgetSpinnerView
        FidgetSpinnerView spinnerView = new FidgetSpinnerView(this, spinnerImage);

        // Set the spinner view as the content view
        FrameLayout layout = new FrameLayout(this);
        layout.addView(spinnerView);
        setContentView(layout);
    }
}