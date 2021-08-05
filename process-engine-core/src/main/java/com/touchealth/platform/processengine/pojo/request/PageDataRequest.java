package com.touchealth.platform.processengine.pojo.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageDataRequest implements Serializable {

    Long pageNo = 1L;
    Long pageSize = 10L;

}
