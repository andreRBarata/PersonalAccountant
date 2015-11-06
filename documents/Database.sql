/*
	Names - Varchar(30)
	Ids - Integer
	Paths - Varchar(100)
*/

create table Category(
	id integer primary key,
	name varchar(30),
	budget decimal,
	image_path varchar(100),
	counting_period integer,
	last_reset date
);

create table SpendingHistory(
	id integer primary key,
	category_id integer,
	budget decimal,
	start_date date,
	end_date date,
	constraint SpendingHistory_category_fk foreign key (category_id)
		references Category(category_id);
);

create table Receipt(
	id integer primary key,
	image_path varchar(100),
	category_id integer,
	date_recorded date,
	cost decimal,
	constraint Receipt_category_fk foreign key (category_id)
		references Category(category_id);
);
