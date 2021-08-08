-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`              bigint(0) NOT NULL AUTO_INCREMENT,
    `user_name`       varchar(64),
    `avatar`          varchar(255),
    `email`           varchar(64),
    `password`        varchar(64),
    `status`          int(0) NOT NULL, -- 0：正常 1：被锁定
    `last_login_time` datetime(0),
    `create_time`     datetime(0),
    `update_time`     datetime(0),
    PRIMARY KEY (`id`) USING BTREE,
    INDEX             `INDEX_USERNAME`(`user_name`)
) ENGINE = InnoDB;

-- ----------------------------
-- Records of t_user
-- ----------------------------
-- 密码为111111的md5值
INSERT INTO `t_user`
VALUES (1, 'blade',
        NULL,
        NULL, '96e79218965eb72c92a549dd5a330112', 0, NULL, NULL, NULL);
