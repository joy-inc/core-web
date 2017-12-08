# core-web

**Android Webkit + Tencent X5 Webkit + MVP + Dagger2**

### 外部引用

```
compile 'com.joy.support:core-web:0.4.8'
```

### 自身依赖

```
compile 'com.joy.support:core-share:0.2.7'
compile 'com.joy.support:x5-download:3.5.0'
compile 'org.jsoup:jsoup:1.10.3'
apt 'com.google.dagger:dagger-compiler:2.4'
```

### 版本历史

- `0.4.8` fix getScale()不为整数时，判断是否可以滚动存在误差；重定向URL依然回调onOverrideUrl；增加onConsoleMessage回调方法；fix NoSuchMethodError: No virtual method DP(F)；整理代码；

- `0.4.2` 增加BaseWebViewFragment/BaseWebX5Fragment；增加BasePageWebViewActivity/BasePageWebX5Activity支持webview纵向翻页；BaseViewWeb/BaseViewWebX5增加泛型，移除无用的方法，整理代码；

- `0.4.1` 更新x5到3.5.0；更新Jsoup到1.10.3；更新core-share到0.2.5；

- `0.4.0` 升级x5到3.3.0; 适配上层库的修改；更改fadeInTitleAll()的调用时机；

- `0.3.9` 对shouldInterceptRequest的处理；拆分BaseViewWeb、UIDelegate；部分参数重命名（不重要）；

- `0.3.8` 引用x5最新SDK2.6.0，适配新版本的写法；适配core-ui层Toolbar主题的更改；适配JoyShare相关的更改；增加方法，整理代码；

- `0.3.7` navigationBar增加一个icon & 分享；整理代码；增加addNavigationBar、setNavigationBarVisible方法；增加方法（Toolbar相关）；

- `0.2.2` 允许自定义Toolbar drawable资源；追加（BaseWebX5Presenter忘改了）；完善超时逻辑；整理代码；

## Initialize（必须继承BaseApplication）

```
public class YourApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        JoyWeb.initX5Environment(this, callback);// 预加载腾讯X5内核，如果用到腾讯X5服务必须调用此方法，callback可以为null。
    }
}
```

## StartActivity

**BaseWebViewActivity**

```
public static void startSelf(@NonNull Context context, @NonNull String url);
public static void startSelf(@NonNull Context context, @NonNull String url, boolean cacheEnable);
public static void startSelf(@NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable);
public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url);
public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, boolean cacheEnable);
public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable);
```

**BaseWebX5Activity**

```
public static void startSelf(@NonNull Context context, @NonNull String url);
public static void startSelf(@NonNull Context context, @NonNull String url, boolean cacheEnable);
public static void startSelf(@NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable);
public static void startTarget(Class<? extends BaseWebX5Activity> target, @NonNull Context context, @NonNull String url);
public static void startTarget(Class<? extends BaseWebX5Activity> target, @NonNull Context context, @NonNull String url, boolean cacheEnable);
public static void startTarget(Class<? extends BaseWebX5Activity> target, @NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable);
```

## Theme

**Network: loadingView / errorTip / emptyTip**

```
    <declare-styleable name="Theme">
        <attr name="loadingView" format="reference" />
        <attr name="errorTip" format="reference" />
        <attr name="emptyTip" format="reference" />
    </declare-styleable>
```

**Toolbar: noTitle / overlay**

```
    <declare-styleable name="Toolbar">
        <attr name="noTitle" format="boolean" />
        <attr name="overlay" format="boolean" />
    </declare-styleable>
```

**WebView: longClickable**

```
    <declare-styleable name="Theme">
        <attr name="longClickable" format="boolean" />
        <attr name="titleMoreEnable" format="boolean" />
        <attr name="titleCloseEnable" format="boolean" />
        <attr name="progressEnable" format="boolean" />
        <attr name="progressHeight" format="dimension" />
    </declare-styleable>
```

**WebView NavigationBar:**

```
    <declare-styleable name="NavigationBar">
        <attr name="navDisplay" format="boolean" />
        <attr name="navAnimate" format="boolean" />
        <attr name="navHeight" format="dimension" />
        <attr name="navElevation" format="dimension" />
        <attr name="navBackgroundColor" format="color" />
        <attr name="navIconFirst" format="reference" />
        <attr name="navIconSecond" format="reference" />
        <attr name="navIconThird" format="reference" />
        <attr name="navIconFourth" format="reference" />
        <attr name="navIconBackground" format="reference" />
        <attr name="navIconAlpha" format="float" />
    </declare-styleable>
```

### Joy-Library中的引用体系

![](core-web.png)
