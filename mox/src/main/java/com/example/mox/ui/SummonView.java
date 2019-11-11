package com.example.mox.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

public class SummonView extends View implements View.OnTouchListener {

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

    public SummonView(Context context) {
        super(context);
        this.context = context;
        setOnTouchListener(this);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(30);
    }

    private float lastX;
    private float lastY;
    private int lastAction;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("xxx", "dispatchTouchEvent event = " + MotionEvent.actionToString(event.getActionMasked()));
        if (lastAction == event.getAction() && lastX == event.getX() && lastY == event.getY()) {
            Log.d("xxx", "oldEvent");
            return super.dispatchTouchEvent(event);
        } else {
            Log.d("xxx", "newEvent");
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
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("xxx", "onTouchEvent event = " + MotionEvent.actionToString(event.getActionMasked()));

        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.d("xxx", "onTouch    " + MotionEvent.actionToString(event.getActionMasked()));
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

        context.startActivity(new Intent(context, SettingActivity.class));

        if (Math.abs(width - height) < width * 0.3f && Math.abs(left.y - right.y) < width * 0.3f && Math.abs(top.x - bottom.x) < width * 0.3f) {
            Log.d("xxx", "isCircle");
        } else {
            Log.d("xxx", "notCircle");
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
}
