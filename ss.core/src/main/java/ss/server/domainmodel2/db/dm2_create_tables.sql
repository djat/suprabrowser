### 
### SS tables for contatact management
### 

### sphere, member, sphere-member settings
DROP TABLE IF EXISTS `sphere`;
DROP TABLE IF EXISTS `member`;
DROP TABLE IF EXISTS `invited_member`;
DROP TABLE IF EXISTS `configuration`;

CREATE TABLE `sphere` ( 
	`id` bigint unsigned NOT NULL COMMENT 'Identifier',  
	`system_name` varchar(128) NOT NULL default '' COMMENT 'Alternative primary key used by',   
	`title` varchar(128) NOT NULL default '' COMMENT 'Sphere title',   
	`preferences_xml` longtext COMMENT 'Sphere preferences', 
	PRIMARY KEY (`id`), 
	UNIQUE KEY `idx_system_name` (`system_name`)
);


CREATE TABLE `member` ( 
	`id` bigint unsigned NOT NULL COMMENT 'Identifier',  
	`login` varchar(128) NOT NULL default '' COMMENT 'User login',   
	`first_name` varchar(128) NOT NULL default '' COMMENT 'User login',
	`core_sphere_id` bigint unsigned NOT NULL default 0 COMMENT 'Core sphere identifier. 0 means - no sphere',     
	`preferences_xml` longtext COMMENT 'Member personal preferences', 
	PRIMARY KEY (`id`), 
	UNIQUE KEY `idx_login` (`login`)
);

CREATE TABLE `invited_member` (
	`id` bigint unsigned NOT NULL COMMENT 'Identifier',  
	`sphere_id` bigint unsigned NOT NULL COMMENT 'Reference to sphere.id',  
	`member_id` bigint unsigned NOT NULL COMMENT 'Reference to member.id',  
	`preferences_xml` longtext COMMENT 'Preferences for member in sphere', 
	PRIMARY KEY (`id`), 
	UNIQUE KEY `idx_sphere_member` (`sphere_id`, `member_id`)	
);

CREATE TABLE `configuration` ( 
	`id` bigint unsigned NOT NULL COMMENT 'Identifier',  
	`name` varchar(128) NOT NULL default '' COMMENT 'Configuration name',   
	`value_xml` longtext COMMENT 'Configuration value', 
	PRIMARY KEY (`id`), 
	UNIQUE KEY `idx_name` (`name`)
);

