package com.joy.webview;

import android.support.annotation.Nullable;
import android.webkit.CookieManager;

import com.joy.utils.TextUtil;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2016/10/13.
 */

public class JoyWeb {

    private static String mUserAgent;
    private static String mCookie;
    public static boolean mIsCookieSeeded;

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
        mIsCookieSeeded = false;
        removeAllCookies();
    }

    private static void removeAllCookies() {
        try {
            if (SDK_INT >= LOLLIPOP) {
                CookieManager.getInstance().removeAllCookies(value -> {});
            } else {
                CookieManager.getInstance().removeAllCookie();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void release() {
        mUserAgent = null;
        mCookie = null;
        mIsCookieSeeded = false;
    }
}
