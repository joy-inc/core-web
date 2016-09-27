package com.joy.webview.module;

import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.joy.inject.ActivityScope;
import com.joy.inject.module.ActivityModule;
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
    WebView provideWebView(Activity activity) {

        WebView webView = new WebView(activity);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        return webView;
    }
}
