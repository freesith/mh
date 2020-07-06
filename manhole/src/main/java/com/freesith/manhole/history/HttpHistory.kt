package com.freesith.manhole.history

class HttpHistory {
    var id: Int? = null
    var url: String? = null
    var method: String? = null
    var requestBody: String? = null
    var code: Int? = null
    var responseBody: String? = null
    var mock: Boolean = false
    var time: Long = 0
}