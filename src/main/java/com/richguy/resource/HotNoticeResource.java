package com.richguy.resource;

import com.zfoo.storage.model.anno.Id;
import com.zfoo.storage.model.anno.Resource;

@Resource
public class HotNoticeResource {

    @Id
    private int id;

    private int type;

    private String word;

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return word;
    }

}
