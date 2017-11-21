package com.joy.webview.component;

import com.joy.inject.ActivityScope;
import com.joy.webview.module.BasePageWebModule;
import com.joy.webview.ui.BasePageWebViewActivity;
import com.joy.webview.ui.BasePageWebX5Activity;

import dagger.Component;

/**
 * Created by Daisw on 2017/11/15.
 */

@ActivityScope
@Component(
        modules = BasePageWebModule.class
)
public interface BasePageWebComponent {
    void inject(BasePageWebViewActivity activity);
    void inject(BasePageWebX5Activity activity);
}
