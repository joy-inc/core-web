package com.joy.webview.ui.interfaces;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.joy.share.ShareItem;
import com.joy.ui.interfaces.BaseViewNet;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.UIDelegate;
import com.joy.webview.view.NavigationBar;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2016/9/24.
 */

public interface BaseViewWeb<E> extends BaseViewNet<E> {

    IPresenter getPresenter();
    UIDelegate getUIDelegate();

    void onTitleCloseClick();
    void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);

    boolean isProgressEnabled();
    ProgressBar initProgressBar();

    NavigationBar initNavigationBar();
    void onNavCustomItemClick(ImageView view);

    void onPageStarted(WebView webView, String url, Bitmap favicon);
    void onProgress(WebView webView, int progress);
    void onPageFinished(String url);
    void onReceivedError(int errorCode, String description, String failingUrl);
    void onReceivedTitle(WebView webView, String title);
    boolean onOverrideUrl(WebView webView, String url);
    WebResourceResponse onInterceptRequest(WebView webView, String url);
    void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);
    void onHideCustomView();
    @TargetApi(HONEYCOMB) void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType);
    @TargetApi(LOLLIPOP) boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback);
    void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);

    boolean onShareItemClick(int position, View v, ShareItem item);
}
