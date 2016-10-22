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
import android.widget.TextView;

import com.joy.inject.module.ActivityModule;
import com.joy.share.JoyShare;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseHttpUiActivity;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.component.BaseWebViewComponent;
import com.joy.webview.component.DaggerBaseWebViewComponent;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.presenter.BaseWebViewPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.ui.interfaces.KConstant;
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

    @Inject
    BaseWebViewPresenter mPresenter;

    protected String mUrl;
    protected String mTitle;
    protected TextView mTvTitle;
    protected JoyShare mJoyShare;
    protected NavigationBar mNavBar;
    protected boolean mNavDisplay = true;
    protected boolean mNavAnimate = true;
    protected int mNavHeight;
    protected int mNavElevation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        component().inject(this);
        setContentView(mPresenter.getWebView());
        resolveThemeAttribute();// remove
        addNavBarIfNecessary();
        mPresenter.load(mUrl);
    }

    private BaseWebViewComponent component() {
        return DaggerBaseWebViewComponent.builder()
                .activityModule(new ActivityModule(this))
                .baseWebViewModule(new BaseWebViewModule(this))
                .build();
    }

    //    @Override
    protected void resolveThemeAttribute() {
        TypedArray a = obtainStyledAttributes(R.styleable.NavigationBar);
        mNavDisplay = a.getBoolean(R.styleable.NavigationBar_navDisplay, true);
        mNavAnimate = a.getBoolean(R.styleable.NavigationBar_navAnimate, true);
        mNavHeight = a.getDimensionPixelSize(R.styleable.NavigationBar_navHeight, 0);
        mNavElevation = a.getDimensionPixelSize(R.styleable.NavigationBar_navElevation, 0);
        a.recycle();
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

    @Override
    protected void initData() {
        mUrl = getIntent().getStringExtra(KEY_URL);
        mTitle = getIntent().getStringExtra(KEY_TITLE);
        mPresenter.setUserAgent(getIntent().getStringExtra(KEY_USER_AGENT));

        mJoyShare = new JoyShare(this);
        mJoyShare.setData(getShareItems());
        mJoyShare.setOnItemClickListener(this::onShareItemClick);
    }

    @Override
    protected void initTitleView() {
        if (!isNoTitle()) {
            addTitleLeftBackView();
            addTitleRightMoreView((v) -> mJoyShare.show());
            mTvTitle = addTitleMiddleView(mTitle);
        }
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
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (mNavDisplay && mNavAnimate) {
            mNavBar.runEnterAnimator();
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (!isNoTitle() && TextUtil.isEmpty(mTitle)) {
            mTvTitle.setText(title);
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

    public static void startActivity(@NonNull Context context, @NonNull String url) {
        startActivity(context, url, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String title) {
        startActivity(context, url, title, null);
    }

    public static void startActivity(@NonNull Context context, @NonNull String url, @Nullable String title, @Nullable String userAgent) {
        Intent intent = new Intent(context, BaseWebViewActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_USER_AGENT, userAgent);
        context.startActivity(intent);
    }
}
