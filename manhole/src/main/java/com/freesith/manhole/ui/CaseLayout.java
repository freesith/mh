package com.freesith.manhole.ui;

import android.content.Context;
import android.os.Build;
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

import com.freesith.manhole.ManholeMock;
import com.freesith.manhole.R;
import com.freesith.manhole.bean.Case;
import com.freesith.manhole.bean.Flow;
import com.freesith.manhole.ui.adapter.CaseAdapter;
import com.freesith.manhole.ui.adapter.CaseChoiceAdapter;
import com.freesith.manhole.ui.util.ViewUtil;


public class CaseLayout extends LinearLayout implements CaseAdapter.CaseListener {

    public CaseLayout(Context context) {
        super(context);
        init(context);
    }

    public CaseLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CaseLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CaseLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private TextView tvName;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvClose;
    private RecyclerView rvCase;
    private Context context;

    private CaseChoiceAdapter caseChoiceAdapter;


    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_case, this);
        tvName = view.findViewById(R.id.manhole_tvName);
        tvTitle = view.findViewById(R.id.manhole_tvTitle);
        tvDesc = view.findViewById(R.id.manhole_tvDesc);
        tvClose = view.findViewById(R.id.manhole_tvClose);
        rvCase = view.findViewById(R.id.manhole_rvCase);
        rvCase.setLayoutManager(new LinearLayoutManager(context));
        caseChoiceAdapter = new CaseChoiceAdapter(context);
        caseChoiceAdapter.setCaseListener(this);
        rvCase.setAdapter(caseChoiceAdapter);

        tvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewGroup) getParent()).removeView(CaseLayout.this);
            }
        });
    }

    public void showCase(String caseName) {
        Case caze = ManholeMock.INSTANCE.getCaseByName(caseName);
        if (caze != null) {
            tvName.setText(caze.name);
            tvTitle.setText(caze.title);
            tvDesc.setText(caze.desc);
            caseChoiceAdapter.setChoiceList(caze.mocks);
            caseChoiceAdapter.notifyDataSetChanged();
        }
    }

    public void showFlow(String flowName) {
        Flow flow = ManholeMock.INSTANCE.getFlowByName(flowName);
        if (flow != null) {
            tvName.setText(flow.name);
            tvTitle.setText(flow.title);
            tvDesc.setText(flow.desc);
            caseChoiceAdapter.setCaseList(flow.cases);
            caseChoiceAdapter.setChoiceList(flow.mocks);
            caseChoiceAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCaseEnableChanged(String name, boolean enable, int position) {

    }

    @Override
    public void onCaseClick(String name) {
        CaseLayout caseLayout = new CaseLayout(context);
        ViewUtil.findCoverLayout(this).addView(caseLayout, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        caseLayout.showCase(name);
    }
}
