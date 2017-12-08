package com.joy.webview.view;

import android.content.Context;
import android.util.AttributeSet;

import com.joy.ui.view.viewpager.VerticalViewPager;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by Daisw on 2016/11/23.
 */

public class WebX5 extends WebView implements VerticalViewPager.Scrollable {

    public WebX5(Context context) {
        super(context);
    }

    public WebX5(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebX5(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean canScrollUp() {
        final float scale = getScale();
        final float approximately = Math.floor(scale) < scale ? scale : 0;
        return getWebScrollY() < Math.floor(getContentHeight() * getScale() - getMeasuredHeight()) - approximately;
    }

    @Override
    public boolean canScrollDown() {
        return getWebScrollY() > 0;
    }
}
