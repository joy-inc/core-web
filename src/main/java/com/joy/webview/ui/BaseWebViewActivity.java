package com.joy.webview.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.joy.inject.module.ActivityModule;
import com.joy.share.JoyShare;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.utils.LogMgr;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.ui.interfaces.KConstant;
import com.joy.webview.utils.AnimatorUtils;
import com.joy.webview.view.NavigationBar;

import java.util.List;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Daisw on 16/9/7.
 */

public class BaseWebViewActivity extends BaseHttpUiActivity implements BaseViewWeb, KConstant {

    protected final String classSimpleName = getClass().getSimpleName();

    @Inject
    BaseWebViewPresenter mPresenter;

    protected String mUrl;
    protected CharSequence mTitle;
    protected TextView mTvTitle;
    protected boolean mTitleMoreEnable;
    protected boolean mTitleCloseEnable;
    protected ImageButton mIbTitleMore;
    protected ImageButton mIbTitleClose;
    protected JoyShare mJoyShare;
    protected NavigationBar mNavBar;
    protected boolean mNavDisplay = false;
    protected boolean mNavAnimate = true;
    protected int mNavHeight;
    protected int mNavElevation;

    protected boolean mLongClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component().inject(this);
        setContentView(mPresenter.getWebView());
        mPresenter.load(mUrl);
    }

    private BaseWebViewComponent component() {
        return DaggerBaseWebViewComponent.builder()
                .activityModule(new ActivityModule(this))
                .baseWebViewModule(new BaseWebViewModule(this))
                .build();
    }

    @Override
    protected void resolveThemeAttribute() {
        super.resolveThemeAttribute();
        TypedArray themeTa = obtainStyledAttributes(R.styleable.Theme);
        mLongClickable = themeTa.getBoolean(R.styleable.Theme_longClickable, true);
        mTitleMoreEnable = themeTa.getBoolean(R.styleable.Theme_titleMoreEnable, true);
        mTitleCloseEnable = themeTa.getBoolean(R.styleable.Theme_titleCloseEnable, true);
        themeTa.recycle();

        TypedArray navTa = obtainStyledAttributes(R.styleable.NavigationBar);
        mNavDisplay = navTa.getBoolean(R.styleable.NavigationBar_navDisplay, false);
        mNavAnimate = navTa.getBoolean(R.styleable.NavigationBar_navAnimate, true);
        mNavHeight = navTa.getDimensionPixelSize(R.styleable.NavigationBar_navHeight, 0);
        mNavElevation = navTa.getDimensionPixelSize(R.styleable.NavigationBar_navElevation, 0);
        navTa.recycle();
    }

    @Override
    protected void initData() {
        mUrl = getIntent().getStringExtra(KEY_URL);
        mTitle = getIntent().getCharSequenceExtra(KEY_TITLE);

        mJoyShare = new JoyShare(this);
        mJoyShare.setData(getShareItems());
        mJoyShare.setOnItemClickListener(this::onShareItemClick);
    }

    @Override
    protected void initTitleView() {
        if (!isNoTitle()) {
            addTitleLeftBackView((v) -> mPresenter.goBack());
            if (mTitleMoreEnable) {
                mIbTitleMore = addTitleRightMoreView((v) -> onTitleMoreClick());
                mIbTitleMore.setAlpha(0.f);
            }
            if (mTitleCloseEnable) {
                mIbTitleClose = addTitleRightView(R.drawable.ic_close_white_24dp, (v) -> finish());
            }
            if (mTitleMoreEnable && mTitleCloseEnable) {
                mIbTitleMore.setMinimumWidth(DP_1_PX * 40);
                mIbTitleClose.setMinimumWidth(DP_1_PX * 40);
            }
            mTvTitle = addTitleMiddleView(mTitle);
            if (TextUtil.isEmpty(mTitle)) {
                mTvTitle.setAlpha(0.f);
            }
        }
    }

    @Override
    protected void initContentView() {
        mPresenter.getWebView().setOnLongClickListener((v) -> !mLongClickable);
        addNavBarIfNecessary();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        AnimatorUtils.fadeIn(mTvTitle, title);
    }

    protected void fadeInTitleMore() {
        AnimatorUtils.fadeIn(mIbTitleMore);
    }

    protected void onTitleMoreClick() {
        mJoyShare.show();
    }

    private void addNavBarIfNecessary() {
        if (mNavDisplay) {
            mNavBar = initNavigationBar();
            if (mNavAnimate) {
                mNavBar.setAlpha(0.f);
                mNavBar.setTranslationY(mNavHeight);
            } else {
                getContentViewLp().bottomMargin = mNavHeight - mNavElevation;
            }
            LayoutParams navBarLp = new LayoutParams(MATCH_PARENT, mNavHeight);
            navBarLp.gravity = Gravity.BOTTOM;
            addContentView(mNavBar, navBarLp);
        }
    }

    protected NavigationBar initNavigationBar() {
        NavigationBar navBar = inflateLayout(R.layout.lib_view_web_navigation_bar);
        navBar.findViewById(R.id.ivNav1).setOnClickListener((v1) -> mPresenter.goBack());
        navBar.findViewById(R.id.ivNav2).setOnClickListener((v1) -> finish());
        navBar.findViewById(R.id.ivNav3).setOnClickListener((v1) -> mPresenter.goForward());
        navBar.findViewById(R.id.ivNav4).setOnClickListener((v1) -> mJoyShare.show());
        return navBar;
    }

    protected final BaseWebViewPresenter getPresenter() {
        return mPresenter;
    }

    protected List<ShareItem> getShareItems() {
        return mJoyShare.getDefaultItems();
    }

    protected void onShareItemClick(int position, View v, ShareItem item) {
        mJoyShare.dismiss();
    }

    @Override
    protected void doOnRetry() {
        mPresenter.reload();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", classSimpleName + " onPageStarted # currentPageIndex: " + mPresenter.getCurrentIndex() + " url: " + url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", classSimpleName + " onPageFinished # url: " + url);
        }
        fadeInTitleMore();
        if (mNavDisplay && mNavAnimate) {
            mNavBar.runEnterAnimator();
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", classSimpleName + " onReceivedError # errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (!isNoTitle() && TextUtil.isEmpty(mTitle)) {
            setTitle(title);
        }
    }

    @Override
    public void onProgress(WebView view, int progress) {
    }

    @Override
    public boolean onOverrideUrl(WebView view, String url) {
        return false;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
    }

    @Override
    public void onHideCustomView() {
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    // file upload callback for 3.0 ~ 5.0
    @Override
    @TargetApi(HONEYCOMB)
    public void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, @Nullable String capture) {
    }

    // file upload callback for >= 5.0
    @Override
    @TargetApi(LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return false;
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (mNavDisplay && mNavAnimate) {
            if (scrollY > oldScrollY) {// to down
                mNavBar.runExitAnimator();
            } else {// to up
                mNavBar.runEnterAnimator();
            }
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.goBack();
    }

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        startActivity(context, url, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable CharSequence title) {
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        context.startActivity(intent);
    }
}
