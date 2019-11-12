package com.example.mox.db.bean;

public class Mock implements Comparable<Mock> {

    public String name; // 文件名
    public boolean enable = false;
    public String title;
    public String desc;
    public int priority;
    public MockRequest request;
    public MockResponse response;

    @Override
    public int compareTo(Mock o) {
        if (o == null) {
            return 1;
        }
        if (priority > o.priority) {
            return 1;
        } else if (priority < o.priority) {
            return -1;
        }
        return 0;
    }
}


