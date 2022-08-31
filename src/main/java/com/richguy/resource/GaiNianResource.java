package com.richguy.resource;

import com.zfoo.storage.model.anno.Id;
import com.zfoo.storage.model.anno.Resource;

@Resource
public class GaiNianResource {

    @Id
    private String id;

    public String getId() {
        return id;
    }

}
