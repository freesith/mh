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
import com.freesith.manhole.crash.CrashInfo
import com.freesith.manhole.crash.ManholeCrash
import kotlinx.android.synthetic.main.layout_crash_detail.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class CrashDetailView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr), CoroutineScope by MainScope(),
    View.OnClickListener {

    val timeFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS")

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    var crashInfo: CrashInfo? = null
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_crash_detail, this)
        findViewById<View>(R.id.tvClose).setOnClickListener { (parent as ViewGroup).removeView(this@CrashDetailView) }
        setBackgroundColor(Color.WHITE)
        tvDesc.setOnClickListener(this)
        tvClose.setOnClickListener(this)
    }

    fun showCrash(crashId: Int) {
        launch {
            withContext(Dispatchers.IO) { ManholeCrash.getExceptionById(crashId) }?.let {
                crashInfo = it
                launch (Dispatchers.Main){
                    tvName.text = it.name
                    tvDesc.text = it.desc
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tvDesc -> {
                crashInfo?.desc?.let {
                    copy(it)
                }
            }

            R.id.tvClose -> {
                (parent as ViewGroup).removeView(this@CrashDetailView)
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