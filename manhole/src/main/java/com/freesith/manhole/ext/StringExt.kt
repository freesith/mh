package com.freesith.manhole.ext

fun String?.default(default: String): String {
    if (this.isNullOrEmpty()) {
        return default
    }
    return this
}