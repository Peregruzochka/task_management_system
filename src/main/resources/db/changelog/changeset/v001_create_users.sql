CREATE TABLE users(
    id                  UUID          PRIMARY KEY,
    username            VARCHAR(64)   NOT NULL,
    encoded_password    VARCHAR(128)  NOT NULL,
    email               VARCHAR(64)   UNIQUE NOT NULL,
    user_role           VARCHAR(32)   NOT NULL,
    created_at          TIMESTAMP     NOT NULL
)