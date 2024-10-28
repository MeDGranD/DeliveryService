INSERT INTO dishes (id, name, description, cost) VALUES (1, 'Паста карбонара', 'Итальянская паста с беконом и сливочным соусом', 10.99);
INSERT INTO dishes (id, name, description, cost) VALUES (2, 'Стейк из говядины', 'Сочный стейк с приправами и запеченными овощами', 15.50);
INSERT INTO dishes (id, name, description, cost) VALUES (3, 'Салат Цезарь', 'Классический салат с курицей и соусом Цезарь', 8.75);
INSERT INTO dishes (id, name, description, cost) VALUES (4, 'Суп гороховый', 'Традиционный суп с овощами и колбасой', 6.25);
INSERT INTO dishes (id, name, description, cost) VALUES (5, 'Рыбные тикки', 'Креветки и лосось, приготовленные в тандыре', 12.99);

INSERT INTO users (id, username, password, role, creationDate, birthday) VALUES (1, 'user1', 'password1', 'admin', '2023-05-15', '1990-07-20');
INSERT INTO users (id, username, password, role, creationDate, birthday) VALUES (2, 'user2', 'password2', 'user', '2023-06-20', '1985-09-10');
INSERT INTO users (id, username, password, role, creationDate, birthday) VALUES (3, 'user3', 'password3', 'user', '2023-07-25', '1995-04-05');
INSERT INTO users (id, username, password, role, creationDate, birthday) VALUES (4, 'user4', 'password4', 'user', '2023-08-30', '1988-12-15');
INSERT INTO users (id, username, password, role, creationDate, birthday) VALUES (5, 'user5', 'password5', 'user', '2023-09-10', '1999-11-25');

INSERT INTO orders (id, user_id, address, creationDate, deliveryDate) VALUES (1, 1, 'Улица Пушкина, дом Колотушкина', '2024-10-15', '2024-10-16');
INSERT INTO orders (id, user_id, address, creationDate, deliveryDate) VALUES (2, 2, 'Проспект Ленина, дом 10, квартира 5', '2024-10-16', '2024-10-17');
INSERT INTO orders (id, user_id, address, creationDate, deliveryDate) VALUES (3, 3, 'Улица Гагарина, дом 12', '2024-10-17', '2024-10-18');
INSERT INTO orders (id, user_id, address, creationDate, deliveryDate) VALUES (4, 4, 'Площадь Победы, дом 5, квартира 3', '2024-10-18', '2024-10-19');
INSERT INTO orders (id, user_id, address, creationDate, deliveryDate) VALUES (5, 5, 'Улица Сталина, дом 8', '2024-10-19', '2024-10-20');

INSERT INTO orders_dishes (order_id, dish_id, quantity) VALUES (1, 1, 2);
INSERT INTO orders_dishes (order_id, dish_id, quantity) VALUES (1, 2, 1);
INSERT INTO orders_dishes (order_id, dish_id, quantity) VALUES (2, 3, 3);
INSERT INTO orders_dishes (order_id, dish_id, quantity) VALUES (3, 4, 1);
INSERT INTO orders_dishes (order_id, dish_id, quantity) VALUES (4, 5, 2);