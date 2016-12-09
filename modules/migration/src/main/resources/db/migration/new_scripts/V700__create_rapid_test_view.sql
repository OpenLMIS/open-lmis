DROP MATERIALIZED VIEW IF EXISTS vw_rapid_test;

CREATE MATERIALIZED VIEW vw_rapid_test AS
  SELECT
    uuid_in(md5(random() :: TEXT || now() :: TEXT) :: cstring) AS uuid,
    data_items.name                     AS item_name,
    data_items.value                    AS item_value,
    data_items.formid                   AS formid,
    colums.code                         AS column_code,
    data_forms.facilityid               AS facilityid,
    facilities.name                     AS facility_name,
    facilities.code                     AS facility_code,
    zone.name                           AS district_name,
    zone.code                           AS district_code,
    parent_zone.name                    AS province_name,
    parent_zone.code                    AS province_code,

    data_forms.supplementalprogramid    AS supplementalprogramid,
    supplemental_programs.code          AS form_code,
    supplemental_programs.name          AS form_name,
    supplemental_programs.description   AS form_description,

    data_forms.modifiedby               AS modifyby,
    data_forms.createdby                AS createdby,
    data_forms.startdate                AS startdate,
    data_forms.enddate                  AS enddate
  FROM program_data_items AS data_items
    JOIN program_data_columns AS colums ON data_items.programdatacolumnid = colums.id
    JOIN program_data_forms AS data_forms ON data_forms.id = data_items.formid
    JOIN facilities ON facilities.id = data_forms.facilityid
    JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
    JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
    JOIN supplemental_programs ON supplemental_programs.id = data_forms.supplementalprogramid;

CREATE UNIQUE INDEX idx_vw_rapid_test ON vw_rapid_test (uuid);