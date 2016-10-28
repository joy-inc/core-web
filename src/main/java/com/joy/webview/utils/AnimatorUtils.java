package com.joy.webview.utils;

import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by Daisw on 2016/10/23.
 */

public class AnimatorUtils {

    public static void fadeIn(TextView tv, CharSequence title) {
        if (tv != null) {
            tv.setText(title);
            fadeIn(tv);
        }
    }

    public static void fadeIn(View v, long delay) {
        if (v != null && v.getAlpha() != 1.f) {
            v.animate()
                    .alpha(1.f)
                    .setDuration(200)
                    .setStartDelay(delay)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
    }

    public static void fadeIn(View v) {
        fadeIn(v, 0);
    }

    public static void fadeOut(View v, long delay) {
        if (v != null && v.getAlpha() != 0.f) {
            v.animate()
                    .alpha(0.f)
                    .setDuration(200)
                    .setStartDelay(delay)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
    }

    public static void fadeOut(View v) {
        fadeOut(v, 0);
    }
}
