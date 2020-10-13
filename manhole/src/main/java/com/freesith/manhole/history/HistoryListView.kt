package com.freesith.manhole.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.freesith.manhole.R
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.adapter.base.LoadMoreAdapterDecorator
import com.freesith.manhole.ui.interfaces.MonitorListener
import kotlinx.android.synthetic.main.layout_history.view.*
import kotlinx.coroutines.*

class HistoryListView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope(),
    BaseAdapter.OnItemClickListener<HttpHistory> {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    private val historyListAdapter: HistoryListAdapter = HistoryListAdapter(context!!)
    private val loadMoreAdapterDecorator = LoadMoreAdapterDecorator(context!!, historyListAdapter) {
        onLoadMore()
    }
    var monitorListener: MonitorListener? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_history, this)
        manhole_rvHistory.layoutManager = LinearLayoutManager(context)
        manhole_rvHistory.adapter = loadMoreAdapterDecorator
        historyListAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(t: HttpHistory, position: Int) {
        t.id?.let {
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
            val list = async.await()
            historyListAdapter.mList.clear()
            loadMoreAdapterDecorator.loadSuccess(list.size == 20)
            historyListAdapter.mList.addAll(list)
            loadMoreAdapterDecorator.notifyDataSetChanged()
        }
    }

    private fun onLoadMore() {
        launch {
            val size = historyListAdapter.mList.size
            val async = async (Dispatchers.IO){
                ManholeHistory.readMoreHistory(historyListAdapter.mList[size - 1].id!!, 20)
            }
            val list = async.await()
            loadMoreAdapterDecorator.loadSuccess(list.size == 20)
            if (list.isNotEmpty()) {
                historyListAdapter.mList.addAll(list)
                loadMoreAdapterDecorator.notifyItemInserted(size)
            }
        }
    }

}