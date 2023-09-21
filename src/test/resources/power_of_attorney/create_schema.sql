CREATE TABLE sales_roles
(
    id                           BIGINT AUTO_INCREMENT NOT NULL,
    role_name                    VARCHAR(255)          NULL,
    `description`                VARCHAR(255)          NULL,
    default_power_of_attorney_oa INT                   NULL,
    default_power_of_attorney_fa INT                   NULL,
    CONSTRAINT pk_sales_roles PRIMARY KEY (id)
);

ALTER TABLE sales_roles
    ADD CONSTRAINT uc_sales_roles_rolename UNIQUE (role_name);


CREATE TABLE users
(
    id                                  BIGINT       NOT NULL,
    ad_id                               VARCHAR(255) NULL,
    org_nr                              VARCHAR(255) NULL,
    org_name                            VARCHAR(255) NULL,
    region_name                         VARCHAR(255) NULL,
    sure_name                           VARCHAR(255) NULL,
    name                                VARCHAR(255) NULL,
    username                            VARCHAR(255) NULL,
    username_alias                      VARCHAR(255) NULL,
    job_title                           VARCHAR(255) NULL,
    full_name                           VARCHAR(255) NULL,
    resource_nr                         VARCHAR(255) NULL,
    phone_number                        VARCHAR(255) NULL,
    email                               VARCHAR(255) NULL,
    sales_role_id                       BIGINT       NULL,
    associated_place                    VARCHAR(255) NULL,
    power_of_attorneyoa                 INT          NULL,
    power_of_attorneyfa                 INT          NULL,
    overall_power_of_attorney           VARCHAR(255) NULL,
    email_sales_manager                 VARCHAR(255) NULL,
    regional_managers_power_of_attorney VARCHAR(255) NULL,
    email_regional_manager              VARCHAR(255) NULL,
    department                          VARCHAR(255) NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_SALESROLE FOREIGN KEY (sales_role_id) REFERENCES sales_roles (id);

create table if not exists sale_office_power_of_attorney_matrix(id bigint not null primary key auto_increment,
                                                                sales_office varchar(255) UNIQUE,
                                                                sales_office_name varchar(255),
                                                                region varchar(255),
                                                                ordinary_waste_lvl_one_holder_id BIGINT       NULL,
                                                                ordinary_waste_lvl_two_holder_id BIGINT       NULL,
                                                                dangerous_waste_holder_id        BIGINT       NULL,
                                                                CONSTRAINT pk_sale_office_power_of_attorney_matrix PRIMARY KEY (id)
);

ALTER TABLE sale_office_power_of_attorney_matrix
    ADD CONSTRAINT FK_SALEOFFICEPOWEROFATTORNEYMATRIX_ON_ORDINARYWASTELVLONEHOLDER FOREIGN KEY (ordinary_waste_lvl_one_holder_id) REFERENCES users (id);

ALTER TABLE sale_office_power_of_attorney_matrix
    ADD CONSTRAINT FK_SALEOFFICEPOWEROFATTORNEYMATRIX_ON_ORDINARYWASTELVLTWOHOLDER FOREIGN KEY (ordinary_waste_lvl_two_holder_id) REFERENCES users (id);

ALTER TABLE sale_office_power_of_attorney_matrix
    ADD CONSTRAINT FK_SALE_OFFICE_POWER_OF_ATTORNEY_MATRIX_ON_DANGEROUSWASTEHOLDER FOREIGN KEY (dangerous_waste_holder_id) REFERENCES users (id);