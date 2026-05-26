-- MySQL dump 10.13  Distrib 8.4.9, for Win64 (x86_64)
--
-- Host: localhost    Database: swimming_coach_platform
-- ------------------------------------------------------
-- Server version	8.4.9

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `swimming_coach_platform`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `swimming_coach_platform` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `swimming_coach_platform`;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '地点',
  `status` enum('PENDING','CONFIRMED','COMPLETED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'PENDING' COMMENT '状态：待确认/已确认/已完成/已取消',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '备注',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coach_id` (`coach_id`) USING BTREE,
  KEY `idx_student_id` (`student_id`) USING BTREE,
  KEY `idx_date` (`appointment_date`) USING BTREE,
  CONSTRAINT `appointments_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `appointments_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='预约时间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_messages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'TEXT' COMMENT '消息类型',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sender` (`sender_id`) USING BTREE,
  KEY `idx_receiver` (`receiver_id`) USING BTREE,
  KEY `idx_time` (`send_time`) USING BTREE,
  CONSTRAINT `chat_messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `chat_messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coach_profiles`
--

DROP TABLE IF EXISTS `coach_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coach_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像URL',
  `certificate_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '教练资格证件号',
  `id_card_number` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '身份证号',
  `certificate_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '证件类型（如：国家游泳教练证）',
  `experience_years` int DEFAULT NULL COMMENT '执教年限',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '个人简介',
  `qualification_verified` tinyint DEFAULT '0' COMMENT '资格认证状态：0-待审核，1-已通过，2-未通过',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE,
  UNIQUE KEY `id_card_number` (`id_card_number`) USING BTREE,
  UNIQUE KEY `certificate_number` (`certificate_number`) USING BTREE,
  KEY `idx_certificate_number` (`certificate_number`) USING BTREE,
  CONSTRAINT `coach_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='教练详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coach_shares`
--

DROP TABLE IF EXISTS `coach_shares`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coach_shares` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分享ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '内容描述',
  `case_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '教学案例描述',
  `student_id` bigint DEFAULT NULL COMMENT '相关学员ID（可选）',
  `images` json DEFAULT NULL COMMENT '图片URL列表（JSON数组）',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '视频URL',
  `likes_count` int DEFAULT '0' COMMENT '点赞数',
  `comments_count` int DEFAULT '0' COMMENT '评论数',
  `is_public` tinyint DEFAULT '1' COMMENT '是否公开：0-私有，1-公开',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coach_id` (`coach_id`) USING BTREE,
  KEY `student_id` (`student_id`) USING BTREE,
  CONSTRAINT `coach_shares_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `coach_shares_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='教练教学成功分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `coach_student_relations`
--

DROP TABLE IF EXISTS `coach_student_relations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coach_student_relations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `relationship_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '正式学员' COMMENT '关系类型',
  `start_date` date DEFAULT NULL COMMENT '开始关联日期',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-已结束，1-进行中',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_coach_student` (`coach_id`,`student_id`) USING BTREE,
  KEY `idx_coach_id` (`coach_id`) USING BTREE,
  KEY `idx_student_id` (`student_id`) USING BTREE,
  CONSTRAINT `coach_student_relations_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `coach_student_relations_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='教练-学员关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '评论者ID',
  `share_type` enum('STUDENT','COACH') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分享类型：学员/教练',
  `share_id` bigint NOT NULL COMMENT '分享记录ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_share` (`share_type`,`share_id`) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `plan_id` bigint DEFAULT NULL COMMENT '关联的计划ID（可选）',
  `message_type` enum('TEXT','FEEDBACK','NOTIFICATION') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'TEXT' COMMENT '消息类型：文本/反馈/通知',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sender_id` (`sender_id`) USING BTREE,
  KEY `idx_receiver_id` (`receiver_id`) USING BTREE,
  KEY `idx_plan_id` (`plan_id`) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_3` FOREIGN KEY (`plan_id`) REFERENCES `training_plans` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='消息通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `plan_details`
--

DROP TABLE IF EXISTS `plan_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plan_details` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `training_item` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '训练项目（如：自由泳、蛙泳等）',
  `duration` int DEFAULT NULL COMMENT '训练时长（分钟）',
  `distance` int DEFAULT NULL COMMENT '训练距离（米）',
  `intensity` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '强度等级',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '备注说明',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `day_number` int DEFAULT '1' COMMENT '第几天',
  `is_checked` tinyint DEFAULT '0' COMMENT '是否打卡',
  `training_date` date DEFAULT NULL COMMENT '训练日期',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_plan_id` (`plan_id`) USING BTREE,
  CONSTRAINT `plan_details_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `training_plans` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='计划详情表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `post_comment_likes`
--

DROP TABLE IF EXISTS `post_comment_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comment_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment_id` bigint NOT NULL COMMENT '璇勮?ID',
  `user_id` bigint NOT NULL COMMENT '鐐硅禐鐢ㄦ埛ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `post_comment_likes_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `post_comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `post_comment_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='甯栧瓙璇勮?鐐硅禐琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `post_comments`
--

DROP TABLE IF EXISTS `post_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID（回复）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `like_count` int DEFAULT '0' COMMENT '鐐硅禐鏁',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '鏄?惁鍒犻櫎',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `idx_post_id` (`post_id`) USING BTREE,
  KEY `idx_post_comments_parent_id` (`parent_id`),
  KEY `idx_post_comments_post_id_created_at` (`post_id`,`created_at`),
  CONSTRAINT `post_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `post_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='帖子评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `post_likes`
--

DROP TABLE IF EXISTS `post_likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '甯栧瓙ID',
  `user_id` bigint NOT NULL COMMENT '鐐硅禐鐢ㄦ埛ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `post_likes_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `post_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='甯栧瓙鐐硅禐琛';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '发帖用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `plan_detail_id` bigint DEFAULT NULL COMMENT '关联的训练详情ID',
  `day_number` int DEFAULT NULL COMMENT '打卡第几天',
  `plan_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '训练计划名称',
  `training_date` date DEFAULT NULL COMMENT '训练日期',
  `intensity` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '强度等级',
  `duration` int DEFAULT NULL COMMENT '时长（分钟）',
  `distance` int DEFAULT NULL COMMENT '距离（米）',
  `show_training_info` tinyint(1) DEFAULT '0' COMMENT '是否显示训练信息',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `plan_detail_id` (`plan_detail_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE,
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `posts_ibfk_2` FOREIGN KEY (`plan_detail_id`) REFERENCES `plan_details` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `student_profiles`
--

DROP TABLE IF EXISTS `student_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '档案ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像URL',
  `gender` enum('MALE','FEMALE','OTHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '性别',
  `age` int DEFAULT NULL COMMENT '年龄',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE,
  CONSTRAINT `student_profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='学员详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `student_shares`
--

DROP TABLE IF EXISTS `student_shares`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_shares` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分享ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '内容描述',
  `images` json DEFAULT NULL COMMENT '图片URL列表（JSON数组）',
  `video_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '视频URL',
  `plan_id` bigint DEFAULT NULL COMMENT '关联的训练计划ID',
  `likes_count` int DEFAULT '0' COMMENT '点赞数',
  `comments_count` int DEFAULT '0' COMMENT '评论数',
  `is_public` tinyint DEFAULT '1' COMMENT '是否公开：0-私有，1-公开',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_student_id` (`student_id`) USING BTREE,
  KEY `idx_plan_id` (`plan_id`) USING BTREE,
  CONSTRAINT `student_shares_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_shares_ibfk_2` FOREIGN KEY (`plan_id`) REFERENCES `training_plans` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='学员训练分享表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `time_slots`
--

DROP TABLE IF EXISTS `time_slots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `time_slots` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `slot_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时间段名称',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `max_capacity` int DEFAULT '5' COMMENT '最大预约人数',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `is_active` tinyint DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `training_plans`
--

DROP TABLE IF EXISTS `training_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `training_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `coach_id` bigint NOT NULL COMMENT '教练ID',
  `student_id` bigint NOT NULL COMMENT '学员ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '计划描述',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` enum('DRAFT','ACTIVE','COMPLETED','CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'DRAFT' COMMENT '状态：草稿/进行中/已完成/已取消',
  `ai_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'AI建议内容',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_coach_id` (`coach_id`) USING BTREE,
  KEY `idx_student_id` (`student_id`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  CONSTRAINT `training_plans_ibfk_1` FOREIGN KEY (`coach_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `training_plans_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='训练计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像URL',
  `role` enum('COACH','STUDENT') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色：教练/学员',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE,
  KEY `idx_username` (`username`) USING BTREE,
  KEY `idx_role` (`role`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` (`id`, `coach_id`, `student_id`, `appointment_date`, `start_time`, `end_time`, `location`, `status`, `notes`, `created_at`, `updated_at`) VALUES (18,6,10,'2026-05-26','08:30:00','10:00:00','','CANCELLED','','2026-05-25 05:33:49','2026-05-25 05:33:59');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;

/*!40000 ALTER TABLE `chat_messages` DISABLE KEYS */;
INSERT INTO `chat_messages` (`id`, `sender_id`, `receiver_id`, `content`, `message_type`, `is_read`, `send_time`) VALUES (1,2,1,'11','TEXT',1,'2026-05-09 11:05:51'),(2,2,1,'11','TEXT',1,'2026-05-09 11:05:51'),(3,1,2,'6657','TEXT',1,'2026-05-09 11:06:10'),(4,1,2,'6657','TEXT',1,'2026-05-09 11:06:10'),(5,1,2,'6657','TEXT',1,'2026-05-09 11:06:23'),(6,1,2,'6657','TEXT',1,'2026-05-09 11:06:23'),(7,2,1,'1','TEXT',1,'2026-05-09 11:06:36'),(8,2,1,'1','TEXT',1,'2026-05-09 11:06:36'),(9,1,2,'1','TEXT',1,'2026-05-09 11:16:10'),(10,2,1,'你好王教练','TEXT',1,'2026-05-09 11:17:30'),(11,6,10,'不错呀','TEXT',1,'2026-05-25 05:49:02'),(12,10,6,'谢谢教练','TEXT',1,'2026-05-25 05:50:35'),(13,6,15,'看得到不','TEXT',1,'2026-05-25 18:12:52'),(14,15,6,'看得到','TEXT',1,'2026-05-25 18:14:36');
/*!40000 ALTER TABLE `chat_messages` ENABLE KEYS */;

/*!40000 ALTER TABLE `coach_profiles` DISABLE KEYS */;
INSERT INTO `coach_profiles` (`id`, `user_id`, `real_name`, `phone`, `email`, `avatar`, `certificate_number`, `id_card_number`, `certificate_type`, `experience_years`, `introduction`, `qualification_verified`, `created_at`, `updated_at`) VALUES (1,4,'许洋帆','19977233756','19977233756@163.com',NULL,NULL,NULL,NULL,NULL,NULL,2,'2026-05-13 14:10:57','2026-05-15 08:53:54'),(2,5,'大志','12233445678','12233445678@163.com',NULL,NULL,NULL,NULL,NULL,NULL,0,'2026-05-13 14:39:21','2026-05-13 14:48:20'),(3,6,'大志','19955888745','19955888745@163.com',NULL,NULL,NULL,NULL,NULL,NULL,1,'2026-05-13 14:42:41','2026-05-13 14:44:56'),(4,7,'大志','19955888745','19955888745@163.com',NULL,NULL,NULL,NULL,NULL,NULL,2,'2026-05-14 09:28:53','2026-05-14 09:28:53'),(5,13,'教练1','19977231935','19977231935@163.com',NULL,NULL,NULL,NULL,NULL,NULL,2,'2026-05-25 06:17:31','2026-05-25 06:17:31');
/*!40000 ALTER TABLE `coach_profiles` ENABLE KEYS */;

/*!40000 ALTER TABLE `coach_shares` DISABLE KEYS */;
/*!40000 ALTER TABLE `coach_shares` ENABLE KEYS */;

/*!40000 ALTER TABLE `coach_student_relations` DISABLE KEYS */;
INSERT INTO `coach_student_relations` (`id`, `coach_id`, `student_id`, `relationship_type`, `start_date`, `status`, `created_at`, `updated_at`) VALUES (1,6,2,'正式学员','2026-05-15',1,'2026-05-15 09:08:49','2026-05-15 09:08:49'),(3,6,10,'正式学员','2026-05-15',1,'2026-05-15 09:48:51','2026-05-15 09:48:51'),(4,6,11,'正式学员','2026-05-15',1,'2026-05-15 10:53:05','2026-05-15 10:53:05'),(5,13,12,'正式学员','2026-05-25',1,'2026-05-25 06:19:09','2026-05-25 06:19:09'),(6,13,11,'正式学员','2026-05-25',1,'2026-05-25 06:19:36','2026-05-25 06:19:36'),(7,6,14,'正式学员','2026-05-25',1,'2026-05-25 11:25:48','2026-05-25 11:25:48'),(8,6,15,'正式学员','2026-05-25',1,'2026-05-25 17:26:52','2026-05-25 17:26:52');
/*!40000 ALTER TABLE `coach_student_relations` ENABLE KEYS */;

/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;

/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;

/*!40000 ALTER TABLE `plan_details` DISABLE KEYS */;
INSERT INTO `plan_details` (`id`, `plan_id`, `training_item`, `duration`, `distance`, `intensity`, `notes`, `sort_order`, `created_at`, `day_number`, `is_checked`, `training_date`) VALUES (6,7,NULL,NULL,NULL,'低','111',1,'2026-05-20 08:51:05',1,1,'2026-05-20'),(7,7,NULL,NULL,NULL,'中等','2',1,'2026-05-20 08:51:05',2,0,'2026-05-21'),(8,7,NULL,NULL,NULL,'高','3',1,'2026-05-20 08:51:05',3,1,'2026-05-22'),(9,7,NULL,NULL,NULL,'低','4',1,'2026-05-20 08:51:05',4,0,'2026-05-23'),(10,7,NULL,NULL,NULL,'中等','5',1,'2026-05-20 08:51:05',5,0,'2026-05-24'),(11,8,'',NULL,NULL,'','',1,'2026-05-25 11:23:44',1,0,'2026-05-25'),(12,8,'',NULL,NULL,'','',1,'2026-05-25 11:23:44',2,0,'2026-05-26'),(13,8,'',NULL,NULL,'','',1,'2026-05-25 11:23:44',3,0,'2026-05-27'),(14,8,'',NULL,NULL,'','',1,'2026-05-25 11:23:44',4,0,'2026-05-28'),(15,8,'',NULL,NULL,'','',1,'2026-05-25 11:23:44',5,0,'2026-05-29');
/*!40000 ALTER TABLE `plan_details` ENABLE KEYS */;

/*!40000 ALTER TABLE `post_comment_likes` DISABLE KEYS */;
INSERT INTO `post_comment_likes` (`id`, `comment_id`, `user_id`, `created_at`) VALUES (1,1,10,'2026-05-25 18:05:32');
/*!40000 ALTER TABLE `post_comment_likes` ENABLE KEYS */;

/*!40000 ALTER TABLE `post_comments` DISABLE KEYS */;
INSERT INTO `post_comments` (`id`, `post_id`, `user_id`, `content`, `parent_id`, `created_at`, `like_count`, `updated_at`, `is_deleted`) VALUES (1,1,10,'不错',NULL,'2026-05-25 18:05:30',1,'2026-05-25 18:05:32',0),(2,1,6,'不错在哪',1,'2026-05-25 18:06:11',0,'2026-05-25 18:06:11',0),(3,1,6,'不错嘛',NULL,'2026-05-25 18:06:20',0,'2026-05-25 18:06:20',0);
/*!40000 ALTER TABLE `post_comments` ENABLE KEYS */;

/*!40000 ALTER TABLE `post_likes` DISABLE KEYS */;
INSERT INTO `post_likes` (`id`, `post_id`, `user_id`, `created_at`) VALUES (1,2,10,'2026-05-25 18:05:39'),(2,1,6,'2026-05-25 18:06:01'),(3,2,6,'2026-05-25 18:06:44');
/*!40000 ALTER TABLE `post_likes` ENABLE KEYS */;

/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` (`id`, `user_id`, `title`, `content`, `plan_detail_id`, `day_number`, `plan_name`, `training_date`, `intensity`, `duration`, `distance`, `show_training_info`, `like_count`, `comment_count`, `created_at`, `updated_at`) VALUES (1,10,'1','1',7,2,'kskbl','2026-05-21','中等',NULL,NULL,1,2,3,'2026-05-22 11:00:29','2026-05-25 18:06:20'),(2,10,'努力就有回报','努力就有回报',10,5,'kskbl','2026-05-24','中等',NULL,NULL,1,2,0,'2026-05-25 16:42:36','2026-05-25 18:06:44');
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;

/*!40000 ALTER TABLE `student_profiles` DISABLE KEYS */;
INSERT INTO `student_profiles` (`id`, `user_id`, `real_name`, `phone`, `email`, `avatar`, `gender`, `age`, `created_at`, `updated_at`) VALUES (1,10,'大志','13366547965','1',NULL,'MALE',18,'2026-05-15 09:18:03','2026-05-22 09:30:36'),(2,11,'大志桑','19977231935','19977231935@163.com',NULL,'MALE',23,'2026-05-15 10:52:47','2026-05-15 10:52:47'),(3,12,'小智','19977231935','19977231935@163.com',NULL,'MALE',23,'2026-05-25 06:16:22','2026-05-25 06:16:22'),(4,14,'彭博','1','1@qq.com',NULL,'MALE',22,'2026-05-25 11:25:08','2026-05-25 11:25:08'),(5,15,'测试1','1234567','1234567@qq.com',NULL,'FEMALE',18,'2026-05-25 17:25:47','2026-05-25 17:25:47');
/*!40000 ALTER TABLE `student_profiles` ENABLE KEYS */;

/*!40000 ALTER TABLE `student_shares` DISABLE KEYS */;
/*!40000 ALTER TABLE `student_shares` ENABLE KEYS */;

/*!40000 ALTER TABLE `time_slots` DISABLE KEYS */;
INSERT INTO `time_slots` (`id`, `slot_name`, `start_time`, `end_time`, `max_capacity`, `sort_order`, `is_active`, `created_at`, `updated_at`) VALUES (1,'早上','08:30:00','10:00:00',5,1,1,'2026-05-20 13:53:28','2026-05-20 13:53:28'),(2,'下午','15:30:00','17:00:00',5,2,1,'2026-05-20 13:53:28','2026-05-20 13:53:28'),(3,'晚上','19:30:00','21:00:00',5,3,1,'2026-05-20 13:53:28','2026-05-20 13:53:28');
/*!40000 ALTER TABLE `time_slots` ENABLE KEYS */;

/*!40000 ALTER TABLE `training_plans` DISABLE KEYS */;
INSERT INTO `training_plans` (`id`, `coach_id`, `student_id`, `plan_name`, `description`, `start_date`, `end_date`, `status`, `ai_suggestions`, `created_at`, `updated_at`) VALUES (7,6,10,'kskbl','zdjd','2026-05-20','2026-05-24','ACTIVE',NULL,'2026-05-20 08:51:05','2026-05-20 08:51:05'),(8,6,11,'1','1','2026-05-25','2026-05-29','ACTIVE',NULL,'2026-05-25 11:23:44','2026-05-25 11:23:44');
/*!40000 ALTER TABLE `training_plans` ENABLE KEYS */;

/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `avatar`, `role`, `status`, `created_at`, `updated_at`) VALUES (1,'coach_wang','123456','王教练','13800138001','wang@swimming.com',NULL,'COACH',1,'2026-05-09 09:44:23','2026-05-09 09:44:23'),(2,'student_li','123456','李学员','13800138002','li@swimming.com',NULL,'STUDENT',1,'2026-05-09 09:44:23','2026-05-09 09:44:23'),(4,'Victim','123456',NULL,NULL,NULL,NULL,'COACH',1,'2026-05-13 14:10:57','2026-05-13 14:10:57'),(5,'HAJIMI','123456',NULL,NULL,NULL,NULL,'COACH',1,'2026-05-13 14:39:21','2026-05-13 14:39:21'),(6,'DAZHI','123456',NULL,NULL,NULL,NULL,'COACH',1,'2026-05-13 14:42:41','2026-05-13 14:42:41'),(7,'lucaiwen','123456',NULL,NULL,NULL,NULL,'COACH',1,'2026-05-14 09:28:53','2026-05-14 09:28:53'),(10,'DAZHIxue','123456',NULL,NULL,NULL,NULL,'STUDENT',1,'2026-05-15 09:18:03','2026-05-15 09:18:03'),(11,'student_zhi','123456',NULL,NULL,NULL,NULL,'STUDENT',1,'2026-05-15 10:52:47','2026-05-15 10:52:47'),(12,'xiaozhi','123456',NULL,NULL,NULL,NULL,'STUDENT',1,'2026-05-25 06:16:22','2026-05-25 06:16:22'),(13,'jiaolian1','123456',NULL,NULL,NULL,NULL,'COACH',1,'2026-05-25 06:17:31','2026-05-25 06:17:31'),(14,'PB','123456',NULL,NULL,NULL,NULL,'STUDENT',1,'2026-05-25 11:25:08','2026-05-25 11:25:08'),(15,'cheshi1','123456',NULL,NULL,NULL,NULL,'STUDENT',1,'2026-05-25 17:25:47','2026-05-25 17:25:47');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-27  1:15:49
