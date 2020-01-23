package com.freesith.jsonview.bean

open class JsonElement<T> {

    var level = 0
    var line  = 0
    var value: T? = null
    var name: String? = null

    var hasChild = false
    var childStart = 0
    var childEnd = 0

    var overview: String? = null

}