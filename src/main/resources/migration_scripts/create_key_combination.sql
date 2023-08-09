create table key_combinations
(
    id              bigint auto_increment
        primary key,
    description     varchar(255) null,
    key_combination varchar(255) null,
    title_type_id   bigint       null,
    constraint FK_key_combination_title_type
        foreign key (title_type_id) references title_types (id)
);

