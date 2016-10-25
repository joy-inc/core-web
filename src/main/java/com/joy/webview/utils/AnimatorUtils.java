package com.joy.webview.utils;

import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by Daisw on 2016/10/23.
 */

public class AnimatorUtils {

    public static void fadeIn(TextView tv, CharSequence title) {
        if (tv != null) {
            tv.setText(title);
            if (tv.getAlpha() != 1.f) {
                tv.animate()
                        .alpha(1.f)
                        .setDuration(200)
                        .setInterpolator(new LinearInterpolator())
                        .start();
            }
        }
    }
}
