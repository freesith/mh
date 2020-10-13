package com.freesith.manhole.ui

import android.content.Context
import android.os.Build
import android.os.FileUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.freesith.manhole.ManholeMock
import com.freesith.manhole.ManholeConstants
import com.freesith.manhole.R
import com.freesith.manhole.ext.default
import com.freesith.manhole.history.ManholeHistory
import com.freesith.manhole.util.ManholeSp
import kotlinx.android.synthetic.main.layout_setting.view.*
import okhttp3.*
import java.io.*

class SettingView : LinearLayout, View.OnClickListener {

    //TODO 2019-11-21 by WangChao
    private val lastPath = ""
    private var thisContext: Context? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        this.thisContext = context
        val view = LayoutInflater.from(context).inflate(R.layout.layout_setting, this)
        manhole_btnEdit?.setOnClickListener(this)
        manhole_btnRefresh?.setOnClickListener(this)
        manhole_etPath?.setText(ManholeSp.dbPath.default("http://apk.minim.red/moc.db"))
        manhole_btnEdit?.setVisibility(View.GONE)

        manhole_swHistory.isChecked = ManholeSp.enableHistory
        manhole_swHistory.setOnCheckedChangeListener { buttonView, isChecked ->
            ManholeSp.enableHistory = isChecked
        }
        manhole_swSummon.isChecked = ManholeSp.enableSummon
        manhole_swSummon.setOnCheckedChangeListener { buttonView, isChecked ->
            ManholeSp.enableSummon = isChecked
        }
        manhole_swShortcut.isChecked = ManholeSp.enableHistoryShortcut
        manhole_swShortcut.setOnCheckedChangeListener { buttonView, isChecked ->
            ManholeSp.enableHistoryShortcut = isChecked
        }
        manhole_btnClear?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.manhole_btnEdit -> {
                if (manhole_etPath!!.isEnabled) {
                    val trim = manhole_etPath!!.text.toString().trim { it <= ' ' }
                    if (lastPath != trim) {
                        //TODO 2019-11-21 by WangChao 保存
                        refresh()
                    }
                    manhole_etPath!!.isEnabled = false
                    manhole_btnEdit!!.text = "编辑"
                } else {
                    manhole_etPath!!.isEnabled = true
                    manhole_etPath!!.setSelection(manhole_etPath!!.text.length)
                    manhole_btnEdit!!.text = "保存"
                }
            }
            R.id.manhole_btnRefresh -> {
                refresh()
            }

            R.id.manhole_btnClear -> {
                ManholeHistory.clearDb()
                Toast.makeText(thisContext, "已清空", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refresh() {
        val trim = manhole_etPath!!.text.toString().trim { it <= ' ' }
        if (trim.startsWith("http")) {
            downloadDbFile(trim)
            ManholeSp.dbPath = trim
        } else if (trim.startsWith("/")) {
            copyDBFile(trim)
            ManholeMock.initMockDb(
                thisContext,
                thisContext!!.filesDir
                    .absolutePath + File.separator + ManholeConstants.MOCK_DB_NAME
            )
            ManholeSp.dbPath = trim
        }
    }

    private fun downloadDbFile(url: String) {
        val request =
            Request.Builder().url(url).header("Cache-Control", "no-cache").get().build()
        val client = OkHttpClient.Builder().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                post(Runnable {
                    Toast.makeText(
                        thisContext,
                        "fail:" + e.javaClass.simpleName,
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }

            @Throws(IOException::class)
            override fun onResponse(
                call: Call,
                response: Response
            ) {
                if (response != null) {
                    val code = response.code()
                    if (code != 200 || response.body() == null) {
                        return
                    }
                    val inputStream = response.body()!!.byteStream()
                    val dbFile =
                        File(thisContext!!.filesDir, ManholeConstants.MOCK_DB_NAME)
                    if (!dbFile.exists()) {
                        try {
                            dbFile.createNewFile()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    var outputStream: OutputStream? = null
                    try {
                        outputStream = FileOutputStream(dbFile)
                        val buffer = ByteArray(4096)
                        var len = -1
                        while ((inputStream!!.read(buffer).also { len = it }) > 0) {
                            outputStream.write(buffer, 0, len)
                        }
                        ManholeMock.initMockDb(
                            thisContext,
                            thisContext!!.filesDir
                                .absolutePath + File.separator + ManholeConstants.MOCK_DB_NAME
                        )
                        post(object : Runnable {
                            override fun run() {
                                Toast.makeText(thisContext, "complete", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close()
                            } catch (e: IOException) {
                            }
                        }
                        if (outputStream != null) {
                            try {
                                outputStream.close()
                            } catch (e: IOException) {
                            }
                        }
                    }
                }
            }
        })
    }

    private fun copyDBFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            val dbFile = File(thisContext!!.filesDir, ManholeConstants.MOCK_DB_NAME)
            if (!dbFile.exists()) {
                try {
                    dbFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                inputStream = FileInputStream(file)
                outputStream = FileOutputStream(dbFile)
                FileUtils.copy(inputStream, outputStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                    }
                }
            }
        }
    }
}