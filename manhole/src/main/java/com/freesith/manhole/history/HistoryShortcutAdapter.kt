package com.freesith.manhole.history

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import com.freesith.manhole.R
import com.freesith.manhole.ui.adapter.base.BaseAdapter
import com.freesith.manhole.ui.adapter.base.BaseViewHolder

class HistoryShortcutAdapter(context: Context?) : BaseAdapter<HttpHistory>(context) {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_history_shortcut
    }

    override fun bindView(holder: BaseViewHolder<HttpHistory>?, t: HttpHistory?, position: Int) {
        holder?.setText(R.id.tvHistory, t?.url)
        Log.d("xxx","bind position = " + position + "   code = " + t?.code)
        if (t?.code == null) {
            holder?.getView<TextView>(R.id.tvHistory)?.setBackgroundColor(Color.RED)
        } else {
            holder?.getView<TextView>(R.id.tvHistory)?.setBackgroundColor(Color.BLUE)
        }
    }
}