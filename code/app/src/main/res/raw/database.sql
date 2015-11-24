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
	last_reset datetime,
	next_reset datetime
);

create table SpendingHistory(
	id integer primary key autoincrement,
	category_id integer,
	start_date datetime,
	end_date datetime,
	budget decimal,
	constraint SpendingHistory_category_fk foreign key (category_id)
		references Category(category_id)
);

create table Receipt(
	id integer primary key autoincrement,
	image_path varchar(100),
	category_id integer,
	date_recorded datetime default CURRENT_TIMESTAMP,
	cost decimal,
	constraint Receipt_category_fk foreign key (category_id)
		references Category(category_id)
);

create view Category_xp as
	select c.id, c.name, c.budget, c.image_path, sum(r.cost) as total,
		c.last_reset, c.next_reset
		from Category c
		left join Receipt r on c.id = r.category_id
		and r.date_recorded >= c.last_reset
		group by c.id;