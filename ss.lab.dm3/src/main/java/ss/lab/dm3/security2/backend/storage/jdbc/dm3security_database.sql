drop table if exists dm3s_user;
drop table if exists dm3s_user_authority;
    
create table dm3s_user (
	id bigint not null auto_increment,
	name varchar(255) not null unique,
	details varchar(255),
	primary key (id)
) type=InnoDB;

create table dm3s_user_authority (
	id bigint not null auto_increment,
	user_id bigint not null,
	authority_id bigint not null,
	primary key (id)
) type=InnoDB;
