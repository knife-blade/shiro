DROP DATABASE IF EXISTS shiro;
CREATE DATABASE shiro DEFAULT CHARACTER SET utf8;
USE shiro;

DROP TABLE IF EXISTS t_user;
DROP TABLE IF EXISTS t_role;
DROP TABLE IF EXISTS t_permission;
DROP TABLE IF EXISTS t_user_role_mid;
DROP TABLE IF EXISTS t_role_permission_mid;

create table t_user (
  id bigint AUTO_INCREMENT,
  user_name VARCHAR(100),
  password VARCHAR(100),
  salt VARCHAR(100),
  PRIMARY KEY(id)
) charset=utf8 ENGINE=InnoDB;

create table t_role (
  id bigint AUTO_INCREMENT,
  name VARCHAR(100),
  description VARCHAR(100),
  PRIMARY KEY(id)
) charset=utf8 ENGINE=InnoDB;

create table t_permission (
  id bigint AUTO_INCREMENT,
  name VARCHAR(100),
  description VARCHAR(100),
  PRIMARY KEY(id)
) charset=utf8 ENGINE=InnoDB;

create table t_user_role_mid (
  id bigint AUTO_INCREMENT,
  user_id bigint,
  role_id bigint,
  PRIMARY KEY(id)
) charset=utf8 ENGINE=InnoDB;

create table t_role_permission_mid (
  id bigint AUTO_INCREMENT,
  role_id bigint,
  permission_id bigint,
  PRIMARY KEY(id)
) charset=utf8 ENGINE=InnoDB;

-- 密码：12345
INSERT INTO `t_user` VALUES (1,'zhang3','a7d59dfc5332749cb801f86a24f5f590','e5ykFiNwShfCXvBRPr3wXg==');
-- 密码：abcde
INSERT INTO `t_user` VALUES (2,'li4','43e28304197b9216e45ab1ce8dac831b','jPz19y7arvYIGhuUjsb6sQ==');
INSERT INTO `t_role` VALUES (1,'admin','超级管理员');
INSERT INTO `t_role` VALUES (2,'productManager','产品管理员');
INSERT INTO `t_role` VALUES (3,'orderManager','订单管理员');
INSERT INTO `t_permission` VALUES (1,'product:add','增加产品');
INSERT INTO `t_permission` VALUES (2,'product:delete','删除产品');
INSERT INTO `t_permission` VALUES (3,'product:edit','编辑产品');
INSERT INTO `t_permission` VALUES (4,'product:view','查看产品');
INSERT INTO `t_permission` VALUES (5,'order:add','增加订单');
INSERT INTO `t_permission` VALUES (6,'order:delete','删除订单');
INSERT INTO `t_permission` VALUES (7,'order:edit','编辑订单');
INSERT INTO `t_permission` VALUES (8,'order:view','查看订单');
INSERT INTO `t_user_role_mid` VALUES (1,2,2);
INSERT INTO `t_user_role_mid` VALUES (2,1,1);
INSERT INTO `t_role_permission_mid` VALUES (1,1,1);
INSERT INTO `t_role_permission_mid` VALUES (2,1,2);
INSERT INTO `t_role_permission_mid` VALUES (3,1,3);
INSERT INTO `t_role_permission_mid` VALUES (4,1,4);
INSERT INTO `t_role_permission_mid` VALUES (5,1,5);
INSERT INTO `t_role_permission_mid` VALUES (6,1,6);
INSERT INTO `t_role_permission_mid` VALUES (7,1,7);
INSERT INTO `t_role_permission_mid` VALUES (8,1,8);
INSERT INTO `t_role_permission_mid` VALUES (9,2,1);
INSERT INTO `t_role_permission_mid` VALUES (10,2,2);
INSERT INTO `t_role_permission_mid` VALUES (11,2,3);
INSERT INTO `t_role_permission_mid` VALUES (12,2,4);
INSERT INTO `t_role_permission_mid` VALUES (13,3,5);
INSERT INTO `t_role_permission_mid` VALUES (14,3,6);
INSERT INTO `t_role_permission_mid` VALUES (15,3,7);
INSERT INTO `t_role_permission_mid` VALUES (16,3,8);