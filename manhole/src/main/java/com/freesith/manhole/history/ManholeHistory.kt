package com.freesith.manhole.history

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.freesith.manhole.ManholeConstants
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.io.File
import java.io.IOException
import java.lang.Exception

private const val TABLE_HISTORY = "manhole_history"
private const val COLUMN_URL = "url"
private const val COLUMN_REQUEST = "request"
private const val COLUMN_RESPONSE = "response"
private const val COLUMN_TIME = "time"
private const val COLUMN_MOCK = "mock"
private const val COLUMN_CODE = "code"

object ManholeHistory {

    private var historyDb: SQLiteDatabase? = null

    fun init(context: Context) {
        val historySqliteHelper = HistorySqliteHelper(
            context,
            context.filesDir.absolutePath + File.separator + ManholeConstants.HISTORY_DB_NAME,
            null,
            1
        )
        historyDb = historySqliteHelper.writableDatabase
    }

    fun recordHistory(mocked: Boolean, request: Request, response: Response) {
        val url = request.url().toString()
        val buffer = Buffer()
        val requestBody = request.body()
        try {
            requestBody?.writeTo(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val requestBodyString = buffer.readUtf8()
        val code = response.code()
        val contentLength = response.body()?.contentLength() ?: 0
        var responseBodyString = if (contentLength > 0) {
            response.peekBody(contentLength).string()
        } else {
            ""
        }
//        val case = Case()
//        case.desc = "xgagea"
//        case.enable = false
//        case.module = "gageage"
//        case.name = "gaegaoighae"
//        case.title = "geajfioaifa"
//
//        val mock = MockChoice()
//        mock.desc = "xxxxxxxxxxxxxx"
//        mock.enable = true
//        mock.name = "gageag"
//        mock.title = "zzzzzzzzzzzzzzzzz"
//        case.mocks = mutableListOf(mock)
//        responseBodyString = JSON.toJSONString(case)
//        Log.d("xxx","response = " + responseBodyString)
        writeHistoryDatabase(mocked, url, requestBodyString, code, responseBodyString)
    }

    private fun writeHistoryDatabase(
        mocked: Boolean,
        url: String,
        requestBody: String,
        code: Int,
        responseBody: String
    ) {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_URL, url)
        contentValues.put(COLUMN_REQUEST, requestBody)
        contentValues.put(COLUMN_CODE, code)
        contentValues.put(COLUMN_RESPONSE, responseBody)
        contentValues.put(COLUMN_TIME, System.currentTimeMillis())
        contentValues.put(COLUMN_MOCK, if (mocked) 1 else 0)
        historyDb?.insert(TABLE_HISTORY, null, contentValues)
    }


    fun readHistoryDown(count: Int) : List<HttpHistory> {
        val sql = """SELECT * FROM $TABLE_HISTORY ORDER BY id DESC LIMIT $count"""
        val historyList = mutableListOf<HttpHistory>()
        var cursor: Cursor? = null
        try {
            cursor = historyDb?.rawQuery(sql, null)
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val url = cursor.getString(cursor.getColumnIndex(COLUMN_URL))
                val requestBody = cursor.getString(cursor.getColumnIndex(COLUMN_REQUEST))
                val code = cursor.getInt(cursor.getColumnIndex(COLUMN_CODE))
                val responseBody = cursor.getString(cursor.getColumnIndex(COLUMN_RESPONSE))
                val mocked = cursor.getInt(cursor.getColumnIndex(COLUMN_MOCK))
                val time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME))

                val httpHistory = HttpHistory()
                httpHistory.id = id
                httpHistory.url = url
                httpHistory.requestBody = requestBody
                httpHistory.code = code
                httpHistory.responseBody = responseBody
                httpHistory.mock = mocked == 1
                httpHistory.time = time
                historyList.add(httpHistory)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return historyList
    }

    fun readMoreHistory(id: Int, count: Int) {

    }

}

class HistorySqliteHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """CREATE TABLE $TABLE_HISTORY (
            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
            $COLUMN_URL TEXT,
            $COLUMN_REQUEST TEXT, 
            $COLUMN_CODE INTEGER, 
            $COLUMN_RESPONSE TEXT, 
            $COLUMN_MOCK INTEGER, 
            $COLUMN_TIME INTEGER)""".trimMargin()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}