package com.joy.webview.presenter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joy.utils.LogMgr;
import com.joy.utils.TextUtil;
import com.joy.webview.JoyWeb;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.utils.DocumentParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

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
    private int mCurIndex;
    private Map<String, Boolean> mSessionFinished;

    @Inject
    BaseWebViewPresenter() {
        mSessionFinished = new HashMap<>();
    }

    @Inject
    void setWebViewClient() {

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String prevUrl = view.getUrl();
                boolean isRedirected = mSessionFinished.get(prevUrl) != null && !mSessionFinished.get(prevUrl);
                if (isRedirected) {// 如果initialUrl被重定向了，则跳出方法体。
                    return;
                }
                mSessionFinished.put(url, false);
                mIsError = false;
                mBaseView.hideContent();
                mBaseView.hideTipView();
                mBaseView.showLoading();
                if (!mNeedSeedCookie) {
                    mBaseView.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mSessionFinished.put(failingUrl, true);
                if (mNeedSeedCookie) {
                    mNeedSeedCookie = false;
                    mWebView.loadUrl(mInitialUrl);
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
                mSessionFinished.put(url, true);
                if (!url.equals(view.getUrl())) {// 如果当前URL和webview所持有的URL不一致时，抛掉当前URL的回调，跳出方法体。
                    return;
                }
                if (mNeedSeedCookie) {
                    mNeedSeedCookie = false;
                    JoyWeb.mIsCookieSeeded = true;
                    mWebView.loadUrl(mInitialUrl);
                } else if (!mIsError) {
                    mCurIndex = mWebView.copyBackForwardList().getCurrentIndex();
                    if (mCurIndex == -1) {
                        return;
                    }
                    mBaseView.hideLoading();
                    mBaseView.hideTipView();
                    mBaseView.showContent();
                    if (mCurIndex == 1 && TextUtil.isNotEmpty(mInitialUrl) && !url.equals(mInitialUrl)) {
                        mInitialUrl = url;// 当加载过cookieUrl，并且initialUrl被重定向了，这时把重定向后的URL赋给initialUrl。
                    }
                    if (url.equals(mInitialUrl) && mCurIndex != 0) {
                        mWebView.clearHistory();
                        mCurIndex = mWebView.copyBackForwardList().getCurrentIndex();
                    }
                    getHtmlByTagName("html", 0);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String prevUrl = view.getUrl();
                boolean isAutoRedirect = mSessionFinished.get(prevUrl) != null && !mSessionFinished.get(prevUrl);
                if (isAutoRedirect) {// 如果是自动重定向，则交给webview处理。
                    LogMgr.d("core-web", "BaseWebViewPresenter shouldOverrideUrlLoading # auto redirect");
                    return false;
                }
                boolean consumed = mBaseView.onOverrideUrl(view, url);
                if (!consumed) {
                    mCurIndex++;
                }
                return consumed;
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
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength)
                -> mBaseView.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength));
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

    @Nullable
    @Override
    public Elements getElementsByTag(String tagName) {
        return DocumentParser.getElementsByTag(mDocument, tagName);
    }

    @Nullable
    @Override
    public Element getElementByTag(String tagName, int index) {
        return DocumentParser.getElementByTag(mDocument, tagName, index);
    }

    @Nullable
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

    /**
     * Get the index of the current history item. This index can be used to
     * directly index into the array list.
     *
     * @return The current index from 0...n or -1 if the list is empty.
     */
    public int getCurrentIndex() {
        return mCurIndex;
    }

    public boolean isFirstPage() {
        return mCurIndex <= 0;
    }

    @Override
    public void load(String url) {
        if (TextUtil.isNotEmpty(url)) {
            String cookie = JoyWeb.getCookie();
            mNeedSeedCookie = TextUtil.isNotEmpty(cookie) && !JoyWeb.mIsCookieSeeded;
            if (mNeedSeedCookie) {
                mInitialUrl = url;
                mWebView.loadUrl(cookie);
            } else {
                mWebView.loadUrl(url);
            }
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
        mCurIndex = mWebView.copyBackForwardList().getCurrentIndex() - 1;
        if (canGoBack()) {
            mWebView.goBack();
        } else {
            mBaseView.finish();
        }
    }

    @Override
    public void goForward() {
        if (canGoForward()) {
            mCurIndex++;
            mWebView.goForward();
        }
    }
}
