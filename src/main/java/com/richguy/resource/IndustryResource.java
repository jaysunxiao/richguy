package com.richguy.resource;

import com.zfoo.storage.model.anno.Id;
import com.zfoo.storage.model.anno.Resource;

@Resource
public class IndustryResource {

    @Id
    private int code;

    private String name;

    private int realCode;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getRealCode() {
        return realCode;
    }
}
