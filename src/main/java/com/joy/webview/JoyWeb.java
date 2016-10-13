package com.joy.webview;

/**
 * Created by Daisw on 2016/10/13.
 */

public class JoyWeb {

    private static String mUserAgent;

    public static void setUserAgent(String userAgent) {
        mUserAgent = userAgent;
    }

    public static String getUserAgent() {
        return mUserAgent;
    }

    public static void release() {
        mUserAgent = null;
    }
}
