-- 添加登录组件标识
alter table page_manager
	add is_sign tinyint default 0 not null comment '是否是登录组件；0|否 1|是';

-- 用户登录历史token过期时间
ALTER TABLE user_login_history add expiration_time datetime default null comment '过期时间';
