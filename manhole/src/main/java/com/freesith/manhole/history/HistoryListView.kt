package com.freesith.manhole.history

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.freesith.manhole.R
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.interfaces.MonitorListener
import kotlinx.android.synthetic.main.layout_history.view.*
import kotlinx.coroutines.*

class HistoryListView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope(),
    BaseAdapter.OnItemClickListener<HttpHistory> {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    private val historyListAdapter: HistoryListAdapter = HistoryListAdapter(context)
    var monitorListener: MonitorListener? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_history, this)
        rvHistory.layoutManager = LinearLayoutManager(context)
        rvHistory.adapter = historyListAdapter
        historyListAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(t: HttpHistory?, position: Int) {
        t?.id?.let {
            monitorListener?.onShowHistoryDetail(it)
        }
    }

    fun show() {
        loadHistory()
    }

    private fun loadHistory() {
        launch {
            val async = async(Dispatchers.IO) {
                ManholeHistory.readSimpleHistoryDown(20)
            }
            historyListAdapter.list = async.await()
            historyListAdapter.notifyDataSetChanged()
        }
    }


}