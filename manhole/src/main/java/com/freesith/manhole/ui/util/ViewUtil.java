package com.freesith.manhole.ui.util;

import android.view.View;
import android.view.ViewParent;

import com.freesith.manhole.ui.ContainerLayout;

public class ViewUtil {

    public static ContainerLayout findCoverLayout(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ContainerLayout) {
            return (ContainerLayout)parent;
        } else if (parent instanceof View){
            return findCoverLayout((View) parent);
        } else {
            return null;
        }
    }
}
