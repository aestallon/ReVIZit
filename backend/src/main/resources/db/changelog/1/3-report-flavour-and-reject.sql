--liquibase formatted sql

--changeset bazil:11
alter table water_report
    add column flavour     int references water_flavour,
    add column rejected_at timestamp without time zone,
    add column rejected_by int references user_account;
