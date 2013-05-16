CREATE OR REPLACE FUNCTION public.fn_delete_sample_data()
RETURNS void AS
$$
DECLARE
/*
fn_delete_sample_data() - This function delete sample data create through script. It uses
a special userid 1001 to delete records.
Input:

 Output:

--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/
BEGIN
  BEGIN
    -- delete sample records
    DELETE FROM requisition_line_items  WHERE modifiedby = 1001;
    DELETE FROM requisitions  WHERE createdby = 1001;
    DELETE FROM users  WHERE id = 1001;
   EXCEPTION WHEN OTHERS THEN
    -- do nothing
    NULL;
  END;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
--------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_random_number
(
  IN   integer,
  IN   integer
)
RETURNS integer AS
$$
DECLARE
    start_int ALIAS FOR $1;
    end_int ALIAS FOR $2;
BEGIN
    RETURN trunc(random() * (end_int-start_int) + start_int);
END;
$$
LANGUAGE 'plpgsql'
VOLATILE
RETURNS NULL ON NULL INPUT
SECURITY INVOKER
COST 100;
------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_program_product_id
(
  IN  v_program  integer,
  IN  v_product  integer
)
RETURNS integer AS
$$
DECLARE

  v_ret integer;

BEGIN
   SELECT id into v_ret FROM program_products where programid = v_program and productid = v_product;


     return v_ret;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
-----------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_amc
(
  IN  v_program    integer,
  IN  v_facility   integer,
  IN  v_product    varchar,
  IN  v_period     integer,
  IN  v_dispensed  integer
)
RETURNS integer AS
$$
DECLARE
/*
fn_get_amc() - This function calculates amc for a given program, facility, product and period.
This function is primarily used for data generation script but could be
Input:
  v_program - Program id
  v_facility - Facility id
  v_product  - Product code
  v_dispensed - quantity dispensed in the current period

 Output:
   return average from current and last two requisition period

TODO: Check business rules
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/
 v_quantitydispensed  requisition_line_items.quantitydispensed%TYPE;
 v_req integer;
 t_period integer;
 v_total integer;
 v_amc integer;
 v_period_num integer;

BEGIN
     v_total = 0;
     v_period_num = 0;

     v_total = v_total + v_dispensed;
     v_period_num = v_period_num + 1;


     t_period = v_period-1;
     if t_period = 0 then
      t_period = 1;
     end if;

     select id into v_req from requisitions where programid = v_program and facilityid = v_facility and periodid = t_period;

     if v_req > 0 then
      select quantitydispensed into v_quantitydispensed from requisition_line_items where rnrid = v_req  and productcode = v_product;
      v_total = v_total + v_quantitydispensed;
      v_period_num = v_period_num + 1;
     end if;

    t_period = v_period -2;
     if t_period = 0 then
      t_period = 1;
     end if;

     select id into v_req from requisitions where programid = v_program and facilityid = v_facility and periodid = t_period;

     if v_req > 0 then
      select quantitydispensed into v_quantitydispensed from requisition_line_items where rnrid = v_req  and productcode = v_product;
      v_total = v_total + v_quantitydispensed;
      v_period_num = v_period_num + 1;
     end if;

     if v_period_num > 0 then
      v_amc = v_total / v_period_num;
     else
      v_amc = 0;
     end if;


     return v_amc;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
-----------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_max_mos
(
  IN  v_program   integer,
  IN  v_facility  integer,
  IN  v_product   varchar
)
RETURNS integer AS
$$
DECLARE
/*
fn_get_max_mos() - This function return maximum months of stock a facility can have based on the program and type.
This is primarily used by data generation script but may be adapted for application use

Input:
  v_program - Program id
  v_facility - Facility id
  v_product  - Product code


 Output:
   integer - maximum months of stock
TODO: Check business rules, use cursor where possible
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/
  v_ret integer;
  v_programproductid integer;
  v_facilitytypeid integer;
  v_productid integer;

BEGIN

   select id into v_productid from products where code =  v_product;
   -- lookup program-product key
   v_programproductid := fn_get_program_product_id(v_program, v_productid);
   -- determine type of facility
   select typeid into v_facilitytypeid from facilities where id =  v_facility;

   select maxmonthsofstock into v_ret from facility_approved_products where programproductid = v_programproductid and facilitytypeid = v_facilitytypeid;
   -- return
   return v_ret;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_open_balance
(
  IN  v_program   integer,
  IN  v_facility  integer,
  IN  v_product   varchar,
  IN  v_period    integer
)
RETURNS integer AS
$$
DECLARE
/*
fn_get_open_balance() - Returns last period ending balance as opening balance of the current period
This is primarily used by data generation script but may be adapted for application use

Input:
  v_program integer -  Program id
  v_facility integer - Facility id
  v_product character varying - product code
  v_period integer - period id

 Output:
   integer - open balance of current period
TODO: Check business rules, use cursor where possible
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/
 v_beginningbalance  requisition_line_items.beginningbalance%TYPE;
 v_req integer;
 t_period integer;

BEGIN
     -- last requisition period
     t_period = v_period - 1;

     -- requisition id of the period above
     select id into v_req from requisitions where programid = v_program and facilityid = v_facility and periodid = t_period;
     -- save stock on hand from previous period
     select stockinhand into v_beginningbalance from requisition_line_items where rnrid = v_req  and productcode = v_product;

     -- set to 0 if no data
     v_beginningbalance = coalesce(v_beginningbalance, 0);

     -- return
     return v_beginningbalance;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
----------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_packtoship
(
  IN  v_product  varchar,
  IN  v_qty      integer
)
RETURNS integer AS
$$
DECLARE

  v_ret integer;
  v_packsize integer;
  v_roundingrule integer;

BEGIN
   SELECT packsize, packroundingthreshold into v_packsize,v_roundingrule FROM products where code =  v_product;

   v_packsize = coalesce(v_packsize, 1);
   v_roundingrule = coalesce(v_roundingrule, 1);

   if v_packsize > 0 then
    v_ret = round(v_qty / v_packsize);
   end if;

   return v_ret;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_product_category
(
  IN  v_id  varchar
)
RETURNS varchar AS
$$
DECLARE

 v_ret varchar(200);
BEGIN

    select name into v_ret from product_categories, products where products.categoryid = product_categories.id and products.code = v_id;

    RETURN v_ret;
END;
$$
LANGUAGE 'plpgsql'
VOLATILE
RETURNS NULL ON NULL INPUT
SECURITY INVOKER
COST 100;
------------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_product_unit
(
  IN  v_id  varchar
)
RETURNS varchar AS
$$
DECLARE

 v_ret varchar(200);
BEGIN
    select dispensingunit into v_ret from products where code = v_id;

    RETURN v_ret;
END;
$$
LANGUAGE 'plpgsql'
VOLATILE
RETURNS NULL ON NULL INPUT
SECURITY INVOKER
COST 100;
-----------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_supervisorynote_id
(
  IN  v_facility  integer
)
RETURNS integer AS
$$
DECLARE

  v_ret integer;

BEGIN
   SELECT supervisory_nodes.id into v_ret FROM   public.facilities, public.supervisory_nodes WHERE supervisory_nodes.facilityid = facilities.id and facilities.id = v_facility;

      return v_ret;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
-------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_total_adj
(
  IN  v_line  integer
)
RETURNS integer AS
$$
DECLARE

 v_ret integer;


BEGIN

  SELECT
  sum(case cast(losses_adjustments_types.additive as integer)
  when 0
  then requisition_line_item_losses_adjustments.quantity * -1
  else requisition_line_item_losses_adjustments.quantity * 1
  end )
  into v_ret
FROM
  public.requisition_line_item_losses_adjustments,
  public.losses_adjustments_types
WHERE
  requisition_line_item_losses_adjustments.type = losses_adjustments_types.name
  and requisition_line_item_losses_adjustments.requisitionlineitemid = v_line;

  v_ret = coalesce(v_ret, 0);

  return v_ret;

END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
------------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_req_adj
(
  IN  v_line  integer
)
RETURNS void AS
$$
DECLARE
 arr_adj  	varchar(1000) ARRAY;
 arr_adj_len integer;


BEGIN

  SELECT INTO arr_adj array_agg(name) FROM losses_adjustments_types;
  arr_adj_len = array_upper(arr_adj, 1);

-- facility loop
 FOR i IN 1 .. arr_adj_len
  LOOP
    BEGIN
	INSERT INTO requisition_line_item_losses_adjustments( requisitionlineitemid, type, quantity)  VALUES (v_line, arr_adj[i],fn_get_random_number(0,50));
    EXCEPTION
        WHEN unique_violation THEN
        null;
            -- don't worry if it already exists
    END;

     end loop;
END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
----------------------------------------------
CREATE OR REPLACE FUNCTION public.fn_tbl_indicator_summary
(
  IN  in_summarycode     varchar,
  IN  in_periodid        integer,
  IN  in_product         varchar,
  IN  in_programid       integer = NULL::integer,
  IN  in_scheduleid      integer = NULL::integer,
  IN  in_facilitytypeid  integer = NULL::integer
)
RETURNS TABLE
(
  out_fac_count       integer,
  out_stockedout      integer,
  out_stockedoutrate  double precision
)
AS
$$
DECLARE
/*
fn_tbl_indicator_summary - This function is intended to be used for .......

Input:
	in_portletcode - two character code indicating what out to return
	in_periodid - processing period id
	in_product - product id
	in_programid - program id
	in_scheduleid - processing schedule id
	in_facilitytypeid - facility type id

 Output: table
	  out_fac_count - facility count
	  out_stockedout - number of facilities stocked out
	  out_stockedoutrate - stockout rate
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/


 -- decalre output variables
 ret_faccount integer;
 ret_stockedout integer;
 ret_rate float;

 -- cursor for stock out calculations
  stockout_cursor CURSOR FOR
  SELECT count(*) faccount, sum(CASE stockinhand WHEN 0 THEN 1 ELSE 0 END) stockedout
  FROM vw_requisition_detail
  WHERE (program_id = in_programid or in_programid is null)
  AND (processing_periods_id = in_periodid or in_periodid is null)
  AND (processing_schedules_id = in_scheduleid or in_scheduleid  is null)
  AND (processing_schedules_id = in_scheduleid or in_scheduleid  is null)
  AND (facility_type_id = in_facilitytypeid or in_facilitytypeid  is null)
  AND (productcode = in_product or in_product is null);

  -- to store stockout data
  rec_stockout RECORD;

BEGIN

-- prepare dataset to return
if in_summarycode = 'SO' then
-- stockout summary
open stockout_cursor;
fetch stockout_cursor into rec_stockout;

-- save data
ret_faccount = rec_stockout.faccount;
ret_stockedout = rec_stockout.stockedout;

-- handle nulls
ret_faccount = coalesce(ret_faccount, 0);
ret_stockedout = coalesce(ret_stockedout, 0);
ret_rate = 0;

-- calculate stockout rate
if ret_faccount > 0 and ret_stockedout > 0 then
 ret_rate = cast(ret_stockedout as float) / cast(ret_faccount as float) * 100.0;

end if;

close stockout_cursor;

else
-- no or invalid summary code provided
ret_faccount = null;
ret_stockedout = null;
ret_rate = null;

end if;


-- return output record
RETURN QUERY SELECT ret_faccount, ret_stockedout, ret_rate;
END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER;
-------------------------------
CREATE OR REPLACE FUNCTION public.fn_get_supplier
(
  IN  v_program          integer,
  IN  v_facility         integer,
  IN  v_supplyingnoteid  integer
)
RETURNS varchar AS
$$
DECLARE
/*
fn_get_supplier() - return supplier name given the program, facility and supplying note

Input:
v_program integer - program id
v_facility integer  - facility id
v_supplyingnoteid integer - supplying node id

 Output:
   integer - supplier id
TODO: Check business rules, use cursor where possible
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/
  -- define supplier cursor
  supplier_cursor      CURSOR FOR
  SELECT supervisory_node_id
  FROM vw_program_facility_supplier
  where program_id = v_program
  and supply_line_id = v_supplyingnoteid;

v_ret character varying;

BEGIN
  v_ret = 0;
   -- save supplier
   open supplier_cursor;
   fetch supplier_cursor into v_ret;
   close supplier_cursor;

   return v_ret;
END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;
------------------------------------------------
CREATE OR REPLACE FUNCTION public."fn_make_sample_data(v_program int4, v_facility varchar, v_perio"
(
  IN  v_program   integer,
  IN  v_facility  varchar,
  IN  v_period    varchar
)
RETURNS void AS
$$
DECLARE
/*
fn_make_sample_data() - This function will generate sample requisition data for the program, facilities and time period supplied.
 Multiple facilities and periods must be provided  in delimited format and delimiting character must be "-".

Input:
  v_program - Program id
  v_facility - Facility id - multiple facilities are delimited by dash(-)
  v_period - period string- multiple periods are delimited by dash(-)

 Output:


TODO: Check business rules, optimize code
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/

/*



*/
    next_req_id integer;
    next_req_line_id integer;

    arr_req_len integer;
    arr_facility_len integer;
    arr_product_len integer;
    arr_period_len integer;

    count_req integer;
    count_facility integer;
    name_req character(18);

    arr_req  integer ARRAY;
    arr_facility  		integer ARRAY;
    arr_program   		integer ARRAY;
    arr_period    		integer ARRAY;

    arr_productcode   		character varying(1000) ARRAY;
    arr_productname   		character varying(1000) ARRAY;
    arr_productprice   		character varying(1000) ARRAY;
    arr_product_display_order  	integer ARRAY;



    arr_product text[];


