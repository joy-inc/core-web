package com.joy.webview.component;

import com.joy.inject.ActivityScope;
import com.joy.webview.module.BaseWebViewModule;
import com.joy.webview.ui.BaseWebViewActivity;

import dagger.Component;

/**
 * Created by Daisw on 16/8/16.
 */

@ActivityScope
@Component(
        modules = BaseWebViewModule.class
)
public interface BaseWebViewComponent {

    void inject(BaseWebViewActivity activity);
}
