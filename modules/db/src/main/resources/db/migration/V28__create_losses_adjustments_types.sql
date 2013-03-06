CREATE TABLE losses_adjustments_types (

  name VARCHAR(50) NOT NULL,
  description VARCHAR(100) NOT NULL,
  additive BOOLEAN,
  displayOrder INTEGER,
  UNIQUE (name),
  UNIQUE (description)
);