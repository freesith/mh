package com.example.mox;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.mox.db.bean.Mock;
import com.example.mox.db.bean.MockRequest;
import com.example.mox.db.bean.MockResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

public class Mox {

    public static final String TAG = "xxx_mox";

    private SQLiteDatabase db;

    private static volatile Mox mox;
    private static volatile boolean inited = false;

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

        refreshMocks();
    }


    public void log(Request request, Response response) {

    }



    public MockResponse mock (Request request) {
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

        Set<String> enableCases = new HashSet<>();
        Set<String> enableMocks = new HashSet<>();

        Cursor cursorFlow = db.rawQuery("SELECT * FROM table_flow WHERE status = 1", null);
        while (cursorFlow.moveToNext()) {
            String cases = cursorFlow.getString(cursorFlow.getColumnIndex("cases"));
            String[] splitCases = cases.split(",");
            enableCases.addAll(Arrays.asList(splitCases));
            String mocks = cursorFlow.getString(cursorFlow.getColumnIndex("mocks"));
            String[] splitMocks = mocks.split(",");
            enableMocks.addAll(Arrays.asList(splitMocks));
        }
        cursorFlow.close();


        String caseSql;
        if (!enableCases.isEmpty()) {
            boolean first = true;
            StringBuilder builder = new StringBuilder();
            for (String caseName : enableCases) {
                if (!first) {
                    builder.append(",");
                } else {
                    first = false;
                }
                builder.append("'").append(caseName).append("'");
            }
            String caseSelection = builder.toString();
            caseSql = "SELECT DISTINCT mocks FROM table_case WHERE status = 1 OR name IN (" + caseSelection + ")";
        } else {
            caseSql = "SELECT DISTINCT mocks FROM table_case WHERE status = 1";
        }
        Cursor cursorCase = db.rawQuery(caseSql, null);
        while (cursorCase.moveToNext()) {
            String mocks = cursorCase.getString(cursorCase.getColumnIndex("mocks"));
            String[] splitMocks = mocks.split(",");
            enableMocks.addAll(Arrays.asList(splitMocks));
        }
        cursorCase.close();


        String mockSql;
        if (!enableMocks.isEmpty()) {
            boolean first = true;
            StringBuilder builder = new StringBuilder();
            for (String mockName : enableMocks) {
                if (!first) {
                    builder.append(",");
                } else {
                    first = false;
                }
                builder.append("'").append(mockName).append("'");
            }
            String mockSelection = builder.toString();
            mockSql = "SELECT * FROM table_mock WHERE status = 1 OR name IN (" + mockSelection + ")";
        } else {
            mockSql = "SELECT * FROM table_case WHERE status = 1";
        }

        Cursor cursor = db.rawQuery(mockSql, null);
        while (cursor.moveToNext()) {
            String method = cursor.getString(cursor.getColumnIndex("method")).toLowerCase();
            String path = cursor.getString(cursor.getColumnIndex("path"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String json = cursor.getString(cursor.getColumnIndex("json"));

            MockRequest mockRequest = new MockRequest();
            mockRequest.method = method;
            mockRequest.path = path;
            Mock mock = JSON.parseObject(json, Mock.class);

            //TODO 冲突的检查和回退
            //TODO 2019-11-11 by WangChao 检查是否存在
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
        Cursor cursor = db.rawQuery("SELECT * FROM table_mock ORDER BY status DESC", null);
        List<Mock> mockList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name")).toLowerCase();
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            String json = cursor.getString(cursor.getColumnIndex("json"));

            Mock mock = JSON.parseObject(json, Mock.class);
            mock.name = name;
            mock.enable = (status & 1) == 1;
            mockList.add(mock);
        }
        cursor.close();
        return mockList;
    }
}
