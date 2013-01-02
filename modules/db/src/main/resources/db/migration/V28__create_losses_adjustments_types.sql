CREATE TABLE losses_adjustments_types (

  name VARCHAR(50),
  description VARCHAR(100),
  additive BOOLEAN,
  displayOrder INTEGER,
  UNIQUE (name),
  UNIQUE (description)
);