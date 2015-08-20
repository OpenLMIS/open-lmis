DROP TABLE IF EXISTS vaccine_facility_targets;
CREATE TABLE vaccine_facility_targets
(
targetYear integer, -- year
facilityId integer, -- facility code
targetPopulation integer, -- target population
expectedBirths integer, -- expected births
expectedPregnancies integer, -- expected pregnancies
pregnantWomen integer, -- pregnant women
survingInfants integer, -- surviving infants
children01 integer, -- children 0-1
children12 integer, -- children 1-2
adolocentGirls integer -- adolocent girls 
)
WITH (
  OIDS=FALSE
);

ALTER TABLE vaccine_facility_targets
  OWNER TO openlmis;

COMMENT ON COLUMN vaccine_facility_targets.targetYear	IS 'year';
COMMENT ON COLUMN vaccine_facility_targets.facilityId	IS 'facilityid';
COMMENT ON COLUMN vaccine_facility_targets.targetPopulation	IS 'targetpopulation';
COMMENT ON COLUMN vaccine_facility_targets.expectedBirths	IS 'expectedbirths';
COMMENT ON COLUMN vaccine_facility_targets.expectedPregnancies	IS 'expectedpregnancies';
COMMENT ON COLUMN vaccine_facility_targets.pregnantWomen	IS 'pregnantwomen';
COMMENT ON COLUMN vaccine_facility_targets.survingInfants	IS 'survinginfants';
COMMENT ON COLUMN vaccine_facility_targets.children01	IS 'children01';
COMMENT ON COLUMN vaccine_facility_targets.children12	IS 'children12';
COMMENT ON COLUMN vaccine_facility_targets.adolocentGirls	IS 'adolocentgirls';


CREATE UNIQUE INDEX uc_vaccine_facility_targets_year
  ON vaccine_facility_targets
  USING btree
  (facilityid, targetyear);
COMMENT ON INDEX uc_vaccine_targets_year
  IS 'One target per facility per year allowed';






