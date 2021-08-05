package com.touchealth.platform.processengine.pojo.dto.kezhu;
import com.touchealth.platform.processengine.common.constant.CommonObject;
import lombok.Data;
import java.util.List;

/**
 * @Description : 科助网点
 * @Author : dangshilin
 * @CreateTime : 2021年04月20日 15:32
 */
@Data
public class KeZhuBranchDto {
    /**
     * 医院ID
     */
    private Long id;

    /**
     * 医院名称
     */
    private String name;

    /**
     * 网点列表
     */
    private List<CommonObject> branchList;
}
