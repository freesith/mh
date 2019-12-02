package com.freesith.manhole.db.bean;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okio.Buffer;

public class MockRequest {
    public String method;
    public List<String> host;
    public String path;
    //"page=1&cursor=<>"
    public HashMap<String, String> urlQuery;
    public List<String> requestBody;


    public boolean matches(Request request) {
        String method = request.method();
        HttpUrl url = request.url();
        String path = url.encodedPath();
        String host = url.host();
        HashMap<String, String> urlQueryMap = new HashMap<>();
        String query = url.query();
        String bodyString = "";
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            bodyString = buffer.toString();
        }

        if (!TextUtils.isEmpty(this.method) && !this.method.equalsIgnoreCase(method)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.path) && !this.path.equals(path)) {
            return false;
        }
        if (this.host != null && !this.host.isEmpty() && this.host.indexOf(host) == -1) {
            return false;
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
