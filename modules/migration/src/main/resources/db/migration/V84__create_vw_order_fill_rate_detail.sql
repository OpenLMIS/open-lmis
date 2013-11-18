 DROP VIEW IF EXISTS vw_order_fill_rate_details CASCADE;

CREATE OR REPLACE VIEW vw_order_fill_rate_details AS
 SELECT r.programid, programs.name AS program, li.productcategory AS category,
    prc.id AS categoryid, pp.id AS periodid, pp.name AS period,
    ps.id AS scheduleid, ps.name AS schedule, ft.id AS facilitytypeid,
    count(li.quantityapproved) AS total, ft.name AS facilitytype,
    requisition_group_members.id AS rgroupid, rg.name AS rgroup, r.id AS req_id,
    f.id AS facilityid, f.name AS facility, li.productcode, li.product,
    pr.id AS productid, s.name AS supplyingfacility, gz.id AS zoneid,
    gz.name AS region, li.quantityreceived AS receipts,
    li.quantityapproved AS approved
   FROM requisition_line_items li
   JOIN requisitions r ON r.id = li.rnrid
   JOIN facilities f ON r.facilityid = f.id
   JOIN facility_types ft ON ft.id = f.typeid
   JOIN processing_periods pp ON pp.id = r.periodid
   JOIN products pr ON pr.code::text = li.productcode::text
   JOIN geographic_zones gz ON gz.id = f.geographiczoneid
   JOIN product_categories prc ON prc.id = pr.categoryid
   JOIN programs ON r.programid = programs.id
   JOIN requisition_group_members ON requisition_group_members.facilityid = f.id
   JOIN requisition_groups rg ON rg.id = requisition_group_members.requisitiongroupid
   JOIN requisition_group_program_schedules rgps ON rgps.programid = programs.id AND rgps.requisitiongroupid = rg.id
   JOIN processing_schedules ps ON ps.id = rgps.scheduleid
   JOIN supply_lines sl ON sl.supervisorynodeid = r.supervisorynodeid AND r.programid = sl.programid
   JOIN facilities s ON s.id = sl.supplyingfacilityid
  GROUP BY li.product, r.id, li.productcategory, f.name, ft.name, li.productcode, li.quantityapproved, li.quantityreceived, gz.name, r.programid, programs.name, prc.id, pp.id, ps.id, ft.id, requisition_group_members.id, rg.name, f.id, pr.id, s.name, gz.id;

ALTER TABLE vw_order_fill_rate_details
  OWNER TO postgres;
