# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature
# 保持反射不被混淆
-keepattributes EnclosingMethod

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

# 保留自定义的View
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class com.htc.horizonos.entry.**{*;}

############################################
# OkHttp 5.x / Okio
# 必须保留类和方法，防止 HTTPS / TLS 崩溃
############################################
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

############################################
# Lottie
# JSON -> Model 是反射映射，必须 keep
############################################
-keep class com.airbnb.lottie.** { *; }

############################################
# Banner (io.github.youth5201314)
# Adapter / View + 泛型 / 反射，必须 keep
############################################
-keep class com.youth.banner.** { *; }

############################################
#CircleImageView
############################################
-keep class de.hdodenhof.circleimageview.** { *; }

############################################
# Baidu MTJ SDK
# 老 SDK 反射多，必须 keep
############################################
-keep class com.baidu.** { *; }

############################################
# 保留四大组件
# 避免混淆 Activity / Service / BroadcastReceiver / ContentProvider
############################################
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider

# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.android.org.conscrypt.TrustManagerImpl
-dontwarn com.baidu.helios.OnGetIdResultCallback
-dontwarn com.oracle.svm.core.annotate.Delete
-dontwarn com.oracle.svm.core.annotate.Substitute
-dontwarn com.oracle.svm.core.annotate.TargetClass
-dontwarn dalvik.system.BlockGuard$VmPolicy
-dontwarn dalvik.system.CloseGuard
-dontwarn java.lang.Module
-dontwarn libcore.net.http.Dns
-dontwarn libcore.net.http.HttpURLConnectionFactory
-dontwarn libcore.util.NativeAllocationRegistry
-dontwarn org.graalvm.nativeimage.hosted.Feature$BeforeAnalysisAccess
-dontwarn org.graalvm.nativeimage.hosted.Feature
-dontwarn org.graalvm.nativeimage.hosted.RuntimeResourceAccess


