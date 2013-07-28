-- Function: fn_get_max_mos(integer, integer, character varying)

-- DROP FUNCTION fn_get_max_mos(integer, integer, character varying);

CREATE OR REPLACE FUNCTION fn_get_max_mos(v_program integer, v_facility integer, v_product character varying)
  RETURNS integer AS
$BODY$
DECLARE

  v_ret integer;
  v_programproductid integer;
  v_facilitytypeid integer;
  v_productid integer;
     
BEGIN
   select id into v_productid from products where code =  v_product;
   v_programproductid := fn_get_program_product_id(v_program, v_productid);
   select typeid into v_facilitytypeid from facilities where id =  v_facility;

   select maxmonthsofstock into v_ret from facility_approved_products where programproductid = v_programproductid and facilitytypeid = v_facilitytypeid;

   return v_ret;       
 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_max_mos(integer, integer, character varying)
  OWNER TO postgres;