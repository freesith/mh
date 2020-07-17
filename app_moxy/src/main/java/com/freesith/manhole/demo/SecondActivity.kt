package com.freesith.manhole.demo

import android.app.Activity
import android.os.Bundle
import com.freesith.manhole.demo.Client.okHttpClient
import okhttp3.*
import java.io.IOException

class SecondActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        request1()
    }

    private fun request1() {
        val body = RequestBody.create(
            MediaType.get("application/json"),
            "a=1&b=2&json={'gaega':1,'gageagageag':'gaegeaehrhshsh'}&tt=ggpaehgpehigaigepgpeag"
        )
        val request = Request.Builder().url("http://www.baidu.com/")
            .post(body)
            .build()
        okHttpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val string = response.body()!!.string()
                }
            })
    }

}