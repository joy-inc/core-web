### core-web

Android Webkit + Tencent X5 Webkit

### Add core-web to your project

Gradle:

```
compile 'com.joy.support:core-web:0.1.0'
```

Maven:

```
<dependency>
  <groupId>com.joy.support</groupId>
  <artifactId>core-web</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>
```

Ivy:

```
<dependency org='com.joy.support' name='core-web' rev='0.1.0'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```

### Example

```
public class JoyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JoyWeb.setUserAgent(user_agent);
        if (isLogin()) {
            JoyWeb.setCookie(cookie_url);
        }
    }

    public static void releaseJoyWeb() {
        JoyWeb.release();
    }
}
```

### StartActivity

**BaseWebViewActivity**

```
BaseWebViewActivity.startActivity(@NonNull Context context, @NonNull String url);
BaseWebViewActivity.startActivity(@NonNull Context context, @NonNull String url, @Nullable String title);
```

**BaseWebX5Activity**

```
BaseWebX5Activity.startActivity(@NonNull Context context, @NonNull String url);
BaseWebX5Activity.startActivity(@NonNull Context context, @NonNull String url, @Nullable String title);
```

### Theme

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
</declare-styleable>
```
