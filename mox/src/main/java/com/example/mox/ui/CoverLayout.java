package com.example.mox.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mox.R;

public class CoverLayout extends FrameLayout implements View.OnTouchListener {

    private static final String TAG = "xxx";

    private float downX = -1;
    private float downY = -1;
    private Point left = new Point();
    private Point right = new Point();
    private Point top = new Point();
    private Point bottom = new Point();
    private float lastX;
    private float lastY;
    private int lastAction;

    private MonitorView vMonitorView;

    public CoverLayout(Context context) {
        super(context);
        init(context);
    }

    public CoverLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CoverLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CoverLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cover, this);
        setOnTouchListener(this);
        vMonitorView = view.findViewById(R.id.vMonitor);
    }

    private void showMonitorView(int left, int top, int right, int bottom) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(vMonitorView, (left + right) / 2, (top + bottom) / 2, (bottom - top + right - left) / 4, getHeight());
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    vMonitorView.show();
                    vMonitorView.setVisibility(View.VISIBLE);
                }

            });
            circularReveal.setDuration(1000).start();
        } else {
            vMonitorView.show();
            vMonitorView.setVisibility(View.VISIBLE);
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (vMonitorView.getVisibility() == View.VISIBLE) {
            return super.dispatchTouchEvent(event);
        }

        if (lastAction == event.getAction() && lastX == event.getX() && lastY == event.getY()) {
            return super.dispatchTouchEvent(event);
        } else {
            lastAction = event.getAction();
            lastX = event.getX();
            lastY = event.getY();
            if (lastAction == MotionEvent.ACTION_UP) {
                return super.dispatchTouchEvent(event);
            }
            ((View) getParent()).dispatchTouchEvent(event);
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                resetDots(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                if (downX == -1 || downY == -1) {
                    return false;
                }

                if (x < left.x) {
                    left.set(x, y);
                }
                if (x > right.x) {
                    right.set(x, y);
                }
                if (y < top.y) {
                    top.set(x, y);
                }
                if (y > bottom.y) {
                    bottom.set(x, y);
                }
                break;

            case MotionEvent.ACTION_UP:
                downX = -1;
                downY = -1;
                checkCircle();

                break;
        }
        return false;
    }


    private void checkCircle() {
        int width = right.x - left.x;
        int height = bottom.y - top.y;

        if (width < 100 || height < 100) {
            return;
        }

        if (Math.abs(width - height) < Math.max(height, width) * 0.3f) {
            if (Math.abs(top.y + height / 2 - left.y) < height * 0.3f && Math.abs(top.y + height / 2 - right.y) < height * 0.3f) {
                if (Math.abs(left.x + width / 2 - top.x) < width * 0.3f && Math.abs(left.x + width / 2 - bottom.x) < width * 0.3f) {
                    showMonitorView(left.x, top.y, right.x, bottom.y);
                }
            }
        }
    }

    private void resetDots(int x, int y) {
        left.set(x, y);
        right.set(x, y);
        top.set(x, y);
        bottom.set(x, y);
    }
}
