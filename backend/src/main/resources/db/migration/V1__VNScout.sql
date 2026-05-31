-- we don't know how to generate root <with-no-name> (class Root) :(

grant select on performance_schema.* to 'mysql.session'@localhost;

grant trigger on sys.* to 'mysql.sys'@localhost;

grant audit_abort_exempt, firewall_exempt, select, system_user on *.* to 'mysql.infoschema'@localhost;

grant audit_abort_exempt, authentication_policy_admin, backup_admin, clone_admin, connection_admin, firewall_exempt, persist_ro_variables_admin, session_variables_admin, shutdown, super, system_user, system_variables_admin on *.* to 'mysql.session'@localhost;

grant audit_abort_exempt, firewall_exempt, system_user on *.* to 'mysql.sys'@localhost;

grant alter, alter routine, application_password_admin, audit_abort_exempt, audit_admin, authentication_policy_admin, backup_admin, binlog_admin, binlog_encryption_admin, clone_admin, connection_admin, create, create role, create routine, create tablespace, create temporary tables, create user, create view, delete, drop, drop role, encryption_key_admin, event, execute, file, firewall_exempt, flush_optimizer_costs, flush_status, flush_tables, flush_user_resources, group_replication_admin, group_replication_stream, index, innodb_redo_log_archive, innodb_redo_log_enable, insert, lock tables, passwordless_user_admin, persist_ro_variables_admin, process, references, reload, replication client, replication slave, replication_applier, replication_slave_admin, resource_group_admin, resource_group_user, role_admin, select, sensitive_variables_observer, service_connection_admin, session_variables_admin, set_user_id, show databases, show view, show_routine, shutdown, super, system_user, system_variables_admin, table_encryption_admin, telemetry_log_admin, trigger, update, xa_recover_admin, grant option on *.* to root;

grant alter, alter routine, application_password_admin, audit_abort_exempt, audit_admin, authentication_policy_admin, backup_admin, binlog_admin, binlog_encryption_admin, clone_admin, connection_admin, create, create role, create routine, create tablespace, create temporary tables, create user, create view, delete, drop, drop role, encryption_key_admin, event, execute, file, firewall_exempt, flush_optimizer_costs, flush_status, flush_tables, flush_user_resources, group_replication_admin, group_replication_stream, index, innodb_redo_log_archive, innodb_redo_log_enable, insert, lock tables, passwordless_user_admin, persist_ro_variables_admin, process, references, reload, replication client, replication slave, replication_applier, replication_slave_admin, resource_group_admin, resource_group_user, role_admin, select, sensitive_variables_observer, service_connection_admin, session_variables_admin, set_user_id, show databases, show view, show_routine, shutdown, super, system_user, system_variables_admin, table_encryption_admin, telemetry_log_admin, trigger, update, xa_recover_admin, grant option on *.* to root@localhost;

create table banned_words
(
    id       varchar(255) not null
        primary key,
    language varchar(255) not null,
    type     varchar(255) not null,
    word     varchar(255) not null,
    constraint UK1e00wb8uymyfiuyiv4ajqhtsp
        unique (word)
);

create table locations
(
    id             varchar(36)    not null
        primary key,
    created_by     varchar(255)   null,
    created_date   date           null,
    deleted        int            not null,
    deleted_at     datetime(6)    null,
    modified_by    varchar(255)   null,
    modified_date  date           null,
    category       varchar(255)   null,
    check_in_count bigint         null,
    code           varchar(255)   not null,
    cover_photo    varchar(255)   null,
    description    text           null,
    golden_hour    varchar(255)   null,
    latitude       decimal(10, 7) null,
    level          int            not null,
    longitude      decimal(10, 7) null,
    name           varchar(255)   not null,
    name_with_type varchar(255)   null,
    post_count     bigint         null,
    slug           varchar(255)   null,
    type           varchar(255)   null,
    parent_id      varchar(36)    null,
    constraint UKnjcw38t3qcy312pglqpf3pd59
        unique (code),
    constraint FKhjdkpuoptx1cd04r3atchkpi0
        foreign key (parent_id) references locations (id)
);

create table permission
(
    id              varchar(50)                                                                                                            not null
        primary key,
    permission_key  enum ('APPROVE', 'CREATE', 'DELETE', 'MANAGE', 'UPDATE', 'VIEW')                                                       null,
    permission_type enum ('EXPENSE', 'INVENTORY', 'PAYROLL', 'REPORT', 'ROLE', 'SCHEDULE', 'SCHEDULE_DOCUMENT', 'TRAVEL', 'TRUCK', 'USER') null
);

