-- Active: 1714007442487@@127.0.0.1@3306@joeun
create table persistent_logins (
    username varchar(64) not null
    , series varchar(64) primary key
    , token varchar(64) not null
    , last_used timestamp not null
);


select *
from persistent_logins;
;

TRUNCATE persistent_logins;

commit;