package com.joy.webview.utils;

import android.net.Uri;

import com.joy.utils.TextUtil;

/**
 * Created by Daisw on 2016/11/9.
 */

public class UriUtils {

    public static boolean isEquals(String s1, String s2) {
        return (TextUtil.isEmpty(s1) && TextUtil.isEmpty(s2)) || isEquals(Uri.parse(s1), Uri.parse(s2));
    }

    public static boolean isEquals(Uri uri1, Uri uri2) {
        return uri1.getScheme().equals(uri2.getScheme()) && uri1.getAuthority().equals(uri2.getAuthority()) && uri1.getPathSegments().equals(uri2.getPathSegments());
    }
}
