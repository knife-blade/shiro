package com.touchealth.platform.processengine.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.Serializable;
import java.util.List;


public interface BaseService<T> extends IService<T>{

	T  baseLoad(T t);

	List<T> baseFindList(T t);

	List<T> baseFindList(QueryWrapper<T> qw);

	Page<T> baseFindPage(T t, int current, int size);

	Page<T> baseFindPage(QueryWrapper<T> qw, int current, int size);

	boolean baseModify(T update, T query);

	boolean baseDelete(T query);
	
}
