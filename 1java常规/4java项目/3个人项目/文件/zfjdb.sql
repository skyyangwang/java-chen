/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : zfjdb

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2014-12-10 23:10:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_cms_diary`
-- ----------------------------
DROP TABLE IF EXISTS `t_cms_diary`;
CREATE TABLE `t_cms_diary` (
  `diary_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `diary_addTime` datetime NOT NULL COMMENT '添加时间',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
  `diary_status` tinyint(4) NOT NULL COMMENT '状态 | 0普通 1置顶',
  `diary_type` tinyint(4) NOT NULL COMMENT '类型 | 0全部可见1自己可见',
  `diary_content` varchar(500) DEFAULT NULL COMMENT '内容',
  PRIMARY KEY (`diary_id`),
  KEY `fk_tCmsDiary_user_id` (`user_id`),
  CONSTRAINT `fk_tCmsDiary_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_cms_diary
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_dict_options`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dict_options`;
CREATE TABLE `t_sys_dict_options` (
  `options_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type_id` bigint(20) unsigned NOT NULL COMMENT '类型id',
  `sup_options_id` bigint(20) unsigned DEFAULT NULL COMMENT '父类id',
  `options_name` varchar(50) NOT NULL COMMENT '选项名',
  `options_sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序数字',
  `options_addTime` datetime NOT NULL,
  PRIMARY KEY (`options_id`),
  KEY `fk_tSysDictOptions_type_id` (`type_id`),
  KEY `fk_tSysDictOptions_sup_options_id` (`sup_options_id`),
  CONSTRAINT `fk_tSysDictOptions_sup_options_id` FOREIGN KEY (`sup_options_id`) REFERENCES `t_sys_dict_options` (`options_id`),
  CONSTRAINT `fk_tSysDictOptions_type_id` FOREIGN KEY (`type_id`) REFERENCES `t_sys_dict_type` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dict_options
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_dict_type`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dict_type`;
CREATE TABLE `t_sys_dict_type` (
  `type_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type_name` varchar(50) NOT NULL COMMENT '类型名',
  `type_sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序数字',
  `type_addTime` datetime NOT NULL COMMENT '添加日期',
  `type_explains` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dict_type
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_log`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_log`;
CREATE TABLE `t_sys_log` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name_` varchar(255) NOT NULL COMMENT '名称',
  `date_` datetime NOT NULL COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_resource`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_resource`;
CREATE TABLE `t_sys_resource` (
  `resource_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `sup_resource_id` bigint(20) unsigned DEFAULT NULL COMMENT '父类id',
  `res_name` varchar(50) NOT NULL COMMENT '资源名',
  `res_action` varchar(100) DEFAULT NULL COMMENT '动作',
  `res_url` varchar(100) DEFAULT NULL COMMENT 'url',
  `res_code` varchar(20) DEFAULT NULL COMMENT '代码',
  `res_sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `res_remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`resource_id`),
  KEY `fk_tSysResource_sup_resource_id` (`sup_resource_id`),
  CONSTRAINT `fk_tSysResource_sup_resource_id` FOREIGN KEY (`sup_resource_id`) REFERENCES `t_sys_resource` (`resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_resource
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) NOT NULL COMMENT '角色名',
  `role_status` tinyint(4) NOT NULL COMMENT '状态-0关闭1正常',
  `role_sequence` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `role_remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_role_resource`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_resource`;
CREATE TABLE `t_sys_role_resource` (
  `role_resource_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) unsigned NOT NULL COMMENT '角色id',
  `resource_id` bigint(20) unsigned NOT NULL COMMENT '资源id',
  PRIMARY KEY (`role_resource_id`),
  KEY `fk_tSysRoleResource_role_id` (`role_id`),
  KEY `fk_tSysRoleResource_resource_id` (`resource_id`),
  CONSTRAINT `fk_tSysRoleResource_resource_id` FOREIGN KEY (`resource_id`) REFERENCES `t_sys_resource` (`resource_id`),
  CONSTRAINT `fk_tSysRoleResource_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role_resource
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `area_id` bigint(20) DEFAULT '0' COMMENT '区域id',
  `dept_id` bigint(20) DEFAULT '0' COMMENT '部门id',
  `jobs_id` bigint(20) DEFAULT '0' COMMENT '岗位id',
  `user_name` varchar(50) NOT NULL COMMENT '用户名',
  `user_pwd` varchar(50) NOT NULL COMMENT '密码',
  `user_addTime` datetime NOT NULL COMMENT '建立时间',
  `user_updateTime` datetime NOT NULL COMMENT '修改时间',
  `user_status` tinyint(4) NOT NULL COMMENT '状态-0锁定1正常2注销',
  `user_remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
  `user_role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
  `role_id` bigint(20) unsigned NOT NULL COMMENT '角色id',
  PRIMARY KEY (`user_role_id`),
  KEY `fk_tSysUserRole_user_id` (`user_id`),
  KEY `fk_tSysUserRole_role_id` (`role_id`),
  CONSTRAINT `fk_tSysUserRole_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`user_id`),
  CONSTRAINT `fk_tSysUserRole_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------
