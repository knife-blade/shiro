package com.touchealth.platform.processengine.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据对象
 * @param <T>
 */
@Data
@NoArgsConstructor
public class PageData<T> implements Serializable {

    private static final long serialVersionUID = 142005332185073340L;

    private Integer pageNo;
    private Integer pageSize;
    /**
     * 总记录数
     */
    private Integer total;
    /**
     * 总页数
     */
    private Integer totalPage;
    private List<T> data;

    public PageData(Integer pageNo, Integer pageSize, Integer total, Integer totalPage) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPage = totalPage;
    }

    public PageData(Integer pageNo, Integer pageSize, Integer total, List<T> data) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    public PageData(Integer pageNo, Integer pageSize, Integer total, Integer totalPage, List<T> data) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPage = totalPage;
        this.data = data;
    }
}