-- requisition_group record
r_id					requisitions.id%TYPE;
r_facilityid				requisitions.facilityid%TYPE;
r_programid				requisitions.programid%TYPE;
r_periodid				requisitions.periodid%TYPE;
r_status				requisitions.status%TYPE;
r_fullsupplyitemssubmittedcost		requisitions.fullsupplyitemssubmittedcost%TYPE;
r_nonfullsupplyitemssubmittedcost	requisitions.nonfullsupplyitemssubmittedcost%TYPE;
r_supervisorynodeid			requisitions.supervisorynodeid%TYPE;
r_supplyingfacilityid			requisitions.supplyingfacilityid%TYPE;
r_orderbatchid				requisitions.orderbatchid%TYPE;
r_modifiedby				requisitions.modifiedby%TYPE;
r_submitteddate				requisitions.submitteddate%TYPE;
r_modifieddate				requisitions.modifieddate%TYPE;
r_createdby				requisitions.createdby%TYPE;
r_createddate				requisitions.createddate%TYPE;


-- variable for requisition detail (transaction)
v_id				requisition_line_items.id%TYPE;
v_rnrid				requisition_line_items.rnrid%TYPE;
v_productcode			requisition_line_items.productcode%TYPE;
v_product			requisition_line_items.product%TYPE;
v_productdisplayorder		requisition_line_items.productdisplayorder%TYPE;
v_productcategory 		requisition_line_items.productcategory %TYPE;
v_productcategorydisplayorder	requisition_line_items.productcategorydisplayorder%TYPE;
v_dispensingunit		requisition_line_items.dispensingunit%TYPE;
v_beginningbalance		requisition_line_items.beginningbalance%TYPE;
v_quantityreceived		requisition_line_items.quantityreceived%TYPE;
v_quantitydispensed		requisition_line_items.quantitydispensed%TYPE;
v_stockinhand			requisition_line_items.stockinhand%TYPE;
v_quantityrequested		requisition_line_items.quantityrequested%TYPE;
v_reasonforrequestedquantity	requisition_line_items.reasonforrequestedquantity%TYPE;
v_calculatedorderquantity	requisition_line_items.calculatedorderquantity%TYPE;
v_quantityapproved		requisition_line_items.quantityapproved%TYPE;
v_totallossesandadjustments	requisition_line_items.totallossesandadjustments%TYPE;
v_newpatientcount		requisition_line_items.newpatientcount%TYPE;
v_stockoutdays			requisition_line_items.stockoutdays%TYPE;
v_normalizedconsumption		requisition_line_items.normalizedconsumption%TYPE;
v_amc				requisition_line_items.amc%TYPE;
v_maxmonthsofstock		requisition_line_items.maxmonthsofstock%TYPE;
v_maxstockquantity		requisition_line_items.maxstockquantity%TYPE;
v_packstoship			requisition_line_items.packstoship%TYPE;
v_price				requisition_line_items.price%TYPE;
v_remarks			requisition_line_items.remarks%TYPE;
v_dosespermonth			requisition_line_items.dosespermonth%TYPE;
v_dosesperdispensingunit	requisition_line_items.dosesperdispensingunit%TYPE;
v_packsize			requisition_line_items.packsize%TYPE;
v_roundtozero			requisition_line_items.roundtozero%TYPE;
v_packroundingthreshold		requisition_line_items.packroundingthreshold%TYPE;
v_fullsupply			requisition_line_items.fullsupply%TYPE;
v_previousstockinhandavailable	requisition_line_items.previousstockinhandavailable%TYPE;
v_modifiedby			requisition_line_items.modifiedby%TYPE;
v_modifieddate			requisition_line_items.modifieddate%TYPE;
v_createdby			requisition_line_items.createdby%TYPE;
v_createddate			requisition_line_items.createddate%TYPE;

