package com.example.mox.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewStub;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mox.R;
import com.example.mox.db.bean.Mock;
import com.example.mox.ui.interfaces.MonitorListener;

public class MonitorView extends LinearLayout implements View.OnClickListener, MonitorListener {

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

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_monitor, this);

        ll_mock = view.findViewById(R.id.ll_mock);
        vbSetting = view.findViewById(R.id.vbSetting);
        v_mock = view.findViewById(R.id.v_mock);

        ll_mock.setMonitorListener(this);

        view.findViewById(R.id.tvClose).setOnClickListener(this);
        view.findViewById(R.id.tabSetting).setOnClickListener(this);
        view.findViewById(R.id.tabMock).setOnClickListener(this);

    }

    public void show() {
        ll_mock.showFlow();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvClose) {
            hideMonitorView();
        }  else if (v.getId() == R.id.tabSetting) {
            switchSetting();
        } else if (v.getId() == R.id.tabMock) {
            switchMock();
        }
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
    }

    private void switchMock() {
        if (settingView != null) {
            settingView.setVisibility(View.GONE);
        }
        ll_mock.setVisibility(View.VISIBLE);
        v_mock.setVisibility(GONE);
    }

    @Override
    public void onShowSingleMock(Mock mock) {
        v_mock.showMock(mock);
        v_mock.setVisibility(View.VISIBLE);
        ll_mock.setVisibility(View.GONE);
    }

}
