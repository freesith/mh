package com.freesith.manhole.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.freesith.manhole.R;
import com.freesith.manhole.history.HistoryShortcut;
import com.freesith.manhole.util.ManholeSp;

public class CoverLayout extends ContainerLayout{

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
    private Context context;
    private boolean enableSummon = ManholeSp.INSTANCE.getEnableSummon();

    private MonitorView vMonitorView;
    private HistoryShortcut vHistoryShortcut;

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

    private void init(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cover, this);
        vMonitorView = view.findViewById(R.id.vMonitor);
        vHistoryShortcut = view.findViewById(R.id.vHistoryShortcut);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (h > 0 && Build.VERSION.SDK_INT >= 17) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                Point point = new Point();
                Display defaultDisplay = windowManager.getDefaultDisplay();
                if (defaultDisplay != null) {
                    defaultDisplay.getRealSize(point);
                    if (h == point.y) {
                        Resources resources = context.getResources();
                        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
                        int height = resources.getDimensionPixelSize(resourceId);
                        setPadding(0, height, 0 ,0);
                    } else {
                        setPadding(0, 0, 0, 0);
                    }
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!enableSummon) {
            return super.dispatchTouchEvent(event);
        }
        if (vMonitorView.getVisibility() == View.VISIBLE) {
            return super.dispatchTouchEvent(event);
        }
        ViewGroup parent = (ViewGroup) getParent();
        int childCount = parent.getChildCount();
        for (int i = childCount - 1; i >= 0 ; i--) {
            View child = parent.getChildAt(i);
            if (child != null
                    && child != this
                    && child.getVisibility() == View.VISIBLE
                    && child.dispatchTouchEvent(event)) {
                break;
            }
        }
        onCoverTouch(event);
        return true;
    }

    private void onCoverTouch(MotionEvent event) {
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
                    return;
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

    public void onStart() {
        vHistoryShortcut.onStart();
    }

    public void onStop() {
        vHistoryShortcut.onStop();
    }
}
