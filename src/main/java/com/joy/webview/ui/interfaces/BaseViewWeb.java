package com.joy.webview.ui.interfaces;

import android.webkit.WebView;

import com.joy.ui.activity.interfaces.BaseViewNet;

/**
 * Created by Daisw on 2016/9/24.
 */

public interface BaseViewWeb extends BaseViewNet {

//    void onPageStarted(WebView view, String url, Bitmap favicon);

//    void onPageFinished(WebView view, String url);

//    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

    void onReceivedTitle(WebView view, String title);

//    void onProgress(WebView view, int progress);

    boolean overrideUrl(WebView view, String url);
}
