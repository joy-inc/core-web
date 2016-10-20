package com.joy.webview.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.joy.webview.R;

/**
 * Created by Daisw on 2016/10/15.
 */

public class NavigationBar extends LinearLayout {

    private int mNavHeight;
    private boolean mEnterAnimatorLocked, mExitAnimatorLocked;
    private ViewPropertyAnimator mEnterAnimator, mExitAnimator;

    public NavigationBar(Context context) {
        super(context);
        resolveThemeAttribute(context);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveThemeAttribute(context);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveThemeAttribute(context);
    }

    private void resolveThemeAttribute(Context context) {
        TypedArray a = context.obtainStyledAttributes(R.styleable.NavigationBar);
        mNavHeight = a.getDimensionPixelSize(R.styleable.NavigationBar_navHeight, 0);
        a.recycle();
    }

    public void runEnterAnimator() {
        if (mEnterAnimatorLocked || getTranslationY() == 0) {
            return;
        }
        if (mExitAnimator != null) {
            mExitAnimator.cancel();
        }
        mEnterAnimator = animate()
                .alpha(1.f)
                .translationY(0)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(2.f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mEnterAnimatorLocked = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mEnterAnimatorLocked = false;
                    }
                });
        mEnterAnimator.start();
    }

    public void runExitAnimator() {
        if (mExitAnimatorLocked || getTranslationY() == mNavHeight) {
            return;
        }
        if (mEnterAnimator != null) {
            mEnterAnimator.cancel();
        }
        mExitAnimator = animate()
                .alpha(0.f)
                .translationY(mNavHeight)
                .setDuration(300)
                .setInterpolator(new DecelerateInterpolator(2.f))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mExitAnimatorLocked = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mExitAnimatorLocked = false;
                    }
                });
        mEnterAnimator.start();
    }
}
