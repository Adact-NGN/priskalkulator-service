# Sales roles

INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (1, 1, 'Kundeveileder', 'KV');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (2, 2, 'Salgskonsulent (rolle a)', 'SA');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (3, 3, 'Salgskonsulent (rolle b)', 'SB');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (4, 4, 'KAM lokalt', 'KL');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (5, 5, 'KAM nasjonalt', 'KN');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Salgsleder/salgssjef lokalt', 'SL');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Salgssjef divisjon', 'SD');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Distriktssjef', 'DR');
INSERT INTO sales_roles (default_power_of_attorney_fa, default_power_of_attorney_oa, description, role_name) VALUES (6, 6, 'Superadmin', 'Admin');

# Power of attorney

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
