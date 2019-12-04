package com.freesith.manhole.bean;

import java.util.List;

public class Case {
    public String name;
    public String title;
    public String module;
    public String desc;
    public List<MockChoice> mocks;

    //local
    public boolean enable;
    public boolean passive;

}
