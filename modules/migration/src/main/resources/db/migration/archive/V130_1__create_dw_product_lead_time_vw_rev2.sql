DROP VIEW IF EXISTS dw_product_lead_time_vw;

CREATE OR REPLACE VIEW dw_product_lead_time_vw AS

 SELECT dw_orders.programid, dw_orders.geographiczoneid, dw_orders.periodid,
  facilities.name,facilities.code,dw_orders.facilityid,
	  sum(date_part('day',age(authorizeddate,submitteddate))) AS subToAuth,
	  sum(date_part('day',age(inapprovaldate,authorizeddate))) AS authToInApproval,
	  sum(date_part('day',age(approveddate,inapprovaldate))) AS inApprovalToApproved,
	  sum(date_part('day',age(releaseddate,approveddate))) AS approvedToReleased
  
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
created March 14, 2014 wolde
';
