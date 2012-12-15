CREATE TABLE role_rights (
  roleId INT REFERENCES roles(id),
  rightId VARCHAR REFERENCES rights(id),
  CONSTRAINT unique_role_right UNIQUE (roleId, rightId)
);
