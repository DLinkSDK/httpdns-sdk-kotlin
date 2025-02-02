# httpdns-sdk-kotlin

Step 1: Get the SDK

(1) Configure the Maven repository
```kotlin   
repositories {
   maven { url 'https://maven.deeplink.dev/repository/maven-releases/' }
}
```

Note: The Maven repository address needs to be configured in both 'buildscript' and 'allprojects' in the root directory's 'build.gradle'.

(2) If you are using Gradle for integration, add the following code to your project's build.gradle:
```kotlin
implementation 'dev.deeplink:httpdns:2.0.6'
```

Step 3: Configure AndroidManifest

Find the project configuration file AndroidManifest.xml in your project, and add the following permissions:

```kotlin
<uses-permission android:name="android.permission.INTERNET" />
```

If you need to add obfuscation during packaging, please add the following code to the obfuscation configuration file:
```kotlin
-keep class dev.deeplink.httpdns.bean.**{*;}
```

Step 4: Initialize the SDK
If your application is in multi-process mode, please initialize the SDK in the main process. Here is the reference code:
```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // If your application is in multi-process mode, please initialize the SDK in the main process.
        if (baseContext.packageName.equals(packageName)) {

            // Initialize the SDK
            HttpDNS.init(this)
        }
    }
}
```

Step 5: Request the IP corresponding to the domain name
```kotlin
// Asynchronously obtain the IP address of the specified domain name list
val asyncDomainList =
    mutableListOf("asyncDomain1.com", "asyncDomain2.com")
HttpDNS.refreshAsync(asyncDomainList)

Handler(Looper.myLooper()!!).postDelayed({
    //If the request is successful, the IP address for the domain name can be obtained from the cache.
    val ipList = HttpDNS.getIpListForDomain("asyncDomain1.com")
    Log.i(TAG, "IP list of \"syncDomain1.com\"-> $ipList")
}, 5000)

Thread {
    // Synchronously obtain the IP address of the specified domain name list
    val syncDomainList =
        mutableListOf("asyncDomain3.com", "asyncDomain4.com")
    val result = HttpDNS.refreshSync(syncDomainList)
    if (result.isSuccess) {
        //If the request is successful, the IP address for the domain name can be obtained from the cache.
        val ipList = HttpDNS.getIpListForDomain("asyncDomain3.com")
        Log.i(TAG, "IP list of \"syncDomain3.com\"-> $ipList")
    } else {
        Log.i(TAG, "IP list of \"syncDomain3.com\" error -> ${result.exceptionOrNull()}")
    }
}.start()
```
