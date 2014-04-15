DROP FUNCTION IF EXISTS fn_populate_dw_orders(integer);

CREATE OR REPLACE FUNCTION fn_populate_dw_orders(in_flag integer)
  RETURNS character varying AS
$BODY$

 DECLARE
 rec RECORD ;
 rec2 RECORD ;
 li INTEGER ;
 msg CHARACTER VARYING (2000) ;
 v_programid INTEGER ;
 v_geographiczoneid INTEGER ;
 v_facilityid INTEGER ;
 v_facilitycode CHARACTER VARYING (50) ;
 v_periodid INTEGER ;
 v_rnrid INTEGER ;
 v_status CHARACTER VARYING (20) ;
 v_productid INTEGER ;
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
 v_programname CHARACTER VARYING (50) ;
 v_facilityname CHARACTER VARYING (50) ;
 v_productprimaryname CHARACTER VARYING (150) ;
 v_productfullname CHARACTER VARYING (250) ;
 v_geographiczonename CHARACTER VARYING (250) ;
 v_processingperiodname CHARACTER VARYING (250) ;
 v_soh INTEGER;
 v_amc  INTEGER;
 v_mos numeric(6,1);
 v_emergency boolean;

 v_requisitiongroupid integer;
 v_requisitiongroupname character varying(50);
 v_facilitytypeid integer;
 v_facilitytypename character varying(50);
 v_scheduleid integer;
 v_schedulename character varying(50);
 v_productcategoryid integer;
 v_productcategoryname character varying(150);
 v_productgroupid integer;
 v_productgroupname character varying(250);
 v_stockedoutinpast boolean;
 v_suppliedinpast boolean;
 v_mossuppliedinpast numeric(6,1);
 
BEGIN
   li := 0 ; 
   msg := 'Data saved successfully' ;
 
DELETE FROM dw_orders ; 

FOR rec IN 

SELECT
vw_requisition_detail.*,
requisition_groups.name as requisitiongroupname,
processing_schedules.name as schedulename,
product_categories.name as categoryname
FROM
vw_requisition_detail
INNER JOIN requisition_groups ON requisition_groups.id = vw_requisition_detail.requisitiongroupid
INNER JOIN processing_schedules ON processing_schedules.id = vw_requisition_detail.scheduleid
INNER JOIN product_categories ON product_categories.id = vw_requisition_detail.categoryid

 LOOP --fetch the table row inside the loop
 
 v_programid = rec.program_id ;
 v_geographiczoneid = rec.zone_id ;
 v_facilityid = rec.facility_id ;
 v_facilitycode = rec.facility_code ;
 v_periodid = rec.processing_periods_id ;
 v_rnrid = rec.req_id ;
 v_status = rec.req_status ;
 v_productid = rec.product_id ;
 v_productcode = rec.product_code ;
 v_quantityrequested = rec.quantityrequested ;
 v_quantityapproved = rec.quantityapproved ;
 v_quantityshipped = 1 ;
 v_quantityreceived = rec.quantityreceived ; -- will set the date later
 v_createddate = NULL ;
 v_approveddate = NULL ;
 v_shippeddate = NULL ;
 v_receiveddate = NULL ;
 v_programname = rec.program_name ;
 v_facilityname = rec.facility_name ;
 v_productprimaryname = rec.product_primaryname ;
 v_productfullname = rec.product ;
 v_processingperiodname = rec.processing_periods_name ; 
 v_soh = rec.stockinhand; 
 v_amc = rec.amc;
 v_mos = CASE WHEN v_amc > 0 THEN v_soh / v_amc ELSE 0 END;
 v_emergency = rec.emergency;


 v_requisitiongroupid = rec.requisitiongroupid;
 v_requisitiongroupname = rec.requisitiongroupname;
 v_facilitytypeid = rec.facility_type_id;
 v_facilitytypename = rec.facility_type_name;
 v_scheduleid = rec.scheduleid;
 v_schedulename = rec.schedulename;
 v_productcategoryid = rec.categoryid;
 v_productcategoryname = rec.categoryname;
 v_productgroupid = rec.productgroupid;
 v_productgroupname = rec.productgroupid;
 v_stockedoutinpast = 'N';
 v_suppliedinpast = 'N';
 v_mossuppliedinpast = 1;

  SELECT
			NAME INTO v_geographiczonename
		FROM
			geographic_zones
		WHERE                          wh
			ID = rec.zone_id ;
		IF rec.stockinhand = 0 THEN
			v_stocking = 'S' ;
		ELSEIF rec.stockinhand > 0
		AND rec.stockinhand <= (
			COALESCE (rec.amc, 0) * rec.nominaleop
		) THEN
			v_stocking = 'U' ;
		ELSEIF rec.stockinhand > 0
		AND rec.stockinhand >= (
			COALESCE (rec.amc, 0) * rec.nominaleop
		) THEN
			v_stocking = 'O' ;
		ELSE
			v_stocking = 'A' ;
		END
		IF ; 
   
