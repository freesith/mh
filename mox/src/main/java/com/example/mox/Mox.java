package com.example.mox;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alibaba.fastjson.JSON;
import com.example.mox.db.bean.Case;
import com.example.mox.db.bean.Flow;
import com.example.mox.db.bean.Mock;
import com.example.mox.db.bean.MockRequest;
import com.example.mox.db.bean.MockResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class Mox {

    public static final String TAG = "xxx_mox";

    public static final int FLAG_ENABLE = 1;
    public static final int FLAG_PASSIVE = 1 << 1;

    private SQLiteDatabase db;

    private static volatile Mox mox;
    private static volatile boolean inited = false;

    private ConcurrentHashMap<String, Flow> flowMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Case> caseMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Mock> mockMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, LinkedList<Mock>> enableMockMap = new ConcurrentHashMap<>();


    public static Mox getInstance() {
        if (mox == null) {
            mox = new Mox();
        }
        return mox;
    }

    private Mox() {
    }

    static void init() {
        if (inited) {
            return;
        }
        mox = new Mox();
        inited = true;
    }

    public synchronized void initDb(Context context, String dbPath) {
        if (db != null) {
            db.close();
        }
        SqliteHelper sqliteHelper = new SqliteHelper(context, dbPath, null, 1);
        db = sqliteHelper.getWritableDatabase();
        db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);

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
        LinkedList<Mock> mocks = enableMockMap.get(method + path);
        if (mocks == null || mocks.isEmpty()) {
            return null;
        }
        for (Mock mock : mocks) {
            if (mock.request != null && mock.request.matches(request)) {
                return mock.response;
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

        if (db == null) {
            return;
        }

        String mockSql = "SELECT * FROM table_case WHERE status& ? = ? OR status& ? = ?";
        Cursor cursor = db.rawQuery(mockSql, new String[]{String.valueOf(FLAG_PASSIVE), String.valueOf(FLAG_PASSIVE), String.valueOf(FLAG_ENABLE), String.valueOf(FLAG_ENABLE)});
        while (cursor.moveToNext()) {
            String method = cursor.getString(cursor.getColumnIndex("method")).toLowerCase();
            String path = cursor.getString(cursor.getColumnIndex("path"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String json = cursor.getString(cursor.getColumnIndex("json"));

            MockRequest mockRequest = new MockRequest();
            mockRequest.method = method;
            mockRequest.path = path;
            Mock mock = JSON.parseObject(json, Mock.class);
            LinkedList<Mock> mocks = enableMockMap.get(method + path);
            if (mocks == null) {
                mocks = new LinkedList<>();
                enableMockMap.put(method + path, mocks);
            }
            mocks.add(mock);
        }
        cursor.close();
    }

    public List<Mock> getMocks() {
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM table_mock ORDER BY status DESC", null);
        List<Mock> mockList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String json = cursor.getString(cursor.getColumnIndex("json"));

            Mock mock = JSON.parseObject(json, Mock.class);
            mock.name = name;
            mock.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            mock.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
            mockList.add(mock);
        }
        cursor.close();
        return mockList;
    }

    public List<Case> getCases() {
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM table_case ORDER BY status DESC", null);
        List<Case> caseList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String mocks = cursor.getString(cursor.getColumnIndex("mocks"));
            String title = cursor.getString(cursor.getColumnIndex("title"));

            Case caze = new Case();
            caze.name = name;
            caze.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            caze.passive = (status & FLAG_PASSIVE) == FLAG_PASSIVE;
            caze.mocks = Arrays.asList(mocks.split(","));
            caze.title = title;
            caseList.add(caze);
        }
        cursor.close();
        return caseList;
    }

    public List<Flow> getFlows() {
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM table_flow ORDER BY status DESC", null);
        List<Flow> flowList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String mocks = cursor.getString(cursor.getColumnIndex("mocks"));
            String cases = cursor.getString(cursor.getColumnIndex("cases"));
            String title = cursor.getString(cursor.getColumnIndex("title"));

            Flow flow = new Flow();
            flow.name = name;
            flow.enable = (status & FLAG_ENABLE) == FLAG_ENABLE;
            flow.mocks = Arrays.asList(mocks.split(","));
            flow.cases = Arrays.asList(cases.split(","));
            flow.title = title;
            flowList.add(flow);
        }
        cursor.close();
        return flowList;
    }


    public void updateFlowEnable(String flowName, boolean enable) {
        if (db == null) {
            return ;
        }
        if (enable) {
            db.execSQL("UPDATE table_flow SET status = status | ? WHERE name = ?", new Object[]{FLAG_ENABLE, flowName});
        } else {
            db.execSQL("UPDATE table_flow SET status = status & ? WHERE name = ?", new Object[]{~FLAG_ENABLE, flowName});
        }
        refreshPassive();
        refreshMocks();
    }

    public void updateCaseEnable(String caseName, boolean enable) {
        if (db == null) {
            return ;
        }
        if (enable) {
            db.execSQL("UPDATE table_case SET status = status | ? WHERE name = ?", new Object[]{FLAG_ENABLE, caseName});
        } else {
            db.execSQL("UPDATE table_case SET status = status & ? WHERE name = ?", new Object[]{~FLAG_ENABLE, caseName});
        }
        refreshPassive();
        refreshMocks();
    }

    public void updateMockEnable(String mockName, boolean enable) {
        if (db == null) {
            return ;
        }
        if (enable) {
            db.execSQL("UPDATE table_mock SET status = status | ? WHERE name = ?", new Object[]{FLAG_ENABLE, mockName});
        } else {
            db.execSQL("UPDATE table_mock SET status = status & ? WHERE name = ?", new Object[]{~FLAG_ENABLE, mockName});
        }
        refreshMocks();
    }


    private void refreshPassive() {
        if (db == null) {
            return ;
        }

        HashSet<String> passiveCases = new HashSet<>();
        HashSet<String> passiveMocks = new HashSet<>();

        Cursor cursorFlow = db.rawQuery("SELECT * FROM table_flow WHERE status&" + FLAG_ENABLE + " = " + FLAG_ENABLE, null);
        while (cursorFlow.moveToNext()) {
            String cases = cursorFlow.getString(cursorFlow.getColumnIndex("cases"));
            passiveCases.addAll(Arrays.asList(cases.split(",")));
            String mocks = cursorFlow.getString(cursorFlow.getColumnIndex("mocks"));
            passiveMocks.addAll(Arrays.asList(mocks.split(",")));
        }
        cursorFlow.close();

        String passiveCaseNames = Util.setToSelection(passiveCases);
        String sql = "UPDATE table_case SET status = CASE \n" +
                "WHEN name IN (" + passiveCaseNames + ") THEN \n" +
                "status | " + FLAG_PASSIVE + " \n" +
                "ELSE\n" +
                "status & " + ~FLAG_PASSIVE + " \n" +
                "END;";
        db.execSQL(sql, new Object[]{});
        Cursor cursorCases = db.rawQuery("SELECT * FROM table_case WHERE status& " + FLAG_ENABLE + " = " + FLAG_ENABLE + " OR status& " + FLAG_PASSIVE + " = " + FLAG_PASSIVE, null);
        while (cursorCases.moveToNext()) {
            String mocks = cursorCases.getString(cursorCases.getColumnIndex("mocks"));
            passiveMocks.addAll(Arrays.asList(mocks.split(",")));
        }
        cursorCases.close();

        String passiveMockNames = Util.setToSelection(passiveMocks);
        db.execSQL("UPDATE table_mock SET status = CASE \n" +
                "WHEN name IN ("+passiveMockNames+") THEN \n" +
                "status | "+FLAG_PASSIVE+" \n" +
                "ELSE \n" +
                "status& "+~FLAG_PASSIVE+" \n" +
                "END;", new Object[]{});
    }


    private void lockCases(String names) {

    }

}
