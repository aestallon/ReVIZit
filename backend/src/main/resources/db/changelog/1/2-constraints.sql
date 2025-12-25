--liquibase formatted sql

--changeset bazil:7
alter table water_state
    add constraint water_state_empty_cnt_check check (empty_cnt >= 0);

--changeset bazil:8
alter table water_state
    add constraint water_state_curr_pct_check check (curr_pct >= 0 and curr_pct <= 100);

--changeset bazil:9
alter table water_state
    add constraint water_state_full_cnt_check check (full_cnt >= 0);

--changeset bazil:10
alter table water_report
    add constraint water_report_val_check check (val >= 0 and val <= 100);
