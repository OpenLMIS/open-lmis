CREATE TABLE role_rights (
  role_id INT REFERENCES roles(id),
  right_id VARCHAR REFERENCES rights(id),
  CONSTRAINT unique_role_right UNIQUE (role_id, right_id)
);
