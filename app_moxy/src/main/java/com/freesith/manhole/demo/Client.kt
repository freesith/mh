package com.freesith.manhole.demo

import com.freesith.manhole.MockInterceptor
import okhttp3.OkHttpClient

object Client {
    val okHttpClient = OkHttpClient.Builder().addInterceptor(MockInterceptor()).build()
}