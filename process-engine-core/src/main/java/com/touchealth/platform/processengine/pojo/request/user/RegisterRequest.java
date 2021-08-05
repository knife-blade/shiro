package com.touchealth.platform.processengine.pojo.request.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class RegisterRequest {

    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
