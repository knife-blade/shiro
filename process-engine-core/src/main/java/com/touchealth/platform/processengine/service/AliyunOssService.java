package com.touchealth.platform.processengine.service;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.touchealth.platform.processengine.config.AliYunOssConfig;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.utils.StackTraceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 阿里云OSS服务
 * @program: touchealth-common
 * @author: xianghy
 * @create: 2020/8/28
 *
 * @author SunYang 20201120
 */
@Slf4j
@Service
public class AliyunOssService implements Closeable, InitializingBean {

    private OSSClient client;

    @Autowired
    private AliYunOssConfig aliYunOssConfig;

    /**
     * 上传
     * @param file  web传入的文件
     * @param path
     * @return
     */
    public String upload(MultipartFile file, String path) throws IOException {
        return upload(new ByteArrayInputStream(file.getBytes()), path, file.getContentType());
    }

    /**
     * 上传
     * @param data 二进制数据
     * @param path 文件路径，包括文件名。
     * @return
     */
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path, null);
    }

    /**
     * 上传
     * @param inputStream 数据流
     * @param path 文件路径，包括文件名。
     * @param contentType   文件类型
     * @return
     */
    public String upload(InputStream inputStream, String path, String contentType) {
        if (StringUtils.isEmpty(path)) {
            throw new ParameterException("文件名不能为空！");
        }

        // 处理路径信息,默认前缀，区分环境
        String prefix = aliYunOssConfig.getAliyunPrefix();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setCacheControl("max-age=31536000");
            if (!StringUtils.isEmpty(contentType)) {
                metadata.setContentType(contentType);
            }
            client.putObject(aliYunOssConfig.getAliyunBucketName(), prefix + path, inputStream, metadata);
        } catch (Exception e){
            log.error("AliyunOssService upload has error. {}", StackTraceUtil.getStackTrace(e));
            throw new ParameterException("上传文件失败，请检查配置信息");
        }

        return aliYunOssConfig.getAliyunDomain() + "/" + prefix + path;
    }

    public boolean doExist(String fileName) {
        return doExist(aliYunOssConfig.getAliyunBucketName(), fileName);
    }

    public boolean doExist(String bucketName, String fileName) {
        if (StringUtils.isEmpty(bucketName) || StringUtils.isEmpty(fileName)) {
            throw new ParameterException("参数不能为空！");
        }

        return client.doesObjectExist(bucketName, fileName);
    }

    public boolean delete(String fileName) {
        return delete(aliYunOssConfig.getAliyunBucketName(), fileName);
    }

    public boolean delete(String bucketName, String fileName) {
        if (StringUtils.isEmpty(bucketName) || StringUtils.isEmpty(fileName)) {
            throw new ParameterException("参数不能为空！");
        }

        try {
            client.deleteObject(bucketName, fileName);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }

    public void getObject(String path, String localFilePath) {
        if (StringUtils.isEmpty(path) || StringUtils.isEmpty(localFilePath)) {
            throw new ParameterException("参数不能为空！");
        }

        client.getObject(new GetObjectRequest(aliYunOssConfig.getAliyunBucketName(), path), new File(localFilePath));
    }

    public InputStream getObjectToStream(String objectName) {
        if (StringUtils.isEmpty(objectName)) {
            throw new ParameterException("参数不能为空！");
        }

        OSSObject ossObject = client.getObject(aliYunOssConfig.getAliyunBucketName(), objectName);
        return ossObject.getObjectContent();
    }

    @Override
    public void close() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() {
        init();
    }

    private void init(){
        client = (OSSClient) new OSSClientBuilder().build(aliYunOssConfig.getAliyunEndPoint(), aliYunOssConfig.getAliyunAccessKeyId(), aliYunOssConfig.getAliyunAccessKeySecret());
    }
}