/*  
 FOR rec2 IN SELECT
			*
		FROM
			requisition_status_changes where rnrid = rec.req_id LOOP
		IF rec2.status = 'INITIATED' THEN
			v_initiateddate = rec2.modifieddate ;
		END
		IF ;
		IF rec2.status = 'SUBMITTED' THEN
			v_submitteddate = rec2.modifieddate ;
		END
		IF ;
		IF rec2.status = 'IN_APPROVAL' THEN
			v_inapprovaldate = rec2.modifieddate ;
		END
		IF ;
		IF rec2.status = 'APPROVED' THEN
			v_approveddate = rec2.modifieddate ;
		END
		IF ;
		IF rec2.status = 'RELEASED' THEN
			v_releaseddate = rec2.modifieddate ;
		END
		IF ;
		END LOOP ;
*/
 INSERT INTO dw_orders (
			programid,
			geographiczoneid,
			facilityid,
			facilitycode,
			periodid,
			rnrid,
			emergency,
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
			facilityname,
			productprimaryname,
			productfullname,
			geographiczonename,
			processingperiodname,
      soh,
      amc,
      mos,
			requisitiongroupid,
			requisitiongroupname, 
			facilitytypeid, 
			facilitytypename, 
			scheduleid, 
			schedulename, 
			productcategoryid, 
			productcategoryname, 
			productgroupid, 
			productgroupname, 
			stockedoutinpast, 
			suppliedinpast, 
			mossuppliedinpast
		)
		VALUES
			(
				v_programid,
				v_geographiczoneid,
				v_facilityid,
				v_facilitycode,
				v_periodid,
				v_rnrid,
				v_emergency,
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
				v_facilityname,
				v_productprimaryname,
				v_productfullname,
				v_geographiczonename,
				v_processingperiodname,
        v_soh,
        v_amc,
        v_mos,
				v_requisitiongroupid,
				v_requisitiongroupname, 
				v_facilitytypeid, 
				v_facilitytypename, 
				v_scheduleid, 
				v_schedulename, 
				v_productcategoryid, 
				v_productcategoryname, 
				v_productgroupid, 
				v_productgroupname, 
				v_stockedoutinpast, 
				v_suppliedinpast, 
				v_mossuppliedinpast
			) ;
		END loop ; -- update rnr create date

		UPDATE dw_orders o
		SET createddate = r.createddate
		FROM
			requisitions r
		WHERE
			o.rnrid = r. ID ;

		UPDATE dw_orders o
		SET initiateddate = r.createddate
		FROM
			requisition_status_changes r
		WHERE
			o.rnrid = r.rnrid
      AND r.status = 'INITIATED';


	UPDATE dw_orders o
		SET submitteddate = r.createddate
		FROM
			requisition_status_changes r
		WHERE
			o.rnrid = r.rnrid
      AND r.status = 'SUBMITTED';


	UPDATE dw_orders o
		SET authorizeddate = r.createddate
		FROM
			requisition_status_changes r
		WHERE
			o.rnrid = r.rnrid
      AND r.status = 'AUTHORIZED';


	UPDATE dw_orders o
		SET inapprovaldate = r.createddate
		FROM
			requisition_status_changes r
		WHERE
			o.rnrid = r.rnrid
      AND r.status = 'IN_APPROVAL';


	UPDATE dw_orders o
		SET approveddate = r.createddate
		FROM
			requisition_status_changes r
		WHERE
			o.rnrid = r.rnrid
      AND r.status = 'APPROVED';

	UPDATE dw_orders o
		SET releaseddate = r.createddate
		FROM
			requisition_status_changes r
		WHERE
			o.rnrid = r.rnrid
      AND r.status = 'RELEASED';


     -- update reporting status
		--TODO: replace hard-coded value 10 with due date
			UPDATE dw_orders o
		SET reporting = CASE
		WHEN EXTRACT (DAY FROM r.createddate) > 10 THEN
			'L'
		ELSE
			'O'
		END
		FROM
			requisitions r
		WHERE
			o.rnrid = r. ID ; -- update rnr approved date
		
		
    UPDATE dw_orders o
		SET shippeddate = s.shippeddate,
		quantityshipped = s.quantityshipped
	FROM
		shipment_line_items s
	WHERE
		o.rnrid = s.orderid
	AND o.productcode = s.productcode ; -- update rnr received date from pod
	UPDATE dw_orders o
	SET receiveddate = P .receiveddate
	FROM
		pod P
	WHERE
		o.rnrid = P .orderid ; RETURN msg ; EXCEPTION
	WHEN OTHERS THEN
		RETURN 'Error populating data. Please consult database administrtor. ' || SQLERRM ;
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
