-- Table: mos_adjustment_basis
DROP TABLE IF EXISTS mos_adjustment_basis;
CREATE TABLE mos_adjustment_basis
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
CREATE UNIQUE INDEX mos_adjustment_basis_name_key ON mos_adjustment_basis(name);
COMMENT ON TABLE mos_adjustment_basis IS  'This will be used in sending out the notifications to the facility and also display on the report'; 
COMMENT ON COLUMN mos_adjustment_basis.id IS 'id';
COMMENT ON COLUMN mos_adjustment_basis.name IS 'name';
COMMENT ON COLUMN mos_adjustment_basis.description IS 'description';
COMMENT ON COLUMN mos_adjustment_basis.displayOrder IS 'displayOrder';
COMMENT ON COLUMN mos_adjustment_basis.createdBy IS 'createdBy';
COMMENT ON COLUMN mos_adjustment_basis.createdDate IS 'createdDate';
COMMENT ON COLUMN mos_adjustment_basis.modifiedBy IS 'modifiedBy';
COMMENT ON COLUMN mos_adjustment_basis.modifiedDate IS 'modifiedDate';