# HexGlyph ProGuard rules

# Keep Hilt entry points
-keep class * extends dagger.hilt.android.internal.managers.ApplicationComponentManager { *; }

# Keep Room entities
-keep class com.hexglyph.a1.data.local.entity.** { *; }

# Keep data classes used in serialization
-keepclassmembers class com.hexglyph.a1.** {
    public <init>(...);
}

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
