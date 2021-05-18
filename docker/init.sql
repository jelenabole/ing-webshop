create table customer
(
	id bigserial not null
		constraint customer_pkey
			primary key,
	email varchar(255),
	first_name varchar(255),
	last_name varchar(255)
);

alter table customer owner to postgres;

create table product
(
	id bigserial not null
		constraint product_pkey
			primary key,
	code varchar(10) not null
		constraint uk_h3w5r1mx6d0e5c6um32dgyjej
			unique,
	description varchar(255),
	is_available boolean not null,
	name varchar(255),
	price_hrk numeric(19,2) not null
);

alter table product owner to postgres;

create table webshop_order
(
	id bigserial not null
		constraint webshop_order_pkey
			primary key,
	status varchar(255) not null,
	total_price_eur numeric(19,2) default 0 not null,
	total_price_hrk numeric(19,2) default 0 not null,
	customer_id bigint
		constraint fkfwgyft2j50y1oyor9y5455djq
			references customer
);

alter table webshop_order owner to postgres;

create table order_item
(
	id bigserial not null
		constraint order_item_pkey
			primary key,
	quantity integer not null
		constraint order_item_quantity_check
			check (quantity >= 0),
	product_id bigint not null
		constraint fk551losx9j75ss5d6bfsqvijna
			references product,
	order_id bigint
		constraint fkdk3kexcjjiok4a2p3v0iidowk
			references webshop_order
);

alter table order_item owner to postgres;
