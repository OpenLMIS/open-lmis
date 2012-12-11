CREATE TABLE role_assignments (
  user_id INTEGER REFERENCES users(id),
  role_id INTEGER REFERENCES roles(id),
  program_id INTEGER REFERENCES program(id),
  CONSTRAINT unique_role_assignment UNIQUE (user_id, role_id, program_id)
);