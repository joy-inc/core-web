package com.joy.webview.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joy.utils.TextUtil;
import com.joy.webview.R;
import com.joy.webview.view.WebX5;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.joy.ui.utils.DimenCons.DP;

/**
 * Created by Daisw on 2017/11/15.
 */

public class BasePageWebX5Fragment extends BaseWebX5Fragment {

    private TextView mTvPageTitle, mTvPageSubtitle;
    private String mPageTitle, mPageSubtitle;

    public WebX5 getWebView() {
        return (WebX5) getPresenter().getWebView();
    }

    @Override
    public void setContentView(View contentView) {
        LinearLayout llRoot = new LinearLayout(getActivity());
        llRoot.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        llRoot.setOrientation(LinearLayout.VERTICAL);
        llRoot.addView(inflateLayout(R.layout.lib_view_page_margin), new LayoutParams(MATCH_PARENT, DP(80)));
        llRoot.addView(contentView, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
        super.setContentView(llRoot);
    }

    @Override
    protected void initContentView() {
        super.initContentView();
        getWebView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        mTvPageTitle = findViewById(R.id.tvPageTitle);
        setPageTitle(mPageTitle);
        mTvPageSubtitle = findViewById(R.id.tvPageSubtitle);
        setPageSubtitle(mPageSubtitle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTvPageTitle = null;
        mTvPageSubtitle = null;
    }

    public BasePageWebX5Fragment setPageTitle(String title) {
        if (mTvPageTitle != null) {
            mTvPageTitle.setText(title);
        } else {
            mPageTitle = title;
        }
        return this;
    }

    public BasePageWebX5Fragment setPageSubtitle(String subtitle) {
        if (mTvPageSubtitle != null) {
            mTvPageSubtitle.setText(subtitle);
            mTvPageSubtitle.setVisibility(TextUtil.isEmpty(subtitle) ? View.GONE : View.VISIBLE);
        } else {
            mPageSubtitle = subtitle;
        }
        return this;
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        ((BasePageWebX5Activity) getActivity()).onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
    }

    public static BasePageWebX5Fragment instantiate(@NonNull Context context, @NonNull String url) {
        return instantiate(context, url, false);
    }

    public static BasePageWebX5Fragment instantiate(@NonNull Context context, @NonNull String url, boolean cacheEnable) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putBoolean(KEY_CACHE_ENABLE, cacheEnable);
        return (BasePageWebX5Fragment) Fragment.instantiate(context, BasePageWebX5Fragment.class.getName(), bundle);
    }
}
