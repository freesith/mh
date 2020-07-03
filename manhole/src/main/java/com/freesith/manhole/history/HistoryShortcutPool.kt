package com.freesith.manhole.history

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object HistoryShortcutPool : CoroutineScope by MainScope(){

    var historyChangeListener: HistoryChangeListener? = null
        set(value) {
            field = value
            field?.onHistoryReplace(historyList)
        }

    val historyList = mutableListOf<HttpHistory>()

    fun newRequest(history: HttpHistory) {
        Log.d("xxx","new request")
        historyList.add(0, history)
        launch {
            historyChangeListener?.onNewRequest(history)
        }
    }

    fun requestFinish(history: HttpHistory) {
        Log.d("xxx", "requestFinish listener = " + historyChangeListener)
        launch {
            historyChangeListener?.onRequestFinish(history)
        }
    }

    interface HistoryChangeListener {
        fun onHistoryReplace(list: List<HttpHistory>)
        fun onNewRequest(history: HttpHistory)
        fun onRequestFinish(history: HttpHistory)
    }
}


