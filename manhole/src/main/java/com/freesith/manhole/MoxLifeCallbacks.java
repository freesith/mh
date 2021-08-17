package com.freesith.manhole;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.freesith.manhole.ui.CoverLayout;

public class MoxLifeCallbacks implements Application.ActivityLifecycleCallbacks {

    private Activity resumeActivity;

    String packageName;

    public MoxLifeCallbacks(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        int childCount = content.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = content.getChildAt(i);
            if (child instanceof CoverLayout) {
                ((CoverLayout)child).onStart();
                return;
            }
        }
        CoverLayout summonView = new CoverLayout(activity);
        content.addView(summonView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        summonView.onStart();
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
        FrameLayout content = activity.findViewById(android.R.id.content);
        int childCount = content.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = content.getChildAt(i);
            if (child instanceof CoverLayout) {
                ((CoverLayout) child).onStop();
                return;
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
