/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50511
Source Host           : localhost:3306
Source Database       : bank

Target Server Type    : MYSQL
Target Server Version : 50511
File Encoding         : 65001

Date: 2011-12-25 15:27:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `favorite_food`
-- ----------------------------
DROP TABLE IF EXISTS `favorite_food`;
CREATE TABLE `favorite_food` (
  `person_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `food` varchar(20) NOT NULL DEFAULT '',
  PRIMARY KEY (`person_id`,`food`),
  CONSTRAINT `fk_fav_food_person_id` FOREIGN KEY (`person_id`) REFERENCES `person` (`person_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of favorite_food
-- ----------------------------
