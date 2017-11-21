package com.joy.webview.ui.interfaces;

import android.support.annotation.NonNull;
import android.view.View;

import com.joy.share.ShareItem;
import com.joy.ui.fragment.BaseUiFragment;
import com.joy.ui.interfaces.BaseView;
import com.joy.webview.view.NavigationBar;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.List;

/**
 * Created by Daisw on 2017/11/17.
 */

public interface BaseViewPageWeb<F extends BaseUiFragment> extends BaseView<ActivityEvent> {

    F getFragment(@NonNull String url);

    NavigationBar initNavigationBar();
    void onNavigationItemClick(int position);

    List<ShareItem> getShareItems();
    boolean onShareItemClick(int position, View v, ShareItem item);
}
