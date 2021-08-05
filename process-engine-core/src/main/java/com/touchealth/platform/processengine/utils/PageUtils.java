package com.touchealth.platform.processengine.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchealth.common.basic.response.PageInfo;
import com.touchealth.common.basic.utils.BaseHelper;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2021-04-21 14:11:36
 */
public class PageUtils {

    public static <T> PageInfo<T> changePage(Page<?> page, Class<T> clazz) {
        return new PageInfo<>((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal(), BaseHelper.r2t(page.getRecords(), clazz));
    }

    public static <T> PageInfo<T> changePage(Page<?> page, List<T> data) {
        return new PageInfo<>((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal(), data);
    }

    public static <T> PageInfo<T> changePage(PageInfo<?> setMealPage, List<T> data) {
        return new PageInfo<T>(setMealPage.getPageNo(), setMealPage.getPageSize(), setMealPage.getTotal(), data);
    }
}
