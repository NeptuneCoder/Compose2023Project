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


-dontoptimize
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings

-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

#support
# platform version.  We know about them, and they are safe.
#-dontwarn android.support.**
#-keep class android.support.v4.** { *; }
#-keep class android.support.v7.** { *; }
#-keep class android.support.v8.** { *; }
#-keep class android.support.multidex.**{*;}
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**


# BaiduNavi SDK
-dontoptimize
-ignorewarnings
-keeppackagenames com.baidu.**
-keepattributes
Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,
LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod
-dontwarn com.baidu.**
-dontwarn com.baidu.navisdk.**
-dontwarn com.baidu.navi.**
-keep class com.baidu.** { *; }
-keep interface com.baidu.** { *; }
-keep class vi.com.gdi.** { *; }
-dontwarn com.google.protobuf.**
-keep class com.google.protobuf.** { *;}
-keep interface com.google.protobuf.** { *;}
-dontwarn com.google.android.support.v4.**
-keep class com.google.android.support.v4.** { *; }
-keep interface com.google.android.support.v4.app.** { *; }
-keep public class * extends com.google.android.support.v4.**
-keep public class * extends com.google.android.support.v4.app.Fragment
#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}