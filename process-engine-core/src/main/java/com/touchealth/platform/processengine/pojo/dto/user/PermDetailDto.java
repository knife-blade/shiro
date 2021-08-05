package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.handler.WebPermsHandler;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限详情。
 * @see PermDto
 */
@Data
@NoArgsConstructor
public class PermDetailDto {

    private Long id;
    private String code;
    private String name;
    /**
     * 前端展示的权限名
     */
    private String webName;
    private boolean have;
    private Integer type;
    private String group;
    /**
     * 数据或功能权限列表
     */
    private List<PermDetailDto> permList;
    /**
     * 子权限
     */
    private List<PermDetailDto> childs;

    public PermDetailDto(String code, String webName) {
        this.code = code;
        this.webName = webName;
        this.have = true;
        this.childs = new ArrayList<>();
    }

    public PermDetailDto(Long id, String code, String name, Integer type, boolean have, String group, String permCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.have = have;
        this.group = group;
        this.childs = new ArrayList<>();
//        this.webName = WebPermsHandler.getPermWebName(permCode);
        this.webName = name;
    }
}
