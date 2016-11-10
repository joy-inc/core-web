package com.joy.webview.presenter;

import android.support.annotation.WorkerThread;
import android.view.View;
import android.webkit.JavascriptInterface;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Daisw on 2016/10/13.
 */

public interface IPresenter {

    View getWebView();

    String getUrl();

    String getTitle();

    void onPause();

    void onResume();

    void onDestroy();

    void stopLoading();

    void switchErrorView(int errorCode, String description, String failingUrl);

    void setUserAgent(String userAgent);

    void load(String url);

    void reload();

    boolean canGoBack();

    boolean canGoForward();

    void goBack();

    void goForward();

    boolean canGoBackOrForward(int steps);

    boolean goBackOrForward(int steps);

    boolean isHostFinishing();

    Document getDocument();

    Elements getElementsByTag(String tagName);

    Element getElementByTag(String tagName, int index);

    Element getFirstElementByTag(String tagName);

    String getTag(String tagName);

    String getAttribute(String attrName, String attrValue, String attributeKey);

    abstract class JSHtmlSource {
        @JavascriptInterface
        @WorkerThread
        public abstract void receivedHtml(String html);
    }
}
