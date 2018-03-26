package com.joy.webview.ui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;

import com.joy.inject.ActivityScope;
import com.joy.share.JoyShare;
import com.joy.share.ShareItem;
import com.joy.share.ShareUtil;
import com.joy.ui.activity.BaseUiActivity;
import com.joy.ui.fragment.BaseUiFragment;
import com.joy.ui.view.viewpager.PageChangeListenerAdapter;
import com.joy.ui.view.viewpager.VerticalViewPager;
import com.joy.utils.LayoutInflater;
import com.joy.webview.R;
import com.joy.webview.ui.interfaces.BaseViewPageWeb;
import com.joy.webview.view.NavigationBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.joy.ui.utils.DimenCons.DP;

/**
 * Created by Daisw on 2017/11/17.
 */

@ActivityScope
public class UIPageDelegate<F extends BaseUiFragment> {

    @Inject
    BaseViewPageWeb mBaseView;

    @Inject
    BaseUiActivity mActivity;

    VerticalViewPager mVerticalViewPager;
    List<BasePageEntity> mPageEntities;
    List<F> mFragments;
    int mCurrentPosition;

    JoyShare mJoyShare;
    NavigationBar mNavBar;
    boolean mNavDisplay = false;
    boolean mNavAnimate = true;
    int mNavHeight;
    int mNavElevation;

    @Inject
    UIPageDelegate() {
    }

    void onCreate() {
        mActivity.setContentView(R.layout.lib_view_page_container);
    }

    void resolveThemeAttribute() {
        TypedArray navTa = mActivity.obtainStyledAttributes(R.styleable.NavigationBar);
        mNavDisplay = navTa.getBoolean(R.styleable.NavigationBar_navDisplay, false);
        mNavAnimate = navTa.getBoolean(R.styleable.NavigationBar_navAnimate, true);
        mNavHeight = navTa.getDimensionPixelSize(R.styleable.NavigationBar_navHeight, 0);
        mNavElevation = navTa.getDimensionPixelSize(R.styleable.NavigationBar_navElevation, 0);
        navTa.recycle();
    }

    void initData() {
        Intent intent = mActivity.getIntent();
        mCurrentPosition = intent.getIntExtra("currentPosition", 0);
        mPageEntities = intent.getParcelableArrayListExtra("pageEntities");

        mFragments = new ArrayList<>();
        int i = 0;
        String title, subtitle;
        for (BasePageEntity entity : mPageEntities) {
            if (i == 0) {
                title = mActivity.getString(R.string.toast_nothing);
                subtitle = null;
            } else if (i == mCurrentPosition) {
                title = mPageEntities.get(i - 1).getTitle();
                subtitle = mActivity.getString(R.string.prev_page);
            } else {
                title = entity.getTitle();
                subtitle = mActivity.getString(R.string.next_page);
            }
            F f = (F) mBaseView.getFragment(entity.getUrl());
            if (f instanceof BasePageWebViewFragment) {
                ((BasePageWebViewFragment) f)
                        .setPageTitle(title)
                        .setPageSubtitle(subtitle);
            } else if (f instanceof BasePageWebX5Fragment) {
                ((BasePageWebX5Fragment) f)
                        .setPageTitle(title)
                        .setPageSubtitle(subtitle);
            }
            mFragments.add(f);
            i++;
        }

        mJoyShare = new JoyShare(mActivity);
        mJoyShare.setData(mBaseView.getShareItems());
        mJoyShare.setOnItemClickListener((position, v, item) -> mBaseView.onShareItemClick(position, v, item));
    }

    void initTitle() {
        if (mActivity.hasTitle()) {
            mActivity.setTitle(mPageEntities.get(mCurrentPosition).getTitle());
        }
    }

