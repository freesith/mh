package com.freesith.jsonvision;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class JsonVisionView extends LinearLayout {

    public JsonVisionView(Context context) {
        super(context);
    }

    public JsonVisionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public JsonVisionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JsonVisionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void showJson(String json) {

    }
}
