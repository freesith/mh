package com.freesith.manhole.ui;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freesith.manhole.R;
import com.freesith.manhole.ManholeMock;
import com.freesith.manhole.Util;
import com.freesith.manhole.bean.Mock;
import com.freesith.manhole.bean.MockChoice;
import com.freesith.manhole.ui.adapter.EnableChoiceAdapter;
import com.freesith.manhole.ui.adapter.MockChoiceAdapter;
import com.freesith.manhole.ui.util.ViewUtil;

import java.util.List;

//import com.freesith.jsonvision.JsonVisionView;

public class MockView extends LinearLayout implements EnableChoiceAdapter.ChoiceListener {

    private Context context;

    private TextView tvName;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvMethod;
    private TextView tvPath;
    private TextView tvHost;
    private RecyclerView rvChoice;

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
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.manhole_layout_mock, this);

        tvName = view.findViewById(R.id.manhole_tvName);
        tvTitle = view.findViewById(R.id.manhole_tvTitle);
        tvDesc = view.findViewById(R.id.manhole_tvDesc);
        tvMethod = view.findViewById(R.id.manhole_tvMethod);
        tvPath = view.findViewById(R.id.manhole_tvPath);
        tvHost = view.findViewById(R.id.manhole_tvHost);
        rvChoice = view.findViewById(R.id.manhole_rvChoice);

        findViewById(R.id.manhole_tvClose).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) getParent()).removeView(MockView.this);
            }
        });
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

        List<MockChoice> choices = ManholeMock.INSTANCE.getChoicesByMock(mock.name);
        MockChoiceAdapter adapter = new MockChoiceAdapter(context);
        rvChoice.setLayoutManager(new LinearLayoutManager(context));
        rvChoice.setAdapter(adapter);
        adapter.setList(choices);
        adapter.notifyDataSetChanged();

        adapter.setChoiceListener(this);
        String join = Util.join(mock.request.host, "\n");
        if (TextUtils.isEmpty(join)) {
            join = "*";
        }
        tvHost.setText(join);
    }

    @Override
    public void onChoiceEnableChanged(MockChoice choice, boolean enable, int position) {
        ManholeMock.INSTANCE.updateMockChoiceEnable(choice.mockName, choice.index, enable);
    }

    @Override
    public void onMockNameClick(String name) {
        //do nothing
    }

    @Override
    public void onChoiceClick(MockChoice mock) {
        ChoiceLayout choiceLayout = new ChoiceLayout(context);
        ViewUtil.findCoverLayout(this).addView(choiceLayout, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        choiceLayout.showChoice(mock);
    }
}
