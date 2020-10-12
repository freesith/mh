package com.freesith.manhole.ui.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.freesith.manhole.ui.ContainerLayout;
import com.freesith.manhole.ui.CoverLayout;

public class ViewUtil {

    public static ViewGroup findCoverLayout(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ContainerLayout) {
            return (ContainerLayout)parent;
        } else if (parent instanceof View){
            return findCoverLayout((View) parent);
        } else {
            return null;
        }
    }

    public static int dp2px(final Context context, final float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float dp2pxFloat(final Context context, final float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
