package com.joy.webview.ui.interfaces;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebView;

import com.joy.ui.activity.interfaces.BaseViewNet;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2016/9/24.
 */

public interface BaseViewWeb extends BaseViewNet {

    boolean isProgressEnabled();

    void onPageStarted(WebView view, String url, Bitmap favicon);

    void onPageFinished(WebView view, String url);

    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

    void onReceivedTitle(WebView view, String title);

    void onProgress(WebView view, int progress);

    boolean onOverrideUrl(WebView view, String url);

    void onShowCustomView(View view, CustomViewCallback callback);

    void onHideCustomView();

    void onDownloadStart(String url, String userAgent,
                         String contentDisposition, String mimetype, long contentLength);

    @TargetApi(HONEYCOMB)
    void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, @Nullable String capture);

    @TargetApi(LOLLIPOP)
    boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams);

    void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);
}
