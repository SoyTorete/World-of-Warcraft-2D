/*
Navicat MySQL Data Transfer

Source Server         : Localhost
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : wow_auth

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2019-02-16 21:41:07
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auth_realms
-- ----------------------------
DROP TABLE IF EXISTS `auth_realms`;
CREATE TABLE `auth_realms` (
  `realm_id` int(11) NOT NULL,
  `realm_name` varchar(32) DEFAULT NULL,
  `realm_port` int(5) DEFAULT NULL,
  PRIMARY KEY (`realm_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_users
-- ----------------------------
DROP TABLE IF EXISTS `auth_users`;
CREATE TABLE `auth_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `salt` varchar(255) DEFAULT NULL,
  `user_level` int(1) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user_bans
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_bans`;
CREATE TABLE `auth_user_bans` (
  `user_id` int(11) NOT NULL,
  `banned` varchar(255) DEFAULT NULL,
  `suspended` varchar(255) DEFAULT NULL,
  `suspension_time` date DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
