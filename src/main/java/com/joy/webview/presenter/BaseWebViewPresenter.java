package com.joy.webview.presenter;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joy.utils.LogMgr;
import com.joy.webview.ui.interfaces.BaseViewWeb;

import javax.inject.Inject;

/**
 * Created by Daisw on 16/8/14.
 */

public class BaseWebViewPresenter {

    @Inject
    WebView mWebView;

    @Inject
    BaseViewWeb mBaseView;

    boolean isError;

    @Inject
    BaseWebViewPresenter() {

    }

    @Inject
    void setWebViewClient() {

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

//                mBaseView.onPageStarted(view, url, favicon);
                isError = false;
                LogMgr.d("daisw", "onPageStarted");
                mBaseView.hideContent();
                mBaseView.hideTipView();
                mBaseView.showLoading();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

//                mBaseView.onReceivedError(view, errorCode, description, failingUrl);
                isError = true;
                LogMgr.d("daisw", "onReceivedError");
                mBaseView.hideLoading();
                mBaseView.hideContent();
                mBaseView.showErrorTip();
            }

            @Override
            public void onPageFinished(WebView view, String url) {

//                mBaseView.onPageFinished(view, url);
                if (!isError) {
                    LogMgr.d("daisw", "onPageFinished " + url);
                    mBaseView.hideLoading();
                    mBaseView.hideTipView();
                    mBaseView.showContent();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                LogMgr.d("daisw", "shouldOverrideUrlLoading");
                return mBaseView.overrideUrl(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {

                mBaseView.onReceivedTitle(view, title);
                LogMgr.d("daisw", "onReceivedTitle: " + title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

//                mBaseView.onProgress(view, newProgress);
//                LogMgr.d("daisw", "onProgressChanged: " + newProgress);
            }
        });
    }

    public WebView getWebView() {

        return mWebView;
    }

    public void load(String url) {

        mWebView.loadUrl(url);
    }

    public void reload() {

        mWebView.reload();
    }

    public void goBack() {

        mWebView.goBack();
    }

    public void goForward() {

        mWebView.goForward();
    }
}