v_numberofmonths integer;

arr_product_packsize 			integer ARRAY;
arr_product_packroundingthreshold	integer ARRAY;
arr_product_dosesperdispensingunit 	integer ARRAY;
arr_product_roundtozero 		boolean ARRAY;
arr_product_fullsupply 			boolean ARRAY;



BEGIN

    -- first delete delete sample records

    DELETE FROM requisition_line_item_losses_adjustments WHERE requisitionlineitemid IN ( SELECT id FROM requisition_line_items  WHERE modifiedby = 1001);
    DELETE FROM requisition_line_items  WHERE modifiedby = 1001;
    DELETE FROM requisitions  WHERE createdby = 1001;

  -- explode facility and period parameter and find length of each array
  arr_facility 		=  String_To_Array(v_facility,'-'); --   '{4303,4304,4305}';
  arr_facility_len 	= array_upper(arr_facility, 1);
  arr_period  		= String_To_Array(v_period,'-'); --'{1,2,3,4,5,6}';
  arr_period_len 	= array_upper(arr_period, 1);
  arr_req_len 		= array_upper(arr_req, 1);


   BEGIN
   -- this block create a special user if does not exist. If already exists, ignore
        BEGIN
            ALTER TABLE public.requisition_line_item_losses_adjustments DROP CONSTRAINT requisition_line_item_losses_adjustm_requisitionlineitemid_fkey;
        EXCEPTION
            WHEN OTHERS THEN NULL;
        END;
    END;


