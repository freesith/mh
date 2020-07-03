package com.freesith.manhole.core

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private val sharedPreferences: SharedPreferences by lazy {
    ManholeContext.context.getSharedPreferences(
        "manhole_sp", Context.MODE_PRIVATE
    )
}

interface SpAccessor {
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
}

fun SpAccessor.spBoolean(): ReadWriteProperty<SpAccessor, Boolean> = SpBoolean
fun SpAccessor.spInt(): ReadWriteProperty<SpAccessor, Int> = SpInt
fun SpAccessor.spString(): ReadWriteProperty<SpAccessor, String> = SpString

object SpInt : ReadWriteProperty<SpAccessor, Int> {

    override fun getValue(thisRef: SpAccessor, property: KProperty<*>): Int {
        return sharedPreferences.getInt(property.name, 0)
    }

    override fun setValue(thisRef: SpAccessor, property: KProperty<*>, value: Int) {
        sharedPreferences.edit().putInt(property.name, value).apply()
    }
}

object SpBoolean : ReadWriteProperty<SpAccessor, Boolean> {

    override fun getValue(thisRef: SpAccessor, property: KProperty<*>): Boolean {
        return sharedPreferences.getBoolean(property.name, false)
    }

    override fun setValue(thisRef: SpAccessor, property: KProperty<*>, value: Boolean) {
        sharedPreferences.edit().putBoolean(property.name, value).apply()
    }
}

object SpString : ReadWriteProperty<SpAccessor, String> {
    override fun getValue(thisRef: SpAccessor, property: KProperty<*>): String {
        return sharedPreferences.getString(property.name, "") ?: ""
    }

    override fun setValue(thisRef: SpAccessor, property: KProperty<*>, value: String) {
        sharedPreferences.edit().putString(property.name, value).apply()
    }
}
