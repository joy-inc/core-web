package com.joy.webview.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.joy.utils.TextUtil;
import com.joy.utils.ToastUtil;
import com.joy.webview.R;

/**
 * Created by Daisw on 2016/10/28.
 */

public class PayIntercepter {

    public static final String SCHEME_WECHAT = "weixin://";
    public static final String SCHEME_ALIPAY = "alipays://";

    public static boolean interceptPayIntent(Context context, String url) {
        if (context == null || TextUtil.isEmpty(url)) {
            return false;
        }
        try {
            if (url.startsWith(SCHEME_WECHAT) || url.startsWith(SCHEME_ALIPAY)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            if (url.startsWith(SCHEME_WECHAT)) {
                ToastUtil.showToast(context, R.string.toast_wxpay_no_weixin);
            }
            return true;
        }
        return false;
    }
}
