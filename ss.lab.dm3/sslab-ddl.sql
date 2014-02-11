
    alter table sphere 
        drop 
        foreign key FKCA9867CD80907503;

    alter table supra_sphere 
        drop 
        foreign key FK30484D4FA8EFBE58;

    alter table user_account 
        drop 
        foreign key FK14C321B981F862F8;

    alter table user_in_sphere 
        drop 
        foreign key FK3C4318B3A8EFBE58;

    alter table user_in_sphere 
        drop 
        foreign key FK3C4318B315D76045;

    drop table if exists forum_sphere_extension;

    drop table if exists sphere;

    drop table if exists sphere_extension;

    drop table if exists supra_sphere;

    drop table if exists user_account;

    drop table if exists user_in_sphere;

    drop table if exists wiki_sphere_extension;

    create table forum_sphere_extension (
        id bigint not null,
        description varchar(255),
        primary key (id)
    ) type=InnoDB;

    create table sphere (
        id bigint not null,
        email_alias_enabled bit not null,
        default_delivery_type varchar(255),
        sphere_type varchar(255),
        email_alias_addresses varchar(255),
        system_name varchar(255) not null unique,
        display_name varchar(255) not null,
        extension_qualifier varchar(255),
        extension_id bigint,
        parent_sphere_id bigint,
        primary key (id)
    ) type=InnoDB;

    create table sphere_extension (
        id bigint not null,
        description varchar(255),
        primary key (id)
    ) type=InnoDB;

    create table supra_sphere (
        id bigint not null,
        domain_names varchar(255),
        sphere_id bigint,
        primary key (id)
    ) type=InnoDB;

    create table user_account (
        id bigint not null,
        contact_name varchar(255) not null,
        contact_card_id varchar(255) not null,
        login varchar(255) not null unique,
        home_sphere_id bigint,
        primary key (id)
    ) type=InnoDB;

    create table user_in_sphere (
        id bigint not null,
        sphere_display_name varchar(255),
        user_account_id bigint,
        sphere_id bigint,
        primary key (id)
    ) type=InnoDB;

    create table wiki_sphere_extension (
        id bigint not null,
        description varchar(255),
        primary key (id)
    ) type=InnoDB;

    alter table sphere 
        add index FKCA9867CD80907503 (parent_sphere_id), 
        add constraint FKCA9867CD80907503 
        foreign key (parent_sphere_id) 
        references sphere (id);

    alter table supra_sphere 
        add index FK30484D4FA8EFBE58 (sphere_id), 
        add constraint FK30484D4FA8EFBE58 
        foreign key (sphere_id) 
        references sphere (id);

    create index idx_login on user_account (login);

    alter table user_account 
        add index FK14C321B981F862F8 (home_sphere_id), 
        add constraint FK14C321B981F862F8 
        foreign key (home_sphere_id) 
        references sphere (id);

    alter table user_in_sphere 
        add index FK3C4318B3A8EFBE58 (sphere_id), 
        add constraint FK3C4318B3A8EFBE58 
        foreign key (sphere_id) 
        references sphere (id);

    alter table user_in_sphere 
        add index FK3C4318B315D76045 (user_account_id), 
        add constraint FK3C4318B315D76045 
        foreign key (user_account_id) 
        references user_account (id);
