package com.touchealth.platform.processengine.pojo.dto.user;

import com.touchealth.platform.processengine.entity.user.PostJob;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 岗位树数据
 */
@Data
@NoArgsConstructor
public class PostJobTreeDto {

    /**
     * 岗位ID
     */
    private Long id;
    /**
     * 岗位编码
     */
    private String jobCode;
    /**
     * 岗位名称
     */
    private String jobName;
    /**
     * 部门ID
     */
    private Long pid;
    /**
     * 包含人数
     */
    private int count;

    public PostJobTreeDto(PostJob postJob) {
        this.id = postJob.getId();
        this.jobCode = postJob.getCode();
        this.jobName = postJob.getName();
        this.pid = postJob.getDeptId();
    }

}
