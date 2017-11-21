package com.joy.webview.presenter;

import android.support.annotation.WorkerThread;
import android.view.View;
import android.webkit.JavascriptInterface;

import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Daisw on 2016/10/13.
 */

public interface IPresenter {

    boolean isHostFinishing();

    boolean isProgressEnabled();

    View getWebView();

    String getUrl();

    String getTitle();

    int getContentHeight();

    void onLifecycleEvent(ActivityEvent event);

    void onLifecycleEvent(FragmentEvent event);

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
