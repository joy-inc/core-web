package com.joy.webview.presenter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.WorkerThread;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joy.utils.TextUtil;
import com.joy.webview.JoyWeb;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.utils.DocumentParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 16/8/14.
 */

@SuppressLint("AddJavascriptInterface")
public class BaseWebViewPresenter implements IPresenter {

    @Inject
    WebView mWebView;

    @Inject
    BaseViewWeb mBaseView;

    private String mInitialUrl;
    private Document mDocument;
    private boolean mIsError;
    private boolean mNeedSeedCookie;

    @Inject
    BaseWebViewPresenter() {
    }

    @Inject
    void setWebViewClient() {

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mIsError = false;
                mBaseView.hideContent();
                mBaseView.hideTipView();
                mBaseView.showLoading();
                mBaseView.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (mNeedSeedCookie) {
                    mWebView.loadUrl(mInitialUrl);
                    mNeedSeedCookie = false;
                } else {
                    mIsError = true;
                    mBaseView.hideLoading();
                    mBaseView.hideContent();
                    mBaseView.showErrorTip();
                    mBaseView.onReceivedError(view, errorCode, description, failingUrl);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mNeedSeedCookie) {
                    JoyWeb.mIsCookieSeeded = true;
                    mWebView.loadUrl(mInitialUrl);
                    mNeedSeedCookie = false;
                } else if (!mIsError) {
                    mBaseView.hideLoading();
                    mBaseView.hideTipView();
                    mBaseView.showContent();
                    getHtmlByTagName("html", 0);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return mBaseView.onOverrideUrl(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                mBaseView.onShowCustomView(view, callback);
            }

            @Override
            public void onHideCustomView() {
                mBaseView.onHideCustomView();
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (!mIsError && !mNeedSeedCookie) {
                    mBaseView.onReceivedTitle(view, title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (!mNeedSeedCookie) {
                    mBaseView.onProgress(view, newProgress);
                }
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
            @TargetApi(HONEYCOMB)
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType) {
                mBaseView.onShowFileChooser(filePathCallback, acceptType, null);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @TargetApi(JELLY_BEAN)
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
                mBaseView.onShowFileChooser(filePathCallback, acceptType, capture);
            }

            // for >= Lollipop, all in one
            @Override
            @TargetApi(LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return mBaseView.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });
        mWebView.addJavascriptInterface(new JSHtmlSource() {
            @JavascriptInterface
            @WorkerThread
            @Override
            public void receivedHtml(String html) {
                mWebView.post(() -> onReceivedHtml(html));
            }
        }, "htmlSource");
    }

    private void onReceivedHtml(String html) {
        mDocument = Jsoup.parse(html);
        mBaseView.onPageFinished(mWebView, mWebView.getUrl());
    }

    @SuppressLint("DefaultLocale")
    private void getHtmlByTagName(String tag, int index) {
        String format = "javascript:window.htmlSource.receivedHtml(document.getElementsByTagName('%s')[%d].outerHTML);";
        mWebView.loadUrl(String.format(format, tag, index));
    }

    @Override
    public void setUserAgent(String userAgent) {
        if (TextUtil.isNotEmpty(userAgent)) {
            WebSettings settings = mWebView.getSettings();
            settings.setUserAgentString(settings.getUserAgentString() + " " + userAgent);
        }
    }

    @Override
    public Document getDocument() {
        return mDocument;
    }

    @Override
    public Elements getElementsByTag(String tagName) {
        return DocumentParser.getElementsByTag(mDocument, tagName);
    }

    @Override
    public Element getElementByTag(String tagName, int index) {
        return DocumentParser.getElementByTag(mDocument, tagName, index);
    }

    @Override
    public Element getFirstElementByTag(String tagName) {
        return DocumentParser.getFirstElementByTag(mDocument, tagName);
    }

    @Override
    public String getTag(String tagName) {
        return DocumentParser.getTag(mDocument, tagName);
    }

    @Override
    public String getAttribute(String attrName, String attrValue, String attributeKey) {
        return DocumentParser.getAttribute(mDocument, attrName, attrValue, attributeKey);
    }

    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public String url() {
        return mWebView.getUrl();
    }

    @Override
    public void load(String url) {
        String cookie = JoyWeb.getCookie();
        mNeedSeedCookie = TextUtil.isNotEmpty(cookie) && !JoyWeb.mIsCookieSeeded;
        if (mNeedSeedCookie) {
            mInitialUrl = url;
            mWebView.loadUrl(cookie);
        } else {
            mWebView.loadUrl(url);
        }
    }

    @Override
    public void reload() {
        mWebView.reload();
    }

    @Override
    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    @Override
    public boolean canGoForward() {
        return mWebView.canGoForward();
    }

    @Override
    public void goBack() {
        if (canGoBack()) {
            mWebView.goBack();
        } else {
            mBaseView.finish();
        }
    }

    @Override
    public void goForward() {
        if (canGoForward()) {
            mWebView.goForward();
        }
    }
}
