-- 供货商表
create table supplier
(
    id           bigserial primary key,
    avatar       varchar(256),
    name         varchar(100) not null,
    description  text,
    url          varchar(256),
    submitted_by bigint       not null,
    status       int          not null,
    created_at   timestamp    not null default CURRENT_TIMESTAMP,
    updated_at   timestamp    not null default CURRENT_TIMESTAMP
);

-- 供货商表API信息
create table supplier_api_info
(
    id         bigserial primary key,
    api_url    varchar(256),
    api_key   varchar(128),
    api_secret    varchar(128),
    api_token  varchar(512),
    status     int       not null,
    updated_at timestamp not null default CURRENT_TIMESTAMP
);

-- 供货商商平类目
create table supplier_catalog
(
    id          bigserial primary key,
    supplier_id bigint       not null,
    group_id    bigint       not null,
    name        varchar(200) not null,
    img_url     varchar(256),
    status      int          not null default 0,
    created_at  timestamp    not null default CURRENT_TIMESTAMP,
    updated_at  timestamp    not null default CURRENT_TIMESTAMP
);

-- 供货商商品表
create table supplier_goods
(
    id                bigserial primary key,
    supplier_id       bigint       not null,
    catalog_id        bigint       not null,
    supplier_goods_id bigint       not null,
    supplier_group_id bigint       not null,
    name              varchar(100) not null,
    main_pic          varchar(256),
    description       text,
    price             float        not null,
    market_price      float,
    stock             int          not null,
    buy_min_num       int          not null,
    type              int          not null,
    status            int          not null,
    created_at        timestamp    not null default CURRENT_TIMESTAMP,
    updated_at        timestamp    not null default CURRENT_TIMESTAMP
);
