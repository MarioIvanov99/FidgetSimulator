package com.example.fidgetsimulator.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ImageComponent {
    final private Bitmap image;
    final private float centerX, centerY;

    public ImageComponent(Bitmap image, float centerX, float centerY) {
        this.image = image;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, centerX - image.getWidth() / 2f, centerY - image.getHeight() / 2f, null);
    }

    public Bitmap getBitmap() {
        return image;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }
}
