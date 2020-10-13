package com.freesith.manhole.history

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import com.freesith.manhole.R
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.adapter.base.BaseViewHolder

class HistoryShortcutAdapter(context: Context) : BaseAdapter<HttpHistory>(context) {

    companion object {
        const val COLOR_LOADING: Int = 0xff2a88ab.toInt()
        const val COLOR_FINISH: Int = 0xffb36e4b.toInt()
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_history_shortcut
    }

    override fun bindView(holder: BaseViewHolder<HttpHistory>?, t: HttpHistory, position: Int) {
        holder?.setText(R.id.manhole_tvHistory, t.url)
        Log.d("xxx", "bind position = " + position + "   code = " + t.code)
        if (t.code == null) {
            holder?.getView<TextView>(R.id.manhole_tvHistory)?.setBackgroundColor(COLOR_LOADING)
        } else {
            holder?.getView<TextView>(R.id.manhole_tvHistory)?.setBackgroundColor(COLOR_FINISH)
        }
    }
}