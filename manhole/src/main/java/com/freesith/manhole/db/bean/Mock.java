package com.freesith.manhole.db.bean;

public class Mock implements Comparable<Mock> {

    public String name; // 文件名
    public String title;
    public String desc;
    public int priority;
    public MockRequest request;
    public MockResponse response;

    //local
    public boolean passive = false;
    public boolean enable = false;


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


