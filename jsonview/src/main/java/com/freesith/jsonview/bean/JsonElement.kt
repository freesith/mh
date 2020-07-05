package com.freesith.jsonview.bean

import android.graphics.Color

open class JsonElement<T> {

    companion object {
        val COLOR_STRING = Color.parseColor("#b38e4b")
        val COLOR_NUMBER = Color.parseColor("#41acf7")
        val COLOR_BOOLEAN = Color.parseColor("#f05482")
        val COLOR_HINT = Color.parseColor("#e9e3e9")
    }

    var level = 0
    var line = 0
    var value: T? = null
    var name: String? = null

    var hasChild = false
    var childStart = 0
    var childEnd = 0

    var valueColor: Int = Color.BLACK
    var overview: String? = null

}