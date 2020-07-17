package com.freesith.manhole.history

import android.content.Context
import com.freesith.manhole.R
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class HistoryListAdapter(context: Context) : BaseAdapter<HttpHistory>(context) {

    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    override fun getLayoutId(viewType: Int): Int = R.layout.item_history_list

    override fun bindView(holder: BaseViewHolder<HttpHistory>?, t: HttpHistory, position: Int) {
        holder?.setText(R.id.tv_url, t?.url)
        holder?.setText(R.id.tv_method, t?.method)
        holder?.setText(R.id.tv_code, t?.code?.toString())
        t?.time?.let {
            holder?.setText(R.id.tv_time, timeFormat.format(Date(it)))
        }
    }
}