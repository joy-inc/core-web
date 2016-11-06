
# core-web

Android Webkit + Tencent X5 Webkit + MVP + Dagger2

## Add core-web to your project

Gradle:

```
compile 'com.joy.support:core-web:0.2.1'
```

Maven:

```
<dependency>
  <groupId>com.joy.support</groupId>
  <artifactId>core-web</artifactId>
  <version>0.2.1</version>
  <type>pom</type>
</dependency>
```

Ivy:

```
<dependency org='com.joy.support' name='core-web' rev='0.2.1'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```

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
public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url);
public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, boolean cacheEnable);
public static void startTarget(Class<? extends BaseWebViewActivity> target, @NonNull Context context, @NonNull String url, @Nullable CharSequence title, boolean cacheEnable);
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
