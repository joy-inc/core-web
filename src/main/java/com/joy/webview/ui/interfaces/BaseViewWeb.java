package com.joy.webview.ui.interfaces;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.ProgressBar;

import com.joy.share.ShareItem;
import com.joy.ui.activity.interfaces.BaseViewNet;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.UIDelegate;
import com.joy.webview.view.NavigationBar;

import java.util.List;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * Created by Daisw on 2016/9/24.
 */

public interface BaseViewWeb extends BaseViewNet {

    IPresenter getPresenter();

    UIDelegate getUIDelegate();

    boolean isProgressEnabled();

    void onPageStarted(String url, Bitmap favicon);

    void onPageFinished(String url);

    void onReceivedError(int errorCode, String description, String failingUrl);

    void onReceivedTitle(String title);

    void onProgress(int progress);

    boolean onOverrideUrl(String url);

    void onShowCustomView(View view);

    void onHideCustomView();

    void onDownloadStart(String url, String userAgent,
                         String contentDisposition, String mimetype, long contentLength);

    @TargetApi(HONEYCOMB)
    void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType);

    @TargetApi(LOLLIPOP)
    boolean onShowFileChooser(ValueCallback<Uri[]> filePathCallback);

    void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY);

    void onTitleBackClick();

    void onTitleMoreClick();

    void onTitleCloseClick();

    ProgressBar initProgressBar();

    NavigationBar initNavigationBar();

    List<ShareItem> getShareItems();

    void addShareItem(ShareItem item);

    void addShareItem(int position, ShareItem item);

    void addShareItems(List<ShareItem> items);

    void addShareItems(int position, List<ShareItem> items);

    boolean onShareItemClick(int position, View v, ShareItem item);
}
