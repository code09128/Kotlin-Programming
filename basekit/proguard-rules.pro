# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 代碼混淆壓縮比，在0和7之間，默認為5，一般不需要改
-optimizationpasses 5

# 混淆時不使用大小寫混合，混淆後的類名為小寫
-dontusemixedcaseclassnames

# 指定不去忽略非公共的庫的類
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共庫的類的成員
-dontskipnonpubliclibraryclassmembers

#不做預校驗
-dontpreverify

#生成映射文件
-verbose

#使用printmapping指定映射文件的名稱
-printmapping proguardMapping.txt

#指定混淆時採用的算法
-optimizations ! code/ simplification/arithmetic,!field/*.class/merging/*

#保護代碼中的Annotation不被混淆
-keepattributes *Annotation

#避免混淆泛型
-keepattributes Signature

#拋出異常時保​​留代碼行號
-keepattributes SourceFile,LineNumberTable

#保留四大組件，自定義的Application等不被混淆，因為這些類都有可能被外部調用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

#保留自定義控件不被混淆
-keep public class * extends android.view.View

#保留native方法不被混淆
-keepclasswithmembername class * {
    native <methods>;
}

#保留某些子類不被混淆
-keep public class * extends android.app.Activity

#保留R文件下的資源不被混淆
-keep class **.R${*;}

#保留枚舉類不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueof(java.lang.String);
}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

#Delete Log
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
}

#################### Butter Knife ####################
-keep class butterknife.**{ *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder{ *; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
#################### Butter Knife ####################

