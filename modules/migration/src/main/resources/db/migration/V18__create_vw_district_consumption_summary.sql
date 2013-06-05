CREATE VIEW public.vw_district_consumption_summary
(
  program_id,
  program_name,
  processing_periods_id,
  processing_periods_name,
  processing_periods_start_date,
  processing_periods_end_date,
  processing_schedules_id,
  processing_schedules_name,
  facility_type_name,
  facility_type_id,
  requisition_group_id,
  requisition_group_name,
  requisition_group_noteid,
  facility_code,
  facility_id,
  facility_name,
  zone_name,
  requisition_line_item_id,
  productcode,
  product,
  product_id,
  product_category_name,
  product_category_id,
  normalizedconsumption,
  id
)
AS
SELECT DISTINCT
  programs.id AS program_id,
  programs.name AS program_name,
  processing_periods.id AS processing_periods_id,
  processing_periods.name AS processing_periods_name,
  processing_periods.startdate AS processing_periods_start_date,
  processing_periods.enddate AS processing_periods_end_date,
  processing_schedules.id AS processing_schedules_id,
  processing_schedules.name AS processing_schedules_name,
  facility_types.name AS facility_type_name,
  facility_types.id AS facility_type_id,
  requisition_groups.id AS requisition_group_id,
  requisition_groups.name AS requisition_group_name,
  requisition_groups.supervisorynodeid AS requisition_group_noteid,
  facilities.code AS facility_code,
  facilities.id AS facility_id,
  facilities.name AS facility_name,
  geographic_zones.name AS zone_name,
  requisition_line_items.id AS requisition_line_item_id,
  requisition_line_items.productcode,
  requisition_line_items.product,
  products.id AS product_id,
  product_categories.name AS product_category_name,
  product_categories.id AS product_category_id,
  requisition_line_items.normalizedconsumption,
  requisition_line_items.id
FROM ((((((((((((((facilities
  JOIN facility_types ON
    (
      (facilities.typeid = facility_types.id)
    ))
	JOIN geographic_zones on
	( (geographic_zones.id = facilities.geographiczoneid)
	))
  JOIN requisitions ON
    (
      (requisitions.facilityid = facilities.id)
    ))
  JOIN requisition_line_items ON
    (
      (requisition_line_items.rnrid = requisitions.id)
    ))
  JOIN products ON
    (
      (
        (products.code)::text = (requisition_line_items.productcode)::text
      )
    ))
  JOIN product_categories ON
    (
      (
        product_categories.id = products.categoryid
      )
    ))
  JOIN program_products ON
    (
      (program_products.productid = products.id)
    ))
  JOIN programs ON
    (
      (
        (program_products.programid = programs.id) AND
        (programs.id = requisitions.programid)
      )
    ))
  JOIN programs_supported ON
    (
      (
        (programs.id = programs_supported.programid) AND
        (facilities.id = programs_supported.facilityid)
      )
    ))
  JOIN requisition_group_members ON
    (
      (facilities.id = requisition_group_members.facilityid)
    ))
  JOIN requisition_groups ON
    (
      (requisition_groups.id = requisition_group_members.requisitiongroupid)
    ))
  JOIN requisition_group_program_schedules ON
    (
      (
        (requisition_group_program_schedules.programid = programs.id) AND
        (requisition_group_program_schedules.requisitiongroupid = requisition_groups.id)
      )
    ))
  JOIN processing_schedules ON
    (
      (processing_schedules.id = requisition_group_program_schedules.programid)
    ))
  JOIN processing_periods ON
    (
      (processing_periods.scheduleid = processing_schedules.id)
    ))

  WHERE requisitions.status = 'APPROVED'