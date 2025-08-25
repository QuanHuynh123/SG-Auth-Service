CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    account_id UUID NOT NULL,
    CONSTRAINT fk_account FOREIGN KEY(account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
);