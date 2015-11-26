/*
	Names - Varchar(30)
	Ids - Integer
	Paths - Varchar(100)
*/

create table Category(
	id integer primary key autoincrement,
	name varchar(30),
	current_budget decimal,
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
	select c.id, c.name, c.current_budget as budget, c.image_path, sum(r.cost) as total,
		c.last_reset, c.next_reset
		from Category c
		left join Receipt r on c.id = r.category_id
		and r.date_recorded between c.last_reset and c.next_reset
		group by c.id;

create view SpendingHistory_xp as
	select * from (
		select c.id as category_id, c.name, c.budget, c.image_path, c.total,
			c.last_reset as start_date, c.next_reset as end_date
		from Category_xp c
		union
		select s.category_id, c.name, s.budget, c.image_path, sum(r.cost) as total,
			s.start_date, s.end_date
		from SpendingHistory s
		left join Category c on s.category_id = c.id
		left join Receipt r on c.id = r.category_id
			and r.date_recorded between s.start_date and s.end_date
		group by s.id
	)
	order by end_date DESC;