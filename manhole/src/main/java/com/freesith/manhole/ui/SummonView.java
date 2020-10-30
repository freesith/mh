package com.freesith.manhole.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class SummonView extends View implements View.OnTouchListener {

    public static final String TAG = "manhole_summon";
    private float downX = -1;
    private float downY = -1;
    private Point left = new Point();
    private Point right = new Point();
    private Point top = new Point();
    private Point bottom = new Point();

    private int direction = -1;

    private Path path = new Path();
    private Paint paint = new Paint();

    private Context context;
    private OnDrawCircleListener onDrawCircleListener;

    public void setOnDrawCircleListener(OnDrawCircleListener onDrawCircleListener) {
        this.onDrawCircleListener = onDrawCircleListener;
    }

    public SummonView(Context context) {
        super(context);
        init(context);
    }

    public SummonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SummonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SummonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOnTouchListener(this);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(30);
    }

    private float lastX;
    private float lastY;
    private int lastAction;


    private ViewGroup content;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        content = (ViewGroup) getParent().getParent();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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
                path.rewind();
                path.moveTo(x, y);
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

                path.lineTo(x, y);
                path.moveTo(x, y);
                invalidate();
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

        if (Math.abs(width - height) < Math.max(height, width) * 0.3f) {
            if (Math.abs(top.y + height / 2 - left.y) < height * 0.3f && Math.abs(top.y + height / 2 - right.y) < height * 0.3f) {
                if (Math.abs(left.x + width / 2 - top.x) < width * 0.3f && Math.abs(left.x + width / 2 - bottom.x) < width * 0.3f) {
                    Log.d(TAG, "checkCircle: CIRCLE");
                    if (onDrawCircleListener != null) {
                        onDrawCircleListener.onCircleDrawn(left.x, top.y, right.x, bottom.y);
                    }
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    public interface OnDrawCircleListener {
        void onCircleDrawn(int left, int top, int right, int bottom);
    }
}
