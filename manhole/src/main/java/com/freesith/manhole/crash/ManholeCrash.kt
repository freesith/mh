package com.freesith.manhole.crash

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.freesith.manhole.ManholeConstants
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.Exception

private const val TABLE_EXCEPTION = "manhole_exception"
private const val COLUMN_ID = "id"
private const val COLUMN_TIME = "time"
private const val COLUMN_NAME = "name"
private const val COLUMN_DESC = "desc"

object ManholeCrash  {

    private var exceptionDb: SQLiteDatabase? = null

    fun init(context: Context) {
        val crashSqliteHelper = ExceptionSqliteHelper(
            context,
            context.filesDir.absolutePath + File.separator + ManholeConstants.EXCEPTION_DB_NAME,
            null,
            1
        )
        exceptionDb = crashSqliteHelper.writableDatabase
    }

    fun uncaughtException(t: Thread, e: Throwable) {
        val caughtException = CrashInfo()
        caughtException.time = System.currentTimeMillis()
        caughtException.name = e::class.java.simpleName
        caughtException.desc = getExceptionInfo(e)

        saveExceptionInfo(caughtException)
    }

    private fun getExceptionInfo(ex: Throwable): String? {
        val writer: Writer = StringWriter()
        val pw = PrintWriter(writer)
        ex.printStackTrace(pw)
        pw.close()
        return writer.toString()
    }

    private fun saveExceptionInfo(caughtException: CrashInfo) {
        val contentValues = ContentValues()
        contentValues.put(COLUMN_NAME, caughtException.name)
        contentValues.put(COLUMN_DESC, caughtException.desc)
        contentValues.put(COLUMN_TIME, caughtException.time)
        exceptionDb?.insert(TABLE_EXCEPTION, null, contentValues)
    }

    fun readSimpleExceptionDown(count: Int): List<CrashInfo> {
        val sql = """SELECT ${COLUMN_ID}, $COLUMN_NAME, $COLUMN_TIME FROM $TABLE_EXCEPTION ORDER BY $COLUMN_ID DESC LIMIT $count"""
        val exceptionList = mutableListOf<CrashInfo>()
        var cursor: Cursor? = null
        try {
            cursor = exceptionDb?.rawQuery(sql, null)
            while (cursor?.moveToNext() == true) {
                val exception = CrashInfo()
                exception.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                exception.name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                exception.time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME))
                exceptionList.add(exception)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return exceptionList
    }

    fun readMoreException(id: Int, count: Int) : List<CrashInfo> {
        val sql = """SELECT $COLUMN_ID, ${COLUMN_NAME}, $COLUMN_TIME FROM $TABLE_EXCEPTION  WHERE $COLUMN_ID < $id ORDER BY id DESC LIMIT $count"""
        val exceptionList = mutableListOf<CrashInfo>()
        var cursor: Cursor? = null
        try {
            cursor = exceptionDb?.rawQuery(sql, null)
            while (cursor?.moveToNext() == true) {
                val caughtException = CrashInfo()
                caughtException.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                caughtException.name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                caughtException.time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME))
                exceptionList.add(caughtException)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return exceptionList
    }

    fun getExceptionById(exceptionId: Int): CrashInfo? {
        val sql = """SELECT * FROM $TABLE_EXCEPTION WHERE ${COLUMN_ID} = $exceptionId"""
        var cursor: Cursor? = null
        try {
            cursor = exceptionDb?.rawQuery(sql, null)
            if (cursor?.count == 1 && cursor?.moveToNext()) {
                val caughtException = CrashInfo()
                caughtException.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                caughtException.name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                caughtException.desc = cursor.getString(cursor.getColumnIndex(COLUMN_DESC))
                caughtException.time = cursor.getLong(cursor.getColumnIndex(COLUMN_TIME))
                return caughtException
            }
        } catch (e : Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

}

class ExceptionSqliteHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """CREATE TABLE $TABLE_EXCEPTION (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
            $COLUMN_NAME TEXT,
            $COLUMN_DESC TEXT,
            $COLUMN_TIME INTEGER)""".trimMargin()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}