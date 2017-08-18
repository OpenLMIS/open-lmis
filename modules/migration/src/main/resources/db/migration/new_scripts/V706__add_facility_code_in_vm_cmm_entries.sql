DROP MATERIALIZED VIEW IF EXISTS vw_cmm_entries;

CREATE MATERIALIZED VIEW vw_cmm_entries AS
  SELECT
    uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,
    cmm_entries.id                      AS id,
    cmm_entries.cmmvalue                AS cmmvalue,
    cmm_entries.productcode             AS productcode,
    cmm_entries.facilityid              AS facilityid,
    facilities.code                     AS facilityCode,
    cmm_entries.periodbegin             AS periodbegin,
    cmm_entries.periodend               AS periodend,
    zone.name                           AS district_name,
    zone.code                           AS district_code,
    parent_zone.name                    AS province_name,
    parent_zone.code                    AS province_code
  FROM cmm_entries AS cmm_entries
    JOIN facilities ON facilities.id = cmm_entries.facilityid
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id;

CREATE UNIQUE INDEX idx_vw_cmm_entries ON vw_cmm_entries (uuid, facilitycode, productcode);

CREATE OR REPLACE FUNCTION refresh_vw_cmm_entries()
  RETURNS INT LANGUAGE plpgsql
AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vw_cmm_entries;
  RETURN 1;
END $$;