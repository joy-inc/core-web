package com.joy.webview.ui.interfaces;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;

import com.joy.ui.activity.interfaces.BaseViewNet;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient.FileChooserParams;
import com.tencent.smtt.sdk.WebView;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2016/9/24.
 */

public interface BaseViewWebX5 extends BaseViewNet {

    void onPageStarted(WebView view, String url, Bitmap favicon);

    void onPageFinished(WebView view, String url);

    void onReceivedError(WebView view, WebResourceRequest webResourceRequest);

    void onReceivedTitle(WebView view, String title);

    void onProgress(WebView view, int progress);

    boolean onOverrideUrl(WebView view, String url);

    void onShowCustomView(View view, CustomViewCallback callback);

    void onHideCustomView();

    @TargetApi(HONEYCOMB)
    void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, @Nullable String capture);

    @TargetApi(LOLLIPOP)
    boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);
}
