package com.freesith.manhole.bean;

import java.util.Map;

public class MockResponse {
    //如果true,直接返回data,
    //如果false,请求网络,替换covers里的字段
    public String data;
    //httpCode
    public int code;
    //httpMessage
    public String message;
    public boolean cover = false;
    public Map<String, Object> covers;
}
