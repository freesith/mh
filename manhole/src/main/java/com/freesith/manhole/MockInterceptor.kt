package com.freesith.manhole

import com.freesith.manhole.ManholeMock.log
import com.freesith.manhole.history.HistoryShortcutPool.newRequest
import com.freesith.manhole.history.HistoryShortcutPool.requestFinish
import com.freesith.manhole.history.HttpHistory
import com.freesith.manhole.history.ManholeHistory.recordHistory
import com.freesith.manhole.util.ManholeSp.enableHistory
import com.freesith.manhole.util.ManholeSp.enableHistoryShortcut
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MockInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var history: HttpHistory? = null
        if (enableHistoryShortcut) {
            history = HttpHistory()
            history.url = request.url().toString()
            newRequest(history)
        }
        val mockResonse = ManholeMock.mock(request)
        return if (mockResonse == null) {
            val proceed = chain.proceed(request)
            log(request, proceed)
            if (enableHistory) {
                try {
                    recordHistory(false, request, proceed)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (history != null) {
                history.code = proceed.code()
                requestFinish(history)
            }
            proceed
        } else {
            if (mockResonse.cover && mockResonse.covers != null && !mockResonse.covers.isEmpty()) {
                val proceed = chain.proceed(request)
                val code = proceed.code()
                if (code == 200 && proceed.body() != null) {
                    val mediaType = proceed.body()!!.contentType()
                    val string = proceed.body()!!.string()
                    //TODO 2019-11-12 by WangChao 改变其中某些字段
                    val builder =
                        Response.Builder().code(code).message(proceed.message())
                            .request(proceed.request())
                            .headers(proceed.headers())
                    try {
                        val jsonObject = JSONObject(string)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        builder.body(ResponseBody.create(mediaType, string))
                    }
                    builder.build()
                } else {
                    proceed
                }
            } else {
                val mockBody =
                    ResponseBody.create(MediaType.get("application/json"), mockResonse.data)
                val response =
                    Response.Builder().protocol(Protocol.HTTP_1_1).code(200)
                        .message("Success").request(request).body(mockBody).build()
                if (enableHistory) {
                    try {
                        recordHistory(true, request, response)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (history != null) {
                    history.code = response.code()
                    history.mock = true
                    requestFinish(history)
                }
                response
            }
        }
    }




    private fun needMock(request: Request): Boolean {
        val method = request.method()
        val url = request.url()
        val host = url.host()
        val path = url.encodedPath()
        val query = url.query()
        return false
    }

    companion object {
        //value : Number, String
        //user? . photos?[0].url? ="http......"
        //list? . [0]? . [0]? = "xxx"
        //list[*]?.hell=xxx
        //list.[*].
        //
        const val TYPE_NUMBER = 1
        const val TYPE_BOOLEAN = 2
        const val TYPE_STRING = 3
        const val TYPE_OBJECT = 4
        const val TYPE_ARRAY = 5
    }
}