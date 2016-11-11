package com.joy.webview.utils;

import android.os.Handler;
import android.os.Message;

import com.joy.utils.LogMgr;
import com.joy.webview.presenter.IPresenter;

import java.lang.ref.WeakReference;

import static android.webkit.WebViewClient.ERROR_TIMEOUT;
import static com.joy.ui.utils.DimenCons.SCREEN_HEIGHT_ABSOLUTE;

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
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", "TimeoutHandler handleMessage # msg: " + msg);
        }
        IPresenter presenter = mPresenter.get();
        if (presenter != null) {
            if (presenter.isHostFinishing()) {
                return;
            }
            // 在ProgressBar视图下，如果当前加载完的内容的高度大于屏幕的高度则跳出方法体（即使超时了依然等待）
            // FIXME: 2016/11/10 当contentHeight>screenHeight时跳出方法提，但是这样的判断可能会存在问题，如contentHeight本身就没screenHeight大又或者其他不确定因素。
            if (presenter.isProgressEnabled() && presenter.getContentHeight() > SCREEN_HEIGHT_ABSOLUTE) {
                return;
            }
            if (LogMgr.DEBUG) {
                LogMgr.d("core-web", "TimeoutHandler handleMessage # stopLoading");
            }
            presenter.stopLoading();
            presenter.switchErrorView(ERROR_TIMEOUT, "net::ERR_INTERNET_TIMEOUT", presenter.getUrl());
        }
    }
}