    void initContentView() {
        addNavBarIfNecessary();
        mVerticalViewPager = mActivity.findViewById(R.id.vvpContainer);
        mVerticalViewPager.setPageMargin(DP(80));
        BasePageWebAdapter<F> adapter =
                new BasePageWebAdapter<>(mActivity.getSupportFragmentManager());
        adapter.setFragments(mFragments);
        mVerticalViewPager.setAdapter(adapter);
        if (mCurrentPosition != 0) {
            mVerticalViewPager.setCurrentItem(mCurrentPosition, false);
        }
        mVerticalViewPager.setOnPageChangeListener(new PageChangeListenerAdapter() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                if (mActivity.hasTitle()) {
                    mActivity.setTitle(mPageEntities.get(position).getTitle());
                }
                if (position > 0) {
                    updateTitles(mFragments.get(position), position - 1);
                }
                if (position + 1 < mFragments.size()) {
                    updateTitles(mFragments.get(position + 1), position + 1);
                }
            }
        });
    }

    private void updateTitles(F f, int position) {
        if (f instanceof BasePageWebViewFragment) {
            ((BasePageWebViewFragment) f)
                    .setPageTitle(mPageEntities.get(position).getTitle())
                    .setPageSubtitle(mActivity.getString(position < mCurrentPosition ? R.string.prev_page : R.string.next_page));
        } else if (f instanceof BasePageWebX5Fragment) {
            ((BasePageWebX5Fragment) f)
                    .setPageTitle(mPageEntities.get(position).getTitle())
                    .setPageSubtitle(mActivity.getString(position < mCurrentPosition ? R.string.prev_page : R.string.next_page));
        }
    }

    public String getUrl() {
        return mPageEntities.get(mCurrentPosition).getUrl();
    }

    public String getTitle() {
        return mPageEntities.get(mCurrentPosition).getTitle();
    }

    public JoyShare getJoyShare() {
        return mJoyShare;
    }

    boolean onShareItemClick(ShareItem item) {
        getJoyShare().dismiss();
        ShareItem.DEFAULT def = item.mDefault;
        if (def != null) {
            switch (def) {
                case COPY_LINK:
                    ShareUtil.copyUrl(mActivity, getUrl());
                    return true;
                case BROWSER:
                    ShareUtil.openBrowser(mActivity, getUrl());
                    return true;
                case MORE:
                    ShareUtil.shareTextUrl(mActivity, getUrl(), getTitle());
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void addNavBarIfNecessary() {
        if (mNavDisplay) {
            mNavBar = mBaseView.initNavigationBar();
            if (mNavBar != null) {
                addNavigationBar(mNavBar, generateNavBarLp(), mNavAnimate, false);
            }
        }
    }

    private void addNavigationBar(@NonNull NavigationBar navBar, @NonNull LayoutParams lp, boolean animate, boolean showLater) {
        if (animate) {
            navBar.setAlpha(0.f);
            navBar.setTranslationY(lp.height);
        } else {
            mActivity.getContentViewLp().bottomMargin = lp.height - mNavElevation;
        }
        mActivity.addContentView(navBar, lp);
        if (animate && showLater) {
            navBar.runEnterAnimator();
        }
        mNavDisplay = true;
        mNavBar = navBar;
        mNavHeight = lp.height;
        mNavAnimate = animate;
    }

    private LayoutParams generateNavBarLp() {
        LayoutParams lp = new LayoutParams(MATCH_PARENT, mNavHeight);
        lp.gravity = Gravity.BOTTOM;
        return lp;
    }

    @Nullable
    NavigationBar initNavigationBar() {
        NavigationBar navBar = LayoutInflater.inflate(mActivity, R.layout.lib_view_web_navigation_bar);
        navBar.getIvNav(0).setOnClickListener((v1) -> mBaseView.onNavigationItemClick(0));
        navBar.getIvNav(1).setOnClickListener((v1) -> mBaseView.onNavigationItemClick(1));
        navBar.getIvNav(2).setOnClickListener((v1) -> mBaseView.onNavigationItemClick(2));
        navBar.getIvNav(3).setOnClickListener((v1) -> mBaseView.onNavigationItemClick(3));
        return navBar;
    }

    void onNavigationItemClick(int position) {
        switch (position) {
            case 1:
                mVerticalViewPager.pageUp();
                break;
            case 2:
                mVerticalViewPager.pageDown();
                break;
            case 3:
                mJoyShare.show();
                break;
            default:
                break;
        }
    }

    void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (mNavDisplay && mNavAnimate && mNavBar != null) {
            if (scrollY > oldScrollY) {// to down
                mNavBar.runExitAnimator();
            } else {// to up
                mNavBar.runEnterAnimator();
            }
        }
    }

    public F getCurrentFragment() {
        return mFragments.get(mCurrentPosition);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }
}
