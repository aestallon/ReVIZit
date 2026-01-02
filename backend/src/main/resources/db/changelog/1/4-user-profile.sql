--liquibase formatted sql

--changeset bazil:12
alter table user_account
    drop column profile_id;

--changeset bazil:13
create table user_profile
(
    id           serial not null primary key,
    display_name varchar(255),
    pfp_url      varchar(255)
);

--changeset bazil:14
alter table user_account
    add column user_profile int references user_profile;

