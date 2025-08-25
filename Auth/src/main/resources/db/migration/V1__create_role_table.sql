CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    type_role VARCHAR(40) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO role (id,type_role) VALUES
(1, 'ADMIN'),
(2, 'USER'),
(3, 'STAFF_MAKEUP'),
(4, 'STAFF_NAIL');