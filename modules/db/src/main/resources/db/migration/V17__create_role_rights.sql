CREATE TABLE role_rights (
  roleId INT REFERENCES roles(id),
  rightName VARCHAR REFERENCES rights(name),
  CONSTRAINT unique_role_right UNIQUE (roleId, rightName)
);
