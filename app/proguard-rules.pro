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

-keepattributes *Annotation*,InnerClasses
-keepattributes KotlinMetadata
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations

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
#-keepclasseswithmembernames class * {
#    native <methods>;
#}


# 保留Parcelable序列化类不被混淆
#-keep class * implements android.os.Parcelable {
#    public static final android.os.Parcelable$Creator *;
#}

# 保留Serializable序列化的类不被混淆
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    !static !transient <fields>;
#    !private <fields>;
#    !private <methods>;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}

-keep public class com.htc.horizonos.entry.**{*;}

# Gson 核心
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

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

# 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.media.AudioAttributes

# 所有 Fragment 不混淆
-keep class * extends androidx.fragment.app.Fragment

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


