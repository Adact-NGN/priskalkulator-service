create table if not exists discount_matrix
(
    id                   bigint       not null AUTO_INCREMENT
        primary key,
    device_type          varchar(255) null,
    material_designation varchar(255) null,
    material_number      varchar(255) not null,
    sales_org            varchar(255) not null,
    standard_price       double       null,
    zone                 varchar(255) null
);

create table if not exists discount_levels
(
    id                  bigint not null auto_increment
        primary key,
    calculated_discount double null,
    discount            double null,
    level               int    null,
    pct_discount        double null,
    discount_id         bigint not null,
    constraint FK9txsqk658slsxw6rub6rc2a01
        foreign key (discount_id) references discount_matrix (id)
);
