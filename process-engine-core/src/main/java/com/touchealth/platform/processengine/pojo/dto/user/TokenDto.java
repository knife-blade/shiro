package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.constant.GlobalDefine;
import com.touchealth.platform.processengine.pojo.bo.user.TokenBo;
import lombok.Data;

@Data
public class TokenDto {

    private String authorization;

    private TokenUserDto user;

    public static TokenDto convert(TokenBo bo) {
        TokenDto dto = new TokenDto();
        dto.setAuthorization(GlobalDefine.Jwt.TOKEN_PREFIX + bo.getToken());
        TokenUserDto userDto = BaseHelper.r2t(bo.getUser(), TokenUserDto.class);
        dto.setUser(userDto);
        return dto;
    }

}
