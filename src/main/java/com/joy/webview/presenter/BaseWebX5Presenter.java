package com.joy.webview.presenter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.joy.utils.LogMgr;
import com.joy.utils.TextUtil;
import com.joy.webview.JoyWeb;
import com.joy.webview.R;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.utils.DocumentParser;
import com.joy.webview.utils.PayIntercepter;
import com.joy.webview.utils.TimeoutHandler;
import com.joy.webview.utils.UriUtils;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient.CustomViewCallback;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebHistoryItem;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

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
import static com.joy.webview.utils.TimeoutHandler.WHAT_TIMEOUT_ERROR;

/**
 * Created by Daisw on 16/8/14.
 */

@SuppressLint("AddJavascriptInterface")
public class BaseWebX5Presenter implements IPresenter {

    @Inject
    WebView mWebView;

    @Inject
    BaseViewWeb mBaseView;

    private String mTempUrl;
    private Document mDocument;
    private boolean mIsError;
    private boolean mNeedSeedCookie;
    private Map<String, Boolean> mPageFinished;
    private TimeoutHandler mTimerHandler;

    @Inject
    BaseWebX5Presenter() {
        mPageFinished = new HashMap<>();
        mTimerHandler = new TimeoutHandler(this);
    }

    @Inject
    void setWebViewClient() {
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                addTimeoutMessage();
                String prevUrl = view.getUrl();
                boolean isRedirected = !isPageFinished(prevUrl);
                // 如果initialUrl被重定向了，则跳出方法体。
                if (isRedirected) {// TODO: 2016/11/11 经测试只能捕捉首次打开的URL，页面内跳转时的重定向捕捉不到。
                    return;
                }
                mPageFinished.put(url, false);
                mIsError = false;
                mBaseView.hideTipView();
                if (!mBaseView.isProgressEnabled()) {
                    mBaseView.hideContent();
                    mBaseView.showLoading();
                }
                if (!mNeedSeedCookie) {
                    mBaseView.onPageStarted(url, favicon);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (isHostFinishing()) {
                    return;
                }
                removeTimeoutMessage();
                if (mNeedSeedCookie) {
                    mNeedSeedCookie = false;
                    mWebView.loadUrl(mTempUrl);
                } else {
                    switchErrorView(errorCode, description, failingUrl);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebViewClient.a a) {
                if (request.isForMainFrame()) {
                    onReceivedError(view, -1, "", request.getUrl().toString());
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (isHostFinishing()) {
                    return;
                }
                removeTimeoutMessage();
                mPageFinished.put(url, true);
                if (!url.equals(view.getUrl())) {// 如果当前URL和webview所持有的URL不一致时，抛掉当前URL的回调，跳出方法体。
                    return;
                }
                if (mNeedSeedCookie) {
                    mNeedSeedCookie = false;
                    JoyWeb.setCookieSeeded(true);
                    mWebView.loadUrl(mTempUrl);
                } else if (!mIsError) {
                    if (mWebView.copyBackForwardList().getCurrentIndex() == -1) {
                        return;
                    }
                    if (!mBaseView.isProgressEnabled()) {
                        mBaseView.hideLoading();
                    }
                    mBaseView.hideTipView();
                    mBaseView.showContent();
                    getHtmlByTagName("html", 0);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (PayIntercepter.interceptPayIntent(view.getContext(), url)) {
                    return true;
                }
                String prevUrl = view.getUrl();
                boolean isAutoRedirect = !isPageFinished(prevUrl);
                if (isAutoRedirect) {// 如果是自动重定向，则交给webview处理。
                    if (LogMgr.DEBUG) {
                        LogMgr.d("core-web", "BaseWebX5Presenter shouldOverrideUrlLoading # auto redirect " + url);
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return mBaseView.onOverrideUrl(url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                mBaseView.onShowCustomView(view);
            }

            @Override
            public void onHideCustomView() {
                mBaseView.onHideCustomView();
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (isHostFinishing()) {
                    return;
                }
                if (!mIsError && !mNeedSeedCookie) {
                    mBaseView.onReceivedTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (!mNeedSeedCookie) {
                    mBaseView.onProgress(newProgress);
                }
            }

            // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
            @TargetApi(HONEYCOMB)
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType) {
                mBaseView.onShowFileChooser(filePathCallback, acceptType);
            }

            // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
            @Override
            @TargetApi(JELLY_BEAN)
            public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
                mBaseView.onShowFileChooser(filePathCallback, acceptType);
            }

            // for >= Lollipop, all in one
            @Override
            @TargetApi(LOLLIPOP)
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return mBaseView.onShowFileChooser(filePathCallback);
            }
        });
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength)
                -> mBaseView.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength));
        mWebView.addJavascriptInterface(new JSHtmlSource() {
            @JavascriptInterface
            @WorkerThread
            @Override
            public void receivedHtml(final String html) {
//                mWebView.post(() -> onReceivedHtml(html));
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        onReceivedHtml(html);
                    }
                });
            }
        }, "htmlSource");
    }

    @Inject
    void associatedHostLifecycle() {
        mBaseView.lifecycle()
                .subscribe(event -> {
                    switch (event) {
                        case PAUSE:
                            if (isHostFinishing()) {
                                removeTimeoutMessage();
                                stopLoading();
                            }
                            onPause();
                            break;
                        case RESUME:
                            onResume();
                            break;
                        case DESTROY:
                            onDestroy();
                            break;
                        default:
                            break;
                    }
                });
    }

    private boolean isPageFinished(String url) {
        Boolean b = mPageFinished.get(url);
        return b == null || b;
    }

    private void addTimeoutMessage() {
        removeTimeoutMessage();
        mTimerHandler.sendEmptyMessageDelayed(WHAT_TIMEOUT_ERROR, JoyWeb.getTimeoutDuration());
    }

    private void removeTimeoutMessage() {
        mTimerHandler.removeMessages(WHAT_TIMEOUT_ERROR);
    }

    private void onReceivedHtml(String html) {
        mDocument = Jsoup.parse(html);
        mBaseView.onPageFinished(getUrl());
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

    @Override
    public boolean isHostFinishing() {
        return mBaseView.isFinishing();
    }

    @Override
    public boolean isProgressEnabled() {
        return mBaseView.isProgressEnabled();
    }

    @Override
    public WebView getWebView() {
        return mWebView;
    }

    @Override
    public String getUrl() {
        return mWebView.getUrl() == null ? "" : mWebView.getUrl();
    }

    @Override
    public String getTitle() {
        return mWebView.getTitle();
    }

    @Override
    public int getContentHeight() {
        return mWebView.getContentHeight();
    }

    @Override
    public void onPause() {
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        mWebView.onResume();
    }

    @Override
    public void onDestroy() {
        mWebView.destroy();
    }

    @Override
    public void stopLoading() {
        mWebView.stopLoading();
    }

    @Override
    public void switchErrorView(int errorCode, String description, String failingUrl) {
        mIsError = true;
        if (!mBaseView.isProgressEnabled()) {
            mBaseView.hideLoading();
        }
        mBaseView.hideContent();
        mBaseView.showErrorTip();
        mBaseView.onReceivedError(errorCode, description, failingUrl);
    }

    @Override
    public void load(String url) {
        if (TextUtil.isNotEmpty(url)) {
            String cookieUrl = JoyWeb.getCookie();
            mNeedSeedCookie = TextUtil.isNotEmpty(cookieUrl) && !JoyWeb.isCookieSeeded();
            if (mNeedSeedCookie) {
                mTempUrl = url;
                mWebView.loadUrl(cookieUrl);
            } else {
                mWebView.loadUrl(url);
            }
        }
    }

    @Override
    public void reload() {
        load(getUrl());
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
        WebBackForwardList list = mWebView.copyBackForwardList();
        int curIndex = list.getCurrentIndex();
        WebHistoryItem curItem = list.getCurrentItem();
        WebHistoryItem prevItem = list.getItemAtIndex(curIndex - 1);
        int steps = 0;
        if (prevItem != null) {
            steps--;
            if (prevItem.getUrl().equals(JoyWeb.getCookie())) {
                prevItem = list.getItemAtIndex(curIndex - 2);
                if (prevItem != null) {
                    steps--;
                    if (UriUtils.isEquals(prevItem.getUrl(), curItem.getUrl())) {
                        prevItem = list.getItemAtIndex(curIndex - 3);
                        if (prevItem != null) {
                            steps--;
                            goBackOrForward(steps);
                        } else {
                            mBaseView.finish();
                        }
                        return;
                    }
                    goBackOrForward(steps);
                } else {
                    mBaseView.finish();
                }
                return;
            }
            if (goBackOrForward(steps)) {
                return;
            }
        }
        mBaseView.finish();
    }

    @Override
    public void goForward() {
        WebBackForwardList list = mWebView.copyBackForwardList();
        int curIndex = list.getCurrentIndex();
        WebHistoryItem curItem = list.getCurrentItem();
        WebHistoryItem nextItem = list.getItemAtIndex(curIndex + 1);
        int steps = 0;
        if (nextItem != null) {
            steps++;
            if (nextItem.getUrl().equals(JoyWeb.getCookie())) {
                nextItem = list.getItemAtIndex(curIndex + 2);
                if (nextItem != null) {
                    steps++;
                    if (UriUtils.isEquals(nextItem.getUrl(), curItem.getUrl())) {
                        nextItem = list.getItemAtIndex(curIndex + 3);
                        if (nextItem != null) {
                            steps++;
                            goBackOrForward(steps);
                        } else {
                            mBaseView.showToast(R.string.toast_no_next_page);
                        }
                        return;
                    }
                    if (goBackOrForward(steps)) {
                        return;
                    }
                }
            }
            if (goBackOrForward(steps)) {
                return;
            }
        }
        mBaseView.showToast(R.string.toast_no_next_page);
    }

    @Override
    public boolean canGoBackOrForward(int steps) {
        return mWebView.canGoBackOrForward(steps);
    }

    @Override
    public boolean goBackOrForward(int steps) {
        if (canGoBackOrForward(steps)) {
            mWebView.goBackOrForward(steps);
            return true;
        }
        return false;
    }
}
