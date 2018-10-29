
-- auto-generated definition
create table users
(
  id        serial  not null
    constraint users_pkey
    primary key,
  email     text not null,
  password  text    not null,
  params    jsonb,
  first_name text not null,
  last_name  text not null
);

alter table users
  owner to postgres;

create unique index users_id_uindex
  on users (id);

create unique index users_email_uindex
  on users (email);

-- role
create table roles
(
  id   serial not null
    constraint role_pkey
    primary key,
  role text   not null
);

alter table roles
  owner to postgres;

create unique index role_id_uindex
  on roles (id)


insert into users(email, password, first_name, last_name)
values ('konradsob@gmail.com', 'root', 'Konrad', 'Sobolewski');

update users set params = '{"isAdmin": "true", "creationDate": "2018-10-28T21:36:48.809Z"}' where id =1 ;


insert into roles(id , role, user_id) values (1, 'ROLE_ADMIN', 1);
insert into roles(id , role, user_id) values (2, 'ROLE_USER', 1);
