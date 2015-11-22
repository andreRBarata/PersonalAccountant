/*
	Names - Varchar(30)
	Ids - Integer
	Paths - Varchar(100)
*/

create table Category(
	id integer primary key autoincrement,
	name varchar(30),
	budget decimal,
	image_path varchar(100),
	counting_period integer,
	last_reset datetime
);

create table SpendingHistory(
	id integer primary key autoincrement,
	category_id integer,
	start_date datetime,
	end_date datetime,
	constraint SpendingHistory_category_fk foreign key (category_id)
		references Category(category_id)
);

create table Receipt(
	id integer primary key autoincrement,
	image_path varchar(100),
	category_id integer,
	date_recorded datetime default CURRENT_DATE,
	cost decimal,
	constraint Receipt_category_fk foreign key (category_id)
		references Category(category_id)
);
