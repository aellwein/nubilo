
---------------------------------
-- USERS table
---------------------------------
create table if not exists users (
  id integer primary key autoincrement,
  username text not null,
  password text not null,
  email text not null
);

insert into users values (null, 'admin', 'c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec', '');