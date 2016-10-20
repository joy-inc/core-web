package com.joy.webview.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;

import com.joy.inject.ActivityScope;
import com.joy.inject.module.ActivityModule;
import com.joy.utils.TextUtil;
import com.joy.webview.JoyWeb;
import com.joy.webview.ui.interfaces.BaseViewWebX5;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Daisw on 16/8/16.
 */

@Module(includes = ActivityModule.class)
public class BaseWebX5Module {

    private final BaseViewWebX5 mBaseViewWebX5;

    public BaseWebX5Module(BaseViewWebX5 baseViewWebX5) {
        mBaseViewWebX5 = baseViewWebX5;
    }

    @Provides
    @ActivityScope
    BaseViewWebX5 provideBaseViewWebX5() {
        return mBaseViewWebX5;
    }

    @Provides
    @ActivityScope
    @SuppressLint("SetJavaScriptEnabled")
    WebView provideWebView(Activity activity) {

        QbSdk.allowThirdPartyAppDownload(true);

        WebView webView = new WebView(activity) {
            boolean isTouchTriggered = false;

            @Override
            protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (isTouchTriggered) {
                    mBaseViewWebX5.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
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
