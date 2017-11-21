package com.joy.webview.component;

import com.joy.inject.ActivityScope;
import com.joy.webview.module.BaseWebX5Module;
import com.joy.webview.ui.BaseWebX5Activity;
import com.joy.webview.ui.BaseWebX5Fragment;

import dagger.Component;

/**
 * Created by Daisw on 16/8/16.
 */

@ActivityScope
@Component(
        modules = BaseWebX5Module.class
)
public interface BaseWebX5Component {
    void inject(BaseWebX5Activity activity);
    void inject(BaseWebX5Fragment fragment);
}
