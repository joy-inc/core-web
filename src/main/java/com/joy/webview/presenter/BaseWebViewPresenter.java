package com.joy.webview.presenter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.WorkerThread;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joy.utils.CollectionUtil;
import com.joy.utils.TextUtil;
import com.joy.webview.ui.interfaces.BaseViewWeb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Inject;

/**
 * Created by Daisw on 16/8/14.
 */

@SuppressLint("AddJavascriptInterface")
public class BaseWebViewPresenter implements IPresenter {

    @Inject
    WebView mWebView;

    @Inject
    BaseViewWeb mBaseView;

    private boolean isError;
    private Document mDocument;

    @Inject
    BaseWebViewPresenter() {
    }

    @Inject
    void setWebViewClient() {

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isError = false;
                mBaseView.hideContent();
                mBaseView.hideTipView();
                mBaseView.showLoading();
                mBaseView.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                isError = true;
                mBaseView.hideLoading();
                mBaseView.hideContent();
                mBaseView.showErrorTip();
                mBaseView.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (isError)
                    return;
                mBaseView.hideLoading();
                mBaseView.hideTipView();
                mBaseView.showContent();
                getHtmlByTagName("html", 0);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return mBaseView.onOverrideUrl(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                mBaseView.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mBaseView.onProgress(view, newProgress);
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
    public String getTag(String tagName) {
        if (mDocument != null) {
            Elements elements = mDocument.getElementsByTag(tagName);
            if (CollectionUtil.isNotEmpty(elements)) {
                Element element = elements.get(0);
                return element.text();
            }
        }
        return null;
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
        mWebView.loadUrl(url);
    }

    @Override
    public void reload() {
        mWebView.reload();
    }

    @Override
    public void goBack() {
        mWebView.goBack();
    }

    @Override
    public void goForward() {
        mWebView.goForward();
    }
}
