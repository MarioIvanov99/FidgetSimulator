package com.example.fidgetsimulator.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class ImageComponent {
    private Bitmap image;
    private float centerX, centerY;

    public ImageComponent(Bitmap image, float centerX, float centerY) {
        this.image = image;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void draw(Canvas canvas) {
        // Draw the image at the specified center position
        canvas.drawBitmap(image, centerX - image.getWidth() / 2, centerY - image.getHeight() / 2, null);
    }
}
