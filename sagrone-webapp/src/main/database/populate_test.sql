DELETE FROM sagrone.user WHERE true;
DELETE FROM sagrone.sagra WHERE true;
DELETE FROM sagrone.order_content WHERE true;
DELETE FROM sagrone.product WHERE true;
DELETE FROM sagrone.order WHERE true;
DELETE FROM sagrone.category WHERE true;

ALTER SEQUENCE sagrone.sagra_id_seq RESTART;
ALTER SEQUENCE sagrone.order_id_seq RESTART;
ALTER SEQUENCE sagrone.user_id_seq RESTART;

INSERT INTO sagrone.sagra(id,name,description,city,address) VALUES(1,'Festa del Capitel','Tra poco a Fontane!','Villorba (TV)','via Colombere, 31020 Villorba (TV)');
INSERT INTO sagrone.sagra(id,name,description,city,address) VALUES(2,'Festa d estate','Rumatera presenti','Carbonera (TV)','Fraz. Vascon, Carbonera (TV)');
INSERT INTO sagrone.sagra(id,name,description,city,address) VALUES(3,'Sagra della madonna del carmine','Grigliate e panini','Trevignano (TV)','via 24 maggio, 31040, Falze di Trevignano (TV)');

INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('fontaneadmin','2df5c2777b657419f8ca14d9eed37b98',1,true);
INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('fontanecassa1','1828202a4984fa22da7ceb9df5088e79',1,false);
INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('fontanecassa2','dcd4105dea4d44291aba46bc7915d7ef',1,false);

INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('vasconadmin','a54a00dcb5c5f84750e5ef91e5b38c2e',2,true);
INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('vasconcassa1','d90a570eb8686a00c961740fd9133ecd',2,false);
INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('vasconcassa2','947b7ed4d47392824e350cc0fb3e6cbc',2,false);

INSERT INTO sagrone.user(username,password,id_sagra,admin) VALUES('falzeadmin','8599e49712742da981969e78341fd01f',3,true);


INSERT INTO sagrone.category(name) VALUES ('starters');
INSERT INTO sagrone.category(name) VALUES ('first course');
INSERT INTO sagrone.category(name) VALUES ('meat');
INSERT INTO sagrone.category(name) VALUES ('fish');
INSERT INTO sagrone.category(name) VALUES ('sides');
INSERT INTO sagrone.category(name) VALUES ('beverages');
INSERT INTO sagrone.category(name) VALUES ('desserts');

INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('pasta pomodoro',3,'pasta al pomodoro',6,false,true,'first course',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('gnocchi pomodoro',3,'gnocchi al pomodoro',6,false,true,'first course',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('pasta ragu',3,'pasta al ragu',6,false,true,'first course',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('gnocchi ragu',3,'gnocchi al ragu',6,false,true,'first course',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('summer dish',3,'tomato, mozzarella and prosciutto',5,false,true,'starters',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('sagra dish',3,'ribs, pork belly, sausages and fries',10,false,true,'meat',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('ribs',3,'3 ribs with fries',7,false,true,'meat',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('water',3,'water',1.10,false,true,'beverages',NULL,NULL);
INSERT INTO sagrone.product(name,id_sagra,description,price,bar,available,category,photo,photo_type) VALUES ('fries',3,'fries',2.50,false,true,'sides',NULL,NULL);


INSERT INTO sagrone.order(client_name, email, client_num, table_number, id_user, order_time, payment_time) VALUES ('marco', 'marco@gmail.com', 6, '202', NULL, now(), NULL);
INSERT INTO sagrone.order(client_name, email, client_num, table_number, id_user, order_time, payment_time) VALUES ('giovanni', 'giovanni@gmail.com', 2, '701', NULL, now(), NULL);

INSERT INTO sagrone.order_content(id_sagra, id_order, product_name, price, quantity) VALUES (3, 1, 'fries', 2.50, 2);
INSERT INTO sagrone.order_content(id_sagra, id_order, product_name, price, quantity) VALUES (3, 1, 'summer dish', 5, 10);
INSERT INTO sagrone.order_content(id_sagra, id_order, product_name, price, quantity) VALUES (3, 1, 'gnocchi pomodoro', 6, 1);
INSERT INTO sagrone.order_content(id_sagra, id_order, product_name, price, quantity) VALUES (3, 1, 'water', 1.10, 5);
INSERT INTO sagrone.order_content(id_sagra, id_order, product_name, price, quantity) VALUES (3, 2, 'sagra dish', 10, 11);

