-- Function: fn_changeproductcodes()

DROP FUNCTION IF EXISTS fn_changeproductcodes();

CREATE OR REPLACE FUNCTION fn_changeproductcodes()
  RETURNS character varying AS
$BODY$
DECLARE

/*
 This function will update productcode field of products table to new code from product_code_change_log table
 02/04/2014 mahmed - created
*/
message character varying(200);

BEGIN

message = 'ok';
--DELETE FROM product_code_change_log WHERE EXISTS (SELECT 1 FROM products WHERE product_code_change_log.old_code = products.code);

-- Step : Delete constrains. 
EXECUTE 'ALTER TABLE pod_line_items DROP CONSTRAINT IF EXISTS pod_line_items_productcode_fkey';
EXECUTE 'ALTER TABLE requisition_line_items DROP CONSTRAINT IF EXISTS requisition_line_items_productcode_fkey';
EXECUTE 'ALTER TABLE shipment_line_items DROP CONSTRAINT IF EXISTS shipment_line_items_productcode_fkey';


-- Step : Update product code to new code 
 update products p set code=m.new_code from product_code_change_log m where p.code=m.old_code and m.migrated = false;
 update requisition_line_items p set productcode=m.new_code from product_code_change_log m where p.productcode=m.old_code and m.migrated = false;
 update pod_line_items p set productcode=m.new_code from product_code_change_log m where p.productcode=m.old_code and m.migrated = false;
 update shipment_line_items p set productcode=m.new_code from product_code_change_log m where p.productcode=m.old_code and m.migrated = false;
 update product_code_change_log c set changeddate=now(), migrated = true from products p where c.new_code=p.code and c.migrated = false;



-- Step : revert changes (for retest)
/*
 update products p set code=m.old_code from product_code_change_log m where p.code=m.new_code;
 update requisition_line_items p set productcode=m.old_code from product_code_change_log m where p.productcode=m.new_code;
 update pod_line_items p set productcode=m.old_code from product_code_change_log m where p.productcode=m.new_code;
 update shipment_line_items p set productcode=m.old_code from product_code_change_log m where p.productcode=m.new_code;
*/


-- Step : Add constrains

EXECUTE 'ALTER TABLE pod_line_items ADD  CONSTRAINT pod_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products (code) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION';
EXECUTE 'ALTER TABLE requisition_line_items ADD  CONSTRAINT requisition_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products (code) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION';
EXECUTE 'ALTER TABLE shipment_line_items ADD  CONSTRAINT shipment_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products (code) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION';


RETURN message;
EXCEPTION WHEN OTHERS THEN RETURN SQLERRM;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_changeproductcodes()
  OWNER TO postgres;
