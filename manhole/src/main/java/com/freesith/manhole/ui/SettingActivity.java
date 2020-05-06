package com.freesith.manhole.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class SettingActivity extends Activity {

    private MonitorView monitorView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContainerLayout containerLayout = new ContainerLayout(this);
        monitorView = new MonitorView(this);
        containerLayout.addView(monitorView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        setContentView(containerLayout);
        monitorView.show();
    }

}
