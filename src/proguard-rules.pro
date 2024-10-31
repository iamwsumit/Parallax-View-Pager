# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.sumti1334.parallaxviewpager.ParallaxPager {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/sumti1334/parallaxpager/repack'
-flattenpackagehierarchy
-dontpreverify
