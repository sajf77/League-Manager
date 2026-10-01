-dontwarn javax.annotation.**

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes EnclosingMethod

# Keep all data classes used with Gson/Room
-keep class com.sajad.leaguemanager.data.** { *; }

# Keep ViewModel
-keep class com.sajad.leaguemanager.viewmodel.** { *; }

# Gson rules
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Kotlin Pair - R8 renames first/second fields breaking Gson deserialization
-keepclassmembers class kotlin.Pair {
    java.lang.Object first;
    java.lang.Object second;
}

# Keep Gson-serialized collection types (field names matter for deserialization)
-keepclassmembers class kotlin.collections.** { *; }
-keepclassmembers class java.util.LinkedHashMap { *; }
-keepclassmembers class java.util.HashMap { *; }

# Keep Room TypeConverters
-keep class com.sajad.leaguemanager.data.Converters { *; }
