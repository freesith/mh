package com.freesith.manhole.bean;

import java.util.List;

public class Flow {
    public String name;
    public String title;
    public String module;
    public String desc;
    public List<Case> cases;
    public List<MockChoice> mocks;

    //local
    public boolean enable;


}