-- this function creates "AUTHORIZED" dataset
count_req =0;
name_req = 'AUTHORIZED';

-- facility loop
 FOR i IN 1 .. arr_facility_len
  LOOP

SELECT array_agg(products.code), array_agg(primaryname), array_agg(displayorder),
array_agg(packsize), array_agg(packroundingthreshold),array_agg(dosesperdispensingunit),
array_agg(roundtozero),array_agg(fullsupply)

INTO
 arr_productcode, arr_productname, arr_product_display_order,
 arr_product_packsize, arr_product_packroundingthreshold, arr_product_dosesperdispensingunit,
 arr_product_roundtozero,arr_product_fullsupply

FROM
  public.products,
  public.program_products,
  public.programs_supported,
  public.programs
WHERE
  products.id = program_products.id AND
  programs.id = program_products.programid AND
  programs.id = programs_supported.programid
  and programs.id = v_program
  and facilityid = arr_facility[i];

 arr_product_len = array_upper(arr_productcode, 1);

-- loop through each period
FOR t IN 1 .. arr_period_len
  LOOP

    select max(id) into next_req_id from requisitions;
    next_req_id = next_req_id + 1;
    SELECT numberofmonths into v_numberofmonths FROM processing_periods where id =  arr_period[t];
    v_numberofmonths = coalesce(v_numberofmonths, 1);

    BEGIN

