CREATE TABLE IF NOT EXISTS  tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL,
                       priority VARCHAR(20) NOT NULL,
                       author_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                       assignee_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP
);