package org.bspb.smartbirds.pro.utils

import android.os.Looper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@DelicateCoroutinesApi
class SBGlobalScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext

    fun sbLaunch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {

        if (context is EmptyCoroutineContext) {
            val isMainThread = Looper.myLooper() == Looper.getMainLooper()
            return if (isMainThread) {
                debugLog("Launch coroutine in main thread")
                sbLaunchMain(start, block)
            } else {
                debugLog("Launch coroutine in IO thread")
                sbLaunchIO(start, block)
            }
        }

        debugLog("Launch coroutine with argument context")
        return launch(context, start, block)
    }

    private fun sbLaunchMain(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(Dispatchers.Main, start, block)
    }

    private fun sbLaunchIO(
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(Dispatchers.IO, start, block)
    }

}