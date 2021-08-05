package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.entity.user.PostJob;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 岗位数据
 */
@Data
@NoArgsConstructor
public class PostJobDto {

    /**
     * 岗位ID
     */
    private Long postJobId;
    /**
     * 岗位编码
     */
    private String postJobCode;
    /**
     * 岗位名称
     */
    private String postJobName;
    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 包含人数
     */
    private Integer peopleCount;

    public PostJobDto(PostJob postJob) {
        this.postJobId = postJob.getId();
        this.postJobCode = postJob.getCode();
        this.postJobName = postJob.getName();
        this.departmentId = postJob.getDeptId();
    }

}
