package com.touchealth.platform.processengine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<BaseMapper<T>, T> implements BaseService<T> {

	@Override
	public T baseLoad(T t) {
		return this.getOne(new QueryWrapper<T>(t));
	}

	@Override
	public List<T> baseFindList(T t) {
		QueryWrapper<T> qw = new QueryWrapper<>(t);

		return this.baseFindList(qw);
	}

	@Override
	public List<T> baseFindList(QueryWrapper<T> qw) {

		return this.list(qw);
	}

	@Override
	public Page<T> baseFindPage(T t, int current, int size) {
		QueryWrapper<T> qw = new QueryWrapper<>(t);

		return this.baseFindPage(qw, current, size);
	}

	@Override
	public Page<T> baseFindPage(QueryWrapper<T> qw, int current, int size) {
		Page<T> page = new Page<T>(current, size);
		qw.orderByDesc("created_time");

		return this.page(page, qw);
	}

	@Override
	public boolean baseModify(T update, T query) {
		return this.update(update, new QueryWrapper<T>(query));
	}

	@Override
	public boolean baseDelete(T query) {
		return this.remove(new QueryWrapper<T>(query));
	}
	
}
