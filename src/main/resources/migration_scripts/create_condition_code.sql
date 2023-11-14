create table condition_codes
(
    id         bigint auto_increment not null
        primary key,
    code varchar(255) null,
    price_type varchar(255) null
);