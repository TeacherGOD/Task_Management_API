INSERT INTO users (id, email, password, full_name, role)
VALUES
    (1,'user1@mail.com', 'pass1', 'User One', 'ROLE_USER'),
    (2,'admin@mail.com', 'admin', 'Admin', 'ROLE_ADMIN');

INSERT INTO tasks (id,title, description, status, priority, author_id, assignee_id)
VALUES
    (1,'Task 1', 'Desc 1', 'PENDING', 'HIGH', 1, 1),
    (2,'Task 2', 'Desc 2', 'PENDING', 'HIGH', 1, 2),
    (3,'Task 3', 'Desc 2', 'COMPLETED', 'MEDIUM', 1, 2);