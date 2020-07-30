package com.freesith.manhole.history

import android.util.Log
import kotlinx.coroutines.*
import java.lang.IndexOutOfBoundsException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object HistoryShortcutPool : CoroutineScope by MainScope() {

    var historyChangeListener: HistoryChangeListener? = null
        set(value) {
            field = value
            field?.onHistoryReplace(historyList)
        }

    val historyList = Collections.synchronizedList(mutableListOf<HttpHistory>())
    var size = AtomicInteger(0)

    fun newRequest(history: HttpHistory) {
        Log.d("xxx", "new request")
        historyList.add(0, history)
        val incrementAndGet = size.incrementAndGet()
        Log.d("xxx", "newSize = " + incrementAndGet)
        launch {
            historyChangeListener?.onNewRequest(history)
        }
        launch(Dispatchers.IO) {
            delay(5000)
            val curSize = size.get()
            Log.d("xxx","curSize = " + curSize)
            if (curSize > 0) {
                val decrementAndGet = size.decrementAndGet()
                try {
                    historyList.removeAt(decrementAndGet)
                    launch(Dispatchers.Main) {
                        historyChangeListener?.onHistoryRemove(decrementAndGet)
                    }
                } catch (e : IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun requestFinish(history: HttpHistory) {
        Log.d("xxx", "requestFinish listener = " + historyChangeListener)
        launch {
            historyChangeListener?.onRequestFinish(history, historyList.indexOf(history))
        }
    }

    interface HistoryChangeListener {
        fun onHistoryReplace(list: List<HttpHistory>)
        fun onNewRequest(history: HttpHistory)
        fun onRequestFinish(history: HttpHistory, position: Int)
        fun onHistoryRemove(position: Int)
    }
}


