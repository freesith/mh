package com.freesith.manhole.history

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.freesith.manhole.R
import kotlinx.android.synthetic.main.manhole_layout_history_detail.view.*
import kotlinx.android.synthetic.main.manhole_layout_history_header.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryDetailView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope(),
    View.OnClickListener {

    val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS")

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    val headerView = LayoutInflater.from(context).inflate(R.layout.manhole_layout_history_header, null)
    var currentHistory: HttpHistory? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.manhole_layout_history_detail, this)
        findViewById<View>(R.id.manhole_tvClose).setOnClickListener { (parent as ViewGroup).removeView(this@HistoryDetailView) }
        setBackgroundColor(Color.WHITE)
        headerView.manhole_tvRequestBodyStart.setOnClickListener(this)
        headerView.manhole_tvResponseBodyStart.setOnClickListener(this)
    }

    fun showHistory(historyId: Int) {
        launch {
            withContext(Dispatchers.IO) { ManholeHistory.getHistoryById(historyId) }?.let {
                currentHistory = it
                launch (Dispatchers.Main){
                    headerView.manhole_tv_url.text = it.url
                    headerView.manhole_tv_method.text = it.method
                    headerView.manhole_tv_code.text = it.code.toString()
                    headerView.manhole_tv_time.text = timeFormat.format(Date(it.time))
                    if (!it.requestBody.isNullOrEmpty() && it.requestBody!!.startsWith("{")) {
                        headerView.manhole_v_request_json.setJson(it.requestBody)
                    }
                    manhole_v_response_json.setHeaderView(headerView)
                    if (!it.responseBody.isNullOrEmpty() && it.responseBody!!.startsWith("{")) {
                        manhole_v_response_json.setJson(it.responseBody)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.manhole_tvRequestBodyStart -> {
                currentHistory?.requestBody?.let {
                    copy(it)
                }
            }

            R.id.manhole_tvResponseBodyStart -> {
                currentHistory?.responseBody?.let {
                    copy(it)
                }
            }
        }
    }

    private fun copy(content: String) {
        //获取剪贴板管理器：

        //获取剪贴板管理器：
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", content)
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
    }
}