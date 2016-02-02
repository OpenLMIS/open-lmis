/*
  Note: This script is intended to be generic enough to create its intended schema as well as migrate preexisting data into it wherever appropriate.
*/


--Backup program_product_isa
ALTER TABLE program_product_isa RENAME TO orig_program_product_isa;

--Create a table just like it, sans the programproductid column
CREATE TABLE isa_coefficients
(
  LIKE orig_program_product_isa
  including ALL
);
ALTER TABLE isa_coefficients DROP COLUMN programproductid;
ALTER SEQUENCE program_product_isa_id_seq OWNED BY isa_coefficients.id;

/* Add populationSource to isa_coefficients. Note that a null value is intended to indicate that facility-catchment
   population, rather than an external demographic estimate, should be used. This column was added for Tanzania.
   Because it's nullable, however, it should have minimal impact on other countries. */
ALTER TABLE isa_coefficients ADD populationSource INTEGER NULL REFERENCES demographic_estimate_categories(id);


/* Add an isaCoefficientsId column to the program_products table.
   It's usually best to drop-and-recreate the table in order to be able to specify ordinal of new columns.
   In this case, however, additional columns have already been tacked on after the standard created* and modified* columns.
*/
ALTER TABLE program_products ADD isaCoefficientsId INTEGER REFERENCES isa_coefficients(id);

--Backup facility_program_products
ALTER TABLE facility_program_products RENAME TO orig_facility_program_products;

--Create a table like facility_program_products, but with an isaCoefficientsId rather than overriddenISA column
CREATE TABLE facility_program_products (
    id SERIAL PRIMARY KEY,
    facilityId INTEGER NOT NULL REFERENCES facilities(id),
    programProductId INTEGER NOT NULL REFERENCES program_products(id),
    isaCoefficientsId INTEGER REFERENCES isa_coefficients(id),
    createdBy INTEGER,
    createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modifiedBy INTEGER,
    modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (facilityId, programProductId)
);


--Migrate existing data into our new tables. Note that in the case of facility_program_products, the original overriddenisa values are lost.
INSERT INTO facility_program_products(id, facilityId, programProductId, createdBy,createdDate, modifiedBy, modifiedDate)
SELECT id, facilityId, programProductId, createdBy,createdDate, modifiedBy, modifiedDate FROM orig_facility_program_products;

INSERT INTO isa_coefficients(id, whoratio, dosesperyear, wastagefactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue)
SELECT id, whoratio, dosesperyear, wastagefactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue FROM orig_program_product_isa;

UPDATE program_products
SET isaCoefficientsId =
(
   SELECT id FROM orig_program_product_isa oppi
   WHERE oppi.programproductid = program_products.id
);


--Drop our backup tables
DROP VIEW IF EXISTS vw_stock_cards; --Newly added view dependent on facility_program_products
DROP TABLE orig_program_product_isa;
DROP TABLE orig_facility_program_products;

