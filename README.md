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
implementation 'dev.deeplink:httpdns:3.0.0'
```

Step 2: Configure AndroidManifest

Find the project configuration file AndroidManifest.xml in your project, and add the following permissions:

```kotlin
<uses-permission android:name="android.permission.INTERNET" />
```

Step 3: Initialize the SDK
If your application is in multi-process mode, please initialize the SDK in the main process. Here is the reference code:
```kotlin
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
          val config = HttpDNSConfig.Builder().setAccountId("ACCOUNT_ID")
            .setEnableExpiredIP(true)
            .setRefreshAfterNetworkChanged(true)
            .build()
        val httpDns = DLinkHttpDNS(this, config)
    }
}
```

Step 4: Request the IP corresponding to the domain name
```kotlin
 val hostList = mutableListOf("HOST")
// Set the domain name that needs to be pre-resolved.
httpDns.setPreResolveHosts(hostList)

// This method first queries the cache.
// If there is an available resolution result in the cache, the resolution result is immediately returned through the callback.
// If there is no available resolution result in the cache, the domain name resolution will be performed in the worker thread,
// and the resolution result will be returned through the callback after the domain name resolution is completed or the timeout period is reached.
httpDns.getDNSInfoAsync(hostList[0], object : DNSInfoCallback {

    override fun onSuccess(info: DNSInfo) {
        Log.i(TAG, "getDNSInfoAsync host:${hostList[0]} ipv4:${info.ipv4s} ipv6:${info.ipv6s}")
    }

    override fun onFailed(errMsg: String) {
        Log.i(TAG, "getDNSInfoAsync failed, $errMsg")
    }
})

Handler(Looper.myLooper()!!).postDelayed({
    // This method only queries the cache and returns the resolution result in the cache, which may return an empty result.
    // If there is no resolution result in the cache or the resolution result in the cache has expired,
    // the domain name resolution will be performed in the worker thread.
    // After the resolution is successful, the cache will be updated for the next call to domain name resolution.
    val info = httpDns.getDNSInfoFromCache(hostList[0])
    Log.i(TAG, "getDNSInfoFromCache host:${hostList[0]} info:$info")
}, 4000)

Thread {
    // This method first queries the cache.
    // If there is an available resolution result in the cache, the resolution result is returned immediately.
    // If there is no available resolution result in the cache, the thread currently calling the resolution will be blocked and the domain name resolution will be performed in the thread.
    // The resolution result will be returned after the domain name resolution is completed, or an empty result will be returned after the timeout period is reached.
    val info = httpDns.getDNSInfoSync(hostList[0])
    Log.i(TAG, "getDNSInfoSync host:${hostList[0]} info:$info")
}.start()
```
