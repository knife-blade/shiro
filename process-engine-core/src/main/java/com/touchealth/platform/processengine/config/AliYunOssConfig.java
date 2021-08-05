package com.touchealth.platform.processengine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @description: 阿里云oss配置文件
 * @author: xianghy
 **/
@Data
@Configuration
public class AliYunOssConfig implements Serializable {

    private static final long serialVersionUID = -6711431204988090941L;

    /**
     * 阿里云绑定的域名
     */
    @Value("${oss.aliyun.domain}")
    private String aliyunDomain;

    /**
     * 阿里云路径前缀
     */
    @Value("${oss.aliyun.prefix}")
    private String aliyunPrefix;

    /**
     * 阿里云EndPoint
     */
    @Value("${oss.aliyun.end.point}")
    private String aliyunEndPoint;

    /**
     * 阿里云AccessKeyId
     */
    @Value("${oss.aliyun.access.key.id}")
    private String aliyunAccessKeyId;

    /**
     * 阿里云AccessKeySecret
     */
    @Value("${oss.aliyun.access.key.secret}")
    private String aliyunAccessKeySecret;

    /**
     * 阿里云BucketName
     */
    @Value("${oss.aliyun.bucket.name}")
    private String aliyunBucketName;

}
