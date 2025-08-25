CREATE TABLE account (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(250) NOT NULL UNIQUE,
    password VARCHAR(250) NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_account_role FOREIGN KEY (role_id) REFERENCES role(id)
);

INSERT INTO account(id, username, password, role_id)
VALUES (
    'a4be1774-f8d4-4fd2-a9c5-c86ac1af7bdd',
    'admin',
    '$2a$10$EJXYTLeRAsHQQxZv7eHUNuVPHRGGlu1gL6XRPE26l6BFeDvPFNdV.',
    1
);