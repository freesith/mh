package com.freesith.manhole.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.freesith.manhole.R;
import com.freesith.manhole.bean.Mock;
import com.freesith.manhole.history.CrashDetailView;
import com.freesith.manhole.history.CrashListView;
import com.freesith.manhole.history.HistoryDetailView;
import com.freesith.manhole.history.HistoryListView;
import com.freesith.manhole.ui.interfaces.MonitorListener;
import com.freesith.manhole.ui.util.ViewUtil;

public class MonitorView extends LinearLayout implements View.OnClickListener, MonitorListener {

    private Context context;

    public MonitorView(Context context) {
        super(context);
        init(context);
    }

    public MonitorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MonitorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MonitorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private MockListLayout ll_mock;
    private SettingView settingView;
    private MockView v_mock;
    private ViewStub vbSetting;
    private HistoryListView v_history;
    private CrashListView vCrashList;

    private TextView tabMock;
    private TextView tabSetting;
    private TextView tabHistory;
    private TextView tabCrash;

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.manhole_layout_monitor, this);

        ll_mock = view.findViewById(R.id.manhole_ll_mock);
        vbSetting = view.findViewById(R.id.manhole_vbSetting);
        v_mock = view.findViewById(R.id.manhole_v_mock);
        tabMock = view.findViewById(R.id.manhole_tabMock);
        tabSetting = view.findViewById(R.id.manhole_tabSetting);
        tabHistory = view.findViewById(R.id.manhole_tabHistory);
        v_history = view.findViewById(R.id.manhole_v_history);
        tabCrash = view.findViewById(R.id.manhole_tabCrash);
        vCrashList = view.findViewById(R.id.manhole_vCrash);

        ll_mock.setMonitorListener(this);
        v_history.setMonitorListener(this);
        vCrashList.setMonitorListener(this);

        view.findViewById(R.id.manhole_tvClose).setOnClickListener(this);
        tabSetting.setOnClickListener(this);
        tabMock.setOnClickListener(this);
        tabHistory.setOnClickListener(this);
        tabCrash.setOnClickListener(this);
    }

    public void show() {
        ll_mock.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.manhole_tvClose) {
            if (context instanceof SettingActivity) {
                ((Activity) context).onBackPressed();
            } else {
                hideMonitorView();
            }
        } else if (v.getId() == R.id.manhole_tabSetting) {
            switchSetting();
            updateTabs(v);
        } else if (v.getId() == R.id.manhole_tabMock) {
            switchMock();
            updateTabs(v);
        } else if (v.getId() == R.id.manhole_tabHistory) {
            switchHistory();
            updateTabs(v);
        } else if (v.getId() == R.id.manhole_tabCrash) {
            switchCrash();
            updateTabs(v);
        }
    }


    private void updateTabs(View v) {
        tabMock.setBackgroundColor(v.getId() == R.id.manhole_tabMock ? Color.WHITE : Color.TRANSPARENT);
        tabSetting.setBackgroundColor(v.getId() == R.id.manhole_tabSetting ? Color.WHITE : Color.TRANSPARENT);
        tabHistory.setBackgroundColor(v.getId() == R.id.manhole_tabHistory ? Color.WHITE : Color.TRANSPARENT);
        tabCrash.setBackgroundColor(v.getId() == R.id.manhole_tabCrash ? Color.WHITE : Color.TRANSPARENT);
    }

    private void hideMonitorView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(this, getWidth() / 2, getHeight() / 2, getHeight(), 0);
            circularReveal.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    setVisibility(View.GONE);

                }
            });
            circularReveal.setDuration(500).start();
        } else {
            setVisibility(View.INVISIBLE);
        }
    }

    private void switchSetting() {
        if (settingView == null) {
            vbSetting.inflate();
            settingView = findViewById(R.id.manhole_vSetting);
        }
        ll_mock.setVisibility(View.GONE);
        vCrashList.setVisibility(View.GONE);
        settingView.setVisibility(View.VISIBLE);
        v_history.setVisibility(View.GONE);
    }

    private void switchMock() {
        if (settingView != null) {
            settingView.setVisibility(View.GONE);
        }
        ll_mock.setVisibility(View.VISIBLE);
        v_mock.setVisibility(GONE);
        vCrashList.setVisibility(View.GONE);
        v_history.setVisibility(View.GONE);
    }


    private void switchHistory() {
        if (settingView != null) {
            settingView.setVisibility(View.GONE);
        }
        ll_mock.setVisibility(View.GONE);
        v_mock.setVisibility(View.GONE);
        vCrashList.setVisibility(View.GONE);
        v_history.setVisibility(View.VISIBLE);
        v_history.show();
    }

    private void switchCrash() {
        if (settingView != null) {
            settingView.setVisibility(View.GONE);
        }
        ll_mock.setVisibility(View.GONE);
        v_mock.setVisibility(View.GONE);
        v_history.setVisibility(View.GONE);
        vCrashList.setVisibility(View.VISIBLE);
        vCrashList.show();
    }

    @Override
    public void onShowSingleMock(Mock mock) {
        MockView mockView = new MockView(context);
        ViewUtil.findCoverLayout(this).addView(mockView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mockView.showMock(mock);
    }

    @Override
    public void onShowHistoryDetail(int historyId) {
        HistoryDetailView historyDetailView = new HistoryDetailView(context);
        ViewUtil.findCoverLayout(this).addView(historyDetailView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        historyDetailView.showHistory(historyId);
    }

    @Override
    public void onShowCrashDetail(int crashId) {
        CrashDetailView crashDetailView = new CrashDetailView(context);
        ViewUtil.findCoverLayout(this).addView(crashDetailView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        crashDetailView.showCrash(crashId);
    }
}
