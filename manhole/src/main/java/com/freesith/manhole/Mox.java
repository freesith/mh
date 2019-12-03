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
import com.freesith.manhole.db.bean.Case;
import com.freesith.manhole.db.bean.Flow;
import com.freesith.manhole.db.bean.Mock;
import com.freesith.manhole.db.bean.MockChoice;
import com.freesith.manhole.db.bean.MockRequest;
import com.freesith.manhole.db.bean.MockResponse;

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

    public static final int ACTION_SHOW_MOCK = 1;

    private SQLiteDatabase db;

    private static volatile Mox mox;
    private static volatile boolean inited = false;

    private Handler handler;


    public ConcurrentHashMap<String, LinkedList<MockChoice>> enableMockMap = new ConcurrentHashMap<>();


    public Sp sp;

    public static Mox getInstance() {
        if (mox == null) {
            mox = new Mox();
        }
        return mox;
    }

    private Mox() {
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
        mox = new Mox();
        inited = true;
    }

    public synchronized void initDb(Context context, String dbPath) {
        Log.d(TAG, "initDb path = " + dbPath);
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

        if (db == null) {
            return;
        }

        String mockSql = "SELECT * FROM table_mock WHERE status& " + FLAG_PASSIVE + " = " + FLAG_PASSIVE + " OR status& " + FLAG_ENABLE + " = " + FLAG_ENABLE;
        Cursor cursor = db.rawQuery(mockSql, null);
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
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT DISTINCT name, title, description, method, path FROM table_mock ORDER BY status DESC", null);
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
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT DISTINCT name, title ,status FROM table_case ORDER BY status DESC", null);
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
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT DISTINCT name,title,status FROM table_flow ORDER BY status DESC", null);
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
        if (db == null) {
            return;
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
            return;
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
            return;
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
            return;
        }

        HashSet<String> passiveCases = new HashSet<>();
        HashSet<String> passiveChoices = new HashSet<>();

        //找出开启的flow
        Cursor cursorFlow = db.rawQuery("SELECT * FROM table_flow WHERE status&" + FLAG_ENABLE + " = " + FLAG_ENABLE, null);
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
                "WHEN name IN (" + passiveCaseNames + ") THEN \n" +
                "status | " + FLAG_PASSIVE + " \n" +
                "ELSE\n" +
                "status & " + ~FLAG_PASSIVE + " \n" +
                "END;";
        db.execSQL(sql, new Object[]{});
        Cursor cursorCases = db.rawQuery("SELECT * FROM table_case WHERE status& " + FLAG_ENABLE + " = " + FLAG_ENABLE + " OR status& " + FLAG_PASSIVE + " = " + FLAG_PASSIVE, null);
        while (cursorCases.moveToNext()) {
            String choice = cursorCases.getString(cursorCases.getColumnIndex("choice"));
            if (!TextUtils.isEmpty(choice)) {
                passiveChoices.add(choice);
            }
        }
        cursorCases.close();

        String passiveMockNames = Util.setToSelection(passiveChoices);
        db.execSQL("UPDATE table_mock SET status = CASE \n" +
                "WHEN choice IN (" + passiveMockNames + ") THEN \n" +
                "status | " + FLAG_PASSIVE + " \n" +
                "ELSE \n" +
                "status& " + ~FLAG_PASSIVE + " \n" +
                "END;", new Object[]{});
    }


    private void lockCases(String names) {

    }

    public List<MockChoice> getChoicesByMock(String name) {
        if (db == null) {
            return null;
        }
        String sql = "SELECT * FROM table_mock WHERE name='" + name + "'";
        Cursor cursor = db.rawQuery(sql, null);
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
        return list;
    }

    public void  updateMockChoiceEnable(String mockName, int index, boolean enable) {
        if (db == null) {
            return;
        }
        if (enable) {
            db.execSQL("UPDATE table_mock SET status = status | ? WHERE name = ? AND choice = ?", new Object[]{FLAG_ENABLE, mockName, mockName + "_" + index});
        } else {
            db.execSQL("UPDATE table_mock SET status = status & ? WHERE name = ? AND choice = ?", new Object[]{~FLAG_ENABLE, mockName, mockName + "_" + index});
        }
        refreshMocks();
    }
}
