package com.joy.webview.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.joy.inject.ActivityScope;
import com.joy.inject.module.ActivityModule;
import com.joy.ui.activity.BaseUiActivity;
import com.joy.ui.view.JWebView;
import com.joy.utils.DeviceUtil;
import com.joy.utils.TextUtil;
import com.joy.webview.JoyWeb;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;

import dagger.Module;
import dagger.Provides;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;

/**
 * Created by Daisw on 16/8/16.
 */

@Module(includes = ActivityModule.class)
public class BaseWebViewModule {

    private final BaseViewWeb mBaseViewWeb;
    private final boolean mCacheEnable;

    public BaseWebViewModule(BaseViewWeb baseViewWeb, boolean cacheEnable) {
        mBaseViewWeb = baseViewWeb;
        mCacheEnable = cacheEnable;
    }

    @Provides
    @ActivityScope
    BaseViewWeb provideBaseViewWeb() {
        return mBaseViewWeb;
    }

    @Provides
    @ActivityScope
    BaseUiActivity provideBaseUiActivity(Activity activity) {
        return (BaseUiActivity) activity;
    }

    @Provides
    @ActivityScope
    IPresenter provideIPresenter(BaseWebViewPresenter presenter) {
        return presenter;
    }

    @Provides
    @ActivityScope
    @SuppressLint("SetJavaScriptEnabled")
    WebView provideWebView(Activity activity) {
        JWebView webView = new JWebView(activity) {
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
        settings.setDomStorageEnabled(true);

        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        String userAgent = JoyWeb.getUserAgent();
        if (TextUtil.isNotEmpty(userAgent)) {
            settings.setUserAgentString(settings.getUserAgentString() + " " + userAgent);
        }

        settings.setAppCacheEnabled(JoyWeb.isAppCacheEnabled());
        settings.setAppCachePath(JoyWeb.getAppCachePath());
        settings.setAppCacheMaxSize(JoyWeb.getAppCacheMaxSize());
        if (mCacheEnable) {
            boolean networkEnable = DeviceUtil.isNetworkEnable(activity.getApplicationContext());
            settings.setCacheMode(networkEnable ? WebSettings.LOAD_NO_CACHE : WebSettings.LOAD_CACHE_ONLY);
        } else {
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (SDK_INT >= KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        return webView;
    }
}
