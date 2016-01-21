-- Table: mos_adjustment_products
DROP TABLE IF EXISTS mos_adjustment_facilities;
CREATE TABLE mos_adjustment_facilities
(
  id SERIAL PRIMARY KEY,  -- id
  typeId integer REFERENCES mos_adjustment_products (id) NOT NULL, -- typeID                             ,
  facilityId integer REFERENCES facilities (id) NOT NULL, --facilityid
  createdBy integer, -- createdBy
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- createdDate
  modifiedBy integer, -- modifiedBy
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- modifiedDate
);
CREATE UNIQUE INDEX uc_mos_adjustment_facilities_facility ON mos_adjustment_facilities(typeId,facilityId);
COMMENT ON TABLE mos_adjustment_facilities IS  'Apply seasonality adjustment factor to facilities'; 
COMMENT ON COLUMN mos_adjustment_facilities.id IS 'id';
COMMENT ON COLUMN mos_adjustment_facilities.typeId IS 'typeId';
COMMENT ON COLUMN mos_adjustment_facilities.facilityId IS 'facilityid';
COMMENT ON COLUMN mos_adjustment_facilities.createdBy IS 'createdBy';
COMMENT ON COLUMN mos_adjustment_facilities.createdDate IS 'createdDate';
COMMENT ON COLUMN mos_adjustment_facilities.modifiedBy IS 'modifiedBy';
COMMENT ON COLUMN mos_adjustment_facilities.modifiedDate IS 'modifiedDate';
