package com.freesith.manhole.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.freesith.manhole.Mox;
import com.example.mox.R;
import com.freesith.manhole.Util;
import com.freesith.manhole.db.bean.Mock;
//import com.freesith.jsonvision.JsonVisionView;

import java.util.HashMap;
import java.util.Map;

public class MockView extends LinearLayout {

    private TextView tvName;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvMethod;
    private TextView tvPath;
    private Switch switchMock;
    private TextView tvHost;
    private TextView tvQuery;

//    private JsonVisionView v_json;

    public MockView(Context context) {
        super(context);
        init(context);
    }

    public MockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mock, this);

        tvName = view.findViewById(R.id.tvName);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvDesc = view.findViewById(R.id.tvDesc);
        tvMethod = view.findViewById(R.id.tvMethod);
        tvPath = view.findViewById(R.id.tvPath);
        switchMock = view.findViewById(R.id.switchMock);
        tvHost = view.findViewById(R.id.tvHost);
        tvQuery = view.findViewById(R.id.tvQuery);
//        v_json = view.findViewById(R.id.v_json);

    }

    public void showMock(final Mock mock) {
        tvName.setText(mock.name);
        tvTitle.setText(mock.title);
        tvDesc.setText(mock.desc);
        tvMethod.setText(mock.request.method);
        tvPath.setText(mock.request.path);

        if ("get".equalsIgnoreCase(mock.request.method)) {
            tvMethod.setBackgroundResource(R.drawable.manhole_left_circle_get);
            tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_get);
        } else {
            tvMethod.setBackgroundResource(R.drawable.manhole_left_circle_post);
            tvPath.setBackgroundResource(R.drawable.manhole_right_circle_stroke_post);
        }

        switchMock.setOnCheckedChangeListener(null);
        switchMock.setChecked(mock.enable);
        if (mock.enable) {
            switchMock.setEnabled(false);
            switchMock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Mox.getInstance().updateMockEnable(mock.name, isChecked);
                    mock.enable = isChecked;
                }
            });
        }

        tvHost.setText(Util.join(mock.request.host, "\n"));
        HashMap<String, String> query = mock.request.query;
        if (query != null && query.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String,String> entry: query.entrySet()) {
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(entry.getKey()).append(" : ").append(entry.getValue());
            }
            tvQuery.setText(builder.toString());
        }

//        v_json.showJson(mock.response.data);

    }
}
