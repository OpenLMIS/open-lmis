
DROP VIEW IF EXISTS dw_product_lead_time_vw;

CREATE OR REPLACE VIEW dw_product_lead_time_vw AS 
 SELECT dw_orders.programid,
    dw_orders.geographiczoneid,
    dw_orders.periodid,
    facilities.name,
    facilities.code,
    dw_orders.facilityid,
    sum(date_part('day'::text, age(dw_orders.authorizeddate, dw_orders.submitteddate))) AS subtoauth,
    sum(date_part('day'::text, age(dw_orders.inapprovaldate, dw_orders.authorizeddate))) AS authtoinapproval,
    sum(date_part('day'::text, age(dw_orders.approveddate, dw_orders.inapprovaldate))) AS inapprovaltoapproved,
    sum(date_part('day'::text, age(dw_orders.releaseddate, dw_orders.approveddate))) AS approvedtoreleased
   FROM dw_orders
   JOIN facilities ON facilities.id = dw_orders.facilityid
  WHERE dw_orders.status::text = 'RELEASED'::character varying::text
  GROUP BY dw_orders.programid, dw_orders.geographiczoneid, dw_orders.periodid, facilities.name, facilities.code, dw_orders.facilityid;

ALTER TABLE dw_product_lead_time_vw
  OWNER TO postgres;
COMMENT ON VIEW dw_product_lead_time_vw
  IS 'dw_product_lead_time_vw-
calculate product shipping lead time - Total days from the day order submitted to received
Filters: Geographic zone id (district), periodid, program
created March 14, 2014 wolde';
