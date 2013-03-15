CREATE TABLE supply_lines (
  id                    SERIAL PRIMARY KEY,
  description           VARCHAR(250),
  supervisoryNodeId     INTEGER REFERENCES supervisory_nodes(id) NOT NULL,
  programId             INTEGER REFERENCES programs(id) NOT NULL,
  supplyingFacilityId   INTEGER REFERENCES facilities(id) NOT NULL,
  modifiedBy            INTEGER,
  modifiedDate          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdDate           TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

  CONSTRAINT unique_supply_line UNIQUE ( supervisoryNodeId , programId , supplyingFacilityId)
);