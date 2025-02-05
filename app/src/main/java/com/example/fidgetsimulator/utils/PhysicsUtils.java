/*package com.example.fidgetsimulator.utils;

public class PhysicsUtils {
    public static class SwipePhysics {
        public static float calculateRotation(float velocityMagnitude, float velocityModifier, float currentRotation, int direction) {
            // Calculate the final rotation based on swipe speed and direction
            return currentRotation + direction * velocityMagnitude * (float) Math.pow(velocityModifier, 2) * SpinnerConstants.ROTATION_MULTIPLIER;
        }

        public static double calculateDirection(float velocityX, float velocityY, float swipeStartX, float swipeStartY){
            // Calculate the angle of the swipe vector
            double startAngle = Math.atan2(swipeStartY, swipeStartX);
            double endAngle = Math.atan2(velocityY, velocityX);
            double deltaAngle = Math.toDegrees(endAngle - startAngle);

            // Normalize deltaAngle to determine direction
            if (deltaAngle > 180) deltaAngle -= 360;
            if (deltaAngle < -180) deltaAngle += 360;

            // Determine rotation direction: clockwise (positive) or counterclockwise (negative)
            return deltaAngle > 0 ? 1 : -1;
        }

        public static float calculateDuration(float velocityMagnitude, float velocityModifier) {
            return Math.min((float) Math.sqrt(velocityMagnitude) * (velocityModifier * 250f), SpinnerConstants.MAX_ANIMATION_DURATION);
        }
    }
}*/
