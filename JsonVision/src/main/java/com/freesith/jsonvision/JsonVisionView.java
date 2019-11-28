package com.freesith.jsonvision;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.liuxiangdong.jsonview.DefaultJsonView;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Iterator;


public class JsonVisionView extends LinearLayout {

    private TextView tv_json;
    DefaultJsonView rv_json;

    public JsonVisionView(Context context) {
        super(context);
        init(context);
    }

    public JsonVisionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JsonVisionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public JsonVisionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_json_vision, this);
        rv_json = view.findViewById(R.id.rv_json);

    }

    public void showJson(String json) {
        JSONTokener jsonTokener = new JSONTokener(json);
        JSONObject  jsonObject = null;
        try {
            jsonObject = (JSONObject) jsonTokener.nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rv_json.setJson(json);

        if (jsonObject  == null) {
            return;
        }

        Iterator<String> keys = jsonObject.keys();

    }

    private void parseJsonObject(JSONObject jsonObject) {

    }

    private void parseJsonArray(JSONArray jsonArray) {

    }


}
