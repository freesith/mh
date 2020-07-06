package com.freesith.manhole.history

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.freesith.manhole.R
import kotlinx.android.synthetic.main.layout_history_detail.view.*
import kotlinx.android.synthetic.main.layout_history_header.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryDetailView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope() {

    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    val headerView = LayoutInflater.from(context).inflate(R.layout.layout_history_header, null)

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_history_detail, this)
        findViewById<View>(R.id.tvClose).setOnClickListener { (parent as ViewGroup).removeView(this@HistoryDetailView) }
        setBackgroundColor(Color.WHITE)
    }

    fun showHistory(historyId: Int) {
        launch {
            async(Dispatchers.IO) { ManholeHistory.getHistoryById(historyId) }.await()?.let {
                headerView.tv_url.text = it.url
                headerView.tv_method.text = it.method
                headerView.tv_code.text = it.code.toString()
                headerView.tv_time.text = timeFormat.format(Date(it.time))
                if (!it.requestBody.isNullOrEmpty() && it.requestBody!!.startsWith("{")) {
                    headerView.v_request_json.setJson(it.requestBody)
                }
                v_response_json.setHeaderView(headerView)
                if (!it.responseBody.isNullOrEmpty() && it.responseBody!!.startsWith("{")) {
                    v_response_json.setJson(it.responseBody)
                }
            }
        }
    }
}