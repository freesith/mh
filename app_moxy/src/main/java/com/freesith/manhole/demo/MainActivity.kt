package com.freesith.manhole.demo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import com.freesith.manhole.MockInterceptor
import com.freesith.manhole.demo.Client.okHttpClient
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException

class MainActivity : Activity() {
    //    private JsonView jsonView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            try {
                intent.setClass(
                    this@MainActivity,
                    Class.forName("com.freesith.manhole.ui.SettingActivity")
                )
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            //                intent.setClassName("com.freesith.manhole.ui", "com.freesith.manhole.ui.SettingActivity");
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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
        })
        //        button.setOnTouchListener(new ScaleAnimateTouchListener());
        btnRequest.setOnClickListener(View.OnClickListener { request1() })
        btnJump.setOnClickListener(View.OnClickListener {
            val a = 1 / 0
            startActivity(Intent(this@MainActivity, SecondActivity::class.java))
        })
        val newButton = Button(this@MainActivity)
        newButton.text = "BUTTON"
        //        showImportantWindow(newButton);
        val json = """{
      "success": true,
      "sessionTL": 1564725600000,
      "showSuper": true,
      "profile": {
        "region": [
          "山东",
          "青岛"
        ],
        "education": [
          "山东大学"
        ],
        "photo": [
          "https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/2b8415b0-ce21-11e9-a3f7-bd2438cfa05a0.png",
          "https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/41322410-deb6-11e9-aa33-9d2b9153312b0.png",
          "https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/0f486e50-deb6-11e9-b8b5-b32b3dd458340.png",
          "https://oss.intelcupid.com/album/5d6e1d93ca050e41f6ba306d/7344dc60-e97c-11e9-9759-dd59afa219340.png"
        ],
        "answer": [
          "骑车通勤",
          "工作",
          "云吸猫",
          "有猫",
          "",
          ""
        ],
        "gender": 1,
        "constell": 0,
        "age": 29,
        "position": "北京市 朝阳区",
        "employer": "她说",
        "height": 187,
        "occupation": "android工程师",
        "aim": 4
      },
      "name": "木魚",
      "iceBreaker": [],
      "preferGender": 2,
      "birthday": "1996-01-01T00:00:00.000Z",
      "followPublic": true,
      "suggestNum": 21,
      "settings": {
        "allowMatchPush": true,
        "allowUpdatePush": true,
        "allowSuperPush": true,
        "isHide": false,
        "posOption": 0,
        "residence": "United States Florida",
        "gps": [
          -82.142982,
          29.192865
        ],
        "pushTime": 1
      },
      "qaVersion": 1,
      "location": [
        116.4115039116493,
        39.99835959097079
      ],
      "loginDays": 7,
      "hasComment": true,
      "helpNum": 0,
      "avatarNum": 0,
      "privilege": {
        "superTL": 0,
        "vipTL": 0
      },
      "superLike": {
        "number": 0,
        "today": 0
      },
      "comment": [],
      "quest": []
    }"""

