package com.joy.webview.ui;

import android.annotation.TargetApi;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joy.share.JoyShare;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseUiActivity;
import com.joy.utils.LayoutInflater;
import com.joy.utils.LogMgr;
import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.presenter.IPresenter;
import com.joy.webview.ui.interfaces.BaseViewWeb;
import com.joy.webview.utils.AnimatorUtils;
import com.joy.webview.view.NavigationBar;

import java.util.List;

import javax.inject.Inject;

import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.joy.ui.utils.DimenCons.HORIZONTAL_MARGINS;
import static com.joy.ui.utils.DimenCons.STATUS_BAR_HEIGHT;
import static com.joy.webview.ui.interfaces.KConstant.DP_40;
import static com.joy.webview.ui.interfaces.KConstant.KEY_TITLE;
import static com.joy.webview.ui.interfaces.KConstant.KEY_URL;

/**
 * Created by Daisw on 2016/11/4.
 */

public class UIDelegate {

    @Inject
    BaseViewWeb mBaseView;

    @Inject
    BaseUiActivity mActivity;

    @Inject
    IPresenter mPresenter;

    String mUrl;
    CharSequence mTitle;
    TextView mTvTitle;
    boolean mTitleMoreEnable;
    boolean mTitleCloseEnable;
    boolean mProgressEnable;
    ImageButton mIbTitleMore;
    ImageButton mIbTitleClose;
    JoyShare mJoyShare;
    ProgressBar mProgressBar;
    NavigationBar mNavBar;
    boolean mNavDisplay = false;
    boolean mNavAnimate = true;
    int mNavHeight;
    int mNavElevation;

    boolean mLongClickable = true;

    @Inject
    UIDelegate() {
    }

    void onCreate() {
        mActivity.setContentView(mPresenter.getWebView());
        mPresenter.load(mUrl);
    }

    void resolveThemeAttribute() {
        TypedArray themeTa = mActivity.obtainStyledAttributes(R.styleable.Theme);
        mLongClickable = themeTa.getBoolean(R.styleable.Theme_longClickable, true);
        mTitleMoreEnable = themeTa.getBoolean(R.styleable.Theme_titleMoreEnable, true);
        mTitleCloseEnable = themeTa.getBoolean(R.styleable.Theme_titleCloseEnable, true);
        mProgressEnable = themeTa.getBoolean(R.styleable.Theme_progressEnable, false);
        themeTa.recycle();

        TypedArray navTa = mActivity.obtainStyledAttributes(R.styleable.NavigationBar);
        mNavDisplay = navTa.getBoolean(R.styleable.NavigationBar_navDisplay, false);
        mNavAnimate = navTa.getBoolean(R.styleable.NavigationBar_navAnimate, true);
        mNavHeight = navTa.getDimensionPixelSize(R.styleable.NavigationBar_navHeight, 0);
        mNavElevation = navTa.getDimensionPixelSize(R.styleable.NavigationBar_navElevation, 0);
        navTa.recycle();
    }

    void initData() {
        mUrl = mActivity.getIntent().getStringExtra(KEY_URL);
        mTitle = mActivity.getIntent().getCharSequenceExtra(KEY_TITLE);

        mJoyShare = new JoyShare(mActivity);
        mJoyShare.setData(mBaseView.getShareItems());
        mJoyShare.setOnItemClickListener((position, v, item) -> mBaseView.onShareItemClick(position, v, item));
    }

    void initTitleView() {
        if (!mActivity.isNoTitle()) {
            mActivity.addTitleLeftBackView((v) -> mPresenter.goBack());
            if (mTitleMoreEnable) {
                mIbTitleMore = mActivity.addTitleRightMoreView(getTitleMoreClickListener());
                mIbTitleMore.setAlpha(0.f);
            }
            if (mTitleCloseEnable) {
                mIbTitleClose = mActivity.addTitleRightView(R.drawable.ic_close_white_24dp, getTitleCloseClickListener());
                mIbTitleClose.setAlpha(0.f);
            }
            if (mTitleMoreEnable && mTitleCloseEnable) {
                mIbTitleMore.setMinimumWidth(DP_40);
                mIbTitleClose.setMinimumWidth(DP_40);
            }
            mTvTitle = mActivity.addTitleMiddleView(mTitle);
            if (TextUtil.isEmpty(mTitle)) {
                mTvTitle.setAlpha(0.f);
            }
            if (mActivity.getToolbar().getChildCount() == 2) {
                ((Toolbar.LayoutParams) mTvTitle.getLayoutParams()).rightMargin = HORIZONTAL_MARGINS;
            }
        }
    }

    void initContentView() {
        mPresenter.getWebView().setOnLongClickListener((v) -> !mLongClickable);
        addProgressBarIfNecessary();
        addNavBarIfNecessary();
    }

    List<ShareItem> getShareItems() {
        return mJoyShare.getDefaultItems();
    }

    void onShareItemClick(int position, View v, ShareItem item) {
        mJoyShare.dismiss();
    }

    void setTitleMoreEnable(boolean enable) {
        mTitleMoreEnable = enable;
    }

    void onTitleMoreClick() {
        mJoyShare.show();
    }

    View.OnClickListener getTitleMoreClickListener() {
        return (v) -> {
            if (v.getAlpha() == 1.f)
                mBaseView.onTitleMoreClick();
        };
    }

    void setTitleCloseEnable(boolean enable) {
        mTitleCloseEnable = enable;
    }

