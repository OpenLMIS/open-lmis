DO $$
BEGIN
DROP FUNCTION IF EXISTS fn_populate_alert_equipment_nonfunctional(integer);

CREATE OR REPLACE FUNCTION fn_populate_alert_equipment_nonfunctional(in_flag integer)
  RETURNS character varying AS

$BODY$
DECLARE
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;

BEGIN
msg := 'Success!!! fn_populate_equipment_nonfunctional - Data saved successfully' ;
DELETE FROM alert_equipment_nonfunctional;
FOR rec_detail IN
SELECT nf.facilityId, nf.programId, nf.model, nf.modifieddate,nf.modifiedby, nf.operationalstatus, nf.facilityname
FROM vw_cce_repair_management_not_functional nf
LOOP
INSERT INTO alert_equipment_nonfunctional(facilityId, programId, model,modifieddate, modifiedby, facilityname, status )
VALUES (rec_detail.facilityId, rec_detail.programId, rec_detail.model, rec_detail.modifieddate::Date,(select concat(firstname,' ',lastname) as modifiedby from users where id=rec_detail.modifiedby),rec_detail.facilityname,rec_detail.operationalstatus );
END LOOP;

RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'Error!!! fn_populate_equipment_nonfunctional.' || SQLERRM ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_alert_equipment_nonfunctional(integer)
  OWNER TO postgres;
END;
$$