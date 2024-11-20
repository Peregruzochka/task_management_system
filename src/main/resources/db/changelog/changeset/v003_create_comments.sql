CREATE TABLE comments (
    id              UUID            PRIMARY KEY,
    comment_text    VARCHAR(2048)   NOT NULL,
    author_id       UUID            NOT NULL,
    task_id         UUID            NOT NULL,
    created_at      TIMESTAMP       NOT NULL,

    CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users(id),
    CONSTRAINT fk_task_id   FOREIGN KEY (task_id)   REFERENCES tasks(id)
)