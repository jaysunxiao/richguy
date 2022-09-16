package com.richguy.resource;

import com.zfoo.storage.model.anno.Id;
import com.zfoo.storage.model.anno.Resource;

@Resource
public class HotNoticeResource {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

}
