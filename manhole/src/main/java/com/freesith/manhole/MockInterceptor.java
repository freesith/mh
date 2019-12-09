package com.freesith.manhole;

import android.util.Log;

import com.freesith.manhole.bean.MockResponse;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        MockResponse mockResonse = Manhole.getInstance().mock(request);
        if (mockResonse == null) {
            Response proceed = chain.proceed(request);
            Manhole.getInstance().log(request, proceed);
            return proceed;
        } else {
            if (mockResonse.cover && mockResonse.covers != null && !mockResonse.covers.isEmpty()) {
                Response proceed = chain.proceed(request);
                //TODO 2019-11-12 by WangChao 改变其中某些字段
                return proceed;
            } else {
                ResponseBody mockBody = ResponseBody.create(MediaType.get("application/json"), mockResonse.data);
                return new Response.Builder().protocol(Protocol.HTTP_1_1).code(200).message("Success").request(request).body(mockBody).build();
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