    void onTitleCloseClick() {
        mActivity.finish();
    }

    View.OnClickListener getTitleCloseClickListener() {
        return (v) -> {
            if (v.getAlpha() == 1.f)
                mBaseView.onTitleCloseClick();
        };
    }

    void fadeInTitleMore() {
        AnimatorUtils.fadeIn(mIbTitleMore, 200);
    }

    void fadeInTitleAll() {
        if (mTitleCloseEnable) {
            AnimatorUtils.fadeIn(mIbTitleClose, 200);
            if (mTitleMoreEnable) {
                AnimatorUtils.fadeIn(mIbTitleMore, 400);
            }
        } else {
            if (mTitleMoreEnable) {
                AnimatorUtils.fadeIn(mIbTitleMore, 200);
            }
        }
    }

    void setTitle(CharSequence title) {
        AnimatorUtils.fadeIn(mTvTitle, title);
    }

    boolean isProgressEnabled() {
        return mProgressEnable;
    }

    @SuppressWarnings("ResourceType")
    private void addProgressBarIfNecessary() {
        if (mProgressEnable) {
            mProgressBar = mBaseView.initProgressBar();
            mProgressBar.setAlpha(0.f);
            LayoutParams progressLp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            if (!mActivity.isNoTitle() && !mActivity.isOverlay()) {
                int height = mActivity.getToolbarHeight();
                progressLp.topMargin = mActivity.isSystemBarTrans() ? STATUS_BAR_HEIGHT + height : height;
            }
            mActivity.addContentView(mProgressBar, progressLp);
        }
    }

    ProgressBar initProgressBar() {
        return LayoutInflater.inflate(mActivity, R.layout.lib_view_web_progress_bar);
    }

    private void addNavBarIfNecessary() {
        if (mNavDisplay) {
            mNavBar = mBaseView.initNavigationBar();
            if (mNavAnimate) {
                mNavBar.setAlpha(0.f);
                mNavBar.setTranslationY(mNavHeight);
            } else {
                mActivity.getContentViewLp().bottomMargin = mNavHeight - mNavElevation;
            }
            LayoutParams navBarLp = new LayoutParams(MATCH_PARENT, mNavHeight);
            navBarLp.gravity = Gravity.BOTTOM;
            mActivity.addContentView(mNavBar, navBarLp);
        }
    }

    NavigationBar initNavigationBar() {
        NavigationBar navBar = LayoutInflater.inflate(mActivity, R.layout.lib_view_web_navigation_bar);
        navBar.findViewById(R.id.ivNav1).setOnClickListener((v1) -> mPresenter.goBack());
        navBar.findViewById(R.id.ivNav2).setOnClickListener((v1) -> mActivity.finish());
        navBar.findViewById(R.id.ivNav3).setOnClickListener((v1) -> mPresenter.goForward());
        navBar.findViewById(R.id.ivNav4).setOnClickListener((v1) -> mJoyShare.show());
        return navBar;
    }

    void onPageStarted(String url, Bitmap favicon) {
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", mActivity.getClass().getSimpleName() + " onPageStarted # url: " + url);
        }
        AnimatorUtils.fadeIn(mProgressBar);
    }

    void onPageFinished(String url) {
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", mActivity.getClass().getSimpleName() + " onPageFinished # url: " + url);
        }
        if (mNavDisplay && mNavAnimate) {
            mNavBar.runEnterAnimator();
        }
    }

    void onReceivedError(int errorCode, String description, String failingUrl) {
        if (LogMgr.DEBUG) {
            LogMgr.d("core-web", mActivity.getClass().getSimpleName() + " onReceivedError # errorCode: " + errorCode + " description: " + description + " failingUrl: " + failingUrl);
        }
    }

    void onReceivedTitle(String title) {
        if (!mActivity.isNoTitle()) {
            if (TextUtil.isEmpty(mTitle)) {
                setTitle(title);
            }
            fadeInTitleAll();
        }
    }

    void onProgress(int progress) {
        if (mProgressEnable && mProgressBar != null) {
            mProgressBar.setProgress(progress);
            if (progress == 100) {
                AnimatorUtils.fadeOut(mProgressBar);
            }
        }
    }

    boolean onOverrideUrl(String url) {
        return false;
    }

    void onShowCustomView(View view) {
    }

    void onHideCustomView() {
    }

    void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    // file upload callback for 3.0 ~ 5.0
    @TargetApi(HONEYCOMB)
    void onShowFileChooser(ValueCallback<Uri> filePathCallback, String acceptType) {
    }

    // file upload callback for >= 5.0
    @TargetApi(LOLLIPOP)
    boolean onShowFileChooser(ValueCallback<Uri[]> filePathCallback) {
        return false;
    }

    void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (mNavDisplay && mNavAnimate) {
            if (scrollY > oldScrollY) {// to down
                mNavBar.runExitAnimator();
            } else {// to up
                mNavBar.runEnterAnimator();
            }
        }
    }

    void onBackPressed() {
        mPresenter.goBack();
    }

    void load() {
        mPresenter.load(mUrl);
    }

    void reload() {
        mPresenter.reload();
    }

    IPresenter getPresenter() {
        return mPresenter;
    }

    public String getUrl() {
        return mPresenter.getUrl();
    }

    public String getTitle() {
        return mPresenter.getTitle();
    }

    public NavigationBar getNavigationBar() {
        return mNavBar;
    }
}
