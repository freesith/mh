package com.example.moxy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.freesith.manhole.MockInterceptor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {

    OkHttpClient okHttpClient;
    public static final String TAG = "xxx";
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request1();
//                Log.d("xxx","click11111");
//                Button newButton = new Button(MainActivity.this);
//                newButton.setText("BUTTON");
//                newButton.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        Log.d(TAG, "onClick: 3333333333");
//                        Toast.makeText(MainActivity.this,"xxxxxx",Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//                newButton.setOnTouchListener(new ScaleAnimateTouchListener());
//                showImportantWindow(newButton);
//                newButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Log.d(TAG, "onClick: 22222222222");
//                        Toast.makeText(MainActivity.this,"hahaha",Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        });
        button.setOnTouchListener(new ScaleAnimateTouchListener());

        okHttpClient = new OkHttpClient.Builder().addInterceptor(new MockInterceptor()).build();

    }


    private void request1 () {
        RequestBody body = RequestBody.create(MediaType.get("application/json"),"a=1&b=2&json={'gaega':1,'gageagageag':'gaegeaehrhshsh'}&tt=ggpaehgpehigaigepgpeag");
        Request request = new Request.Builder().url("http://www.baidu.com/app/v1/pass")
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d(TAG, "onResponse: response = " + string);
            }
        });
    }

    private FrameLayout flImportantWindow;

    /**
     * 显示类似dialog的view
     * 可以重叠显示
     * @param view
     */
    public void showImportantWindow(final View view) {
        if (view == null) {
            return;
        }
        if (flImportantWindow == null) {
            FrameLayout contentFrameLayout = this.findViewById(android.R.id.content);
            flImportantWindow = new FrameLayout(this);
            FrameLayout.LayoutParams layoutParams =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            flImportantWindow.setLayoutParams(layoutParams);
            flImportantWindow.setBackgroundColor(Color.TRANSPARENT);
            contentFrameLayout.addView(flImportantWindow);
        }

        flImportantWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissImportantWindow();
            }
        });

        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        int childCount = flImportantWindow.getChildCount();
        if (childCount != 0) {
            //如果之前存在弹窗,把上一个弹窗缩小淡出
            View oldChild = flImportantWindow.getChildAt(childCount - 1);
            oldChild.animate().scaleX(0.8f).scaleY(0.8f).alpha(0).setDuration(250).start();
            flImportantWindow.addView(view);
        } else {
            flImportantWindow.addView(view);
            flImportantWindow.setAlpha(0);
            flImportantWindow.setVisibility(View.VISIBLE);
        }
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                view.removeOnLayoutChangeListener(this);
                view.setTranslationY(flImportantWindow.getHeight() - view.getTop());
                view.animate().translationY(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                animation.removeAllListeners();
                            }
                        }).setDuration(250).start();

                flImportantWindow.animate().alpha(1).setDuration(250).start();
            }
        });
    }

    /**
     * 弹窗消失
     * 只会消失flImportantWindow最顶上的子view
     * 第二个子view会放大淡入
     */
    public void dismissImportantWindow() {

        if (flImportantWindow == null || flImportantWindow.getVisibility() == View.GONE) {
            return;
        }
        final int childCount = flImportantWindow.getChildCount();
        if (childCount == 0) {
            flImportantWindow.setVisibility(View.GONE);
            return;
        }

        final View view = flImportantWindow.getChildAt(childCount - 1);
        if (childCount == 1) {
            flImportantWindow.animate().alpha(0).setDuration(150).setListener(null).start();
        } else if (childCount > 1) {
            //如果还存在其他window,把最上层的一个显示出来
            flImportantWindow.getChildAt(childCount - 2).animate().scaleY(1).scaleX(1).alpha(1).setDuration(250).start();
        }
        view.animate().setInterpolator(null).setListener(new AnimatorListenerAdapter() {

            boolean cancel = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                cancel = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!cancel) {
                    flImportantWindow.removeView(view);
                    //只剩下一个子View时,动画播放完Gone掉flImportantWindow
                    if (flImportantWindow.getChildCount() == 0 && flImportantWindow.getVisibility() != View.GONE) {
                        flImportantWindow.setVisibility(View.GONE);
                    }
                }
            }
        }).translationY(flImportantWindow.getHeight() - view.getTop()).setDuration(150).start();
    }


}
