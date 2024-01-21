package dev.deeplink.httpdns.demo

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import dev.deeplink.httpdns.HttpDNS

class MyApplication : Application() {

    companion object {
        private const val TAG = "MyApplication"
    }

    override fun onCreate() {
        super.onCreate()
        // If your application is in multi-process mode, please initialize the SDK in the main process.
        if (baseContext.packageName.equals(packageName)) {

            // Initialize the SDK
            HttpDNS.init(this)

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
        }
    }
}