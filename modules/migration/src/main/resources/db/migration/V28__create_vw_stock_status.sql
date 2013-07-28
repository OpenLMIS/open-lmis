-- View: vw_stock_status

-- DROP VIEW vw_stock_status;

CREATE OR REPLACE VIEW vw_stock_status AS 
 SELECT DISTINCT fn_get_supplying_facility_name(requisitions.supplyingfacilityid) AS supplyingfacility, 
    facilities.name AS facility, requisition_line_items.product, 
    requisition_line_items.stockinhand, requisition_line_items.amc, 
        CASE
            WHEN COALESCE(requisition_line_items.amc, 0) = 0 THEN 0::numeric
            ELSE round((requisition_line_items.stockinhand / requisition_line_items.amc)::numeric, 1)
        END AS mos, 
    COALESCE(
        CASE
            WHEN (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth - requisition_line_items.stockinhand) < 0 THEN 0
            ELSE COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth - requisition_line_items.stockinhand
        END, 0) AS required, 
        CASE
            WHEN requisition_line_items.stockinhand = 0 THEN 'Stockedout'::text
            ELSE 
            CASE
                WHEN requisition_line_items.stockinhand > 0 AND requisition_line_items.stockinhand::numeric <= (COALESCE(requisition_line_items.amc, 0)::numeric * facility_types.nominaleop) THEN 'Under Stocked'::text
                ELSE 
                CASE
                    WHEN requisition_line_items.stockinhand > (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) THEN 'Over Stocked'::text
                    ELSE 'Stocked-according-to-plan'::text
                END
            END
        END AS status
   FROM facilities
   JOIN facility_types ON facilities.typeid = facility_types.id
   JOIN requisitions ON requisitions.facilityid = facilities.id
   JOIN requisition_line_items ON requisition_line_items.rnrid = requisitions.id
   JOIN products ON products.code::text = requisition_line_items.productcode::text
   JOIN program_products ON program_products.productid = products.id
   JOIN programs ON program_products.programid = programs.id AND programs.id = requisitions.programid
   JOIN programs_supported ON programs.id = programs_supported.programid AND facilities.id = programs_supported.facilityid
   JOIN requisition_group_members ON facilities.id = requisition_group_members.facilityid
   JOIN requisition_groups ON requisition_groups.id = requisition_group_members.requisitiongroupid
   JOIN requisition_group_program_schedules ON requisition_group_program_schedules.programid = programs.id AND requisition_group_program_schedules.requisitiongroupid = requisition_groups.id
   JOIN processing_schedules ON processing_schedules.id = requisition_group_program_schedules.programid
   JOIN processing_periods ON processing_periods.scheduleid = processing_schedules.id
  WHERE requisition_line_items.stockinhand IS NOT NULL AND requisitions.status::text = 'RELEASED'::text;

ALTER TABLE vw_stock_status
  OWNER TO postgres;

