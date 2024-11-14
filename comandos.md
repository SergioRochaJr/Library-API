mvn compile

mvn test

mvn spring-boot:run

CREATE TABLE customer (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state DECIMAL NOT NULL,
    country VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_customer_status FOREIGN KEY (status) REFERENCES customer_status (status_code)
);

-- Se o enum CustomerStatus for armazenado em uma tabela separada:
CREATE TABLE customer_status (
    status_code VARCHAR(255) PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL
);

-- Tabela para armazenar os empréstimos (Loan) relacionados ao Customer
CREATE TABLE loan (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(100) NOT NULL,
    CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);
