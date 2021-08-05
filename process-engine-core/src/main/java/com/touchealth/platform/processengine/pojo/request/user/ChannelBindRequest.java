package com.touchealth.platform.processengine.pojo.request.user;

import lombok.Data;

import java.util.List;

@Data
public class ChannelBindRequest {

    private Long staffId;
    private List<String> channelNos;

}
