-- Table: mos_adjustment_types
DROP TABLE IF EXISTS mos_adjustment_types;
CREATE TABLE mos_adjustment_types
(
  id SERIAL PRIMARY KEY,  -- id                            ,
  name character varying(50) NOT NULL, -- name
  description character varying(100),
  displayOrder integer, -- displayOrder
  createdBy integer, -- createdBy
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- createdDate
  modifiedBy integer, -- modifiedBy
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- modifiedDate
);
CREATE UNIQUE INDEX mos_adjustment_types_name_key ON mos_adjustment_types(name);
COMMENT ON TABLE mos_adjustment_types IS  'This will be used in sending out the notifications to the facility and also display on the report'; 
COMMENT ON COLUMN mos_adjustment_types.id IS 'id';
COMMENT ON COLUMN mos_adjustment_types.name IS 'name';
COMMENT ON COLUMN mos_adjustment_types.description IS 'description';
COMMENT ON COLUMN mos_adjustment_types.displayOrder IS 'displayOrder';
COMMENT ON COLUMN mos_adjustment_types.createdBy IS 'createdBy';
COMMENT ON COLUMN mos_adjustment_types.createdDate IS 'createdDate';
COMMENT ON COLUMN mos_adjustment_types.modifiedBy IS 'modifiedBy';
COMMENT ON COLUMN mos_adjustment_types.modifiedDate IS 'modifiedDate';

