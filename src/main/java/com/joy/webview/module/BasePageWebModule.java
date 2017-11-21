package com.joy.webview.module;

import android.app.Activity;

import com.joy.inject.ActivityScope;
import com.joy.inject.module.ActivityModule;
import com.joy.ui.activity.BaseUiActivity;
import com.joy.webview.ui.interfaces.BaseViewPageWeb;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Daisw on 2017/11/15.
 */

@Module(includes = ActivityModule.class)
public class BasePageWebModule {

    private final BaseViewPageWeb mBaseViewPageWeb;

    public BasePageWebModule(BaseViewPageWeb baseViewPageWeb) {
        mBaseViewPageWeb = baseViewPageWeb;
    }

    @Provides
    @ActivityScope
    BaseViewPageWeb provideBaseViewPageWeb() {
        return mBaseViewPageWeb;
    }

    @Provides
    @ActivityScope
    BaseUiActivity provideBaseUiActivity(Activity activity) {
        return (BaseUiActivity) activity;
    }
}
