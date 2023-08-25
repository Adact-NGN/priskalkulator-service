create table key_combinations
(
    id              bigint auto_increment not null
        primary key,
    description     varchar(255) null,
    key_combination varchar(255) null,
    condition_code_id   bigint       null,
    constraint FK_key_combination_condition_code
        foreign key (condition_code_id) references condition_codes (id)
);

