package com.freesith.manhole.demo;

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

import androidx.annotation.Nullable;

import com.freesith.jsonview.JsonUtilKt;
import com.freesith.jsonview.JsonView;
import com.freesith.jsonview.bean.JsonElement;
import com.freesith.manhole.MockInterceptor;

import java.io.IOException;
import java.util.List;

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
    private JsonView jsonView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        jsonView = findViewById(R.id.jsonView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                request1();
//                Log.d("xxx","click11111");

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


        Button newButton = new Button(MainActivity.this);
        newButton.setText("BUTTON");
//        showImportantWindow(newButton);

        okHttpClient = new OkHttpClient.Builder().addInterceptor(new MockInterceptor()).build();

        String json = "{\n" +
                "      \"success\": true,\n" +
                "      \"sessionTL\": 1564725600000,\n" +
                "      \"showSuper\": true,\n" +
                "      \"profile\": {\n" +
                "        \"region\": [\n" +
                "          \"山东\",\n" +
                "          \"青岛\"\n" +
                "        ],\n" +
                "        \"education\": [\n" +
                "          \"山东大学\"\n" +
                "        ],\n" +
                "        \"photo\": [\n" +
                "          \"https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/2b8415b0-ce21-11e9-a3f7-bd2438cfa05a0.png\",\n" +
                "          \"https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/41322410-deb6-11e9-aa33-9d2b9153312b0.png\",\n" +
                "          \"https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/0f486e50-deb6-11e9-b8b5-b32b3dd458340.png\",\n" +
                "          \"https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/7344dc60-e97c-11e9-9759-dd59afa219340.png\"\n" +
                "        ],\n" +
                "        \"answer\": [\n" +
                "          \"骑车通勤\",\n" +
                "          \"工作\",\n" +
                "          \"云吸猫\",\n" +
                "          \"有猫\",\n" +
                "          \"\",\n" +
                "          \"\"\n" +
                "        ],\n" +
                "        \"gender\": 1,\n" +
                "        \"constell\": 0,\n" +
                "        \"age\": 29,\n" +
                "        \"position\": \"北京市 朝阳区\",\n" +
                "        \"employer\": \"她说\",\n" +
                "        \"height\": 187,\n" +
                "        \"occupation\": \"android工程师\",\n" +
                "        \"aim\": 4\n" +
                "      },\n" +
                "      \"name\": \"木魚\",\n" +
                "      \"iceBreaker\": [],\n" +
                "      \"preferGender\": 2,\n" +
                "      \"birthday\": \"1996-01-01T00:00:00.000Z\",\n" +
                "      \"followPublic\": true,\n" +
                "      \"suggestNum\": 21,\n" +
                "      \"settings\": {\n" +
                "        \"allowMatchPush\": true,\n" +
                "        \"allowUpdatePush\": true,\n" +
                "        \"allowSuperPush\": true,\n" +
                "        \"isHide\": false,\n" +
                "        \"posOption\": 0,\n" +
                "        \"residence\": \"United States Florida\",\n" +
                "        \"gps\": [\n" +
                "          -82.142982,\n" +
                "          29.192865\n" +
                "        ],\n" +
                "        \"pushTime\": 1\n" +
                "      },\n" +
                "      \"qaVersion\": 1,\n" +
                "      \"location\": [\n" +
                "        116.4115039116493,\n" +
                "        39.99835959097079\n" +
                "      ],\n" +
                "      \"loginDays\": 7,\n" +
                "      \"hasComment\": true,\n" +
                "      \"helpNum\": 0,\n" +
                "      \"avatarNum\": 0,\n" +
                "      \"privilege\": {\n" +
                "        \"superTL\": 0,\n" +
                "        \"vipTL\": 0\n" +
                "      },\n" +
                "      \"superLike\": {\n" +
                "        \"number\": 0,\n" +
                "        \"today\": 0\n" +
                "      },\n" +
                "      \"comment\": [],\n" +
                "      \"quest\": []\n" +
                "    }";

        jsonView.setJson(json);
//        List<JsonElement<?>> jsonElements = JsonUtilKt.parseJson(json);
//
//        for (int i = 0; i < jsonElements.size(); i++) {
//            JsonElement<?> jsonElement = jsonElements.get(i);
//            Log.d("xxx", "line = " + jsonElement.getLine() + "  level = " + jsonElement.getLevel() + "  name = " + jsonElement.getName() + "   value = " + jsonElement.getValue() + "  hasChild = " + jsonElement.getHasChild() + "    start = " + jsonElement.getChildStart() + "     end = " + jsonElement.getChildEnd());
//        }
//        int a = 1;
//        int b = 2;


    }


    private void request1 () {
        RequestBody body = RequestBody.create(MediaType.get("application/json"),"a=1&b=2&json={'gaega':1,'gageagageag':'gaegeaehrhshsh'}&tt=ggpaehgpehigaigepgpeag");
        Request request = new Request.Builder().url("http://www.baidu.com/")
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
