package com.freesith.manhole;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.freesith.manhole.ui.CoverLayout;

public class MoxLifeCallbacks implements Application.ActivityLifecycleCallbacks {

    private Activity resumeActivity;

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        int childCount = content.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (content.getChildAt(i) instanceof CoverLayout) {
                return;
            }
        }
        CoverLayout summonView = new CoverLayout(activity);
        content.addView(summonView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        resumeActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        if (activity == resumeActivity) {
            resumeActivity = null;
        }
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
//        FrameLayout content = activity.findViewById(android.R.id.content);
//        int childCount = content.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            if (content.getChildAt(i) instanceof CoverLayout) {
//                content.removeViewAt(i);
//                return;
//            }
//        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
