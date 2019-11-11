package com.example.mox.db.bean;

import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import kotlin.text.Regex;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okio.Buffer;

public class MockRequest {
    public String method;
    public List<String> host;
    public String path;
    //"page=1&cursor=<>"
    public HashMap<String, String> query;


    public boolean matches(Request request) {
        String method = request.method();
        HttpUrl url = request.url();
        String path = url.encodedPath();
        String host = url.host();
        HashMap<String, String> requestQueryMap = new HashMap<>();
        String query = url.query();
        if (query != null && !query.isEmpty()) {
            String[] split = query.split("&");
            for (String urlQuery : split) {
                String[] split1 = urlQuery.split("=");
                if (split1.length == 2) {
                    requestQueryMap.put(split1[0], split1[1]);
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
            String bodyString = buffer.toString();
            String[] split = bodyString.split("&");
            for (String urlQuery : split) {
                String[] split1 = urlQuery.split("=");
                if (split1.length == 2) {
                    requestQueryMap.put(split1[0], split1[1]);
                }
            }
            buffer.close();
        }

        if (!TextUtils.isEmpty(this.method) && !this.method.equals(method)) {
            return false;
        }
        if (!TextUtils.isEmpty(this.path) && !this.path.equals(path)) {
            return false;
        }
        if (this.host != null && !this.host.isEmpty() && this.host.indexOf(host) == -1) {
            return false;
        }

        if (this.query != null && !this.query.isEmpty()) {
            for (Map.Entry<String, String> entry : this.query.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null && !value.isEmpty()) {
                    String requestQueryValue = requestQueryMap.get(key);
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

        return true;
    }

}
