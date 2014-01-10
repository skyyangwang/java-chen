/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : website

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2013-01-22 12:41:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `about_us`
-- ----------------------------
DROP TABLE IF EXISTS `about_us`;
CREATE TABLE `about_us` (
  `ab_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `version` int(11) DEFAULT NULL,
  `title` varchar(50) NOT NULL,
  `content` text,
  PRIMARY KEY (`ab_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of about_us
-- ----------------------------
INSERT INTO `about_us` VALUES ('1', '0', '公司简介1', '<p>\r\n	软件公司</p>\r\n');
INSERT INTO `about_us` VALUES ('2', '0', '企业理念', '<p>\r\n	发展，共赢，人才</p>\r\n');
INSERT INTO `about_us` VALUES ('6', '0', '人才理念', '<p>\r\n	<img alt=\"\" src=\"/sshWeb/WebsiteBackstage/userfiles/images/71067c66x9d2d544560c3%26690.jpeg\" style=\"width: 440px; height: 534px; \" />团队，发展，自由，收获！</p>\r\n');

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `admin_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `pwd` varchar(50) NOT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'admin', 'ISMvKXpXpadDiUoOSoAfww==');
INSERT INTO `admin` VALUES ('9', 'aaa', 'R7zlx09Yn0hn29V+nKn4CA==');

-- ----------------------------
-- Table structure for `advertising`
-- ----------------------------
DROP TABLE IF EXISTS `advertising`;
CREATE TABLE `advertising` (
  `adv_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `adv_type_id` bigint(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `urls` varchar(100) DEFAULT NULL,
  `img_path` varchar(50) DEFAULT NULL,
  `add_time` datetime NOT NULL,
  `state` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`adv_id`),
  KEY `fk_adv_adv_type_id` (`adv_type_id`),
  CONSTRAINT `fk_adv_adv_type_id` FOREIGN KEY (`adv_type_id`) REFERENCES `advertising_type` (`adv_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of advertising
-- ----------------------------

-- ----------------------------
-- Table structure for `advertising_type`
-- ----------------------------
DROP TABLE IF EXISTS `advertising_type`;
CREATE TABLE `advertising_type` (
  `adv_type_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`adv_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of advertising_type
-- ----------------------------

-- ----------------------------
-- Table structure for `download_file`
-- ----------------------------
DROP TABLE IF EXISTS `download_file`;
CREATE TABLE `download_file` (
  `file_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `file_name` varchar(50) NOT NULL,
  `file_path` varchar(50) NOT NULL,
  `file_size` varchar(20) DEFAULT NULL,
  `download_times` int(11) NOT NULL DEFAULT '0',
  `developers` varchar(50) DEFAULT NULL,
  `properties` tinyint(4) NOT NULL DEFAULT '0',
  `explain` text,
  `date_retired` datetime NOT NULL,
  `state` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`file_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of download_file
-- ----------------------------
INSERT INTO `download_file` VALUES ('13', '上传1', 'ye1.jpg', '', '0', 'aa', '0', null, '2012-04-15 00:00:00', '0');
INSERT INTO `download_file` VALUES ('15', '中文文件', '叶梓萱2.jpg', '', '0', 'aa', '0', null, '2012-04-21 00:00:00', '0');
INSERT INTO `download_file` VALUES ('17', '上传3', '71067c66x9d2d544560c3&690.jpeg', '', '0', 'aa', '1', null, '2012-05-04 00:00:00', '0');
INSERT INTO `download_file` VALUES ('18', 'aaa', 'ye1.jpg', '', '0', 'aaa', '1', null, '2012-05-24 00:00:00', '0');
INSERT INTO `download_file` VALUES ('19', 'bbb', '71067c66x9d2d544560c3&690.jpeg', '', '0', 'aaa', '1', null, '2012-05-24 00:00:00', '0');
INSERT INTO `download_file` VALUES ('21', 'sss', '71067c66x9d2d544560c3&690.jpeg', '', '0', 'sss', '1', null, '2012-05-09 00:00:00', '0');

-- ----------------------------
-- Table structure for `job`
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `job_id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `position` varchar(50) NOT NULL,
  `numbers` tinyint(4) NOT NULL DEFAULT '0',
  `address` varchar(20) DEFAULT NULL,
  `work_seniority` varchar(10) DEFAULT NULL,
  `education` varchar(10) DEFAULT NULL,
  `content` text,
  `add_time` date NOT NULL,
  `job_state` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of job
-- ----------------------------

-- ----------------------------
-- Table structure for `news`
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `news_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `news_type_id` bigint(10) unsigned NOT NULL,
  `title` varchar(50) NOT NULL,
  `author` varchar(50) DEFAULT NULL,
  `source` varchar(50) DEFAULT NULL,
  `add_time` datetime NOT NULL,
  `hits` int(11) NOT NULL DEFAULT '0',
  `tags` varchar(50) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `content` text,
  `news_types` tinyint(4) NOT NULL DEFAULT '0',
  `image_path` varchar(50) DEFAULT NULL,
  `is_top` tinyint(4) NOT NULL DEFAULT '0',
  `is_quality` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`news_id`),
  KEY `fk_news_news_type_id` (`news_type_id`),
  CONSTRAINT `fk_news_news_type_id` FOREIGN KEY (`news_type_id`) REFERENCES `news_type` (`news_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES ('11', '24', 'mysql数据库技术', '陈', 'me', '2012-11-17 00:00:00', '0', 'mysql', '数据库技术', '<p>\r\n	mysql数据库技术：</p>\r\n<p>\r\n	高级；</p>\r\n', '0', '', '1', '0');
INSERT INTO `news` VALUES ('12', '18', 'ssh技术', '陈', '原创', '1912-11-22 00:00:00', '0', 'ssh', 'ssh技术', '<p>\r\n	缓存，事务；</p>\r\n', '0', '', '0', '0');

-- ----------------------------
-- Table structure for `news_type`
-- ----------------------------
DROP TABLE IF EXISTS `news_type`;
CREATE TABLE `news_type` (
  `news_type_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `superior_news_type_id` bigint(11) unsigned DEFAULT NULL,
  `serial_number` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`news_type_id`),
  KEY `fk_nt_superior_news_type_id` (`superior_news_type_id`),
  CONSTRAINT `fk_nt_superior_news_type_id` FOREIGN KEY (`superior_news_type_id`) REFERENCES `news_type` (`news_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of news_type
-- ----------------------------
INSERT INTO `news_type` VALUES ('18', '公司新闻', null, '0');
INSERT INTO `news_type` VALUES ('19', '行业新闻', null, '0');
INSERT INTO `news_type` VALUES ('22', '技术开发', '19', '0');
INSERT INTO `news_type` VALUES ('24', 'java技术', '22', '0');

-- ----------------------------
-- Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_cd` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `product_type_cd` varchar(20) NOT NULL,
  `price` float NOT NULL,
  `discount_price` float NOT NULL,
  `amount` int(11) NOT NULL DEFAULT '0',
  `balance` int(11) NOT NULL DEFAULT '0',
  `add_time` datetime NOT NULL,
  `hits` int(11) NOT NULL DEFAULT '0',
  `description` varchar(200) DEFAULT NULL,
  `product_inf` text,
  `tags` varchar(50) DEFAULT NULL,
  `img_path` varchar(50) DEFAULT NULL,
  `is_recommend` tinyint(4) NOT NULL DEFAULT '0',
  `is_discount` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`product_cd`),
  KEY `fk_product_product_type_cd` (`product_type_cd`),
  CONSTRAINT `fk_product_product_type_cd` FOREIGN KEY (`product_type_cd`) REFERENCES `product_type` (`product_type_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product
-- ----------------------------

-- ----------------------------
-- Table structure for `product_type`
-- ----------------------------
DROP TABLE IF EXISTS `product_type`;
CREATE TABLE `product_type` (
  `product_type_cd` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `superior_product_type_cd` varchar(20) NOT NULL,
  `serial_number` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`product_type_cd`),
  KEY `fk_pt_superior_product_type_cd` (`superior_product_type_cd`),
  CONSTRAINT `fk_pt_superior_product_type_cd` FOREIGN KEY (`superior_product_type_cd`) REFERENCES `product_type` (`product_type_cd`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product_type
-- ----------------------------

-- ----------------------------
-- Table structure for `t_log_entry`
-- ----------------------------
DROP TABLE IF EXISTS `t_log_entry`;
CREATE TABLE `t_log_entry` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name_` varchar(50) NOT NULL,
  `date_` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_log_entry
-- ----------------------------
INSERT INTO `t_log_entry` VALUES ('1', 'aaa', '2012-04-12 14:12:37');
INSERT INTO `t_log_entry` VALUES ('2', 'bbb', '2012-03-15 00:00:00');

-- ----------------------------
-- Table structure for `t_sys_area`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_area`;
CREATE TABLE `t_sys_area` (
  `area_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `superior_area_id` bigint(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `area_code` varchar(20) DEFAULT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `serial_number` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`area_id`),
  KEY `fk_tSysArea_superior_area_id` (`superior_area_id`),
  CONSTRAINT `fk_tSysArea_superior_area_id` FOREIGN KEY (`superior_area_id`) REFERENCES `t_sys_area` (`area_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_area
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_dept`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dept`;
CREATE TABLE `t_sys_dept` (
  `dept_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `superior_dept_id` bigint(11) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `serial_number` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`dept_id`),
  KEY `fk_tSysDept_superior_dept_id` (`superior_dept_id`),
  CONSTRAINT `fk_tSysDept_superior_dept_id` FOREIGN KEY (`superior_dept_id`) REFERENCES `t_sys_dept` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dept
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_dept_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dept_role`;
CREATE TABLE `t_sys_dept_role` (
  `dept_role_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `dept_id` bigint(10) unsigned NOT NULL,
  `role_id` bigint(10) unsigned NOT NULL,
  PRIMARY KEY (`dept_role_id`),
  KEY `fk_tSysDeptRole_role_id` (`role_id`),
  KEY `fk_tSysDeptRole_dept_id` (`dept_id`),
  CONSTRAINT `fk_tSysDeptRole_dept_id` FOREIGN KEY (`dept_id`) REFERENCES `t_sys_dept` (`dept_id`),
  CONSTRAINT `fk_tSysDeptRole_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dept_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_menu`;
CREATE TABLE `t_sys_menu` (
  `menu_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `superior_menu_id` bigint(10) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `url` varchar(200) DEFAULT NULL,
  `menu_code` varchar(20) DEFAULT NULL,
  `remarks` varchar(200) DEFAULT NULL,
  `serial_number` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`menu_id`),
  KEY `fk_tSysMenu_superior_menu_id` (`superior_menu_id`),
  CONSTRAINT `fk_tSysMenu_superior_menu_id` FOREIGN KEY (`superior_menu_id`) REFERENCES `t_sys_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_menu
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `role_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  `serial_number` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_menu`;
CREATE TABLE `t_sys_role_menu` (
  `role_menu_id` bigint(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` bigint(10) unsigned NOT NULL,
  `menu_id` bigint(10) unsigned NOT NULL,
  PRIMARY KEY (`role_menu_id`),
  KEY `fk_tSysRoleMenu_menu_id` (`menu_id`),
  KEY `fk_tSysRoleMenu_role_id` (`role_id`),
  CONSTRAINT `fk_tSysRoleMenu_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`),
  CONSTRAINT `fk_tSysRoleMenu_menu_id` FOREIGN KEY (`menu_id`) REFERENCES `t_sys_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
  `user_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `area_id` bigint(20) unsigned NOT NULL,
  `dept_id` bigint(20) unsigned NOT NULL,
  `name` varchar(50) NOT NULL,
  `pwd` varchar(50) NOT NULL,
  `createTime` date NOT NULL,
  `remarks` varchar(500) DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`user_id`),
  KEY `fk_user_area_id` (`area_id`),
  KEY `fk_user_dept_id` (`dept_id`),
  CONSTRAINT `fk_tSysUser_dept_id` FOREIGN KEY (`dept_id`) REFERENCES `t_sys_dept` (`dept_id`),
  CONSTRAINT `fk_tSysUser_area_id` FOREIGN KEY (`area_id`) REFERENCES `t_sys_area` (`area_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
  `user_role_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL,
  `role_id` bigint(10) unsigned NOT NULL,
  PRIMARY KEY (`user_role_id`),
  KEY `fk_tSysUserRole_role_id` (`role_id`),
  KEY `fk_tSysUserRole_user_id` (`user_id`),
  CONSTRAINT `fk_tSysUserRole_user_id` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`user_id`),
  CONSTRAINT `fk_tSysUserRole_role_id` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------
