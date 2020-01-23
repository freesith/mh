package com.freesith.manhole.demo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TestView extends View {

    int size = 30;
    Paint paint;
    int drawCount = 0;

    long lastPaintTime = 0;

    public TestView(Context context) {
        super(context);
        init(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (System.currentTimeMillis() - lastPaintTime > 100) {
            drawCount++;
            lastPaintTime = System.currentTimeMillis();

        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int widthCount = measuredWidth / 10;
        int heightCount = measuredHeight / 10;

        for (int i = 0; i < widthCount; i++) {
            for (int j = 0; j < heightCount; j++) {
                int i1 = i + j;
                if (i1 % 2 == drawCount % 2) {
                    canvas.drawRect(i * size, j * size, (i + 1) * size, (j + 1) * size, paint);
                }
            }
        }
//        invalidate();
    }
}
