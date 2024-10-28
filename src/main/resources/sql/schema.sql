CREATE TABLE dishes (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    cost DECIMAL(10, 2) NOT NULL
);

CREATE TABLE users (
    id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    creationDate DATE,
    birthday DATE
);

CREATE TABLE orders (
    id INT PRIMARY KEY,
    user_id INT,
    address VARCHAR(100) NOT NULL,
    creationDate DATE,
    deliveryDate DATE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE orders_dishes (
    order_id INT,
    dish_id INT,
    quantity INT,
    PRIMARY KEY (order_id, dish_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
);