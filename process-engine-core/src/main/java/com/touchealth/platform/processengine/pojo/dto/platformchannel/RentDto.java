package com.touchealth.platform.processengine.pojo.dto.platformchannel;

import com.touchealth.platform.user.client.dto.response.RentRes;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class RentDto {
    /**
     * 租户id
     */
    private Long rentId;
    /**
     * 租户名称
     */
    private String rentName;

    public static List<RentDto> toRentDto(List<RentRes> rentRes) {
        List<RentDto> dtos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rentRes)) {
            rentRes.forEach(e -> dtos.add(toRentDto(e)));
        }
        return dtos;
    }

    public static RentDto toRentDto(RentRes rentRes) {
        if (null == rentRes) {
            return null;
        }
        RentDto dto = new RentDto();
        dto.setRentId(rentRes.getId());
        dto.setRentName(rentRes.getRentName());
        return dto;
    }
}
