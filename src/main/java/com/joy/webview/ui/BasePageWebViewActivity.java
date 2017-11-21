package com.joy.webview.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.joy.inject.module.ActivityModule;
import com.joy.share.ShareItem;
import com.joy.ui.activity.BaseUiActivity;
import com.joy.webview.component.DaggerBasePageWebComponent;
import com.joy.webview.module.BasePageWebModule;
import com.joy.webview.ui.interfaces.BaseViewPageWeb;
import com.joy.webview.view.NavigationBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Daisw on 2017/11/15.
 */

public class BasePageWebViewActivity extends BaseUiActivity implements BaseViewPageWeb<BasePageWebViewFragment> {

    @Inject
    UIPageDelegate<BasePageWebViewFragment> mUIDelegate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerBasePageWebComponent.builder()
                .activityModule(new ActivityModule(this))
                .basePageWebModule(new BasePageWebModule(this))
                .build()
                .inject(this);
        getUIDelegate().onCreate();
    }

    @Override
    public void resolveThemeAttribute() {
        super.resolveThemeAttribute();
        getUIDelegate().resolveThemeAttribute();
    }

    @Override
    protected void initData() {
        getUIDelegate().initData();
    }

    @Override
    protected void initTitleView() {
        super.initTitleView();
        getUIDelegate().initTitle();
    }

    @Override
    public void onTitleMoreClick(View v) {
        getUIDelegate().getJoyShare().show();
    }

    @Override
    protected void initContentView() {
        getUIDelegate().initContentView();
    }

    @Override
    public NavigationBar initNavigationBar() {
        return getUIDelegate().initNavigationBar();
    }

    @Override
    public void onNavigationItemClick(int position) {
        getUIDelegate().onNavigationItemClick(position);
    }

    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        getUIDelegate().onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);
    }

    @Override
    public List<ShareItem> getShareItems() {
        return getUIDelegate().getJoyShare().getDefaultItems();
    }

    @Override
    public boolean onShareItemClick(int position, View v, ShareItem item) {
        return getUIDelegate().onShareItemClick(item);
    }

    public UIPageDelegate<BasePageWebViewFragment> getUIDelegate() {
        return mUIDelegate;
    }

    @Override
    public BasePageWebViewFragment getFragment(@NonNull String url) {
        return BasePageWebViewFragment.instantiate(this, url);
    }

    public static void startActivity(Activity act, ArrayList<? extends BasePageEntity> pageEntities) {
        startActivity(act, pageEntities, 0);
    }

    public static void startActivity(Activity act, ArrayList<? extends BasePageEntity> pageEntities, int position) {
        Intent intent = new Intent(act, BasePageWebViewActivity.class);
        intent.putParcelableArrayListExtra("pageEntities", pageEntities);
        intent.putExtra("currentPosition", position);
        act.startActivity(intent);
    }
}
