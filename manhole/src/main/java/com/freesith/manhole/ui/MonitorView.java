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
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.freesith.manhole.R;
import com.freesith.manhole.bean.Mock;
import com.freesith.manhole.history.AllHistoryView;
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
    private AllHistoryView v_history;

    private TextView tabMock;
    private TextView tabSetting;
    private TextView tabHistory;

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_monitor, this);

        ll_mock = view.findViewById(R.id.ll_mock);
        vbSetting = view.findViewById(R.id.vbSetting);
        v_mock = view.findViewById(R.id.v_mock);
        tabMock = view.findViewById(R.id.tabMock);
        tabSetting = view.findViewById(R.id.tabSetting);
        tabHistory = view.findViewById(R.id.tabHistory);
        v_history = view.findViewById(R.id.v_history);

        ll_mock.setMonitorListener(this);

        view.findViewById(R.id.tvClose).setOnClickListener(this);
        tabSetting.setOnClickListener(this);
        tabMock.setOnClickListener(this);
        tabHistory.setOnClickListener(this);

    }

    public void show() {
        ll_mock.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvClose) {
            if (context instanceof SettingActivity) {
                ((Activity) context).onBackPressed();
            } else {
                hideMonitorView();
            }
        } else if (v.getId() == R.id.tabSetting) {
            switchSetting();
            updateTabs(v);
        } else if (v.getId() == R.id.tabMock) {
            switchMock();
            updateTabs(v);
        } else if (v.getId() == R.id.tabHistory) {
            switchHistory();
            updateTabs(v);
        }
    }

    private void updateTabs(View v) {
        tabMock.setBackgroundColor(v.getId() == R.id.tabMock ? Color.WHITE : Color.TRANSPARENT);
        tabSetting.setBackgroundColor(v.getId() == R.id.tabSetting ? Color.WHITE : Color.TRANSPARENT);
        tabHistory.setBackgroundColor(v.getId() == R.id.tabHistory ? Color.WHITE : Color.TRANSPARENT);
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
            settingView = findViewById(R.id.vSetting);
        }
        ll_mock.setVisibility(View.GONE);
        settingView.setVisibility(View.VISIBLE);
        v_history.setVisibility(View.GONE);
    }

    private void switchMock() {
        if (settingView != null) {
            settingView.setVisibility(View.GONE);
        }
        ll_mock.setVisibility(View.VISIBLE);
        v_mock.setVisibility(GONE);
        v_history.setVisibility(View.GONE);
    }


    private void switchHistory() {
        if (settingView != null) {
            settingView.setVisibility(View.GONE);
        }
        ll_mock.setVisibility(View.GONE);
        v_mock.setVisibility(View.GONE);
        v_history.setVisibility(View.VISIBLE);
        v_history.show();
    }

    @Override
    public void onShowSingleMock(Mock mock) {
        MockView mockView = new MockView(context);
        ContainerLayout coverLayout = ViewUtil.findCoverLayout(this);
        if (coverLayout != null) {
            coverLayout.addView(mockView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        } else {
            ((ViewGroup)getParent()).addView(mockView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        }
        mockView.showMock(mock);
    }

}
