create database youmeet
	with owner scalauser
;

create sequence hibernate_sequence
;

--  users
create table users
(
	id serial not null
		constraint users_pkey
			primary key,
	email text not null,
	password text not null,
	params jsonb,
	first_name text not null,
	last_name text not null
)
;

create unique index users_id_uindex
	on users (id)
;

create unique index users_email_uindex
	on users (email)
;

--  roles
create table roles
(
	id serial not null
		constraint role_pkey
			primary key,
	role text not null,
	user_id integer
		constraint fk97mxvrajhkq19dmvboprimeg1
			references users
)
;

create unique index role_id_uindex
	on roles (id)
;

--  meeting
create table meeting
(
	meeting_id serial not null
		constraint meeting_pkey
			primary key,
	place_longitude text,
	place_latitude text,
	is_one_to_one boolean not null,
	inviter_id integer not null
		constraint meeting_users_id_fk
			references users,
	params json
)
;


create unique index meeting_id_uindex
	on meeting (meeting_id)
;

--    categories
create table categories
(
	id serial not null
		constraint categories_pkey
			primary key,
	type text not null
)
;

create unique index categories_id_uindex
	on categories (id)
;

create unique index categories_type_uindex
	on categories (type)
;

--     hobby
create table hobby
(
	id   serial not null
		constraint hobby_pk
			primary key,
	name text   not null
);

create unique index hobby_id_uindex
on hobby (id);

create unique index hobby_name_uindex
on hobby (name);


--   user hobby
create table user_hobby
(
	user_id integer not null,
	params  jsonb
);

create unique index user_hobby_user_id_uindex
on user_hobby (user_id);

