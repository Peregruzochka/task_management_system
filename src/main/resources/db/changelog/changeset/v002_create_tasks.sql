CREATE TABLE tasks (
    id                  UUID            PRIMARY KEY,
    title               VARCHAR(64)     NOT NULL,
    task_description    VARCHAR(1024),
    task_status         VARCHAR(16)     NOT NULL,
    task_priority       VARCHAR(8)      NOT NULL,
    author_id           UUID            NOT NULL,
    assignee_id         UUID            NOT NULL,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP       NOT NULL,

    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_assignee_id FOREIGN KEY (assignee_id ) REFERENCES users(id)
)