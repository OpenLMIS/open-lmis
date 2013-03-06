CREATE TABLE role_assignments (
  userId INTEGER REFERENCES users(id) NOT NULL,
  roleId INTEGER REFERENCES roles(id) NOT NULL,
  programId INTEGER REFERENCES programs(id),
  supervisoryNodeId INTEGER REFERENCES supervisory_nodes(id),

  CONSTRAINT unique_role_assignment UNIQUE (userId, roleId, programId, supervisoryNodeId)
);