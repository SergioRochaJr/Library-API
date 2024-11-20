CREATE TABLE IF NOT EXISTS tb_book (
    id SERIAL PRIMARY KEY,
    nm_title VARCHAR(100) NOT NULL,
    nm_author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    published_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('AVAILABLE', 'BORROWED'))
);
INSERT INTO tb_book (
        nm_title,
        nm_author,
        isbn,
        published_date,
        status
    )
VALUES (
        'O Senhor dos Anéis',
        'J.R.R. Tolkien',
        '978-0618640157',
        '1954-07-29',
        'AVAILABLE'
    ),
    (
        '1984',
        'George Orwell',
        '978-0451524935',
        '1949-06-08',
        'BORROWED'
    );
CREATE TABLE IF NOT EXISTS tb_customer (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state NUMERIC(10, 2) NOT NULL,
    country VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE'))
);
INSERT INTO tb_customer (
        name,
        lastname,
        address,
        city,
        state,
        country,
        birth_date,
        status
    )
VALUES (
        'Sérgio',
        'da Rocha',
        'Avenida Logo Ali',
        'Santos',
        10,
        'Brasil',
        '1994-02-08',
        'ACTIVE'
    ),
    (
        'Thalles',
        'Palmarim',
        'Rua Logo Lá',
        'São Vicente',
        13.02,
        'Brasil',
        '1993-04-30',
        'INACTIVE'
    ),
    (
        'Rodrigo',
        'Guerreiro',
        'Rua da Ponte que Partiu',
        'Santos',
        10,
        'Brasil',
        '2004-06-04',
        'ACTIVE'
    );
CREATE TABLE IF NOT EXISTS tb_loan (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    loan_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'RETURNED')),
    FOREIGN KEY (customer_id) REFERENCES tb_customer(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES tb_book(id) ON DELETE CASCADE
);
INSERT INTO tb_loan (
        customer_id,
        book_id,
        loan_date,
        return_date,
        status
    )
VALUES (1, 1, '2024-11-01', '2024-11-15', 'RETURNED'),
    (2, 2, '2024-11-05', '2024-11-20', 'ACTIVE'),
    (3, 1, '2024-11-05', '2024-11-20', 'ACTIVE');