package com.mg.gulimall.product.vo;

import lombok.Data;

import java.util.List;
@Data
public class SpuItemAttrGroupVo {
    private String groupName;

    //attrId,attrName,attrValue
    private List<Attr> attrs;
}
