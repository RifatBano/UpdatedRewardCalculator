Tables for UpdatedRewardCalculator created in Postgres:
CREATE TABLE customer (id SERIAL PRIMARY KEY,first_name VARCHAR(255) NOT NULL,last_name VARCHAR(255) NOT NULL,email VARCHAR(255) NOT NULL UNIQUE,password VARCHAR(255) NOT NULL);

CREATE TABLE customer_transaction (id SERIAL PRIMARY KEY,customer_id BIGINT NOT NULL,amount DOUBLE PRECISION NOT NULL,spent_details TEXT,date DATE NOT NULL,CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE);

CREATE TABLE reward_points (id SERIAL PRIMARY KEY,customer_id BIGINT NOT NULL,points INT NOT NULL,month INT NOT NULL CHECK (month >= 1 AND month <= 12),year INT NOT NULL CHECK (year >= 1900 AND year <= 9999),CONSTRAINT fk_customer_reward FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE);
-----------------------------------------------------------------------------------

INSERT INTO customer (first_name, last_name, email, password) VALUES ('John', 'Doe', 'john.doe@example.com', 'password123'),('Jane', 'Smith', 'jane.smith@example.com', 'password456'),('Alice', 'Johnson', 'alice.johnson@example.com', 'password789');

select * from customer;
select * from customer_transaction;
select * from reward_points;

after integrating and running the code in your ide, type in websearch:http://localhost:8090/swagger-ui.html
