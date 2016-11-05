package com.joy.webview;

import android.content.Context;
import android.support.annotation.Nullable;

import com.joy.ui.BaseApplication;
import com.joy.utils.LogMgr;
import com.joy.utils.TextUtil;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.QbSdk;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2016/10/13.
 */

public class JoyWeb {

    private static String mUserAgent;

    private static String mCookie;
    private static boolean mCookieSeeded;

    private static boolean mAppCacheEnabled;
    private static String mAppCachePath;
    private static long mAppCacheMaxSize;

    static {
        mAppCacheEnabled = true;
        mAppCachePath = BaseApplication.getContext().getExternalCacheDir().getPath();
        mAppCacheMaxSize = 1024 * 1024 * 8;// 8M
    }

    /**
     * 预加载腾讯X5内核，在Application的onCreate方法中调用。
     * 如果用到腾讯X5服务，必须调用此方法。
     *
     * @param appContext
     * @param callback
     */
    public static void initX5Environment(Context appContext, @Nullable QbSdk.PreInitCallback callback) {
        QbSdk.initX5Environment(appContext, callback);
    }

    public static void setUserAgent(@Nullable String userAgent) {
        mUserAgent = userAgent;
    }

    @Nullable
    public static String getUserAgent() {
        return mUserAgent;
    }

    public static void setCookie(@Nullable String cookie) {
        mCookie = cookie;
        if (TextUtil.isEmpty(cookie)) {
            clearCookie();
        }
    }

    @Nullable
    public static String getCookie() {
        return mCookie;
    }

    public static void clearCookie() {
        mCookie = null;
        mCookieSeeded = false;
        removeAllCookies();
    }

    public static boolean isCookieSeeded() {
        return mCookieSeeded;
    }

    public static void setCookieSeeded(boolean cookieSeeded) {
        mCookieSeeded = cookieSeeded;
    }

    private static void removeAllCookies() {
        try {
            if (SDK_INT >= LOLLIPOP) {
                CookieManager.getInstance().removeAllCookies(value -> {
                });
                android.webkit.CookieManager.getInstance().removeAllCookies(value -> {
                });
            } else {
                CookieManager.getInstance().removeAllCookie();
                android.webkit.CookieManager.getInstance().removeAllCookie();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppCacheEnabled() {
        return mAppCacheEnabled;
    }

    public static void setAppCacheEnabled(boolean appCacheEnabled) {
        mAppCacheEnabled = appCacheEnabled;
    }

    public static String getAppCachePath() {
        return mAppCachePath;
    }

    public static void setAppCachePath(String appCachePath) {
        mAppCachePath = appCachePath;
    }

    public static long getAppCacheMaxSize() {
        return mAppCacheMaxSize;
    }

    public static void setAppCacheMaxSize(long appCacheMaxSize) {
        mAppCacheMaxSize = appCacheMaxSize;
    }

    public static void setLogcatDisable(boolean disable) {
        LogMgr.DEBUG = disable;
    }
}
