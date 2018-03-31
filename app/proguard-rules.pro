# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/yogeshsoni/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-printmapping mapping.txt
-keepattributes *Annotation*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

#-------------- google direction classes proguard entry start --------- #
-keep class com.akexorcist.**
-keep class com.akexorcist.googledirection.**
-keep class com.akexorcist.googledirection.model.Direction
-keep class com.akexorcist.googledirection.DirectionCallback
-keep class com.akexorcist.googledirection.GoogleDirection
-keep class com.akexorcist.googledirection.constant.TransportMode
-keep class com.akexorcist.googledirection.model.Direction
-keep class com.akexorcist.googledirection.model.Step
-keepclassmembers class com.akexorcist.googledirection {
   public *;
}
# ------------ google direction class end---------- #

-keep public class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep public class * extends com.models2you.client.ui.base.activity.BaseAppCompatActivity {
    public void *(android.view.View);
}
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends com.models2you.client.ui.base.activity.BaseAppCompatActivity
-keep public class * extends com.models2you.client.ui.base.activity.WebViewActivity

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#------------- Support V4 and V7----------------------#
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# Keep the support v4 library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

#----------- Support Design ---------------#
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#----------- Custom Annotations start -----------#
-keep class com.models2you.model.rest.models.ResponseViews$** { *; }
-keep class com.models2you.model.rest.models.view.RequestBody$** { *; }
-keep class com.models2you.model.model.Models { *; }

-keep public class com.models2you.model.rest.api.**{
    public *;
}

-keep class com.models2you.model.rest.models.** { *; }

-keep class com.models2you.model.event$* { *; }
#----------- Custom Annotations end -----------#

# --------- for debug enable--------- #
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#-------------end ---------------------#

#-----------Retrofit Start-------------#
# Retrofit 2.X
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.* { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-dontwarn okio.**

-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod

-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}

-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
#---------Retrofit End------------------#

#-----------OkHttp Start----------------#
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
#------------OkHttp End------------------#

#---------ButterKnife Start--------------#
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#-----------ButterKnife End---------------#

#-----------EventBus Start----------------#
-keepclassmembers,includedescriptorclasses class ** {
    public void onEvent*(***);
}
# Don't warn for missing support classes
-dontwarn de.greenrobot.event.util.*$Support
-dontwarn de.greenrobot.event.util.*$SupportManagerFragment
#------------EnentBus End----------------#

#------------Glide Start------------------#
# https://github.com/bumptech/glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
#------------Glide End-------------------#

# -----------------Google Play Services library----------------#
-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *

-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Parceler configuration
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.Parceler$$Parcels

# Preserve GMS ads classes
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
# -----------------Google Play Services library End----------------#

#--------------- Job Queue Start ------------------------#
-keep class com.path.android.jobqueue.** { *; }
#--------------- Job Queue End -------------------------#

#----------- Logging Start----------------------------------#
-keep class com.models2you.client.log.LogFactory { *; }
-keep class com.models2you.client.log.LogFactory$Log { *; }

-assumenosideeffects class com.models2you.client.log.LogFactory {
    public static *** getLog(...);
 }

-assumenosideeffects class com.models2you.client.log.LogFactory$Log {
    public java.lang.String formatMessage(...);
    public *** verbose(...);
    public *** info(...);
    public *** warn(...);
    public *** debug(...);
    public *** error(...);
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** d(...);
    public static *** e(...);
}
#----------- Logging End----------------------------------#