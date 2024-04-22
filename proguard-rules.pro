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

 -keep class org.apache.log4j.**{*;}

 # kotlin相关
 -keep class kotlin.** { *; }
 -keep class kotlin.Metadata { *; }
 -dontwarn kotlin.**
 -keepclassmembers class **$WhenMappings {
     <fields>;
 }
 -keepclassmembers class kotlin.Metadata {
     public <methods>;
 }
 -assumenosideeffects class kotlin.jvm.internal.Intrinsics {
     static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
 }

 # 保持bean
 -keep class **.bean.** {*;}
 -keep class **.data.** {*;}
 -keep class **.entity.** {*;}
 # 保持 base bean
 -keep class * implements com.common.base.net.ApiResponse {*;}

# ARouter
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
 -keep class * implements com.alibaba.android.arouter.facade.template.IProvider

 # 删除debug日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
    public static *** i(...);
    public static *** v(...);
    public static *** println(...);
    public static *** w(...);
    public static *** wtf(...);
}
# Bugly混淆配置
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# tinker混淆规则
-dontwarn com.tencent.tinker.**
-keep class com.tencent.tinker.** { *; }

# support
-keep class android.support.**{*;}
#bugly 符号表
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

#谷歌支付
-keep class com.android.vending.billing.**
-keep class com.android.billingclient.**
-keep class com.google.android.gms.internal.play_billing.**
-keep class com.google.android.gms.**

 # Add this global rule
-keepattributes Signature


 # FastJson 混淆代码
 -dontwarn com.alibaba.fastjson.**
 -keep class com.alibaba.fastjson.** { *; }
 -keepattributes Signature
 -keepattributes *Annotation*



 # greenDao
 -keep class org.greenrobot.greendao.**{*;}
 -keep public interface org.greenrobot.greendao.**
 -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
 public static java.lang.String TABLENAME;
 }
 -keep class **$Properties
 -keep class net.sqlcipher.database.**{*;}
 -keep public interface net.sqlcipher.database.**
 -dontwarn net.sqlcipher.database.**
 -dontwarn org.greenrobot.greendao.**

 ### greenDAO 3
 -keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
 public static java.lang.String TABLENAME;
 }
 -keep class **$Properties

 # If you do not use SQLCipher:
 -dontwarn org.greenrobot.greendao.database.**
 # If you do not use RxJava:
 -dontwarn rx.**

 ### greenDAO 2
 -keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
 public static java.lang.String TABLENAME;
 }
 -keep class **$Properties



# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

# OkHttp3
-dontwarn com.squareup.okhttp3.**
-dontwarn okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# RxJava RxAndroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Retrofit
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度
-dontpreverify

# 忽略警告
-ignorewarnings

# 保留R下面的资源
-keep class **.R$* {*;}

# 保留support下的所有类及其内部类
-keep class android.support.** {*;}
-dontwarn android.support.**

# 避免混淆泛型
-keepattributes Signature
-keepattributes EnclosingMethod

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

## 保留Parcelable序列化类不被混淆
#-keep class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}

#这样只是不混淆类名，类里面的属性和方法还是被混淆掉了
# -keep public class * extends android.os.Parcelable


#只有这样配置，类名、及其类内部的所有东西都不会被混淆
-keep class * extends android.os.Parcelable{*;}


-keep class * implements java.io.Serializable{*;}

# GSON
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.squareup.leakcanary.**{*;}
# 自定义的实体类
# 使用Gson时需要配置Gson的解析对象及变量都不混淆。不然Gson会找不到变量。
# 将下面替换成自己的实体类
# Keep models for Gson
-keep class com.gdiwing.baselib.net.bean.**{*;}
-keep class com.gdiwing.baselib.net.event.**{*;}