-- create a requistion record per facility and period
r_id  = next_req_id;
r_facilityid = arr_facility[i];
r_programid = 1;
r_periodid = arr_period[t];
r_status = name_req;
r_fullsupplyitemssubmittedcost = 0;
r_nonfullsupplyitemssubmittedcost = 0;
r_supervisorynodeid = fn_get_supervisorynote_id(arr_facility[i]);
r_supplyingfacilityid = null;
r_orderbatchid = null;
r_modifiedby = 1001;
r_submitteddate = CURRENT_DATE;
r_modifieddate = CURRENT_DATE;
r_createdby = 1001;
r_createddate = CURRENT_DATE;


            INSERT INTO requisitions(
	                id, facilityid, programid, periodid, status, fullsupplyitemssubmittedcost,
	                nonfullsupplyitemssubmittedcost, supervisorynodeid, supplyingfacilityid,
	                orderbatchid, modifiedby, submitteddate, modifieddate, createdby,
	                createddate)
	        VALUES (r_id, r_facilityid, r_programid, r_periodid, r_status, r_fullsupplyitemssubmittedcost,
			r_nonfullsupplyitemssubmittedcost, r_supervisorynodeid, r_supplyingfacilityid,
			r_orderbatchid, r_modifiedby, r_submitteddate, r_modifieddate, r_createdby, r_createddate);


