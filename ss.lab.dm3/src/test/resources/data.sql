LOCK TABLES `sphere_extension` WRITE;
DELETE FROM `sphere_extension`;
UNLOCK TABLES;

LOCK TABLES `wiki_sphere_extension` WRITE;
DELETE FROM `wiki_sphere_extension`;
INSERT INTO `wiki_sphere_extension`
(`id`, `description`) VALUES (1, 'Wiki sphere'), (2, 'Wiki2 sphere');
UNLOCK TABLES;

LOCK TABLES `forum_sphere_extension` WRITE;
DELETE FROM `forum_sphere_extension`;
INSERT INTO `forum_sphere_extension`
(`id`, `description`) VALUES (3, 'Forum sphere');
UNLOCK TABLES;

LOCK TABLES `sphere` WRITE;
DELETE FROM `sphere`;
INSERT INTO `sphere` 
(`id`, `system_name`, `display_name`, `sphere_type`, `default_delivery_type`, `email_alias_enabled`, `email_alias_addresses`,`parent_sphere_id`, `extension_qualifier`, `extension_id`) VALUES
(1,'Sphere#1','Sphere_Display#1','GROUP','NORMAL',1,'abc@mail.ru', null, null,  null), 
(2,'Sphere#2','Sphere_Display#2','MEMBER','POLL',0,'def@list.ru', 1, 'WikiSphereExtension',  1),
(3,'Sphere#3','Sphere_Display#3','MEMBER','POLL',0,'sphere3@list.ru', 1, 'ForumSphereExtension', 3);
UNLOCK TABLES;

LOCK TABLES `supra_sphere` WRITE;
DELETE FROM `supra_sphere`;
INSERT INTO `supra_sphere` (`id`, `domain_names`, `sphere_id`) VALUES 
(1,'domain#1',1), 
( 2,'domain#2',2);
UNLOCK TABLES;

LOCK TABLES `user_account` WRITE;
DELETE FROM `user_account`;
INSERT INTO `user_account` (`id`, `login`, `contact_name`, `home_sphere_id`, `contact_card_id`)  VALUES 
(1,'jack','Jack',1,'card#jack'),
(2,'bill','Bill',2,'card#bill'),
(3,'bob','Bob',2,'card#bob'),
(4,'doug','Doug',2,'card#doug'),
(5,'andy','Andy',1,'card#andy'),
(6,'mike','Mike',3,'card#mike'),
(7,'adam','Adam',3,'card#adam'),
(8,'den','Den',1,'card#den'),
(9,'ted','Ted',1,'card#ted'),
(10,'alex','Alex',3,'card#alex'),
(11,'phil','Phil',2,'card#phil'),
(12,'sid','Sid',2,'card#sid'),
(13,'vinny','Vinny',2,'card#vinny'),
(14,'marty','Marty',2,'card#marty'),
(15,'nick','Nick',3,'card#nick'),
(16,'marc','Marc',3,'card#marc'),
(17,'gary','Gary',1,'card#gary'),
(18,'chris','Chris',1,'card#chris'),
(19,'mario','Mario',2,'card#mario'),
(20,'max','Max',1,'card#max');
UNLOCK TABLES;

LOCK TABLES `user_in_sphere` WRITE;
DELETE FROM `user_in_sphere`;
INSERT INTO `user_in_sphere` (`id`, `sphere_display_name`, `sphere_id`, `user_account_id`)  VALUES 
(1,'sphere_display_name#1',1,1),
(2,'sphere_display_name#2',2,2),
(3,null,1,16),
(4, null,1,19);
UNLOCK TABLES;

LOCK TABLES `attachment` WRITE;
DELETE FROM `attachment`;
INSERT INTO `attachment` (`id`, `blob_state`, size, name )  VALUES 
(10, 'READY', 0, 'First file' ),
(11, 'BROKEN', 1000, 'Second file' ),
(12, 'BROKEN', 1000, 'Third file' ),
(13, 'READY', 0, 'First document' ),
(14, 'BROKEN', 1000, 'Second document' ),
(15, 'BROKEN', 1000, 'Third document' );
UNLOCK TABLES;
