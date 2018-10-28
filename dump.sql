
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


-- auto-generated definition
create table users_role
(
  user_id integer not null
    constraint users_role_users_id_fk
    references users,
  role_id integer not null
    constraint users_role_roles_id_fk
    references roles
);

alter table users_role
  owner to postgres;

insert into users(email, password, first_name, last_name)
values ('konradsob@gmail.com', 'root', 'Konrad', 'Sobolewski');

update users set params = '{"isAdmin": "true"}' where id =1 ;


insert into roles(id , role) values (1, 'ROLE_ADMIN');
insert into roles(id , role) values (2, 'ROLE_USER');

insert into users_role(user_id, role_id) values (1, 1);
