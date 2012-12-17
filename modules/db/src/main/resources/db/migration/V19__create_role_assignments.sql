CREATE TABLE role_assignments (
  userId INTEGER REFERENCES users(id),
  roleId INTEGER REFERENCES roles(id),
  programId INTEGER REFERENCES programs(id),
  CONSTRAINT unique_role_assignment UNIQUE (userId, roleId, programId)
);