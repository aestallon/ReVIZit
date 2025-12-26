--liquibase formatted sql

--changeset bazil:1
create table if not exists user_account
(
    id         serial     not null primary key,
    username   text       not null,                 -- username used for authentication
    mail_addr  text       not null default '',      -- email address
    user_pw    text       not null,                 -- encrypted password
    user_role  varchar(5) not null default 'PLAIN', -- possible values: PLAIN, ADMIN
    inactive   boolean    not null default false,
    reg_token  uuid,                                -- token for registration and password recovery
    token_exp  timestamp without time zone,         -- expiration timestamp for the token, if any
    profile_id int
);

--changeset bazil:2
create type report_type as enum (
    'BALLOON_CHANGE',
    'SET_PERCENTAGE',
    'BALLOON_REFILL'
    );

--changeset bazil:3
create table water_flavour
(
    id   serial primary key,
    name text not null unique
);

--changeset bazil:4
create table water_report
(
    id          serial primary key,
    kind        report_type                 not null,
    val         int                         not null,
    reported_at timestamp without time zone not null default now(),
    reported_by int                         references user_account,
    approved_at timestamp without time zone,
    approved_by int references user_account
);

--changeset bazil:5
create table water_state
(
    id         serial primary key,
    empty_cnt  int                         not null default 0,
    full_cnt   int                         not null default 0,
    curr_pct   int                         not null,
    curr_flav  int references water_flavour,
    created_at timestamp without time zone not null default now(),
    report     int references water_report
);

--changeset bazil:6
create table flavour_request
(
    id           serial primary key,
    name         text                        not null,
    submitted_at timestamp without time zone not null default now(),
    submitted_by int                         not null references user_account
);
