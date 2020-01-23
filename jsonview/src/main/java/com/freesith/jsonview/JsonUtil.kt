package com.freesith.jsonview

import com.freesith.jsonview.bean.JsonElement
import org.json.JSONArray
import org.json.JSONObject


fun parseJson(json: String?): MutableList<JsonElement<*>>? {
    if (json == null) {
        return null
    }

    val jsonElements = mutableListOf<JsonElement<*>>()
    val jsonObject = JSONObject(json) ?: return null

    val keys = jsonObject.keys()

    for (key in keys) {
        val value = jsonObject.get(key)
        parseValue(jsonElements, 0, key, value)
    }

    return jsonElements
}

fun parseValue(jsonElements: MutableList<JsonElement<*>>, level: Int, name: String, value: Any?) {
    when (value) {
        is JSONObject -> {
            parseJsonObject(jsonElements, level, name, value)
        }
        is JSONArray -> {
            parseJsonArray(jsonElements, level, name, value)
        }
        is String -> {
            parseString(jsonElements, level, name, value)
        }
        is Number -> {
            parseNumber(jsonElements, level, name, value)
        }

        else -> {
            parseOthers(jsonElements, level, name, value)
        }
    }
}

fun parseJsonObject(
    jsonElements: MutableList<JsonElement<*>>,
    level: Int,
    name: String,
    jsonObject: JSONObject
) {
    val jsonElement = JsonElement<String>()
    jsonElement.name = name
    jsonElement.line = jsonElements.size
    jsonElement.level = level
    jsonElement.value = "{}"
    jsonElements.add(jsonElement)

    val keys = jsonObject.keys()
    if (keys.hasNext()) {
        jsonElement.hasChild = true
        jsonElement.childStart = jsonElements.size
        var count = 0
        for (key in keys) {
            val value = jsonObject.get(key)
            parseValue(jsonElements, level + 1, key, value)
            count++
        }
        jsonElement.value = "{${count}}"
        jsonElement.childEnd = jsonElements.size
    }
}

fun parseJsonArray(
    jsonElements: MutableList<JsonElement<*>>,
    level: Int,
    name: String,
    jsonArray: JSONArray
) {
    val jsonElement = JsonElement<String>()
    jsonElement.name = name
    jsonElement.value = "[]"
    jsonElement.line = jsonElements.size
    jsonElement.level = level
    jsonElements.add(jsonElement)


    val length = jsonArray.length()
    if (length > 0) {
        jsonElement.value = "[${length}]"
        jsonElement.hasChild = true
        jsonElement.childStart = jsonElements.size
        (0 until length).forEach {
            val value = jsonArray.get(it)
            parseValue(jsonElements, level + 1, "${name}[${it}]", value)
        }
        jsonElement.childEnd = jsonElements.size
    }
}

fun parseString(
    jsonElements: MutableList<JsonElement<*>>,
    level: Int,
    name: String,
    value: String
) {
    val jsonElement = JsonElement<String>()
    jsonElement.name = name
    jsonElement.value = "\"${value}\""
    jsonElement.line = jsonElements.size
    jsonElement.level = level
    jsonElements.add(jsonElement)
}

fun parseNumber(
    jsonElements: MutableList<JsonElement<*>>,
    level: Int,
    name: String,
    value: Number
) {
    val jsonElement = JsonElement<Number>()
    jsonElement.name = name
    jsonElement.value = value
    jsonElement.line = jsonElements.size
    jsonElement.level = level
    jsonElements.add(jsonElement)
}

fun parseOthers(jsonElements: MutableList<JsonElement<*>>, level: Int, name: String, value: Any?) {
    val jsonElement = JsonElement<String>()
    jsonElement.name = name
    jsonElement.value = value.toString()
    jsonElement.line = jsonElements.size
    jsonElement.level = level
    jsonElements.add(jsonElement)
}



