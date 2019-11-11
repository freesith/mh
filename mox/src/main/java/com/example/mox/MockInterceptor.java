package com.example.mox;

import com.example.mox.db.bean.MockResponse;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        MockResponse mockResonse = Mox.getInstance().mock(request);
        if (mockResonse == null) {
            Response proceed = chain.proceed(request);
            Mox.log(request, proceed);
            return proceed;
        } else {
            if (mockResonse.cover && mockResonse.covers != null && !mockResonse.covers.isEmpty()) {
                Response proceed = chain.proceed(request);
//                String bodyString = proceed.body().toString();
//                JSONObject jsonObject = JSON.parseObject(bodyString);
//
//                org.json.JSONObject
//                for (Map.Entry<String,Object> cover: mockResonse.covers.entrySet()) {
//                    String key = cover.getKey();
//                    String[] split = key.split(".");
//                    for (int i = 0; i < split.length; i++) {
//                        if (i == split.length - 1) {
//
//                        } else {
//                            Object o = jsonObject.get(split[i]);
//                            jsonObject.getInnerMap()
//                        }
//
//                    }
//                }
                return proceed;
            } else {
                ResponseBody mockBody = ResponseBody.create(MediaType.get("json"), mockResonse.data);
                Response mockedResponse = new Response.Builder().request(request).body(mockBody).build();
                return mockedResponse;
            }
        }
    }

    private boolean needMock(Request request) {
        String method = request.method();
        HttpUrl url = request.url();
        String host = url.host();
        String path = url.encodedPath();
        String query = url.query();
        return false;
    }
}
