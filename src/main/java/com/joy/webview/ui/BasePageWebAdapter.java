package com.joy.webview.ui;

import android.support.v4.app.FragmentManager;

import com.joy.ui.adapter.ExFragmentPagerAdapter;
import com.joy.ui.fragment.BaseUiFragment;
import com.joy.ui.view.viewpager.VerticalViewPager;

/**
 * Created by Daisw on 2017/11/16.
 */

public class BasePageWebAdapter<T extends BaseUiFragment> extends ExFragmentPagerAdapter<T> implements VerticalViewPager.ScrollableProvider {

    public BasePageWebAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public VerticalViewPager.Scrollable provideScrollable(int position) {
        T t = getItem(position);
        if (t instanceof BasePageWebViewFragment) {
            return ((BasePageWebViewFragment) t).getWebView();
        } else if (t instanceof BasePageWebX5Fragment) {
            return ((BasePageWebX5Fragment) t).getWebView();
        }
        return null;
    }
}
