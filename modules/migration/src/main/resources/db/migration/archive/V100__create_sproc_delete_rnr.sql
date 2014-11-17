-- Function: fn_delete_rnr(integer)

-- DROP FUNCTION fn_delete_rnr(integer);

CREATE OR REPLACE FUNCTION fn_delete_rnr(in_rnrid integer)
  RETURNS character varying AS
  $BODY$

/* 01/10/2014 created by Muhammad Ahmed
fn_delete_rnr deletes rnr and associated records. Return 0 if successful, 1 is failed
IN Parameter: 
   in_rnrid - rnr_id
OUT - success or failure message

The following tables are affected.

requisitions
requisition_line_items
regimen_line_items
requisition_status_changes
orders
comments

*/
DECLARE i RECORD;
DECLARE j RECORD;
DECLARE li integer;
DECLARE v_rnr_id integer;
DECLARE v_rli_id integer;


DECLARE msg character varying(2000);
    
  BEGIN
    li := 0;
    msg := 'Requisition id ' || in_rnrid || ' not found. No record deleted.';

    select id into v_rnr_id from requisitions where id = in_rnrid;

    if v_rnr_id > 0 then
     msg = 'Requisition id ' || in_rnrid || ' deleted successfully.';


  -- delete losses and adjustments
  DELETE  FROM  requisition_line_item_losses_adjustments where requisitionlineitemid in (select id from requisition_line_items where rnrid in (select id from requisitions where id = v_rnr_id));

  select id into li from requisition_line_items where rnrid = in_rnrid limit 1;

    -- delete requisition line items
    if li > 0 then
     DELETE FROM requisition_line_items WHERE rnrid= in_rnrid;
    end if;
    --
     DELETE FROM requisition_status_changes where rnrid = v_rnr_id;
     DELETE FROM regimen_line_items where rnrid = v_rnr_id;
     DELETE FROM orders where id = v_rnr_id;
     DELETE FROM comments where rnrid = v_rnr_id;
     DELETE FROM requisitions WHERE id= in_rnrid;
  end if;

    --COMMIT;

    RETURN msg;
     EXCEPTION WHEN OTHERS THEN
     --ROLLBACK;
      RETURN 'Error in deleting requisition id ' || in_rnrid || '. Please consult database administrtor.';

  END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION fn_delete_rnr(integer)
OWNER TO postgres;