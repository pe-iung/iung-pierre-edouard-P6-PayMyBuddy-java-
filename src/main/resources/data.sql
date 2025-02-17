INSERT INTO user_table (username, password, email) VALUES ('dbuser', '$2a$10$M6fHeE2TP4sh3pdSyiNf6Oj79YkqF9vg0706oeNCq5gmfYcZgKnDq', 'USER@email.com'),
                                                     ('dbadmin', '$2a$10$1vG8phlPmXHUQtm3Xw3efOzegqx7RsdrlswUIWkKPIofZIx3X3gvW', 'ADMIN@email.com');

INSERT INTO public.user_friends (friend_id, user_entity_id) VALUES (2, 1);
/*
INSERT INTO public.account_table (balance, user_id, id) VALUES (1000, 1, '94325ed0-01f4-4bb7-a508-998267922ff6');

INSERT INTO public.account_table (balance, user_id, id) VALUES (500, 2, '8ee5404a-359d-4f11-8dcf-04d1ffd1cdb1');
*/

INSERT INTO public.account_table (balance, user_id, id) VALUES (1000, 1, 1);

INSERT INTO public.account_table (balance, user_id, id) VALUES (500, 2, 2);

/* Set Next ID Value to MAX ID
 without this, we face a primary_key id collision when a new account is added from jpa
 context : https://stackoverflow.com/questions/24390820/postgresql-error-23505-duplicate-key-value-violates-unique-constraint-foo-col
 solution fund there : https://gist.github.com/henriquemenezes/962829815e8e7875f5f4376133b2f209?permalink_comment_id=2619919
 official doc : https://www.postgresql.org/docs/8.1/functions-sequence.html
*/
SELECT setval('account_table_id_seq', (SELECT MAX(id) FROM account_table));
