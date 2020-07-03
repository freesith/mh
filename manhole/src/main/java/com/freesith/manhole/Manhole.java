package com.freesith.manhole;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.freesith.manhole.bean.Case;
import com.freesith.manhole.bean.Flow;
import com.freesith.manhole.bean.Mock;
import com.freesith.manhole.bean.MockChoice;
import com.freesith.manhole.bean.MockRequest;
import com.freesith.manhole.bean.MockResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class Manhole {

    public static final String TAG = "xxx_mox";

    public static final int FLAG_ENABLE = 1;
    public static final int FLAG_PASSIVE = 1 << 1;

    public static final int ACTION_SHOW_MOCK = 1;

    private SQLiteDatabase mockDb;

    private static volatile Manhole manhole;
    private static volatile boolean inited = false;

    private Handler handler;

    public ConcurrentHashMap<String, LinkedList<MockChoice>> enableMockMap = new ConcurrentHashMap<>();

    public static Manhole getInstance() {
        if (manhole == null) {
            manhole = new Manhole();
        }
        return manhole;
    }

    private Manhole() {
        initHandler();
    }

    private void initHandler() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case ACTION_SHOW_MOCK:
                        String name = (String) msg.obj;

                        break;
                }
                return false;
            }
        });
    }

    public void showMock(String name) {
        Message message = Message.obtain();
        message.what = ACTION_SHOW_MOCK;
        message.obj = name;
        handler.sendMessage(message);
    }

    static void init() {
        if (inited) {
            return;
        }
        manhole = new Manhole();
        inited = true;
    }

    public synchronized void initMockDb(Context context, String dbPath) {
        Log.d(TAG, "initDb path = " + dbPath);
        if (mockDb != null) {
            mockDb.close();
        }
        MockSqliteHelper sqliteHelper = new MockSqliteHelper(context, dbPath, null, 1);
        mockDb = sqliteHelper.getWritableDatabase();
        mockDb = SQLiteDatabase.openOrCreateDatabase(dbPath, null);

//        refreshPassive();
        refreshMocks();
    }


    public void log(Request request, Response response) {

    }


    public MockResponse mock(Request request) {

        String method = request.method().toLowerCase();
        HttpUrl url = request.url();
        String path = url.encodedPath();
        if (enableMockMap.isEmpty()) {
            return null;
        }
        LinkedList<MockChoice> mockChoices = enableMockMap.get(method + path);
        if (mockChoices == null || mockChoices.isEmpty()) {
            return null;
        }
        for (MockChoice mockChoice : mockChoices) {
            if (mockChoice.matches(request)) {
                MockResponse mockResponse = new MockResponse();
                mockResponse.code = mockChoice.code;
                mockResponse.message = mockChoice.message;
                mockResponse.data = mockChoice.data;
                if (mockChoice.delay > 0) {
                    try {
                        Thread.sleep(mockChoice.delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return mockResponse;
            }
        }

        return null;
    }


    /**
     * enable的mock以method + path为key
     * 改变的时候需要检查,如果path以存在,
     * 那么如果两个mock的host有重合,或参数有重合,那么要提示
     */

    private void refreshMocks() {

        enableMockMap.clear();

        if (mockDb == null) {
            return;
        }

        String mockSql = "SELECT * FROM table_mock WHERE status& " + FLAG_PASSIVE + " = " + FLAG_PASSIVE + " OR status& " + FLAG_ENABLE + " = " + FLAG_ENABLE;
        Cursor cursor = mockDb.rawQuery(mockSql, null);
        while (cursor.moveToNext()) {
            String method = cursor.getString(cursor.getColumnIndex("method")).toLowerCase();
            String host = cursor.getString(cursor.getColumnIndex("host")).toLowerCase();
            String path = cursor.getString(cursor.getColumnIndex("path"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String json = cursor.getString(cursor.getColumnIndex("json"));

            MockChoice mockChoice = JSON.parseObject(json, MockChoice.class);
            mockChoice.method = method;
            mockChoice.path = path;
            if (!TextUtils.isEmpty(host)) {
                String[] split = host.split(",");
                mockChoice.host = Arrays.asList(split);
            }

            mockChoice.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            mockChoice.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;

            LinkedList<MockChoice> mocks = enableMockMap.get(method + path);
            if (mocks == null) {
                mocks = new LinkedList<>();
                enableMockMap.put(method + path, mocks);
            }
            mocks.add(mockChoice);
        }
        cursor.close();
    }

    public List<Mock> getMocks() {
        if (mockDb == null) {
            return null;
        }
        Cursor cursor = mockDb.rawQuery("SELECT DISTINCT name, title, description, method, path FROM table_mock ORDER BY status DESC", null);
        List<Mock> mockList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String desc = cursor.getString(cursor.getColumnIndex("description"));
//            int status = cursor.getInt(cursor.getColumnIndex("status"));
//            String json = cursor.getString(cursor.getColumnIndex("json"));
            String method  = cursor.getString(cursor.getColumnIndex("method"));
            String path  = cursor.getString(cursor.getColumnIndex("path"));

//            Mock mock = JSON.parseObject(json, Mock.class);


            Mock mock = new Mock();
            MockRequest mockRequest = new MockRequest();
            mockRequest.method = method;
            mockRequest.path = path;

            mock.name = name;
            mock.title = title;
            mock.desc = desc;
            mock.request = mockRequest;
//            mock.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
//            mock.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
            mockList.add(mock);
        }
        cursor.close();
        return mockList;
    }

    public List<Case> getCases() {
        if (mockDb == null) {
            return null;
        }
        Cursor cursor = mockDb.rawQuery("SELECT DISTINCT name, title ,status FROM table_case ORDER BY status DESC", null);
        List<Case> caseList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String title = cursor.getString(cursor.getColumnIndex("title"));

            Case caze = new Case();
            caze.name = name;
            caze.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            caze.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
            caze.title = title;
            caseList.add(caze);
        }
        cursor.close();
        return caseList;
    }

    public List<Flow> getFlows() {
        if (mockDb == null) {
            return null;
        }
        Cursor cursor = mockDb.rawQuery("SELECT DISTINCT name,title,status FROM table_flow ORDER BY status DESC", null);
        List<Flow> flowList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String title = cursor.getString(cursor.getColumnIndex("title"));

            Flow flow = new Flow();
            flow.name = name;
            flow.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            flow.title = title;
            flowList.add(flow);
        }
        cursor.close();
        return flowList;
    }


    public void updateFlowEnable(String flowName, boolean enable) {
        if (mockDb == null) {
            return;
        }
        if (enable) {
            mockDb.execSQL("UPDATE table_flow SET status = status | ? WHERE name = ?", new Object[]{FLAG_ENABLE, flowName});
        } else {
            mockDb.execSQL("UPDATE table_flow SET status = status & ? WHERE name = ?", new Object[]{~FLAG_ENABLE, flowName});
        }
        refreshPassive();
        refreshMocks();
    }

    public void updateCaseEnable(String caseName, boolean enable) {
        if (mockDb == null) {
            return;
        }
        if (enable) {
            mockDb.execSQL("UPDATE table_case SET status = status | ? WHERE name = ?", new Object[]{FLAG_ENABLE, caseName});
        } else {
            mockDb.execSQL("UPDATE table_case SET status = status & ? WHERE name = ?", new Object[]{~FLAG_ENABLE, caseName});
        }
        refreshPassive();
        refreshMocks();
    }

    public void updateMockEnable(String mockName, boolean enable) {
        if (mockDb == null) {
            return;
        }
        if (enable) {
            mockDb.execSQL("UPDATE table_mock SET status = status | ? WHERE name = ?", new Object[]{FLAG_ENABLE, mockName});
        } else {
            mockDb.execSQL("UPDATE table_mock SET status = status & ? WHERE name = ?", new Object[]{~FLAG_ENABLE, mockName});
        }
        refreshMocks();
    }


    private void refreshPassive() {
        if (mockDb == null) {
            return;
        }

        HashSet<String> passiveCases = new HashSet<>();
        HashSet<String> passiveChoices = new HashSet<>();

        //找出开启的flow
        Cursor cursorFlow = mockDb.rawQuery("SELECT * FROM table_flow WHERE status&" + FLAG_ENABLE + " = " + FLAG_ENABLE, null);
        while (cursorFlow.moveToNext()) {
            String caseName = cursorFlow.getString(cursorFlow.getColumnIndex("caseName"));
            if (!TextUtils.isEmpty(caseName)) {
                passiveCases.add(caseName);
            }

            String choice = cursorFlow.getString(cursorFlow.getColumnIndex("choice"));
            if (!TextUtils.isEmpty(choice)) {
                passiveChoices.add(choice);
            }
        }
        cursorFlow.close();

        String passiveCaseNames = Util.setToSelection(passiveCases);
        String sql = "UPDATE table_case SET status = CASE \n" +
                "WHEN name IN " + passiveCaseNames + " THEN \n" +
                "status | " + FLAG_PASSIVE + " \n" +
                "ELSE\n" +
                "status & " + ~FLAG_PASSIVE + " \n" +
                "END;";
        mockDb.execSQL(sql, new Object[]{});
        Cursor cursorCases = mockDb.rawQuery("SELECT * FROM table_case WHERE status& " + FLAG_ENABLE + " = " + FLAG_ENABLE + " OR status& " + FLAG_PASSIVE + " = " + FLAG_PASSIVE, null);
        while (cursorCases.moveToNext()) {
            String choice = cursorCases.getString(cursorCases.getColumnIndex("choice"));
            if (!TextUtils.isEmpty(choice)) {
                passiveChoices.add(choice);
            }
        }
        cursorCases.close();

        String passiveMockNames = Util.setToSelection(passiveChoices);
        mockDb.execSQL("UPDATE table_mock SET status = CASE \n" +
                "WHEN choice IN " + passiveMockNames + " THEN \n" +
                "status | " + FLAG_PASSIVE + " \n" +
                "ELSE \n" +
                "status& " + ~FLAG_PASSIVE + " \n" +
                "END;", new Object[]{});
    }


    private void lockCases(String names) {

    }

    public List<MockChoice> getChoicesByMock(String name) {
        if (mockDb == null) {
            return null;
        }
        String sql = "SELECT * FROM table_mock WHERE name='" + name + "'";
        Cursor cursor = mockDb.rawQuery(sql, null);
        List<MockChoice> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String json = cursor.getString(cursor.getColumnIndex("json"));
            String method = cursor.getString(cursor.getColumnIndex("method"));
            String path = cursor.getString(cursor.getColumnIndex("path"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            MockChoice mockChoice = JSON.parseObject(json, MockChoice.class);
            mockChoice.method = method;
            mockChoice.path = path;
            mockChoice.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            mockChoice.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
            list.add(mockChoice);
        }
        cursor.close();
        return list;
    }

    public Case getCaseByName(String name) {
        if (mockDb == null) {
            return null;
        }
        Case caze = new Case();
        String sql = "SELECT * FROM table_case WHERE name='" + name + "'";
        Cursor cursor = mockDb.rawQuery(sql, null);
        boolean first = true;
        Set<String> choiceNameList = new HashSet<>();
        while (cursor.moveToNext()) {
            if (first) {
                caze.title = Util.getCursorString(cursor,"title");
                caze.name = Util.getCursorString(cursor,"name");
                caze.module = Util.getCursorString(cursor, "module");
                caze.desc = Util.getCursorString(cursor, "description");
                first = false;
            }
            String choice = Util.getCursorString(cursor, "choice");
            if (!TextUtils.isEmpty(choice)) {
                choiceNameList.add(choice);
            }
            caze.mocks = getChoiceList(choiceNameList);
        }
        cursor.close();
        return caze;
    }

    public Flow getFlowByName(String name) {
        if (mockDb == null) {
            return null;
        }
        String sql = "SELECT * FROM table_flow WHERE name='" + name + "'";
        Flow flow = new Flow();
        Cursor cursor = mockDb.rawQuery(sql, null);
        boolean first = true;
        Set<String> choiceNameList = new HashSet<>();
        Set<String> caseNameList = new HashSet<>();
        while (cursor.moveToNext()) {
            if (first) {
                flow.title = Util.getCursorString(cursor,"title");
                flow.name = Util.getCursorString(cursor,"name");
                flow.module = Util.getCursorString(cursor, "module");
                flow.desc = Util.getCursorString(cursor, "description");
                first = false;
            }
            String caseName = Util.getCursorString(cursor, "caseName");
            String choice = Util.getCursorString(cursor, "choice");
            if (!TextUtils.isEmpty(caseName)) {
                caseNameList.add(caseName);
            }
            if (!TextUtils.isEmpty(choice)) {
                choiceNameList.add(choice);
            }

            flow.cases = getCaseList(caseNameList);
            flow.mocks = getChoiceList(choiceNameList);

        }
        cursor.close();
        return flow;
    }

    public List<Case> getCaseList(Collection<String> caseNames) {
        if (mockDb == null) {
            return null;
        }
        String selection = Util.setToSelection(caseNames);
        if (TextUtils.isEmpty(selection)) {
            return null;
        }
        List<Case> caseList = new ArrayList<>();
        Cursor cursor = mockDb.rawQuery("SELECT DISTINCT name,title,module,description FROM table_case WHERE name IN " + selection, null);
        while (cursor.moveToNext()) {
            Case caze = new Case();
            caze.name = Util.getCursorString(cursor, "name");
            caze.title = Util.getCursorString(cursor, "title");
            caze.module = Util.getCursorString(cursor, "module");
            caze.desc = Util.getCursorString(cursor, "description");
            caseList.add(caze);
        }
        cursor.close();
        return caseList;
    }

    public List<MockChoice> getChoiceList(Collection<String> choiceNames) {
        if (mockDb == null) {
            return null;
        }
        String selection = Util.setToSelection(choiceNames);
        if (TextUtils.isEmpty(selection)) {
            return null;
        }
        List<MockChoice> caseList = new ArrayList<>();
        Cursor cursor = mockDb.rawQuery("SELECT * FROM table_mock WHERE choice IN " + selection, null);
        while (cursor.moveToNext()) {


            String method = cursor.getString(cursor.getColumnIndex("method")).toLowerCase();
            String host = cursor.getString(cursor.getColumnIndex("host")).toLowerCase();
            String path = cursor.getString(cursor.getColumnIndex("path"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String json = cursor.getString(cursor.getColumnIndex("json"));

            MockChoice mockChoice = JSON.parseObject(json, MockChoice.class);
            mockChoice.method = method;
            mockChoice.path = path;
            if (!TextUtils.isEmpty(host)) {
                String[] split = host.split(",");
                mockChoice.host = Arrays.asList(split);
            }

            mockChoice.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            mockChoice.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
            caseList.add(mockChoice);
        }
        cursor.close();
        return caseList;
    }

    public void  updateMockChoiceEnable(String mockName, int index, boolean enable) {
        if (mockDb == null) {
            return;
        }
        if (enable) {
            mockDb.execSQL("UPDATE table_mock SET status = status | ? WHERE name = ? AND choice = ?", new Object[]{FLAG_ENABLE, mockName, mockName + "_" + index});
        } else {
            mockDb.execSQL("UPDATE table_mock SET status = status & ? WHERE name = ? AND choice = ?", new Object[]{~FLAG_ENABLE, mockName, mockName + "_" + index});
        }
        refreshMocks();
    }

    public Mock findMockByName(String name) {
        if (mockDb == null) {
            return null;
        }
        String sql = "SELECT DISTINCT name,title,description,method,path,host,module FROM table_mock WHERE name = '" + name + "'";
        Cursor cursor = mockDb.rawQuery(sql, null);
        Mock mock = null;
        if (cursor.moveToFirst()) {
            mock = new Mock();
            mock.name = Util.getCursorString(cursor,"name");
            mock.title = Util.getCursorString(cursor,"title");
            mock.desc = Util.getCursorString(cursor,"description");
            mock.desc = Util.getCursorString(cursor,"description");
            mock.request = new MockRequest();
            mock.request.method = Util.getCursorString(cursor,"method");
            String host = Util.getCursorString(cursor, "host");
            if (!TextUtils.isEmpty(host)){
                mock.request.host = Arrays.asList(host.split(","));
            }
            mock.request.path = Util.getCursorString(cursor,"path");
        }
        return mock;

    }
}
