DROP SCHEMA IF EXISTS sagrone CASCADE;

CREATE SCHEMA sagrone;

CREATE TABLE sagrone.sagra(
	id SERIAL NOT NULL,         -- 4 Bytes from 1 to 2147483647
	name VARCHAR(50) NOT NULL,
	description VARCHAR(200),
	city VARCHAR(100),
	address VARCHAR(100),

	PRIMARY KEY (id)
);

COMMENT ON TABLE sagrone.sagra IS 'Represents a "Sagra" (countryside fair)';
COMMENT ON COLUMN sagrone.sagra.id IS 'Unique identifier assigned to each Sagra';
COMMENT ON COLUMN sagrone.sagra.name IS 'Name of the Sagra (normally it represents the main topic)';
COMMENT ON COLUMN sagrone.sagra.description IS 'Brief description of the event (or like special events)';
COMMENT ON COLUMN sagrone.sagra.city IS 'Place where the Sagra is held';
COMMENT ON COLUMN sagrone.sagra.address IS 'Address of the Sagra';


CREATE TABLE sagrone.user(
	id SERIAL NOT NULL,
    username VARCHAR(20) NOT NULL UNIQUE,
	password CHAR(64) NOT NULL,          -- Using md5.
	id_sagra INT NOT NULL,
	admin BOOLEAN NOT NULL,

	PRIMARY KEY (id),
	FOREIGN KEY (id_sagra) REFERENCES sagrone.sagra(id)
);

COMMENT ON TABLE sagrone.user IS 'Represents either the admin of a Sagra or cashiers'' accounts';
COMMENT ON COLUMN sagrone.user.id IS 'Unique number assigned to each user';
COMMENT ON COLUMN sagrone.user.username IS 'Username used to login';
COMMENT ON COLUMN sagrone.user.password IS 'Password to login to user''s account';
COMMENT ON COLUMN sagrone.user.id_sagra IS 'Id of the Sagra whose user belongs to';
COMMENT ON COLUMN sagrone.user.admin IS 'Flag used to sign an user as admin or not';


CREATE TABLE sagrone.order(
    id SERIAL NOT NULL,
    client_name VARCHAR(50) NOT NULL,
    email VARCHAR(50),
    client_num SMALLINT NOT NULL,
    table_number VARCHAR(10),				-- NULL = Take away
    id_user INT,                            -- NULL = not payed
    order_time TIMESTAMP WITH TIME ZONE,
    payment_time TIMESTAMP WITH TIME ZONE,

    PRIMARY KEY (id),
    FOREIGN KEY (id_user) REFERENCES sagrone.user(id)
);

COMMENT ON TABLE sagrone.order IS 'Order made by a client either he/she takes a seat at the Sagra or he/she takes away. If not payed, the order is useless';
COMMENT ON COLUMN sagrone.order.id IS 'Auto-increment Unique identifier assigned to the order';
COMMENT ON COLUMN sagrone.order.client_name IS 'Custom name of the client in order to call him/her when the meal is ready (mostly in take away orders)';
COMMENT ON COLUMN sagrone.order.email IS 'Optional contact where the client can receive a summary of his/her order';
COMMENT ON COLUMN sagrone.order.client_num IS 'Number of people counted for the order (to manage seats)';
COMMENT ON COLUMN sagrone.order.table_number IS 'Table''s number (where clients are seated)';
COMMENT ON COLUMN sagrone.order.id_user IS 'Id of the cashier who sets the order as payed';
COMMENT ON COLUMN sagrone.order.order_time IS 'Timestamp of the order creation';
COMMENT ON COLUMN sagrone.order.payment_time IS 'Timestamp of the order payment';


CREATE TABLE sagrone.category(
    name VARCHAR(20) NOT NULL,

    PRIMARY KEY (name)
);

COMMENT ON TABLE sagrone.category IS 'Category of a product (Appetizer, Main, Drinks, ..)';
COMMENT ON COLUMN sagrone.category.name IS 'Name of the category';


CREATE TABLE sagrone.product(
    name VARCHAR(50) NOT NULL,
    id_sagra INT NOT NULL,
    description VARCHAR(100),
    price REAL,					-- 4 bytes. NULL allowed in case of free items
    bar BOOLEAN NOT NULL,
    available BOOLEAN NOT NULL,
    category VARCHAR(50) NOT NULL,
    photo bytea,
    photo_type VARCHAR(10),

    PRIMARY KEY (name, id_sagra),
    FOREIGN KEY (category) REFERENCES sagrone.category(name),
    CHECK (price IS NULL OR price >=0)
);

COMMENT ON TABLE sagrone.product IS 'Single product (ribs, beer, ..) purchasable in a Sagra';
COMMENT ON COLUMN sagrone.product.name IS 'Name of the item';
COMMENT ON COLUMN sagrone.product.id_sagra IS 'Sagra where the product is purchasable';
COMMENT ON COLUMN sagrone.product.description IS 'Detailed description of the item (number of pieces, what''s is made of,..)';
COMMENT ON COLUMN sagrone.product.price IS 'Cost of the product';
COMMENT ON COLUMN sagrone.product.bar IS 'Flag to indicate if it''s a bar product or not';
COMMENT ON COLUMN sagrone.product.available IS 'Flag to make a product available or not to clients';
COMMENT ON COLUMN sagrone.product.category IS 'Product''s category';
COMMENT ON COLUMN sagrone.product.photo IS 'Photo of the item';
COMMENT ON COLUMN sagrone.product.photo_type IS 'Format of the photo (jpeg, png, ...)';


CREATE TABLE sagrone.order_content(
	id_sagra INT NOT NULL,
    id_order INT NOT NULL,
    product_name VARCHAR(50) NOT NULL,
    price REAL DEFAULT 0,
    quantity SMALLINT DEFAULT 0,

    PRIMARY KEY (id_sagra, id_order, product_name),
    FOREIGN KEY (id_order) REFERENCES sagrone.order(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (product_name, id_sagra) REFERENCES sagrone.product(name, id_sagra) ON UPDATE NO ACTION ON DELETE NO ACTION,
    CHECK (quantity>0)
);

COMMENT ON TABLE sagrone.order_content IS 'Contains each item of any order';
COMMENT ON COLUMN sagrone.order_content.id_sagra IS 'Sagra''s identifier';
COMMENT ON COLUMN sagrone.order_content.id_order IS 'Order''s identifier';
COMMENT ON COLUMN sagrone.order_content.product_name IS 'Name of the item added in a order';
COMMENT ON COLUMN sagrone.order_content.price IS 'Price of a single item. It does not refer to the sagrone.product(price) as foreign key to have a price history';
COMMENT ON COLUMN sagrone.order_content.quantity IS 'Number of products for the same order';


CREATE OR REPLACE FUNCTION f_price()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE sagrone.order_content SET price= (SELECT price FROM sagrone.product WHERE name=NEW.product_name AND id_sagra=NEW.id_sagra) WHERE product_name=NEW.product_name AND id_sagra=NEW.id_sagra;
  RETURN NULL;
END; $$ LANGUAGE 'plpgsql';

CREATE TRIGGER trg_price AFTER INSERT
ON sagrone.order_content
FOR EACH ROW
EXECUTE PROCEDURE f_price();


COMMENT ON TRIGGER trg_price ON sagrone.order_content IS 'Trigger that after an insert on the table order_content updates the price with the current and correct price of the related product from the product table (needed if the price passed is not coherent with the real price of the product)';