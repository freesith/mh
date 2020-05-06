package com.freesith.manhole;

import android.util.Log;

import com.freesith.manhole.bean.MockResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

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
                int code = proceed.code();
                if (code == 200 && proceed.body() != null) {
                    MediaType mediaType = proceed.body().contentType();
                    String string = proceed.body().string();
                    //TODO 2019-11-12 by WangChao 改变其中某些字段

                    Response.Builder builder = new Response.Builder().code(code).message(proceed.message()).request(proceed.request())
                            .headers(proceed.headers());
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        builder.body(ResponseBody.create(mediaType,string));
                    }

                    return builder.build();

                } else {
                    return proceed;
                }
            } else {
                ResponseBody mockBody = ResponseBody.create(MediaType.get("application/json"), mockResonse.data);
                return new Response.Builder().protocol(Protocol.HTTP_1_1).code(200).message("Success").request(request).body(mockBody).build();
            }
        }
    }


    //value : Number, String

    //user? . photos?[0].url? ="http......"
    //list? . [0]? . [0]? = "xxx"
    //list[*]?.hell=xxx
    //

    public static final int TYPE_NUMBER = 1;
    public static final int TYPE_BOOLEAN = 2;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_OBJECT = 4;
    public static final int TYPE_ARRAY = 5;


    private boolean needMock(Request request) {
        String method = request.method();
        HttpUrl url = request.url();
        String host = url.host();
        String path = url.encodedPath();
        String query = url.query();
        return false;
    }
}
