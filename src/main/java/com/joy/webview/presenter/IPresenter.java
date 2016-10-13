package com.joy.webview.presenter;

import android.support.annotation.WorkerThread;
import android.webkit.JavascriptInterface;

/**
 * Created by Daisw on 2016/10/13.
 */

public interface IPresenter {

    void setUserAgent(String userAgent);

    String url();

    void load(String url);

    void reload();

    void goBack();

    void goForward();

    String getTag(String tagName);

    abstract class JSHtmlSource {
        @JavascriptInterface
        @WorkerThread
        public abstract void receivedHtml(String html);
    }
}
