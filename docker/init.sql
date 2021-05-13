create sequence hibernate_sequence;

alter sequence hibernate_sequence owner to postgres;

create table if not exists customer
(
	id bigint not null
		constraint customer_pkey
			primary key,
	email varchar(255),
	first_name varchar(255),
	last_name varchar(255)
);

alter table customer owner to postgres;

create table if not exists product
(
	id bigint not null
		constraint product_pkey
			primary key,
	code varchar(10) not null
		constraint uk_h3w5r1mx6d0e5c6um32dgyjej
			unique,
	description varchar(255),
	is_available boolean not null,
	name varchar(255),
	price_hrk real not null
);

alter table product owner to postgres;

create table if not exists webshop_order
(
	id bigint not null
		constraint webshop_order_pkey
			primary key,
	status varchar(255) not null,
	total_price_eur real not null,
	total_price_hrk real not null
);

alter table webshop_order owner to postgres;

create table if not exists order_item
(
	order_id bigint not null
		constraint fkdk3kexcjjiok4a2p3v0iidowk
			references webshop_order,
	product_id bigint
		constraint fk551losx9j75ss5d6bfsqvijna
			references product,
	quantity integer,
	index_id integer not null,
	constraint order_item_pkey
		primary key (order_id, index_id)
);

alter table order_item owner to postgres;
