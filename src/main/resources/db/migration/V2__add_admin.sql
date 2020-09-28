insert into users (id, username, password, email)
    values(1, 'admin', '123', 'dm@dm.com');

insert into user_role (user_id, roles)
    values(1, 'USER'), (1, 'ADMIN');