-- Function: fn_get_program_product_id(integer, integer)

-- DROP FUNCTION fn_get_program_product_id(integer, integer);

CREATE OR REPLACE FUNCTION fn_get_program_product_id(v_program integer, v_product integer)
  RETURNS integer AS
$BODY$
DECLARE

  v_ret integer;
    
BEGIN
   SELECT id into v_ret FROM program_products where programid = v_program and productid = v_product;
     
 
     return v_ret;       
 
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_get_program_product_id(integer, integer)
  OWNER TO postgres;