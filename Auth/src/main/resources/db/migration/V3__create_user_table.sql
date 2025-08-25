CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(40),
    email VARCHAR(40),
    address VARCHAR(50),
    phone VARCHAR(20) NOT NULL,
    account_id UUID,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    is_guest BOOLEAN DEFAULT FALSE NOT NULL,
    guest_token UUID,
    CONSTRAINT fk_users_account FOREIGN KEY (account_id) REFERENCES account(id)
);

INSERT INTO users(full_name, phone, account_id)
VALUES (
    'Admin User',
    '0123456789',
    'a4be1774-f8d4-4fd2-a9c5-c86ac1af7bdd'
);