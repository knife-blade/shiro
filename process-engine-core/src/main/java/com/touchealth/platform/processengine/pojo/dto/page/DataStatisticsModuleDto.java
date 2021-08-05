package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

import java.util.List;

/**
 * @program: process-engine
 * @description: 页面数据统计展示信息
 * @author: xianghy
 * @create: 2020/12/11
 **/
@Data
public class DataStatisticsModuleDto {

    /**
     * 模块唯一ID
     */
    private Long moduleUniqueId;

    private List<ElementDto> elementDtoList;

    @Data
    public static class ElementDto {

        /**
         * 唯一ID
         */
        private Long uniqueId;

        /**
         * 次数
         */
        private String count;

        /**
         * 人数
         */
        private String personCount;

        /**
         * 空模块ID
         */
        private String blankId;

        public ElementDto() {
        }

        public ElementDto(Long uniqueId) {
            this.uniqueId = uniqueId;
        }
        public ElementDto(String blankId) {
            this.blankId = blankId;
        }
    }

    public DataStatisticsModuleDto() {
    }

    public DataStatisticsModuleDto(Long moduleUniqueId, List<ElementDto> elementDtoList) {
        this.moduleUniqueId = moduleUniqueId;
        this.elementDtoList = elementDtoList;
    }
}
