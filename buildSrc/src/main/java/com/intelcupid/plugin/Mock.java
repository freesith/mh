package com.intelcupid.plugin;

import java.util.List;
import java.util.Map;

public class Mock {
    public boolean enable = false;
    public String desc;
    public MockRequest request;
    public MockResponse response;

    public class MockRequest {
        public List<String> protocal;
        public List<String> host;
        public String path;
        public List<String> query;
        public List<String> dQuery;
    }

    public class MockResponse {
        //如果true,直接返回data,
        //如果false,请求网络,替换covers里的字段
        public boolean block = true;
        public String data;
        public Map<String, Object> covers;
    }
}


