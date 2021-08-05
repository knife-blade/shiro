package com.touchealth.platform.processengine.pojo.dto;

import lombok.Data;

@Data
public class BooleanDto {

    private boolean data;
    private String msg;

    public BooleanDto() {
        this.data = true;
    }

    public BooleanDto(boolean data, String errMsg) {
        this.data = data;
        if (!data) {
            this.msg = errMsg;
        }
    }

    public BooleanDto(boolean data, String errMsg, String sucMsg) {
        this.data = data;
        this.msg = data ? sucMsg : errMsg;
    }

}
