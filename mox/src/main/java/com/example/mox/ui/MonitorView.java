package com.example.mox.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mox.Mox;
import com.example.mox.R;
import com.example.mox.db.bean.Mock;

import java.util.List;

public class MonitorView extends FrameLayout implements View.OnClickListener {

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

    private RecyclerView rvMock;
    private MockAdapter mockAdapter;

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_monitor, this);
        view.findViewById(R.id.tvClose).setOnClickListener(this);

        List<Mock> mocks = Mox.getInstance().getMocks();
        rvMock = view.findViewById(R.id.rvMock);
        mockAdapter = new MockAdapter(context);
        rvMock.setLayoutManager(new LinearLayoutManager(context));
        rvMock.setAdapter(mockAdapter);

        mockAdapter.setMockList(mocks);
        mockAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvClose) {
            hideMonitorView();
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
}
