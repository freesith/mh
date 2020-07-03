package com.freesith.manhole.history

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HistoryShortcut(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope(),
    HistoryShortcutPool.HistoryChangeListener {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val historyAdapter: HistoryShortcutAdapter
    private val recyclerView: RecyclerView = RecyclerView(context)

    init {
        addView(
            recyclerView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        historyAdapter = HistoryShortcutAdapter(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = null
        recyclerView.adapter = historyAdapter
    }

    fun onStart() {
        HistoryShortcutPool.historyChangeListener = this
    }

    fun onStop() {

    }

    override fun onHistoryReplace(list: List<HttpHistory>) {
        launch(Dispatchers.IO) {
            historyAdapter.clear()
            historyAdapter.addList(list)
            launch(Dispatchers.Main) {
                historyAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onNewRequest(history: HttpHistory) {
        historyAdapter.add(history, 0)
        historyAdapter.notifyItemInserted(0)
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
    }

    override fun onRequestFinish(history: HttpHistory) {
        Log.d("xxx","receive finish")
        launch(Dispatchers.IO) {
            historyAdapter.list.forEachIndexed { index, httpHistory ->
                if (history.hashCode() == httpHistory.hashCode()) {
                    Log.d("xxx","find change request code = " + history.code)
                    launch(Dispatchers.Main) {
                        historyAdapter.notifyItemChanged(index)
                    }
                    return@forEachIndexed
                }
            }
        }
    }
}