-- next create transcation records
-- loop through product in the program. Only data for approved programs and products is generated
FOR p IN 1 .. arr_product_len
  LOOP

-- new line item id
select max(id) into next_req_line_id from requisition_line_items;
next_req_line_id =next_req_line_id + 1;

v_id = next_req_line_id;
v_rnrid = r_id;

-- call a function to generate adjustment data
PERFORM fn_req_adj(v_id);

-- save transaction detail, calculate where needed, apply business rule when applicable
v_productcode = arr_productcode[p];
v_product = arr_productname[p];
v_productdisplayorder = arr_product_display_order[i];
v_productcategory = fn_get_product_category(arr_productcode[p]);
v_productcategorydisplayorder = 7;
v_dispensingunit = fn_get_product_unit(arr_productcode[p]);
v_beginningbalance = fn_get_open_balance(1,arr_facility[i],arr_productcode[p],r_periodid);
v_beginningbalance = coalesce(v_beginningbalance, 0);
v_quantityreceived = fn_get_random_number(0,100);
v_quantitydispensed = fn_get_random_number(0,v_beginningbalance+v_quantityreceived);
-- do not make quantity disppensed more than quantity recieved + open balance
if (v_beginningbalance + v_quantityreceived - v_quantitydispensed) < 0 then
 v_quantitydispensed = v_quantityreceived;
end if;
-- get total adjustments
v_totallossesandadjustments = fn_get_total_adj(v_id);
-- calculate stock on hand
v_stockinhand = v_beginningbalance + v_quantityreceived - v_totallossesandadjustments - v_quantitydispensed;
if v_stockinhand < 0 then
 v_stockinhand = 0;
end if;

-- calculate amc
v_amc = fn_get_amc (v_program, arr_facility[i], arr_productcode[p], r_periodid,v_quantitydispensed);   --v_quantitydispensed;
--max months of stock
v_maxmonthsofstock = fn_get_max_mos(v_program,arr_facility[i],arr_productcode[p]);
v_maxmonthsofstock = coalesce(v_maxmonthsofstock, 1);


v_reasonforrequestedquantity = 'tst';

if v_amc = 0 then
v_calculatedorderquantity = 0;
else
v_calculatedorderquantity = v_maxmonthsofstock *  cast(v_amc as int)  -  v_stockinhand;
end if;

