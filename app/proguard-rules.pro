# Shradhanjali R8 rules.
# Compose, AndroidX, AdMob, UMP and kotlinx.serialization ship their own consumer rules.

# Keep @Serializable model classes (drafts are stored as JSON).
-keepclassmembers @kotlinx.serialization.Serializable class com.ereader.ripcardmaker.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepattributes *Annotation*, InnerClasses, Signature
