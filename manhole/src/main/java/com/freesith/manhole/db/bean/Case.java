package com.freesith.manhole.db.bean;

import java.util.List;

public class Case {
    public String name;
    public String title;
    public String desc;
    public List<String> mocks;

    //local
    public boolean enable;
    public boolean passive;

}