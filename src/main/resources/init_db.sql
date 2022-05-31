create database @datasource.database@;

ALTER SCHEMA `@datasource.database@`  DEFAULT CHARACTER SET utf8;

create table sales_role (id bigint not null, description varchar(255), role_name varchar(255), primary key (id)) engine=InnoDB
create table sales_role_user_list (sales_role_id bigint not null, user_list_id bigint not null) engine=InnoDB
create table users (id bigint not null, associated_place varchar(255), email varchar(255), email_regional_manager varchar(255), email_sales_manager varchar(255), full_name varchar(255), name varchar(255), org_name varchar(255), org_nr varchar(255), overall_power_of_atterney varchar(255), phone_number varchar(255), power_of_atterneyfa varchar(255), power_of_atterneyoa varchar(255), region_name varchar(255), regional_managers_power_of_atterney varchar(255), resource_nr varchar(255), sure_name varchar(255), sales_role_id bigint, primary key (id)) engine=InnoDB