create table role
(
    id          varchar(50)  not null
        primary key,
    description varchar(255) null,
    name        varchar(255) null
);

create table role_permissions
(
    role_id        varchar(50) not null,
    permissions_id varchar(50) not null,
    constraint FKclluu29apreb6osx6ogt4qe16
        foreign key (permissions_id) references permission (id),
    constraint FKlodb7xh4a2xjv39gc3lsop95n
        foreign key (role_id) references role (id)
);

create table tags
(
    id            varchar(36)  not null
        primary key,
    created_by    varchar(255) null,
    created_date  date         null,
    deleted       int          not null,
    deleted_at    datetime(6)  null,
    modified_by   varchar(255) null,
    modified_date date         null,
    name          varchar(100) not null,
    constraint UKt48xdq560gs3gap9g7jg36kgc
        unique (name)
);

create table users
(
    id            varchar(36)  not null
        primary key,
    created_by    varchar(255) null,
    created_date  date         null,
    deleted       int          not null,
    deleted_at    datetime(6)  null,
    modified_by   varchar(255) null,
    modified_date date         null,
    avatar_url    varchar(255) null,
    birthday      date         null,
    description   varchar(255) null,
    email         varchar(255) null,
    password      varchar(255) null,
    username      varchar(255) null
);

create table posts
(
    id            varchar(36)  not null
        primary key,
    created_by    varchar(255) null,
    created_date  date         null,
    deleted       int          not null,
    deleted_at    datetime(6)  null,
    modified_by   varchar(255) null,
    modified_date date         null,
    caption       text         null,
    like_count    bigint       null,
    shooting_tip  text         null,
    location_id   varchar(36)  null,
    user_id       varchar(36)  null,
    constraint FK1vpruxtho87bsysr5g2jpntnr
        foreign key (location_id) references locations (id),
    constraint FK5lidm6cqbc7u4xhqpxm898qme
        foreign key (user_id) references users (id)
);

create table likes
(
    id            varchar(36)  not null
        primary key,
    created_by    varchar(255) null,
    created_date  date         null,
    deleted       int          not null,
    deleted_at    datetime(6)  null,
    modified_by   varchar(255) null,
    modified_date date         null,
    post_id       varchar(36)  null,
    user_id       varchar(36)  null,
    constraint UK2jovqhqo324cubdomovkex03b
        unique (user_id, post_id),
    constraint FKnvx9seeqqyy71bij291pwiwrg
        foreign key (user_id) references users (id),
    constraint FKry8tnr4x2vwemv2bb0h5hyl0x
        foreign key (post_id) references posts (id)
);

create table photos
(
    id                varchar(36)  not null
        primary key,
    created_by        varchar(255) null,
    created_date      date         null,
    deleted           int          not null,
    deleted_at        datetime(6)  null,
    modified_by       varchar(255) null,
    modified_date     date         null,
    file_size         bigint       null,
    height            int          null,
    image_url         text         not null,
    location_verified bit          null,
    width             int          null,
    post_id           varchar(36)  null,
    moderation_reason text         null,
    moderation_score  double       null,
    moderation_status varchar(10)  null,
    constraint FK6y417rkxpq0v9rurdmrj96034
        foreign key (post_id) references posts (id)
);

create table photo_metadata
(
    photo_id      varchar(36)    not null
        primary key,
    aperture      decimal(4, 2)  null,
    camera_make   varchar(100)   null,
    camera_model  varchar(100)   null,
    date_taken    datetime(6)    null,
    focal_length  decimal(5, 2)  null,
    gps_latitude  decimal(10, 7) null,
    gps_longitude decimal(10, 7) null,
    iso           int            null,
    lens_model    varchar(100)   null,
    shutter_speed text           null,
    address       text           null,
    district      varchar(100)   null,
    province      varchar(100)   null,
    ward          varchar(100)   null,
    constraint FKqtybwctvqpk60lu7eob712twm
        foreign key (photo_id) references photos (id)
);

create table post_tags
(
    post_id varchar(36) not null,
    tag_id  varchar(36) not null,
    constraint FKkifam22p4s1nm3bkmp1igcn5w
        foreign key (post_id) references posts (id),
    constraint FKm6cfovkyqvu5rlm6ahdx3eavj
        foreign key (tag_id) references tags (id)
);

create table user_role
(
    user_id varchar(36) not null,
    role_id varchar(50) not null,
    constraint FKa68196081fvovjhkek5m97n3y
        foreign key (role_id) references role (id),
    constraint FKj345gk1bovqvfame88rcx7yyx
        foreign key (user_id) references users (id)
);

