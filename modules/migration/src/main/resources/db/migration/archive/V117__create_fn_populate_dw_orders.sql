DROP FUNCTION IF EXISTS fn_populate_dw_orders(integer);

CREATE OR REPLACE FUNCTION fn_populate_dw_orders(in_flag integer)
  RETURNS character varying AS
$BODY$
DECLARE
rec RECORD;	  
li INTEGER; 
		msg CHARACTER VARYING (2000) ; 
		

				v_programid INTEGER;
        v_geographiczoneid INTEGER ;
				v_facilityid INTEGER ;
				v_facilitycode CHARACTER VARYING (50) ;
				v_periodid INTEGER ; v_rnrid INTEGER ;
				v_status CHARACTER VARYING (20) ; v_productid INTEGER ;
				v_productcode CHARACTER VARYING (50) ;
				v_quantityrequested INTEGER ;
				v_quantityapproved INTEGER ;
				v_quantityshipped INTEGER ;
				v_quantityreceived INTEGER ;
				v_createddate TIMESTAMP ;
				v_approveddate TIMESTAMP ;
				v_shippeddate TIMESTAMP ;
				v_receiveddate TIMESTAMP ;
				v_stocking CHARACTER (1) ;
        v_reporting CHARACTER (1) ;
        v_programname character varying(50);
        v_productprimaryname character varying(150);
        v_productfullname character varying(250);
				v_geographiczonename character varying(250);
				v_processingperiodname character varying(250);


			BEGIN
				li := 0 ;
				msg := 'Data saved successfully' ;
			DELETE
				FROM
					dw_orders ; 
			FOR rec in SELECT * FROM vw_requisition_detail
      loop --fetch the table row inside the loop
				
 v_programid = rec.program_id ;
 v_geographiczoneid = rec.zone_id ;
 v_facilityid = rec.facility_id ;
 v_facilitycode = rec.facility_id ;
 v_periodid = rec.processing_periods_id ;
 v_rnrid = rec.req_id;
 v_status = rec.req_status ;
 v_productid = rec.product_id ;
 v_productcode = rec.product_code ;
 v_quantityrequested = rec.quantityrequested;
 v_quantityapproved = rec.quantityapproved;
 v_quantityshipped = 1 ;
 v_quantityreceived = rec.quantityreceived;

-- will set the date later
 v_createddate = NULL ;
 v_approveddate = NULL ;
 v_shippeddate = NULL ;
 v_receiveddate = NULL ;


 v_programname = rec.program_name;
 v_productprimaryname = rec.product_primaryname;
 v_productfullname = rec.product;
 v_processingperiodname =  rec.processing_periods_name;


 select name into v_geographiczonename from geographic_zones where id =  rec.zone_id;

 if rec.stockinhand = 0 then
  v_stocking = 'S' ;
 elseif rec.stockinhand > 0 AND rec.stockinhand <= (COALESCE(rec.amc, 0) * rec.nominaleop) THEN
 v_stocking = 'U' ;
elseif rec.stockinhand > 0 AND rec.stockinhand <= (COALESCE(rec.amc, 0) * rec.nominaleop) THEN 
v_stocking = 'O' ;
else 
 v_stocking = 'A' ;
 end if;


 INSERT INTO dw_orders (
				programid,
        geographiczoneid,
				facilityid,
				facilitycode,
				periodid,
				rnrid,
				status,
				productid,
				productcode,
				quantityrequested,
				quantityapproved,
				quantityshipped,
				quantityreceived,
				createddate,
				approveddate,
				shippeddate,
				receiveddate,
				stocking,
        reporting,
        programname,
        productprimaryname,
        productfullname,
        geographiczonename,
        processingperiodname
        

			)
			VALUES
				( v_programid,
					v_geographiczoneid,
					v_facilityid,
					v_facilitycode,
					v_periodid,
					v_rnrid,
					v_status,
					v_productid,
					v_productcode,
					v_quantityrequested,
					v_quantityapproved,
					v_quantityshipped,
					v_quantityreceived,
					v_createddate,
					v_approveddate,
					v_shippeddate,
					v_receiveddate,
					v_stocking,
          v_reporting,
          v_programname,
          v_productprimaryname,
          v_productfullname,
          v_geographiczonename,
          v_processingperiodname
				) ;
			END loop ;

-- update rnr create date
update dw_orders o
set createddate=r.createddate
from requisitions r
where o.rnrid=r.id;


-- update reporting status
--TODO: replace hard-coded value 10 with due date
update dw_orders o
set reporting= case when extract(day from r.createddate) > 10 then 'L' else 'O' end
from requisitions r
where o.rnrid=r.id;


-- update rnr approved date
update dw_orders o
set approveddate=r.modifieddate
from requisitions r
where o.rnrid=r.id
 and  r.status in ('APPROVED','RELEASED');

-- update rnr ship date and ship quantity from shipment line item for orderid and productcode
update dw_orders o
set shippeddate=s.shippeddate,
    quantityshipped = s.quantityshipped
from shipment_line_items s
where o.rnrid=s.orderid
   and o.productcode = s.productcode;

-- update rnr received date from pod
update dw_orders o
set receiveddate=p.receiveddate
from pod p
where o.rnrid=p.orderid;

 RETURN msg ;
 EXCEPTION
			WHEN OTHERS THEN
				RETURN 'Error populating data. Please consult database administrtor. ' ||SQLERRM ;
			END ; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_dw_orders(integer)
  OWNER TO postgres;
COMMENT ON FUNCTION fn_populate_dw_orders(integer) IS 'populated data in dw_orders table - a flat table to store requisition, stock status, reporting status

References:
dw_orders - table
pod - table
vw_requisition_detail - view
shipment_line_items - table

returns success message on success
returns error message on failure
';
