# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Manish\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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


#-dontwarn android.net.**
#-keep class android.net.http.** { *; }
#-keepclassmembers class android.net.http.** {*;}

-assumenosideeffects class android.util.Log{
public static *** d(...);
        public static *** w(...);
        public static *** v(...);
        public static *** i(...);
}

#-dontwarn org.apache.commons.**
#-keep class org.apache.http.** { *; }
#-dontwarn org.apache.http.**

-dontskipnonpubliclibraryclassmembers
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

#-dontwarn com.root.skor.**
#-keep class com.root.skor.*{ *; }
#-keepclassmembers class com.root.skor.*{ *; }
#-keep interface com.root.skor.*{ *; }
#
#
#-dontwarn com.root.skorsg.**
#-keep class com.root.skorsg.*{ *; }
#-keepclassmembers class com.root.skorsg.*{ *; }
#-keep interface com.root.skorsg.*{ *; }
#
#-dontwarn com.root.skoruae.**
#-keep class com.root.skoruae.*{ *; }
#-keepclassmembers class com.root.skoruae.*{ *; }
#-keep interface com.root.skoruae.*{ *; }

-dontwarn com.root.unilever.ae.**
-keep class com.root.unilever.ae.*{ *; }
-keepclassmembers class com.root.unilever.ae.*{ *; }
-keep interface com.root.unilever.ae.*{ *; }

-dontwarn com.theartofdev.edmodo.cropper.**
-keep class com.theartofdev.edmodo.cropper.* { *; }
-keepclassmembers class com.theartofdev.edmodo.cropper.* { *; }
-keep interface com.theartofdev.edmodo.cropper.* { *; }

-dontwarn dev.dworks.libs.astickyheader.**
-keep class dev.dworks.libs.astickyheader.* { *; }
-keepclassmembers class dev.dworks.libs.astickyheader.* { *; }
-keep interface dev.dworks.libs.astickyheader.* { *; }

-dontwarn com.github.PhilJay:MPAndroidChart.**
-keep class com.github.PhilJay:MPAndroidChart.* { *; }
-keepclassmembers class com.github.PhilJay:MPAndroidChart.* { *; }
-keep interface com.github.PhilJay:MPAndroidChart.* { *; }

-dontwarn com.android.support:recyclerview.**
-keep class com.android.support:recyclerview.* { *; }
-keepclassmembers class com.android.support:recyclerview.* { *; }
-keep interface com.android.support:recyclerview.* { *; }

-dontwarn com.github.bumptech.glide:glide.**
-keep class com.github.bumptech.glide:glide.* { *; }
-keepclassmembers class com.github.bumptech.glide:glide.* { *; }
-keep interface com.github.bumptech.glide:glide.* { *; }

#-dontwarn com.github.siyamed.**
#-keep class com.github.siyamed.* { *; }
#-keepclassmembers class com.github.siyamed.* { *; }
#-keep interface com.github.siyamed.* { *; }

-dontwarn com.github.bumptech.glide.**
-keep class com.github.bumptech.glide.* { *; }
-keepclassmembers class com.github.bumptech.glide.* { *; }
-keep interface com.github.bumptech.glide.* { *; }

-dontwarn de.hdodenhof.circleimageview.**
-keep class de.hdodenhof.circleimageview.* { *; }
-keepclassmembers class de.hdodenhof.circleimageview.* { *; }
-keep interface de.hdodenhof.circleimageview.* { *; }

-dontwarn com.mcxiaoke.viewpagerindicator.**
-keep class com.mcxiaoke.viewpagerindicator.* { *; }
-keepclassmembers class com.mcxiaoke.viewpagerindicator.* { *; }
-keep interface com.mcxiaoke.viewpagerindicator.* { *; }

-dontwarn com.github.flavienlaurent.datetimepicker.**
-keep class com.github.flavienlaurent.datetimepicker.* { *; }
-keepclassmembers class com.github.flavienlaurent.datetimepicker.* { *; }
-keep interface com.github.flavienlaurent.datetimepicker.* { *; }

-keep class com.loopj.android.** { *; }
-keep interface com.loopj.android.** { *; }
-keep class dalvik.system.**{ *; }
-keep class libcore.**{ *; }

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}