//        jsonView.setJson(json);
//        List<JsonElement<?>> jsonElements = JsonUtilKt.parseJson(json);
//
//        for (int i = 0; i < jsonElements.size(); i++) {
//            JsonElement<?> jsonElement = jsonElements.get(i);
//            Log.d("xxx", "line = " + jsonElement.getLine() + "  level = " + jsonElement.getLevel() + "  name = " + jsonElement.getName() + "   value = " + jsonElement.getValue() + "  hasChild = " + jsonElement.getHasChild() + "    start = " + jsonElement.getChildStart() + "     end = " + jsonElement.getChildEnd());
//        }
//        int a = 1;
//        int b = 2;
        btnWipe.setOnClickListener(View.OnClickListener { testWipe() })
    }

    private fun testWipe() {
        val mockInterceptor = MockInterceptor()
        val json = """{
                        "success":true,
                        "faceState":3,
                        "postCovers":[
                            "#9A99F6-#FABFCD&#FFFFFF",
                            "#F3EF8B-#FF7F9F&#FFFFFF",
                            "https://cover.intelcupid.com/book/cover/27186298.jpg"
                        ],
                        "profile":{
                            "region":[
                                "山东",
                                "青岛"
                            ],
                            "photo":[
                                "https://oss.intelcupid.com/album/5ea7962da9a46315551b7340/ef5a2c30-f185-11ea-91c6-494f80e795540.png",
                                "https://oss.intelcupid.com/album/5ea7962da9a46315551b7340/c9c80780-f3d3-11ea-bb24-198d862196e10.png"
                            ],
                            "gender":0
                        },
                        "location":[
                            116.427586,
                            39.918853
                        ],
                        "privilege":{
                            "superTL":1604030400000,
                            "vipTL":1604030400000
                        },
                        "quest":[
                            {
                                "qid":null,
                                "question":"最近正在忙的事",
                                "answer":"棒棒棒风格一更行"
                            }
                        ]
                    }"""

        val map = mutableMapOf<String, Any>()
        map.put("success", false)
        map.put("faceState", 1)
        map.put("profile.quest.[0].qid","xxxxxxx")
        map.put("profile.gender",1)
        map.put("profile.region.[0]","北京")
        map.put("location","null")


    }

    private fun request1() {
        val body = RequestBody.create(
            MediaType.get("application/json"),
            "a=1&b=2&json={'gaega':1,'gageagageag':'gaegeaehrhshsh'}&tt=ggpaehgpehigaigepgpeag"
        )
        val request = Request.Builder().url("http://www.baidu.com/")
            .post(body)
            .build()
        okHttpClient.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {
                }

                @Throws(IOException::class)
                override fun onResponse(
                    call: Call,
                    response: Response
                ) {
                    val string = response.body()!!.string()
                }
            })
    }

    private var flImportantWindow: FrameLayout? = null

    /**
     * 显示类似dialog的view
     * 可以重叠显示
     * @param view
     */
    fun showImportantWindow(view: View?) {
        if (view == null) {
            return
        }
        if (flImportantWindow == null) {
            val contentFrameLayout =
                findViewById<FrameLayout>(android.R.id.content)
            flImportantWindow = FrameLayout(this)
            val layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            flImportantWindow!!.layoutParams = layoutParams
            flImportantWindow!!.setBackgroundColor(Color.TRANSPARENT)
            contentFrameLayout.addView(flImportantWindow)
        }
        flImportantWindow!!.setOnClickListener { dismissImportantWindow() }
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        view.layoutParams = layoutParams
        view.setOnClickListener(View.OnClickListener { })
        val childCount = flImportantWindow!!.childCount
        if (childCount != 0) {
            //如果之前存在弹窗,把上一个弹窗缩小淡出
            val oldChild = flImportantWindow!!.getChildAt(childCount - 1)
            oldChild.animate().scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(250).start()
            flImportantWindow!!.addView(view)
        } else {
            flImportantWindow!!.addView(view)
            flImportantWindow!!.alpha = 0f
            flImportantWindow!!.visibility = View.VISIBLE
        }
        view.addOnLayoutChangeListener(object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                view.removeOnLayoutChangeListener(this)
                view.translationY = flImportantWindow!!.height - view.top.toFloat()
                view.animate().translationY(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            animation.removeAllListeners()
                        }
                    }).setDuration(250).start()
                flImportantWindow!!.animate().alpha(1f).setDuration(250).start()
            }
        })
    }

    /**
     * 弹窗消失
     * 只会消失flImportantWindow最顶上的子view
     * 第二个子view会放大淡入
     */
    fun dismissImportantWindow() {
        if (flImportantWindow == null || flImportantWindow!!.visibility == View.GONE) {
            return
        }
        val childCount = flImportantWindow!!.childCount
        if (childCount == 0) {
            flImportantWindow!!.visibility = View.GONE
            return
        }
        val view = flImportantWindow!!.getChildAt(childCount - 1)
        if (childCount == 1) {
            flImportantWindow!!.animate().alpha(0f).setDuration(150).setListener(null).start()
        } else if (childCount > 1) {
            //如果还存在其他window,把最上层的一个显示出来
            flImportantWindow!!.getChildAt(childCount - 2).animate().scaleY(1f).scaleX(1f).alpha(1f)
                .setDuration(250).start()
        }
        view.animate().setInterpolator(null).setListener(object : AnimatorListenerAdapter() {
            var cancel = false
            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                cancel = true
            }

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (!cancel) {
                    flImportantWindow!!.removeView(view)
                    //只剩下一个子View时,动画播放完Gone掉flImportantWindow
                    if (flImportantWindow!!.childCount == 0 && flImportantWindow!!.visibility != View.GONE) {
                        flImportantWindow!!.visibility = View.GONE
                    }
                }
            }
        }).translationY(flImportantWindow!!.height - view.top.toFloat()).setDuration(150)
            .start()
    }

    companion object {
        const val TAG = "xxx"
    }
}