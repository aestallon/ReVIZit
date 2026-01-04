--liquibase formatted sql

--changeset bazil:15
create table sys_log
(
    id        serial primary key          not null,
    timestamp timestamp without time zone not null default now(),
    username  varchar(255)                not null default 'anonymous',
    action    varchar(255)                not null default 'unknown'
);

--changeset bazil:16
create table sys_log_element
(
    id        serial primary key not null,
    sys_log   integer            not null references sys_log,
    order_num integer            not null,
    qualifier varchar(255)       not null default '',
    name      varchar(255)       not null default '',
    msg       text               not null default ''
);