--Re-add a modified version of vw_stock_cards (which doesn't return an overriddenisa value)
CREATE OR REPLACE VIEW vw_stock_cards AS
 SELECT DISTINCT sc.id,
    sc.facilityid,
    sc.productid,
    sc.totalquantityonhand,
    sc.effectivedate,
    sc.notes,
    sc.createdby,
    sc.createddate,
    sc.modifiedby,
    sc.modifieddate,
    pp.id AS program_product_id,
    pp.programid,
    fap.maxmonthsofstock,
    fap.minmonthsofstock,
    fap.eop
   FROM stock_cards sc
     LEFT JOIN program_products pp ON sc.productid = pp.productid
     LEFT JOIN facilities ON sc.facilityid = facilities.id
     LEFT JOIN facility_approved_products fap ON facilities.typeid = fap.facilitytypeid AND fap.programproductid = pp.id;

ALTER TABLE vw_stock_cards
  OWNER TO postgres;



--Create a convenience function for inserting values. The last two arguments determine what the ISA should be associated with.
--If a ProgramProductId is followed by a facilityTypeID, the ISA is accociated with a facility-program-product.
--Otherwise, if a ProgramProductId is followed by nothing, the ISA is associated with simply a program-pproduct
DROP FUNCTION IF EXISTS fn_insert_isa(numeric(6,3), integer, numeric(6,3), numeric(6,3), integer, integer, integer, integer, timestamp, integer, timestamp, integer, integer, integer);
CREATE OR REPLACE FUNCTION fn_insert_isa
  (
    whoratio numeric(6,3),
    dosesperyear integer,
    wastagefactor numeric(6,3),
    bufferpercentage numeric(6,3),
    minimumvalue integer,
    maximumvalue integer,
    adjustmentvalue integer,
    createdby integer,
    createddate timestamp,
    modifiedby integer,
    modifieddate timestamp,
    populationSource integer,

    program_product_id integer,
    facility_id integer DEFAULT -1
  )
  RETURNS integer AS
  $BODY$
DECLARE
  orig_isa_id integer;
  fac_prog_prod_id integer;
  isa_coefficient_id integer;
BEGIN
  INSERT INTO isa_coefficients(whoratio, dosesperyear, wastagefactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue, createdby, createddate, modifiedby, modifieddate, populationsource)
  VALUES(whoratio, dosesperyear, wastagefactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue, createdby, createddate, modifiedby, modifieddate, populationsource)
  RETURNING id INTO isa_coefficient_id;

  IF facility_id < 0 THEN
      UPDATE program_products
      SET isaCoefficientsId = isa_coefficient_id
      WHERE id = program_product_id;
  ELSE

     SELECT id INTO fac_prog_prod_id FROM facility_program_products
     WHERE programproductid = program_product_id
     AND facilityid = facility_id;

     SELECT ic.id INTO orig_isa_id
     FROM isa_coefficients ic JOIN facility_program_products fpp
     ON fpp.isaCoefficientsId = ic.id
     WHERE fpp.id = fac_prog_prod_id;

     DELETE FROM facility_program_products
     WHERE id = fac_prog_prod_id;

     DELETE FROM isa_coefficients WHERE id = orig_isa_id;

     INSERT INTO facility_program_products(facilityId, programProductId, isaCoefficientsId)
     VALUES(facility_id, program_product_id, isa_coefficient_id);

  END IF;

  return isa_coefficient_id;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION fn_insert_isa(numeric(6,3), integer, numeric(6,3), numeric(6,3), integer, integer, integer, integer, timestamp, integer, timestamp, integer, integer, integer)
OWNER TO postgres;


--Create a convenience function for updating ProgramProductISA values.
CREATE OR REPLACE FUNCTION fn_update_program_product_isa
(
  program_product_id integer,
  isa_coefficient_id integer,

  who_ratio numeric(6,3),
  doses_per_year integer,
  wastage_factor numeric(6,3),
  buffer_percentage numeric(6,3),
  minimum_value integer,
  maximum_value integer,
  adjustment_value integer,
  created_by integer,
  created_date timestamp,
  modified_by integer,
  modified_date timestamp,
  population_source integer
)
  RETURNS void AS
$BODY$
BEGIN

  UPDATE isa_coefficients
  SET whoratio = who_ratio,
	dosesperyear = doses_per_year,
	wastagefactor = wastage_factor,
	bufferpercentage = buffer_percentage,
	minimumvalue = minimum_value,
	maximumvalue = maximum_value,
	adjustmentvalue = adjustment_value,
	createdby = created_by,
	createddate = created_date,
	modifiedby = modified_by,
	modifieddate = modified_date,
  populationsource =  population_source
  WHERE
	id = isa_coefficient_id;

  UPDATE program_products
  SET isaCoefficientsId = isa_coefficient_id
  WHERE id = program_product_id;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_update_program_product_isa(integer, integer, numeric(6,3), integer, numeric(6,3), numeric(6,3), integer, integer, integer, integer, timestamp, integer, timestamp, integer)
  OWNER TO postgres;


--Create a convenience function for removing overridden FacilityProgramProduct ISA values.
--The 3rd parameter specifies whether the FacilityProgramProduct mapping itself should be removed as well.
DROP FUNCTION IF EXISTS fn_delete_facility_program_product_isa(integer, integer, boolean);
CREATE OR REPLACE FUNCTION fn_delete_facility_program_product_isa
(
  program_product_id integer,
  facility_id integer,
  delete_facility_pro_prod_mapping boolean
)
  RETURNS void AS
$BODY$
DECLARE
  isa_coefficient_id integer;
BEGIN
  SELECT INTO isa_coefficient_id ic.id
  FROM facility_program_products fpp JOIN isa_coefficients ic
  ON fpp.isaCoefficientsId = ic.id
  WHERE fpp.facilityid = facility_id AND fpp.programproductid = program_product_id;

  IF delete_facility_pro_prod_mapping THEN
     DELETE FROM facility_program_products
     WHERE facilityid = facility_id AND programproductid = program_product_id;
  ELSE
     UPDATE facility_program_products
     SET isaCoefficientsId = NULL
     WHERE facilityid = facility_id AND programproductid = program_product_id;
  END IF;

  DELETE FROM isa_coefficients
  WHERE id = isa_coefficient_id;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_delete_facility_program_product_isa(integer, integer, boolean)
  OWNER TO postgres;