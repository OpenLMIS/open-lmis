CREATE TABLE supply_lines (

  id SERIAL PRIMARY KEY,
  description VARCHAR(250),
  supervisoryNodeId INTEGER REFERENCES supervisory_nodes(id),
  programId INTEGER REFERENCES programs(id),
  supplyingFacilityId INTEGER REFERENCES facilities(id),
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT unique_supply_line UNIQUE ( supervisoryNodeId , programId , supplyingFacilityId)
);