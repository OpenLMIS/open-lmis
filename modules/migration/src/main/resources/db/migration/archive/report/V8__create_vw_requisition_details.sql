CREATE VIEW public.vw_requisition_detail
(
  program_id,
  program_name,
  processing_periods_id,
  processing_periods_name,
  processing_periods_start_date,
  processing_periods_end_date,
  processing_schedules_id,
  processing_schedules_name,
  facility_type_id,
  facility_type_name,
  requisition_group_id,
  requisition_group_name,
  requisition_group_noteid,
  facility_code,
  facility_name,
  productcode,
  product,
  beginningbalance,
  quantityreceived,
  quantitydispensed,
  stockinhand,
  quantityrequested,
  calculatedorderquantity,
  quantityapproved,
  totallossesandadjustments,
  newpatientcount,
  stockoutdays,
  normalizedconsumption,
  amc,
  maxmonthsofstock,
  maxstockquantity,
  packstoship,
  packsize,
  fullsupply,
  facility_id
)
AS
SELECT DISTINCT
  programs.id AS program_id,
  programs.name AS program_name,
  processing_periods.id AS processing_periods_id,
  processing_periods.name AS processing_periods_name,
  processing_periods.startdate AS processing_periods_start_date,
  processing_periods.startdate AS processing_periods_end_date,
  processing_schedules.id AS processing_schedules_id,
  processing_schedules.name AS processing_schedules_name,
  facility_types.id AS facility_type_id,
  facility_types.name AS facility_type_name,
  requisition_groups.id AS requisition_group_id,
  requisition_groups.name AS requisition_group_name,
  requisition_groups.supervisorynodeid AS requisition_group_noteid,
  facilities.code AS facility_code,
  facilities.name AS facility_name,
  requisition_line_items.productcode,
  requisition_line_items.product,
  requisition_line_items.beginningbalance,
  requisition_line_items.quantityreceived,
  requisition_line_items.quantitydispensed,
  requisition_line_items.stockinhand,
  requisition_line_items.quantityrequested,
  requisition_line_items.calculatedorderquantity,
  requisition_line_items.quantityapproved,
  requisition_line_items.totallossesandadjustments,
  requisition_line_items.newpatientcount,
  requisition_line_items.stockoutdays,
  requisition_line_items.normalizedconsumption,
  requisition_line_items.amc,
  requisition_line_items.maxmonthsofstock,
  requisition_line_items.maxstockquantity,
  requisition_line_items.packstoship,
  requisition_line_items.packsize,
  requisition_line_items.fullsupply,
  facilities.id AS facility_id
FROM ((((((((((((facilities
  JOIN facility_types ON
    (
      (facilities.typeid = facility_types.id)
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
    ));