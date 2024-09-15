create table PAY (
     id         bigint not null auto_increment,
     amount     bigint,
     transaction_name     varchar(255),
     transaction_date_time datetime,
     primary key (id)
) engine = InnoDB;

insert into PAY (amount, transaction_name, transaction_date_time)VALUES (1000, 'trade1', '2018-09-10 00:00:00');
insert into PAY (amount, transaction_name, transaction_date_time)VALUES (2000, 'trade2', '2018-09-10 00:00:00');
insert into PAY (amount, transaction_name, transaction_date_time)VALUES (3000, 'trade3', '2018-09-10 00:00:00');
insert into PAY (amount, transaction_name, transaction_date_time)VALUES (4000, 'trade4', '2018-09-10 00:00:00');