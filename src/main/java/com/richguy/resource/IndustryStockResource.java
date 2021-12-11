package com.richguy.resource;

import com.zfoo.storage.model.anno.Id;
import com.zfoo.storage.model.anno.Resource;

@Resource
public class IndustryStockResource {

    @Id
    private int id;

    private int industry;

    private int stock;

    public int getId() {
        return id;
    }

    public int getIndustry() {
        return industry;
    }

    public int getStock() {
        return stock;
    }
}
