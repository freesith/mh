package com.freesith.manhole.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.freesith.manhole.R
import com.freesith.manhole.crash.CrashInfo
import com.freesith.manhole.crash.ManholeCrash
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.adapter.base.LoadMoreAdapterDecorator
import com.freesith.manhole.ui.interfaces.MonitorListener
import kotlinx.android.synthetic.main.manhole_layout_crash.view.*
import kotlinx.coroutines.*

class CrashListView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope(),
    BaseAdapter.OnItemClickListener<CrashInfo> {

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    private val crashListAdapter: CrashListAdapter = CrashListAdapter(context!!)
    private val loadMoreAdapterDecorator = LoadMoreAdapterDecorator(context!!, crashListAdapter) {
        onLoadMore()
    }
    var monitorListener: MonitorListener? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.manhole_layout_crash, this)
        manhole_rvCrash.layoutManager = LinearLayoutManager(context)
        manhole_rvCrash.adapter = loadMoreAdapterDecorator
        crashListAdapter.setOnItemClickListener(this)
    }

    override fun onItemClick(t: CrashInfo, position: Int) {
        t.id?.let {
            monitorListener?.onShowCrashDetail(it)
        }
    }

    fun show() {
        loadCrash()
    }

    private fun loadCrash() {
        launch {
            val async = async(Dispatchers.IO) {
                ManholeCrash.readSimpleExceptionDown(20)
            }
            val list = async.await()
            crashListAdapter.mList.clear()
            loadMoreAdapterDecorator.loadSuccess(list.size == 20)
            crashListAdapter.mList.addAll(list)
            loadMoreAdapterDecorator.notifyDataSetChanged()
        }
    }

    private fun onLoadMore() {
        launch {
            val size = crashListAdapter.mList.size
            val async = async (Dispatchers.IO){
                ManholeCrash.readMoreException(crashListAdapter.mList[size - 1].id!!, 20)
            }
            val list = async.await()
            loadMoreAdapterDecorator.loadSuccess(list.size == 20)
            if (list.isNotEmpty()) {
                crashListAdapter.mList.addAll(list)
                loadMoreAdapterDecorator.notifyItemInserted(size)
            }
        }
    }

}