CREATE TABLE IF NOT EXISTS  comments (
                          id BIGSERIAL PRIMARY KEY,
                          content TEXT NOT NULL,
                          task_id BIGINT REFERENCES tasks(id) ON DELETE CASCADE,
                          author_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);