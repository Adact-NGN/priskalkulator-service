use pk_poal_db_dev;

create table if not exists sales_roles(id bigint not null primary key, default_power_of_attorney_fa int null, default_power_of_attorney_oa int null, description varchar(255) null, role_name varchar(255) null);

INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (1, 1, 1, 'Kundeveileder', 'KV');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (2, 2, 2, 'Salgskonsulent (rolle a)', 'SA');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (3, 3, 3, 'Salgskonsulent (rolle b)', 'SB');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (4, 4, 4, 'KAM lokalt', 'KL');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (5, 5, 5, 'KAM nasjonalt', 'KN');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 6, 'Salgsleder/salgssjef lokalt', 'SL');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (7, 6, 6, 'Salgssjef divisjon', 'SD');
INSERT INTO sales_roles (id, default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (8, 6, 6, 'Distriktssjef', 'DR');
