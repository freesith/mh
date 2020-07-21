package com.freesith.manhole

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSON
import com.freesith.manhole.bean.*
import okhttp3.Request
import okhttp3.Response
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ManholeMock {
    private var mockDb: SQLiteDatabase? = null
    var enableMockMap = ConcurrentHashMap<String, LinkedList<MockChoice>>()

    @Synchronized
    fun initMockDb(context: Context?, dbPath: String) {
        Log.d(TAG, "initDb path = $dbPath")
        if (mockDb != null) {
            mockDb!!.close()
        }
        val sqliteHelper = MockSqliteHelper(context, dbPath, null, 1)
        mockDb = sqliteHelper.writableDatabase
        mockDb = SQLiteDatabase.openOrCreateDatabase(dbPath, null)

//        refreshPassive();
        refreshMocks()
    }

    fun log(request: Request?, response: Response?) {}
    fun mock(request: Request): MockResponse? {
        val method = request.method().toLowerCase()
        val url = request.url()
        val path = url.encodedPath()
        if (enableMockMap.isEmpty()) {
            return null
        }
        val mockChoices = enableMockMap[method + path]
        if (mockChoices == null || mockChoices.isEmpty()) {
            return null
        }
        for (mockChoice in mockChoices) {
            if (mockChoice.matches(request)) {
                val mockResponse = MockResponse()
                mockResponse.code = mockChoice.code
                mockResponse.message = mockChoice.message
                mockResponse.data = mockChoice.data
                if (mockChoice.delay > 0) {
                    try {
                        Thread.sleep(mockChoice.delay.toLong())
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                return mockResponse
            }
        }
        return null
    }

    /**
     * enable的mock以method + path为key
     * 改变的时候需要检查,如果path以存在,
     * 那么如果两个mock的host有重合,或参数有重合,那么要提示
     */
    private fun refreshMocks() {
        enableMockMap.clear()
        if (mockDb == null) {
            return
        }
        val mockSql =
            "SELECT * FROM table_mock WHERE status& $FLAG_PASSIVE = $FLAG_PASSIVE OR status& $FLAG_ENABLE = $FLAG_ENABLE"
        val cursor = mockDb!!.rawQuery(mockSql, null)
        while (cursor.moveToNext()) {
            val method =
                cursor.getString(cursor.getColumnIndex("method")).toLowerCase()
            val host = cursor.getString(cursor.getColumnIndex("host")).toLowerCase()
            val path = cursor.getString(cursor.getColumnIndex("path"))
            val status = cursor.getInt(cursor.getColumnIndex("status"))
            val json = cursor.getString(cursor.getColumnIndex("json"))
            val mockChoice = JSON.parseObject(json, MockChoice::class.java)
            mockChoice.method = method
            mockChoice.path = path
            if (!TextUtils.isEmpty(host)) {
                val split = host.split(",".toRegex()).toTypedArray()
                mockChoice.host = Arrays.asList(*split)
            }
            mockChoice.enable =
                status and FLAG_ENABLE == FLAG_ENABLE
            mockChoice.passive =
                status and FLAG_PASSIVE == FLAG_PASSIVE
            var mocks = enableMockMap[method + path]
            if (mocks == null) {
                mocks = LinkedList()
                enableMockMap[method + path] = mocks
            }
            mocks.add(mockChoice)
        }
        cursor.close()
    }

    //            int status = cursor.getInt(cursor.getColumnIndex("status"));
//            String json = cursor.getString(cursor.getColumnIndex("json"));
    val mocks: List<Mock>?
        get() {
            if (mockDb == null) {
                return null
            }
            val cursor = mockDb!!.rawQuery(
                "SELECT DISTINCT name, title, description, method, path FROM table_mock ORDER BY status DESC",
                null
            )
            val mockList: MutableList<Mock> = ArrayList()
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val desc = cursor.getString(cursor.getColumnIndex("description"))
                //            int status = cursor.getInt(cursor.getColumnIndex("status"));
//            String json = cursor.getString(cursor.getColumnIndex("json"));
                val method = cursor.getString(cursor.getColumnIndex("method"))
                val path = cursor.getString(cursor.getColumnIndex("path"))

//            Mock mock = JSON.parseObject(json, Mock.class);
                val mock = Mock()
                val mockRequest = MockRequest()
                mockRequest.method = method
                mockRequest.path = path
                mock.name = name
                mock.title = title
                mock.desc = desc
                mock.request = mockRequest
                //            mock.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
//            mock.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
                mockList.add(mock)
            }
            cursor.close()
            return mockList
        }

    val cases: List<Case>?
        get() {
            if (mockDb == null) {
                return null
            }
            val cursor = mockDb!!.rawQuery(
                "SELECT DISTINCT name, title ,status FROM table_case ORDER BY status DESC",
                null
            )
            val caseList: MutableList<Case> = ArrayList()
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val status = cursor.getInt(cursor.getColumnIndex("status"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val caze = Case()
                caze.name = name
                caze.enable =
                    status and FLAG_ENABLE == FLAG_ENABLE
                caze.passive =
                    status and FLAG_PASSIVE == FLAG_PASSIVE
                caze.title = title
                caseList.add(caze)
            }
            cursor.close()
            return caseList
        }

    val flows: List<Flow>?
        get() {
            if (mockDb == null) {
                return null
            }
            val cursor = mockDb!!.rawQuery(
                "SELECT DISTINCT name,title,status FROM table_flow ORDER BY status DESC",
                null
            )
            val flowList: MutableList<Flow> =
                ArrayList()
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val status = cursor.getInt(cursor.getColumnIndex("status"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val flow = Flow()
                flow.name = name
                flow.enable =
                    status and FLAG_ENABLE == FLAG_ENABLE
                flow.title = title
                flowList.add(flow)
            }
            cursor.close()
            return flowList
        }

    fun updateFlowEnable(flowName: String, enable: Boolean) {
        if (mockDb == null) {
            return
        }
        if (enable) {
            mockDb!!.execSQL(
                "UPDATE table_flow SET status = status | ? WHERE name = ?",
                arrayOf<Any>(FLAG_ENABLE, flowName)
            )
        } else {
            mockDb!!.execSQL(
                "UPDATE table_flow SET status = status & ? WHERE name = ?",
                arrayOf<Any>(FLAG_ENABLE.inv(), flowName)
            )
        }
        refreshPassive()
        refreshMocks()
    }

    fun updateCaseEnable(caseName: String, enable: Boolean) {
        if (mockDb == null) {
            return
        }
        if (enable) {
            mockDb!!.execSQL(
                "UPDATE table_case SET status = status | ? WHERE name = ?",
                arrayOf<Any>(FLAG_ENABLE, caseName)
            )
        } else {
            mockDb!!.execSQL(
                "UPDATE table_case SET status = status & ? WHERE name = ?",
                arrayOf<Any>(FLAG_ENABLE.inv(), caseName)
            )
        }
        refreshPassive()
        refreshMocks()
    }

    fun updateMockEnable(mockName: String, enable: Boolean) {
        if (mockDb == null) {
            return
        }
        if (enable) {
            mockDb!!.execSQL(
                "UPDATE table_mock SET status = status | ? WHERE name = ?",
                arrayOf<Any>(FLAG_ENABLE, mockName)
            )
        } else {
            mockDb!!.execSQL(
                "UPDATE table_mock SET status = status & ? WHERE name = ?",
                arrayOf<Any>(FLAG_ENABLE.inv(), mockName)
            )
        }
        refreshMocks()
    }

    private fun refreshPassive() {
        if (mockDb == null) {
            return
        }
        val passiveCases = HashSet<String>()
        val passiveChoices = HashSet<String>()

        //找出开启的flow
        val cursorFlow = mockDb!!.rawQuery(
            "SELECT * FROM table_flow WHERE status&$FLAG_ENABLE = $FLAG_ENABLE",
            null
        )
        while (cursorFlow.moveToNext()) {
            val caseName =
                cursorFlow.getString(cursorFlow.getColumnIndex("caseName"))
            if (!TextUtils.isEmpty(caseName)) {
                passiveCases.add(caseName)
            }
            val choice = cursorFlow.getString(cursorFlow.getColumnIndex("choice"))
            if (!TextUtils.isEmpty(choice)) {
                passiveChoices.add(choice)
            }
        }
        cursorFlow.close()
        val passiveCaseNames = Util.setToSelection(passiveCases)
        val sql = """UPDATE table_case SET status = CASE
WHEN name IN $passiveCaseNames THEN
status | $FLAG_PASSIVE
ELSE
status & ${FLAG_PASSIVE.inv()}
END;"""
        mockDb!!.execSQL(sql, arrayOf())
        val cursorCases = mockDb!!.rawQuery(
            "SELECT * FROM table_case WHERE status& $FLAG_ENABLE = $FLAG_ENABLE OR status& $FLAG_PASSIVE = $FLAG_PASSIVE",
            null
        )
        while (cursorCases.moveToNext()) {
            val choice = cursorCases.getString(cursorCases.getColumnIndex("choice"))
            if (!TextUtils.isEmpty(choice)) {
                passiveChoices.add(choice)
            }
        }
        cursorCases.close()
        val passiveMockNames =
            Util.setToSelection(passiveChoices)
        mockDb!!.execSQL(
            """UPDATE table_mock SET status = CASE
WHEN choice IN $passiveMockNames THEN
status | $FLAG_PASSIVE
ELSE
status& ${FLAG_PASSIVE.inv()}
END;""", arrayOf()
        )
    }

    private fun lockCases(names: String) {}
    fun getChoicesByMock(name: String): List<MockChoice>? {
        if (mockDb == null) {
            return null
        }
        val sql = "SELECT * FROM table_mock WHERE name='$name'"
        val cursor = mockDb!!.rawQuery(sql, null)
        val list: MutableList<MockChoice> = ArrayList()
        while (cursor.moveToNext()) {
            val json = cursor.getString(cursor.getColumnIndex("json"))
            val method = cursor.getString(cursor.getColumnIndex("method"))
            val path = cursor.getString(cursor.getColumnIndex("path"))
            val status = cursor.getInt(cursor.getColumnIndex("status"))
            val mockChoice = JSON.parseObject(json, MockChoice::class.java)
            mockChoice.method = method
            mockChoice.path = path
            mockChoice.enable =
                status and FLAG_ENABLE == FLAG_ENABLE
            mockChoice.passive =
                status and FLAG_PASSIVE == FLAG_PASSIVE
            list.add(mockChoice)
        }
        cursor.close()
        return list
    }

    fun getCaseByName(name: String): Case? {
        if (mockDb == null) {
            return null
        }
        val caze = Case()
        val sql = "SELECT * FROM table_case WHERE name='$name'"
        val cursor = mockDb!!.rawQuery(sql, null)
        var first = true
        val choiceNameList: MutableSet<String> =
            HashSet()
        while (cursor.moveToNext()) {
            if (first) {
                caze.title = Util.getCursorString(cursor, "title")
                caze.name = Util.getCursorString(cursor, "name")
                caze.module = Util.getCursorString(cursor, "module")
                caze.desc = Util.getCursorString(cursor, "description")
                first = false
            }
            val choice = Util.getCursorString(cursor, "choice")
            if (!TextUtils.isEmpty(choice)) {
                choiceNameList.add(choice)
            }
            caze.mocks = getChoiceList(choiceNameList)
        }
        cursor.close()
        return caze
    }

    fun getFlowByName(name: String): Flow? {
        if (mockDb == null) {
            return null
        }
        val sql = "SELECT * FROM table_flow WHERE name='$name'"
        val flow = Flow()
        val cursor = mockDb!!.rawQuery(sql, null)
        var first = true
        val choiceNameList: MutableSet<String> =
            HashSet()
        val caseNameList: MutableSet<String> =
            HashSet()
        while (cursor.moveToNext()) {
            if (first) {
                flow.title = Util.getCursorString(cursor, "title")
                flow.name = Util.getCursorString(cursor, "name")
                flow.module = Util.getCursorString(cursor, "module")
                flow.desc = Util.getCursorString(cursor, "description")
                first = false
            }
            val caseName =
                Util.getCursorString(cursor, "caseName")
            val choice = Util.getCursorString(cursor, "choice")
            if (!TextUtils.isEmpty(caseName)) {
                caseNameList.add(caseName)
            }
            if (!TextUtils.isEmpty(choice)) {
                choiceNameList.add(choice)
            }
            flow.cases = getCaseList(caseNameList)
            flow.mocks = getChoiceList(choiceNameList)
        }
        cursor.close()
        return flow
    }

    fun getCaseList(caseNames: Collection<String>?): List<Case>? {
        if (mockDb == null) {
            return null
        }
        val selection = Util.setToSelection(caseNames)
        if (TextUtils.isEmpty(selection)) {
            return null
        }
        val caseList: MutableList<Case> = ArrayList()
        val cursor = mockDb!!.rawQuery(
            "SELECT DISTINCT name,title,module,description FROM table_case WHERE name IN $selection",
            null
        )
        while (cursor.moveToNext()) {
            val caze = Case()
            caze.name = Util.getCursorString(cursor, "name")
            caze.title = Util.getCursorString(cursor, "title")
            caze.module = Util.getCursorString(cursor, "module")
            caze.desc = Util.getCursorString(cursor, "description")
            caseList.add(caze)
        }
        cursor.close()
        return caseList
    }

    fun getChoiceList(choiceNames: Collection<String>?): List<MockChoice>? {
        if (mockDb == null) {
            return null
        }
        val selection = Util.setToSelection(choiceNames)
        if (TextUtils.isEmpty(selection)) {
            return null
        }
        val caseList: MutableList<MockChoice> = ArrayList()
        val cursor =
            mockDb!!.rawQuery("SELECT * FROM table_mock WHERE choice IN $selection", null)
        while (cursor.moveToNext()) {
            val method =
                cursor.getString(cursor.getColumnIndex("method")).toLowerCase()
            val host = cursor.getString(cursor.getColumnIndex("host")).toLowerCase()
            val path = cursor.getString(cursor.getColumnIndex("path"))
            val status = cursor.getInt(cursor.getColumnIndex("status"))
            val json = cursor.getString(cursor.getColumnIndex("json"))
            val mockChoice = JSON.parseObject(json, MockChoice::class.java)
            mockChoice.method = method
            mockChoice.path = path
            if (!TextUtils.isEmpty(host)) {
                val split = host.split(",".toRegex()).toTypedArray()
                mockChoice.host = Arrays.asList(*split)
            }
            mockChoice.enable =
                status and FLAG_ENABLE == FLAG_ENABLE
            mockChoice.passive =
                status and FLAG_PASSIVE == FLAG_PASSIVE
            caseList.add(mockChoice)
        }
        cursor.close()
        return caseList
    }

    fun updateMockChoiceEnable(
        mockName: String,
        index: Int,
        enable: Boolean
    ) {
        if (mockDb == null) {
            return
        }
        if (enable) {
            mockDb!!.execSQL(
                "UPDATE table_mock SET status = status | ? WHERE name = ? AND choice = ?",
                arrayOf<Any>(FLAG_ENABLE, mockName, mockName + "_" + index)
            )
        } else {
            mockDb!!.execSQL(
                "UPDATE table_mock SET status = status & ? WHERE name = ? AND choice = ?",
                arrayOf<Any>(
                    FLAG_ENABLE.inv(),
                    mockName,
                    mockName + "_" + index
                )
            )
        }
        refreshMocks()
    }

    fun findMockByName(name: String): Mock? {
        if (mockDb == null) {
            return null
        }
        val sql =
            "SELECT DISTINCT name,title,description,method,path,host,module FROM table_mock WHERE name = '$name'"
        val cursor = mockDb!!.rawQuery(sql, null)
        var mock: Mock? = null
        if (cursor.moveToFirst()) {
            mock = Mock()
            mock.name = Util.getCursorString(cursor, "name")
            mock.title = Util.getCursorString(cursor, "title")
            mock.desc = Util.getCursorString(cursor, "description")
            mock.desc = Util.getCursorString(cursor, "description")
            mock.request = MockRequest()
            mock.request.method = Util.getCursorString(cursor, "method")
            val host = Util.getCursorString(cursor, "host")
            if (!TextUtils.isEmpty(host)) {
                mock.request.host = Arrays.asList(
                    *host.split(",".toRegex()).toTypedArray()
                )
            }
            mock.request.path = Util.getCursorString(cursor, "path")
        }
        return mock
    }

    const val TAG = "xxx_mox"
    const val FLAG_ENABLE = 1
    const val FLAG_PASSIVE = 1 shl 1
    const val ACTION_SHOW_MOCK = 1
}