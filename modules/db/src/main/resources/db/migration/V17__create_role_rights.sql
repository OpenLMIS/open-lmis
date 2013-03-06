CREATE TABLE role_rights (
  roleId INT REFERENCES roles(id) NOT NULL,
  rightName VARCHAR REFERENCES rights(name) NOT NULL,
  CONSTRAINT unique_role_right UNIQUE (roleId, rightName)
);
