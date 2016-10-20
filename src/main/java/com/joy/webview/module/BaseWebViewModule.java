package com.joy.webview.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.joy.inject.ActivityScope;
import com.joy.inject.module.ActivityModule;
import com.joy.utils.TextUtil;
import com.joy.webview.JoyWeb;
import com.joy.webview.ui.interfaces.BaseViewWeb;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Daisw on 16/8/16.
 */

@Module(includes = ActivityModule.class)
public class BaseWebViewModule {

    private final BaseViewWeb mBaseViewWeb;

    public BaseWebViewModule(BaseViewWeb baseViewWeb) {
        mBaseViewWeb = baseViewWeb;
    }

    @Provides
    @ActivityScope
    BaseViewWeb provideBaseViewWeb() {
        return mBaseViewWeb;
    }

    @Provides
    @ActivityScope
    @SuppressLint("SetJavaScriptEnabled")
    WebView provideWebView(Activity activity) {

        WebView webView = new WebView(activity) {
            boolean isTouchTriggered = false;

            @Override
            protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isTouchTriggered) {
                    mBaseViewWeb.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
                }
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isTouchTriggered = true;
                }
                return super.onInterceptTouchEvent(event);
            }
        };
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);

        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        String userAgent = JoyWeb.getUserAgent();
        if (TextUtil.isNotEmpty(userAgent)) {
            settings.setUserAgentString(settings.getUserAgentString() + " " + userAgent);
        }

        return webView;
    }
}
