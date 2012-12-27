CREATE TABLE losses_adjustments_types (

  name VARCHAR(250),
  additive BOOLEAN,
  displayOrder INTEGER,

   CONSTRAINT unique_losses_adjustments UNIQUE (name )
);