v_calculatedorderquantity = coalesce(v_calculatedorderquantity, 0);

if v_calculatedorderquantity < 0 then
 v_calculatedorderquantity = 0;
end if;

v_quantityrequested = v_calculatedorderquantity;

if v_calculatedorderquantity > 0 then
v_quantityapproved = fn_get_random_number(0,v_calculatedorderquantity);
else
 v_quantityapproved = null;
end if;



v_newpatientcount = fn_get_random_number(0,100);
v_stockoutdays = fn_get_random_number(0,30);
-- not sure which database field it should come from
v_dosespermonth = 5;
-- adjusted for stock out day and new patients
v_normalizedconsumption = v_quantitydispensed * (v_numberofmonths * 30)/((v_numberofmonths*30) - v_stockoutdays) + v_newpatientcount * v_dosespermonth;

v_maxstockquantity = round(v_amc * v_maxmonthsofstock);

v_packstoship = null;
if v_quantityapproved > 0 then
  v_packstoship =  fn_get_packtoship(arr_productcode[p],v_quantityapproved);
end if;

v_packsize = arr_product_packsize[p];
v_price = 7000;
v_remarks = null;
v_dosesperdispensingunit = arr_product_dosesperdispensingunit[p];
v_roundtozero = 'true';
v_packroundingthreshold = arr_product_packroundingthreshold[p];
v_fullsupply = arr_product_fullsupply[p];
v_previousstockinhandavailable = 'true';
v_modifiedby = 1001;
v_modifieddate = CURRENT_DATE;
v_createdby = 1001;
v_createddate = CURRENT_DATE;


INSERT INTO requisition_line_items(
            id, rnrid, productcode, product, productdisplayorder, productcategory,
            productcategorydisplayorder, dispensingunit, beginningbalance,
            quantityreceived, quantitydispensed, stockinhand, quantityrequested,
            reasonforrequestedquantity, calculatedorderquantity, quantityapproved,
            totallossesandadjustments, newpatientcount, stockoutdays, normalizedconsumption,
            amc, maxmonthsofstock, maxstockquantity, packstoship, price,
            remarks, dosespermonth, dosesperdispensingunit, packsize, roundtozero,
            packroundingthreshold, fullsupply, previousstockinhandavailable,
            modifiedby, modifieddate, createdby, createddate)
VALUES (v_id,
v_rnrid,
v_productcode,
v_product,
v_productdisplayorder,
v_productcategory,
v_productcategorydisplayorder,
v_dispensingunit,
v_beginningbalance,
v_quantityreceived,
v_quantitydispensed,
v_stockinhand,
v_quantityrequested,
v_reasonforrequestedquantity,
v_calculatedorderquantity,
v_quantityapproved,
v_totallossesandadjustments,
v_newpatientcount,
v_stockoutdays,
v_normalizedconsumption,
v_amc,
v_maxmonthsofstock,
v_maxstockquantity,
v_packstoship,
v_price,
v_remarks,
v_dosespermonth,
v_dosesperdispensingunit,
v_packsize,
v_roundtozero,
v_packroundingthreshold,
v_fullsupply,
v_previousstockinhandavailable,
v_modifiedby,
v_modifieddate,
v_createdby,
v_createddate);


end loop;


    EXCEPTION
        WHEN unique_violation THEN
        null;
            -- don't worry if it already exists
    END;

     end loop;
    end loop;
-- delete special user we created in the beginning.
   BEGIN
        BEGIN
            ALTER TABLE public.requisition_line_item_losses_adjustments ADD CONSTRAINT requisition_line_item_losses_adjustm_requisitionlineitemid_fkey FOREIGN KEY (requisitionlineitemid) REFERENCES requisition_line_items(id);
        EXCEPTION
            WHEN OTHERS THEN NULL;
        END;
    END;



END;
$$
LANGUAGE 'plpgsql'
VOLATILE
CALLED ON NULL INPUT
SECURITY INVOKER
COST 100;