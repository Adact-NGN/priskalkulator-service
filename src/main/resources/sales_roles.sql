create table sales_roles
(
    id                           bigint auto_increment not null
        primary key,
    default_power_of_attorney_fa int          null,
    default_power_of_attorney_oa int          null,
    description                  varchar(255) null,
    role_name                    varchar(255) null,
    constraint UK_dr0q4g9hejr4en1x3oypb2gt4
        unique (role_name)
);

INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (1, 1, 1, 'Kundeveileder', 'KV');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (2, 2, 2, 'Salgskonsulent (rolle a)', 'SA');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (3, 3, 3, 'Salgskonsulent (rolle b)', 'SB');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (4, 4, 4, 'KAM lokalt', 'KL');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (5, 5, 5, 'KAM nasjonalt', 'KN');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 6, 'Salgsleder/salgssjef lokalt', 'SL');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (7, 6, 6, 'Salgssjef divisjon', 'SD');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (8, 6, 6, 'Distriktssjef', 'DR');
INSERT INTO pk_poal_db_prod.sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (9, 6, 6, 'Superadmin', 'Admin');
