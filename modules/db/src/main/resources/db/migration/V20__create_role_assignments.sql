CREATE TABLE role_assignments (
  user_id INT REFERENCES users(id),
  role_id INT REFERENCES roles(id),
  program_id VARCHAR(50) REFERENCES program(code),
  CONSTRAINT unique_role_assignment UNIQUE (user_id, role_id, program_id)
);