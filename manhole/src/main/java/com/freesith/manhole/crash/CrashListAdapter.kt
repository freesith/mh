package com.freesith.manhole.history

import android.content.Context
import com.freesith.manhole.R
import com.freesith.manhole.crash.CrashInfo
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

class CrashListAdapter(context: Context) : BaseAdapter<CrashInfo>(context) {

    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    override fun getLayoutId(viewType: Int): Int = R.layout.item_crash_list

    override fun bindView(holder: BaseViewHolder<CrashInfo>?, t: CrashInfo, position: Int) {
        holder?.setText(R.id.manhole_tvName, t?.name)
        t?.time?.let {
            holder?.setText(R.id.manhole_tvTime, timeFormat.format(Date(it)))
        }
    }
}