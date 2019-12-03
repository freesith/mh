package com.freesith.manhole.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.freesith.manhole.Mox;
import com.example.mox.R;
import com.freesith.manhole.db.bean.Case;
import com.freesith.manhole.db.bean.Flow;
import com.freesith.manhole.db.bean.Mock;
import com.freesith.manhole.db.bean.MockChoice;
import com.freesith.manhole.ui.adapter.CaseAdapter;
import com.freesith.manhole.ui.adapter.ChoiceAdapter;
import com.freesith.manhole.ui.adapter.FlowAdapter;
import com.freesith.manhole.ui.adapter.MockAdapter;
import com.freesith.manhole.ui.interfaces.MonitorListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MockListLayout extends LinearLayout implements View.OnClickListener, FlowAdapter.FlowListener, CaseAdapter.CaseListener, MockAdapter.MockListener, ChoiceAdapter.ChoiceListener {

    private Context context;

    private RecyclerView rvMock;
    private MockAdapter mockAdapter;
    private CaseAdapter caseAdapter;
    private FlowAdapter flowAdapter;
    private ChoiceAdapter enableAdapter;

    private TextView tvEnable;
    private TextView tvFlow;
    private TextView tvCase;
    private TextView tvMock;

    private MonitorListener monitorListener;


    public MockListLayout(Context context) {
        super(context);
        init(context);
    }

    public MockListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MockListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MockListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    public void setMonitorListener(MonitorListener monitorListener) {
        this.monitorListener = monitorListener;
    }

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_mock_list, this);

        rvMock = view.findViewById(R.id.rvMock);
        tvFlow = view.findViewById(R.id.tvFlow);
        tvCase = view.findViewById(R.id.tvCase);
        tvMock = view.findViewById(R.id.tvMock);
        tvEnable = view.findViewById(R.id.tvEnable);

        tvFlow.setOnClickListener(this);
        tvCase.setOnClickListener(this);
        tvMock.setOnClickListener(this);
        tvEnable.setOnClickListener(this);

        rvMock.setLayoutManager(new LinearLayoutManager(context));
    }

    public void show() {
        showEnable();
        updateButtons(tvEnable);
    }

    private void showEnable() {
        if (enableAdapter == null) {
            enableAdapter = new ChoiceAdapter(context);
            enableAdapter.setChoiceListener(this);
        }
        ConcurrentHashMap<String, LinkedList<MockChoice>> enableMockMap = Mox.getInstance().enableMockMap;
        List<MockChoice> choiceList = new ArrayList<>();
        if (!enableMockMap.isEmpty()) {
            for(LinkedList linkedList : enableMockMap.values()) {
                choiceList.addAll(linkedList);
            }
        }
        enableAdapter.setList(choiceList);
        rvMock.setAdapter(enableAdapter);
        enableAdapter.notifyDataSetChanged();

    }

    private void showFlow() {
        if (flowAdapter == null) {
            flowAdapter = new FlowAdapter(context);
            flowAdapter.setFlowListener(this);
        }
        List<Flow> flows = Mox.getInstance().getFlows();
        flowAdapter.setList(flows);
        rvMock.setAdapter(flowAdapter);
        flowAdapter.notifyDataSetChanged();
    }

    private void showCase() {
        if (caseAdapter == null) {
            caseAdapter = new CaseAdapter(context);
            caseAdapter.setCaseListener(this);
        }
        List<Case> cases = Mox.getInstance().getCases();
        caseAdapter.setList(cases);
        rvMock.setAdapter(caseAdapter);
        caseAdapter.notifyDataSetChanged();
    }

    private void showMock() {
        if (mockAdapter == null) {
            mockAdapter = new MockAdapter(context);
            mockAdapter.setMockListener(this);
        }
        List<Mock> mocks = Mox.getInstance().getMocks();
        mockAdapter.setList(mocks);
        rvMock.setAdapter(mockAdapter);
        mockAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvFlow) {
            showFlow();
            updateButtons(v);
        } else if (v.getId() == R.id.tvCase) {
            showCase();
            updateButtons(v);
        } else if (v.getId() == R.id.tvMock) {
            showMock();
            updateButtons(v);
        } else if (v.getId() == R.id.tvEnable) {
            showEnable();
            updateButtons(v);
        }


    }

    private void updateButtons(View v) {
        tvCase.setBackgroundColor(v.getId() == R.id.tvCase ? Color.WHITE : Color.TRANSPARENT);
        tvEnable.setBackgroundColor(v.getId() == R.id.tvEnable ? Color.WHITE : Color.TRANSPARENT);
        tvFlow.setBackgroundColor(v.getId() == R.id.tvFlow ? Color.WHITE : Color.TRANSPARENT);
        tvMock.setBackgroundColor(v.getId() == R.id.tvMock ? Color.WHITE : Color.TRANSPARENT);
    }

    @Override
    public void onFlowEnableChanged(String name, boolean enable, int position) {
        Flow flow = flowAdapter.get(position);
        if (flow != null) {
            Mox.getInstance().updateFlowEnable(name, enable);
            flow.enable = enable;
        }
    }

    @Override
    public void onCaseEnableChanged(String name, boolean enable, int position) {
        Case caze = caseAdapter.get(position);
        if (caze != null) {
            Mox.getInstance().updateCaseEnable(name, enable);
            caze.enable = enable;
        }
    }

    @Override
    public void onMockEnableChanged(String name, boolean enable, int position) {
        Mock mock = mockAdapter.get(position);
        if (mock != null) {
            Mox.getInstance().updateMockEnable(name, enable);
            mock.enable = enable;
        }
    }

    @Override
    public void onMockClick(Mock mock) {
        if (monitorListener != null) {
            monitorListener.onShowSingleMock(mock);
        }
    }

    @Override
    public void onChoiceEnableChanged(MockChoice choice, boolean enable, int position) {
        Mox.getInstance().updateMockChoiceEnable(choice.mockName, choice.index, enable);
    }

    @Override
    public void onChoiceClick(MockChoice mock) {

    }
}
