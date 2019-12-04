package com.freesith.manhole.bean;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okio.Buffer;

public class MockChoice{

    public String name;
    public String title;
    public String desc;
    public int index;

    public String method;
    public String path;
    public List<String> host;
    public HashMap<String, String> urlQuery;
    public List<String> requestBody;


    //如果true,直接返回data,
    //如果false,请求网络,替换covers里的字段
    public String data;
    //httpCode
    public int code;
    //httpMessage
    public String message;
    public boolean cover = false;
    public Map<String, Object> covers;


    public boolean enable;
    public boolean passive;
    public String mockName;

    public boolean matches(Request request) {
        String method = request.method();
        HttpUrl url = request.url();
        String path = url.encodedPath();
        String host = url.host();
        HashMap<String, String> urlQueryMap = new HashMap<>();
        String query = url.query();
        String bodyString = "";

        if (!TextUtils.isEmpty(this.method) && !this.method.equalsIgnoreCase(method)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.path) && !this.path.equals(path)) {
            return false;
        }
        if (this.host != null && !this.host.isEmpty() && this.host.indexOf(host) == -1) {
            return false;
        }

        if (query != null && !query.isEmpty()) {
            String[] split = query.split("&");
            for (String urlQuery : split) {
                String[] split1 = urlQuery.split("=");
                if (split1.length == 2) {
                    urlQueryMap.put(split1[0], split1[1]);
                }
            }
        }

        if (request.body() != null) {
            Buffer buffer = new Buffer();
            try {
                request.body().writeTo(buffer);
                bodyString = buffer.readUtf8();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (this.urlQuery != null && !this.urlQuery.isEmpty()) {
            for (Map.Entry<String, String> entry : this.urlQuery.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    String requestQueryValue = urlQueryMap.get(key);
                    if (requestQueryValue == null) {
                        requestQueryValue = "";
                    }
                    if (value.startsWith("<") && value.endsWith(">")) {
                        //TODO 放到一个map里
                        Pattern compile = Pattern.compile(value.substring(1, value.length() - 1));
                        if (!compile.matcher(requestQueryValue).matches()) {
                            return false;
                        }
                    } else {
                        if (!value.equals(requestQueryValue)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (this.requestBody != null && !this.requestBody.isEmpty()) {
            for (String bodyRegex : requestBody) {
                if (bodyRegex.startsWith("<") && bodyRegex.endsWith(">")) {
                    Pattern compile = Pattern.compile(bodyRegex.substring(1, bodyRegex.length() - 1));
                    if (!compile.matcher(bodyString).matches()) {
                        return false;
                    }
                } else {
                    if (!bodyString.contains(bodyRegex)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
