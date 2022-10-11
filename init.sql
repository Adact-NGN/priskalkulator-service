create database pk_poal_db_dev;

grant all privileges on pk_poal_db_dev.* TO 'admin'@'localhost' identified by 'admin1234';

grant all privileges on pk_poal_db_dev.* TO 'admin'@'%' identified by 'admin1234';

flush privileges;