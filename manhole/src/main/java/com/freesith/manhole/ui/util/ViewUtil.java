package com.freesith.manhole.ui.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.freesith.manhole.ui.ContainerLayout;
import com.freesith.manhole.ui.CoverLayout;

public class ViewUtil {

    public static ViewGroup findCoverLayout(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof CoverLayout) {
            return (CoverLayout)parent;
        } else if (parent instanceof View){
            return findCoverLayout((View) parent);
        } else {
            return null;
        }
    }
}
