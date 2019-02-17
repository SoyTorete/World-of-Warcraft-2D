/*
Navicat MySQL Data Transfer

Source Server         : Localhost
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : wow_char

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2019-02-16 21:41:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_characters
-- ----------------------------
DROP TABLE IF EXISTS `user_characters`;
CREATE TABLE `user_characters` (
  `user_id` int(11) NOT NULL,
  `realm_id` int(11) DEFAULT NULL,
  `character_name` varchar(16) DEFAULT NULL,
  `x_position` float(255,0) DEFAULT NULL,
  `y_position` float(255,0) DEFAULT NULL,
  `direction` int(11) DEFAULT NULL,
  `zone` int(11) DEFAULT NULL,
  `race` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
