# USE pk_poal_db_dev;
# SET sql_mode='NO_AUTO_VALUE_ON_ZERO';

# create table if not exists sales_roles(id bigint not null primary key auto_increment, default_power_of_attorney_fa int null, default_power_of_attorney_oa int null, description varchar(255) null, role_name varchar(255) null UNIQUE);

INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (1, 1, 'Kundeveileder', 'KV');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (2, 2, 'Salgskonsulent (rolle a)', 'SA');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (3, 3, 'Salgskonsulent (rolle b)', 'SB');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (4, 4, 'KAM lokalt', 'KL');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (5, 5, 'KAM nasjonalt', 'KN');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Salgsleder/salgssjef lokalt', 'SL');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Salgssjef divisjon', 'SD');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Distriktssjef', 'DR');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Superadmin', 'Admin');

# create table if not exists sale_office_power_of_attorney_matrix(id bigint not null primary key auto_increment,
#                                                                 sales_office varchar(255),
#                                                                 sales_office_name varchar(255),
#                                                                 region varchar(255),
#                                                                 ordinary_waste_lvl_one_holder_id BIGINT       NULL,
#                                                                 ordinary_waste_lvl_two_holder_id BIGINT       NULL,
#                                                                 dangerous_waste_holder_id        BIGINT       NULL
#                                                                 CONSTRAINT pk_sale_office_power_of_attorney_matrix PRIMARY KEY (id)
# );

alter table sale_office_power_of_attorney_matrix add constraint unique_sales_office unique(sales_office);

alter table sale_office_power_of_attorney_matrix modify column if exists id bigint not null primary key auto_increment;

ALTER TABLE sale_office_power_of_attorney_matrix
    ADD CONSTRAINT FK_SALEOFFICEPOWEROFATTORNEYMATRIX_ON_ORDINARYWASTELVLONEHOLDER FOREIGN KEY (ordinary_waste_lvl_one_holder_id) REFERENCES users (id);

ALTER TABLE sale_office_power_of_attorney_matrix
    ADD CONSTRAINT FK_SALEOFFICEPOWEROFATTORNEYMATRIX_ON_ORDINARYWASTELVLTWOHOLDER FOREIGN KEY (ordinary_waste_lvl_two_holder_id) REFERENCES users (id);

ALTER TABLE sale_office_power_of_attorney_matrix
    ADD CONSTRAINT FK_SALE_OFFICE_POWER_OF_ATTORNEY_MATRIX_ON_DANGEROUSWASTEHOLDER FOREIGN KEY (dangerous_waste_holder_id) REFERENCES users (id);

INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (100,'StorOslo','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (101,'Asker/Bærum','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (102,'Romerike','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (103,'Vestby/Moss','Østfold');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (104,'Skien','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (105,'Larvik','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (106,'Tønsberg','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (107,'Drammen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (108,'Kongsberg','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (109,'Hønefoss','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (110,'Molde','Midt-Nord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (112,'Ålesund','Nord-Vest');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (113,'Kristiansund','Midt-Nord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (114,'Namsos','Midt-Nord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (115,'Trondheim','Midt-Nord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (117,'Verdal','Midt-Nord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (118,'NordNorge','Midt-Nord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (119,'Holmestrand','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (120,'Hamarregionen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (121,'Østerdalen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (122,'GLTregionen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (123,'HRAregionen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (124,'Lillehammerregionen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (125,'Gudbrandsdalen','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (126,'Valdres','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (127,'Sarpsborg/Fredriksta','Østfold');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (128,'IndreØstfold/Halden','Østfold');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (129,'Romerike','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (130,'Arendal','Agder');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (131,'Kristiansand','Agder');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (132,'Stavanger','Rogaland');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (133,'Haugesund','Rogaland');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (134,'Bergen','Hordaland');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (135,'Førde','Nord-Vest');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (136,'Florø','Nord-Vest');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (138,'Kragerø','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (139,'Ørsta','Nord-Vest');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (141,'Oslo(GMP)','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (142,'Follo','Oslofjord');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (144,'Ryfylke','Rogaland');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (146,'Mandal','Agder');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (147,'Lyngdal','Agder');
INSERT IGNORE INTO sale_office_power_of_attorney_matrix(sales_office, sales_office_name, region) VALUES (149,'Setesdalen','Agder');
