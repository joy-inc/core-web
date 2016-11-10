package com.joy.webview.utils;

import android.os.Handler;
import android.os.Message;

import com.joy.webview.presenter.IPresenter;

import java.lang.ref.WeakReference;

import static android.webkit.WebViewClient.ERROR_TIMEOUT;

/**
 * Created by Daisw on 2016/11/10.
 */

public class TimeoutHandler extends Handler {

    public static final int WHAT_TIMEOUT_ERROR = 101;
    private final WeakReference<IPresenter> mPresenter;

    public TimeoutHandler(IPresenter presenter) {
        mPresenter = new WeakReference<>(presenter);
    }

    @Override
    public void handleMessage(Message msg) {
//        LogMgr.i("core-web", "handleMessage msg: " + msg);
        IPresenter presenter = mPresenter.get();
        if (presenter != null) {
            if (presenter.isHostFinishing()) {
                return;
            }
//            LogMgr.i("core-web", "handleMessage stopLoading");
            presenter.stopLoading();
            presenter.switchErrorView(ERROR_TIMEOUT, "net::ERR_INTERNET_TIMEOUT", presenter.getUrl());
        }
    }
}
