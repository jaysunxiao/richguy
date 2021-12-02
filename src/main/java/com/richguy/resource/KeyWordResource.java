package com.richguy.resource;

import com.zfoo.storage.model.anno.Id;
import com.zfoo.storage.model.anno.Resource;

@Resource
public class KeyWordResource {

    @Id
    private String word;

    public String getWord() {
        return word;
    }

}
