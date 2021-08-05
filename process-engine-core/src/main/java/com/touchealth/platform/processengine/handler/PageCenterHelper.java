package com.touchealth.platform.processengine.handler;

import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.page.PageOperationLog;
import com.touchealth.platform.processengine.pojo.dto.page.VersionOperationLogDto;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liufengqiang
 * @date 2021-01-20 15:23:19
 */
public class PageCenterHelper {

    public static VersionOperationLogDto getLogContent(PageOperationLog log, String imgTag) {
        if (log.getFolderName() == null) {
            log.setFolderName("");
        }
        if (StringUtils.isBlank(log.getInitValue())) {
            log.setInitValue("无");
        }
        if (StringUtils.isBlank(log.getFinishValue())) {
            log.setFinishValue("无");
        }
        String folderTip = StringUtils.isNotBlank(log.getFolderName()) ? "文件夹内的 " : " ";

        VersionOperationLogDto logDto = new VersionOperationLogDto();
        StringBuilder result = new StringBuilder();
        switch (PageCenterConsts.CombinationType.values()[log.getCombinationType()]) {
            case TYPE_PAGE_A:
                logDto.setContent(result.append(log.getOperate()).append(" ").append(log.getFolderName())
                        .append(folderTip).append(log.getPageName()).append(" 页面").toString());
                break;
            case TYPE_PAGE_B:
                logDto.setContent(result.append(log.getOperate()).append(" ").append(log.getFolderName()).append(folderTip)
                        .append(log.getPageName()).append(" 页面内的").append(log.getName()).append("从")
                        .append(log.getInitValue()).append("到").append(log.getFinishValue()).toString());
                break;
            case TYPE_FOLDER_A:
                logDto.setContent(result.append(log.getOperate()).append(" ").append(log.getPageName()).append("文件夹").toString());
                break;
            case TYPE_FOLDER_B:
                logDto.setContent(result.append(log.getOperate()).append(" ").append(log.getPageName()).append(" 文件夹的")
                        .append(log.getName()).append("从").append(log.getInitValue()).append("到").append(log.getFinishValue()).toString());
                break;
            case TYPE_RELEASE_A:
                logDto.setContent(result.append(log.getContent()).append(" ").append(log.getOperate()).append(" ")
                        .append(log.getInitValue()).append("到").append(log.getFinishValue()).toString());
                break;
            case TYPE_COMPONENT_A:
                if (log.getInitValue().contains(imgTag)) {
                    logDto.setParam1(log.getInitValue());
                    log.setInitValue("$1");
                }
                if (log.getFinishValue().contains(imgTag)) {
                    logDto.setParam2(log.getFinishValue());
                    log.setFinishValue("$2");
                }
                logDto.setContent(result.append(log.getOperate()).append(" ").append(log.getFolderName()).append(folderTip)
                        .append(log.getPageName()).append(" 页面内的").append(log.getModuleName()).append(log.getContent())
                        .append(log.getName()).append("从").append(log.getInitValue()).append("到").append(log.getFinishValue()).toString());
                break;
            case TYPE_COMPONENT_B:
                logDto.setContent(result.append(log.getOperate()).append(" ").append(log.getFolderName()).append(folderTip)
                        .append(log.getPageName()).append(" 页面内的").append(log.getModuleName()).toString());
                break;
            default:
                break;
        }
        return logDto;
    }
}
