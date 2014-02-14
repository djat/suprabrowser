### 
### SS tables for error reporting
### 

DROP TABLE IF EXISTS `log_event`;
DROP TABLE IF EXISTS `log_session`;

CREATE TABLE `log_event` (
	`id` bigint unsigned AUTO_INCREMENT NOT NULL COMMENT 'Auto increment identifier',  
	`session_id` bigint unsigned NOT NULL COMMENT 'Reference to log_session.id',
	`message` text COMMENT 'Log message',  
	`location_info` text COMMENT 'Code location information',
	`stact_trace` text COMMENT 'Stack trace', 
	`context` varchar(255) COMMENT 'String that provide details of log event',
	`level` varchar(16) NOT NULL COMMENT 'Log event level', 
	`date` timestamp COMMENT 'Log event date information', 
	PRIMARY KEY (`id`)	
);

CREATE TABLE `log_session` (
	`id` bigint unsigned AUTO_INCREMENT NOT NULL COMMENT 'Auto increment identifier',  
	`session_key` text COMMENT 'String identifier of session',
	`user_name` varchar(255) COMMENT 'Session user',
	`date` timestamp COMMENT 'Session create date information', 
	`context` varchar(255) COMMENT 'String that provide details of log event',
	PRIMARY KEY (`id`)	
);