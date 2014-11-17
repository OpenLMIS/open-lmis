CREATE VIEW public.vw_program_facility_supplier
(
  program_name,
  facility_name,
  supplying_facility_id,
  supervisory_node_id,
  program_id,
  facility_id,
  facility_code,
  supply_line_id
)
AS
SELECT
  programs.name AS program_name,
  facilities.name AS facility_name,
  supply_lines.supplyingfacilityid AS supplying_facility_id,
  supervisory_nodes.name AS supervisory_node_id,
  programs.id AS program_id,
  facilities.id AS facility_id,
  facilities.code AS facility_code,
  supply_lines.supervisorynodeid AS supply_line_id
FROM (((supply_lines
  JOIN supervisory_nodes ON
    (
      (supply_lines.supervisorynodeid = supervisory_nodes.id)
    ))
  JOIN facilities ON
    (
      (supply_lines.supplyingfacilityid = facilities.id)
    ))
  JOIN programs ON
    (
      (supply_lines.programid = programs.id)
    ));