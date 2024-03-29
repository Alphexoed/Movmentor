package dev.alphexo.movmentor.utils

import org.json.JSONArray
import org.json.JSONObject


fun String.addCharAtIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()

fun extractResponse(
    statusCode: Int,
    response: String,
    result: (response: JSONArray) -> Unit
) {
    if (statusCode == 200 && response.startsWith("{")) {
        JSONObject(response).optJSONArray("response")?.let { result(it) }
    }
}

fun <T> JSONArray.customMap(transform: (JSONObject) -> T): List<T> {
    val resultList = mutableListOf<T>()
    for (i in 0 until length()) {
        val jsonObject = getJSONObject(i)
        resultList.add(transform(jsonObject))
    }
    return resultList
}

fun JSONArray.toJSONObjectList(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }