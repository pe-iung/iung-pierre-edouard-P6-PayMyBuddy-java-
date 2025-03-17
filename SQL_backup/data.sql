INSERT INTO user_table (username, password, email) VALUES ('dbuser', '$2a$10$M6fHeE2TP4sh3pdSyiNf6Oj79YkqF9vg0706oeNCq5gmfYcZgKnDq', 'USER@email.com'),
                                                     ('dbadmin', '$2a$10$1vG8phlPmXHUQtm3Xw3efOzegqx7RsdrlswUIWkKPIofZIx3X3gvW', 'ADMIN@email.com');

INSERT INTO public.user_friends (friend_id, user_entity_id) VALUES ( (select id from user_table where username = 'dbuser') , (select id from user_table where username = 'dbadmin') ),
                                                                   ( (select id from user_table where username = 'dbadmin') , (select id from user_table where username = 'dbuser') );


INSERT INTO public.account_table (balance, user_id) VALUES (100000,  (select id from user_table where username = 'dbuser'));
INSERT INTO public.account_table (balance, user_id) VALUES (50000, (select id from user_table where username = 'dbadmin'));
