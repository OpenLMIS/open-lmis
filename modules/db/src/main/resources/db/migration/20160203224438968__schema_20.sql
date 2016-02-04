--
-- PostgreSQL database dump
--

--
-- Name: atomfeed; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA atomfeed;


ALTER SCHEMA atomfeed OWNER TO postgres;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: tablefunc; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS tablefunc WITH SCHEMA public;


--
-- Name: EXTENSION tablefunc; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION tablefunc IS 'functions that manipulate whole tables, including crosstab';


SET search_path = public, pg_catalog;

--
-- Name: geozonetree_names_type; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE geozonetree_names_type AS (
	hierarchy character varying[],
	leafid integer
);


ALTER TYPE geozonetree_names_type OWNER TO postgres;

--
-- Name: stockmovementtype; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE stockmovementtype AS ENUM (
    'Facility Visit',
    'Order',
    'Inventory Transfer'
);


ALTER TYPE stockmovementtype OWNER TO postgres;

--
-- Name: fn_delete_facility_program_product_isa(integer, integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_delete_facility_program_product_isa(program_product_id integer, facility_id integer, delete_facility_pro_prod_mapping boolean) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE
  isa_coefficient_id integer;
BEGIN
  SELECT INTO isa_coefficient_id ic.id
  FROM facility_program_products fpp JOIN isa_coefficients ic
  ON fpp.isaCoefficientsId = ic.id
  WHERE fpp.facilityid = facility_id AND fpp.programproductid = program_product_id;

  IF delete_facility_pro_prod_mapping THEN
     DELETE FROM facility_program_products
     WHERE facilityid = facility_id AND programproductid = program_product_id;
  ELSE
     UPDATE facility_program_products
     SET isaCoefficientsId = NULL
     WHERE facilityid = facility_id AND programproductid = program_product_id;
  END IF;

  DELETE FROM isa_coefficients
  WHERE id = isa_coefficient_id;
END;
$$;


ALTER FUNCTION public.fn_delete_facility_program_product_isa(program_product_id integer, facility_id integer, delete_facility_pro_prod_mapping boolean) OWNER TO postgres;

--
-- Name: fn_delete_rnr(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_delete_rnr(in_rnrid integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
/*
 2015-04-14 mahmed - handle pod relationships
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
DELETE  FROM  requisition_line_item_losses_adjustments where requisitionlineitemid 
in (select id from requisition_line_items where rnrid in (select id from requisitions where id = v_rnr_id));
select id into li from requisition_line_items where rnrid = in_rnrid limit 1;
if li > 0 then
DELETE FROM requisition_line_items WHERE rnrid= in_rnrid;
end if;
DELETE FROM requisition_status_changes where rnrid = v_rnr_id;
DELETE FROM regimen_line_items where rnrid = v_rnr_id;
DELETE FROM pod_line_items where podid in (select id from pod where orderid = v_rnr_id);
DELETE FROM pod where orderid = v_rnr_id;
DELETE FROM orders where id = v_rnr_id;
DELETE FROM comments where rnrid = v_rnr_id;
DELETE FROM requisitions WHERE id= in_rnrid;

end if;
RETURN msg;
EXCEPTION WHEN OTHERS THEN
RETURN 'Error in deleting requisition id ' || in_rnrid ||'( '|| SQLERRM || ')';
END;
$$;


ALTER FUNCTION public.fn_delete_rnr(in_rnrid integer) OWNER TO postgres;

--
-- Name: fn_dw_scheduler(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_dw_scheduler() RETURNS character varying
    LANGUAGE plpgsql
    AS $$

DECLARE msg character varying(2000);

BEGIN

msg := 'Procedure completed successfully.';

delete from alert_summary;
select fn_populate_alert_facility_stockedout();
select fn_populate_alert_requisition_approved();
select fn_populate_alert_requisition_pending();
select fn_populate_alert_requisition_rejected();
select fn_populate_alert_requisition_emergency();

RETURN msg;

EXCEPTION WHEN OTHERS THEN
return SQLERRM;

END;

$$;


ALTER FUNCTION public.fn_dw_scheduler() OWNER TO postgres;

--
-- Name: fn_get_dashboard_reporting_summary_data(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_dashboard_reporting_summary_data(in_programid integer, in_periodid integer, in_userid integer DEFAULT 0, in_geographiczoneid integer DEFAULT 0) RETURNS TABLE(total integer, expected integer, ever integer, period integer, late integer)
    LANGUAGE plpgsql
    AS $$
DECLARE
-- return values
t_geographiczone_id integer;
qry text;
BEGIN

t_geographiczone_id =  in_geographiczoneid;
qry = '';

if t_geographiczone_id = 0 THEN
t_geographiczone_id = (select id from geographic_zones where COALESCE(parentid,0) = 0);
t_geographiczone_id = COALESCE(t_geographiczone_id,0);
end if;




qry = '
select sum(c.total)::int total, sum(c.expected)::int expected, sum(c.ever)::int ever, sum(c.period)::int period, sum(c.late)::int late  from (
select id, a.total, a.expected, a.ever, a.period, a.late from (
select
     gzz.id,
     gzz.name,
     COALESCE(expected.count,0) expected,
     COALESCE(total.count,0) total,
     COALESCE(ever.count,0) as ever,
     COALESCE(period.count,0) as period,
     COALESCE(late.count,0) late
     from
     geographic_zones gzz
     left join
     geographic_zone_geojson gjson on
     gzz.id = gjson.zoneId
     left join
     (select geographicZoneId, count(*) from facilities
     join programs_supported ps on ps.facilityId = facilities.id
     join geographic_zones gz on gz.id = facilities.geographicZoneId
     join requisition_group_members rgm on rgm.facilityId = facilities.id
     join requisition_group_program_schedules rgps on rgps.requisitionGroupId = rgm.requisitionGroupId
     and rgps.programId = ps.programId

     join processing_periods pp on pp.scheduleId = rgps.scheduleId and pp.id = '||in_periodid ||'
     where gz.levelId = (select max(id) from geographic_levels) and ps.programId = '||in_programid ||'
     group by geographicZoneId
     ) expected
     on gzz.id = expected.geographicZoneId
     left join
     (
			select geographicZoneId, count(*) from facilities
				join programs_supported ps on ps.facilityId = facilities.id
				join geographic_zones gz on gz.id = facilities.geographicZoneId
     where  ps.programId = '||in_programid ||' and facilities.id in
				(select facilityId from requisitions r
				join processing_periods pp on pp.id = r.periodid
				where periodId = '||in_periodid ||'
				and programId = '||in_programid ||'
				and status not in (''INITIATED'', ''SUBMITTED'', ''SKIPPED'') and emergency = false
				and COALESCE(date_part(''day''::text, r.createddate - pp.enddate::date::timestamp), 0::double precision)
                  > COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE configuration_settings.key::text = ''MSD_ZONE_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::double precision
			)  group by geographicZoneId
     ) late on gzz.id = late.geographicZoneId

     left join
     (select geographicZoneId, count(*) from facilities
     join geographic_zones gz on gz.id = facilities.geographicZoneId
     where gz.levelId = (select max(id) from geographic_levels)
     group by geographicZoneId
     ) total
     on gzz.id = total.geographicZoneId
     left join
     (select geographicZoneId, count(*) from facilities
     join programs_supported ps on ps.facilityId = facilities.id
     join geographic_zones gz on gz.id = facilities.geographicZoneId
     where ps.programId = '||in_programid ||'  and facilities.id in
    (select facilityId from requisitions where programId = '||in_programid ||'  )
    group by geographicZoneId
     ) ever
     on gzz.id = ever.geographicZoneId
     left join
     (select geographicZoneId, count(*) from facilities
     join programs_supported ps on ps.facilityId = facilities.id
     join geographic_zones gz on gz.id = facilities.geographicZoneId
     where  ps.programId = '||in_programid ||'  and facilities.id in
     (select facilityId from requisitions where periodId = '||in_periodid ||'  and programId = '||in_programid ||'
     and status not in (''INITIATED'', ''SUBMITTED'', ''SKIPPED'') and emergency = false )
     group by geographicZoneId
     ) period
     on gzz.id = period.geographicZoneId order by gzz.name ) a ';

     if in_userid > 0 then
      qry = qry || ' join ( select distinct geographiczoneid from vw_user_geographic_zones
        where userid = '||in_userid ||'  and programid = '||in_programid ||' ) b
        on b.geographiczoneid = a.id ';
     end if;

     qry = qry || ' join vw_districts vd on vd.district_id = a.id
     where (vd.district_id = '||t_geographiczone_id||' or vd.region_id = '||t_geographiczone_id||' or vd.zone_id = '||t_geographiczone_id||' or vd.parent = '||t_geographiczone_id ||' )) c';

RETURN QUERY EXECUTE qry;
END
$$;


ALTER FUNCTION public.fn_get_dashboard_reporting_summary_data(in_programid integer, in_periodid integer, in_userid integer, in_geographiczoneid integer) OWNER TO postgres;

--
-- Name: fn_get_elmis_stock_status_data(integer, integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_elmis_stock_status_data(in_programid integer, in_geographiczoneid integer, in_periodid integer, in_productid character varying) RETURNS TABLE(productid integer, productname text, periodid integer, periodname text, periodyear integer, quantityonhand integer, quantityconsumed integer, amc integer)
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	stockStatusQuery VARCHAR ; 
	finalQuery VARCHAR ; 
	rowSS RECORD ; 
	v_scheduleid INTEGER ; 
	v_check_id INTEGER ; 
	rec RECORD ;
BEGIN
	SELECT scheduleid INTO v_scheduleid FROM processing_periods
	WHERE  ID = in_periodid ; 
  v_scheduleid = COALESCE (v_scheduleid, 0) ;
	
	EXECUTE 'CREATE TEMP TABLE _stock_status (
		productid integer,
		productname text,
		periodid integer,
		periodname text,
		periodyear integer,
		quantityonhand integer,
		quantityconsumed integer,
		amc integer
		) ON COMMIT DROP' ;

 stockStatusQuery := 'SELECT
		product_id productid,
		product_primaryname productname,
		period_id periodid,
		period_name periodname,
		EXTRACT (
		''year''
		FROM
		period_start_date
		) periodyear,
		SUM (stockinhand) quantityonhand,
		SUM (quantitydispensed) quantityconsumed,
		SUM (amc) amc
		FROM
		vw_requisition_detail_dw
		WHERE
		program_id = ' || in_programid || '
		AND (geographic_zone_id = ' || in_geographiczoneid || ' OR ' || in_geographiczoneid || ' = 0)
		AND product_id IN (' || in_productid || ')
		AND period_id IN (select id from processing_periods where scheduleid = ' || v_scheduleid || ' AND id <= ' || in_periodid || ' order by id desc limit 4)
		GROUP BY
		product_id,
		product_primaryname,
		period_id,
		period_name,
		EXTRACT (
		''year''
		FROM
		period_start_date
		)' ;

FOR rowSS IN EXECUTE stockStatusQuery 
 LOOP EXECUTE 
 'INSERT INTO _stock_status VALUES (' || COALESCE (rowSS.productid, 0) || ',' ||
                                   quote_literal(rowSS.productname :: TEXT) || ',' ||
                                   COALESCE (rowSS.periodid, 0) || ',' ||
                                   quote_literal(rowSS.periodname :: TEXT) || ',' ||
                                   COALESCE (rowSS.periodyear, 0) || ',' ||
                                   COALESCE (rowSS.quantityonhand, 0) || ',' ||
                                   COALESCE (rowSS.quantityconsumed, 0) || ',' ||
                                   COALESCE (rowSS.amc, 0) || ')' ;
 END LOOP ; 
 

FOR rec IN SELECT DISTINCT
		ss.productid,
		ss.productname,
		s. ID periodid,
		s. NAME periodname,
		EXTRACT ('year' FROM startdate) periodyear
	FROM
		_stock_status ss
	CROSS JOIN (
		SELECT
			*
		FROM
			processing_periods
		WHERE
			scheduleid = v_scheduleid
		AND ID <= in_periodid
		ORDER BY
			ID DESC
		LIMIT 4
	) s
	ORDER BY
		ss.productid,
		s. ID DESC 
		
	LOOP 
	SELECT
			T .productid INTO v_check_id
		FROM
			_stock_status T
		WHERE
			T .productid = rec.productid
		AND T .periodid = rec.periodid ; v_check_id = COALESCE (v_check_id, 0) ;

	IF v_check_id = 0 THEN	
	INSERT INTO _stock_status
		VALUES
			(
				rec.productid,
				rec.productname,
				rec.periodid,
				rec.periodname,
				rec.periodyear,
				0,
				0,
				0
			) ;
	END IF ;		
	END LOOP ; 
	finalQuery := 'SELECT productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc FROM  _stock_status order by periodid' ; 
	RETURN QUERY EXECUTE finalQuery ;
		
	END ;
	$$;


ALTER FUNCTION public.fn_get_elmis_stock_status_data(in_programid integer, in_geographiczoneid integer, in_periodid integer, in_productid character varying) OWNER TO postgres;

--
-- Name: fn_get_geozonetree(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_geozonetree(in_facilityid integer) RETURNS TABLE(districtid integer, regionid integer, zoneid integer)
    LANGUAGE plpgsql
    AS $$
DECLARE

finalQuery            VARCHAR;

v_geographiczoneid integer;
v_districtid  integer;
v_regionid    integer;
v_zoneid      integer;

BEGIN

v_geographiczoneid = 0;
v_districtid = 0;
v_regionid = 0;
v_zoneid = 0;

select facilities.geographiczoneid into v_geographiczoneid from facilities where facilities.id = in_facilityid;

if coalesce(v_geographiczoneid, 0)  <> 0 THEN
 --select geographic_zones.id into v_districtid from geographic_zones where geographic_zones.id = v_geographiczoneid;
v_districtid = v_geographiczoneid;
end if;

if coalesce(v_districtid, 0)  <> 0 THEN
 select geographic_zones.parentid into v_regionid from geographic_zones where geographic_zones.id = v_districtid;
end if;

if coalesce(v_regionid, 0)  <> 0 THEN
 select geographic_zones.parentid into v_zoneid from geographic_zones where geographic_zones.id = v_regionid;
end if;


finalQuery := 'SELECT '|| v_districtid ||','||v_regionid||','||v_zoneid;

RETURN QUERY EXECUTE finalQuery;
END;
$$;


ALTER FUNCTION public.fn_get_geozonetree(in_facilityid integer) OWNER TO postgres;

--
-- Name: fn_get_geozonetree_names(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_geozonetree_names() RETURNS SETOF geozonetree_names_type
    LANGUAGE plpgsql
    AS $$
DECLARE
 idVal integer;
 zoneVal varchar[];
 gnt geozonetree_names_type;
BEGIN
	FOR idVal IN (SELECT id FROM geographic_zones WHERE levelid = (SELECT MAX(levelnumber) FROM geographic_levels)) LOOP
	    SELECT * INTO zoneVal FROM fn_get_geozonetree_names(idVal);
	    gnt.hierarchy = zoneVal;
	    gnt.leafid = idVal;
	    RETURN NEXT gnt;
	END LOOP;
END;
$$;


ALTER FUNCTION public.fn_get_geozonetree_names() OWNER TO postgres;

--
-- Name: fn_get_geozonetree_names(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_geozonetree_names(in_geozone_id integer) RETURNS character varying[]
    LANGUAGE plpgsql
    AS $$
BEGIN

RETURN(
	SELECT ARRAY
	(
		WITH RECURSIVE all_geo_zones AS (
		  SELECT  id, parentid, name
		    FROM geographic_zones
		    WHERE id = in_geozone_id
		  UNION
		  SELECT gz.id, gz.parentid, gz.name
		    FROM geographic_zones gz
		    JOIN all_geo_zones agz
		      ON (agz.parentid = gz.id)
		)
		SELECT name FROM all_geo_zones
	) AS geo_zone_hierarchy
);
END;
$$;


ALTER FUNCTION public.fn_get_geozonetree_names(in_geozone_id integer) OWNER TO postgres;

--
-- Name: fn_get_max_mos(integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_max_mos(v_program integer, v_facility integer, v_product character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.fn_get_max_mos(v_program integer, v_facility integer, v_product character varying) OWNER TO postgres;

--
-- Name: fn_get_notification_details(anyelement, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_notification_details(_tbl_name anyelement, id integer) RETURNS SETOF anyelement
    LANGUAGE plpgsql
    AS $$
BEGIN

RETURN QUERY EXECUTE 'SELECT * FROM ' || pg_typeof(_tbl_name) || ' where alertsummaryid = '||id;

END
$$;


ALTER FUNCTION public.fn_get_notification_details(_tbl_name anyelement, id integer) OWNER TO postgres;

--
-- Name: fn_get_notification_details(anyelement, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_notification_details(_tbl_name anyelement, userid integer, programid integer, periodid integer, zoneid integer) RETURNS SETOF anyelement
    LANGUAGE plpgsql
    AS $$
BEGIN

RETURN QUERY EXECUTE 'SELECT * FROM ' || pg_typeof(_tbl_name) ||
 ' where programId = '||programId ||' and periodId= '||periodId||
 'and geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children('||userId||', '||zoneId||'))';

END
$$;


ALTER FUNCTION public.fn_get_notification_details(_tbl_name anyelement, userid integer, programid integer, periodid integer, zoneid integer) OWNER TO postgres;

--
-- Name: fn_get_parent_geographiczone(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_parent_geographiczone(v_geographiczoneid integer, v_level integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE

v_highest_parent_id integer;
v_highest_parent_name geographic_zones.name%TYPE;

v_this_parent_id integer;
v_this_parent_name geographic_zones.name%TYPE;

v_current_parent_id integer;
v_current_parent_name geographic_zones.name%TYPE;

v_parent_geographizone_name geographic_zones.name%TYPE;


BEGIN

select id, name into v_highest_parent_id, v_highest_parent_name from geographic_zones where parentid is null; 
select parentid, name into v_this_parent_id, v_this_parent_name from geographic_zones where id = v_geographiczoneid;


IF (v_geographiczoneid = v_highest_parent_id) THEN
 v_parent_geographizone_name := v_highest_parent_name;
 RETURN v_parent_geographizone_name;
END IF; 


IF v_level = 0 THEN
 v_parent_geographizone_name = v_this_parent_name;
 RETURN v_parent_geographizone_name; 
END IF;


FOR i IN 1..v_level LOOP

select parentid,name into v_this_parent_id, v_parent_geographizone_name from geographic_zones where id = v_this_parent_id;

END LOOP;


v_parent_geographizone_name := coalesce(v_parent_geographizone_name, 'Unknown');

return v_parent_geographizone_name;

END;
$$;


ALTER FUNCTION public.fn_get_parent_geographiczone(v_geographiczoneid integer, v_level integer) OWNER TO postgres;

--
-- Name: fn_get_program_product_id(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_program_product_id(v_program integer, v_product integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE

  v_ret integer;
    
BEGIN
   SELECT id into v_ret FROM program_products where programid = v_program and productid = v_product;
     
 
     return v_ret;       
 
END;
$$;


ALTER FUNCTION public.fn_get_program_product_id(v_program integer, v_product integer) OWNER TO postgres;

--
-- Name: fn_get_reporting_status_by_facilityid_programid_and_periodid(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_reporting_status_by_facilityid_programid_and_periodid(v_facilityid integer, v_programid integer, v_periodid integer) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
v_ret TEXT;
v_reporting_date INTEGER;
v_late_days INTEGER;
v_req_facilityid INTEGER;
BEGIN

select facilityid from requisitions where facilityid = v_facilityid and programid = v_programid and periodid = v_periodid INTO v_req_facilityid;

IF v_req_facilityid IS NULL THEN RETURN 'non_reporting'; END IF;

SELECT value from configuration_settings where key='LATE_REPORTING_DAYS' INTO v_late_days;

SELECT date_part('day', (select createddate from requisitions r where r.programId = v_programid and r.periodId = v_periodid and facilityid = v_facilityid)- 
(select startdate from processing_periods where id = v_periodid))::integer INTO v_reporting_date;

SELECT CASE WHEN 
COALESCE(v_reporting_date,0) > COALESCE(v_late_days,10)
  THEN 'late_reporting' 
  ELSE 'reporting' END INTO v_ret;

return v_ret;
END;
$$;


ALTER FUNCTION public.fn_get_reporting_status_by_facilityid_programid_and_periodid(v_facilityid integer, v_programid integer, v_periodid integer) OWNER TO postgres;

--
-- Name: fn_get_rmnch_stock_status_data(integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_rmnch_stock_status_data(in_geographiczoneid integer, in_periodid integer, in_productid character varying) RETURNS TABLE(productid integer, productname text, periodid integer, periodname text, periodyear integer, quantityonhand integer, quantityconsumed integer, amc integer)
    LANGUAGE plpgsql
    AS $$
DECLARE
stockStatusQuery VARCHAR ;
finalQuery VARCHAR ;
rowSS RECORD ;
v_scheduleid INTEGER ;
v_check_id INTEGER ;
rec RECORD ;
BEGIN
SELECT scheduleid INTO v_scheduleid FROM processing_periods
WHERE  ID = in_periodid ;
v_scheduleid = COALESCE (v_scheduleid, 0) ;

EXECUTE 'CREATE TEMP TABLE _stock_status (
productid integer,
productname text,
periodid integer,
periodname text,
periodyear integer,
quantityonhand integer,
quantityconsumed integer,
amc integer
) ON COMMIT DROP' ;

stockStatusQuery := 
'SELECT  
s.productid,
s.productname,
s.periodid,
s.periodname,
s.reportyear periodyear,
SUM (s.stockonhand) quantityonhand,
SUM (s.issues) quantityconsumed,
SUM (s.amc) amc
 from vw_e2e_stock_status s 
where rmnch =''t''
and (s.geographiczoneid = ' || in_geographiczoneid || ' OR ' || in_geographiczoneid || ' = 0)
AND periodid IN (select id from processing_periods where scheduleid = ' || v_scheduleid || ' AND id <= ' || in_periodid || ' order by id desc limit 4)
and productid in (' || in_productid || ')
group by 
s.productid,
s.productname,
s.periodid,
s.periodname,
s.reportyear';


FOR rowSS IN EXECUTE stockStatusQuery
LOOP EXECUTE
'INSERT INTO _stock_status VALUES (' || COALESCE (rowSS.productid, 0) || ',' ||
quote_literal(rowSS.productname :: TEXT) || ',' ||
COALESCE (rowSS.periodid, 0) || ',' ||
quote_literal(rowSS.periodname :: TEXT) || ',' ||
COALESCE (rowSS.periodyear, 0) || ',' ||
COALESCE (rowSS.quantityonhand, 0) || ',' ||
COALESCE (rowSS.quantityconsumed, 0) || ',' ||
COALESCE (rowSS.amc, 0) || ')' ;
END LOOP ;
FOR rec IN SELECT DISTINCT
ss.productid,
ss.productname,
s. ID periodid,
s. NAME periodname,
EXTRACT ('year' FROM startdate) periodyear
FROM
_stock_status ss
CROSS JOIN (
SELECT
*
FROM
processing_periods
WHERE
scheduleid = v_scheduleid
AND ID <= in_periodid
ORDER BY
ID DESC
LIMIT 4
) s
ORDER BY
ss.productid,
s. ID DESC
LOOP
SELECT
T .productid INTO v_check_id
FROM
_stock_status T
WHERE
T .productid = rec.productid
AND T .periodid = rec.periodid ; v_check_id = COALESCE (v_check_id, 0) ;
IF v_check_id = 0 THEN
INSERT INTO _stock_status
VALUES
(
rec.productid,
rec.productname,
rec.periodid,
rec.periodname,
rec.periodyear,
0,
0,
0
) ;
END IF ;
END LOOP ;
finalQuery := 'SELECT productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc FROM  _stock_status order by periodid' ;
RETURN QUERY EXECUTE finalQuery ;
END ;
$$;


ALTER FUNCTION public.fn_get_rmnch_stock_status_data(in_geographiczoneid integer, in_periodid integer, in_productid character varying) OWNER TO postgres;

--
-- Name: fn_get_stocked_out_notification_details(anyelement, integer, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_stocked_out_notification_details(_tbl_name anyelement, userid integer, programid integer, periodid integer, zoneid integer, productid integer) RETURNS SETOF anyelement
    LANGUAGE plpgsql
    AS $$
BEGIN

RETURN QUERY EXECUTE 'SELECT * FROM ' || pg_typeof(_tbl_name) ||
 ' where programId = '||programId ||' and periodId= '||periodId||' and productId= '||productId||
 'and geographiczoneid in (select geographiczoneid from fn_get_user_geographiczone_children('||userId||', '||zoneId||'))';

END
$$;


ALTER FUNCTION public.fn_get_stocked_out_notification_details(_tbl_name anyelement, userid integer, programid integer, periodid integer, zoneid integer, productid integer) OWNER TO postgres;

--
-- Name: fn_get_supervisorynodeid_by_facilityid(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_supervisorynodeid_by_facilityid(v_facilityid integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
v_ret integer;
BEGIN

SELECT
requisition_groups.supervisorynodeid into v_ret
FROM
requisition_groups
INNER JOIN requisition_group_members ON requisition_groups.id = requisition_group_members.requisitiongroupid
where requisition_group_members.facilityid = v_facilityid LIMIT 1;


return v_ret;
END;
$$;


ALTER FUNCTION public.fn_get_supervisorynodeid_by_facilityid(v_facilityid integer) OWNER TO postgres;

--
-- Name: fn_get_supplying_facility_name(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_supplying_facility_name(v_supervisorynode_id integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE

v_supplying_facility_id integer;
v_supplying_facility_name facilities.name%TYPE;
BEGIN
select supplyingfacilityid into v_supplying_facility_id from supply_lines where supervisorynodeid = v_supervisorynode_id;
select name into v_supplying_facility_name from facilities where id =  v_supplying_facility_id;
v_supplying_facility_name = coalesce(v_supplying_facility_name, 'Unknown');
return v_supplying_facility_name;
END;
$$;


ALTER FUNCTION public.fn_get_supplying_facility_name(v_supervisorynode_id integer) OWNER TO postgres;

--
-- Name: fn_get_timeliness_reporting_dates(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_timeliness_reporting_dates(in_periodid integer) RETURNS TABLE(reportingstartdate date, reportingenddate date, reportinglatestartdate date, reportinglateenddate date)
    LANGUAGE plpgsql
    AS $$
BEGIN

RETURN QUERY EXECUTE 
'SELECT CAST(date_trunc(''month'', enddate::date) + INTERVAL ''1 month'' as date) reportingStartDate, 

 (enddate::date + COALESCE((( SELECT configuration_settings.value FROM configuration_settings
 
 WHERE configuration_settings.key::text = ''MSD_ZONE_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::integer ) reportingEndDate,
 
(CAST(date_trunc(''month'', enddate::date) + INTERVAL ''1 month'' as date) + COALESCE((( SELECT configuration_settings.value FROM configuration_settings

 WHERE configuration_settings.key::text = ''MSD_ZONE_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::integer ) lateReportingStartDate,
 
 (enddate::date + COALESCE((( SELECT configuration_settings.value FROM configuration_settings
 
 WHERE configuration_settings.key::text = ''UNSCHEDULED_REPORTING_CUT_OFF_DATE''::text))::integer, 0)::integer ) lateReportingEndDate FROM processing_periods
  
 WHERE   id = ' || in_periodid || '';
 
END
$$;


ALTER FUNCTION public.fn_get_timeliness_reporting_dates(in_periodid integer) OWNER TO postgres;

--
-- Name: fn_get_user_default_settings(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_user_default_settings(in_programid integer, in_facilityid integer) RETURNS TABLE(programid integer, facilityid integer, scheduleid integer, periodid integer, geographiczoneid integer)
    LANGUAGE plpgsql
    AS $$
DECLARE

_query VARCHAR;
finalQuery            VARCHAR;
rowrec                 RECORD;
BEGIN

_query := 'SELECT
	programid, facilityid, scheduleid, periodid, geographiczoneid
FROM
	vw_expected_facilities
WHERE
	facilityid = ' || in_facilityid || ' 
AND programid = ' || in_programid || ' 
AND periodid IN (
	SELECT
		MAX (periodid) periodid
	FROM
		requisitions
	WHERE
		programid = ' || in_programid || ' 
	AND facilityid = '|| in_facilityid || '
)';

RETURN QUERY EXECUTE _query;
END;
$$;


ALTER FUNCTION public.fn_get_user_default_settings(in_programid integer, in_facilityid integer) OWNER TO postgres;

--
-- Name: fn_get_user_geographiczone_children(integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_user_geographiczone_children(in_userid integer, in_parentid integer) RETURNS TABLE(geographiczoneid integer, levelid integer, parentid integer)
    LANGUAGE plpgsql
    AS $$
DECLARE

finalQuery            VARCHAR;
v_parents character varying;
v_current_parentid integer;
v_parentid integer;
v_result INTEGER = 0;

BEGIN

finalQuery :=
 'WITH  recursive  userGeographicZonesRec AS
          (SELECT *
          FROM geographic_zones 
          WHERE id = '||in_parentid||'
          UNION 
          SELECT gz.* 
          FROM geographic_zones gz 
          JOIN userGeographicZonesRec  ON gz.parentId = userGeographicZonesRec.id )          

          SELECT rec.id,rec.levelid,rec.parentid from userGeographicZonesRec rec
          INNER JOIN vw_user_geographic_zones uz on uz.geographiczoneid = rec.id 
         where userid = '||in_userid;

  RETURN QUERY EXECUTE finalQuery;
END;
$$;


ALTER FUNCTION public.fn_get_user_geographiczone_children(in_userid integer, in_parentid integer) OWNER TO postgres;

--
-- Name: fn_get_vaccine_coverage_denominator(integer, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_vaccine_coverage_denominator(in_program integer, in_facility integer, in_year integer, in_product integer, in_dose integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
v_denominator integer;
v_year integer;
v_target_value integer;
BEGIN

select d.denominatorestimatecategoryid into v_denominator from vaccine_product_doses d 
where programid = in_program 
and productid = in_product 
and doseid = in_dose;
v_denominator = COALESCE(v_denominator,0);

select round(value/12) into v_target_value from facility_demographic_estimates 
 where year = in_year 
 and facilityid = in_facility
 and demographicestimateid = v_denominator;

v_target_value = COALESCE(v_target_value,0);

return v_target_value;
END;
$$;


ALTER FUNCTION public.fn_get_vaccine_coverage_denominator(in_program integer, in_facility integer, in_year integer, in_product integer, in_dose integer) OWNER TO postgres;

--
-- Name: fn_get_vaccine_coverage_district_denominator(integer, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_get_vaccine_coverage_district_denominator(in_program integer, in_district integer, in_year integer, in_product integer, in_dose integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
v_denominator integer;
v_year integer;
v_target_value integer;
BEGIN
-- find the denominator category id for product and dose
select d.denominatorestimatecategoryid into v_denominator from vaccine_product_doses d
where programid = in_program
and productid = in_product
and doseid = in_dose;
v_denominator = COALESCE(v_denominator,0);

-- get target value
select round(value/12) into v_target_value from district_demographic_estimates
where year = in_year
and districtid = in_district
and demographicestimateid = v_denominator;
v_target_value = COALESCE(v_target_value,0);

return v_target_value;
END;
$$;


ALTER FUNCTION public.fn_get_vaccine_coverage_district_denominator(in_program integer, in_district integer, in_year integer, in_product integer, in_dose integer) OWNER TO postgres;

--
-- Name: fn_gettimelinessreportdata(integer, integer, integer, integer, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_gettimelinessreportdata(in_programid integer, in_geographiczoneid integer, in_periodid integer, in_scheduleid integer, in_status character varying, facilityids character varying) RETURNS TABLE(duration date, status text, rnrid integer, facilityname text, facilitytypename text)
    LANGUAGE plpgsql
    AS $$
BEGIN

RETURN QUERY EXECUTE '
SELECT requisition_status_changes.createddate::date duration,requisition_status_changes.status::text,vw_timeliness_report.rnrId::integer,facilityname::text , facilitytypename::text 
FROM vw_timeliness_report
INNER JOIN requisition_status_changes ON vw_timeliness_report.rnrId = requisition_status_changes.rnrid

            WHERE
            requisition_status_changes.status::text <> ALL (ARRAY[''INITIATED''::character varying::text, ''SUBMITTED''::character varying::text, ''SKIPPED''::character varying::text]) AND 
 programId = ' || in_programid || ' and periodId='|| in_periodid ||' AND scheduleId = '|| in_scheduleid ||' and  reportingstatus IN ('''|| in_status ||''') and geographiczoneId = '|| in_geographiczoneid ||'
              AND facilityId IN ('|| facilityIds || ')  
                       GROUP BY requisition_status_changes.createddate,requisition_status_changes.status,vw_timeliness_report.rnrId,facilityname,facilitytypename
                       order by status';

END
$$;


ALTER FUNCTION public.fn_gettimelinessreportdata(in_programid integer, in_geographiczoneid integer, in_periodid integer, in_scheduleid integer, in_status character varying, facilityids character varying) OWNER TO postgres;

--
-- Name: fn_insert_isa(numeric, integer, numeric, numeric, integer, integer, integer, integer, timestamp without time zone, integer, timestamp without time zone, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_insert_isa(whoratio numeric, dosesperyear integer, wastagefactor numeric, bufferpercentage numeric, minimumvalue integer, maximumvalue integer, adjustmentvalue integer, createdby integer, createddate timestamp without time zone, modifiedby integer, modifieddate timestamp without time zone, populationsource integer, program_product_id integer, facility_id integer DEFAULT (-1)) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
  orig_isa_id integer;
  fac_prog_prod_id integer;
  isa_coefficient_id integer;
BEGIN
  INSERT INTO isa_coefficients(whoratio, dosesperyear, wastagefactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue, createdby, createddate, modifiedby, modifieddate, populationsource)
  VALUES(whoratio, dosesperyear, wastagefactor, bufferpercentage, minimumvalue, maximumvalue, adjustmentvalue, createdby, createddate, modifiedby, modifieddate, populationsource)
  RETURNING id INTO isa_coefficient_id;

  IF facility_id < 0 THEN
      UPDATE program_products
      SET isaCoefficientsId = isa_coefficient_id
      WHERE id = program_product_id;
  ELSE

     SELECT id INTO fac_prog_prod_id FROM facility_program_products
     WHERE programproductid = program_product_id
     AND facilityid = facility_id;

     SELECT ic.id INTO orig_isa_id
     FROM isa_coefficients ic JOIN facility_program_products fpp
     ON fpp.isaCoefficientsId = ic.id
     WHERE fpp.id = fac_prog_prod_id;

     DELETE FROM facility_program_products
     WHERE id = fac_prog_prod_id;

     DELETE FROM isa_coefficients WHERE id = orig_isa_id;

     INSERT INTO facility_program_products(facilityId, programProductId, isaCoefficientsId)
     VALUES(facility_id, program_product_id, isa_coefficient_id);

  END IF;

  return isa_coefficient_id;
END;
$$;


ALTER FUNCTION public.fn_insert_isa(whoratio numeric, dosesperyear integer, wastagefactor numeric, bufferpercentage numeric, minimumvalue integer, maximumvalue integer, adjustmentvalue integer, createdby integer, createddate timestamp without time zone, modifiedby integer, modifieddate timestamp without time zone, populationsource integer, program_product_id integer, facility_id integer) OWNER TO postgres;

--
-- Name: fn_populate_alert_equipment_nonfunctional(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_populate_alert_equipment_nonfunctional(in_flag integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.fn_populate_alert_equipment_nonfunctional(in_flag integer) OWNER TO postgres;

--
-- Name: fn_populate_alert_facility_stockedout(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_populate_alert_facility_stockedout(in_flag integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
rec_summary RECORD ;
rec_detail RECORD ;
msg CHARACTER VARYING (2000) ;
v_summaryid integer;
v_this_run_date date;
v_last_run_date date;
BEGIN
msg := 'Success!!! fn_populate_alert_facility_stockedout.' ;
v_this_run_date = now()::date;
if in_flag = 1 then
v_last_run_date = (select modifieddate::date from dw_orders order by 1 desc limit 1);
else
v_last_run_date = (select now()::date - interval '12 month'::interval)::date;
end if;
delete from alert_summary where alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT'
and  COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

delete from alert_facility_stockedout where COALESCE(modifieddate,v_last_run_date) >= v_last_run_date;

FOR rec_summary IN
select d.programid, d.periodid, d.geographiczoneid geoid, d.productid, d.productprimaryname product,
count(facilityid) facility_count
from dw_orders d
where stocking = 'S'
and tracer ='t'
and modifieddate::date >= v_last_run_date
GROUP BY
1, 2, 3, 4, 5
LOOP
INSERT INTO alert_summary(
statics_value, description, geographiczoneid, alerttypeid,programid, periodid, productid, modifieddate)
VALUES (rec_summary.facility_count,'Facilities stocked out of ' ||rec_summary.product, rec_summary.geoid, 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT', rec_summary.programid, rec_summary.periodid, rec_summary.productid,v_this_run_date);
end loop;
FOR rec_detail IN
select d.programid, d.periodid, d.geographiczoneid geoid,
d.geographiczonename, d.facilityid facility_id,
d.facilityname facility,
d.productid, d.productprimaryname product,
d.stockoutdays,
d.amc
from dw_orders d
where stocking = 'S'
and tracer = 't'
and modifieddate::date >= v_last_run_date
LOOP --fetch the table row inside the loop
select id into v_summaryid from alert_summary
where programid = rec_detail.programid
and periodid = rec_detail.periodid
and geographiczoneid = rec_detail.geoid
and productid = rec_detail.productid
and alerttypeid = 'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT';
INSERT INTO alert_facility_stockedout(
alertsummaryid, programid, periodid, geographiczoneid, geographiczonename, facilityid, facilityname, productid, productname, stockoutdays, amc, modifieddate)
VALUES (v_summaryid, rec_detail.programid, rec_detail.periodid, rec_detail.geoid, rec_detail.geographiczonename, rec_detail.facility_id, rec_detail.facility, rec_detail.productid, rec_detail.product, rec_detail.stockoutdays, rec_detail.amc,v_this_run_date);
END LOOP;
RETURN msg ;
EXCEPTION
WHEN OTHERS THEN
RETURN 'Error!!! fn_populate_alert_facility_stockedout. ' || SQLERRM ;
END ;
$$;


ALTER FUNCTION public.fn_populate_alert_facility_stockedout(in_flag integer) OWNER TO postgres;

--
-- Name: fn_populate_dw_orders(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_populate_dw_orders(in_flag integer) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE
rec RECORD ;
rec2 RECORD ;
li INTEGER ;
msg CHARACTER VARYING (2000) ;
v_programid INTEGER ;
v_programcode CHARACTER VARYING (100);
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
v_quantityapprovedprev INTEGER ;
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
v_previousstockinhand integer;
v_emergency boolean;
v_supervisorynodeid INTEGER;
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
v_late_days integer;
previous_periodid integer;
v_totalsuppliedinpast integer;
v_mossuppliedinpast_denominator integer;
v_previousrnrid integer;
v_lastupdatedate TIMESTAMP ;
v_tracer boolean;
v_skipped boolean;
v_stockoutdays integer;
v_last_run TIMESTAMP;
v_this_run TIMESTAMP; -- timestamp of this function run
v_rnr_modifieddate TIMESTAMP;
v_updated boolean;
v_periodstartdate date;
v_periodenddate date;
v_openingbalance integer;
v_dispensed integer;
v_adjustment integer;
v_quantityordered integer;
v_quantityshipped INTEGER ;
v_dateordered date;
v_dateshipped date;
v_expired integer;

v_sohprev INTEGER;
v_sohprev2 INTEGER;
v_sohprev3 INTEGER;

v_dispensedprev  INTEGER;
v_dispensedprev2  INTEGER;
v_dispensedprev3  INTEGER;

v_amcprev  INTEGER;
v_amcprev2  INTEGER;
v_amcprev3  INTEGER;

v_mosprev numeric(6,1);
v_mosprev2 numeric(6,1);
v_mosprev3 numeric(6,1);

p1 RECORD;
p2 RECORD;
p3 RECORD;


BEGIN
msg := 'Data saved successfully' ;
v_this_run = now();
v_updated = 'f'; --
if in_flag = 1 then
select a.last_run into v_last_run from (select modifieddate last_run
from dw_orders order by 1 desc limit 1) a;
if length(COALESCE(v_last_run::text,''))= 0 then
v_last_run = (now() - interval '12 month'::interval);
end if;
else
v_last_run = (now() - interval '12 month'::interval);
end if;
msg ='before delete';
DELETE FROM dw_orders where modifieddate::date < (now()::date - interval '12 month'::interval)::date;
if in_flag = 1 then
DELETE FROM dw_orders where
rnrid in (
SELECT
vw_requisition_detail_dw.requisition_id
FROM
vw_requisition_detail_dw
where skipped = 'f'
and product_tracer = 't'
and modifieddate > v_last_run
and requisition_status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')
and (COALESCE(stockinhand,0)
+ COALESCE(beginningbalance,0)
+ COALESCE(quantitydispensed,0)
+ COALESCE(quantityreceived,0)
+ COALESCE(stockoutdays,0)
+ abs(COALESCE(totallossesandadjustments,0))) >  0
);
ELSE
delete from dw_orders;
end if;
msg ='before requisition detail data';
FOR rec IN
SELECT
*
FROM
vw_requisition_detail_dw
where skipped = 'f'
and product_tracer = 't'
and modifieddate > v_last_run
and requisition_status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')
and (COALESCE(stockinhand,0)
+ COALESCE(beginningbalance,0)
+ COALESCE(quantitydispensed,0)
+ COALESCE(quantityreceived,0)
+ COALESCE(stockoutdays,0)
+ abs(COALESCE(totallossesandadjustments,0))) >  0
LOOP --fetch the table row inside the loop
v_programid = rec.program_id ;
v_programcode = rec.program_code;
v_geographiczoneid = rec.geographic_zone_id;
v_facilityid = rec.facility_id ;
v_facilitycode = rec.facility_code ;
v_periodid = rec.period_id ;
v_rnrid = rec.requisition_id ;
v_status = rec.requisition_status ;
v_productid = rec.product_id ;
v_productcode = rec.product_code ;
v_quantityrequested = rec.quantityrequested ;
v_quantityapproved = rec.quantityapproved ;
v_quantityreceived = rec.quantityreceived ; -- will set the date later
v_createddate = NULL ;
v_approveddate = NULL ;
v_shippeddate = NULL ;
v_receiveddate = NULL ;
v_programname = rec.program_name ;
v_facilityname = rec.facility_name ;
v_productprimaryname = rec.product_primaryname ;
v_productfullname = rec.product_fullname;
v_processingperiodname = rec.period_name ;
v_soh = rec.stockinhand;
v_amc = rec.amc;
v_mos = CASE WHEN v_amc > 0 THEN v_soh::numeric / v_amc::numeric ELSE 0 END;
v_emergency = rec.requisition_emergency;
v_supervisorynodeid = NULL;
v_requisitiongroupid = NULL;
v_requisitiongroupname = NULL;
v_facilitytypeid = rec.facility_type_id;
v_facilitytypename = rec.facility_type_name;
v_scheduleid = rec.processing_schedule_id;
v_schedulename = rec.processing_schedule_name;
v_productcategoryid = rec.product_category_id;
v_productcategoryname = rec.product_category_name;
v_productgroupid = rec.product_group_id;
v_productgroupname = rec.product_group_id;
v_stockedoutinpast = 'N';
v_suppliedinpast = 'N';
v_mossuppliedinpast = 1;
v_geographiczonename =  rec.geographic_zone_name;
v_previousstockinhand =  rec.previousstockinhand;
v_tracer =  rec.product_tracer;
v_skipped =  rec.skipped;
v_stockoutdays =  rec.stockoutdays;
v_totalsuppliedinpast = 0;
v_rnr_modifieddate = rec.modifieddate;
v_periodstartdate = rec.period_start_date::date;
v_periodenddate = rec.period_end_date::date;
v_openingbalance = rec.beginningbalance;
v_dispensed = rec.quantitydispensed;
v_adjustment = rec.totallossesandadjustments;
v_quantityordered = rec.quantityordered;
v_quantityshipped = rec.quantityshipped;
v_dateordered = rec.ordereddate::date;
v_dateshipped = rec.shippeddate::date;

v_sohprev =  null;
v_dispensedprev = null;
v_amcprev =  null;
v_mosprev =  null;

v_sohprev2 =  null;
v_dispensedprev2 = null;
v_amcprev2 =  null;
v_mosprev2 =  null;

v_sohprev3 =  null;
v_dispensedprev3 = null;
v_amcprev3 =  null;
v_mosprev3 =  null;



if v_previousstockinhand = 0 then
v_stockedoutinpast = 'Y';
end if;

msg ='before last two';

select * from fn_previous_rnr_detail(v_programid, v_periodid,v_facilityid,v_productcode) into p1;
v_previousrnrid = COALESCE(p1.rnrid,0);

v_sohprev =  p1.stockinhand;
v_dispensedprev = p1.quantitydispensed;
v_amcprev = p1.amc;

if (v_amcprev > 0 and v_sohprev > 0) then
v_mosprev = v_sohprev::numeric / v_amcprev::numeric;
elseif v_sohprev = 0 THEN
v_mosprev = 0;
end if;

-- last 2nd period
select periodid into previous_periodid from requisitions where requisitions.id = p1.rnrid;
previous_periodid = COALESCE(previous_periodid,0);
select * from fn_previous_rnr_detail(v_programid, previous_periodid,v_facilityid,v_productcode) into p2;

v_mossuppliedinpast_denominator = 0;
v_sohprev2 =  p2.stockinhand;
v_dispensedprev2 = p2.quantitydispensed;
v_amcprev2 = p2.amc;

-- calcuate past 2 mos
if (v_amcprev2 > 0 and v_sohprev2 > 0) then
v_mosprev2 = v_sohprev2::numeric / v_amcprev2::numeric;
elseif v_sohprev2 = 0 THEN
v_mosprev2 = 0;
end if;


if COALESCE(v_soh,0) > 0 then
v_mossuppliedinpast_denominator  = v_amc;
elsif COALESCE(p1.stockinhand,0) > 0 then
v_mossuppliedinpast_denominator  = p1.amc;
elsif COALESCE(p2.stockinhand,0) > 0 then
v_mossuppliedinpast_denominator  = p2.amc;
end if;
if p1.stockinhand = 0 and p2.stockinhand = 0 then
v_stockedoutinpast = 'Y';
end if;
if p1.quantityreceived > 0 or p2.quantityreceived > 0 then
v_suppliedinpast = 'Y';
v_totalsuppliedinpast = COALESCE(p1.quantityreceived,0) + COALESCE(p2.quantityreceived,0);
end if;

v_mossuppliedinpast = 0;
if v_mossuppliedinpast_denominator > 0 then
v_mossuppliedinpast = v_totalsuppliedinpast::numeric /  v_mossuppliedinpast_denominator;
end if;
v_quantityapprovedprev = COALESCE(p1.quantityapproved,0);



------------------------------- last 3rd period
select periodid into previous_periodid from requisitions where requisitions.id = p2.rnrid;
previous_periodid = COALESCE(previous_periodid,0);
select * from fn_previous_rnr_detail(v_programid, previous_periodid,v_facilityid,v_productcode) into p3;

v_sohprev3 =  p3.stockinhand;
v_dispensedprev3 = p3.quantitydispensed;
v_amcprev3 = p3.amc;

if (v_amcprev3 > 0 and v_sohprev3 > 0) then
v_mosprev3 = v_sohprev3::numeric / v_amcprev3::numeric;
elseif v_sohprev3 = 0 THEN
v_mosprev3 = 0;
end if;



IF rec.stockinhand = 0 THEN
v_stocking = 'S' ;
ELSEIF rec.stockinhand > 0
AND rec.stockinhand <= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_minmonthsofstock,rec.facility_type_nominaleop),0)
) THEN
v_stocking = 'U' ;
ELSEIF rec.stockinhand > 0
AND rec.stockinhand >= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_maxmonthsofstock,rec.facility_type_nominalmaxmonth),0)
) THEN
v_stocking = 'O' ;
ELSEIF rec.stockinhand <= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_maxmonthsofstock,rec.facility_type_nominalmaxmonth),0))
AND rec.stockinhand >= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_minmonthsofstock,rec.facility_type_nominaleop),0)) THEN
v_stocking = 'A' ;
ELSE
v_stocking = 'K' ;
END IF ;


-- get expired quantity

SELECT
sum(abs(quantity)) into v_expired
FROM
requisition_line_items i
INNER JOIN requisition_line_item_losses_adjustments j
 ON j.requisitionlineitemid = i.id
where j.type = 'EXPIRED'
and i.rnrid =  v_rnrid and productcode = v_productcode;
-- if no data, assume 0 expired 
v_expired = COALESCE(v_expired,0);



msg ='before insert';
INSERT INTO dw_orders (
programid,
programcode,
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
quantityreceived,
quantityapprovedprev,
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
mossuppliedinpast,
supervisorynodeid,
modifieddate,
tracer,
skipped,
stockoutdays,
rnrmodifieddate,
periodstartdate,
periodenddate,
openingBalance,
dispensed,
adjustment,
quantityordered,
quantityshipped,
dateordered,
dateshipped,

sohprev,
dispensedprev,
amcprev,
mosprev,

sohprev2,
dispensedprev2,
amcprev2,
mosprev2,

sohprev3,
dispensedprev3,
amcprev3,
mosprev3,

quantityExpired

)
VALUES
(
v_programid,
v_programcode,
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
v_quantityreceived,
v_quantityapprovedprev,
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
v_mossuppliedinpast,
v_supervisorynodeid,
v_this_run,
v_tracer,
v_skipped,
v_stockoutdays,
v_rnr_modifieddate,
v_periodstartdate,
v_periodenddate,
v_openingBalance,
v_dispensed,
v_adjustment,
v_quantityordered,
v_quantityshipped,
v_dateordered,
v_dateshipped,

v_sohprev,
v_dispensedprev,
v_amcprev,
v_mosprev,

v_sohprev2,
v_dispensedprev2,
v_amcprev2,
v_mosprev2,

v_sohprev3,
v_dispensedprev3,
v_amcprev3,
v_mosprev3,
v_expired
) ;


v_updated = 't';
END loop ; -- update rnr create date
msg ='x11';
IF v_updated = 't' THEN
UPDATE dw_orders o
SET createddate = r.createddate
FROM
requisitions r
WHERE
o.rnrid = r. ID
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET initiateddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'INITIATED'
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET submitteddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'SUBMITTED'
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET authorizeddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'AUTHORIZED'
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET inapprovaldate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'IN_APPROVAL'
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET approveddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'APPROVED'
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET releaseddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'RELEASED'
and o.modifieddate >= v_this_run;
SELECT value from configuration_settings where key='LATE_REPORTING_DAYS' INTO v_late_days;
v_late_days = COALESCE(v_late_days,10);
UPDATE dw_orders o
SET reporting = CASE
WHEN (EXTRACT (DAY FROM r.createddate) - (select EXTRACT (DAY FROM startdate) from processing_periods where id = r.periodid)) > v_late_days THEN
'L'
ELSE
'O'
END
FROM
requisitions r
WHERE
o.rnrid = r. ID  -- update rnr approved date
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET shippeddate = s.shippeddate,
quantityshipped = s.quantityshipped
FROM
shipment_line_items s
WHERE
o.rnrid = s.orderid
AND o.productcode = s.productcode -- update rnr received date from pod
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET receiveddate = P .receiveddate
FROM
pod P
WHERE
o.rnrid = P .orderid
and o.modifieddate >= v_this_run;
UPDATE dw_orders o
SET rmnch = 't'
where o.productid in (
select products.id from program_products
join products on program_products.productid = products.id
join programs on program_products.programid = programs.id
where lower(programs.code) = 'rmnch'
)
and o.modifieddate >= v_this_run;
if in_flag = 0 THEN
delete from alert_facility_stockedout;
delete from alert_requisition_approved;
delete from alert_requisition_emergency;
delete from alert_requisition_pending;
delete from alert_requisition_rejected;
delete from alert_summary;
end if;
msg = fn_populate_alert_facility_stockedout(in_flag);
msg = fn_populate_alert_requisition_approved(in_flag);
msg = fn_populate_alert_requisition_pending(in_flag);
msg = fn_populate_alert_requisition_rejected(in_flag);
msg = fn_populate_alert_requisition_emergency(in_flag);
END IF;
msg := 'Data saved successfully' ;
RETURN msg;
EXCEPTION
WHEN OTHERS THEN
RETURN msg || ' Error populating data. Please consult database administrtor. ' || SQLERRM;
END ; $$;


ALTER FUNCTION public.fn_populate_dw_orders(in_flag integer) OWNER TO postgres;

--
-- Name: fn_previous_cb(integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_previous_cb(v_rnr_id integer, v_productcode character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
v_ret integer;
v_prev_id integer;

BEGIN

select stockinhand  into v_ret from requisition_line_items where id < v_rnr_id and productcode = v_productcode;
v_ret = COALESCE(v_ret,0);

return v_ret;
END;
$$;


ALTER FUNCTION public.fn_previous_cb(v_rnr_id integer, v_productcode character varying) OWNER TO postgres;

--
-- Name: fn_previous_cb(integer, integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_previous_cb(v_program_id integer, v_facility_id integer, v_period_id integer, v_productcode character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
v_ret integer;
v_prev_id integer;
v_rnr_id integer;
BEGIN

select id into v_rnr_id from requisitions where periodid < v_period_id and facilityid = v_facility_id and programid = v_program_id order by periodid desc limit 1;
v_rnr_id = COALESCE(v_rnr_id,0);

if v_rnr_id > 0 then
 select stockinhand into v_ret from requisition_line_items where rnrid = v_rnr_id and productcode = v_productcode;
end if;

v_ret = COALESCE(v_ret,0);
return v_ret;
END;
$$;


ALTER FUNCTION public.fn_previous_cb(v_program_id integer, v_facility_id integer, v_period_id integer, v_productcode character varying) OWNER TO postgres;

--
-- Name: fn_previous_pd(integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_previous_pd(v_rnr_id integer, v_period_id integer, v_productcode character varying) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
v_ret integer;
v_prev_id integer;
v_rnr_id integer;
BEGIN
select id into v_rnr_id from requisitions where periodid < v_period_id order by periodid desc limit 1;
v_rnr_id = COALESCE(v_rnr_id,0);
if v_rnr_id > 0 then
select quantityreceived into v_ret from requisition_line_items where rnrid = v_rnr_id and productcode = v_productcode;
end if;
v_ret = COALESCE(v_ret,0);
return v_ret;
END;
$$;


ALTER FUNCTION public.fn_previous_pd(v_rnr_id integer, v_period_id integer, v_productcode character varying) OWNER TO postgres;

--
-- Name: fn_previous_rnr_detail(integer, integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_previous_rnr_detail(v_program_id integer, v_period_id integer, v_facility_id integer, v_productcode character varying) RETURNS TABLE(rnrid integer, productcode character varying, beginningbalance integer, quantityreceived integer, quantitydispensed integer, stockinhand integer, quantityrequested integer, calculatedorderquantity integer, quantityapproved integer, totallossesandadjustments integer, reportingdays integer, previousstockinhand integer, periodnormalizedconsumption integer, amc integer)
    LANGUAGE plpgsql
    AS $$
DECLARE
v_ret integer;
v_prev_id integer;
v_rnr_id integer;
finalQuery            VARCHAR;
BEGIN

select id into v_rnr_id from requisitions where requisitions.periodid < v_period_id and facilityid = v_facility_id and requisitions.programid = v_program_id order by requisitions.periodid desc limit 1;
v_rnr_id = COALESCE(v_rnr_id,0);

finalQuery :=
 'select 
rnrid,
productcode,
beginningbalance,
quantityreceived,
quantitydispensed,
stockinhand,
quantityrequested,
calculatedorderquantity,
quantityapproved,
totallossesandadjustments,
reportingdays,
previousstockinhand,
periodnormalizedconsumption,
amc
from requisition_line_items where rnrid = '||v_rnr_id || ' and productcode = '||chr(39)||v_productcode||chr(39);

  RETURN QUERY EXECUTE finalQuery;


END;
$$;


ALTER FUNCTION public.fn_previous_rnr_detail(v_program_id integer, v_period_id integer, v_facility_id integer, v_productcode character varying) OWNER TO postgres;

--
-- Name: fn_set_user_preference(integer, character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_set_user_preference(in_userid integer, in_key character varying, in_value character varying) RETURNS character varying
    LANGUAGE plpgsql
    AS $$
DECLARE msg character varying(2000);
check_id integer;
productids int[];
i integer;
valid_list integer;

BEGIN

msg := 'ERROR';

IF in_key != ANY(ARRAY['DEFAULT_PROGRAM','DEFAULT_SCHEDULE','DEFAULT_PERIOD','DEFAULT_GEOGRAPHIC_ZONE','DEFAULT_FACILITY','DEFAULT_PRODUCTS']) THEN
 msg := 'Invalid key';
END IF;


if in_key = 'DEFAULT_PROGRAM' THEN
 select id into check_id from programs where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_PROGRAM' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PROGRAM', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid program value. Aborting';  
 end if;
end if;

if in_key = 'DEFAULT_SCHEDULE' THEN
 select id into check_id from processing_schedules where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_SCHEDULE' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_SCHEDULE', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid schedule value. Aborting';  
 end if;
end if;


if in_key = 'DEFAULT_PERIOD' THEN
 select id into check_id from processing_periods where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_PERIOD' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PERIOD', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid period value. Aborting';  
 end if;
end if;

if in_key = 'DEFAULT_GEOGRAPHIC_ZONE' THEN
 select id into check_id from geographic_zones where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_GEOGRAPHIC_ZONE' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_GEOGRAPHIC_ZONE', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid geographic zone value. Aborting';  
 end if; 
end if;

if in_key = 'DEFAULT_FACILITY' THEN
 select id into check_id from facilities where id = in_value::int;
 check_id = COALESCE(check_id,0);
 if check_id > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_FACILITY' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_FACILITY', in_value, 2, now(), 2, now());
  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid facility value. Aborting';  
 end if;

end if;

valid_list = 1;
if in_key = 'DEFAULT_PRODUCTS' THEN
 productids = '{'||in_value||'}';
FOREACH i IN ARRAY productids
LOOP 
 select id into check_id from products where id = i::int;
 check_id = COALESCE(check_id,0);
 if check_id = 0 then
  valid_list = 0;
 end if; 
END LOOP;

 if valid_list > 0 then
  delete from user_preferences where userpreferencekey = 'DEFAULT_PRODUCTS' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PRODUCTS', in_value, 2, now(), 2, now());

  delete from user_preferences where userpreferencekey = 'DEFAULT_PRODUCT' and userid = in_userid;
  insert into user_preferences values (in_userid, 'DEFAULT_PRODUCT', productids[1], 2, now(), 2, now());

  msg := 'user.preference.set.successfully';
 else
  msg := 'Invalid product values. Aborting';  
 end if;
end if;


RETURN msg;
EXCEPTION WHEN OTHERS THEN
return SQLERRM;
END;
$$;


ALTER FUNCTION public.fn_set_user_preference(in_userid integer, in_key character varying, in_value character varying) OWNER TO postgres;

--
-- Name: fn_tbl_user_attributes(integer, character varying, integer, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_tbl_user_attributes(in_user_id integer DEFAULT NULL::integer, in_user_name character varying DEFAULT NULL::character varying, in_program_id integer DEFAULT NULL::integer, in_output text DEFAULT NULL::text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
/*
fn_tbl_user_attributes - This function returns user attributes like
requisition groups, programs, supervisornode and facilities one at a time.

Input:
 in_user_id - user id
 in_username - username
 in in_program_id - program id
 in_output - special code for output to return
           RGID - list of requisition group ids
           RGCODE - list of requistion group code (single quoted)
           SNODE - supervisor node ids
           FACCODE - list of facility code
           FACid - list of facility ids
 Output: comma-delimited list

Example Call-
 SELECT fn_tbl_user_attributes(16,null,1,'RGID');
 SELECT fn_tbl_user_attributes(null,'Elias',1,'RGID');
 SELECT fn_tbl_user_attributes(16,null,1,'SNODE');
 SELECT fn_tbl_user_attributes(16,null,1,'FACID');
 SELECT fn_tbl_user_attributes(16,null,1,'FACCODE');

TODO: Find a a way to pass parameters by name
--------------------------------------------------------------------------------
Modification History (LIFO)
--------------------------------------------------------------------------------
05.13.2013 - mahmed - Created
*/

  -- user requisition groups
  rg_cursor CURSOR FOR
  SELECT distinct on (user_id,rg_id) user_id, rg_id, rg_code, role_id
  FROM vw_user_role_program_rg
  where (user_id = in_user_id or in_user_id is null)
  and (username = in_user_name or in_user_name is null)
  and (program_id = in_program_id or in_program_id is null);

  -- user facilities
  fac_cursor CURSOR FOR
  SELECT distinct on (user_id,facility_id) user_id, facility_id, facility_code, role_id
  FROM vw_user_program_facilities
  where (user_id = in_user_id or in_user_id is null)
  and (username = in_user_name or in_user_name is null)
  and (program_id = in_program_id or in_program_id is null);

  -- admin user
  user_cursor CURSOR FOR
  SELECT role_assignments.roleid
  FROM  users
  INNER JOIN role_assignments ON role_assignments.userid = users.id
  where (users.id = in_user_id or in_user_id is null)
    and (users.username = in_user_name or in_user_name is null)
    and role_assignments.roleid = 1;


rec RECORD;
delim character(1);
ret_val TEXT;

BEGIN
--
delim = '';
ret_val = '';

open user_cursor;
FETCH user_cursor INTO rec;
IF FOUND THEN
 ret_val = '*';
 RETURN ret_val;
end if;
close user_cursor;


-- check the output request
IF upper(in_output) = 'FACCODE' OR upper(in_output) = 'FACID' THEN
-- facility information requested
OPEN fac_cursor;
  LOOP
    FETCH fac_cursor INTO rec;
    EXIT WHEN NOT FOUND;

if upper(in_output) = 'FACID' THEN
 ret_val = ret_val || delim ||rec.facility_id;
elsif upper(in_output) = 'FACCODe' THEN
 ret_val = ret_val || delim ||chr(39)||rec.facility_code||chr(39);
else
 ret_val = '';
END IF;

delim = ',';

  END LOOP;
  CLOSE fac_cursor;

ELSIF upper(in_output) = 'RGID' OR upper(in_output) = 'RGCODE' OR upper(in_output) = 'SNODE' THEN

OPEN rg_cursor;
  LOOP
    FETCH rg_cursor INTO rec;
    EXIT WHEN NOT FOUND;

if upper(in_output) = 'RGID' THEN
 ret_val = ret_val || delim ||rec.rg_id;
elsif upper(in_output) = 'RGCODE' THEN
 ret_val = ret_val || delim ||chr(39)||rec.rg_code||chr(39);
elsif upper(in_output) = 'SNODE' THEN
ret_val = ret_val || delim ||rec.supervisorynodeid;
else
 ret_val = '';
END IF;

delim = ',';

  END LOOP;
  CLOSE rg_cursor;
END IF;


ret_val = coalesce(ret_val, 'none');

RETURN ret_val;
EXCEPTION WHEN OTHERS THEN RETURN SQLERRM;
END;

$$;


ALTER FUNCTION public.fn_tbl_user_attributes(in_user_id integer, in_user_name character varying, in_program_id integer, in_output text) OWNER TO postgres;

--
-- Name: fn_update_program_product_isa(integer, integer, numeric, integer, numeric, numeric, integer, integer, integer, integer, timestamp without time zone, integer, timestamp without time zone, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_update_program_product_isa(program_product_id integer, isa_coefficient_id integer, who_ratio numeric, doses_per_year integer, wastage_factor numeric, buffer_percentage numeric, minimum_value integer, maximum_value integer, adjustment_value integer, created_by integer, created_date timestamp without time zone, modified_by integer, modified_date timestamp without time zone, population_source integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
BEGIN

  UPDATE isa_coefficients
  SET whoratio = who_ratio,
	dosesperyear = doses_per_year,
	wastagefactor = wastage_factor,
	bufferpercentage = buffer_percentage,
	minimumvalue = minimum_value,
	maximumvalue = maximum_value,
	adjustmentvalue = adjustment_value,
	createdby = created_by,
	createddate = created_date,
	modifiedby = modified_by,
	modifieddate = modified_date,
  populationsource =  population_source
  WHERE
	id = isa_coefficient_id;

  UPDATE program_products
  SET isaCoefficientsId = isa_coefficient_id
  WHERE id = program_product_id;

END;
$$;


ALTER FUNCTION public.fn_update_program_product_isa(program_product_id integer, isa_coefficient_id integer, who_ratio numeric, doses_per_year integer, wastage_factor numeric, buffer_percentage numeric, minimum_value integer, maximum_value integer, adjustment_value integer, created_by integer, created_date timestamp without time zone, modified_by integer, modified_date timestamp without time zone, population_source integer) OWNER TO postgres;

--
-- Name: fn_vaccine_facility_n_rnrs(character varying, character varying, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_vaccine_facility_n_rnrs(in_program_code character varying, in_facility_code character varying, in_product_code character varying, in_n integer DEFAULT 4) RETURNS TABLE(program_code text, facility_code text, product_code text, period_id integer, opening_balance integer, quantity_received integer, quantity_issued integer, quantity_vvm_alerted integer, quantity_freezed integer, quantity_expired integer, quantity_discarded_unopened integer, quantity_discarded_opened integer, quantity_wasted_other integer, ending_balance integer, closing_balance integer, days_stockedout integer, price numeric)
    LANGUAGE plpgsql
    AS $$
/*
 This function function build anlystical table of key logistics indicators of product 
  for past n periods of single reporting facility

 indicators include:
 opening balanace
 quantity received,
 quantity dispensed
 adjusted consumption
 adjustment
 stockinhand
 quantity requested
 quantity approved
 quanity expired
 price
 
*/

DECLARE

-- return values
v_rnr_id integer;
finalQuery            VARCHAR;

-- temp
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;
t_program_id integer;
t_facility_id integer;

BEGIN

-- get ids, the nth function called below requires id except for product 
select id into t_product_id from products where lower(code) = lower(in_product_code); 
select id into t_program_id from programs where lower(code)= lower(in_program_code);
select id into t_facility_id from facilities where lower(code)= lower(in_facility_code);
select currentprice into t_price from program_products where productid = t_product_id and programid = t_program_id;

select vaccine_reports.periodid, processing_periods.startdate::date into t_id, t_date 
 from vaccine_reports
 join processing_periods ON vaccine_reports.periodid = processing_periods.id
 where facilityid = t_facility_id 
   and vaccine_reports.programid = t_program_id
  order by processing_periods.startdate desc
 limit 1;

-- get start date
t_start_date = t_date;
t_period_id = COALESCE(t_id,0);

i := 0;
finalQuery = '';
-- not executed if in_nth is 0(current period)
FOR i in 0..in_n-1
 LOOP
 if i = 0 THEN
  finalQuery := '';
 ELSE
  finalQuery := finalQuery || ' union all ';
 END IF;
 finalQuery := finalQuery ||
   'select '''||
    in_program_code|| '''::text program_code, '''||
    in_facility_code|| '''::text facility_code, '''||
    in_product_code|| '''::text product_code, '||
    'periodid period_id,
		openingbalance opening_balance,
		quantityreceived quantity_received,
		quantityissued quantity_issued,
		quantityvvmalerted quantity_vvm_alerted,
		quantityfreezed quantity_freezed,
		quantityexpired quantity_expired,
		quantitydiscardedunopened quantity_discarded_unopened,
		quantitydiscardedopened quantity_discarded_opened,
		quantitywastedother quantity_wasted_other,
		endingbalance ending_balance,
		closingbalance closing_balance,
		daysstockedout days_stockedout,
		price
    from fn_vaccine_facility_nth_rnr('||t_program_id||','||t_period_id||','||t_facility_id||','||t_product_id||','||i||')';
 i = i+1;
end loop;

RETURN QUERY EXECUTE finalQuery;
END;
$$;


ALTER FUNCTION public.fn_vaccine_facility_n_rnrs(in_program_code character varying, in_facility_code character varying, in_product_code character varying, in_n integer) OWNER TO postgres;

--
-- Name: fn_vaccine_facility_nth_rnr(integer, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_vaccine_facility_nth_rnr(in_program_id integer, in_period_id integer, in_facility_id integer, in_productid integer, in_nth integer DEFAULT 0) RETURNS TABLE(reportid integer, productcode character varying, openingbalance integer, quantityreceived integer, quantityissued integer, quantityvvmalerted integer, quantityfreezed integer, quantityexpired integer, quantitydiscardedunopened integer, quantitydiscardedopened integer, quantitywastedother integer, endingbalance integer, closingbalance integer, daysstockedout integer, price numeric, periodid integer)
    LANGUAGE plpgsql
    AS $$
/*
 This function function build anlystical table of key logistics indicators of product for past 4 periods of single reporting facility

 indicators include:
 opening balanace
 quantity received,
 quantity dispensed
 adjusted consumption
 adjustment
 stockinhand
 quantity requested
 quantity approved
 quanity expired
 price
 
*/

DECLARE

-- return values
v_rnr_id integer;
finalQuery            VARCHAR;

-- temp
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;

BEGIN

t_product_id = in_productid;

-- get price
--select id into t_product_id from products where code = in_productcode; 
select currentprice into t_price from program_products where productid = t_product_id and programid = in_program_id;

-- get start date
t_start_date = (select startdate::date from processing_periods where id = in_period_id);
t_period_id = COALESCE(in_period_id,0);

i := 0;

-- not executed if in_nth is 0(current period)
FOR i in 1..in_nth 
 LOOP
 i = i+1;
 select vaccine_reports.periodid, processing_periods.startdate::date into t_id, t_date 
 from vaccine_reports
 join processing_periods ON vaccine_reports.periodid = processing_periods.id
 where processing_periods.startdate < t_start_date 
   and facilityid = in_facility_id 
   and vaccine_reports.programid = in_program_id
  order by processing_periods.startdate desc
 limit 1;
 t_start_date = t_date;
 t_period_id = COALESCE(t_id,0);

 EXIT WHEN t_period_id =  0;
 
 END LOOP;

if t_period_id > 0 then

-- get requisition id of nth period
select id into v_rnr_id 
 from vaccine_reports
 where vaccine_reports.periodid = t_period_id 
 and facilityid = in_facility_id 
 and vaccine_reports.programid = in_program_id 
 order by vaccine_reports.periodid desc
 limit 1;

 v_rnr_id = COALESCE(v_rnr_id,0);

 finalQuery :=
'select
reportid,
productcode,
openingbalance,
quantityreceived,
quantityissued,
quantityvvmalerted,
quantityfreezed,
quantityexpired,
quantitydiscardedunopened,
quantitydiscardedopened,
quantitywastedother,
endingbalance,
closingbalance,
daysstockedout,'
||t_price|| ' price, '|| t_period_id || ' periodid '||'
from vaccine_report_logistics_line_items where reportid = '||v_rnr_id || ' and productid = '||in_productid;

ELSE

finalQuery :=
       'select
				null::int reportid,
				null::character varying productcode,
				null::int openingbalance,
				null::int quantityreceived,
				null::int quantityissued,
				null::int quantityvvmalerted,
				null::int quantityfreezed,
				null::int quantityexpired,
				null::int quantitydiscardedunopened,
				null::int quantitydiscardedopened,
				null::int quantitywastedother,
				null::int endingbalance,
				null::int closingbalance,
				null::int daysstockedout,
				null::numeric price,
				null::integer periodid';
end if;

RETURN QUERY EXECUTE finalQuery;
END;
$$;


ALTER FUNCTION public.fn_vaccine_facility_nth_rnr(in_program_id integer, in_period_id integer, in_facility_id integer, in_productid integer, in_nth integer) OWNER TO postgres;

--
-- Name: fn_vaccine_geozone_n_rnrs(character varying, integer, integer, character varying, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_vaccine_geozone_n_rnrs(in_program_code character varying, in_period_id integer, in_geographiczone_id integer, in_product_code character varying, in_n integer DEFAULT 4) RETURNS TABLE(program_code text, geographiczone_id integer, product_code text, period_id integer, opening_balance integer, quantity_received integer, quantity_issued integer, quantity_vvm_alerted integer, quantity_freezed integer, quantity_expired integer, quantity_discarded_unopened integer, quantity_discarded_opened integer, quantity_wasted_other integer, ending_balance integer, closing_balance integer, days_stockedout integer, price numeric)
    LANGUAGE plpgsql
    AS $$
/*
 This function function build anlystical table of key logistics indicators of product 
  for past n periods of single reporting facility

 indicators include:
 opening balanace
 quantity received,
 quantity dispensed
 adjusted consumption
 adjustment
 stockinhand
 quantity requested
 quantity approved
 quanity expired
 price
 
*/

DECLARE

-- return values
v_rnr_id integer;
finalQuery            VARCHAR;

-- temp
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;
t_program_id integer;
t_geographiczone_id integer;

BEGIN

-- get ids, the nth function called below requires id except for product 
select id into t_product_id from products where lower(code) = lower(in_product_code); 
select id into t_program_id from programs where lower(code)= lower(in_program_code);
select currentprice into t_price from program_products where productid = t_product_id and programid = t_program_id;


SELECT
  vaccine_reports.periodid,
  processing_periods.startdate::date into t_id, t_date   
 FROM
  vaccine_reports
  JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
  INNER JOIN facilities ON facilities.id = vaccine_reports.facilityid
  INNER JOIN vw_districts ON facilities.geographiczoneid = vw_districts.district_id
  where  processing_periods.id = in_period_id
  order by processing_periods.startdate desc
 limit 1;


-- get start date
t_start_date = t_date;
t_period_id = COALESCE(t_id,0);

i := 0;
finalQuery = '';
-- not executed if in_nth is 0(current period)
FOR i in 0..in_n-1
 LOOP
 if i = 0 THEN
  finalQuery := '';
 ELSE
  finalQuery := finalQuery || ' union all ';
 END IF;
 finalQuery := finalQuery ||
    'select '''||
    in_program_code|| '''::text program_code, '||
    in_geographiczone_id|| '::integer geographiczone_id, '''||
    in_product_code|| '''::text product_code, '||
    'periodid period_id,
		openingbalance opening_balance,
		quantityreceived quantity_received,
		quantityissued quantity_issued,
		quantityvvmalerted quantity_vvm_alerted,
		quantityfreezed quantity_freezed,
		quantityexpired quantity_expired,
		quantitydiscardedunopened quantity_discarded_unopened,
		quantitydiscardedopened quantity_discarded_opened,
		quantitywastedother quantity_wasted_other,
		endingbalance ending_balance,
		closingbalance closing_balance,
		daysstockedout days_stockedout,
		price
    from fn_vaccine_geozone_nth_rnr('||t_program_id||','||t_period_id||','||in_geographiczone_id||','||t_product_id||','||i||')';
   i = i+1;
end loop;

RETURN QUERY EXECUTE finalQuery;
END;
$$;


ALTER FUNCTION public.fn_vaccine_geozone_n_rnrs(in_program_code character varying, in_period_id integer, in_geographiczone_id integer, in_product_code character varying, in_n integer) OWNER TO postgres;

--
-- Name: fn_vaccine_geozone_nth_rnr(integer, integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_vaccine_geozone_nth_rnr(in_program_id integer, in_period_id integer, in_geographiczone_id integer, in_product_id integer, in_nth integer DEFAULT 0) RETURNS TABLE(periodid integer, geographiczoneid integer, openingbalance integer, quantityreceived integer, quantityissued integer, quantityvvmalerted integer, quantityfreezed integer, quantityexpired integer, quantitydiscardedunopened integer, quantitydiscardedopened integer, quantitywastedother integer, endingbalance integer, closingbalance integer, daysstockedout integer, price numeric)
    LANGUAGE plpgsql
    AS $$
DECLARE
v_rnr_id integer;
finalQuery            VARCHAR;
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;
t_where_1 varchar;
t_group_by varchar;
t_schedule_id integer;
BEGIN
t_product_id = in_product_id;
t_period_id = COALESCE(in_period_id,0);
select currentprice into t_price from program_products where productid = t_product_id and programid = in_program_id;
select startdate::date, scheduleid into t_start_date, t_schedule_id from processing_periods where id = t_period_id;
i := 0;
FOR i in 1..in_nth
LOOP
i = i+1;
SELECT
vaccine_reports.periodid,
processing_periods.startdate::date into t_id, t_date
FROM
vaccine_reports
JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
INNER JOIN facilities ON facilities.id = vaccine_reports.facilityid
INNER JOIN vw_districts ON facilities.geographiczoneid = vw_districts.district_id
where processing_periods.scheduleid = t_schedule_id
and processing_periods.startdate::date < t_start_date
order by processing_periods.startdate desc
limit 1;
t_start_date = t_date;
t_period_id = COALESCE(t_id,0);
EXIT WHEN t_period_id =  0;
END LOOP;
t_where_1  = ' where productid = '||in_product_id||' and (district_id = '||in_geographiczone_id||' or region_id = '||in_geographiczone_id||' or zone_id = '||in_geographiczone_id || ' or parent =  '||in_geographiczone_id ||')';
t_group_by = ' group by vaccine_reports.perioid ';
if t_period_id > 0 then
t_where_1 = t_where_1 || ' and periodid = '||t_period_id;
finalQuery :=
'SELECT '||
t_period_id || '::integer period_d, '||
in_geographiczone_id || '::integer geographiczoneid, '||'
sum(openingbalance)::int openingbalance,
sum(quantityreceived)::int quantityreceived,
sum(quantityissued)::int quantityissued,
sum(quantityvvmalerted)::int quantityvvmalerted,
sum(quantityfreezed)::int quantityfreezed,
sum(quantityexpired)::int quantityexpired,
sum(quantitydiscardedunopened)::int quantitydiscardedunopened,
sum(quantitydiscardedopened)::int quantitydiscardedopened,
sum(quantitywastedother)::int quantitywastedother,
sum(endingbalance)::int endingbalance,
sum(closingbalance)::int closingbalance,
sum(daysstockedout)::int daysstockedout, 0::numeric price
FROM
vaccine_report_logistics_line_items
JOIN vaccine_reports ON vaccine_report_logistics_line_items.reportid = vaccine_reports.id
INNER JOIN processing_periods ON processing_periods.id = vaccine_reports.periodid
INNER JOIN facilities ON facilities.id = vaccine_reports.facilityid
INNER JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid '||
t_where_1;
ELSE
finalQuery :=
'select
null::int periodid,
null::int geographiczoneid,
null::int openingbalance,
null::int quantityreceived,
null::int quantityissued,
null::int quantityvvmalerted,
null::int quantityfreezed,
null::int quantityexpired,
null::int quantitydiscardedunopened,
null::int quantitydiscardedopened,
null::int quantitywastedother,
null::int endingbalance,
null::int closingbalance,
null::int daysstockedout,
null::numeric price';
end if;
RETURN QUERY EXECUTE finalQuery;
END;
$$;


ALTER FUNCTION public.fn_vaccine_geozone_nth_rnr(in_program_id integer, in_period_id integer, in_geographiczone_id integer, in_product_id integer, in_nth integer) OWNER TO postgres;

--
-- Name: fn_vims_monthly_report_detail(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_vims_monthly_report_detail(in_programid integer, in_geographiczoneid integer, in_periodid integer) RETURNS TABLE(programid integer, geographiczoneid integer, district character varying, periodid integer, periodname character varying, year integer, productid integer, productname character varying, productgroupcode character varying, productcategorycode character varying, cov_child_num_infants_fixed integer, cov_child_num_infants_mobile integer, cov_child_num_other integer, cov_child_num_monthly integer, cov_child_num_cumulative integer, cov_child_num_drop_outs integer, cov_child_num_perf_class character varying, cov_women_num_pregnant_fixed integer, cov_women_num_pregnant_mobile integer, cov_women_num_other integer, cov_women_num_monthly integer, cov_women_num_cumulative integer, cov_women_num_drop_outs integer, cov_women_num_perf_class character varying, cov_girls_num_adole_fixed integer, cov_girls_num_adole_mobile integer, cov_girls_num_other integer, cov_girls_num_monthly integer, cov_girls_num_cumulative integer, cov_girls_num_drop_outs integer, cov_girls_num_perf_class character varying, cov_vitamin_num_less_than_year_count integer, cov_vitamin_num_more_than_year_count integer, cov_vitamin_num_partum_count integer, cov_vitamin_num_less_than_year_monthly integer, cov_vitamin_num_more_than_year_monthly integer, cov_vitamin_num_partum_monthly integer, cov_vitamin_num_less_than_year_cumulative integer, cov_vitamin_num_more_than_year_cumulative integer, cov_vitamin_num_partum_cumulative integer, ss_received integer, ss_onhand integer, ss_vvm integer, ss_freezing integer, ss_expired integer, ss_opened integer, ss_wastage_rate integer, svl_0_11_months_cases integer, svl_0_11_months_deaths integer, svl_12_59_months_cases integer, svl_12_59_months_deaths integer, svl_5_15_years_cases integer, svl_5_15_years_deaths integer, svl_15_plus_years_cases integer, svl_15_plus_years_deaths integer, svl_status_vaccinated integer, svl_status_unvaccinated integer, svl_status_unknown integer)
    LANGUAGE plpgsql
    AS $$
DECLARE

q VARCHAR ;
r RECORD ;
v_id INTEGER ;

v_programid integer = 1;
v_geographiczoneid integer = 1;
v_district varchar = '';
v_periodid integer = 1;
v_periodname varchar = '';
v_year integer = 1;
v_productid integer = 1;
v_productname varchar = '';
v_productgroupcode varchar = '';
v_productcategorycode varchar = '';
v_cov_child_num_infants_fixed integer = 1;
v_cov_child_num_infants_mobile integer = 1;
v_cov_child_num_other integer = 1;
v_cov_child_num_monthly integer = 1;
v_cov_child_num_cumulative integer = 1;
v_cov_child_num_drop_outs integer = 1;
v_cov_child_num_perf_class varchar = '';
v_cov_women_num_pregnant_fixed integer = 1;
v_cov_women_num_pregnant_mobile integer = 1;
v_cov_women_num_other integer = 1;
v_cov_women_num_monthly integer = 1;
v_cov_women_num_cumulative integer = 1;
v_cov_women_num_drop_outs integer = 1;
v_cov_women_num_perf_class varchar = '';
v_cov_girls_num_adole_fixed integer = 1;
v_cov_girls_num_adole_mobile integer = 1;
v_cov_girls_num_other integer = 1;
v_cov_girls_num_monthly integer = 1;
v_cov_girls_num_cumulative integer = 1;
v_cov_girls_num_drop_outs integer = 1;
v_cov_girls_num_perf_class varchar = '';

v_cov_vitamin_num_less_than_year_count integer = 1;
v_cov_vitamin_num_more_than_year_count integer = 1;
v_cov_vitamin_num_partum_count integer = 1;

v_cov_vitamin_num_less_than_year_monthly integer = 1;
v_cov_vitamin_num_more_than_year_monthly integer = 1;
v_cov_vitamin_num_partum_monthly integer = 1;
v_cov_vitamin_num_less_than_year_cumulative integer = 1;
v_cov_vitamin_num_more_than_year_cumulative integer = 1;
v_cov_vitamin_num_partum_cumulative integer = 1;
v_ss_received integer = 1;
v_ss_onhand integer = 1;
v_ss_vvm integer = 1;
v_ss_freezing integer = 1;
v_ss_expired integer = 1;
v_ss_opened integer = 1;
v_ss_wastage_rate integer = 1;
v_svl_0_11_months_cases integer = 1;
v_svl_0_11_months_deaths integer = 1;
v_svl_12_59_months_cases integer = 1;
v_svl_12_59_months_deaths integer = 1;
v_svl_5_15_years_cases integer = 1;
v_svl_5_15_years_deaths integer = 1;
v_svl_15_plus_years_cases integer = 1;
v_svl_15_plus_years_deaths integer = 1;
v_svl_status_vaccinated integer = 1;
v_svl_status_unvaccinated integer = 1;
v_svl_status_unknown integer = 1;  



BEGIN

EXECUTE 'CREATE TEMP TABLE _data (
	programid integer,
	geographiczoneid integer,
	district varchar,
	periodid integer,
	periodname varchar,
	year integer,
	productid integer,
	productname varchar,
  productgroupcode varchar,
  productcategorycode varchar,
	cov_child_num_infants_fixed integer,
	cov_child_num_infants_mobile integer,
	cov_child_num_other integer,
	cov_child_num_monthly integer,
	cov_child_num_cumulative integer,
	cov_child_num_drop_outs integer,
	cov_child_num_perf_class varchar,
	cov_women_num_pregnant_fixed integer,
	cov_women_num_pregnant_mobile integer,
	cov_women_num_other integer,
	cov_women_num_monthly integer,
	cov_women_num_cumulative integer,
	cov_women_num_drop_outs integer,
	cov_women_num_perf_class varchar,
	cov_girls_num_adole_fixed integer,
	cov_girls_num_adole_mobile integer,
	cov_girls_num_other integer,
	cov_girls_num_monthly integer,
	cov_girls_num_cumulative integer,
	cov_girls_num_drop_outs integer,
	cov_girls_num_perf_class varchar,
  cov_vitamin_num_less_than_year_count integer,
  cov_vitamin_num_more_than_year_count integer,
  cov_vitamin_num_partum_count integer,
  cov_vitamin_num_less_than_year_monthly integer,
  cov_vitamin_num_more_than_year_monthly integer,
  cov_vitamin_num_partum_monthly integer,
  cov_vitamin_num_less_than_year_cumulative integer,
  cov_vitamin_num_more_than_year_cumulative integer,
  cov_vitamin_num_partum_cumulative integer,
	ss_received integer,
	ss_onhand integer,
	ss_vvm integer,
	ss_freezing integer,
	ss_expired integer,
	ss_opened integer,
	ss_wastage_rate integer,
	svl_0_11_months_cases integer,
	svl_0_11_months_deaths integer,
	svl_12_59_months_cases integer,
	svl_12_59_months_deaths integer,
	svl_5_15_years_cases integer,
	svl_5_15_years_deaths integer,
	svl_15_plus_years_cases integer,
	svl_15_plus_years_deaths integer,
	svl_status_vaccinated integer,
	svl_status_unvaccinated integer,
	svl_status_unknown integer  

) ON COMMIT DROP' ;

q= '
SELECT
geographic_zones.name AS district,
facilities.name AS facilityname,
processing_periods.name AS periodname,
extract(year from processing_periods.startdate) AS reportyear,
vaccine_report_logistics_line_items.productname,
product_groups.code AS productgroupcode,
product_categories.code productcategorycode,
vaccine_report_logistics_line_items.id,
vaccine_report_logistics_line_items.reportid,
vaccine_report_logistics_line_items.productid,
vaccine_report_logistics_line_items.productcode,
vaccine_report_logistics_line_items.productname,
vaccine_report_logistics_line_items.displayorder,
vaccine_report_logistics_line_items.openingbalance,
vaccine_report_logistics_line_items.quantityreceived,
vaccine_report_logistics_line_items.quantityissued,
vaccine_report_logistics_line_items.quantityvvmalerted,
vaccine_report_logistics_line_items.quantityfreezed,
vaccine_report_logistics_line_items.quantityexpired,
vaccine_report_logistics_line_items.quantitydiscardedunopened,
vaccine_report_logistics_line_items.quantitydiscardedopened,
vaccine_report_logistics_line_items.quantitywastedother,
vaccine_report_logistics_line_items.endingbalance,
vaccine_report_logistics_line_items.createdby,
vaccine_report_logistics_line_items.createddate,
vaccine_report_logistics_line_items.modifiedby,
vaccine_report_logistics_line_items.modifieddate,
vaccine_report_logistics_line_items.productcategory,
vaccine_report_logistics_line_items.closingbalance,
vaccine_report_logistics_line_items.daysstockedout,
vaccine_report_logistics_line_items.remarks,
vaccine_report_logistics_line_items.discardingreasonid,
vaccine_report_logistics_line_items.discardingreasonexplanation

FROM
vaccine_report_logistics_line_items
JOIN products ON vaccine_report_logistics_line_items.productid = products.id
JOIN vaccine_reports ON vaccine_report_logistics_line_items.reportid = vaccine_reports.id
JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
JOIN facilities ON vaccine_reports.facilityid = facilities.id
JOIN geographic_zones ON facilities.geographiczoneid = geographic_zones.id
JOIN product_groups ON products.productgroupid = product_groups.id
JOIN program_products ON program_products.productid = products.id AND program_products.programid = vaccine_reports.programid
JOIN product_categories ON program_products.productcategoryid = product_categories.id
AND vaccine_reports.programid = '|| in_programid ||'
AND geographiczoneid = '|| in_geographiczoneid ||'
AND vaccine_reports.periodid = '|| in_periodid ||'
AND vaccine_reports.id = (select id from vaccine_reports where programid = '|| in_programid ||' 
      and periodid = '|| in_periodid ||' order by id desc limit 1)';

FOR r IN EXECUTE q
LOOP 

 v_programid = in_programid;
 v_periodid = in_periodid;
 v_geographiczoneid = in_geographiczoneid;
 v_district = r.district;
 v_periodname = r.periodname;
 v_year = r.reportYear;
 v_productname =  r.productname;
 v_productgroupcode = COALESCE(r.productgroupcode,'unk');
 v_productcategorycode = COALESCE(r.productcategorycode,'unk');

EXECUTE

	'INSERT INTO _data VALUES (' || 
	v_programid || ', '|| 
	v_geographiczoneid || ', '|| 
	quote_literal(v_district) || ', '|| 
	v_periodid || ', '|| 
	quote_literal(v_periodname) || ', '|| 
	v_year || ', '|| 
	v_productid || ', '|| 
	quote_literal(v_productname) || ', '||
  quote_literal(v_productgroupcode) || ', '||
  quote_literal(v_productcategorycode) || ', '||  
	v_cov_child_num_infants_fixed || ', '|| 
	v_cov_child_num_infants_mobile || ', '|| 
	v_cov_child_num_other || ', '|| 
	v_cov_child_num_monthly || ', '|| 
	v_cov_child_num_cumulative || ', '|| 
	v_cov_child_num_drop_outs || ', '|| 
	quote_literal(v_cov_child_num_perf_class) || ', '|| 
	v_cov_women_num_pregnant_fixed || ', '|| 
	v_cov_women_num_pregnant_mobile || ', '|| 
	v_cov_women_num_other || ', '|| 
	v_cov_women_num_monthly || ', '|| 
	v_cov_women_num_cumulative || ', '|| 
	v_cov_women_num_drop_outs || ', '|| 
	quote_literal(v_cov_women_num_perf_class) || ', '|| 
	v_cov_girls_num_adole_fixed || ', '|| 
	v_cov_girls_num_adole_mobile || ', '|| 
	v_cov_girls_num_other || ', '|| 
	v_cov_girls_num_monthly || ', '|| 
	v_cov_girls_num_cumulative || ', '|| 
	v_cov_girls_num_drop_outs || ', '|| 
	quote_literal(v_cov_girls_num_perf_class) || ', '||
  --
  v_cov_vitamin_num_less_than_year_count    || ', '|| 
  v_cov_vitamin_num_more_than_year_count    || ', '|| 
  v_cov_vitamin_num_partum_count            || ', '||  
  v_cov_vitamin_num_less_than_year_monthly    || ', '|| 
  v_cov_vitamin_num_more_than_year_monthly    || ', '|| 
  v_cov_vitamin_num_partum_monthly            || ', '|| 
  v_cov_vitamin_num_less_than_year_cumulative || ', '|| 
  v_cov_vitamin_num_more_than_year_cumulative || ', '|| 
  v_cov_vitamin_num_partum_cumulative         || ', '|| 
 --
	v_ss_received || ', '|| 
	v_ss_onhand || ', '|| 
	v_ss_vvm || ', '|| 
	v_ss_freezing || ', '|| 
	v_ss_expired || ', '|| 
	v_ss_opened || ', '|| 
	v_ss_wastage_rate || ', '|| 
	v_svl_0_11_months_cases || ', '|| 
	v_svl_0_11_months_deaths || ', '|| 
	v_svl_12_59_months_cases || ', '|| 
	v_svl_12_59_months_deaths || ', '|| 
	v_svl_5_15_years_cases || ', '|| 
	v_svl_5_15_years_deaths || ', '|| 
	v_svl_15_plus_years_cases || ', '|| 
	v_svl_15_plus_years_deaths || ', '|| 
	v_svl_status_vaccinated || ', '|| 
	v_svl_status_unvaccinated || ', '|| 
	v_svl_status_unknown || ')';

END LOOP ;

-- return data table
q := '
select 
	programid,
	geographiczoneid,
	district,
	periodid,
	periodname,
	year,
	productid,
	productname,
  productgroupcode,
  productcategorycode, 
	cov_child_num_infants_fixed,
	cov_child_num_infants_mobile,
	cov_child_num_other,
	cov_child_num_monthly,
	cov_child_num_cumulative,
	cov_child_num_drop_outs,
	cov_child_num_perf_class,
	cov_women_num_pregnant_fixed,
	cov_women_num_pregnant_mobile,
	cov_women_num_other,
	cov_women_num_monthly,
	cov_women_num_cumulative,
	cov_women_num_drop_outs,
	cov_women_num_perf_class,
	cov_girls_num_adole_fixed,
	cov_girls_num_adole_mobile,
	cov_girls_num_other,
	cov_girls_num_monthly,
	cov_girls_num_cumulative,
	cov_girls_num_drop_outs,
	cov_girls_num_perf_class,
  cov_vitamin_num_less_than_year_count,
  cov_vitamin_num_more_than_year_count,
  cov_vitamin_num_partum_count,
  cov_vitamin_num_less_than_year_monthly,
  cov_vitamin_num_more_than_year_monthly,
  cov_vitamin_num_partum_monthly,
  cov_vitamin_num_less_than_year_cumulative,
  cov_vitamin_num_more_than_year_cumulative,
  cov_vitamin_num_partum_cumulative,	
  ss_received,
	ss_onhand,
	ss_vvm,
	ss_freezing,
	ss_expired,
	ss_opened,
	ss_wastage_rate,
	svl_0_11_months_cases,
	svl_0_11_months_deaths,
	svl_12_59_months_cases,
	svl_12_59_months_deaths,
	svl_5_15_years_cases,
	svl_5_15_years_deaths,
	svl_15_plus_years_cases,
	svl_15_plus_years_deaths,
	svl_status_vaccinated,
	svl_status_unvaccinated,
	svl_status_unknown
from _data' ;

RETURN QUERY EXECUTE q ;
END ;

$$;


ALTER FUNCTION public.fn_vims_monthly_report_detail(in_programid integer, in_geographiczoneid integer, in_periodid integer) OWNER TO postgres;

--
-- Name: fn_vims_monthly_report_summary(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION fn_vims_monthly_report_summary(in_programid integer, in_geographiczoneid integer, in_periodid integer) RETURNS TABLE(programid integer, geographiczoneid integer, district character varying, periodid integer, periodname character varying, year integer, demo_population integer, demo_surviving_infants_0_11_annual integer, demo_surviving_infants_0_11_monthly integer, demo_population_pregnant integer, demo_population_new_born integer, compl_num_facilities integer, compl_num_vaccine_units integer, compl_num_reports_received integer, compl_num_reports_online integer, compl_num_outreach_sessions integer, iec_num_sessions integer, iec_num_participants integer, iec_num_radio_spots integer, iec_num_home_visits integer, aefi_num_cases integer, wastage_num_safety_boxes_used integer, wastage_num_safety_boxes_disposed integer, cca_num_reported_temp_status integer, cca_num_temp_2_c integer, cca_num_temp_8_c integer, cca_min_temp integer, cca_max_temp integer, cca_num_temp_low_alarm integer, cca_num_temp_high_alarm integer, ccb_min_temp integer, ccb_max_temp integer, ccb_num_temp_low_alarm integer, ccb_num_temp_high_alarm integer)
    LANGUAGE plpgsql
    AS $$
DECLARE

q VARCHAR ;
r RECORD ;
v_id INTEGER ;

v_programid  integer = 1;
v_geographiczoneid  integer = 1;
v_district  varchar = '';
v_periodid  integer = 1;
v_periodname  varchar = '';
v_year  integer = 1;
v_demo_population  integer = 1;
v_demo_surviving_infants_0_11_annual  integer = 1;
v_demo_surviving_infants_0_11_monthly  integer = 1;
v_demo_population_pregnant  integer = 1;
v_demo_population_new_born  integer = 1;  
v_compl_num_facilities  integer = 1;
v_compl_num_vaccine_units  integer = 1;
v_compl_num_reports_received  integer = 1;
v_compl_num_reports_online  integer = 1;
v_compl_num_outreach_sessions  integer = 1;  
v_iec_num_sessions  integer = 1;
v_iec_num_participants  integer = 1;
v_iec_num_radio_spots  integer = 1;
v_iec_num_home_visits  integer = 1;
v_aefi_num_cases  integer = 1;
v_wastage_num_safety_boxes_used integer = 1;
v_wastage_num_safety_boxes_disposed integer = 1;
v_cca_num_reported_temp_status  integer = 1;
v_cca_num_temp_2_c  integer = 1;
v_cca_num_temp_8_c  integer = 1;
v_cca_min_temp  integer = 1;
v_cca_max_temp  integer = 1;
v_cca_num_temp_low_alarm  integer = 1;
v_cca_num_temp_high_alarm  integer = 1;
v_ccb_min_temp  integer = 1;
v_ccb_max_temp  integer = 1;
v_ccb_num_temp_low_alarm  integer = 1;
v_ccb_num_temp_high_alarm  integer = 1;


BEGIN

EXECUTE 'CREATE TEMP TABLE _data (
  programid integer,
  geographiczoneid integer,
  district varchar,
  periodid integer,
  periodname varchar,
  year integer,
  demo_population integer,
  demo_surviving_infants_0_11_annual integer,
  demo_surviving_infants_0_11_monthly integer,
  demo_population_pregnant integer,
  demo_population_new_born integer,  
  compl_num_facilities integer,
  compl_num_vaccine_units integer,
  compl_num_reports_received integer,
  compl_num_reports_online integer,
  compl_num_outreach_sessions integer,  
  iec_num_sessions integer,
  iec_num_participants integer,
  iec_num_radio_spots integer,
  iec_num_home_visits integer,
  aefi_num_cases integer,
  wastage_num_safety_boxes_used integer,
  wastage_num_safety_boxes_disposed integer,
  cca_num_reported_temp_status integer,
  cca_num_temp_2_c integer,
  cca_num_temp_8_c integer,
  cca_min_temp integer,
  cca_max_temp integer,
  cca_num_temp_low_alarm integer,
  cca_num_temp_high_alarm integer,
  ccb_min_temp integer,
  ccb_max_temp integer,
  ccb_num_temp_low_alarm integer,
  ccb_num_temp_high_alarm integer
) ON COMMIT DROP' ;

q= '
SELECT 
     geographic_zones.name district,
     facilities.name facilityname,
     processing_periods.name periodname,
     extract(year from processing_periods.startdate) reportyear,
     vaccine_reports.*       
FROM vaccine_reports
JOIN processing_periods on vaccine_reports.periodid = processing_periods.id
JOIN facilities ON vaccine_reports.facilityid = facilities. ID
JOIN geographic_zones on facilities.geographiczoneid = geographic_zones.id     
 AND programid = '|| in_programid ||'
 AND geographiczoneid = '|| in_geographiczoneid ||'
 AND periodid = '|| in_periodid;

FOR r IN EXECUTE q
LOOP 

 v_programid = in_programid;
 v_periodid = in_periodid;
 v_geographiczoneid = in_geographiczoneid;
 v_district = r.district;
 v_periodname = r.periodname;
 v_year = r.reportYear;


EXECUTE

'INSERT INTO _data VALUES (' || 
	v_programid || ',' ||
	v_geographiczoneid || ',' ||
	quote_literal(v_district)|| ',' ||
	v_periodid || ',' ||
  quote_literal(v_periodname)|| ',' ||
	v_year || ',' ||
	v_demo_population || ',' ||
	v_demo_surviving_infants_0_11_annual || ',' ||
  v_demo_surviving_infants_0_11_monthly || ',' ||
	v_demo_population_pregnant || ',' ||
	v_demo_population_new_born || ',' ||  
	v_compl_num_facilities || ',' ||
	v_compl_num_vaccine_units || ',' ||
	v_compl_num_reports_received || ',' ||
	v_compl_num_reports_online || ',' ||
	v_compl_num_outreach_sessions || ',' ||  
	v_iec_num_sessions || ',' ||
	v_iec_num_participants || ',' ||
	v_iec_num_radio_spots || ',' ||
	v_iec_num_home_visits || ',' ||
	v_aefi_num_cases || ',' ||
  v_wastage_num_safety_boxes_used || ',' ||
  v_wastage_num_safety_boxes_disposed || ',' ||
	v_cca_num_reported_temp_status || ',' ||
	v_cca_num_temp_2_c || ',' ||
	v_cca_num_temp_8_c || ',' ||
	v_cca_min_temp || ',' ||
	v_cca_max_temp || ',' ||
	v_cca_num_temp_low_alarm || ',' ||
	v_cca_num_temp_high_alarm || ',' ||
	v_ccb_min_temp || ',' ||
	v_ccb_max_temp || ',' ||
	v_ccb_num_temp_low_alarm || ',' ||
	v_ccb_num_temp_high_alarm || ')';

END LOOP ;

-- return data table
q := '
 select 
	programid,
	geographiczoneid,
	district,
	periodid,
	periodname,
	year,
	demo_population,
	demo_surviving_infants_0_11_annual,
  demo_surviving_infants_0_11_monthly,
	demo_population_pregnant,
	demo_population_new_born,  
	compl_num_facilities,
	compl_num_vaccine_units,
	compl_num_reports_received,
	compl_num_reports_online,
	compl_num_outreach_sessions,  
	iec_num_sessions,
	iec_num_participants,
	iec_num_radio_spots,
	iec_num_home_visits,
	aefi_num_cases,
  wastage_num_safety_boxes_used,
  wastage_num_safety_boxes_disposed,
	cca_num_reported_temp_status,
	cca_num_temp_2_c,
	cca_num_temp_8_c,
	cca_min_temp,
	cca_max_temp,
	cca_num_temp_low_alarm,
	cca_num_temp_high_alarm,
	ccb_min_temp,
	ccb_max_temp,
	ccb_num_temp_low_alarm,
	ccb_num_temp_high_alarm
   from _data' ;

RETURN QUERY EXECUTE q ;
END ;

$$;


ALTER FUNCTION public.fn_vims_monthly_report_summary(in_programid integer, in_geographiczoneid integer, in_periodid integer) OWNER TO postgres;

--
-- Name: getrgprogramsupplyline(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION getrgprogramsupplyline() RETURNS TABLE(snode text, name text, requisitiongroup text)
    LANGUAGE plpgsql
    AS $$
  DECLARE
    requisitionGroupQuery VARCHAR;
    finalQuery            VARCHAR;
    ultimateParentRecord  RECORD;
    rowRG                 RECORD;
  BEGIN
    EXECUTE 'CREATE TEMP TABLE rg_supervisory_node (
            requisitionGroupId INTEGER, 
            requisitionGroup TEXT,
            supervisoryNodeId INTEGER, 
            sNode TEXT,
            programId INTEGER, 
            name TEXT,
            ultimateParentId INTEGER 
            ) ON COMMIT DROP';
    requisitionGroupQuery := 'SELECT RG.id, RG.code || '' '' || RG.name as requisitionGroup, RG.supervisoryNodeId, RGPS.programId, pg.name 
                              FROM requisition_groups AS RG INNER JOIN requisition_group_program_schedules AS RGPS ON RG.id = RGPS.requisitionGroupId 
                              INNER JOIN programs pg ON pg.id=RGPS.programid WHERE pg.active=true AND pg.push=false';
    FOR rowRG IN EXECUTE requisitionGroupQuery LOOP
    WITH RECURSIVE supervisoryNodesRec(id, sName, parentId, depth, path) AS
    (
      SELECT
        superNode.id,
        superNode.code || ' ' || superNode.name :: TEXT AS sName,
        superNode.parentId,
        1 :: INT                                        AS depth,
        superNode.id :: TEXT                            AS path
      FROM supervisory_nodes superNode
      WHERE id IN (rowRG.supervisoryNodeId)
      UNION
      SELECT
        sn.id,
        sn.code || ' ' || sn.name :: TEXT AS sName,
        sn.parentId,
        snRec.depth + 1                   AS depth,
        (snRec.path)
      FROM supervisory_nodes sn
        JOIN supervisoryNodesRec snRec
          ON sn.id = snRec.parentId
    )
    SELECT
      INTO ultimateParentRecord path  AS id,
                                id    AS ultimateParentId,
                                sName AS sNode
    FROM supervisoryNodesRec
    WHERE depth = (SELECT
                     max(depth)
                   FROM supervisoryNodesRec);
      EXECUTE
      'INSERT INTO rg_supervisory_node VALUES (' || rowRG.id || ',' ||
      quote_literal(rowRG.requisitionGroup) || ',' || rowRG.supervisoryNodeId ||
      ',' || quote_literal(ultimateParentRecord.sNode) || ',' || rowRG.programId
      || ',' || quote_literal(rowRG.name) || ',' ||
      ultimateParentRecord.ultimateParentId || ')';
    END LOOP;
    finalQuery := 'SELECT
                  RGS.snode            AS SupervisoryNode,
                  RGS.name             AS ProgramName,
                  RGS.requisitiongroup AS RequisitionGroup
                  FROM rg_supervisory_node AS RGS
                  WHERE NOT EXISTS
                  (SELECT
                     *
                   FROM supply_lines
                     INNER JOIN facilities f
                       ON f.id = supply_lines.supplyingFacilityId
                   WHERE supply_lines.supervisorynodeid = RGS.ultimateparentid AND
                         RGS.programid = supply_lines.programid AND f.enabled = TRUE)
                  ORDER BY SupervisoryNode, ProgramName, RequisitionGroup';
    RETURN QUERY EXECUTE finalQuery;
  END;
  $$;


ALTER FUNCTION public.getrgprogramsupplyline() OWNER TO postgres;

SET search_path = atomfeed, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: chunking_history; Type: TABLE; Schema: atomfeed; Owner: postgres; Tablespace: 
--

CREATE TABLE chunking_history (
    id integer NOT NULL,
    chunk_length bigint,
    start bigint NOT NULL
);


ALTER TABLE chunking_history OWNER TO postgres;

--
-- Name: chunking_history_id_seq; Type: SEQUENCE; Schema: atomfeed; Owner: postgres
--

CREATE SEQUENCE chunking_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE chunking_history_id_seq OWNER TO postgres;

--
-- Name: chunking_history_id_seq; Type: SEQUENCE OWNED BY; Schema: atomfeed; Owner: postgres
--

ALTER SEQUENCE chunking_history_id_seq OWNED BY chunking_history.id;


--
-- Name: event_records; Type: TABLE; Schema: atomfeed; Owner: postgres; Tablespace: 
--

CREATE TABLE event_records (
    id integer NOT NULL,
    uuid character varying(40),
    title character varying(255),
    "timestamp" timestamp without time zone DEFAULT now(),
    uri character varying(255),
    object character varying(5000),
    category character varying(255)
);


ALTER TABLE event_records OWNER TO postgres;

--
-- Name: event_records_id_seq; Type: SEQUENCE; Schema: atomfeed; Owner: postgres
--

CREATE SEQUENCE event_records_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE event_records_id_seq OWNER TO postgres;

--
-- Name: event_records_id_seq; Type: SEQUENCE OWNED BY; Schema: atomfeed; Owner: postgres
--

ALTER SEQUENCE event_records_id_seq OWNED BY event_records.id;


SET search_path = public, pg_catalog;

--
-- Name: adult_coverage_opened_vial_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE adult_coverage_opened_vial_line_items (
    id integer NOT NULL,
    facilityvisitid integer NOT NULL,
    productvialname character varying(255) NOT NULL,
    openedvials integer,
    packsize integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE adult_coverage_opened_vial_line_items OWNER TO postgres;

--
-- Name: adult_coverage_opened_vial_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE adult_coverage_opened_vial_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE adult_coverage_opened_vial_line_items_id_seq OWNER TO postgres;

--
-- Name: adult_coverage_opened_vial_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE adult_coverage_opened_vial_line_items_id_seq OWNED BY adult_coverage_opened_vial_line_items.id;


--
-- Name: alert_facility_stockedout; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_facility_stockedout (
    id integer NOT NULL,
    alertsummaryid integer,
    programid integer,
    periodid integer,
    geographiczoneid integer,
    geographiczonename character varying(250),
    facilityid integer,
    facilityname character varying(50),
    productid integer,
    productname character varying(150),
    stockoutdays integer,
    amc integer,
    modifieddate date
);


ALTER TABLE alert_facility_stockedout OWNER TO postgres;

--
-- Name: alert_facility_stockedout_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_facility_stockedout_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_facility_stockedout_id_seq OWNER TO postgres;

--
-- Name: alert_facility_stockedout_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_facility_stockedout_id_seq OWNED BY alert_facility_stockedout.id;


--
-- Name: alert_requisition_approved; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_requisition_approved (
    id integer NOT NULL,
    alertsummaryid integer,
    programid integer,
    periodid integer,
    geographiczoneid integer,
    geographiczonename character varying(250),
    rnrid integer,
    rnrtype character varying(50),
    facilityid integer,
    facilityname character varying(50),
    modifieddate date
);


ALTER TABLE alert_requisition_approved OWNER TO postgres;

--
-- Name: alert_requisition_approved_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_requisition_approved_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_requisition_approved_id_seq OWNER TO postgres;

--
-- Name: alert_requisition_approved_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_requisition_approved_id_seq OWNED BY alert_requisition_approved.id;


--
-- Name: alert_requisition_emergency; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_requisition_emergency (
    id integer NOT NULL,
    alertsummaryid integer,
    programid integer,
    periodid integer,
    geographiczoneid integer,
    geographiczonename character varying(250),
    rnrid integer,
    rnrtype character varying(50),
    facilityid integer,
    status character varying(50),
    facilityname character varying(50),
    modifieddate date
);


ALTER TABLE alert_requisition_emergency OWNER TO postgres;

--
-- Name: alert_requisition_emergency_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_requisition_emergency_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_requisition_emergency_id_seq OWNER TO postgres;

--
-- Name: alert_requisition_emergency_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_requisition_emergency_id_seq OWNED BY alert_requisition_emergency.id;


--
-- Name: alert_requisition_pending; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_requisition_pending (
    id integer NOT NULL,
    alertsummaryid integer,
    programid integer,
    periodid integer,
    geographiczoneid integer,
    geographiczonename character varying(250),
    rnrid integer,
    rnrtype character varying(50),
    facilityid integer,
    facilityname character varying(50),
    modifieddate date
);


ALTER TABLE alert_requisition_pending OWNER TO postgres;

--
-- Name: alert_requisition_pending_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_requisition_pending_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_requisition_pending_id_seq OWNER TO postgres;

--
-- Name: alert_requisition_pending_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_requisition_pending_id_seq OWNED BY alert_requisition_pending.id;


--
-- Name: alert_requisition_rejected; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_requisition_rejected (
    id integer NOT NULL,
    alertsummaryid integer,
    programid integer,
    periodid integer,
    geographiczoneid integer,
    geographiczonename character varying(250),
    rnrid integer,
    rnrtype character varying(50),
    facilityid integer,
    facilityname character varying(50),
    modifieddate date
);


ALTER TABLE alert_requisition_rejected OWNER TO postgres;

--
-- Name: alert_requisition_rejected_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_requisition_rejected_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_requisition_rejected_id_seq OWNER TO postgres;

--
-- Name: alert_requisition_rejected_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_requisition_rejected_id_seq OWNED BY alert_requisition_rejected.id;


--
-- Name: alert_stockedout; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_stockedout (
    id integer NOT NULL,
    alertsummaryid integer,
    facilityid integer,
    facilityname character varying(50),
    stockoutdays integer,
    amc integer,
    productid integer
);


ALTER TABLE alert_stockedout OWNER TO postgres;

--
-- Name: alert_stockedout_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_stockedout_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_stockedout_id_seq OWNER TO postgres;

--
-- Name: alert_stockedout_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_stockedout_id_seq OWNED BY alert_stockedout.id;


--
-- Name: alert_summary; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alert_summary (
    id integer NOT NULL,
    statics_value integer,
    description character varying(2000),
    geographiczoneid integer,
    alerttypeid character varying(50),
    programid integer,
    periodid integer,
    productid integer,
    modifieddate date
);


ALTER TABLE alert_summary OWNER TO postgres;

--
-- Name: alert_summary_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE alert_summary_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE alert_summary_id_seq OWNER TO postgres;

--
-- Name: alert_summary_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE alert_summary_id_seq OWNED BY alert_summary.id;


--
-- Name: alerts; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE alerts (
    alerttype character varying(50) NOT NULL,
    display_section character varying(50),
    email boolean,
    sms boolean,
    detail_table character varying(50),
    sms_msg_template_key character varying(250),
    email_msg_template_key character varying(250)
);


ALTER TABLE alerts OWNER TO postgres;

--
-- Name: budget_configuration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE budget_configuration (
    headerinfile boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE budget_configuration OWNER TO postgres;

--
-- Name: budget_file_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE budget_file_columns (
    id integer NOT NULL,
    name character varying(150) NOT NULL,
    datafieldlabel character varying(150),
    "position" integer,
    include boolean NOT NULL,
    mandatory boolean NOT NULL,
    datepattern character varying(25),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE budget_file_columns OWNER TO postgres;

--
-- Name: budget_file_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE budget_file_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE budget_file_columns_id_seq OWNER TO postgres;

--
-- Name: budget_file_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE budget_file_columns_id_seq OWNED BY budget_file_columns.id;


--
-- Name: budget_file_info; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE budget_file_info (
    id integer NOT NULL,
    filename character varying(200) NOT NULL,
    processingerror boolean DEFAULT false NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE budget_file_info OWNER TO postgres;

--
-- Name: budget_file_info_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE budget_file_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE budget_file_info_id_seq OWNER TO postgres;

--
-- Name: budget_file_info_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE budget_file_info_id_seq OWNED BY budget_file_info.id;


--
-- Name: budget_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE budget_line_items (
    id integer NOT NULL,
    periodid integer NOT NULL,
    budgetfileid integer NOT NULL,
    perioddate timestamp without time zone NOT NULL,
    allocatedbudget numeric(20,2) NOT NULL,
    notes character varying(255),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    facilityid integer NOT NULL,
    programid integer NOT NULL
);


ALTER TABLE budget_line_items OWNER TO postgres;

--
-- Name: budget_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE budget_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE budget_line_items_id_seq OWNER TO postgres;

--
-- Name: budget_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE budget_line_items_id_seq OWNED BY budget_line_items.id;


--
-- Name: child_coverage_opened_vial_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE child_coverage_opened_vial_line_items (
    id integer NOT NULL,
    facilityvisitid integer NOT NULL,
    productvialname character varying(255) NOT NULL,
    openedvials integer,
    packsize integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE child_coverage_opened_vial_line_items OWNER TO postgres;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE comments (
    id integer NOT NULL,
    rnrid integer NOT NULL,
    commenttext character varying(250) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer NOT NULL,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE comments OWNER TO postgres;

--
-- Name: comments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE comments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE comments_id_seq OWNER TO postgres;

--
-- Name: comments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE comments_id_seq OWNED BY comments.id;


--
-- Name: configurable_rnr_options; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE configurable_rnr_options (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    label character varying(200) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE configurable_rnr_options OWNER TO postgres;

--
-- Name: configurable_rnr_options_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE configurable_rnr_options_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE configurable_rnr_options_id_seq OWNER TO postgres;

--
-- Name: configurable_rnr_options_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE configurable_rnr_options_id_seq OWNED BY configurable_rnr_options.id;


--
-- Name: configuration_settings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE configuration_settings (
    id integer NOT NULL,
    key character varying(250) NOT NULL,
    value character varying(2000),
    name character varying(250) NOT NULL,
    description character varying(1000),
    groupname character varying(250) DEFAULT 'General'::character varying NOT NULL,
    displayorder integer DEFAULT 1 NOT NULL,
    valuetype character varying(250) DEFAULT 'TEXT'::character varying NOT NULL,
    valueoptions character varying(1000),
    isconfigurable boolean DEFAULT true
);


ALTER TABLE configuration_settings OWNER TO postgres;

--
-- Name: configuration_settings_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE configuration_settings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE configuration_settings_id_seq OWNER TO postgres;

--
-- Name: configuration_settings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE configuration_settings_id_seq OWNED BY configuration_settings.id;


--
-- Name: coverage_product_vials; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE coverage_product_vials (
    id integer NOT NULL,
    vial character varying(255) NOT NULL,
    productcode character varying(50) NOT NULL,
    childcoverage boolean NOT NULL
);


ALTER TABLE coverage_product_vials OWNER TO postgres;

--
-- Name: coverage_product_vials_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE coverage_product_vials_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coverage_product_vials_id_seq OWNER TO postgres;

--
-- Name: coverage_product_vials_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE coverage_product_vials_id_seq OWNED BY coverage_product_vials.id;


--
-- Name: coverage_target_group_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE coverage_target_group_products (
    id integer NOT NULL,
    targetgroupentity character varying(255) NOT NULL,
    productcode character varying(50) NOT NULL,
    childcoverage boolean NOT NULL
);


ALTER TABLE coverage_target_group_products OWNER TO postgres;

--
-- Name: coverage_vaccination_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE coverage_vaccination_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE coverage_vaccination_products_id_seq OWNER TO postgres;

--
-- Name: coverage_vaccination_products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE coverage_vaccination_products_id_seq OWNED BY coverage_target_group_products.id;


--
-- Name: custom_reports; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE custom_reports (
    id integer NOT NULL,
    reportkey character varying(50) NOT NULL,
    name character varying(50),
    description character varying(50),
    active boolean,
    createdby integer,
    help character varying(5000),
    filters character varying(5000),
    query character varying(5000),
    category character varying(5000),
    columnoptions character varying(5000),
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE custom_reports OWNER TO postgres;

--
-- Name: custom_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE custom_reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE custom_reports_id_seq OWNER TO postgres;

--
-- Name: custom_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE custom_reports_id_seq OWNED BY custom_reports.id;


--
-- Name: delivery_zone_members; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE delivery_zone_members (
    id integer NOT NULL,
    deliveryzoneid integer NOT NULL,
    facilityid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE delivery_zone_members OWNER TO postgres;

--
-- Name: delivery_zone_members_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE delivery_zone_members_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE delivery_zone_members_id_seq OWNER TO postgres;

--
-- Name: delivery_zone_members_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE delivery_zone_members_id_seq OWNED BY delivery_zone_members.id;


--
-- Name: delivery_zone_program_schedules; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE delivery_zone_program_schedules (
    id integer NOT NULL,
    deliveryzoneid integer NOT NULL,
    programid integer NOT NULL,
    scheduleid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE delivery_zone_program_schedules OWNER TO postgres;

--
-- Name: delivery_zone_program_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE delivery_zone_program_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE delivery_zone_program_schedules_id_seq OWNER TO postgres;

--
-- Name: delivery_zone_program_schedules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE delivery_zone_program_schedules_id_seq OWNED BY delivery_zone_program_schedules.id;


--
-- Name: delivery_zone_warehouses; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE delivery_zone_warehouses (
    id integer NOT NULL,
    deliveryzoneid integer NOT NULL,
    warehouseid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE delivery_zone_warehouses OWNER TO postgres;

--
-- Name: delivery_zone_warehouses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE delivery_zone_warehouses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE delivery_zone_warehouses_id_seq OWNER TO postgres;

--
-- Name: delivery_zone_warehouses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE delivery_zone_warehouses_id_seq OWNED BY delivery_zone_warehouses.id;


--
-- Name: delivery_zones; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE delivery_zones (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(250),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE delivery_zones OWNER TO postgres;

--
-- Name: delivery_zones_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE delivery_zones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE delivery_zones_id_seq OWNER TO postgres;

--
-- Name: delivery_zones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE delivery_zones_id_seq OWNED BY delivery_zones.id;


--
-- Name: demographic_estimate_categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE demographic_estimate_categories (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(1000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    isprimaryestimate boolean DEFAULT false NOT NULL,
    defaultconversionfactor numeric DEFAULT 1 NOT NULL
);


ALTER TABLE demographic_estimate_categories OWNER TO postgres;

--
-- Name: demographic_estimate_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE demographic_estimate_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE demographic_estimate_categories_id_seq OWNER TO postgres;

--
-- Name: demographic_estimate_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE demographic_estimate_categories_id_seq OWNED BY demographic_estimate_categories.id;


--
-- Name: refrigerator_readings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE refrigerator_readings (
    id integer NOT NULL,
    temperature numeric(4,1),
    functioningcorrectly character varying(1),
    lowalarmevents numeric(3,0),
    highalarmevents numeric(3,0),
    problemsincelasttime character varying(1),
    notes character varying(255),
    refrigeratorid integer NOT NULL,
    refrigeratorserialnumber character varying(30) NOT NULL,
    refrigeratorbrand character varying(20),
    refrigeratormodel character varying(20),
    facilityvisitid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE refrigerator_readings OWNER TO postgres;

--
-- Name: distribution_refrigerator_readings_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE distribution_refrigerator_readings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE distribution_refrigerator_readings_id_seq OWNER TO postgres;

--
-- Name: distribution_refrigerator_readings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE distribution_refrigerator_readings_id_seq OWNED BY refrigerator_readings.id;


--
-- Name: distributions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE distributions (
    id integer NOT NULL,
    deliveryzoneid integer NOT NULL,
    programid integer NOT NULL,
    periodid integer NOT NULL,
    status character varying(50),
    createdby integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer NOT NULL,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE distributions OWNER TO postgres;

--
-- Name: distributions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE distributions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE distributions_id_seq OWNER TO postgres;

--
-- Name: distributions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE distributions_id_seq OWNED BY distributions.id;


--
-- Name: district_demographic_estimates; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE district_demographic_estimates (
    id integer NOT NULL,
    year integer NOT NULL,
    districtid integer NOT NULL,
    demographicestimateid integer NOT NULL,
    conversionfactor numeric,
    value integer DEFAULT 0 NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    programid integer NOT NULL,
    isfinal boolean DEFAULT false NOT NULL
);


ALTER TABLE district_demographic_estimates OWNER TO postgres;

--
-- Name: district_demographic_estimates_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE district_demographic_estimates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE district_demographic_estimates_id_seq OWNER TO postgres;

--
-- Name: district_demographic_estimates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE district_demographic_estimates_id_seq OWNED BY district_demographic_estimates.id;


--
-- Name: donors; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE donors (
    id integer NOT NULL,
    shortname character varying(200) NOT NULL,
    longname character varying(200) NOT NULL,
    code character varying(50),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE donors OWNER TO postgres;

--
-- Name: donors_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE donors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE donors_id_seq OWNER TO postgres;

--
-- Name: donors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE donors_id_seq OWNED BY donors.id;


--
-- Name: dosage_frequencies; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dosage_frequencies (
    id integer NOT NULL,
    name character varying(20),
    numericquantityperday numeric
);


ALTER TABLE dosage_frequencies OWNER TO postgres;

--
-- Name: dosage_frequencies_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE dosage_frequencies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dosage_frequencies_id_seq OWNER TO postgres;

--
-- Name: dosage_frequencies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE dosage_frequencies_id_seq OWNED BY dosage_frequencies.id;


--
-- Name: dosage_units; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dosage_units (
    id integer NOT NULL,
    code character varying(20),
    displayorder integer,
    createddate timestamp without time zone DEFAULT now(),
    createdby integer,
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE dosage_units OWNER TO postgres;

--
-- Name: dosage_units_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE dosage_units_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE dosage_units_id_seq OWNER TO postgres;

--
-- Name: dosage_units_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE dosage_units_id_seq OWNED BY dosage_units.id;


--
-- Name: dw_orders; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE dw_orders (
    programid integer,
    programname character varying(50),
    scheduleid integer,
    schedulename character varying(50),
    periodid integer NOT NULL,
    processingperiodname character varying(250),
    geographiczoneid integer NOT NULL,
    geographiczonename character varying(250),
    supervisorynodeid integer,
    requisitiongroupid integer,
    requisitiongroupname character varying(50),
    facilitytypeid integer,
    facilitytypename character varying(50),
    facilityid integer NOT NULL,
    facilitycode character varying(50) NOT NULL,
    facilityname character varying(50),
    productcategoryid integer,
    productcategoryname character varying(150),
    productgroupid integer,
    productgroupname character varying(250),
    rnrid integer NOT NULL,
    emergency boolean,
    status character varying(20) NOT NULL,
    createddate timestamp without time zone,
    approveddate timestamp without time zone,
    shippeddate timestamp without time zone,
    receiveddate timestamp without time zone,
    initiateddate timestamp without time zone,
    submitteddate timestamp without time zone,
    authorizeddate timestamp without time zone,
    inapprovaldate timestamp without time zone,
    releaseddate timestamp without time zone,
    productid integer NOT NULL,
    productcode character varying(50) NOT NULL,
    productprimaryname character varying(150),
    productfullname character varying(250),
    quantityrequested integer,
    quantityapproved integer,
    quantityshipped integer,
    quantityreceived integer,
    soh integer,
    amc integer,
    mos numeric(6,1),
    stockedoutinpast boolean,
    suppliedinpast boolean,
    mossuppliedinpast numeric(6,1),
    stocking character(1),
    reporting character(1),
    modifieddate timestamp without time zone DEFAULT now(),
    tracer boolean,
    skipped boolean,
    stockoutdays integer DEFAULT 0,
    quantityapprovedprev integer,
    programcode character varying(80),
    periodstartdate date,
    periodenddate date,
    rnrmodifieddate date,
    openingbalance integer,
    adjustment integer,
    quantityordered integer,
    dateordered date,
    dateshipped date,
    dispensed integer,
    rmnch boolean DEFAULT false,
    sohprev integer,
    dispensedprev integer,
    amcprev integer,
    mosprev numeric(6,1),
    expirationdate character varying(10),
    price numeric(15,4),
    dispensedprev2 integer,
    sohprev2 integer,
    amcprev2 integer,
    mosprev2 numeric(6,1),
    dispensedprev3 integer,
    sohprev3 integer,
    amcprev3 integer,
    mosprev3 numeric(6,1),
    quantityexpired integer
);


ALTER TABLE dw_orders OWNER TO postgres;

--
-- Name: TABLE dw_orders; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE dw_orders IS 'stores data to calculate order fill rate and item fill rate
Definitions:
ORDER FILL RATE: Total number of products received / Total number of products approved Parameters: geograhic zone, facility, period
ITEM FILL RATE: Total qty received / Total qty approved. Parameter: geograhic zone, product, period
Joins:
requisitions, facilitities, products, requision_line_items, shipment_line_items,requisition_status_changes,pod
Fields and source:
geographic zone id - facilities table
facility id -- facilities table
period id -- requisitions tables
rnr id - requisitions
product id - requisition_line_items
quantity requested -- requisition_line_items
quantity approved -- requisition_line_items
quantity received -- requisition_line_items
date requisition created -
date requisition approved -
date requisition (order) shipped';


--
-- Name: COLUMN dw_orders.rmnch; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN dw_orders.rmnch IS 'true if product is part of RMNCH program';


--
-- Name: dw_order_fill_rate_vw; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW dw_order_fill_rate_vw AS
 SELECT dw_orders.programid,
    dw_orders.periodid,
    dw_orders.geographiczoneid,
    dw_orders.facilityid,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityapproved, 0) = 0) THEN (0)::numeric
            ELSE
            CASE
                WHEN (dw_orders.quantityapproved > 0) THEN (1)::numeric
                ELSE (0)::numeric
            END
        END) AS totalproductsapproved,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityreceived, 0) = 0) THEN (0)::numeric
            ELSE
            CASE
                WHEN (dw_orders.quantityreceived > 0) THEN (1)::numeric
                ELSE (0)::numeric
            END
        END) AS totalproductsreceived,
        CASE
            WHEN (COALESCE(sum(
            CASE
                WHEN (COALESCE(dw_orders.quantityapproved, 0) = 0) THEN (0)::numeric
                ELSE
                CASE
                    WHEN (dw_orders.quantityapproved > 0) THEN (1)::numeric
                    ELSE (0)::numeric
                END
            END), (0)::numeric) = (0)::numeric) THEN (0)::numeric
            ELSE ((sum(
            CASE
                WHEN (COALESCE(dw_orders.quantityreceived, 0) = 0) THEN (0)::numeric
                ELSE
                CASE
                    WHEN (dw_orders.quantityreceived > 0) THEN (1)::numeric
                    ELSE (0)::numeric
                END
            END) / sum(
            CASE
                WHEN (COALESCE(dw_orders.quantityapproved, 0) = 0) THEN (0)::numeric
                ELSE
                CASE
                    WHEN (dw_orders.quantityapproved > 0) THEN (1)::numeric
                    ELSE (0)::numeric
                END
            END)) * (100)::numeric)
        END AS order_fill_rate
   FROM dw_orders
  WHERE ((dw_orders.status)::text = ANY (ARRAY[('APPROVED'::character varying)::text, ('RELEASED'::character varying)::text]))
  GROUP BY dw_orders.programid, dw_orders.periodid, dw_orders.geographiczoneid, dw_orders.facilityid;


ALTER TABLE dw_order_fill_rate_vw OWNER TO postgres;

--
-- Name: facilities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facilities (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(250),
    gln character varying(30),
    mainphone character varying(20),
    fax character varying(20),
    address1 character varying(50),
    address2 character varying(50),
    geographiczoneid integer NOT NULL,
    typeid integer NOT NULL,
    catchmentpopulation integer,
    latitude numeric(8,5),
    longitude numeric(8,5),
    altitude numeric(8,4),
    operatedbyid integer,
    coldstoragegrosscapacity numeric(8,4),
    coldstoragenetcapacity numeric(8,4),
    suppliesothers boolean,
    sdp boolean NOT NULL,
    online boolean,
    satellite boolean,
    parentfacilityid integer,
    haselectricity boolean,
    haselectronicscc boolean,
    haselectronicdar boolean,
    active boolean NOT NULL,
    golivedate date NOT NULL,
    godowndate date,
    comment text,
    enabled boolean NOT NULL,
    virtualfacility boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    pricescheduleid integer
);


ALTER TABLE facilities OWNER TO postgres;

--
-- Name: facility_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_types (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(30) NOT NULL,
    description character varying(250),
    levelid integer,
    nominalmaxmonth integer NOT NULL,
    nominaleop numeric(4,2) NOT NULL,
    displayorder integer,
    active boolean,
    createddate timestamp without time zone DEFAULT now(),
    createdby integer,
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE facility_types OWNER TO postgres;

--
-- Name: geographic_zones; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE geographic_zones (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(250) NOT NULL,
    levelid integer NOT NULL,
    parentid integer,
    catchmentpopulation integer,
    latitude numeric(8,5),
    longitude numeric(8,5),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE geographic_zones OWNER TO postgres;

--
-- Name: processing_periods; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE processing_periods (
    id integer NOT NULL,
    scheduleid integer NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(250),
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone NOT NULL,
    numberofmonths integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE processing_periods OWNER TO postgres;

--
-- Name: processing_schedules; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE processing_schedules (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(250),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE processing_schedules OWNER TO postgres;

--
-- Name: product_categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product_categories (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL,
    displayorder integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE product_categories OWNER TO postgres;

--
-- Name: products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE products (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    alternateitemcode character varying(20),
    manufacturer character varying(100),
    manufacturercode character varying(30),
    manufacturerbarcode character varying(20),
    mohbarcode character varying(20),
    gtin character varying(20),
    type character varying(100),
    primaryname character varying(150) NOT NULL,
    fullname character varying(250),
    genericname character varying(100),
    alternatename character varying(100),
    description character varying(250),
    strength character varying(14),
    formid integer,
    dosageunitid integer,
    productgroupid integer,
    dispensingunit character varying(20) NOT NULL,
    dosesperdispensingunit smallint NOT NULL,
    packsize smallint NOT NULL,
    alternatepacksize smallint,
    storerefrigerated boolean,
    storeroomtemperature boolean,
    hazardous boolean,
    flammable boolean,
    controlledsubstance boolean,
    lightsensitive boolean,
    approvedbywho boolean,
    contraceptivecyp numeric(8,4),
    packlength numeric(8,4),
    packwidth numeric(8,4),
    packheight numeric(8,4),
    packweight numeric(8,4),
    packspercarton smallint,
    cartonlength numeric(8,4),
    cartonwidth numeric(8,4),
    cartonheight numeric(8,4),
    cartonsperpallet smallint,
    expectedshelflife smallint,
    specialstorageinstructions text,
    specialtransportinstructions text,
    active boolean NOT NULL,
    fullsupply boolean NOT NULL,
    tracer boolean NOT NULL,
    roundtozero boolean NOT NULL,
    archived boolean,
    packroundingthreshold smallint NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE products OWNER TO postgres;

--
-- Name: program_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE program_products (
    id integer NOT NULL,
    programid integer NOT NULL,
    productid integer NOT NULL,
    dosespermonth integer NOT NULL,
    active boolean NOT NULL,
    currentprice numeric(20,2) DEFAULT 0,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    productcategoryid integer NOT NULL,
    displayorder integer,
    fullsupply boolean,
    isacoefficientsid integer
);


ALTER TABLE program_products OWNER TO postgres;

--
-- Name: programs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE programs (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50),
    description character varying(50),
    active boolean,
    templateconfigured boolean,
    regimentemplateconfigured boolean,
    budgetingapplies boolean DEFAULT false NOT NULL,
    usesdar boolean,
    push boolean DEFAULT false,
    sendfeed boolean DEFAULT false,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    isequipmentconfigured boolean DEFAULT false,
    hideskippedproducts boolean DEFAULT false NOT NULL,
    shownonfullsupplytab boolean DEFAULT true NOT NULL,
    enableskipperiod boolean DEFAULT false NOT NULL,
    enableivdform boolean DEFAULT false NOT NULL,
    usepriceschedule boolean DEFAULT false NOT NULL
);


ALTER TABLE programs OWNER TO postgres;

--
-- Name: requisition_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_line_items (
    id integer NOT NULL,
    rnrid integer NOT NULL,
    productcode character varying(50) NOT NULL,
    product character varying(250),
    productdisplayorder integer,
    productcategory character varying(100),
    productcategorydisplayorder integer,
    dispensingunit character varying(20) NOT NULL,
    beginningbalance integer,
    quantityreceived integer,
    quantitydispensed integer,
    stockinhand integer,
    quantityrequested integer,
    reasonforrequestedquantity text,
    calculatedorderquantity integer,
    quantityapproved integer,
    totallossesandadjustments integer,
    newpatientcount integer,
    stockoutdays integer,
    normalizedconsumption integer,
    amc integer,
    maxmonthsofstock integer NOT NULL,
    maxstockquantity integer,
    packstoship integer,
    price numeric(15,4),
    expirationdate character varying(10),
    remarks text,
    dosespermonth integer NOT NULL,
    dosesperdispensingunit integer NOT NULL,
    packsize smallint NOT NULL,
    roundtozero boolean,
    packroundingthreshold integer,
    fullsupply boolean NOT NULL,
    skipped boolean DEFAULT false NOT NULL,
    reportingdays integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    previousnormalizedconsumptions character varying(25) DEFAULT '[]'::character varying,
    previousstockinhand integer,
    periodnormalizedconsumption integer
);


ALTER TABLE requisition_line_items OWNER TO postgres;

--
-- Name: requisitions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisitions (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    programid integer NOT NULL,
    periodid integer NOT NULL,
    status character varying(20) NOT NULL,
    emergency boolean DEFAULT false NOT NULL,
    fullsupplyitemssubmittedcost numeric(15,2),
    nonfullsupplyitemssubmittedcost numeric(15,2),
    supervisorynodeid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    allocatedbudget numeric(20,2),
    clientsubmittedtime timestamp without time zone,
    clientsubmittednotes character varying(256)
);


ALTER TABLE requisitions OWNER TO postgres;

--
-- Name: vw_stock_status_2; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_stock_status_2 AS
 SELECT facilities.code AS facilitycode,
    products.code AS productcode,
    facilities.name AS facility,
    requisitions.status AS req_status,
    requisition_line_items.product,
    requisition_line_items.stockinhand,
    ((((requisition_line_items.stockinhand + requisition_line_items.beginningbalance) + requisition_line_items.quantitydispensed) + requisition_line_items.quantityreceived) + abs(requisition_line_items.totallossesandadjustments)) AS reported_figures,
    requisitions.id AS rnrid,
    requisition_line_items.amc,
        CASE
            WHEN (COALESCE(requisition_line_items.amc, 0) = 0) THEN (0)::numeric
            ELSE round((((requisition_line_items.stockinhand)::double precision / (requisition_line_items.amc)::double precision))::numeric, 2)
        END AS mos,
    COALESCE(
        CASE
            WHEN (((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) - requisition_line_items.stockinhand) < 0) THEN 0
            ELSE ((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) - requisition_line_items.stockinhand)
        END, 0) AS required,
        CASE
            WHEN (requisition_line_items.stockinhand = 0) THEN 'SO'::text
            ELSE
            CASE
                WHEN ((requisition_line_items.stockinhand > 0) AND ((requisition_line_items.stockinhand)::numeric <= ((COALESCE(requisition_line_items.amc, 0))::numeric * facility_types.nominaleop))) THEN 'US'::text
                ELSE
                CASE
                    WHEN (requisition_line_items.stockinhand > (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth)) THEN 'OS'::text
                    ELSE 'SP'::text
                END
            END
        END AS status,
    facility_types.name AS facilitytypename,
    geographic_zones.id AS gz_id,
    geographic_zones.name AS location,
    products.id AS productid,
    processing_periods.startdate,
    programs.id AS programid,
    processing_schedules.id AS psid,
    processing_periods.enddate,
    processing_periods.id AS periodid,
    facility_types.id AS facilitytypeid,
    program_products.productcategoryid AS categoryid,
    products.tracer AS indicator_product,
    facilities.id AS facility_id,
    processing_periods.name AS processing_period_name,
    requisition_line_items.stockoutdays,
    0 AS supervisorynodeid
   FROM ((((((((((requisition_line_items
     JOIN requisitions ON ((requisitions.id = requisition_line_items.rnrid)))
     JOIN facilities ON ((facilities.id = requisitions.facilityid)))
     JOIN facility_types ON ((facility_types.id = facilities.typeid)))
     JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
     JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
     JOIN products ON (((products.code)::text = (requisition_line_items.productcode)::text)))
     JOIN program_products ON (((requisitions.programid = program_products.programid) AND (products.id = program_products.productid))))
     JOIN product_categories ON ((product_categories.id = program_products.productcategoryid)))
     JOIN programs ON ((programs.id = requisitions.programid)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
  WHERE ((requisition_line_items.stockinhand IS NOT NULL) AND (requisition_line_items.skipped = false));


ALTER TABLE vw_stock_status_2 OWNER TO postgres;

--
-- Name: dw_product_facility_stock_info_vw; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW dw_product_facility_stock_info_vw AS
 SELECT 0 AS requisitiongroupid,
    vw_stock_status_2.programid,
    vw_stock_status_2.periodid,
    vw_stock_status_2.gz_id AS geographiczoneid,
    vw_stock_status_2.location AS geographiczonename,
    vw_stock_status_2.facility_id AS facilityid,
    vw_stock_status_2.facility AS facilityname,
    vw_stock_status_2.facilitycode,
    vw_stock_status_2.productid,
    vw_stock_status_2.product AS primaryname,
    vw_stock_status_2.amc,
    vw_stock_status_2.stockinhand AS soh,
    vw_stock_status_2.mos,
    vw_stock_status_2.status,
        CASE vw_stock_status_2.status
            WHEN 'SP'::text THEN 'A'::text
            WHEN 'OS'::text THEN 'O'::text
            WHEN 'US'::text THEN 'U'::text
            WHEN 'SO'::text THEN 'S'::text
            ELSE NULL::text
        END AS stocking
   FROM vw_stock_status_2
  ORDER BY vw_stock_status_2.gz_id, vw_stock_status_2.programid, vw_stock_status_2.periodid, vw_stock_status_2.productid, vw_stock_status_2.product, vw_stock_status_2.status;


ALTER TABLE dw_product_facility_stock_info_vw OWNER TO postgres;

--
-- Name: dw_product_fill_rate_vw; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW dw_product_fill_rate_vw AS
 SELECT dw_orders.programid,
    dw_orders.periodid,
    dw_orders.geographiczoneid,
    dw_orders.facilityid,
    dw_orders.productid,
    products.primaryname,
    sum((COALESCE(dw_orders.quantityapproved, 0))::numeric) AS quantityapproved,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityreceived, 0) = 0) THEN (dw_orders.quantityshipped)::numeric
            ELSE (COALESCE(dw_orders.quantityreceived, 0))::numeric
        END) AS quantityreceived,
        CASE
            WHEN (COALESCE(sum((COALESCE(dw_orders.quantityapproved, 0))::numeric), (0)::numeric) = (0)::numeric) THEN (0)::numeric
            ELSE round(((sum(
            CASE
                WHEN (COALESCE(dw_orders.quantityreceived, 0) = 0) THEN (dw_orders.quantityshipped)::numeric
                ELSE (COALESCE(dw_orders.quantityreceived, 0))::numeric
            END) / sum((COALESCE(dw_orders.quantityapproved, 0))::numeric)) * (100)::numeric), 2)
        END AS order_fill_rate
   FROM (dw_orders
     JOIN products ON ((products.id = dw_orders.productid)))
  WHERE ((dw_orders.status)::text = ANY (ARRAY[('APPROVED'::character varying)::text, ('RELEASED'::character varying)::text]))
  GROUP BY dw_orders.programid, dw_orders.periodid, dw_orders.geographiczoneid, dw_orders.facilityid, dw_orders.productid, products.primaryname;


ALTER TABLE dw_product_fill_rate_vw OWNER TO postgres;

--
-- Name: dw_product_lead_time_vw; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW dw_product_lead_time_vw AS
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
   FROM (dw_orders
     JOIN facilities ON ((facilities.id = dw_orders.facilityid)))
  WHERE ((dw_orders.status)::text = ('RELEASED'::character varying)::text)
  GROUP BY dw_orders.programid, dw_orders.geographiczoneid, dw_orders.periodid, facilities.name, facilities.code, dw_orders.facilityid;


ALTER TABLE dw_product_lead_time_vw OWNER TO postgres;

--
-- Name: VIEW dw_product_lead_time_vw; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON VIEW dw_product_lead_time_vw IS 'dw_product_lead_time_vw-
calculate product shipping lead time - Total days from the day order submitted to received
Filters: Geographic zone id (district), periodid, program
created March 14, 2014 wolde';


--
-- Name: elmis_help; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE elmis_help (
    name character varying(500),
    modifiedby integer,
    htmlcontent character varying(2000),
    imagelink character varying(100),
    createddate date,
    id integer NOT NULL,
    createdby integer,
    modifieddate date,
    helptopicid integer
);


ALTER TABLE elmis_help OWNER TO postgres;

--
-- Name: elmis_help_document; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE elmis_help_document (
    id integer NOT NULL,
    document_type character varying(20),
    url character varying(100),
    created_date date,
    modified_date date,
    created_by integer,
    modified_by integer
);


ALTER TABLE elmis_help_document OWNER TO postgres;

--
-- Name: elmis_help_document_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE elmis_help_document_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE elmis_help_document_id_seq OWNER TO postgres;

--
-- Name: elmis_help_document_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE elmis_help_document_id_seq OWNED BY elmis_help_document.id;


--
-- Name: elmis_help_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE elmis_help_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE elmis_help_id_seq OWNER TO postgres;

--
-- Name: elmis_help_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE elmis_help_id_seq OWNED BY elmis_help.id;


--
-- Name: elmis_help_topic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE elmis_help_topic (
    level integer,
    name character varying(200),
    created_by integer,
    createddate date,
    modifiedby integer,
    modifieddate date,
    id integer NOT NULL,
    parent_help_topic_id integer,
    is_category boolean DEFAULT true,
    html_content character varying(50000)
);


ALTER TABLE elmis_help_topic OWNER TO postgres;

--
-- Name: elmis_help_topic_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE elmis_help_topic_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE elmis_help_topic_id_seq OWNER TO postgres;

--
-- Name: elmis_help_topic_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE elmis_help_topic_id_seq OWNED BY elmis_help_topic.id;


--
-- Name: elmis_help_topic_roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE elmis_help_topic_roles (
    id integer NOT NULL,
    help_topic_id integer,
    role_id integer,
    is_asigned boolean DEFAULT true,
    was_previosly_assigned boolean DEFAULT true,
    created_by integer,
    createddate date,
    modifiedby integer,
    modifieddate date
);


ALTER TABLE elmis_help_topic_roles OWNER TO postgres;

--
-- Name: elmis_help_topic_roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE elmis_help_topic_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE elmis_help_topic_roles_id_seq OWNER TO postgres;

--
-- Name: elmis_help_topic_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE elmis_help_topic_roles_id_seq OWNED BY elmis_help_topic_roles.id;


--
-- Name: email_attachments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE email_attachments (
    id integer NOT NULL,
    attachmentname character varying(255) NOT NULL,
    attachmentpath character varying(510) NOT NULL,
    attachmentfiletype character varying(255) NOT NULL,
    createddate timestamp without time zone DEFAULT now()
);


ALTER TABLE email_attachments OWNER TO postgres;

--
-- Name: email_attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE email_attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE email_attachments_id_seq OWNER TO postgres;

--
-- Name: email_attachments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE email_attachments_id_seq OWNED BY email_attachments.id;


--
-- Name: email_attachments_relation; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE email_attachments_relation (
    emailid integer NOT NULL,
    attachmentid integer NOT NULL
);


ALTER TABLE email_attachments_relation OWNER TO postgres;

--
-- Name: email_notifications; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE email_notifications (
    id integer NOT NULL,
    receiver character varying(250) NOT NULL,
    subject text,
    content text,
    sent boolean DEFAULT false NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    ishtml boolean DEFAULT false NOT NULL
);


ALTER TABLE email_notifications OWNER TO postgres;

--
-- Name: email_notifications_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE email_notifications_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE email_notifications_id_seq OWNER TO postgres;

--
-- Name: email_notifications_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE email_notifications_id_seq OWNED BY email_notifications.id;


--
-- Name: emergency_requisitions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE emergency_requisitions (
    id integer NOT NULL,
    alertsummaryid integer,
    rnrid integer,
    facilityid integer,
    status character varying(50)
);


ALTER TABLE emergency_requisitions OWNER TO postgres;

--
-- Name: emergency_requisitions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE emergency_requisitions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE emergency_requisitions_id_seq OWNER TO postgres;

--
-- Name: emergency_requisitions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE emergency_requisitions_id_seq OWNED BY emergency_requisitions.id;


--
-- Name: epi_inventory_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE epi_inventory_line_items (
    id integer NOT NULL,
    productname character varying(250),
    idealquantity numeric,
    existingquantity numeric(7,0),
    spoiledquantity numeric(7,0),
    deliveredquantity numeric(7,0),
    facilityvisitid integer NOT NULL,
    productcode character varying(50) NOT NULL,
    productdisplayorder integer,
    programproductid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    idealquantitybypacksize numeric
);


ALTER TABLE epi_inventory_line_items OWNER TO postgres;

--
-- Name: epi_inventory_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE epi_inventory_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE epi_inventory_line_items_id_seq OWNER TO postgres;

--
-- Name: epi_inventory_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE epi_inventory_line_items_id_seq OWNED BY epi_inventory_line_items.id;


--
-- Name: epi_use_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE epi_use_line_items (
    id integer NOT NULL,
    productgroupid integer,
    productgroupname character varying(250),
    stockatfirstofmonth numeric(7,0),
    received numeric(7,0),
    distributed numeric(7,0),
    loss numeric(7,0),
    stockatendofmonth numeric(7,0),
    expirationdate character varying(10),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    facilityvisitid integer NOT NULL
);


ALTER TABLE epi_use_line_items OWNER TO postgres;

--
-- Name: epi_use_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE epi_use_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE epi_use_line_items_id_seq OWNER TO postgres;

--
-- Name: epi_use_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE epi_use_line_items_id_seq OWNED BY epi_use_line_items.id;


--
-- Name: equipment_cold_chain_equipment_designations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_cold_chain_equipment_designations (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    "hasEnergy" boolean DEFAULT true,
    "isRefrigerator" boolean,
    "isFreezer" boolean
);


ALTER TABLE equipment_cold_chain_equipment_designations OWNER TO postgres;

--
-- Name: equipment_cold_chain_equipment_designations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_cold_chain_equipment_designations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_cold_chain_equipment_designations_id_seq OWNER TO postgres;

--
-- Name: equipment_cold_chain_equipment_designations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_cold_chain_equipment_designations_id_seq OWNED BY equipment_cold_chain_equipment_designations.id;


--
-- Name: equipment_energy_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_energy_types (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_energy_types OWNER TO postgres;

--
-- Name: equipment_cold_chain_equipment_energy_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_cold_chain_equipment_energy_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_cold_chain_equipment_energy_types_id_seq OWNER TO postgres;

--
-- Name: equipment_cold_chain_equipment_energy_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_cold_chain_equipment_energy_types_id_seq OWNED BY equipment_energy_types.id;


--
-- Name: equipment_cold_chain_equipment_pqs_status; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_cold_chain_equipment_pqs_status (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_cold_chain_equipment_pqs_status OWNER TO postgres;

--
-- Name: equipment_cold_chain_equipment_pqs_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_cold_chain_equipment_pqs_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_cold_chain_equipment_pqs_status_id_seq OWNER TO postgres;

--
-- Name: equipment_cold_chain_equipment_pqs_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_cold_chain_equipment_pqs_status_id_seq OWNED BY equipment_cold_chain_equipment_pqs_status.id;


--
-- Name: equipment_cold_chain_equipments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_cold_chain_equipments (
    equipmentid integer NOT NULL,
    designationid integer NOT NULL,
    ccecode character varying(200),
    pqscode character varying(200),
    refrigeratorcapacity numeric(8,2),
    freezercapacity numeric(8,2),
    refrigerant character varying(200),
    temperaturezone character varying(200),
    maxtemperature integer,
    mintemperature integer,
    holdovertime character varying(200),
    energyconsumption character varying(200),
    dimension character varying(200),
    price numeric(18,2),
    pqsstatusid integer NOT NULL,
    donorid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    capacity numeric(8,2)
);


ALTER TABLE equipment_cold_chain_equipments OWNER TO postgres;

--
-- Name: equipment_contract_service_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_contract_service_types (
    id integer NOT NULL,
    contractid integer,
    servicetypeid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_contract_service_types OWNER TO postgres;

--
-- Name: equipment_contract_service_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_contract_service_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_contract_service_types_id_seq OWNER TO postgres;

--
-- Name: equipment_contract_service_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_contract_service_types_id_seq OWNED BY equipment_contract_service_types.id;


--
-- Name: equipment_inventories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_inventories (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    programid integer NOT NULL,
    equipmentid integer NOT NULL,
    serialnumber character varying(200),
    yearofinstallation integer DEFAULT 1900,
    purchaseprice numeric(18,3) DEFAULT 0,
    sourceoffund character varying(200),
    replacementrecommended boolean DEFAULT false,
    reasonforreplacement character varying(2000),
    nameofassessor character varying(200),
    datelastassessed date DEFAULT now(),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    isactive boolean DEFAULT true NOT NULL,
    datedecommissioned date,
    primarydonorid integer,
    hasstabilizer boolean
);


ALTER TABLE equipment_inventories OWNER TO postgres;

--
-- Name: equipment_inventory_statuses; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_inventory_statuses (
    id integer NOT NULL,
    inventoryid integer NOT NULL,
    statusid integer NOT NULL,
    notfunctionalstatusid integer,
    createddate timestamp with time zone DEFAULT now(),
    createdby integer,
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE equipment_inventory_statuses OWNER TO postgres;

--
-- Name: equipment_inventory_statuses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_inventory_statuses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_inventory_statuses_id_seq OWNER TO postgres;

--
-- Name: equipment_inventory_statuses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_inventory_statuses_id_seq OWNED BY equipment_inventory_statuses.id;


--
-- Name: equipment_maintenance_logs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_maintenance_logs (
    id integer NOT NULL,
    userid integer NOT NULL,
    vendorid integer NOT NULL,
    contractid integer NOT NULL,
    facilityid integer NOT NULL,
    equipmentid integer,
    maintenancedate date,
    serviceperformed character varying(2000),
    finding character varying(2000),
    recommendation character varying(2000),
    requestid integer,
    nextvisitdate date,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_maintenance_logs OWNER TO postgres;

--
-- Name: equipment_maintenance_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_maintenance_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_maintenance_logs_id_seq OWNER TO postgres;

--
-- Name: equipment_maintenance_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_maintenance_logs_id_seq OWNED BY equipment_maintenance_logs.id;


--
-- Name: equipment_maintenance_requests; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_maintenance_requests (
    id integer NOT NULL,
    userid integer NOT NULL,
    facilityid integer NOT NULL,
    inventoryid integer NOT NULL,
    vendorid integer,
    requestdate date,
    reason character varying(2000),
    recommendeddate date,
    comment character varying(2000),
    resolved boolean DEFAULT false NOT NULL,
    vendorcomment character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_maintenance_requests OWNER TO postgres;

--
-- Name: equipment_maintenance_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_maintenance_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_maintenance_requests_id_seq OWNER TO postgres;

--
-- Name: equipment_maintenance_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_maintenance_requests_id_seq OWNED BY equipment_maintenance_requests.id;


--
-- Name: equipment_operational_status; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_operational_status (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    displayorder integer DEFAULT 0 NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    category text,
    isbad boolean
);


ALTER TABLE equipment_operational_status OWNER TO postgres;

--
-- Name: equipment_operational_status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_operational_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_operational_status_id_seq OWNER TO postgres;

--
-- Name: equipment_operational_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_operational_status_id_seq OWNED BY equipment_operational_status.id;


--
-- Name: equipment_service_contract_equipment_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_service_contract_equipment_types (
    id integer NOT NULL,
    contractid integer NOT NULL,
    equipmenttypeid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_service_contract_equipment_types OWNER TO postgres;

--
-- Name: equipment_service_contract_equipments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_service_contract_equipments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_service_contract_equipments_id_seq OWNER TO postgres;

--
-- Name: equipment_service_contract_equipments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_service_contract_equipments_id_seq OWNED BY equipment_service_contract_equipment_types.id;


--
-- Name: equipment_service_contract_facilities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_service_contract_facilities (
    id integer NOT NULL,
    contractid integer,
    facilityid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_service_contract_facilities OWNER TO postgres;

--
-- Name: equipment_service_contract_facilities_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_service_contract_facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_service_contract_facilities_id_seq OWNER TO postgres;

--
-- Name: equipment_service_contract_facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_service_contract_facilities_id_seq OWNED BY equipment_service_contract_facilities.id;


--
-- Name: equipment_service_contracts; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_service_contracts (
    id integer NOT NULL,
    vendorid integer NOT NULL,
    identifier character varying(1000) NOT NULL,
    startdate date,
    enddate date,
    description character varying(2000),
    terms character varying(2000),
    coverage character varying(2000),
    contractdate date,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_service_contracts OWNER TO postgres;

--
-- Name: equipment_service_contracts_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_service_contracts_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_service_contracts_id_seq OWNER TO postgres;

--
-- Name: equipment_service_contracts_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_service_contracts_id_seq OWNED BY equipment_service_contracts.id;


--
-- Name: equipment_service_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_service_types (
    id integer NOT NULL,
    name character varying(1000) NOT NULL,
    description character varying(2000) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_service_types OWNER TO postgres;

--
-- Name: equipment_service_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_service_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_service_types_id_seq OWNER TO postgres;

--
-- Name: equipment_service_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_service_types_id_seq OWNED BY equipment_service_types.id;


--
-- Name: equipment_service_vendor_users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_service_vendor_users (
    id integer NOT NULL,
    userid integer NOT NULL,
    vendorid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_service_vendor_users OWNER TO postgres;

--
-- Name: equipment_service_vendor_users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_service_vendor_users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_service_vendor_users_id_seq OWNER TO postgres;

--
-- Name: equipment_service_vendor_users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_service_vendor_users_id_seq OWNED BY equipment_service_vendor_users.id;


--
-- Name: equipment_service_vendors; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_service_vendors (
    id integer NOT NULL,
    name character varying(1000) NOT NULL,
    website character varying(1000) NOT NULL,
    contactperson character varying(200),
    primaryphone character varying(20),
    email character varying(200),
    description character varying(2000),
    specialization character varying(2000),
    geographiccoverage character varying(2000),
    registrationdate date,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_service_vendors OWNER TO postgres;

--
-- Name: equipment_service_vendors_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_service_vendors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_service_vendors_id_seq OWNER TO postgres;

--
-- Name: equipment_service_vendors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_service_vendors_id_seq OWNED BY equipment_service_vendors.id;


--
-- Name: equipment_status_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_status_line_items (
    id integer NOT NULL,
    rnrid integer NOT NULL,
    code character varying(200) NOT NULL,
    equipmentname character varying(200) NOT NULL,
    equipmentcategory character varying(200) NOT NULL,
    equipmentserial character varying(200),
    equipmentinventoryid integer NOT NULL,
    testcount integer,
    totalcount integer,
    daysoutofuse integer NOT NULL,
    remarks character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    inventorystatusid integer NOT NULL
);


ALTER TABLE equipment_status_line_items OWNER TO postgres;

--
-- Name: equipment_status_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_status_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_status_line_items_id_seq OWNER TO postgres;

--
-- Name: equipment_status_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_status_line_items_id_seq OWNED BY equipment_status_line_items.id;


--
-- Name: equipment_type_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_type_products (
    id integer NOT NULL,
    programequipmenttypeid integer NOT NULL,
    productid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_type_products OWNER TO postgres;

--
-- Name: equipment_type_programs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_type_programs (
    id integer NOT NULL,
    programid integer NOT NULL,
    equipmenttypeid integer NOT NULL,
    displayorder integer NOT NULL,
    enabletestcount boolean DEFAULT false,
    enabletotalcolumn boolean DEFAULT false,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE equipment_type_programs OWNER TO postgres;

--
-- Name: equipment_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipment_types (
    id integer NOT NULL,
    code character varying(20) NOT NULL,
    name character varying(200),
    displayorder integer DEFAULT 0 NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    iscoldchain boolean
);


ALTER TABLE equipment_types OWNER TO postgres;

--
-- Name: equipment_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipment_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipment_types_id_seq OWNER TO postgres;

--
-- Name: equipment_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipment_types_id_seq OWNED BY equipment_types.id;


--
-- Name: equipments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE equipments (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    equipmenttypeid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    manufacturer character varying(200),
    model character varying(200),
    energytypeid integer
);


ALTER TABLE equipments OWNER TO postgres;

--
-- Name: equipments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE equipments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE equipments_id_seq OWNER TO postgres;

--
-- Name: equipments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE equipments_id_seq OWNED BY equipments.id;


--
-- Name: facilities_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facilities_id_seq OWNER TO postgres;

--
-- Name: facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facilities_id_seq OWNED BY facilities.id;


--
-- Name: facility_approved_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_approved_products (
    id integer NOT NULL,
    facilitytypeid integer NOT NULL,
    programproductid integer NOT NULL,
    maxmonthsofstock integer NOT NULL,
    minmonthsofstock numeric(4,2),
    eop numeric(4,2),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE facility_approved_products OWNER TO postgres;

--
-- Name: facility_approved_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_approved_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_approved_products_id_seq OWNER TO postgres;

--
-- Name: facility_approved_products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_approved_products_id_seq OWNED BY facility_approved_products.id;


--
-- Name: facility_demographic_estimates; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_demographic_estimates (
    id integer NOT NULL,
    year integer NOT NULL,
    facilityid integer NOT NULL,
    demographicestimateid integer NOT NULL,
    conversionfactor numeric,
    value integer DEFAULT 0 NOT NULL,
    programid integer NOT NULL,
    isfinal boolean DEFAULT false NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone
);


ALTER TABLE facility_demographic_estimates OWNER TO postgres;

--
-- Name: facility_demographic_estimates_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_demographic_estimates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_demographic_estimates_id_seq OWNER TO postgres;

--
-- Name: facility_demographic_estimates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_demographic_estimates_id_seq OWNED BY facility_demographic_estimates.id;


--
-- Name: facility_ftp_details; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_ftp_details (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    serverhost character varying(100) NOT NULL,
    serverport character varying(10) NOT NULL,
    username character varying(100) NOT NULL,
    password character varying(50) NOT NULL,
    localfolderpath character varying(255) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE facility_ftp_details OWNER TO postgres;

--
-- Name: facility_ftp_details_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_ftp_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_ftp_details_id_seq OWNER TO postgres;

--
-- Name: facility_ftp_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_ftp_details_id_seq OWNED BY facility_ftp_details.id;


--
-- Name: facility_mappings; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_mappings (
    id integer NOT NULL,
    interfaceid integer NOT NULL,
    facilityid integer NOT NULL,
    mappedid character varying(100) NOT NULL,
    active boolean DEFAULT true,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE facility_mappings OWNER TO postgres;

--
-- Name: TABLE facility_mappings; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE facility_mappings IS 'Facility code mapping with other interfacing applications such as DHIS2';


--
-- Name: COLUMN facility_mappings.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.id IS 'ID';


--
-- Name: COLUMN facility_mappings.interfaceid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.interfaceid IS 'Interfacing apps id';


--
-- Name: COLUMN facility_mappings.facilityid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.facilityid IS 'Facility id';


--
-- Name: COLUMN facility_mappings.mappedid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.mappedid IS 'Mapped id';


--
-- Name: COLUMN facility_mappings.active; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.active IS 'Active';


--
-- Name: COLUMN facility_mappings.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.createdby IS 'Created by';


--
-- Name: COLUMN facility_mappings.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.createddate IS 'Created on';


--
-- Name: COLUMN facility_mappings.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.modifiedby IS 'Modified by';


--
-- Name: COLUMN facility_mappings.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN facility_mappings.modifieddate IS 'Modified on';


--
-- Name: facility_mappings_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_mappings_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_mappings_id_seq OWNER TO postgres;

--
-- Name: facility_mappings_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_mappings_id_seq OWNED BY facility_mappings.id;


--
-- Name: facility_operators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_operators (
    id integer NOT NULL,
    code character varying NOT NULL,
    text character varying(20),
    displayorder integer,
    createddate timestamp without time zone DEFAULT now(),
    createdby integer,
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE facility_operators OWNER TO postgres;

--
-- Name: facility_operators_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_operators_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_operators_id_seq OWNER TO postgres;

--
-- Name: facility_operators_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_operators_id_seq OWNED BY facility_operators.id;


--
-- Name: facility_program_equipments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_program_equipments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_program_equipments_id_seq OWNER TO postgres;

--
-- Name: facility_program_equipments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_program_equipments_id_seq OWNED BY equipment_inventories.id;


--
-- Name: facility_program_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_program_products (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    programproductid integer NOT NULL,
    isacoefficientsid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE facility_program_products OWNER TO postgres;

--
-- Name: facility_program_products_id_seq1; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_program_products_id_seq1
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_program_products_id_seq1 OWNER TO postgres;

--
-- Name: facility_program_products_id_seq1; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_program_products_id_seq1 OWNED BY facility_program_products.id;


--
-- Name: facility_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_types_id_seq OWNER TO postgres;

--
-- Name: facility_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_types_id_seq OWNED BY facility_types.id;


--
-- Name: facility_visits; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE facility_visits (
    id integer NOT NULL,
    distributionid integer,
    facilityid integer,
    confirmedbyname character varying(50),
    confirmedbytitle character varying(50),
    verifiedbyname character varying(50),
    verifiedbytitle character varying(50),
    observations text,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    synced boolean DEFAULT false,
    modifieddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    visited boolean,
    visitdate timestamp without time zone,
    vehicleid character varying(20),
    facilitycatchmentpopulation integer,
    reasonfornotvisiting character varying(50),
    otherreasondescription character varying(255)
);


ALTER TABLE facility_visits OWNER TO postgres;

--
-- Name: facility_visits_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE facility_visits_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE facility_visits_id_seq OWNER TO postgres;

--
-- Name: facility_visits_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE facility_visits_id_seq OWNED BY facility_visits.id;


--
-- Name: fulfillment_role_assignments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE fulfillment_role_assignments (
    userid integer NOT NULL,
    roleid integer NOT NULL,
    facilityid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE fulfillment_role_assignments OWNER TO postgres;

--
-- Name: full_coverages; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE full_coverages (
    id integer NOT NULL,
    femalehealthcenter numeric(7,0),
    femaleoutreach numeric(7,0),
    maleoutreach numeric(7,0),
    malehealthcenter numeric(7,0),
    facilityvisitid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE full_coverages OWNER TO postgres;

--
-- Name: geographic_levels; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE geographic_levels (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(250) NOT NULL,
    levelnumber integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    createdby integer,
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE geographic_levels OWNER TO postgres;

--
-- Name: geographic_levels_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE geographic_levels_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE geographic_levels_id_seq OWNER TO postgres;

--
-- Name: geographic_levels_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE geographic_levels_id_seq OWNED BY geographic_levels.id;


--
-- Name: geographic_zone_geojson; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE geographic_zone_geojson (
    id integer NOT NULL,
    zoneid integer,
    geojsonid integer,
    geometry text,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE geographic_zone_geojson OWNER TO postgres;

--
-- Name: geographic_zone_geojson_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE geographic_zone_geojson_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE geographic_zone_geojson_id_seq OWNER TO postgres;

--
-- Name: geographic_zone_geojson_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE geographic_zone_geojson_id_seq OWNED BY geographic_zone_geojson.id;


--
-- Name: geographic_zones_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE geographic_zones_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE geographic_zones_id_seq OWNER TO postgres;

--
-- Name: geographic_zones_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE geographic_zones_id_seq OWNED BY geographic_zones.id;


--
-- Name: gtin_lookups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE gtin_lookups (
    id integer NOT NULL,
    gtin character varying(255) NOT NULL,
    productid integer NOT NULL,
    manufacturename character varying,
    dosespervial integer NOT NULL,
    vialsperbox integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer,
    boxesperbox integer
);


ALTER TABLE gtin_lookups OWNER TO postgres;

--
-- Name: TABLE gtin_lookups; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE gtin_lookups IS 'Information About different Vaccine Packaging Information';


--
-- Name: gtin_lookups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE gtin_lookups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE gtin_lookups_id_seq OWNER TO postgres;

--
-- Name: gtin_lookups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE gtin_lookups_id_seq OWNED BY gtin_lookups.id;


--
-- Name: interface_apps; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE interface_apps (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    active boolean DEFAULT true,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE interface_apps OWNER TO postgres;

--
-- Name: TABLE interface_apps; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE interface_apps IS 'Applications with which eLMIS interfaces';


--
-- Name: COLUMN interface_apps.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_apps.id IS 'ID';


--
-- Name: COLUMN interface_apps.active; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_apps.active IS 'Active';


--
-- Name: COLUMN interface_apps.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_apps.createdby IS 'Created by';


--
-- Name: COLUMN interface_apps.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_apps.createddate IS 'Created on';


--
-- Name: COLUMN interface_apps.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_apps.modifiedby IS 'Modified by';


--
-- Name: COLUMN interface_apps.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_apps.modifieddate IS 'Modified on';


--
-- Name: interface_apps_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE interface_apps_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE interface_apps_id_seq OWNER TO postgres;

--
-- Name: interface_apps_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE interface_apps_id_seq OWNED BY interface_apps.id;


--
-- Name: interface_dataset; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE interface_dataset (
    id integer NOT NULL,
    interfaceid integer NOT NULL,
    datasetname character varying(100) NOT NULL,
    datasetid character varying(60) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE interface_dataset OWNER TO postgres;

--
-- Name: TABLE interface_dataset; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE interface_dataset IS 'Datasets to send to interfacing apps';


--
-- Name: COLUMN interface_dataset.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.id IS 'Id';


--
-- Name: COLUMN interface_dataset.interfaceid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.interfaceid IS 'Interface Id';


--
-- Name: COLUMN interface_dataset.datasetname; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.datasetname IS 'Dataset name';


--
-- Name: COLUMN interface_dataset.datasetid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.datasetid IS 'Dataset Id';


--
-- Name: COLUMN interface_dataset.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.createdby IS 'Created by';


--
-- Name: COLUMN interface_dataset.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.createddate IS 'Created on';


--
-- Name: COLUMN interface_dataset.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.modifiedby IS 'Modified by';


--
-- Name: COLUMN interface_dataset.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN interface_dataset.modifieddate IS 'Modified on';


--
-- Name: interface_dataset_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE interface_dataset_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE interface_dataset_id_seq OWNER TO postgres;

--
-- Name: interface_dataset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE interface_dataset_id_seq OWNED BY interface_dataset.id;


--
-- Name: isa_coefficients; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE isa_coefficients (
    id integer NOT NULL,
    whoratio numeric(6,3) NOT NULL,
    dosesperyear integer NOT NULL,
    wastagefactor numeric(6,3) NOT NULL,
    bufferpercentage numeric(6,3) NOT NULL,
    minimumvalue integer,
    maximumvalue integer,
    adjustmentvalue integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    populationsource integer
);


ALTER TABLE isa_coefficients OWNER TO postgres;

--
-- Name: losses_adjustments_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE losses_adjustments_types (
    name character varying(50) NOT NULL,
    description text NOT NULL,
    additive boolean,
    displayorder integer,
    createddate timestamp without time zone DEFAULT now(),
    isdefault boolean DEFAULT true,
    category text
);


ALTER TABLE losses_adjustments_types OWNER TO postgres;

--
-- Name: lots; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE lots (
    id integer NOT NULL,
    productid integer NOT NULL,
    lotnumber text,
    manufacturername text,
    manufacturedate timestamp with time zone DEFAULT now(),
    expirationdate timestamp with time zone DEFAULT now(),
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE lots OWNER TO postgres;

--
-- Name: lots_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE lots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE lots_id_seq OWNER TO postgres;

--
-- Name: lots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE lots_id_seq OWNED BY lots.id;


--
-- Name: lots_on_hand; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE lots_on_hand (
    id integer NOT NULL,
    stockcardid integer NOT NULL,
    lotid integer NOT NULL,
    quantityonhand integer DEFAULT 0,
    effectivedate timestamp with time zone DEFAULT now(),
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE lots_on_hand OWNER TO postgres;

--
-- Name: lots_on_hand_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE lots_on_hand_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE lots_on_hand_id_seq OWNER TO postgres;

--
-- Name: lots_on_hand_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE lots_on_hand_id_seq OWNED BY lots_on_hand.id;


--
-- Name: manufacturers; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE manufacturers (
    id integer NOT NULL,
    name character varying(1000) NOT NULL,
    website character varying(1000) NOT NULL,
    contactperson character varying(200),
    primaryphone character varying(20),
    email character varying(200),
    description character varying(2000),
    specialization character varying(2000),
    geographiccoverage character varying(2000),
    registrationdate date,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE manufacturers OWNER TO postgres;

--
-- Name: manufacturers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE manufacturers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE manufacturers_id_seq OWNER TO postgres;

--
-- Name: manufacturers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE manufacturers_id_seq OWNED BY manufacturers.id;


--
-- Name: master_regimen_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE master_regimen_columns (
    name character varying(100) NOT NULL,
    label character varying(100) NOT NULL,
    visible boolean NOT NULL,
    datatype character varying(50) NOT NULL,
    displayorder integer DEFAULT 0 NOT NULL
);


ALTER TABLE master_regimen_columns OWNER TO postgres;

--
-- Name: master_rnr_column_options; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE master_rnr_column_options (
    id integer NOT NULL,
    masterrnrcolumnid integer,
    rnroptionid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE master_rnr_column_options OWNER TO postgres;

--
-- Name: master_rnr_column_options_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE master_rnr_column_options_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master_rnr_column_options_id_seq OWNER TO postgres;

--
-- Name: master_rnr_column_options_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE master_rnr_column_options_id_seq OWNED BY master_rnr_column_options.id;


--
-- Name: master_rnr_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE master_rnr_columns (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    "position" integer NOT NULL,
    source character varying(1) NOT NULL,
    sourceconfigurable boolean NOT NULL,
    label character varying(200),
    formula character varying(200),
    indicator character varying(50) NOT NULL,
    used boolean NOT NULL,
    visible boolean NOT NULL,
    mandatory boolean NOT NULL,
    description character varying(250),
    createddate timestamp without time zone DEFAULT now(),
    calculationoption character varying(200) DEFAULT 'DEFAULT'::character varying
);


ALTER TABLE master_rnr_columns OWNER TO postgres;

--
-- Name: master_rnr_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE master_rnr_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master_rnr_columns_id_seq OWNER TO postgres;

--
-- Name: master_rnr_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE master_rnr_columns_id_seq OWNED BY master_rnr_columns.id;


--
-- Name: mos_adjustment_basis; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mos_adjustment_basis (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(100),
    displayorder integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE mos_adjustment_basis OWNER TO postgres;

--
-- Name: TABLE mos_adjustment_basis; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE mos_adjustment_basis IS 'This will be used in sending out the notifications to the facility and also display on the report';


--
-- Name: COLUMN mos_adjustment_basis.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.id IS 'id';


--
-- Name: COLUMN mos_adjustment_basis.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.name IS 'name';


--
-- Name: COLUMN mos_adjustment_basis.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.description IS 'description';


--
-- Name: COLUMN mos_adjustment_basis.displayorder; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.displayorder IS 'displayOrder';


--
-- Name: COLUMN mos_adjustment_basis.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.createdby IS 'createdBy';


--
-- Name: COLUMN mos_adjustment_basis.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.createddate IS 'createdDate';


--
-- Name: COLUMN mos_adjustment_basis.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.modifiedby IS 'modifiedBy';


--
-- Name: COLUMN mos_adjustment_basis.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_basis.modifieddate IS 'modifiedDate';


--
-- Name: mos_adjustment_basis_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mos_adjustment_basis_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE mos_adjustment_basis_id_seq OWNER TO postgres;

--
-- Name: mos_adjustment_basis_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mos_adjustment_basis_id_seq OWNED BY mos_adjustment_basis.id;


--
-- Name: mos_adjustment_facilities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mos_adjustment_facilities (
    id integer NOT NULL,
    typeid integer NOT NULL,
    facilityid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE mos_adjustment_facilities OWNER TO postgres;

--
-- Name: TABLE mos_adjustment_facilities; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE mos_adjustment_facilities IS 'Apply seasonality adjustment factor to facilities';


--
-- Name: COLUMN mos_adjustment_facilities.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.id IS 'id';


--
-- Name: COLUMN mos_adjustment_facilities.typeid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.typeid IS 'typeId';


--
-- Name: COLUMN mos_adjustment_facilities.facilityid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.facilityid IS 'facilityid';


--
-- Name: COLUMN mos_adjustment_facilities.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.createdby IS 'createdBy';


--
-- Name: COLUMN mos_adjustment_facilities.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.createddate IS 'createdDate';


--
-- Name: COLUMN mos_adjustment_facilities.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.modifiedby IS 'modifiedBy';


--
-- Name: COLUMN mos_adjustment_facilities.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_facilities.modifieddate IS 'modifiedDate';


--
-- Name: mos_adjustment_facilities_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mos_adjustment_facilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE mos_adjustment_facilities_id_seq OWNER TO postgres;

--
-- Name: mos_adjustment_facilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mos_adjustment_facilities_id_seq OWNED BY mos_adjustment_facilities.id;


--
-- Name: mos_adjustment_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mos_adjustment_products (
    id integer NOT NULL,
    typeid integer NOT NULL,
    basisid integer NOT NULL,
    productid integer NOT NULL,
    startdate date,
    enddate date,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE mos_adjustment_products OWNER TO postgres;

--
-- Name: TABLE mos_adjustment_products; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE mos_adjustment_products IS 'Adjust MOS for seasonality / rationing';


--
-- Name: COLUMN mos_adjustment_products.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.id IS 'id';


--
-- Name: COLUMN mos_adjustment_products.typeid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.typeid IS 'typeId';


--
-- Name: COLUMN mos_adjustment_products.basisid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.basisid IS 'basisId';


--
-- Name: COLUMN mos_adjustment_products.productid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.productid IS 'productID';


--
-- Name: COLUMN mos_adjustment_products.startdate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.startdate IS 'startDate';


--
-- Name: COLUMN mos_adjustment_products.enddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.enddate IS 'endDate';


--
-- Name: COLUMN mos_adjustment_products.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.createdby IS 'createdBy';


--
-- Name: COLUMN mos_adjustment_products.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.createddate IS 'createdDate';


--
-- Name: COLUMN mos_adjustment_products.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.modifiedby IS 'modifiedBy';


--
-- Name: COLUMN mos_adjustment_products.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_products.modifieddate IS 'modifiedDate';


--
-- Name: mos_adjustment_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mos_adjustment_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE mos_adjustment_products_id_seq OWNER TO postgres;

--
-- Name: mos_adjustment_products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mos_adjustment_products_id_seq OWNED BY mos_adjustment_products.id;


--
-- Name: mos_adjustment_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE mos_adjustment_types (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(100),
    displayorder integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE mos_adjustment_types OWNER TO postgres;

--
-- Name: TABLE mos_adjustment_types; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE mos_adjustment_types IS 'This will be used in sending out the notifications to the facility and also display on the report';


--
-- Name: COLUMN mos_adjustment_types.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.id IS 'id';


--
-- Name: COLUMN mos_adjustment_types.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.name IS 'name';


--
-- Name: COLUMN mos_adjustment_types.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.description IS 'description';


--
-- Name: COLUMN mos_adjustment_types.displayorder; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.displayorder IS 'displayOrder';


--
-- Name: COLUMN mos_adjustment_types.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.createdby IS 'createdBy';


--
-- Name: COLUMN mos_adjustment_types.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.createddate IS 'createdDate';


--
-- Name: COLUMN mos_adjustment_types.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.modifiedby IS 'modifiedBy';


--
-- Name: COLUMN mos_adjustment_types.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN mos_adjustment_types.modifieddate IS 'modifiedDate';


--
-- Name: mos_adjustment_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE mos_adjustment_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE mos_adjustment_types_id_seq OWNER TO postgres;

--
-- Name: mos_adjustment_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE mos_adjustment_types_id_seq OWNED BY mos_adjustment_types.id;


--
-- Name: opened_vial_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE opened_vial_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE opened_vial_line_items_id_seq OWNER TO postgres;

--
-- Name: opened_vial_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE opened_vial_line_items_id_seq OWNED BY child_coverage_opened_vial_line_items.id;


--
-- Name: order_configuration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE order_configuration (
    fileprefix character varying(8),
    headerinfile boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE order_configuration OWNER TO postgres;

--
-- Name: order_file_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE order_file_columns (
    id integer NOT NULL,
    datafieldlabel character varying(50),
    nested character varying(50),
    keypath character varying(50),
    includeinorderfile boolean DEFAULT true NOT NULL,
    columnlabel character varying(50),
    format character varying(20),
    "position" integer NOT NULL,
    openlmisfield boolean DEFAULT false NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE order_file_columns OWNER TO postgres;

--
-- Name: order_file_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE order_file_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE order_file_columns_id_seq OWNER TO postgres;

--
-- Name: order_file_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE order_file_columns_id_seq OWNED BY order_file_columns.id;


--
-- Name: order_number_configuration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE order_number_configuration (
    ordernumberprefix character varying(8),
    includeordernumberprefix boolean,
    includeprogramcode boolean,
    includesequencecode boolean,
    includernrtypesuffix boolean
);


ALTER TABLE order_number_configuration OWNER TO postgres;

--
-- Name: order_quantity_adjustment_factors; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE order_quantity_adjustment_factors (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(100),
    displayorder integer,
    basedonformula boolean DEFAULT false,
    createdby integer,
    createddate timestamp(6) without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp(6) without time zone DEFAULT now()
);


ALTER TABLE order_quantity_adjustment_factors OWNER TO postgres;

--
-- Name: TABLE order_quantity_adjustment_factors; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE order_quantity_adjustment_factors IS 'Basis of adjustment will enable the user to specify exact approach they need to use in adjusting for seasonality:
* Based on eZICS formula
* Based on Noel Watson
* Based on MOS AdjustmentThis will be used in sending out the notifications to the facility and also display on the report';


--
-- Name: COLUMN order_quantity_adjustment_factors.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.id IS 'ID';


--
-- Name: COLUMN order_quantity_adjustment_factors.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.name IS 'Name';


--
-- Name: COLUMN order_quantity_adjustment_factors.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.description IS 'Description';


--
-- Name: COLUMN order_quantity_adjustment_factors.displayorder; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.displayorder IS 'Display Order';


--
-- Name: COLUMN order_quantity_adjustment_factors.basedonformula; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.basedonformula IS 'Based On Formula';


--
-- Name: COLUMN order_quantity_adjustment_factors.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.createdby IS 'Created By';


--
-- Name: COLUMN order_quantity_adjustment_factors.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.createddate IS 'Created Date';


--
-- Name: COLUMN order_quantity_adjustment_factors.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.modifiedby IS 'Modified By';


--
-- Name: COLUMN order_quantity_adjustment_factors.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_factors.modifieddate IS 'Modified Date';


--
-- Name: order_quantity_adjustment_factors_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE order_quantity_adjustment_factors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE order_quantity_adjustment_factors_id_seq OWNER TO postgres;

--
-- Name: order_quantity_adjustment_factors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE order_quantity_adjustment_factors_id_seq OWNED BY order_quantity_adjustment_factors.id;


--
-- Name: order_quantity_adjustment_products; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE order_quantity_adjustment_products (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    productid integer NOT NULL,
    typeid integer NOT NULL,
    factorid integer NOT NULL,
    startdate date,
    enddate date,
    minmonthsofstock integer,
    maxmonthsofstock integer,
    formula character varying(500),
    createdby integer,
    createddate timestamp(6) without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp(6) without time zone DEFAULT now(),
    description character varying(200),
    CONSTRAINT chk_order_quantity_adjustment_products_min_mos CHECK ((COALESCE(minmonthsofstock, 0) < COALESCE(maxmonthsofstock, 1)))
);


ALTER TABLE order_quantity_adjustment_products OWNER TO postgres;

--
-- Name: TABLE order_quantity_adjustment_products; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE order_quantity_adjustment_products IS 'Adjust MOS for seasonality / rationing';


--
-- Name: COLUMN order_quantity_adjustment_products.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.id IS 'ID';


--
-- Name: COLUMN order_quantity_adjustment_products.facilityid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.facilityid IS 'Facility ID';


--
-- Name: COLUMN order_quantity_adjustment_products.productid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.productid IS 'Product ID';


--
-- Name: COLUMN order_quantity_adjustment_products.typeid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.typeid IS 'Type ID';


--
-- Name: COLUMN order_quantity_adjustment_products.factorid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.factorid IS 'Factor ID';


--
-- Name: COLUMN order_quantity_adjustment_products.startdate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.startdate IS 'Start Date';


--
-- Name: COLUMN order_quantity_adjustment_products.enddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.enddate IS 'End Date';


--
-- Name: COLUMN order_quantity_adjustment_products.minmonthsofstock; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.minmonthsofstock IS 'Minimum Months of Stock';


--
-- Name: COLUMN order_quantity_adjustment_products.maxmonthsofstock; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.maxmonthsofstock IS 'Maximum Months of Stock';


--
-- Name: COLUMN order_quantity_adjustment_products.formula; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.formula IS 'Formula';


--
-- Name: COLUMN order_quantity_adjustment_products.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.createdby IS 'Created By';


--
-- Name: COLUMN order_quantity_adjustment_products.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.createddate IS 'Created Date';


--
-- Name: COLUMN order_quantity_adjustment_products.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.modifiedby IS 'Modified By';


--
-- Name: COLUMN order_quantity_adjustment_products.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_products.modifieddate IS 'Modified Date';


--
-- Name: order_quantity_adjustment_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE order_quantity_adjustment_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE order_quantity_adjustment_products_id_seq OWNER TO postgres;

--
-- Name: order_quantity_adjustment_products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE order_quantity_adjustment_products_id_seq OWNED BY order_quantity_adjustment_products.id;


--
-- Name: order_quantity_adjustment_types; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE order_quantity_adjustment_types (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(100),
    displayorder integer,
    createdby integer,
    createddate timestamp(6) without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp(6) without time zone DEFAULT now()
);


ALTER TABLE order_quantity_adjustment_types OWNER TO postgres;

--
-- Name: TABLE order_quantity_adjustment_types; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE order_quantity_adjustment_types IS 'Adjustment types include:
* Rationing
* Seasonality
* Outbreak
* Malaria season
* Remote Facility
* MSL Physical Inventory close-out
* Other


This will be used in sending out the notifications to the facility and also display on the report';


--
-- Name: COLUMN order_quantity_adjustment_types.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.id IS 'ID';


--
-- Name: COLUMN order_quantity_adjustment_types.name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.name IS 'Name';


--
-- Name: COLUMN order_quantity_adjustment_types.description; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.description IS 'Description';


--
-- Name: COLUMN order_quantity_adjustment_types.displayorder; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.displayorder IS 'Display Order';


--
-- Name: COLUMN order_quantity_adjustment_types.createdby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.createdby IS 'Created By';


--
-- Name: COLUMN order_quantity_adjustment_types.createddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.createddate IS 'Created Date';


--
-- Name: COLUMN order_quantity_adjustment_types.modifiedby; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.modifiedby IS 'Modified By';


--
-- Name: COLUMN order_quantity_adjustment_types.modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN order_quantity_adjustment_types.modifieddate IS 'Modified Date';


--
-- Name: order_quantity_adjustment_types_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE order_quantity_adjustment_types_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE order_quantity_adjustment_types_id_seq OWNER TO postgres;

--
-- Name: order_quantity_adjustment_types_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE order_quantity_adjustment_types_id_seq OWNED BY order_quantity_adjustment_types.id;


--
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE orders (
    id integer NOT NULL,
    shipmentid integer,
    status character varying(20) NOT NULL,
    ftpcomment character varying(50),
    supplylineid integer,
    createdby integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer NOT NULL,
    modifieddate timestamp without time zone DEFAULT now(),
    ordernumber character varying(100) DEFAULT 0 NOT NULL
);


ALTER TABLE orders OWNER TO postgres;

--
-- Name: patient_quantification_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE patient_quantification_line_items (
    id integer NOT NULL,
    category character varying(50),
    total integer,
    rnrid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE patient_quantification_line_items OWNER TO postgres;

--
-- Name: patient_quantification_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE patient_quantification_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE patient_quantification_line_items_id_seq OWNER TO postgres;

--
-- Name: patient_quantification_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE patient_quantification_line_items_id_seq OWNED BY patient_quantification_line_items.id;


--
-- Name: period_short_names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE period_short_names (
    id integer NOT NULL,
    periodid integer,
    name character varying(50) NOT NULL,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE period_short_names OWNER TO postgres;

--
-- Name: period_short_names_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE period_short_names_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE period_short_names_id_seq OWNER TO postgres;

--
-- Name: period_short_names_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE period_short_names_id_seq OWNED BY period_short_names.id;


--
-- Name: pod; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pod (
    id integer NOT NULL,
    orderid integer NOT NULL,
    receiveddate timestamp without time zone DEFAULT now(),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    facilityid integer NOT NULL,
    programid integer NOT NULL,
    periodid integer NOT NULL,
    deliveredby character varying(100),
    receivedby character varying(100),
    ordernumber character varying(100) DEFAULT 0 NOT NULL
);


ALTER TABLE pod OWNER TO postgres;

--
-- Name: pod_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE pod_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE pod_id_seq OWNER TO postgres;

--
-- Name: pod_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE pod_id_seq OWNED BY pod.id;


--
-- Name: pod_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE pod_line_items (
    id integer NOT NULL,
    podid integer NOT NULL,
    productcode character varying(50) NOT NULL,
    quantityreceived integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    productname character varying(250),
    dispensingunit character varying(20),
    packstoship integer,
    quantityshipped integer,
    notes character varying(250),
    fullsupply boolean,
    productcategory character varying(100),
    productcategorydisplayorder integer,
    productdisplayorder integer,
    quantityreturned integer,
    replacedproductcode character varying(50)
);


ALTER TABLE pod_line_items OWNER TO postgres;

--
-- Name: pod_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE pod_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE pod_line_items_id_seq OWNER TO postgres;

--
-- Name: pod_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE pod_line_items_id_seq OWNED BY pod_line_items.id;


--
-- Name: price_schedules; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE price_schedules (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    description character varying(500),
    displayorder integer DEFAULT 0 NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE price_schedules OWNER TO postgres;

--
-- Name: price_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE price_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE price_schedules_id_seq OWNER TO postgres;

--
-- Name: price_schedules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE price_schedules_id_seq OWNED BY price_schedules.id;


--
-- Name: processing_periods_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE processing_periods_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE processing_periods_id_seq OWNER TO postgres;

--
-- Name: processing_periods_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE processing_periods_id_seq OWNED BY processing_periods.id;


--
-- Name: processing_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE processing_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE processing_schedules_id_seq OWNER TO postgres;

--
-- Name: processing_schedules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE processing_schedules_id_seq OWNED BY processing_schedules.id;


--
-- Name: product_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_categories_id_seq OWNER TO postgres;

--
-- Name: product_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_categories_id_seq OWNED BY product_categories.id;


--
-- Name: product_forms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product_forms (
    id integer NOT NULL,
    code character varying(20),
    displayorder integer,
    createddate timestamp without time zone DEFAULT now(),
    createdby integer,
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE product_forms OWNER TO postgres;

--
-- Name: product_forms_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_forms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_forms_id_seq OWNER TO postgres;

--
-- Name: product_forms_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_forms_id_seq OWNED BY product_forms.id;


--
-- Name: product_groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product_groups (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(250) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE product_groups OWNER TO postgres;

--
-- Name: product_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_groups_id_seq OWNER TO postgres;

--
-- Name: product_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_groups_id_seq OWNED BY product_groups.id;


--
-- Name: product_price_schedules; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product_price_schedules (
    id integer NOT NULL,
    pricescheduleid integer NOT NULL,
    productid integer NOT NULL,
    price numeric(12,4) DEFAULT 0 NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE product_price_schedules OWNER TO postgres;

--
-- Name: product_price_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_price_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_price_schedules_id_seq OWNER TO postgres;

--
-- Name: product_price_schedules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_price_schedules_id_seq OWNED BY product_price_schedules.id;


--
-- Name: product_short_names; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE product_short_names (
    id integer NOT NULL,
    productid integer,
    name character varying(50) NOT NULL,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE product_short_names OWNER TO postgres;

--
-- Name: product_short_names_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE product_short_names_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_short_names_id_seq OWNER TO postgres;

--
-- Name: product_short_names_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE product_short_names_id_seq OWNED BY product_short_names.id;


--
-- Name: products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE products_id_seq OWNER TO postgres;

--
-- Name: products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE products_id_seq OWNED BY products.id;


--
-- Name: program_equipment_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_equipment_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_equipment_products_id_seq OWNER TO postgres;

--
-- Name: program_equipment_products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_equipment_products_id_seq OWNED BY equipment_type_products.id;


--
-- Name: program_equipments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_equipments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_equipments_id_seq OWNER TO postgres;

--
-- Name: program_equipments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_equipments_id_seq OWNED BY equipment_type_programs.id;


--
-- Name: program_product_isa_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_product_isa_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_product_isa_id_seq OWNER TO postgres;

--
-- Name: program_product_isa_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_product_isa_id_seq OWNED BY isa_coefficients.id;


--
-- Name: program_product_price_history; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE program_product_price_history (
    id integer NOT NULL,
    programproductid integer NOT NULL,
    price numeric(20,2) DEFAULT 0,
    priceperdosage numeric(20,2) DEFAULT 0,
    source character varying(50),
    startdate timestamp without time zone DEFAULT now(),
    enddate timestamp without time zone DEFAULT now(),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE program_product_price_history OWNER TO postgres;

--
-- Name: program_product_price_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_product_price_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_product_price_history_id_seq OWNER TO postgres;

--
-- Name: program_product_price_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_product_price_history_id_seq OWNED BY program_product_price_history.id;


--
-- Name: program_products_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_products_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_products_id_seq OWNER TO postgres;

--
-- Name: program_products_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_products_id_seq OWNED BY program_products.id;


--
-- Name: program_regimen_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE program_regimen_columns (
    id integer NOT NULL,
    programid integer NOT NULL,
    name character varying(100) NOT NULL,
    label character varying(100) NOT NULL,
    visible boolean NOT NULL,
    datatype character varying(50) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    displayorder integer DEFAULT 0 NOT NULL
);


ALTER TABLE program_regimen_columns OWNER TO postgres;

--
-- Name: program_regimen_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_regimen_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_regimen_columns_id_seq OWNER TO postgres;

--
-- Name: program_regimen_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_regimen_columns_id_seq OWNED BY program_regimen_columns.id;


--
-- Name: program_rnr_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE program_rnr_columns (
    id integer NOT NULL,
    mastercolumnid integer NOT NULL,
    programid integer NOT NULL,
    label character varying(200) NOT NULL,
    visible boolean NOT NULL,
    "position" integer NOT NULL,
    source character varying(1),
    formulavalidationrequired boolean,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    rnroptionid integer,
    calculationoption character varying(200) DEFAULT 'DEFAULT'::character varying
);


ALTER TABLE program_rnr_columns OWNER TO postgres;

--
-- Name: program_rnr_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE program_rnr_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE program_rnr_columns_id_seq OWNER TO postgres;

--
-- Name: program_rnr_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE program_rnr_columns_id_seq OWNED BY program_rnr_columns.id;


--
-- Name: programs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE programs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE programs_id_seq OWNER TO postgres;

--
-- Name: programs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE programs_id_seq OWNED BY programs.id;


--
-- Name: programs_supported; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE programs_supported (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    programid integer NOT NULL,
    startdate timestamp without time zone,
    active boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE programs_supported OWNER TO postgres;

--
-- Name: programs_supported_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE programs_supported_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE programs_supported_id_seq OWNER TO postgres;

--
-- Name: programs_supported_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE programs_supported_id_seq OWNED BY programs_supported.id;


--
-- Name: refrigerator_problems; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE refrigerator_problems (
    id integer NOT NULL,
    readingid integer,
    operatorerror boolean DEFAULT false,
    burnerproblem boolean DEFAULT false,
    gasleakage boolean DEFAULT false,
    egpfault boolean DEFAULT false,
    thermostatsetting boolean DEFAULT false,
    other boolean DEFAULT false,
    otherproblemexplanation character varying(255),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE refrigerator_problems OWNER TO postgres;

--
-- Name: refrigerator_problems_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE refrigerator_problems_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE refrigerator_problems_id_seq OWNER TO postgres;

--
-- Name: refrigerator_problems_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE refrigerator_problems_id_seq OWNED BY refrigerator_problems.id;


--
-- Name: refrigerators; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE refrigerators (
    id integer NOT NULL,
    brand character varying(20),
    model character varying(20),
    serialnumber character varying(30) NOT NULL,
    facilityid integer,
    createdby integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer NOT NULL,
    modifieddate timestamp without time zone DEFAULT now(),
    enabled boolean DEFAULT true
);


ALTER TABLE refrigerators OWNER TO postgres;

--
-- Name: refrigerators_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE refrigerators_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE refrigerators_id_seq OWNER TO postgres;

--
-- Name: refrigerators_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE refrigerators_id_seq OWNED BY refrigerators.id;


--
-- Name: regimen_categories; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regimen_categories (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    displayorder integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE regimen_categories OWNER TO postgres;

--
-- Name: regimen_categories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE regimen_categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE regimen_categories_id_seq OWNER TO postgres;

--
-- Name: regimen_categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE regimen_categories_id_seq OWNED BY regimen_categories.id;


--
-- Name: regimen_combination_constituents; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regimen_combination_constituents (
    id integer NOT NULL,
    defaultdosageid integer,
    productcomboid integer,
    productid integer
);


ALTER TABLE regimen_combination_constituents OWNER TO postgres;

--
-- Name: regimen_combination_constituents_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE regimen_combination_constituents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE regimen_combination_constituents_id_seq OWNER TO postgres;

--
-- Name: regimen_combination_constituents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE regimen_combination_constituents_id_seq OWNED BY regimen_combination_constituents.id;


--
-- Name: regimen_constituents_dosages; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regimen_constituents_dosages (
    id integer NOT NULL,
    regimenproductid integer NOT NULL,
    quantity numeric,
    dosageunitid integer,
    dosagefrequencyid integer
);


ALTER TABLE regimen_constituents_dosages OWNER TO postgres;

--
-- Name: regimen_constituents_dosages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE regimen_constituents_dosages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE regimen_constituents_dosages_id_seq OWNER TO postgres;

--
-- Name: regimen_constituents_dosages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE regimen_constituents_dosages_id_seq OWNED BY regimen_constituents_dosages.id;


--
-- Name: regimen_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regimen_line_items (
    id integer NOT NULL,
    code character varying(50),
    name character varying(250),
    regimendisplayorder integer,
    regimencategory character varying(50),
    regimencategorydisplayorder integer,
    rnrid integer NOT NULL,
    patientsontreatment integer,
    patientstoinitiatetreatment integer,
    patientsstoppedtreatment integer,
    remarks character varying(255),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    skipped boolean DEFAULT false NOT NULL,
    patientsontreatmentadult integer,
    patientstoinitiatetreatmentadult integer,
    patientsstoppedtreatmentadult integer,
    patientsontreatmentchildren integer,
    patientstoinitiatetreatmentchildren integer,
    patientsstoppedtreatmentchildren integer
);


ALTER TABLE regimen_line_items OWNER TO postgres;

--
-- Name: regimen_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE regimen_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE regimen_line_items_id_seq OWNER TO postgres;

--
-- Name: regimen_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE regimen_line_items_id_seq OWNED BY regimen_line_items.id;


--
-- Name: regimen_product_combinations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regimen_product_combinations (
    id integer NOT NULL,
    regimenid integer,
    name character varying(50)
);


ALTER TABLE regimen_product_combinations OWNER TO postgres;

--
-- Name: regimen_product_combinations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE regimen_product_combinations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE regimen_product_combinations_id_seq OWNER TO postgres;

--
-- Name: regimen_product_combinations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE regimen_product_combinations_id_seq OWNED BY regimen_product_combinations.id;


--
-- Name: regimens; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE regimens (
    id integer NOT NULL,
    programid integer NOT NULL,
    categoryid integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    active boolean,
    displayorder integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE regimens OWNER TO postgres;

--
-- Name: regimens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE regimens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE regimens_id_seq OWNER TO postgres;

--
-- Name: regimens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE regimens_id_seq OWNED BY regimens.id;


--
-- Name: report_rights; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE report_rights (
    id integer NOT NULL,
    templateid integer NOT NULL,
    rightname character varying NOT NULL
);


ALTER TABLE report_rights OWNER TO postgres;

--
-- Name: report_rights_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE report_rights_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE report_rights_id_seq OWNER TO postgres;

--
-- Name: report_rights_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE report_rights_id_seq OWNED BY report_rights.id;


--
-- Name: templates; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE templates (
    id integer NOT NULL,
    name character varying NOT NULL,
    data bytea NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    type character varying NOT NULL,
    description character varying(500)
);


ALTER TABLE templates OWNER TO postgres;

--
-- Name: report_templates_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE report_templates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE report_templates_id_seq OWNER TO postgres;

--
-- Name: report_templates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE report_templates_id_seq OWNED BY templates.id;


--
-- Name: requisition_group_members; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_group_members (
    id integer NOT NULL,
    requisitiongroupid integer NOT NULL,
    facilityid integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE requisition_group_members OWNER TO postgres;

--
-- Name: requisition_group_members_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE requisition_group_members_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisition_group_members_id_seq OWNER TO postgres;

--
-- Name: requisition_group_members_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE requisition_group_members_id_seq OWNED BY requisition_group_members.id;


--
-- Name: requisition_group_program_schedules; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_group_program_schedules (
    id integer NOT NULL,
    requisitiongroupid integer NOT NULL,
    programid integer NOT NULL,
    scheduleid integer NOT NULL,
    directdelivery boolean NOT NULL,
    dropofffacilityid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE requisition_group_program_schedules OWNER TO postgres;

--
-- Name: requisition_group_program_schedules_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE requisition_group_program_schedules_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisition_group_program_schedules_id_seq OWNER TO postgres;

--
-- Name: requisition_group_program_schedules_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE requisition_group_program_schedules_id_seq OWNED BY requisition_group_program_schedules.id;


--
-- Name: requisition_groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_groups (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(250),
    supervisorynodeid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE requisition_groups OWNER TO postgres;

--
-- Name: requisition_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE requisition_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisition_groups_id_seq OWNER TO postgres;

--
-- Name: requisition_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE requisition_groups_id_seq OWNED BY requisition_groups.id;


--
-- Name: requisition_line_item_losses_adjustments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_line_item_losses_adjustments (
    requisitionlineitemid integer NOT NULL,
    type character varying(250) NOT NULL,
    quantity integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE requisition_line_item_losses_adjustments OWNER TO postgres;

--
-- Name: requisition_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE requisition_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisition_line_items_id_seq OWNER TO postgres;

--
-- Name: requisition_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE requisition_line_items_id_seq OWNED BY requisition_line_items.id;


--
-- Name: requisition_signatures; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_signatures (
    signatureid integer NOT NULL,
    rnrid integer NOT NULL
);


ALTER TABLE requisition_signatures OWNER TO postgres;

--
-- Name: requisition_status_changes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE requisition_status_changes (
    id integer NOT NULL,
    rnrid integer NOT NULL,
    status character varying(20) NOT NULL,
    createdby integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer NOT NULL,
    modifieddate timestamp without time zone DEFAULT now(),
    username character varying(100)
);


ALTER TABLE requisition_status_changes OWNER TO postgres;

--
-- Name: requisition_status_changes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE requisition_status_changes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisition_status_changes_id_seq OWNER TO postgres;

--
-- Name: requisition_status_changes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE requisition_status_changes_id_seq OWNED BY requisition_status_changes.id;


--
-- Name: requisitions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE requisitions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE requisitions_id_seq OWNER TO postgres;

--
-- Name: requisitions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE requisitions_id_seq OWNED BY requisitions.id;


--
-- Name: rights; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE rights (
    name character varying(200) NOT NULL,
    righttype character varying(20) NOT NULL,
    description character varying(200),
    createddate timestamp without time zone DEFAULT now(),
    displayorder integer,
    displaynamekey character varying(150)
);


ALTER TABLE rights OWNER TO postgres;

--
-- Name: role_assignments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE role_assignments (
    userid integer NOT NULL,
    roleid integer NOT NULL,
    programid integer,
    supervisorynodeid integer,
    deliveryzoneid integer
);


ALTER TABLE role_assignments OWNER TO postgres;

--
-- Name: role_rights; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE role_rights (
    roleid integer NOT NULL,
    rightname character varying NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now()
);


ALTER TABLE role_rights OWNER TO postgres;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE roles (
    id integer NOT NULL,
    name character varying(50) NOT NULL,
    description character varying(250),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE roles OWNER TO postgres;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE roles_id_seq OWNER TO postgres;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE roles_id_seq OWNED BY roles.id;


--
-- Name: shipment_configuration; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment_configuration (
    headerinfile boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE shipment_configuration OWNER TO postgres;

--
-- Name: shipment_file_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment_file_columns (
    id integer NOT NULL,
    name character varying(150) NOT NULL,
    datafieldlabel character varying(150),
    "position" integer,
    include boolean NOT NULL,
    mandatory boolean NOT NULL,
    datepattern character varying(25),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE shipment_file_columns OWNER TO postgres;

--
-- Name: shipment_file_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE shipment_file_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shipment_file_columns_id_seq OWNER TO postgres;

--
-- Name: shipment_file_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE shipment_file_columns_id_seq OWNED BY shipment_file_columns.id;


--
-- Name: shipment_file_info; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment_file_info (
    id integer NOT NULL,
    filename character varying(200) NOT NULL,
    processingerror boolean NOT NULL,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE shipment_file_info OWNER TO postgres;

--
-- Name: shipment_file_info_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE shipment_file_info_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shipment_file_info_id_seq OWNER TO postgres;

--
-- Name: shipment_file_info_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE shipment_file_info_id_seq OWNED BY shipment_file_info.id;


--
-- Name: shipment_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE shipment_line_items (
    id integer NOT NULL,
    orderid integer NOT NULL,
    concatenatedorderid character varying(50),
    facilitycode character varying(50),
    programcode character varying(50),
    productcode character varying(50) NOT NULL,
    quantityordered integer,
    quantityshipped integer NOT NULL,
    cost numeric(15,2),
    packeddate timestamp without time zone,
    shippeddate timestamp without time zone,
    substitutedproductcode character varying(50),
    substitutedproductname character varying(200),
    substitutedproductquantityshipped integer,
    packsize integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    productname character varying(250) NOT NULL,
    dispensingunit character varying(20) NOT NULL,
    productcategory character varying(100),
    packstoship integer,
    productcategorydisplayorder integer,
    productdisplayorder integer,
    fullsupply boolean,
    replacedproductcode character varying(50),
    ordernumber character varying(100) NOT NULL
);


ALTER TABLE shipment_line_items OWNER TO postgres;

--
-- Name: shipment_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE shipment_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shipment_line_items_id_seq OWNER TO postgres;

--
-- Name: shipment_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE shipment_line_items_id_seq OWNED BY shipment_line_items.id;


--
-- Name: signatures; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE signatures (
    id integer NOT NULL,
    type character varying NOT NULL,
    text character varying NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE signatures OWNER TO postgres;

--
-- Name: signatures_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE signatures_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE signatures_id_seq OWNER TO postgres;

--
-- Name: signatures_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE signatures_id_seq OWNED BY signatures.id;


--
-- Name: sms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE sms (
    id integer NOT NULL,
    message character varying(250),
    phonenumber character varying(20),
    direction character varying(40),
    sent boolean DEFAULT false,
    datesaved date
);


ALTER TABLE sms OWNER TO postgres;

--
-- Name: sms_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE sms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sms_id_seq OWNER TO postgres;

--
-- Name: sms_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE sms_id_seq OWNED BY sms.id;


--
-- Name: stock_adjustment_reasons_programs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_adjustment_reasons_programs (
    id integer NOT NULL,
    programcode text NOT NULL,
    reasonname text NOT NULL,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_adjustment_reasons_programs OWNER TO postgres;

--
-- Name: stock_adjustment_reasons_programs_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_adjustment_reasons_programs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_adjustment_reasons_programs_id_seq OWNER TO postgres;

--
-- Name: stock_adjustment_reasons_programs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_adjustment_reasons_programs_id_seq OWNED BY stock_adjustment_reasons_programs.id;


--
-- Name: stock_card_entries; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_card_entries (
    id integer NOT NULL,
    stockcardid integer NOT NULL,
    lotonhandid integer,
    type text NOT NULL,
    quantity integer DEFAULT 0 NOT NULL,
    stockmovementid integer,
    referencenumber text,
    adjustmenttype text,
    notes text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now(),
    occurred date
);


ALTER TABLE stock_card_entries OWNER TO postgres;

--
-- Name: stock_card_entry_key_values; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_card_entry_key_values (
    stockcardentryid integer NOT NULL,
    keycolumn text NOT NULL,
    valuecolumn text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_card_entry_key_values OWNER TO postgres;

--
-- Name: stock_card_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_card_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_card_line_items_id_seq OWNER TO postgres;

--
-- Name: stock_card_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_card_line_items_id_seq OWNED BY stock_card_entries.id;


--
-- Name: stock_cards; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_cards (
    id integer NOT NULL,
    facilityid integer NOT NULL,
    productid integer NOT NULL,
    totalquantityonhand integer DEFAULT 0,
    effectivedate timestamp with time zone DEFAULT now(),
    notes text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_cards OWNER TO postgres;

--
-- Name: stock_cards_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_cards_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_cards_id_seq OWNER TO postgres;

--
-- Name: stock_cards_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_cards_id_seq OWNED BY stock_cards.id;


--
-- Name: stock_movement_line_item_extra_fields; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_movement_line_item_extra_fields (
    id integer NOT NULL,
    stockmovementlineitemid integer NOT NULL,
    issuevoucher character varying(250) NOT NULL,
    issuedate character varying(250) NOT NULL,
    tofacilityname character varying(250) NOT NULL,
    productid integer NOT NULL,
    dosesrequested integer NOT NULL,
    gap integer DEFAULT 0,
    productcategoryid integer,
    quantityonhand integer DEFAULT 0,
    createdby integer,
    createddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_movement_line_item_extra_fields OWNER TO postgres;

--
-- Name: stock_movement_line_item_extra_fields_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_movement_line_item_extra_fields_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_movement_line_item_extra_fields_id_seq OWNER TO postgres;

--
-- Name: stock_movement_line_item_extra_fields_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_movement_line_item_extra_fields_id_seq OWNED BY stock_movement_line_item_extra_fields.id;


--
-- Name: stock_movement_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_movement_line_items (
    id integer NOT NULL,
    stockmovementid integer NOT NULL,
    lotid integer NOT NULL,
    quantity integer DEFAULT 0 NOT NULL,
    notes text,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_movement_line_items OWNER TO postgres;

--
-- Name: stock_movement_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_movement_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_movement_line_items_id_seq OWNER TO postgres;

--
-- Name: stock_movement_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_movement_line_items_id_seq OWNED BY stock_movement_line_items.id;


--
-- Name: stock_movement_lots; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_movement_lots (
    id integer NOT NULL,
    stockmovementlineitemid integer NOT NULL,
    lotid integer NOT NULL,
    quantity integer DEFAULT 0,
    effectivedate timestamp with time zone DEFAULT now(),
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_movement_lots OWNER TO postgres;

--
-- Name: stock_movement_lots_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_movement_lots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_movement_lots_id_seq OWNER TO postgres;

--
-- Name: stock_movement_lots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_movement_lots_id_seq OWNED BY stock_movement_lots.id;


--
-- Name: stock_movements; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE stock_movements (
    id integer NOT NULL,
    type stockmovementtype NOT NULL,
    fromfacilityid integer,
    tofacilityid integer,
    initiateddate timestamp with time zone DEFAULT now(),
    shippeddate timestamp with time zone DEFAULT now(),
    expecteddate timestamp with time zone DEFAULT now(),
    receiveddate timestamp with time zone DEFAULT now(),
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE stock_movements OWNER TO postgres;

--
-- Name: stock_movements_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE stock_movements_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE stock_movements_id_seq OWNER TO postgres;

--
-- Name: stock_movements_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE stock_movements_id_seq OWNED BY stock_movements.id;


--
-- Name: supervisory_nodes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE supervisory_nodes (
    id integer NOT NULL,
    parentid integer,
    facilityid integer NOT NULL,
    name character varying(50) NOT NULL,
    code character varying(50) NOT NULL,
    description character varying(250),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE supervisory_nodes OWNER TO postgres;

--
-- Name: supervisory_nodes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE supervisory_nodes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE supervisory_nodes_id_seq OWNER TO postgres;

--
-- Name: supervisory_nodes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE supervisory_nodes_id_seq OWNED BY supervisory_nodes.id;


--
-- Name: supply_lines; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE supply_lines (
    id integer NOT NULL,
    description character varying(250),
    supervisorynodeid integer,
    programid integer NOT NULL,
    supplyingfacilityid integer NOT NULL,
    exportorders boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    parentid integer
);


ALTER TABLE supply_lines OWNER TO postgres;

--
-- Name: supply_lines_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE supply_lines_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE supply_lines_id_seq OWNER TO postgres;

--
-- Name: supply_lines_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE supply_lines_id_seq OWNED BY supply_lines.id;


--
-- Name: template_parameters; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE template_parameters (
    id integer NOT NULL,
    templateid integer NOT NULL,
    name character varying(250) NOT NULL,
    displayname character varying(250) NOT NULL,
    description character varying(500),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    defaultvalue character varying(500),
    datatype character varying(500) NOT NULL,
    selectsql text
);


ALTER TABLE template_parameters OWNER TO postgres;

--
-- Name: template_parameters_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE template_parameters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE template_parameters_id_seq OWNER TO postgres;

--
-- Name: template_parameters_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE template_parameters_id_seq OWNED BY template_parameters.id;


--
-- Name: user_password_reset_tokens; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_password_reset_tokens (
    userid integer NOT NULL,
    token character varying(250) NOT NULL,
    createddate timestamp without time zone DEFAULT now()
);


ALTER TABLE user_password_reset_tokens OWNER TO postgres;

--
-- Name: user_preference_master; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_preference_master (
    id integer NOT NULL,
    key character varying(50) NOT NULL,
    name character varying(50) NOT NULL,
    groupname character varying(50),
    groupdisplayorder integer DEFAULT 1,
    displayorder integer,
    description character varying(2000),
    entitytype character varying(50),
    inputtype character varying(50),
    datatype character varying(50),
    defaultvalue character varying(2000),
    isactive boolean DEFAULT true,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE user_preference_master OWNER TO postgres;

--
-- Name: user_preference_master_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE user_preference_master_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE user_preference_master_id_seq OWNER TO postgres;

--
-- Name: user_preference_master_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE user_preference_master_id_seq OWNED BY user_preference_master.id;


--
-- Name: user_preference_roles; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_preference_roles (
    roleid integer NOT NULL,
    userpreferencekey character varying(50),
    isapplicable boolean,
    defaultvalue character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE user_preference_roles OWNER TO postgres;

--
-- Name: user_preferences; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_preferences (
    userid integer NOT NULL,
    userpreferencekey character varying(50),
    value character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE user_preferences OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(128) DEFAULT 'not-in-use'::character varying,
    firstname character varying(50) NOT NULL,
    lastname character varying(50) NOT NULL,
    employeeid character varying(50),
    restrictlogin boolean DEFAULT false,
    jobtitle character varying(50),
    primarynotificationmethod character varying(50),
    officephone character varying(30),
    cellphone character varying(30),
    email character varying(50),
    supervisorid integer,
    facilityid integer,
    verified boolean DEFAULT false,
    active boolean DEFAULT true,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    ismobileuser boolean DEFAULT false
);


ALTER TABLE users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: vaccination_adult_coverage_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccination_adult_coverage_line_items (
    id integer NOT NULL,
    facilityvisitid integer NOT NULL,
    demographicgroup character varying(255) NOT NULL,
    targetgroup integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    healthcentertetanus1 integer,
    outreachtetanus1 integer,
    healthcentertetanus2to5 integer,
    outreachtetanus2to5 integer
);


ALTER TABLE vaccination_adult_coverage_line_items OWNER TO postgres;

--
-- Name: vaccination_adult_coverage_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccination_adult_coverage_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccination_adult_coverage_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccination_adult_coverage_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccination_adult_coverage_line_items_id_seq OWNED BY vaccination_adult_coverage_line_items.id;


--
-- Name: vaccination_child_coverage_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccination_child_coverage_line_items (
    id integer NOT NULL,
    facilityvisitid integer NOT NULL,
    vaccination character varying(255) NOT NULL,
    targetgroup integer,
    healthcenter11months integer,
    outreach11months integer,
    healthcenter23months integer,
    outreach23months integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccination_child_coverage_line_items OWNER TO postgres;

--
-- Name: vaccination_child_coverage_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccination_child_coverage_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccination_child_coverage_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccination_child_coverage_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccination_child_coverage_line_items_id_seq OWNED BY vaccination_child_coverage_line_items.id;


--
-- Name: vaccination_full_coverages_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccination_full_coverages_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccination_full_coverages_id_seq OWNER TO postgres;

--
-- Name: vaccination_full_coverages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccination_full_coverages_id_seq OWNED BY full_coverages.id;


--
-- Name: vaccine_discarding_reasons; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_discarding_reasons (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    requiresexplanation boolean DEFAULT false NOT NULL,
    displayorder integer NOT NULL,
    createdby integer,
    createddate date DEFAULT now(),
    modifiedby integer,
    modifieddate date DEFAULT now()
);


ALTER TABLE vaccine_discarding_reasons OWNER TO postgres;

--
-- Name: vaccine_discarding_reasons_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_discarding_reasons_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_discarding_reasons_id_seq OWNER TO postgres;

--
-- Name: vaccine_discarding_reasons_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_discarding_reasons_id_seq OWNED BY vaccine_discarding_reasons.id;


--
-- Name: vaccine_diseases; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_diseases (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(200),
    displayorder integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_diseases OWNER TO postgres;

--
-- Name: vaccine_diseases_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_diseases_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_diseases_id_seq OWNER TO postgres;

--
-- Name: vaccine_diseases_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_diseases_id_seq OWNED BY vaccine_diseases.id;


--
-- Name: vaccine_distribution_line_item_lots; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_distribution_line_item_lots (
    id integer NOT NULL,
    distributionlineitemid integer NOT NULL,
    lotid integer NOT NULL,
    quantity integer DEFAULT 0,
    vvmstatus smallint,
    createdby integer,
    createddate timestamp without time zone,
    modifiedby integer,
    modifieddate timestamp without time zone
);


ALTER TABLE vaccine_distribution_line_item_lots OWNER TO postgres;

--
-- Name: vaccine_distribution_line_item_lots_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_distribution_line_item_lots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_distribution_line_item_lots_id_seq OWNER TO postgres;

--
-- Name: vaccine_distribution_line_item_lots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_distribution_line_item_lots_id_seq OWNED BY vaccine_distribution_line_item_lots.id;


--
-- Name: vaccine_distribution_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_distribution_line_items (
    id integer NOT NULL,
    distributionid integer NOT NULL,
    productid integer NOT NULL,
    quantity integer DEFAULT 0,
    vvmstatus smallint,
    createdby integer,
    createddate timestamp without time zone,
    modifiedby integer,
    modifieddate timestamp without time zone
);


ALTER TABLE vaccine_distribution_line_items OWNER TO postgres;

--
-- Name: vaccine_distribution_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_distribution_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_distribution_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_distribution_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_distribution_line_items_id_seq OWNED BY vaccine_distribution_line_items.id;


--
-- Name: vaccine_distributions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_distributions (
    id integer NOT NULL,
    tofacilityid integer NOT NULL,
    fromfacilityid integer NOT NULL,
    vouchernumber character varying(100),
    distributiondate timestamp without time zone,
    periodid integer,
    orderid integer,
    status character varying(20),
    createdby integer,
    createddate timestamp without time zone,
    modifiedby integer,
    modifieddate timestamp without time zone,
    distributiontype character varying(40)
);


ALTER TABLE vaccine_distributions OWNER TO postgres;

--
-- Name: vaccine_distributions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_distributions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_distributions_id_seq OWNER TO postgres;

--
-- Name: vaccine_distributions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_distributions_id_seq OWNED BY vaccine_distributions.id;


--
-- Name: vaccine_doses; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_doses (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(1000),
    displayorder integer NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_doses OWNER TO postgres;

--
-- Name: vaccine_doses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_doses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_doses_id_seq OWNER TO postgres;

--
-- Name: vaccine_doses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_doses_id_seq OWNED BY vaccine_doses.id;


--
-- Name: vaccine_inventory_product_configurations; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_inventory_product_configurations (
    id integer NOT NULL,
    type character varying(50) NOT NULL,
    productid integer,
    batchtracked boolean,
    vvmtracked boolean,
    survivinginfants boolean,
    denominatorestimatecategoryid integer
);


ALTER TABLE vaccine_inventory_product_configurations OWNER TO postgres;

--
-- Name: vaccine_inventory_product_configurations_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_inventory_product_configurations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_inventory_product_configurations_id_seq OWNER TO postgres;

--
-- Name: vaccine_inventory_product_configurations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_inventory_product_configurations_id_seq OWNED BY vaccine_inventory_product_configurations.id;


--
-- Name: vaccine_ivd_tab_visibilities; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_ivd_tab_visibilities (
    id integer NOT NULL,
    programid integer NOT NULL,
    tab character varying(200) NOT NULL,
    name character varying(200) NOT NULL,
    visible boolean DEFAULT true NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_ivd_tab_visibilities OWNER TO postgres;

--
-- Name: vaccine_ivd_tab_visibilities_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_ivd_tab_visibilities_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_ivd_tab_visibilities_id_seq OWNER TO postgres;

--
-- Name: vaccine_ivd_tab_visibilities_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_ivd_tab_visibilities_id_seq OWNED BY vaccine_ivd_tab_visibilities.id;


--
-- Name: vaccine_ivd_tabs; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_ivd_tabs (
    tab character varying(200) NOT NULL,
    name character varying(200) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_ivd_tabs OWNER TO postgres;

--
-- Name: vaccine_logistics_master_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_logistics_master_columns (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    description character varying(200) NOT NULL,
    label character varying(200) NOT NULL,
    indicator character varying(20) NOT NULL,
    displayorder integer NOT NULL,
    mandatory boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_logistics_master_columns OWNER TO postgres;

--
-- Name: vaccine_logistics_master_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_logistics_master_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_logistics_master_columns_id_seq OWNER TO postgres;

--
-- Name: vaccine_logistics_master_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_logistics_master_columns_id_seq OWNED BY vaccine_logistics_master_columns.id;


--
-- Name: vaccine_lots_on_hand_adjustments; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_lots_on_hand_adjustments (
    id integer NOT NULL,
    lotonhandid integer,
    adjustmentreason character(50),
    quantity integer,
    createdby integer,
    createddate timestamp with time zone,
    modifiedby integer,
    modifieddate timestamp with time zone,
    effectivedate timestamp with time zone
);


ALTER TABLE vaccine_lots_on_hand_adjustments OWNER TO postgres;

--
-- Name: vaccine_lots_on_hand_adjustments_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_lots_on_hand_adjustments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_lots_on_hand_adjustments_id_seq OWNER TO postgres;

--
-- Name: vaccine_lots_on_hand_adjustments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_lots_on_hand_adjustments_id_seq OWNED BY vaccine_lots_on_hand_adjustments.id;


--
-- Name: vaccine_lots_on_hand_vvm; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_lots_on_hand_vvm (
    id integer NOT NULL,
    lotonhandid integer,
    vvmstatus smallint,
    effectivedate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_lots_on_hand_vvm OWNER TO postgres;

--
-- Name: vaccine_lots_on_hand_vvm_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_lots_on_hand_vvm_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_lots_on_hand_vvm_id_seq OWNER TO postgres;

--
-- Name: vaccine_lots_on_hand_vvm_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_lots_on_hand_vvm_id_seq OWNED BY vaccine_lots_on_hand_vvm.id;


--
-- Name: vaccine_order_requisition_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_order_requisition_line_items (
    id integer NOT NULL,
    orderid integer NOT NULL,
    productid integer NOT NULL,
    productname character varying(200) NOT NULL,
    maximumstock integer,
    reorderlevel integer,
    bufferstock integer,
    stockonhand integer,
    quantityrequested integer,
    ordereddate character varying(100) NOT NULL,
    overriddenisa integer,
    maxmonthsofstock integer,
    minmonthsofstock integer,
    eop integer,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now()
);


ALTER TABLE vaccine_order_requisition_line_items OWNER TO postgres;

--
-- Name: vaccine_order_requisition_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_order_requisition_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_order_requisition_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_order_requisition_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_order_requisition_line_items_id_seq OWNED BY vaccine_order_requisition_line_items.id;


--
-- Name: vaccine_order_requisition_master_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_order_requisition_master_columns (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    description character varying(200) NOT NULL,
    label character varying(200) NOT NULL,
    indicator character varying(200) NOT NULL,
    displayorder integer NOT NULL,
    mandatory boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_order_requisition_master_columns OWNER TO postgres;

--
-- Name: vaccine_order_requisition_master_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_order_requisition_master_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_order_requisition_master_columns_id_seq OWNER TO postgres;

--
-- Name: vaccine_order_requisition_master_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_order_requisition_master_columns_id_seq OWNED BY vaccine_order_requisition_master_columns.id;


--
-- Name: vaccine_order_requisition_status_changes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_order_requisition_status_changes (
    id integer NOT NULL,
    orderid integer NOT NULL,
    status character varying(50) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_order_requisition_status_changes OWNER TO postgres;

--
-- Name: vaccine_order_requisition_status_changes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_order_requisition_status_changes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_order_requisition_status_changes_id_seq OWNER TO postgres;

--
-- Name: vaccine_order_requisition_status_changes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_order_requisition_status_changes_id_seq OWNED BY vaccine_order_requisition_status_changes.id;


--
-- Name: vaccine_order_requisitions; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_order_requisitions (
    id integer NOT NULL,
    periodid integer NOT NULL,
    programid integer NOT NULL,
    status character varying(100) NOT NULL,
    supervisorynodeid integer,
    facilityid integer,
    orderdate character varying(100) NOT NULL,
    createdby integer,
    createddate timestamp with time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp with time zone DEFAULT now(),
    emergency boolean DEFAULT false
);


ALTER TABLE vaccine_order_requisitions OWNER TO postgres;

--
-- Name: vaccine_order_requisitions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_order_requisitions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_order_requisitions_id_seq OWNER TO postgres;

--
-- Name: vaccine_order_requisitions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_order_requisitions_id_seq OWNED BY vaccine_order_requisitions.id;


--
-- Name: vaccine_product_doses; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_product_doses (
    id integer NOT NULL,
    doseid integer NOT NULL,
    programid integer NOT NULL,
    productid integer NOT NULL,
    displayname character varying(100) NOT NULL,
    displayorder integer NOT NULL,
    trackmale boolean,
    trackfemale boolean,
    denominatorestimatecategoryid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_product_doses OWNER TO postgres;

--
-- Name: vaccine_product_doses_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_product_doses_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_product_doses_id_seq OWNER TO postgres;

--
-- Name: vaccine_product_doses_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_product_doses_id_seq OWNED BY vaccine_product_doses.id;


--
-- Name: vaccine_program_logistics_columns; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_program_logistics_columns (
    id integer NOT NULL,
    programid integer NOT NULL,
    mastercolumnid integer NOT NULL,
    label character varying(200) NOT NULL,
    displayorder integer NOT NULL,
    visible boolean NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_program_logistics_columns OWNER TO postgres;

--
-- Name: vaccine_program_logistics_columns_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_program_logistics_columns_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_program_logistics_columns_id_seq OWNER TO postgres;

--
-- Name: vaccine_program_logistics_columns_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_program_logistics_columns_id_seq OWNED BY vaccine_program_logistics_columns.id;


--
-- Name: vaccine_report_adverse_effect_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_adverse_effect_line_items (
    id integer NOT NULL,
    reportid integer NOT NULL,
    productid integer NOT NULL,
    date date,
    batch character varying(100) NOT NULL,
    expiry date,
    cases integer NOT NULL,
    notes character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    manufacturer character varying(200),
    isinvestigated boolean DEFAULT false NOT NULL
);


ALTER TABLE vaccine_report_adverse_effect_line_items OWNER TO postgres;

--
-- Name: vaccine_report_adverse_effect_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_adverse_effect_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_adverse_effect_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_adverse_effect_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_adverse_effect_line_items_id_seq OWNED BY vaccine_report_adverse_effect_line_items.id;


--
-- Name: vaccine_report_campaign_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_campaign_line_items (
    id integer NOT NULL,
    reportid integer NOT NULL,
    name character varying(200) NOT NULL,
    venue character varying(200),
    startdate date,
    enddate date,
    childrenvaccinated integer,
    pregnantwomanvaccinated integer,
    otherobjectives character varying(2000),
    vaccinated character varying(200),
    remarks character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_report_campaign_line_items OWNER TO postgres;

--
-- Name: vaccine_report_campaign_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_campaign_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_campaign_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_campaign_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_campaign_line_items_id_seq OWNED BY vaccine_report_campaign_line_items.id;


--
-- Name: vaccine_report_cold_chain_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_cold_chain_line_items (
    id integer NOT NULL,
    reportid integer NOT NULL,
    equipmentinventoryid integer NOT NULL,
    mintemp numeric,
    maxtemp numeric,
    minepisodetemp numeric,
    maxepisodetemp numeric,
    remarks character varying(2000),
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_report_cold_chain_line_items OWNER TO postgres;

--
-- Name: vaccine_report_cold_chain_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_cold_chain_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_cold_chain_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_cold_chain_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_cold_chain_line_items_id_seq OWNED BY vaccine_report_cold_chain_line_items.id;


--
-- Name: vaccine_report_coverage_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_coverage_line_items (
    id integer NOT NULL,
    skipped boolean DEFAULT false NOT NULL,
    reportid integer NOT NULL,
    productid integer NOT NULL,
    doseid integer NOT NULL,
    displayorder integer NOT NULL,
    displayname character varying(100) NOT NULL,
    trackmale boolean DEFAULT true NOT NULL,
    trackfemale boolean DEFAULT true NOT NULL,
    regularmale integer,
    regularfemale integer,
    outreachmale integer,
    outreachfemale integer,
    campaignmale integer,
    campaignfemale integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_report_coverage_line_items OWNER TO postgres;

--
-- Name: vaccine_report_coverage_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_coverage_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_coverage_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_coverage_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_coverage_line_items_id_seq OWNED BY vaccine_report_coverage_line_items.id;


--
-- Name: vaccine_report_disease_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_disease_line_items (
    id integer NOT NULL,
    reportid integer NOT NULL,
    diseaseid integer NOT NULL,
    diseasename character varying(200) NOT NULL,
    displayorder integer NOT NULL,
    cases integer,
    death integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    cumulative integer
);


ALTER TABLE vaccine_report_disease_line_items OWNER TO postgres;

--
-- Name: vaccine_report_disease_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_disease_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_disease_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_disease_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_disease_line_items_id_seq OWNED BY vaccine_report_disease_line_items.id;


--
-- Name: vaccine_report_logistics_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_logistics_line_items (
    id integer NOT NULL,
    reportid integer NOT NULL,
    productid integer NOT NULL,
    productcode character varying(100) NOT NULL,
    productname character varying(200) NOT NULL,
    displayorder integer NOT NULL,
    openingbalance integer,
    quantityreceived integer,
    quantityissued integer,
    quantityvvmalerted integer,
    quantityfreezed integer,
    quantityexpired integer,
    quantitydiscardedunopened integer,
    quantitydiscardedopened integer,
    quantitywastedother integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    productcategory character varying(200),
    closingbalance integer,
    daysstockedout integer,
    remarks character varying(500),
    discardingreasonid integer,
    discardingreasonexplanation character varying(500)
);


ALTER TABLE vaccine_report_logistics_line_items OWNER TO postgres;

--
-- Name: vaccine_report_logistics_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_logistics_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_logistics_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_logistics_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_logistics_line_items_id_seq OWNED BY vaccine_report_logistics_line_items.id;


--
-- Name: vaccine_report_status_changes; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_status_changes (
    id integer NOT NULL,
    reportid integer NOT NULL,
    status character varying(50) NOT NULL,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now()
);


ALTER TABLE vaccine_report_status_changes OWNER TO postgres;

--
-- Name: vaccine_report_status_changes_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_status_changes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_status_changes_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_status_changes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_status_changes_id_seq OWNED BY vaccine_report_status_changes.id;


--
-- Name: vaccine_report_vitamin_supplementation_line_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_report_vitamin_supplementation_line_items (
    id integer NOT NULL,
    reportid integer NOT NULL,
    vaccinevitaminid integer NOT NULL,
    vitaminagegroupid integer NOT NULL,
    vitaminname character varying(100) NOT NULL,
    displayorder integer NOT NULL,
    malevalue integer,
    femalevalue integer,
    createdby integer,
    createddate date DEFAULT now(),
    modifiedby integer,
    modifieddate date DEFAULT now()
);


ALTER TABLE vaccine_report_vitamin_supplementation_line_items OWNER TO postgres;

--
-- Name: vaccine_report_vitamin_supplementation_line_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_report_vitamin_supplementation_line_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_report_vitamin_supplementation_line_items_id_seq OWNER TO postgres;

--
-- Name: vaccine_report_vitamin_supplementation_line_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_report_vitamin_supplementation_line_items_id_seq OWNED BY vaccine_report_vitamin_supplementation_line_items.id;


--
-- Name: vaccine_reports; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_reports (
    id integer NOT NULL,
    periodid integer NOT NULL,
    programid integer NOT NULL,
    facilityid integer NOT NULL,
    status character varying(100) NOT NULL,
    supervisorynodeid integer,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    majorimmunizationactivities character varying(4000),
    fixedimmunizationsessions integer,
    outreachimmunizationsessions integer,
    outreachimmunizationsessionscanceled integer,
    submissiondate date
);


ALTER TABLE vaccine_reports OWNER TO postgres;

--
-- Name: vaccine_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_reports_id_seq OWNER TO postgres;

--
-- Name: vaccine_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_reports_id_seq OWNED BY vaccine_reports.id;


--
-- Name: vaccine_vitamin_supplementation_age_groups; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_vitamin_supplementation_age_groups (
    id integer NOT NULL,
    name character varying(200) NOT NULL,
    description character varying(500),
    displayorder integer NOT NULL,
    createdby integer,
    createddate date DEFAULT now(),
    modifiedby integer,
    modifieddate date DEFAULT now()
);


ALTER TABLE vaccine_vitamin_supplementation_age_groups OWNER TO postgres;

--
-- Name: vaccine_vitamin_supplementation_age_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_vitamin_supplementation_age_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_vitamin_supplementation_age_groups_id_seq OWNER TO postgres;

--
-- Name: vaccine_vitamin_supplementation_age_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_vitamin_supplementation_age_groups_id_seq OWNED BY vaccine_vitamin_supplementation_age_groups.id;


--
-- Name: vaccine_vitamins; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE vaccine_vitamins (
    id integer NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(200) NOT NULL,
    description character varying(5000) NOT NULL,
    displayorder integer NOT NULL,
    createdby integer,
    createddate date DEFAULT now(),
    modifiedby integer,
    modifieddate date DEFAULT now()
);


ALTER TABLE vaccine_vitamins OWNER TO postgres;

--
-- Name: vaccine_vitamins_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE vaccine_vitamins_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE vaccine_vitamins_id_seq OWNER TO postgres;

--
-- Name: vaccine_vitamins_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE vaccine_vitamins_id_seq OWNED BY vaccine_vitamins.id;


--
-- Name: var_details; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE var_details (
    id integer NOT NULL,
    awbnumber character varying(255),
    flightnumber character varying(255),
    estimatetimeofarrival timestamp without time zone DEFAULT now(),
    actualtimeofarrival timestamp without time zone DEFAULT now(),
    numberofitemsinspected integer,
    coolanttype character varying(255),
    tempraturemonitor character varying(255),
    purchaseordernumber character varying(255),
    clearingagent character varying(255),
    labels character varying(255),
    comments character varying(255),
    invoice character varying(255),
    packinglist character varying(255),
    releasecerificate character varying(255),
    airwaybill character varying(255),
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer,
    deliverystatus character varying(255),
    destnationairport character varying(255)
);


ALTER TABLE var_details OWNER TO postgres;

--
-- Name: TABLE var_details; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE var_details IS 'Vaccine Arrival Report Details';


--
-- Name: var_details_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE var_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE var_details_id_seq OWNER TO postgres;

--
-- Name: var_details_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE var_details_id_seq OWNED BY var_details.id;


--
-- Name: var_item_alarms; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE var_item_alarms (
    id integer NOT NULL,
    vardetailsid integer,
    productid integer,
    boxnumber integer,
    lotnumber character varying,
    alarmtemprature character varying,
    coldchainmonitor character varying,
    timeofinspection timestamp without time zone,
    gtinlookupid integer,
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer
);


ALTER TABLE var_item_alarms OWNER TO postgres;

--
-- Name: TABLE var_item_alarms; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE var_item_alarms IS 'Store Alarm Information for Items with Problems';


--
-- Name: var_item_alarms_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE var_item_alarms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE var_item_alarms_id_seq OWNER TO postgres;

--
-- Name: var_item_alarms_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE var_item_alarms_id_seq OWNED BY var_item_alarms.id;


--
-- Name: var_item_partials; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE var_item_partials (
    id integer NOT NULL,
    vardetailsid integer,
    productid integer,
    boxnumber integer,
    lotnumber character varying(255),
    expectednumber integer,
    availablenumber integer,
    gtinlookupid integer,
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer
);


ALTER TABLE var_item_partials OWNER TO postgres;

--
-- Name: TABLE var_item_partials; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE var_item_partials IS 'Store The information about Partial boxes';


--
-- Name: var_item_partials_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE var_item_partials_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE var_item_partials_id_seq OWNER TO postgres;

--
-- Name: var_item_partials_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE var_item_partials_id_seq OWNED BY var_item_partials.id;


--
-- Name: var_items; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE var_items (
    id integer NOT NULL,
    vardetailsid integer NOT NULL,
    shipmentnumber character varying(255) NOT NULL,
    productid integer NOT NULL,
    manufacturedate timestamp without time zone DEFAULT now(),
    expiredate timestamp without time zone DEFAULT now(),
    lotnumber character varying(255),
    numberofdoses integer NOT NULL,
    derliverystatus character varying(255),
    numberreceived integer,
    physicaldamage character varying(255),
    damagedamount integer,
    vvmstatus character varying(255),
    problems text,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    gtinlookupid integer NOT NULL
);


ALTER TABLE var_items OWNER TO postgres;

--
-- Name: TABLE var_items; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE var_items IS 'Vaccine Arrival Report Items';


--
-- Name: var_items_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE var_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE var_items_id_seq OWNER TO postgres;

--
-- Name: var_items_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE var_items_id_seq OWNED BY var_items.id;


--
-- Name: vw_cce_repair_management; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_cce_repair_management AS
 SELECT ei.id,
    ei.programid AS pid,
    ei.facilityid AS fid,
    f.geographiczoneid AS geoid,
    ft.code AS facility_code,
    f.name AS facility_name,
    e.model,
    et.name AS type_name,
    eos.name AS operationalstatus,
    count(1) OVER (PARTITION BY eos.name, ei.facilityid) AS operationalstatuscount,
    eet.name AS energytype,
    count(1) OVER (PARTITION BY eet.name, ei.facilityid) AS energytypecount
   FROM (((((((equipment_inventories ei
     JOIN equipment_inventory_statuses eis ON ((eis.id = ( SELECT eisb.id
           FROM equipment_inventory_statuses eisb
          WHERE (eisb.inventoryid = ei.id)
          ORDER BY eisb.createddate DESC
         LIMIT 1))))
     JOIN equipment_operational_status eos ON ((eis.statusid = eos.id)))
     JOIN equipments e ON ((ei.equipmentid = e.id)))
     JOIN equipment_types et ON ((e.equipmenttypeid = et.id)))
     JOIN facilities f ON ((f.id = ei.facilityid)))
     JOIN facility_types ft ON ((ft.id = f.typeid)))
     LEFT JOIN equipment_energy_types eet ON ((e.energytypeid = eet.id)))
  WHERE (et.iscoldchain IS TRUE);


ALTER TABLE vw_cce_repair_management OWNER TO postgres;

--
-- Name: vw_cce_repair_management_not_functional; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_cce_repair_management_not_functional AS
 SELECT notfunctional.facilityid,
    notfunctional.facilityid AS fid,
    notfunctional.pid AS programid,
    notfunctional.model,
    notfunctional.name AS operationalstatus,
    notfunctional.modifieddate,
    notfunctional.modifiedby,
    notfunctional.fname AS facilityname,
    count(1) OVER (PARTITION BY notfunctional.notfunctionalstatusid, notfunctional.facilityid) AS notfunctionalstatuscount
   FROM ( SELECT DISTINCT ON (eis.inventoryid) eis.inventoryid,
            eis.notfunctionalstatusid,
            eos.name AS status,
            eosnf.name,
            ei.facilityid,
            f.name AS fname,
            ei.programid AS pid,
            e.model,
            ei.modifieddate,
            ei.modifiedby
           FROM ((((((equipment_inventory_statuses eis
             LEFT JOIN equipment_operational_status eosnf ON ((eosnf.id = eis.notfunctionalstatusid)))
             LEFT JOIN equipment_operational_status eos ON ((eos.id = eis.statusid)))
             LEFT JOIN equipment_inventories ei ON ((ei.id = eis.inventoryid)))
             JOIN equipments e ON ((ei.equipmentid = e.id)))
             JOIN equipment_types et ON ((e.equipmenttypeid = et.id)))
             JOIN facilities f ON ((f.id = ei.facilityid)))
          WHERE (et.iscoldchain IS TRUE)
          ORDER BY eis.inventoryid, eis.createddate DESC) notfunctional
  WHERE (notfunctional.name IS NOT NULL);


ALTER TABLE vw_cce_repair_management_not_functional OWNER TO postgres;

--
-- Name: vw_cold_chain_equipment; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_cold_chain_equipment AS
 SELECT nested.id AS "equipmentId",
    nested.manufacturer,
    nested.model,
    eet.name AS "energyTypeName",
    ecce.ccecode AS "equipmentColdChainEquipmentsCode",
    ecce.refrigerant,
    ecce.refrigeratorcapacity,
    ecce.freezercapacity,
    eos1.functional_status,
    eos2.non_functional_status,
    ei.yearofinstallation,
    (date_part('year'::text, ('now'::text)::date) - (ei.yearofinstallation)::double precision) AS "equipmentAge",
    (ei.yearofinstallation + 11) AS "yearOfReplacement",
    facilities.id AS "facilityId",
    facilities.name AS "facilityName",
    ft.name AS "facilityTypeName",
    facilities.address1 AS "facilityAddress1",
    facilities.address2 AS "facilityAddress2",
    facilities.haselectricity AS "facilityHasElectricity",
    fo.text AS "facilityOperator",
    gz.id AS geozoneid,
    gz.name AS geozonename,
    geo_zone_tree.hierarchy AS "geozoneHierarchy"
   FROM (((((((((((equipment_inventories ei
     JOIN ( SELECT DISTINCT e.id,
            e.name,
            e.equipmenttypeid,
            e.createdby,
            e.createddate,
            e.modifiedby,
            e.modifieddate,
            e.manufacturer,
            e.model,
            e.energytypeid
           FROM (equipments e
             JOIN equipment_types et ON ((e.equipmenttypeid = et.id)))
          WHERE (et.iscoldchain = true)) nested ON ((nested.id = ei.equipmentid)))
     LEFT JOIN equipment_cold_chain_equipments ecce ON ((nested.id = ecce.equipmentid)))
     LEFT JOIN equipment_energy_types eet ON ((nested.energytypeid = eet.id)))
     LEFT JOIN facilities ON ((ei.facilityid = facilities.id)))
     LEFT JOIN facility_types ft ON ((facilities.typeid = ft.id)))
     LEFT JOIN equipment_inventory_statuses eis ON (((ei.id = eis.inventoryid) AND (eis.createddate = ( SELECT max(equipment_inventory_statuses.createddate) AS max
           FROM equipment_inventory_statuses
          WHERE (equipment_inventory_statuses.inventoryid = ei.id))))))
     LEFT JOIN ( SELECT equipment_operational_status.id,
            equipment_operational_status.name AS functional_status
           FROM equipment_operational_status) eos1 ON ((eos1.id = eis.statusid)))
     LEFT JOIN ( SELECT equipment_operational_status.id,
            equipment_operational_status.name AS non_functional_status
           FROM equipment_operational_status) eos2 ON ((eos2.id = eis.notfunctionalstatusid)))
     LEFT JOIN facility_operators fo ON ((facilities.operatedbyid = fo.id)))
     LEFT JOIN geographic_zones gz ON ((facilities.geographiczoneid = gz.id)))
     LEFT JOIN ( SELECT fn_get_geozonetree_names.hierarchy,
            fn_get_geozonetree_names.leafid
           FROM fn_get_geozonetree_names() fn_get_geozonetree_names(hierarchy, leafid)) geo_zone_tree ON ((gz.id = geo_zone_tree.leafid)));


ALTER TABLE vw_cold_chain_equipment OWNER TO postgres;

--
-- Name: vw_district_financial_summary; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_district_financial_summary AS
 SELECT processing_periods.id AS periodid,
    processing_periods.name AS period,
    processing_periods.startdate,
    processing_periods.enddate,
    processing_periods.scheduleid,
    processing_schedules.name AS schedule,
    facility_types.id AS facilitytypeid,
    facility_types.name AS facilitytype,
    facilities.code AS facilitycode,
    facilities.name AS facility,
    facilities.id AS facility_id,
    requisitions.id AS rnrid,
    requisitions.status,
    geographic_zones.name AS region,
    p.name AS program,
    p.id AS programid,
    requisitions.fullsupplyitemssubmittedcost,
    requisitions.nonfullsupplyitemssubmittedcost,
    geographic_zones.id AS zoneid
   FROM ((((((requisitions
     JOIN facilities ON ((facilities.id = requisitions.facilityid)))
     JOIN facility_types ON ((facility_types.id = facilities.typeid)))
     JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
     JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
     JOIN programs p ON ((p.id = requisitions.programid)))
  WHERE ((requisitions.status)::text = ANY (ARRAY[('IN_APPROVAL'::character varying)::text, ('APPROVED'::character varying)::text, ('RELEASED'::character varying)::text]));


ALTER TABLE vw_district_financial_summary OWNER TO postgres;

--
-- Name: vw_districts; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_districts AS
 SELECT d.id AS district_id,
    d.name AS district_name,
    r.id AS region_id,
    r.name AS region_name,
    z.id AS zone_id,
    z.name AS zone_name,
    z.parentid AS parent
   FROM ((geographic_zones d
     JOIN geographic_zones r ON ((d.parentid = r.id)))
     JOIN geographic_zones z ON ((z.id = r.parentid)));


ALTER TABLE vw_districts OWNER TO postgres;

--
-- Name: vw_e2e_stock_status; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_e2e_stock_status AS
 SELECT d.facilityid,
    d.facilityname,
    d.geographiczoneid,
    d.geographiczonename AS districtname,
    d.programcode,
    d.programname,
    date_part('year'::text, d.periodstartdate) AS reportyear,
    date_part('month'::text, d.periodstartdate) AS reportmonth,
    (((date_part('month'::text, d.periodstartdate) / (4)::double precision))::integer + 1) AS reportquarter,
    (d.createddate)::date AS reporteddate,
    d.periodid,
    d.processingperiodname AS periodname,
    d.productid,
    d.productcode,
    d.productprimaryname AS productname,
    d.openingbalance,
    d.quantityreceived AS received,
    d.dispensed AS issues,
    d.adjustment,
    d.soh AS stockonhand,
    d.amc,
    d.mos,
    d.stocking AS stockstatus,
    d.stockoutdays,
    d.quantityordered,
    d.quantityshipped AS quantitysupplied,
    d.dateordered,
    d.dateshipped AS datesupplied,
    d.rmnch
   FROM dw_orders d;


ALTER TABLE vw_e2e_stock_status OWNER TO postgres;

--
-- Name: vw_equipment_list_by_donor; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_equipment_list_by_donor AS
 SELECT geographic_zones.name AS district,
    facilities.name AS facilityname,
    donors.shortname AS donor,
    equipment_inventories.sourceoffund,
    equipments.name AS equipment_name,
    equipments.model,
    equipment_inventories.yearofinstallation,
        CASE
            WHEN (equipment_inventories.isactive = true) THEN 'Yes'::text
            ELSE 'No'::text
        END AS isactive,
        CASE
            WHEN (equipment_inventories.datedecommissioned IS NULL) THEN '-'::text
            ELSE (equipment_inventories.datedecommissioned)::text
        END AS datedecommissioned,
        CASE
            WHEN (equipment_inventories.replacementrecommended = false) THEN 'No'::text
            ELSE 'Yes'::text
        END AS replacementrecommended,
    facilities.id AS facility_id,
    programs.id AS programid,
    equipments.id AS equipment_id,
    equipment_inventory_statuses.statusid AS status_id,
    equipment_types.id AS equipmenttype_id,
    facilities.geographiczoneid,
    facilities.typeid AS ftype_id,
    vw_districts.district_id,
    vw_districts.zone_id,
    vw_districts.region_id,
    vw_districts.parent,
    donors.id AS donorid
   FROM (((((((((equipment_inventories
     JOIN equipments ON ((equipment_inventories.equipmentid = equipments.id)))
     JOIN programs ON ((equipment_inventories.programid = programs.id)))
     JOIN facilities ON ((facilities.id = equipment_inventories.facilityid)))
     JOIN facility_types ON ((facilities.typeid = facility_types.id)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
     JOIN equipment_types ON ((equipment_types.id = equipments.equipmenttypeid)))
     LEFT JOIN donors ON ((donors.id = equipment_inventories.primarydonorid)))
     JOIN vw_districts ON ((vw_districts.district_id = facilities.geographiczoneid)))
     JOIN equipment_inventory_statuses ON ((equipment_inventory_statuses.inventoryid = equipment_inventories.id)))
  ORDER BY geographic_zones.name, facilities.name, equipments.model;


ALTER TABLE vw_equipment_list_by_donor OWNER TO postgres;

--
-- Name: vw_expected_facilities; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_expected_facilities AS
 SELECT facilities.id AS facilityid,
    facilities.name AS facilityname,
    ps.programid,
    pp.scheduleid,
    pp.id AS periodid,
    pp.name AS periodname,
    pp.startdate,
    pp.enddate,
    gz.id AS geographiczoneid,
    gz.name AS geographiczonename
   FROM (((((facilities
     JOIN programs_supported ps ON ((ps.facilityid = facilities.id)))
     JOIN geographic_zones gz ON ((gz.id = facilities.geographiczoneid)))
     JOIN requisition_group_members rgm ON ((rgm.facilityid = facilities.id)))
     JOIN requisition_group_program_schedules rgps ON (((rgps.requisitiongroupid = rgm.requisitiongroupid) AND (rgps.programid = ps.programid))))
     JOIN processing_periods pp ON ((pp.scheduleid = rgps.scheduleid)))
  WHERE (gz.levelid = ( SELECT max(geographic_levels.id) AS max
           FROM geographic_levels));


ALTER TABLE vw_expected_facilities OWNER TO postgres;

--
-- Name: vw_facility_requisitions; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_facility_requisitions AS
 SELECT facilities.id AS facilityid,
    facilities.code AS facilitycode,
    facilities.name AS facilityname,
    requisitions.id AS rnrid,
    requisitions.periodid,
    requisitions.status,
    facilities.geographiczoneid,
    facilities.enabled,
    facilities.sdp,
    facilities.typeid,
    requisitions.programid,
    requisitions.emergency,
    requisitions.createddate,
    geographic_zones.name AS geographiczonename
   FROM ((requisitions
     JOIN facilities ON ((facilities.id = requisitions.facilityid)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)));


ALTER TABLE vw_facility_requisitions OWNER TO postgres;

--
-- Name: vw_lab_equipment_status; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_lab_equipment_status AS
 SELECT programs.name AS program,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    facility_types.name AS facility_type,
    vw_districts.district_name AS disrict,
    vw_districts.zone_name AS zone,
    equipment_types.name AS equipment_type,
    equipments.model AS equipment_model,
    equipment_inventories.serialnumber AS serial_number,
    equipments.name AS equipment_name,
    equipment_operational_status.name AS equipment_status,
    facilities.latitude,
    facilities.longitude,
    facilities.id AS facility_id,
    programs.id AS programid,
    equipments.id AS equipment_id,
    equipment_operational_status.id AS status_id,
    facilities.geographiczoneid,
    facilities.typeid AS ftype_id,
    vw_districts.district_id,
    vw_districts.zone_id,
    vw_districts.region_id,
    vw_districts.parent,
    equipment_types.id AS equipmenttype_id
   FROM (((((((((equipment_inventories
     JOIN facilities ON ((facilities.id = equipment_inventories.facilityid)))
     JOIN facility_types ON ((facility_types.id = facilities.typeid)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
     JOIN programs ON ((equipment_inventories.programid = programs.id)))
     JOIN vw_districts ON ((vw_districts.district_id = facilities.geographiczoneid)))
     JOIN equipments ON ((equipments.id = equipment_inventories.equipmentid)))
     JOIN equipment_types ON ((equipment_types.id = equipments.equipmenttypeid)))
     JOIN equipment_inventory_statuses ON ((equipment_inventory_statuses.inventoryid = equipment_inventories.id)))
     JOIN equipment_operational_status ON ((equipment_operational_status.id = equipment_inventory_statuses.statusid)))
  ORDER BY facilities.name;


ALTER TABLE vw_lab_equipment_status OWNER TO postgres;

--
-- Name: vw_order_fill_rate; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_order_fill_rate AS
 SELECT dw_orders.status,
    dw_orders.facilityid,
    dw_orders.periodid,
    dw_orders.productfullname AS product,
    products.code AS productcode,
    facilities.name AS facilityname,
    dw_orders.scheduleid,
    dw_orders.facilitytypeid,
    dw_orders.productid,
    dw_orders.productcategoryid,
    dw_orders.programid,
    dw_orders.geographiczoneid AS zoneid,
    dw_orders.geographiczonename AS zonename,
    sum((COALESCE(dw_orders.quantityapprovedprev, 0))::numeric) AS quantityapproved,
    sum((COALESCE(dw_orders.quantityreceived, 0))::numeric) AS quantityreceived,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityapprovedprev, 0) = 0) THEN (0)::numeric
            ELSE
            CASE
                WHEN (dw_orders.quantityapprovedprev > 0) THEN (1)::numeric
                ELSE (0)::numeric
            END
        END) AS totalproductsapproved,
    sum(
        CASE
            WHEN (COALESCE(dw_orders.quantityreceived, 0) = 0) THEN (0)::numeric
            ELSE
            CASE
                WHEN (dw_orders.quantityreceived > 0) THEN (1)::numeric
                ELSE (0)::numeric
            END
        END) AS totalproductsreceived,
    sum(
        CASE
            WHEN ((COALESCE(dw_orders.quantityreceived, 0) > 1) AND (COALESCE(dw_orders.quantityapprovedprev, 0) = 0)) THEN (1)::numeric
            ELSE (0)::numeric
        END) AS totalproductspushed
   FROM ((dw_orders
     JOIN products ON (((products.id = dw_orders.productid) AND ((products.primaryname)::text = (dw_orders.productprimaryname)::text))))
     JOIN facilities ON ((facilities.id = dw_orders.facilityid)))
  WHERE ((dw_orders.status)::text = ANY (ARRAY[('RELEASED'::character varying)::text]))
  GROUP BY dw_orders.scheduleid, dw_orders.facilitytypeid, dw_orders.productid, dw_orders.status, dw_orders.facilityid, dw_orders.periodid, dw_orders.productfullname, products.code, facilities.name, dw_orders.productcategoryid, dw_orders.programid, dw_orders.geographiczoneid, dw_orders.geographiczonename;


ALTER TABLE vw_order_fill_rate OWNER TO postgres;

--
-- Name: vw_program_facility_supplier; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_program_facility_supplier AS
 SELECT programs.name AS program_name,
    facilities.name AS facility_name,
    supply_lines.supplyingfacilityid AS supplying_facility_id,
    supervisory_nodes.name AS supervisory_node_name,
    supervisory_nodes.id AS supervisory_node_id,
    programs.id AS program_id,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    supply_lines.supervisorynodeid AS supply_line_id
   FROM (((supply_lines
     JOIN supervisory_nodes ON ((supply_lines.supervisorynodeid = supervisory_nodes.id)))
     JOIN facilities ON ((supply_lines.supplyingfacilityid = facilities.id)))
     JOIN programs ON ((supply_lines.programid = programs.id)));


ALTER TABLE vw_program_facility_supplier OWNER TO postgres;

--
-- Name: vw_regimen_district_distribution; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_regimen_district_distribution AS
 SELECT r.programid,
    rgps.scheduleid,
    pp.id AS periodid,
    regimens.categoryid,
    regimens.id AS regimenid,
    li.name AS regimen,
    li.patientsontreatment,
    li.patientstoinitiatetreatment,
    li.patientsstoppedtreatment,
    r.facilityid,
    r.status,
    f.name AS facilityname,
    f.code AS facilitycode,
    f.typeid AS facilitytypeid,
    ft.name AS facilitytype,
    d.district_name AS district,
    d.district_id AS districtid,
    d.region_id AS regionid,
    d.region_name AS region,
    d.zone_id AS zoneid,
    d.zone_name AS zone,
    d.parent
   FROM (((((((((regimen_line_items li
     JOIN requisitions r ON ((li.rnrid = r.id)))
     JOIN facilities f ON ((r.facilityid = f.id)))
     JOIN facility_types ft ON ((f.typeid = ft.id)))
     JOIN vw_districts d ON ((f.geographiczoneid = d.district_id)))
     JOIN requisition_group_members rgm ON ((r.facilityid = rgm.facilityid)))
     JOIN programs_supported ps ON (((r.programid = ps.programid) AND (r.facilityid = ps.facilityid))))
     JOIN regimens ON (((li.code)::text = (regimens.code)::text)))
     JOIN processing_periods pp ON ((r.periodid = pp.id)))
     JOIN requisition_group_program_schedules rgps ON (((rgm.requisitiongroupid = rgps.requisitiongroupid) AND (pp.scheduleid = rgps.scheduleid))));


ALTER TABLE vw_regimen_district_distribution OWNER TO postgres;

--
-- Name: vw_replacement_plan_summary; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_replacement_plan_summary AS
 SELECT (( SELECT (date_part('year'::text, ('now'::text)::date))::integer AS date_part) - ( SELECT (configuration_settings.value)::integer AS value
           FROM configuration_settings
          WHERE ((configuration_settings.key)::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text))) AS this_year,
    ( SELECT (date_part('year'::text, ('now'::text)::date))::integer AS date_part) AS replacementyearone,
    (( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 1)) - ( SELECT (configuration_settings.value)::integer AS value
           FROM configuration_settings
          WHERE ((configuration_settings.key)::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text))) AS second_year,
    ( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 1)) AS replacementyeartwo,
    (( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 2)) - ( SELECT (configuration_settings.value)::integer AS value
           FROM configuration_settings
          WHERE ((configuration_settings.key)::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text))) AS third_year,
    ( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 2)) AS replacementyearthree,
    (( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 3)) - ( SELECT (configuration_settings.value)::integer AS value
           FROM configuration_settings
          WHERE ((configuration_settings.key)::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text))) AS fourth_year,
    ( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 3)) AS replacementyearfour,
    (( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 4)) - ( SELECT (configuration_settings.value)::integer AS value
           FROM configuration_settings
          WHERE ((configuration_settings.key)::text = 'EQUIPMENT_REPLACEMENT_YEAR'::text))) AS fifth_year,
    ( SELECT ((date_part('year'::text, ('now'::text)::date))::integer + 4)) AS replacementyearfive,
    ei.facilityid,
    ei.programid,
    f.typeid AS facilitytypeid,
    vwd.region_name AS region,
    vwd.district_name AS district,
    f.name AS facilityname,
    ft.code AS facilitytypecode,
    ft.name AS facilitytypename,
    e.manufacturer AS brand,
    e.model,
    COALESCE(cce.refrigeratorcapacity, (0)::numeric) AS capacity,
    eos.name AS working_status,
    COALESCE(brkd.break_down, (0)::bigint) AS break_down,
        CASE
            WHEN ((eose.name)::text = 'Obsolete'::text) THEN 'O'::character varying
            WHEN ((eose.name)::text = 'Waiting For Repair'::text) THEN 'W'::character varying
            WHEN ((eose.name)::text = 'Waiting For Spare Parts'::text) THEN 'S'::character varying
            ELSE eose.name
        END AS status,
    e.name AS equipment_name,
    ei.yearofinstallation,
    COALESCE(ei.purchaseprice, (0)::numeric) AS purchaseprice,
    eet.name AS sourceofenergy,
    ei.serialnumber,
    COALESCE((( SELECT (date_part('year'::text, ('now'::text)::date))::integer AS date_part) - ei.yearofinstallation), 0) AS age
   FROM (((((((((((equipment_inventories ei
     JOIN equipment_inventory_statuses eis ON ((eis.id = ( SELECT eisb.id
           FROM equipment_inventory_statuses eisb
          WHERE (eisb.inventoryid = ei.id)
          ORDER BY eisb.createddate DESC
         LIMIT 1))))
     JOIN equipment_operational_status eos ON ((eis.statusid = eos.id)))
     LEFT JOIN equipment_operational_status eose ON ((eis.notfunctionalstatusid = eose.id)))
     JOIN equipments e ON ((ei.equipmentid = e.id)))
     JOIN equipment_cold_chain_equipments cce ON ((cce.equipmentid = e.id)))
     JOIN equipment_types et ON ((e.equipmenttypeid = et.id)))
     JOIN facilities f ON ((f.id = ei.facilityid)))
     JOIN facility_types ft ON ((ft.id = f.typeid)))
     JOIN vw_districts vwd ON ((vwd.district_id = f.geographiczoneid)))
     LEFT JOIN ( SELECT eis_1.inventoryid AS id,
            COALESCE(count(eis_1.id), (0)::bigint) AS break_down
           FROM (equipment_inventory_statuses eis_1
             LEFT JOIN equipment_operational_status eos_1 ON ((eos_1.id = eis_1.statusid)))
          WHERE ((eos_1.name)::text = 'Not Functional'::text)
          GROUP BY eis_1.inventoryid) brkd ON ((brkd.id = ei.id)))
     LEFT JOIN equipment_energy_types eet ON ((e.energytypeid = eet.id)))
  WHERE (et.iscoldchain IS TRUE);


ALTER TABLE vw_replacement_plan_summary OWNER TO postgres;

--
-- Name: vw_requisition_adjustment; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_requisition_adjustment AS
 SELECT programs.id AS program_id,
    programs.name AS program_name,
    processing_periods.id AS processing_periods_id,
    processing_periods.name AS processing_periods_name,
    processing_periods.startdate AS processing_periods_start_date,
    processing_periods.enddate AS processing_periods_end_date,
    processing_schedules.id AS processing_schedules_id,
    processing_schedules.name AS processing_schedules_name,
    facility_types.name AS facility_type_name,
    facility_types.id AS facility_type_id,
    facilities.code AS facility_code,
    facilities.id AS facility_id,
    facilities.name AS facility_name,
    requisition_line_items.id AS requisition_line_item_id,
    requisition_line_items.productcode,
    requisition_line_items.product,
    products.id AS product_id,
    product_categories.name AS product_category_name,
    product_categories.id AS product_category_id,
    requisitions.status AS req_status,
    requisition_line_items.beginningbalance,
    requisition_line_items.quantityreceived,
    requisition_line_items.quantitydispensed,
    requisition_line_items.stockinhand,
    requisition_line_items.quantityrequested,
    requisition_line_items.calculatedorderquantity,
    requisition_line_items.quantityapproved,
    requisition_line_items.totallossesandadjustments,
    requisition_line_items.newpatientcount,
    requisition_line_items.stockoutdays,
    requisition_line_items.normalizedconsumption,
    requisition_line_items.amc,
    requisition_line_items.maxmonthsofstock,
    requisition_line_items.maxstockquantity,
    requisition_line_items.packstoship,
    requisition_line_items.packsize,
    requisition_line_items.fullsupply,
    requisition_line_item_losses_adjustments.type AS adjustment_type,
    requisition_line_item_losses_adjustments.quantity AS adjutment_qty,
    losses_adjustments_types.displayorder AS adjustment_display_order,
    losses_adjustments_types.additive AS adjustment_additive,
    (fn_get_supplying_facility_name(requisitions.supervisorynodeid))::text AS supplying_facility_name,
    requisition_line_items.id
   FROM ((((((((((((requisition_line_items
     JOIN requisitions ON ((requisition_line_items.rnrid = requisitions.id)))
     JOIN products ON (((requisition_line_items.productcode)::text = (products.code)::text)))
     JOIN programs ON ((requisitions.programid = programs.id)))
     JOIN program_products ON (((products.id = program_products.productid) AND (program_products.programid = programs.id))))
     JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
     JOIN product_categories ON ((program_products.productcategoryid = product_categories.id)))
     JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
     JOIN facilities ON ((requisitions.facilityid = facilities.id)))
     JOIN facility_types ON ((facilities.typeid = facility_types.id)))
     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
     JOIN requisition_line_item_losses_adjustments ON ((requisition_line_items.id = requisition_line_item_losses_adjustments.requisitionlineitemid)))
     JOIN losses_adjustments_types ON (((requisition_line_item_losses_adjustments.type)::text = (losses_adjustments_types.name)::text)));


ALTER TABLE vw_requisition_adjustment OWNER TO postgres;

--
-- Name: vw_requisition_detail; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_requisition_detail AS
 SELECT programs.id AS program_id,
    programs.name AS program_name,
    products.id AS product_id,
    products.code AS product_code,
    products.primaryname AS product_primaryname,
    products.description AS product_description,
    products.tracer AS indicator_product,
    processing_periods.id AS processing_periods_id,
    processing_periods.name AS processing_periods_name,
    processing_periods.startdate AS processing_periods_start_date,
    processing_periods.enddate AS processing_periods_end_date,
    processing_periods.scheduleid AS processing_schedules_id,
    facility_types.id AS facility_type_id,
    facility_types.name AS facility_type_name,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    requisition_line_items.productcode,
    requisition_line_items.product,
    requisition_line_items.beginningbalance,
    requisition_line_items.quantityreceived,
    requisition_line_items.quantitydispensed,
    requisition_line_items.stockinhand,
    requisition_line_items.quantityrequested,
    requisition_line_items.calculatedorderquantity,
    requisition_line_items.quantityapproved,
    requisition_line_items.totallossesandadjustments,
    requisition_line_items.newpatientcount,
    requisition_line_items.stockoutdays,
    requisition_line_items.normalizedconsumption,
    requisition_line_items.amc,
    requisition_line_items.maxmonthsofstock,
    requisition_line_items.maxstockquantity,
    requisition_line_items.packstoship,
    requisition_line_items.packsize,
    requisition_line_items.fullsupply,
    facilities.id AS facility_id,
    requisitions.id AS req_id,
    requisitions.status AS req_status,
    requisition_line_items.id AS req_line_id,
    geographic_zones.id AS zone_id,
    geographic_zones.name AS region,
    facility_types.nominalmaxmonth,
    facility_types.nominaleop,
    dosage_units.code AS du_code,
    product_forms.code AS pf_code,
    products.dispensingunit,
    program_products.productcategoryid AS categoryid,
    products.productgroupid,
    processing_periods.scheduleid,
    requisitions.emergency
   FROM ((((((((((((requisition_line_items
     JOIN requisitions ON ((requisition_line_items.rnrid = requisitions.id)))
     JOIN products ON (((requisition_line_items.productcode)::text = (products.code)::text)))
     JOIN programs ON ((requisitions.programid = programs.id)))
     JOIN program_products ON (((products.id = program_products.productid) AND (program_products.programid = programs.id))))
     JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
     JOIN product_categories ON ((program_products.productcategoryid = product_categories.id)))
     JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
     JOIN facilities ON ((requisitions.facilityid = facilities.id)))
     JOIN facility_types ON ((facilities.typeid = facility_types.id)))
     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
     JOIN product_forms ON ((products.formid = product_forms.id)))
     JOIN dosage_units ON ((products.dosageunitid = dosage_units.id)));


ALTER TABLE vw_requisition_detail OWNER TO postgres;

--
-- Name: vw_requisition_detail_dw; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_requisition_detail_dw AS
 SELECT programs.id AS program_id,
    programs.code AS program_code,
    programs.name AS program_name,
    processing_schedules.id AS processing_schedule_id,
    processing_schedules.name AS processing_schedule_name,
    processing_periods.id AS period_id,
    processing_periods.name AS period_name,
    processing_periods.startdate AS period_start_date,
    processing_periods.enddate AS period_end_date,
    geographic_zones.id AS geographic_zone_id,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.levelid AS geographic_zone_level,
    facility_types.id AS facility_type_id,
    facility_types.name AS facility_type_name,
    facility_types.nominaleop AS facility_type_nominaleop,
    facility_types.nominalmaxmonth AS facility_type_nominalmaxmonth,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    facilities.sdp AS facility_sdp,
    facilities.enabled AS facility_enabled,
    facility_approved_products.maxmonthsofstock AS facility_approved_product_maxmonthsofstock,
    facility_approved_products.minmonthsofstock AS facility_approved_product_minmonthsofstock,
    facility_approved_products.eop AS facility_approved_product_eop,
    requisitions.id AS requisition_id,
    requisitions.status AS requisition_status,
    requisitions.emergency AS requisition_emergency,
    product_categories.id AS product_category_id,
    product_categories.name AS product_category_name,
    products.productgroupid AS product_group_id,
    products.id AS product_id,
    products.code AS product_code,
    products.description AS product_description,
    products.dispensingunit AS product_dispensingunit,
    products.primaryname AS product_primaryname,
    products.fullname AS product_fullname,
    products.tracer AS product_tracer,
    requisition_line_items.amc,
    requisition_line_items.beginningbalance,
    requisition_line_items.calculatedorderquantity,
    requisition_line_items.createddate,
    requisition_line_items.fullsupply,
    requisition_line_items.id AS line_item_id,
    requisition_line_items.maxmonthsofstock,
    requisition_line_items.maxstockquantity,
    requisition_line_items.modifieddate,
    requisition_line_items.newpatientcount,
    requisition_line_items.normalizedconsumption,
    requisition_line_items.packsize,
    requisition_line_items.packstoship,
    requisition_line_items.previousstockinhand,
    requisition_line_items.quantityapproved,
    requisition_line_items.quantitydispensed,
    requisition_line_items.quantityreceived,
    requisition_line_items.quantityrequested,
    requisition_line_items.skipped,
    requisition_line_items.stockinhand,
    requisition_line_items.stockoutdays,
    requisition_line_items.totallossesandadjustments,
    shipment_line_items.quantityordered,
    shipment_line_items.quantityshipped,
    orders.modifieddate AS ordereddate,
    shipment_line_items.shippeddate
   FROM (((((((((((((requisition_line_items
     JOIN requisitions ON ((requisition_line_items.rnrid = requisitions.id)))
     JOIN products ON (((requisition_line_items.productcode)::text = (products.code)::text)))
     JOIN programs ON ((requisitions.programid = programs.id)))
     JOIN facilities ON ((requisitions.facilityid = facilities.id)))
     JOIN processing_periods ON ((requisitions.periodid = processing_periods.id)))
     LEFT JOIN orders ON ((requisitions.id = orders.id)))
     LEFT JOIN shipment_line_items ON ((shipment_line_items.orderid = orders.id)))
     JOIN processing_schedules ON ((processing_periods.scheduleid = processing_schedules.id)))
     JOIN program_products ON (((products.id = program_products.productid) AND (program_products.programid = programs.id))))
     JOIN product_categories ON ((program_products.productcategoryid = product_categories.id)))
     JOIN facility_types ON ((facilities.typeid = facility_types.id)))
     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
     LEFT JOIN facility_approved_products ON (((facility_approved_products.facilitytypeid = facility_types.id) AND (facility_approved_products.programproductid = program_products.id))));


ALTER TABLE vw_requisition_detail_dw OWNER TO postgres;

--
-- Name: vw_rnr_feedback; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_rnr_feedback AS
 SELECT vw_requisition_detail.program_id,
    vw_requisition_detail.program_name,
    vw_requisition_detail.product_id,
    vw_requisition_detail.product_code,
    vw_requisition_detail.product_primaryname,
    shipment_line_items.substitutedproductcode,
    shipment_line_items.substitutedproductname,
    vw_requisition_detail.product_description,
    vw_requisition_detail.indicator_product,
    vw_requisition_detail.processing_periods_id,
    vw_requisition_detail.processing_periods_name,
    vw_requisition_detail.processing_periods_start_date,
    vw_requisition_detail.processing_periods_end_date,
    vw_requisition_detail.processing_schedules_id,
    vw_requisition_detail.facility_type_id,
    vw_requisition_detail.facility_type_name,
    vw_requisition_detail.facility_code,
    vw_requisition_detail.facility_name,
    vw_requisition_detail.productcode,
    vw_requisition_detail.product,
    vw_requisition_detail.facility_id,
    vw_requisition_detail.req_id,
    vw_requisition_detail.req_status,
    vw_requisition_detail.req_line_id,
    vw_requisition_detail.zone_id,
    vw_requisition_detail.region,
    vw_requisition_detail.du_code,
    vw_requisition_detail.pf_code,
    COALESCE(vw_requisition_detail.beginningbalance, 0) AS beginningbalance,
    COALESCE(vw_requisition_detail.quantityreceived, 0) AS quantityreceived,
    COALESCE(vw_requisition_detail.quantitydispensed, 0) AS quantitydispensed,
    COALESCE(vw_requisition_detail.stockinhand, 0) AS stockinhand,
    COALESCE(vw_requisition_detail.quantityapproved, 0) AS quantityapproved,
    COALESCE(vw_requisition_detail.totallossesandadjustments, 0) AS totallossesandadjustments,
    COALESCE(vw_requisition_detail.newpatientcount, 0) AS newpatientcount,
    COALESCE(vw_requisition_detail.stockoutdays, 0) AS stockoutdays,
    COALESCE(vw_requisition_detail.normalizedconsumption, 0) AS normalizedconsumption,
    COALESCE(vw_requisition_detail.amc, 0) AS amc,
    COALESCE(vw_requisition_detail.maxmonthsofstock, 0) AS maxmonthsofstock,
    COALESCE(vw_requisition_detail.maxstockquantity, 0) AS maxstockquantity,
    COALESCE(vw_requisition_detail.packstoship, 0) AS packstoship,
    vw_requisition_detail.packsize,
    vw_requisition_detail.fullsupply,
    vw_requisition_detail.nominalmaxmonth,
    vw_requisition_detail.nominaleop,
    vw_requisition_detail.dispensingunit,
    COALESCE(vw_requisition_detail.calculatedorderquantity, 0) AS calculatedorderquantity,
    COALESCE(vw_requisition_detail.quantityrequested, 0) AS quantityrequested,
    COALESCE(shipment_line_items.quantityshipped, 0) AS quantityshipped,
    COALESCE(shipment_line_items.substitutedproductquantityshipped, 0) AS substitutedproductquantityshipped,
    (COALESCE(shipment_line_items.quantityshipped, 0) + COALESCE(shipment_line_items.substitutedproductquantityshipped, 0)) AS quantity_shipped_total,
        CASE
            WHEN (fn_previous_cb(vw_requisition_detail.req_id, vw_requisition_detail.product_code) <> COALESCE(vw_requisition_detail.beginningbalance, 0)) THEN 1
            ELSE 0
        END AS err_open_balance,
        CASE
            WHEN (COALESCE(vw_requisition_detail.calculatedorderquantity, 0) <> COALESCE(vw_requisition_detail.quantityrequested, 0)) THEN 1
            ELSE 0
        END AS err_qty_required,
        CASE
            WHEN (COALESCE(vw_requisition_detail.quantityreceived, 0) <> (COALESCE(shipment_line_items.quantityshipped, 0) + COALESCE(shipment_line_items.substitutedproductquantityshipped, 0))) THEN 1
            ELSE 0
        END AS err_qty_received,
        CASE
            WHEN (COALESCE(vw_requisition_detail.stockinhand, 0) <> (((COALESCE(vw_requisition_detail.beginningbalance, 0) + COALESCE(vw_requisition_detail.quantityreceived, 0)) - COALESCE(vw_requisition_detail.quantitydispensed, 0)) + COALESCE(vw_requisition_detail.totallossesandadjustments, 0))) THEN 1
            ELSE 0
        END AS err_qty_stockinhand
   FROM ((vw_requisition_detail
     LEFT JOIN orders ON ((orders.id = vw_requisition_detail.req_id)))
     LEFT JOIN shipment_line_items ON (((orders.id = shipment_line_items.orderid) AND ((vw_requisition_detail.product_code)::text = (shipment_line_items.productcode)::text))));


ALTER TABLE vw_rnr_feedback OWNER TO postgres;

--
-- Name: vw_rnr_status; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_rnr_status AS
 SELECT p.name AS programname,
    r.programid,
    r.periodid,
    f.id AS facilityid,
    r.id AS rnrid,
    r.status,
    gz.name AS geographiczonename
   FROM ((((facilities f
     JOIN requisitions r ON ((r.facilityid = f.id)))
     JOIN programs p ON ((p.id = r.programid)))
     JOIN requisition_status_changes ON ((r.id = requisition_status_changes.rnrid)))
     JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)));


ALTER TABLE vw_rnr_status OWNER TO postgres;

--
-- Name: vw_rnr_status_details; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_rnr_status_details AS
 SELECT p.name AS programname,
    r.programid,
    r.periodid,
    ps.name AS periodname,
    r.createddate,
    f.code AS facilitycode,
    f.name AS facilityname,
    f.id AS facilityid,
    r.id AS rnrid,
    r.status,
    ft.name AS facilitytypename,
    gz.id AS geographiczoneid,
    gz.name AS geographiczonename
   FROM ((((((facilities f
     JOIN requisitions r ON ((r.facilityid = f.id)))
     JOIN programs p ON ((p.id = r.programid)))
     JOIN processing_periods ps ON ((ps.id = r.periodid)))
     JOIN requisition_status_changes ON ((r.id = requisition_status_changes.rnrid)))
     JOIN facility_types ft ON ((ft.id = f.typeid)))
     JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)));


ALTER TABLE vw_rnr_status_details OWNER TO postgres;

--
-- Name: vw_stock_cards; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_stock_cards AS
 SELECT DISTINCT sc.id,
    sc.facilityid,
    sc.productid,
    sc.totalquantityonhand,
    sc.effectivedate,
    sc.notes,
    sc.createdby,
    sc.createddate,
    sc.modifiedby,
    sc.modifieddate,
    pp.id AS program_product_id,
    pp.programid,
    fap.maxmonthsofstock,
    fap.minmonthsofstock,
    fap.eop,
    ic.whoratio,
    ic.dosesperyear,
    ic.wastagefactor,
    ic.bufferpercentage,
    ic.minimumvalue,
    ic.maximumvalue,
    ic.adjustmentvalue
   FROM ((((stock_cards sc
     LEFT JOIN program_products pp ON ((sc.productid = pp.productid)))
     LEFT JOIN facilities ON ((sc.facilityid = facilities.id)))
     LEFT JOIN isa_coefficients ic ON ((ic.id = pp.isacoefficientsid)))
     LEFT JOIN facility_approved_products fap ON (((facilities.typeid = fap.facilitytypeid) AND (fap.programproductid = pp.id))));


ALTER TABLE vw_stock_cards OWNER TO postgres;

--
-- Name: vw_stock_status; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_stock_status AS
 SELECT fn_get_supplying_facility_name(requisitions.supervisorynodeid) AS supplyingfacility,
    facilities.code AS facilitycode,
    products.code AS productcode,
    facilities.name AS facility,
    requisitions.status AS req_status,
    requisition_line_items.product,
    requisition_line_items.stockinhand,
    ((((requisition_line_items.stockinhand + requisition_line_items.beginningbalance) + requisition_line_items.quantitydispensed) + requisition_line_items.quantityreceived) + abs(requisition_line_items.totallossesandadjustments)) AS reported_figures,
    requisitions.id AS rnrid,
    requisition_line_items.amc,
        CASE
            WHEN (COALESCE(requisition_line_items.amc, 0) = 0) THEN (0)::numeric
            ELSE ((requisition_line_items.stockinhand)::numeric / (requisition_line_items.amc)::numeric)
        END AS mos,
    COALESCE(
        CASE
            WHEN (((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) - requisition_line_items.stockinhand) < 0) THEN 0
            ELSE ((COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth) - requisition_line_items.stockinhand)
        END, 0) AS required,
        CASE
            WHEN (requisition_line_items.stockinhand = 0) THEN 'SO'::text
            ELSE
            CASE
                WHEN ((requisition_line_items.stockinhand > 0) AND ((requisition_line_items.stockinhand)::numeric <= ((COALESCE(requisition_line_items.amc, 0))::numeric * facility_types.nominaleop))) THEN 'US'::text
                ELSE
                CASE
                    WHEN (requisition_line_items.stockinhand > (COALESCE(requisition_line_items.amc, 0) * facility_types.nominalmaxmonth)) THEN 'OS'::text
                    ELSE 'SP'::text
                END
            END
        END AS status,
    facility_types.name AS facilitytypename,
    geographic_zones.id AS gz_id,
    geographic_zones.name AS location,
    products.id AS productid,
    processing_periods.startdate,
    programs.id AS programid,
    processing_schedules.id AS psid,
    processing_periods.enddate,
    processing_periods.id AS periodid,
    facility_types.id AS facilitytypeid,
    program_products.productcategoryid AS categoryid,
    products.tracer AS indicator_product,
    facilities.id AS facility_id,
    processing_periods.name AS processing_period_name,
    requisition_line_items.stockoutdays,
    requisitions.supervisorynodeid
   FROM ((((((((((requisition_line_items
     JOIN requisitions ON ((requisitions.id = requisition_line_items.rnrid)))
     JOIN facilities ON ((facilities.id = requisitions.facilityid)))
     JOIN facility_types ON ((facility_types.id = facilities.typeid)))
     JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
     JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
     JOIN products ON (((products.code)::text = (requisition_line_items.productcode)::text)))
     JOIN program_products ON (((requisitions.programid = program_products.programid) AND (products.id = program_products.productid))))
     JOIN product_categories ON ((product_categories.id = program_products.productcategoryid)))
     JOIN programs ON ((programs.id = requisitions.programid)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
  WHERE ((requisition_line_items.stockinhand IS NOT NULL) AND (requisition_line_items.skipped = false));


ALTER TABLE vw_stock_status OWNER TO postgres;

--
-- Name: vw_supply_status; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_supply_status AS
 SELECT requisition_line_items.id AS li_id,
    requisition_line_items.rnrid AS li_rnrid,
    requisition_line_items.productcode AS li_productcode,
    requisition_line_items.product AS li_product,
    requisition_line_items.productdisplayorder AS li_productdisplayorder,
    requisition_line_items.productcategory AS li_productcategory,
    requisition_line_items.productcategorydisplayorder AS li_productcategorydisplayorder,
    requisition_line_items.dispensingunit AS li_dispensingunit,
    requisition_line_items.beginningbalance AS li_beginningbalance,
    requisition_line_items.quantityreceived AS li_quantityreceived,
    requisition_line_items.quantitydispensed AS li_quantitydispensed,
    requisition_line_items.stockinhand AS li_stockinhand,
    requisition_line_items.quantityrequested AS li_quantityrequested,
    requisition_line_items.reasonforrequestedquantity AS li_reasonforrequestedquantity,
    requisition_line_items.calculatedorderquantity AS li_calculatedorderquantity,
    requisition_line_items.quantityapproved AS li_quantityapproved,
    requisition_line_items.totallossesandadjustments AS li_totallossesandadjustments,
    requisition_line_items.newpatientcount AS li_newpatientcount,
    requisition_line_items.stockoutdays AS li_stockoutdays,
    requisition_line_items.normalizedconsumption AS li_normalizedconsumption,
    requisition_line_items.amc AS li_amc,
    requisition_line_items.maxmonthsofstock AS li_maxmonthsofstock,
    requisition_line_items.maxstockquantity AS li_maxstockquantity,
    requisition_line_items.packstoship AS li_packstoship,
    requisition_line_items.price AS li_price,
    requisition_line_items.expirationdate AS li_expirationdate,
    requisition_line_items.remarks AS li_remarks,
    requisition_line_items.dosespermonth AS li_dosespermonth,
    requisition_line_items.dosesperdispensingunit AS li_dosesperdispensingunit,
    requisition_line_items.packsize AS li_packsize,
    requisition_line_items.roundtozero AS li_roundtozero,
    requisition_line_items.packroundingthreshold AS li_packroundingthreshold,
    requisition_line_items.fullsupply AS li_fullsupply,
    requisition_line_items.createdby AS li_createdby,
    requisition_line_items.createddate AS li_createddate,
    requisition_line_items.modifiedby AS li_modifiedby,
    requisition_line_items.modifieddate AS li_modifieddate,
    programs.id AS pg_id,
    programs.code AS pg_code,
    programs.name AS pg_name,
    products.id AS p_id,
    products.code AS p_code,
    products.primaryname AS p_primaryname,
    program_products.displayorder AS p_displayorder,
    products.tracer AS indicator_product,
    products.description AS p_description,
    facility_types.name AS facility_type_name,
    facility_types.id AS ft_id,
    facility_types.code AS ft_code,
    facility_types.nominalmaxmonth AS ft_nominalmaxmonth,
    facility_types.nominaleop AS ft_nominaleop,
    facilities.id AS f_id,
    facilities.code AS f_code,
    facilities.name AS facility,
    fn_get_supplying_facility_name(requisitions.supervisorynodeid) AS supplyingfacility,
    facilities.geographiczoneid AS f_zoneid,
    facility_approved_products.maxmonthsofstock AS fp_maxmonthsofstock,
    facility_approved_products.minmonthsofstock AS fp_minmonthsofstock,
    facility_approved_products.eop AS fp_eop,
    requisitions.status AS r_status,
    requisitions.supervisorynodeid,
    processing_schedules.id AS ps_id,
    processing_periods.id AS pp_id,
    geographic_zones.id AS geographiczoneid,
    geographic_zones.name AS geographiczonename
   FROM (((((((((((requisition_line_items
     JOIN requisitions ON ((requisitions.id = requisition_line_items.rnrid)))
     JOIN facilities ON ((facilities.id = requisitions.facilityid)))
     JOIN facility_types ON ((facility_types.id = facilities.typeid)))
     JOIN processing_periods ON ((processing_periods.id = requisitions.periodid)))
     JOIN processing_schedules ON ((processing_schedules.id = processing_periods.scheduleid)))
     JOIN products ON (((products.code)::text = (requisition_line_items.productcode)::text)))
     JOIN program_products ON (((requisitions.programid = program_products.programid) AND (products.id = program_products.productid))))
     JOIN product_categories ON ((product_categories.id = program_products.productcategoryid)))
     JOIN programs ON ((programs.id = requisitions.programid)))
     JOIN geographic_zones ON ((geographic_zones.id = facilities.geographiczoneid)))
     JOIN facility_approved_products ON (((facility_types.id = facility_approved_products.facilitytypeid) AND (facility_approved_products.programproductid = program_products.id))));


ALTER TABLE vw_supply_status OWNER TO postgres;

--
-- Name: vw_timeliness_report; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_timeliness_report AS
 SELECT requisitions.programid,
    requisitions.periodid,
    rgps.scheduleid,
    facilities.geographiczoneid,
    facilities.name AS facilityname,
    facilities.code AS facilitycode,
    requisitions.createddate,
    pp.enddate,
    requisitions.status,
    requisitions.id AS rnrid,
    facilities.id AS facilityid,
    facility_types.id AS typeid,
    facility_types.name AS facilitytypename,
        CASE
            WHEN (COALESCE(date_part('day'::text, (requisitions.createddate - ((pp.enddate)::date)::timestamp without time zone)), (0)::double precision) <= (COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE ((configuration_settings.key)::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text)))::integer, 0))::double precision) THEN 'R'::text
            WHEN (COALESCE(date_part('day'::text, (requisitions.createddate - ((pp.enddate)::date)::timestamp without time zone)), (0)::double precision) > (COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE ((configuration_settings.key)::text = 'UNSCHEDULED_REPORTING_CUT_OFF_DATE'::text)))::integer, 0))::double precision) THEN 'U'::text
            WHEN (COALESCE(date_part('day'::text, (requisitions.createddate - ((pp.enddate)::date)::timestamp without time zone)), (0)::double precision) > (COALESCE((( SELECT configuration_settings.value
               FROM configuration_settings
              WHERE ((configuration_settings.key)::text = 'MSD_ZONE_REPORTING_CUT_OFF_DATE'::text)))::integer, 0))::double precision) THEN 'L'::text
            ELSE 'N'::text
        END AS reportingstatus
   FROM (((((((requisitions
     JOIN facilities ON ((requisitions.facilityid = facilities.id)))
     JOIN requisition_group_members rgm ON ((rgm.facilityid = requisitions.facilityid)))
     JOIN facility_types ON ((facilities.typeid = facility_types.id)))
     JOIN programs_supported ps ON (((ps.programid = requisitions.programid) AND (requisitions.facilityid = ps.facilityid))))
     JOIN processing_periods pp ON ((pp.id = requisitions.periodid)))
     JOIN requisition_group_program_schedules rgps ON ((((rgps.requisitiongroupid = rgm.requisitiongroupid) AND (rgps.programid = requisitions.programid)) AND (pp.scheduleid = rgps.scheduleid))))
     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
  WHERE ((((requisitions.status)::text = ANY (ARRAY[('IN_APPROVAL'::character varying)::text, ('APPROVED'::character varying)::text, ('RELEASED'::character varying)::text])) AND (facilities.active = true)) AND (requisitions.emergency = false))
  GROUP BY requisitions.status, requisitions.createddate, pp.enddate, requisitions.id, requisitions.programid, requisitions.periodid, rgps.scheduleid, facilities.geographiczoneid, facilities.name, facilities.code, facilities.id, facility_types.id, facility_types.name;


ALTER TABLE vw_timeliness_report OWNER TO postgres;

--
-- Name: vw_user_facilities; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_user_facilities AS
 SELECT DISTINCT f.id AS facility_id,
    f.geographiczoneid AS district_id,
    rg.id AS requisition_group_id,
    ra.userid AS user_id,
    ra.programid AS program_id
   FROM ((((facilities f
     JOIN requisition_group_members m ON ((m.facilityid = f.id)))
     JOIN requisition_groups rg ON ((rg.id = m.requisitiongroupid)))
     JOIN supervisory_nodes sn ON ((sn.id = rg.supervisorynodeid)))
     JOIN role_assignments ra ON (((ra.supervisorynodeid = sn.id) OR (ra.supervisorynodeid = sn.parentid))));


ALTER TABLE vw_user_facilities OWNER TO postgres;

--
-- Name: vw_user_districts; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_user_districts AS
 SELECT DISTINCT vw_user_facilities.user_id,
    vw_user_facilities.district_id,
    vw_user_facilities.program_id
   FROM vw_user_facilities;


ALTER TABLE vw_user_districts OWNER TO postgres;

--
-- Name: vw_user_geographic_zones; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_user_geographic_zones AS
 SELECT DISTINCT ra.userid,
    ra.supervisorynodeid,
    gz.id AS geographiczoneid,
    gz.levelid,
    ra.programid
   FROM ((((((facilities f
     JOIN geographic_zones gz ON ((gz.id = f.geographiczoneid)))
     JOIN requisition_group_members m ON ((m.facilityid = f.id)))
     JOIN requisition_groups rg ON ((rg.id = m.requisitiongroupid)))
     JOIN supervisory_nodes sn ON ((sn.id = rg.supervisorynodeid)))
     JOIN role_assignments ra ON (((ra.supervisorynodeid = sn.id) OR (ra.supervisorynodeid = sn.parentid))))
     JOIN geographic_zones d ON ((d.id = f.geographiczoneid)))
  WHERE ((ra.supervisorynodeid IS NOT NULL) AND (rg.supervisorynodeid IS NOT NULL));


ALTER TABLE vw_user_geographic_zones OWNER TO postgres;

--
-- Name: vw_user_role_assignments; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_user_role_assignments AS
 SELECT users.firstname,
    users.lastname,
    users.email,
    users.cellphone,
    users.officephone,
    supervisory_nodes.name AS supervisorynodename,
    programs.name AS programname,
    roles.name AS rolename,
    programs.id AS programid,
    supervisory_nodes.id AS supervisorynodeid,
    roles.id AS roleid
   FROM ((((roles
     JOIN role_assignments ON ((roles.id = role_assignments.roleid)))
     JOIN programs ON ((programs.id = role_assignments.programid)))
     JOIN supervisory_nodes ON ((supervisory_nodes.id = role_assignments.supervisorynodeid)))
     JOIN users ON ((users.id = role_assignments.userid)));


ALTER TABLE vw_user_role_assignments OWNER TO postgres;

--
-- Name: vw_user_supervisorynodes; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_user_supervisorynodes AS
 WITH RECURSIVE supervisorynodesrec AS (
         SELECT DISTINCT ra.userid,
            ra.programid,
            s.id,
            s.parentid,
            s.facilityid,
            s.name,
            s.code,
            s.description,
            s.createdby,
            s.createddate,
            s.modifiedby,
            s.modifieddate
           FROM (supervisory_nodes s
             JOIN role_assignments ra ON ((s.id = ra.supervisorynodeid)))
        UNION
         SELECT supervisorynodesrec_1.userid,
            supervisorynodesrec_1.programid,
            sn.id,
            sn.parentid,
            sn.facilityid,
            sn.name,
            sn.code,
            sn.description,
            sn.createdby,
            sn.createddate,
            sn.modifiedby,
            sn.modifieddate
           FROM (supervisory_nodes sn
             JOIN supervisorynodesrec supervisorynodesrec_1 ON ((sn.parentid = supervisorynodesrec_1.id)))
        )
 SELECT supervisorynodesrec.userid,
    supervisorynodesrec.programid,
    supervisorynodesrec.id,
    supervisorynodesrec.parentid,
    supervisorynodesrec.facilityid,
    supervisorynodesrec.name,
    supervisorynodesrec.code,
    supervisorynodesrec.description,
    supervisorynodesrec.createdby,
    supervisorynodesrec.createddate,
    supervisorynodesrec.modifiedby,
    supervisorynodesrec.modifieddate
   FROM supervisorynodesrec;


ALTER TABLE vw_user_supervisorynodes OWNER TO postgres;

--
-- Name: vw_vaccine_campaign; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_campaign AS
 SELECT gz.id AS geographic_zone_id,
    gz.name AS geographic_zone_name,
    gz.levelid AS level_id,
    gz.parentid AS parent_id,
    f.code AS facility_code,
    f.name AS facility_name,
    pp.id AS period_id,
    pp.name AS period_name,
    vr.id AS report_id,
    vr.programid AS program_id,
    vr.facilityid AS facility_id,
    vr.status,
    camp.name AS camp_name,
    camp.venue AS camp_venue,
    camp.startdate AS camp_start_date,
    camp.enddate AS camp_end_date,
    camp.vaccinated AS camp_vaccinated,
    camp.childrenvaccinated AS camp_childrenvaccinated,
    camp.pregnantwomanvaccinated AS camp_pregnantwomanvaccinated,
    camp.remarks AS camp_remarks,
    camp.otherobjectives AS camp_other
   FROM ((((vaccine_report_campaign_line_items camp
     JOIN vaccine_reports vr ON ((camp.reportid = vr.id)))
     JOIN processing_periods pp ON ((vr.periodid = pp.id)))
     JOIN facilities f ON ((vr.facilityid = f.id)))
     JOIN geographic_zones gz ON ((f.geographiczoneid = gz.id)));


ALTER TABLE vw_vaccine_campaign OWNER TO postgres;

--
-- Name: vw_vaccine_cold_chain; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_cold_chain AS
 SELECT pp.id AS period_id,
    pp.name AS period_name,
    pp.startdate AS period_start_date,
    pp.enddate AS period_end_date,
    gz.id AS geographic_zone_id,
    gz.name AS geographic_zone_name,
    gz.levelid AS level_id,
    gz.parentid AS parent_id,
    f.id AS facility_id,
    f.code AS facility_code,
    f.name AS facility_name,
    vr.id AS report_id,
    vr.programid,
    vr.createddate AS reported_date,
    e.name AS equipment_name,
    e.model,
    ei.yearofinstallation,
    et.name AS equipment_type_name,
    ccli.mintemp,
    ccli.maxtemp,
    ccli.minepisodetemp,
    ccli.maxepisodetemp,
    eet.name AS energy_source,
    es.name AS status
   FROM ((((((((((vaccine_report_cold_chain_line_items ccli
     JOIN vaccine_reports vr ON ((vr.id = ccli.reportid)))
     JOIN facilities f ON ((vr.facilityid = f.id)))
     JOIN geographic_zones gz ON ((f.geographiczoneid = gz.id)))
     JOIN processing_periods pp ON ((vr.periodid = pp.id)))
     JOIN equipment_inventories ei ON (((ei.facilityid = f.id) AND (ccli.equipmentinventoryid = ei.id))))
     JOIN equipments e ON ((ei.equipmentid = e.id)))
     JOIN equipment_types et ON ((e.equipmenttypeid = et.id)))
     JOIN equipment_energy_types eet ON ((e.energytypeid = eet.id)))
     LEFT JOIN equipment_inventory_statuses eis ON ((eis.inventoryid = ei.id)))
     JOIN equipment_operational_status es ON ((es.id = eis.statusid)));


ALTER TABLE vw_vaccine_cold_chain OWNER TO postgres;

--
-- Name: vw_vaccine_coverage; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_coverage AS
 SELECT a.program_id,
    a.geographic_zone_id,
    a.geographic_zone_name,
    a.level_id,
    a.parent_id,
    a.period_id,
    a.period_name,
    a.period_year,
    a.period_start_date,
    a.period_end_date,
    a.facility_id,
    a.facility_code,
    a.facility_name,
    a.report_id,
    a.fixed_immunization_session,
    a.outreach_immunization_session,
    a.product_id,
    a.product_code,
    a.product_name,
    a.dose_id,
    a.display_order,
    a.display_name,
    a.within_male,
    a.within_female,
    a.within_total,
    0 AS within_coverage,
    a.outside_male,
    a.outside_female,
    a.outside_total,
    0 AS outside_coverage,
    a.camp_male,
    a.camp_female,
    a.camp_total,
    a.within_outside_total,
    fn_get_vaccine_coverage_denominator(a.program_id, a.facility_id, a.period_year, a.product_id, a.dose_id) AS denominator,
    a.cum_within_total,
    a.cum_outside_total,
    (a.cum_within_total + a.cum_outside_total) AS cum_within_outside_total,
    0 AS within_outside_coverage,
    0 AS cum_within_coverage,
    0 AS cum_outside_coverage,
    0 AS cum_within_outside_coverage,
    a.bcg_1,
    a.mr_1,
    a.dtp_1,
    a.dtp_3
   FROM ( WITH temp AS (
                 SELECT geographic_zones.id AS geographic_zone_id,
                    geographic_zones.name AS geographic_zone_name,
                    geographic_zones.levelid AS level_id,
                    geographic_zones.parentid AS parent_id,
                    processing_periods.id AS period_id,
                    processing_periods.name AS period_name,
                    processing_periods.startdate AS period_start_date,
                    processing_periods.enddate AS period_end_date,
                    facilities.id AS facility_id,
                    facilities.code AS facility_code,
                    facilities.name AS facility_name,
                    vaccine_reports.id AS report_id,
                    vaccine_reports.programid AS program_id,
                    vaccine_reports.fixedimmunizationsessions AS fixed_immunization_session,
                    vaccine_reports.outreachimmunizationsessions AS outreach_immunization_session,
                    products.id AS product_id,
                    products.code AS product_code,
                    products.primaryname AS product_name,
                    vaccine_report_coverage_line_items.doseid AS dose_id,
                    vaccine_report_coverage_line_items.displayorder AS display_order,
                    vaccine_report_coverage_line_items.displayname AS display_name,
                    vaccine_report_coverage_line_items.regularmale AS within_male,
                    vaccine_report_coverage_line_items.regularfemale AS within_female,
                    vaccine_report_coverage_line_items.outreachmale AS outside_male,
                    vaccine_report_coverage_line_items.outreachfemale AS outside_female,
                    vaccine_report_coverage_line_items.campaignmale AS camp_male,
                    vaccine_report_coverage_line_items.campaignfemale AS camp_female,
                    ( SELECT sum((COALESCE(l.regularmale, 0) + COALESCE(l.regularfemale, 0))) AS sum
                           FROM ((vaccine_report_coverage_line_items l
                             JOIN vaccine_reports r ON ((r.id = l.reportid)))
                             JOIN processing_periods pp ON ((pp.id = r.periodid)))
                          WHERE (((((date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate)) AND (pp.startdate <= processing_periods.startdate)) AND (r.facilityid = vaccine_reports.facilityid)) AND (l.productid = vaccine_report_coverage_line_items.productid)) AND (l.doseid = vaccine_report_coverage_line_items.doseid))) AS cum_within_total,
                    ( SELECT sum((COALESCE(l.outreachmale, 0) + COALESCE(l.outreachfemale, 0))) AS sum
                           FROM ((vaccine_report_coverage_line_items l
                             JOIN vaccine_reports r ON ((r.id = l.reportid)))
                             JOIN processing_periods pp ON ((pp.id = r.periodid)))
                          WHERE (((((date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate)) AND (pp.startdate <= processing_periods.startdate)) AND (r.facilityid = vaccine_reports.facilityid)) AND (l.productid = vaccine_report_coverage_line_items.productid)) AND (l.doseid = vaccine_report_coverage_line_items.doseid))) AS cum_outside_total,
                        CASE
                            WHEN (((products.code)::text = (( SELECT configuration_settings.value
                               FROM configuration_settings
                              WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_BCG'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                            ELSE 0
                        END AS bcg_1,
                        CASE
                            WHEN (((products.code)::text = (( SELECT configuration_settings.value
                               FROM configuration_settings
                              WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_MR'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                            ELSE 0
                        END AS mr_1,
                        CASE
                            WHEN (((products.code)::text = (( SELECT configuration_settings.value
                               FROM configuration_settings
                              WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 1)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                            ELSE 0
                        END AS dtp_1,
                        CASE
                            WHEN (((products.code)::text = (( SELECT configuration_settings.value
                               FROM configuration_settings
                              WHERE ((configuration_settings.key)::text = 'VACCINE_DROPOUT_DTP'::text)))::text) AND (vaccine_report_coverage_line_items.doseid = 3)) THEN (COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0))
                            ELSE 0
                        END AS dtp_3
                   FROM (((((vaccine_report_coverage_line_items
                     JOIN vaccine_reports ON ((vaccine_report_coverage_line_items.reportid = vaccine_reports.id)))
                     JOIN processing_periods ON ((vaccine_reports.periodid = processing_periods.id)))
                     JOIN facilities ON ((vaccine_reports.facilityid = facilities.id)))
                     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
                     JOIN products ON ((vaccine_report_coverage_line_items.productid = products.id)))
                )
         SELECT b.program_id,
            b.geographic_zone_id,
            b.geographic_zone_name,
            b.level_id,
            b.parent_id,
            b.period_id,
            b.period_name,
            (date_part('year'::text, b.period_start_date))::integer AS period_year,
            b.period_start_date,
            b.period_end_date,
            b.facility_id,
            b.facility_code,
            b.facility_name,
            b.report_id,
            b.fixed_immunization_session,
            b.outreach_immunization_session,
            b.product_id,
            b.product_code,
            b.product_name,
            b.dose_id,
            b.display_order,
            b.display_name,
            COALESCE(b.within_male, 0) AS within_male,
            COALESCE(b.within_female, 0) AS within_female,
            (COALESCE(b.within_male, 0) + COALESCE(b.within_female, 0)) AS within_total,
            COALESCE(b.outside_male, 0) AS outside_male,
            COALESCE(b.outside_female, 0) AS outside_female,
            (COALESCE(b.outside_male, 0) + COALESCE(b.outside_female, 0)) AS outside_total,
            COALESCE(b.camp_male, 0) AS camp_male,
            COALESCE(b.camp_female, 0) AS camp_female,
            (COALESCE(b.camp_male, 0) + COALESCE(b.camp_female, 0)) AS camp_total,
            (((COALESCE(b.within_male, 0) + COALESCE(b.within_female, 0)) + COALESCE(b.outside_male, 0)) + COALESCE(b.outside_female, 0)) AS within_outside_total,
            b.cum_within_total,
            b.cum_outside_total,
            b.bcg_1,
            b.mr_1,
            b.dtp_1,
            b.dtp_3
           FROM temp b) a
  ORDER BY a.display_order;


ALTER TABLE vw_vaccine_coverage OWNER TO postgres;

--
-- Name: vw_vaccine_disease_surveillance; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_disease_surveillance AS
 WITH tmp AS (
         SELECT geographic_zones.id AS geographic_zone_id,
            geographic_zones.name AS geographic_zone_name,
            geographic_zones.levelid AS level_id,
            geographic_zones.parentid AS parent_id,
            facilities.id AS facility_id,
            facilities.code AS facility_code,
            facilities.name AS facility_name,
            vaccine_reports.periodid AS period_id,
            processing_periods.name AS period_name,
            (processing_periods.startdate)::date AS period_start_date,
            (processing_periods.enddate)::date AS period_end_date,
            date_part('year'::text, processing_periods.startdate) AS report_year,
            vaccine_reports.id AS report_id,
            vaccine_reports.status,
            vaccine_reports.programid AS program_id,
            vaccine_report_disease_line_items.diseaseid AS disease_id,
            vaccine_report_disease_line_items.diseasename AS disease_name,
            vaccine_report_disease_line_items.displayorder AS display_order,
            vaccine_report_disease_line_items.cases,
            vaccine_report_disease_line_items.death,
            ( SELECT sum(COALESCE(l.cases, 0)) AS sum
                   FROM ((vaccine_report_disease_line_items l
                     JOIN vaccine_reports r ON ((r.id = l.reportid)))
                     JOIN processing_periods pp ON ((pp.id = r.periodid)))
                  WHERE ((((date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate)) AND (pp.startdate <= processing_periods.startdate)) AND (r.facilityid = vaccine_reports.facilityid)) AND (l.diseaseid = vaccine_report_disease_line_items.diseaseid))) AS cum_cases,
            ( SELECT sum(COALESCE(l.death, 0)) AS sum
                   FROM ((vaccine_report_disease_line_items l
                     JOIN vaccine_reports r ON ((r.id = l.reportid)))
                     JOIN processing_periods pp ON ((pp.id = r.periodid)))
                  WHERE ((((date_part('year'::text, pp.startdate) = date_part('year'::text, processing_periods.startdate)) AND (pp.startdate <= processing_periods.startdate)) AND (r.facilityid = vaccine_reports.facilityid)) AND (l.diseaseid = vaccine_report_disease_line_items.diseaseid))) AS cum_deaths
           FROM ((((vaccine_report_disease_line_items
             JOIN vaccine_reports ON ((vaccine_report_disease_line_items.reportid = vaccine_reports.id)))
             JOIN processing_periods ON ((vaccine_reports.periodid = processing_periods.id)))
             JOIN facilities ON ((vaccine_reports.facilityid = facilities.id)))
             JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
        )
 SELECT t.program_id,
    t.geographic_zone_id,
    t.geographic_zone_name,
    t.level_id,
    t.parent_id,
    t.facility_id,
    t.facility_code,
    t.facility_name,
    t.report_id,
    t.period_id,
    t.report_year,
    t.period_start_date,
    t.cases,
    t.death,
    t.disease_name,
    t.display_order,
    t.cum_cases,
    t.cum_deaths
   FROM tmp t
  ORDER BY t.display_order, t.facility_id, t.period_start_date;


ALTER TABLE vw_vaccine_disease_surveillance OWNER TO postgres;

--
-- Name: vw_vaccine_district_target_population; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_district_target_population AS
 SELECT e.year,
    e.districtid AS geographic_zone_id,
    c.id AS category_id,
    c.name AS category_name,
    e.value AS target_value_annual,
    round(((e.value / 12))::double precision) AS target_value_monthly
   FROM (demographic_estimate_categories c
     JOIN district_demographic_estimates e ON ((c.id = e.demographicestimateid)));


ALTER TABLE vw_vaccine_district_target_population OWNER TO postgres;

--
-- Name: vw_vaccine_estimates; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_estimates AS
 SELECT facility_demographic_estimates.year,
    facilities.id AS facility_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.catchmentpopulation AS population,
    demographic_estimate_categories.name AS category_name,
    facility_demographic_estimates.demographicestimateid AS demographic_estimate_id,
    facility_demographic_estimates.conversionfactor AS converstion_factory,
    facility_demographic_estimates.value
   FROM (((demographic_estimate_categories
     JOIN facility_demographic_estimates ON ((facility_demographic_estimates.demographicestimateid = demographic_estimate_categories.id)))
     JOIN facilities ON ((facility_demographic_estimates.facilityid = facilities.id)))
     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)));


ALTER TABLE vw_vaccine_estimates OWNER TO postgres;

--
-- Name: vw_vaccine_iefi; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_iefi AS
 SELECT geographic_zones.id AS geographic_zone_id,
    geographic_zones.name AS geographic_zone_name,
    geographic_zones.levelid AS level_id,
    geographic_zones.parentid AS parent_id,
    facilities.code AS facility_code,
    facilities.name AS facility_name,
    processing_periods.id AS period_id,
    processing_periods.name AS period_name,
    vaccine_reports.id AS report_id,
    vaccine_reports.programid AS program_id,
    vaccine_reports.facilityid AS facility_id,
    vaccine_reports.status,
    products.id AS product_id,
    products.code AS product_code,
    products.primaryname AS product_name,
    vaccine_report_adverse_effect_line_items.date AS aefi_date,
    vaccine_report_adverse_effect_line_items.batch AS aefi_batch,
    vaccine_report_adverse_effect_line_items.expiry AS aefi_expiry_date,
    vaccine_report_adverse_effect_line_items.cases AS aefi_case,
    vaccine_report_adverse_effect_line_items.notes AS aefi_notes,
    vaccine_report_adverse_effect_line_items.isinvestigated AS is_investigated,
    vaccine_report_adverse_effect_line_items.manufacturer
   FROM (((((vaccine_report_adverse_effect_line_items
     JOIN vaccine_reports ON ((vaccine_report_adverse_effect_line_items.reportid = vaccine_reports.id)))
     JOIN processing_periods ON ((vaccine_reports.periodid = processing_periods.id)))
     JOIN products ON ((vaccine_report_adverse_effect_line_items.productid = products.id)))
     JOIN facilities ON ((vaccine_reports.facilityid = facilities.id)))
     JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)));


ALTER TABLE vw_vaccine_iefi OWNER TO postgres;

--
-- Name: vw_vaccine_stock_status; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_stock_status AS
 WITH t AS (
         SELECT geographic_zones.id AS geographic_zone_id,
            geographic_zones.name AS geographic_zone_name,
            geographic_zones.levelid AS level_id,
            geographic_zones.parentid AS parent_id,
            facilities.id AS facility_id,
            facilities.code AS facility_code,
            facilities.name AS facility_name,
            processing_periods.name AS period_name,
            processing_periods.startdate AS period_start_date,
            processing_periods.enddate AS period_end_date,
            processing_periods.id AS period_id,
            vaccine_reports.id AS report_id,
            vaccine_reports.programid AS program_id,
            product_categories.code AS product_category_code,
            vaccine_report_logistics_line_items.productid AS product_id,
            vaccine_report_logistics_line_items.productcode AS product_code,
            vaccine_report_logistics_line_items.productname AS product_name,
            vaccine_report_logistics_line_items.displayorder AS display_order,
            vaccine_report_logistics_line_items.openingbalance AS opening_balanace,
            vaccine_report_logistics_line_items.quantityreceived AS quantity_received,
            vaccine_report_logistics_line_items.quantityissued AS quantity_issued,
            vaccine_report_logistics_line_items.quantityvvmalerted AS quantity_vvm_alerted,
            vaccine_report_logistics_line_items.quantityfreezed AS quantity_freezed,
            vaccine_report_logistics_line_items.quantityexpired AS quantity_expired,
            vaccine_report_logistics_line_items.quantitydiscardedunopened AS quantity_discarded_unopened,
            vaccine_report_logistics_line_items.quantitydiscardedopened AS quantity_discarded_opened,
            vaccine_report_logistics_line_items.quantitywastedother AS quantity_wasted_other,
            vaccine_report_logistics_line_items.daysstockedout AS days_stocked_out,
            vaccine_report_logistics_line_items.closingbalance AS closing_balance,
            vaccine_discarding_reasons.name AS reason_for_discarding,
            (COALESCE(vaccine_report_logistics_line_items.quantityissued, 0) + COALESCE(vaccine_report_logistics_line_items.quantitydiscardedunopened, 0)) AS usage_denominator,
            cv.vaccinated
           FROM ((((((((vaccine_report_logistics_line_items
             JOIN vaccine_reports ON ((vaccine_report_logistics_line_items.reportid = vaccine_reports.id)))
             JOIN processing_periods ON ((vaccine_reports.periodid = processing_periods.id)))
             LEFT JOIN vaccine_discarding_reasons ON ((vaccine_report_logistics_line_items.discardingreasonid = vaccine_discarding_reasons.id)))
             JOIN facilities ON ((vaccine_reports.facilityid = facilities.id)))
             JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
             JOIN program_products ON (((program_products.programid = vaccine_reports.programid) AND (program_products.productid = vaccine_report_logistics_line_items.productid))))
             JOIN product_categories ON ((program_products.productcategoryid = product_categories.id)))
             LEFT JOIN ( SELECT vaccine_report_coverage_line_items.reportid,
                    vaccine_report_coverage_line_items.productid,
                    sum((((((COALESCE(vaccine_report_coverage_line_items.regularmale, 0) + COALESCE(vaccine_report_coverage_line_items.regularfemale, 0)) + COALESCE(vaccine_report_coverage_line_items.outreachmale, 0)) + COALESCE(vaccine_report_coverage_line_items.outreachfemale, 0)) + COALESCE(vaccine_report_coverage_line_items.campaignmale, 0)) + COALESCE(vaccine_report_coverage_line_items.campaignfemale, 0))) AS vaccinated
                   FROM vaccine_report_coverage_line_items
                  GROUP BY vaccine_report_coverage_line_items.reportid, vaccine_report_coverage_line_items.productid) cv ON (((cv.reportid = vaccine_reports.id) AND (vaccine_report_logistics_line_items.productid = cv.productid))))
        )
 SELECT t.geographic_zone_id,
    t.geographic_zone_name,
    t.level_id,
    t.parent_id,
    t.facility_id,
    t.facility_code,
    t.facility_name,
    t.period_name,
    t.period_start_date,
    t.period_end_date,
    t.period_id,
    t.report_id,
    t.program_id,
    t.product_category_code,
    t.product_id,
    t.product_code,
    t.product_name,
    t.display_order,
    t.opening_balanace,
    t.quantity_received,
    t.quantity_issued,
    t.quantity_vvm_alerted,
    t.quantity_freezed,
    t.quantity_expired,
    t.quantity_discarded_unopened,
    t.quantity_discarded_opened,
    t.quantity_wasted_other,
    t.days_stocked_out,
    t.closing_balance,
    t.reason_for_discarding,
        CASE
            WHEN ((t.reason_for_discarding)::text = 'Expired'::text) THEN t.quantity_discarded_unopened
            ELSE 0
        END AS expired,
        CASE
            WHEN ((t.reason_for_discarding)::text = 'Broken'::text) THEN t.quantity_discarded_unopened
            ELSE 0
        END AS broken,
        CASE
            WHEN ((t.reason_for_discarding)::text = 'Cold Chain Failure'::text) THEN t.quantity_discarded_unopened
            ELSE 0
        END AS cold_chain_failure,
        CASE
            WHEN ((t.reason_for_discarding)::text <> ALL (ARRAY[('Expired'::character varying)::text, ('Broken'::character varying)::text, ('Cold Chain Failure'::character varying)::text])) THEN t.quantity_discarded_unopened
            ELSE 0
        END AS other,
    0 AS children_immunized,
    0 AS pregnant_women_immunized,
    t.vaccinated,
    t.usage_denominator,
        CASE
            WHEN (t.usage_denominator > 0) THEN (round(((t.vaccinated)::numeric / (t.usage_denominator)::numeric), 4) * (100)::numeric)
            ELSE NULL::numeric
        END AS usage_rate,
        CASE
            WHEN (t.usage_denominator > 0) THEN ((100)::numeric - (round(((t.vaccinated)::numeric / (t.usage_denominator)::numeric), 4) * (100)::numeric))
            ELSE NULL::numeric
        END AS wastage_rate
   FROM t
  ORDER BY t.display_order;


ALTER TABLE vw_vaccine_stock_status OWNER TO postgres;

--
-- Name: vw_vaccine_target_population; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_target_population AS
 SELECT e.year,
    e.facilityid AS facility_id,
    f.geographiczoneid AS geographic_zone_id,
    c.id AS category_id,
    c.name AS category_name,
    e.value AS target_value_annual,
    round(((e.value / 12))::double precision) AS target_value_monthly
   FROM ((demographic_estimate_categories c
     LEFT JOIN facility_demographic_estimates e ON ((c.id = e.demographicestimateid)))
     JOIN facilities f ON ((e.facilityid = f.id)));


ALTER TABLE vw_vaccine_target_population OWNER TO postgres;

--
-- Name: vw_vaccine_vitamin_supplementation; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW vw_vaccine_vitamin_supplementation AS
 WITH tmp AS (
         SELECT geographic_zones.id AS geographic_zone_id,
            geographic_zones.name AS geographic_zone_name,
            geographic_zones.levelid AS level_id,
            geographic_zones.parentid AS parent_id,
            facilities.id AS facility_id,
            facilities.code AS facility_code,
            facilities.name AS facility_name,
            vaccine_reports.periodid AS period_id,
            processing_periods.name AS period_name,
            (processing_periods.startdate)::date AS period_start_date,
            (processing_periods.enddate)::date AS period_end_date,
            date_part('year'::text, processing_periods.startdate) AS report_year,
            vaccine_vitamin_supplementation_age_groups.name AS age_group,
            vaccine_vitamins.name AS vitamin_name,
            vaccine_reports.id AS report_id,
            vaccine_reports.status,
            vaccine_report_vitamin_supplementation_line_items.malevalue AS male_value,
            vaccine_report_vitamin_supplementation_line_items.femalevalue AS female_value,
            (vaccine_report_vitamin_supplementation_line_items.malevalue + vaccine_report_vitamin_supplementation_line_items.femalevalue) AS total_value
           FROM ((((((vaccine_report_vitamin_supplementation_line_items
             JOIN vaccine_reports ON ((vaccine_report_vitamin_supplementation_line_items.reportid = vaccine_reports.id)))
             JOIN processing_periods ON ((vaccine_reports.periodid = processing_periods.id)))
             JOIN facilities ON ((vaccine_reports.facilityid = facilities.id)))
             JOIN geographic_zones ON ((facilities.geographiczoneid = geographic_zones.id)))
             JOIN vaccine_vitamin_supplementation_age_groups ON ((vaccine_vitamin_supplementation_age_groups.id = vaccine_report_vitamin_supplementation_line_items.vitaminagegroupid)))
             JOIN vaccine_vitamins ON ((vaccine_vitamins.id = vaccine_report_vitamin_supplementation_line_items.vaccinevitaminid)))
        )
 SELECT t.geographic_zone_id,
    t.geographic_zone_name,
    t.level_id,
    t.parent_id,
    t.facility_id,
    t.facility_code,
    t.facility_name,
    t.report_id,
    t.period_id,
    t.report_year,
    t.age_group,
    t.vitamin_name,
    t.period_start_date,
    t.male_value,
    t.female_value,
    t.total_value
   FROM tmp t;


ALTER TABLE vw_vaccine_vitamin_supplementation OWNER TO postgres;

SET search_path = atomfeed, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: atomfeed; Owner: postgres
--

ALTER TABLE ONLY chunking_history ALTER COLUMN id SET DEFAULT nextval('chunking_history_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: atomfeed; Owner: postgres
--

ALTER TABLE ONLY event_records ALTER COLUMN id SET DEFAULT nextval('event_records_id_seq'::regclass);


SET search_path = public, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY adult_coverage_opened_vial_line_items ALTER COLUMN id SET DEFAULT nextval('adult_coverage_opened_vial_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_facility_stockedout ALTER COLUMN id SET DEFAULT nextval('alert_facility_stockedout_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_requisition_approved ALTER COLUMN id SET DEFAULT nextval('alert_requisition_approved_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_requisition_emergency ALTER COLUMN id SET DEFAULT nextval('alert_requisition_emergency_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_requisition_pending ALTER COLUMN id SET DEFAULT nextval('alert_requisition_pending_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_requisition_rejected ALTER COLUMN id SET DEFAULT nextval('alert_requisition_rejected_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_stockedout ALTER COLUMN id SET DEFAULT nextval('alert_stockedout_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY alert_summary ALTER COLUMN id SET DEFAULT nextval('alert_summary_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_file_columns ALTER COLUMN id SET DEFAULT nextval('budget_file_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_file_info ALTER COLUMN id SET DEFAULT nextval('budget_file_info_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items ALTER COLUMN id SET DEFAULT nextval('budget_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY child_coverage_opened_vial_line_items ALTER COLUMN id SET DEFAULT nextval('opened_vial_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments ALTER COLUMN id SET DEFAULT nextval('comments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY configurable_rnr_options ALTER COLUMN id SET DEFAULT nextval('configurable_rnr_options_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY configuration_settings ALTER COLUMN id SET DEFAULT nextval('configuration_settings_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY coverage_product_vials ALTER COLUMN id SET DEFAULT nextval('coverage_product_vials_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY coverage_target_group_products ALTER COLUMN id SET DEFAULT nextval('coverage_vaccination_products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY custom_reports ALTER COLUMN id SET DEFAULT nextval('custom_reports_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_members ALTER COLUMN id SET DEFAULT nextval('delivery_zone_members_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules ALTER COLUMN id SET DEFAULT nextval('delivery_zone_program_schedules_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_warehouses ALTER COLUMN id SET DEFAULT nextval('delivery_zone_warehouses_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zones ALTER COLUMN id SET DEFAULT nextval('delivery_zones_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY demographic_estimate_categories ALTER COLUMN id SET DEFAULT nextval('demographic_estimate_categories_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions ALTER COLUMN id SET DEFAULT nextval('distributions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates ALTER COLUMN id SET DEFAULT nextval('district_demographic_estimates_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY donors ALTER COLUMN id SET DEFAULT nextval('donors_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dosage_frequencies ALTER COLUMN id SET DEFAULT nextval('dosage_frequencies_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY dosage_units ALTER COLUMN id SET DEFAULT nextval('dosage_units_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help ALTER COLUMN id SET DEFAULT nextval('elmis_help_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_document ALTER COLUMN id SET DEFAULT nextval('elmis_help_document_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic ALTER COLUMN id SET DEFAULT nextval('elmis_help_topic_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic_roles ALTER COLUMN id SET DEFAULT nextval('elmis_help_topic_roles_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY email_attachments ALTER COLUMN id SET DEFAULT nextval('email_attachments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY email_notifications ALTER COLUMN id SET DEFAULT nextval('email_notifications_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY emergency_requisitions ALTER COLUMN id SET DEFAULT nextval('emergency_requisitions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_inventory_line_items ALTER COLUMN id SET DEFAULT nextval('epi_inventory_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items ALTER COLUMN id SET DEFAULT nextval('epi_use_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipment_designations ALTER COLUMN id SET DEFAULT nextval('equipment_cold_chain_equipment_designations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipment_pqs_status ALTER COLUMN id SET DEFAULT nextval('equipment_cold_chain_equipment_pqs_status_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_contract_service_types ALTER COLUMN id SET DEFAULT nextval('equipment_contract_service_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_energy_types ALTER COLUMN id SET DEFAULT nextval('equipment_cold_chain_equipment_energy_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories ALTER COLUMN id SET DEFAULT nextval('facility_program_equipments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses ALTER COLUMN id SET DEFAULT nextval('equipment_inventory_statuses_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs ALTER COLUMN id SET DEFAULT nextval('equipment_maintenance_logs_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests ALTER COLUMN id SET DEFAULT nextval('equipment_maintenance_requests_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_operational_status ALTER COLUMN id SET DEFAULT nextval('equipment_operational_status_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_equipment_types ALTER COLUMN id SET DEFAULT nextval('equipment_service_contract_equipments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_facilities ALTER COLUMN id SET DEFAULT nextval('equipment_service_contract_facilities_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contracts ALTER COLUMN id SET DEFAULT nextval('equipment_service_contracts_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_types ALTER COLUMN id SET DEFAULT nextval('equipment_service_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_vendor_users ALTER COLUMN id SET DEFAULT nextval('equipment_service_vendor_users_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_vendors ALTER COLUMN id SET DEFAULT nextval('equipment_service_vendors_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items ALTER COLUMN id SET DEFAULT nextval('equipment_status_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_products ALTER COLUMN id SET DEFAULT nextval('program_equipment_products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_programs ALTER COLUMN id SET DEFAULT nextval('program_equipments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_types ALTER COLUMN id SET DEFAULT nextval('equipment_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipments ALTER COLUMN id SET DEFAULT nextval('equipments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities ALTER COLUMN id SET DEFAULT nextval('facilities_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_approved_products ALTER COLUMN id SET DEFAULT nextval('facility_approved_products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates ALTER COLUMN id SET DEFAULT nextval('facility_demographic_estimates_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_ftp_details ALTER COLUMN id SET DEFAULT nextval('facility_ftp_details_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_mappings ALTER COLUMN id SET DEFAULT nextval('facility_mappings_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_operators ALTER COLUMN id SET DEFAULT nextval('facility_operators_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products ALTER COLUMN id SET DEFAULT nextval('facility_program_products_id_seq1'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_types ALTER COLUMN id SET DEFAULT nextval('facility_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_visits ALTER COLUMN id SET DEFAULT nextval('facility_visits_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY full_coverages ALTER COLUMN id SET DEFAULT nextval('vaccination_full_coverages_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_levels ALTER COLUMN id SET DEFAULT nextval('geographic_levels_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_zone_geojson ALTER COLUMN id SET DEFAULT nextval('geographic_zone_geojson_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_zones ALTER COLUMN id SET DEFAULT nextval('geographic_zones_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY gtin_lookups ALTER COLUMN id SET DEFAULT nextval('gtin_lookups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interface_apps ALTER COLUMN id SET DEFAULT nextval('interface_apps_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interface_dataset ALTER COLUMN id SET DEFAULT nextval('interface_dataset_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY isa_coefficients ALTER COLUMN id SET DEFAULT nextval('program_product_isa_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots ALTER COLUMN id SET DEFAULT nextval('lots_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots_on_hand ALTER COLUMN id SET DEFAULT nextval('lots_on_hand_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY manufacturers ALTER COLUMN id SET DEFAULT nextval('manufacturers_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_rnr_column_options ALTER COLUMN id SET DEFAULT nextval('master_rnr_column_options_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_rnr_columns ALTER COLUMN id SET DEFAULT nextval('master_rnr_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_basis ALTER COLUMN id SET DEFAULT nextval('mos_adjustment_basis_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_facilities ALTER COLUMN id SET DEFAULT nextval('mos_adjustment_facilities_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products ALTER COLUMN id SET DEFAULT nextval('mos_adjustment_products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_types ALTER COLUMN id SET DEFAULT nextval('mos_adjustment_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_file_columns ALTER COLUMN id SET DEFAULT nextval('order_file_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_factors ALTER COLUMN id SET DEFAULT nextval('order_quantity_adjustment_factors_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products ALTER COLUMN id SET DEFAULT nextval('order_quantity_adjustment_products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_types ALTER COLUMN id SET DEFAULT nextval('order_quantity_adjustment_types_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY patient_quantification_line_items ALTER COLUMN id SET DEFAULT nextval('patient_quantification_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY period_short_names ALTER COLUMN id SET DEFAULT nextval('period_short_names_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod ALTER COLUMN id SET DEFAULT nextval('pod_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod_line_items ALTER COLUMN id SET DEFAULT nextval('pod_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY price_schedules ALTER COLUMN id SET DEFAULT nextval('price_schedules_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY processing_periods ALTER COLUMN id SET DEFAULT nextval('processing_periods_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY processing_schedules ALTER COLUMN id SET DEFAULT nextval('processing_schedules_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_categories ALTER COLUMN id SET DEFAULT nextval('product_categories_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_forms ALTER COLUMN id SET DEFAULT nextval('product_forms_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_groups ALTER COLUMN id SET DEFAULT nextval('product_groups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_price_schedules ALTER COLUMN id SET DEFAULT nextval('product_price_schedules_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_short_names ALTER COLUMN id SET DEFAULT nextval('product_short_names_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products ALTER COLUMN id SET DEFAULT nextval('products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_product_price_history ALTER COLUMN id SET DEFAULT nextval('program_product_price_history_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products ALTER COLUMN id SET DEFAULT nextval('program_products_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_regimen_columns ALTER COLUMN id SET DEFAULT nextval('program_regimen_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_rnr_columns ALTER COLUMN id SET DEFAULT nextval('program_rnr_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY programs ALTER COLUMN id SET DEFAULT nextval('programs_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY programs_supported ALTER COLUMN id SET DEFAULT nextval('programs_supported_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_problems ALTER COLUMN id SET DEFAULT nextval('refrigerator_problems_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_readings ALTER COLUMN id SET DEFAULT nextval('distribution_refrigerator_readings_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators ALTER COLUMN id SET DEFAULT nextval('refrigerators_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_categories ALTER COLUMN id SET DEFAULT nextval('regimen_categories_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_combination_constituents ALTER COLUMN id SET DEFAULT nextval('regimen_combination_constituents_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages ALTER COLUMN id SET DEFAULT nextval('regimen_constituents_dosages_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_line_items ALTER COLUMN id SET DEFAULT nextval('regimen_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_product_combinations ALTER COLUMN id SET DEFAULT nextval('regimen_product_combinations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimens ALTER COLUMN id SET DEFAULT nextval('regimens_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY report_rights ALTER COLUMN id SET DEFAULT nextval('report_rights_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_members ALTER COLUMN id SET DEFAULT nextval('requisition_group_members_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules ALTER COLUMN id SET DEFAULT nextval('requisition_group_program_schedules_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_groups ALTER COLUMN id SET DEFAULT nextval('requisition_groups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_items ALTER COLUMN id SET DEFAULT nextval('requisition_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes ALTER COLUMN id SET DEFAULT nextval('requisition_status_changes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions ALTER COLUMN id SET DEFAULT nextval('requisitions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY roles ALTER COLUMN id SET DEFAULT nextval('roles_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_file_columns ALTER COLUMN id SET DEFAULT nextval('shipment_file_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_file_info ALTER COLUMN id SET DEFAULT nextval('shipment_file_info_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_line_items ALTER COLUMN id SET DEFAULT nextval('shipment_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY signatures ALTER COLUMN id SET DEFAULT nextval('signatures_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sms ALTER COLUMN id SET DEFAULT nextval('sms_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_adjustment_reasons_programs ALTER COLUMN id SET DEFAULT nextval('stock_adjustment_reasons_programs_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries ALTER COLUMN id SET DEFAULT nextval('stock_card_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_cards ALTER COLUMN id SET DEFAULT nextval('stock_cards_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_item_extra_fields ALTER COLUMN id SET DEFAULT nextval('stock_movement_line_item_extra_fields_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_items ALTER COLUMN id SET DEFAULT nextval('stock_movement_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_lots ALTER COLUMN id SET DEFAULT nextval('stock_movement_lots_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movements ALTER COLUMN id SET DEFAULT nextval('stock_movements_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supervisory_nodes ALTER COLUMN id SET DEFAULT nextval('supervisory_nodes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines ALTER COLUMN id SET DEFAULT nextval('supply_lines_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY template_parameters ALTER COLUMN id SET DEFAULT nextval('template_parameters_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY templates ALTER COLUMN id SET DEFAULT nextval('report_templates_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preference_master ALTER COLUMN id SET DEFAULT nextval('user_preference_master_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccination_adult_coverage_line_items ALTER COLUMN id SET DEFAULT nextval('vaccination_adult_coverage_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccination_child_coverage_line_items ALTER COLUMN id SET DEFAULT nextval('vaccination_child_coverage_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_discarding_reasons ALTER COLUMN id SET DEFAULT nextval('vaccine_discarding_reasons_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_diseases ALTER COLUMN id SET DEFAULT nextval('vaccine_diseases_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_item_lots ALTER COLUMN id SET DEFAULT nextval('vaccine_distribution_line_item_lots_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_distribution_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions ALTER COLUMN id SET DEFAULT nextval('vaccine_distributions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_doses ALTER COLUMN id SET DEFAULT nextval('vaccine_doses_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_inventory_product_configurations ALTER COLUMN id SET DEFAULT nextval('vaccine_inventory_product_configurations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_ivd_tab_visibilities ALTER COLUMN id SET DEFAULT nextval('vaccine_ivd_tab_visibilities_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_logistics_master_columns ALTER COLUMN id SET DEFAULT nextval('vaccine_logistics_master_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_lots_on_hand_adjustments ALTER COLUMN id SET DEFAULT nextval('vaccine_lots_on_hand_adjustments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_lots_on_hand_vvm ALTER COLUMN id SET DEFAULT nextval('vaccine_lots_on_hand_vvm_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisition_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_order_requisition_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisition_master_columns ALTER COLUMN id SET DEFAULT nextval('vaccine_order_requisition_master_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisition_status_changes ALTER COLUMN id SET DEFAULT nextval('vaccine_order_requisition_status_changes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions ALTER COLUMN id SET DEFAULT nextval('vaccine_order_requisitions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses ALTER COLUMN id SET DEFAULT nextval('vaccine_product_doses_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_program_logistics_columns ALTER COLUMN id SET DEFAULT nextval('vaccine_program_logistics_columns_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_adverse_effect_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_adverse_effect_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_campaign_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_campaign_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_cold_chain_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_cold_chain_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_coverage_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_disease_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_disease_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_logistics_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_status_changes ALTER COLUMN id SET DEFAULT nextval('vaccine_report_status_changes_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items ALTER COLUMN id SET DEFAULT nextval('vaccine_report_vitamin_supplementation_line_items_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports ALTER COLUMN id SET DEFAULT nextval('vaccine_reports_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_vitamin_supplementation_age_groups ALTER COLUMN id SET DEFAULT nextval('vaccine_vitamin_supplementation_age_groups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_vitamins ALTER COLUMN id SET DEFAULT nextval('vaccine_vitamins_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY var_details ALTER COLUMN id SET DEFAULT nextval('var_details_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY var_item_alarms ALTER COLUMN id SET DEFAULT nextval('var_item_alarms_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY var_item_partials ALTER COLUMN id SET DEFAULT nextval('var_item_partials_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY var_items ALTER COLUMN id SET DEFAULT nextval('var_items_id_seq'::regclass);


SET search_path = atomfeed, pg_catalog;

--
-- Name: chunking_history_pkey; Type: CONSTRAINT; Schema: atomfeed; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY chunking_history
    ADD CONSTRAINT chunking_history_pkey PRIMARY KEY (id);


--
-- Name: event_records_pkey; Type: CONSTRAINT; Schema: atomfeed; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY event_records
    ADD CONSTRAINT event_records_pkey PRIMARY KEY (id);


SET search_path = public, pg_catalog;

--
-- Name: adjusment_reasons_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_lots_on_hand_adjustments
    ADD CONSTRAINT adjusment_reasons_pkey PRIMARY KEY (id);


--
-- Name: adult_coverage_opened_vial_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY adult_coverage_opened_vial_line_items
    ADD CONSTRAINT adult_coverage_opened_vial_line_items_pkey PRIMARY KEY (id);


--
-- Name: alert_facility_stockedout_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_facility_stockedout
    ADD CONSTRAINT alert_facility_stockedout_pkey PRIMARY KEY (id);


--
-- Name: alert_requisition_approved_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_requisition_approved
    ADD CONSTRAINT alert_requisition_approved_pkey PRIMARY KEY (id);


--
-- Name: alert_requisition_emergency_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_requisition_emergency
    ADD CONSTRAINT alert_requisition_emergency_pkey PRIMARY KEY (id);


--
-- Name: alert_requisition_pending_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_requisition_pending
    ADD CONSTRAINT alert_requisition_pending_pk PRIMARY KEY (id);


--
-- Name: alert_requisition_rejected_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_requisition_rejected
    ADD CONSTRAINT alert_requisition_rejected_pk PRIMARY KEY (id);


--
-- Name: alert_stockedout_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_stockedout
    ADD CONSTRAINT alert_stockedout_pkey PRIMARY KEY (id);


--
-- Name: alert_summary_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alert_summary
    ADD CONSTRAINT alert_summary_pkey PRIMARY KEY (id);


--
-- Name: alerts_pk; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY alerts
    ADD CONSTRAINT alerts_pk PRIMARY KEY (alerttype);


--
-- Name: budget_file_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY budget_file_columns
    ADD CONSTRAINT budget_file_columns_pkey PRIMARY KEY (id);


--
-- Name: budget_file_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY budget_file_info
    ADD CONSTRAINT budget_file_info_pkey PRIMARY KEY (id);


--
-- Name: budget_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_pkey PRIMARY KEY (id);


--
-- Name: cce_designations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_cold_chain_equipment_designations
    ADD CONSTRAINT cce_designations_pkey PRIMARY KEY (id);


--
-- Name: cce_energy_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_energy_types
    ADD CONSTRAINT cce_energy_types_pkey PRIMARY KEY (id);


--
-- Name: cce_psqstatus_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_cold_chain_equipment_pqs_status
    ADD CONSTRAINT cce_psqstatus_pkey PRIMARY KEY (id);


--
-- Name: comments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_pkey PRIMARY KEY (id);


--
-- Name: configurable_rnr_options_label_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configurable_rnr_options
    ADD CONSTRAINT configurable_rnr_options_label_key UNIQUE (label);


--
-- Name: configurable_rnr_options_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configurable_rnr_options
    ADD CONSTRAINT configurable_rnr_options_name_key UNIQUE (name);


--
-- Name: configurable_rnr_options_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configurable_rnr_options
    ADD CONSTRAINT configurable_rnr_options_pkey PRIMARY KEY (id);


--
-- Name: configuration_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY configuration_settings
    ADD CONSTRAINT configuration_settings_pkey PRIMARY KEY (id);


--
-- Name: coverage_product_vials_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY coverage_product_vials
    ADD CONSTRAINT coverage_product_vials_pkey PRIMARY KEY (id);


--
-- Name: coverage_product_vials_vial_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY coverage_product_vials
    ADD CONSTRAINT coverage_product_vials_vial_key UNIQUE (vial);


--
-- Name: coverage_vaccination_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY coverage_target_group_products
    ADD CONSTRAINT coverage_vaccination_products_pkey PRIMARY KEY (id);


--
-- Name: custom_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY custom_reports
    ADD CONSTRAINT custom_reports_pkey PRIMARY KEY (id);


--
-- Name: custom_reports_reportkey_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY custom_reports
    ADD CONSTRAINT custom_reports_reportkey_key UNIQUE (reportkey);


--
-- Name: delivery_zone_members_deliveryzoneid_facilityid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zone_members
    ADD CONSTRAINT delivery_zone_members_deliveryzoneid_facilityid_key UNIQUE (deliveryzoneid, facilityid);


--
-- Name: delivery_zone_members_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zone_members
    ADD CONSTRAINT delivery_zone_members_pkey PRIMARY KEY (id);


--
-- Name: delivery_zone_program_schedules_deliveryzoneid_programid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_deliveryzoneid_programid_key UNIQUE (deliveryzoneid, programid);


--
-- Name: delivery_zone_program_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_pkey PRIMARY KEY (id);


--
-- Name: delivery_zone_warehouses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zone_warehouses
    ADD CONSTRAINT delivery_zone_warehouses_pkey PRIMARY KEY (id);


--
-- Name: delivery_zones_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zones
    ADD CONSTRAINT delivery_zones_code_key UNIQUE (code);


--
-- Name: delivery_zones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY delivery_zones
    ADD CONSTRAINT delivery_zones_pkey PRIMARY KEY (id);


--
-- Name: demographic_estimate_categories_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY demographic_estimate_categories
    ADD CONSTRAINT demographic_estimate_categories_name_key UNIQUE (name);


--
-- Name: demographic_estimate_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY demographic_estimate_categories
    ADD CONSTRAINT demographic_estimate_categories_pkey PRIMARY KEY (id);


--
-- Name: distribution_refrigerator_readings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY refrigerator_readings
    ADD CONSTRAINT distribution_refrigerator_readings_pkey PRIMARY KEY (id);


--
-- Name: distributions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_pkey PRIMARY KEY (id);


--
-- Name: district_demographic_estimates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_pkey PRIMARY KEY (id);


--
-- Name: donors_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY donors
    ADD CONSTRAINT donors_code_key UNIQUE (code);


--
-- Name: donors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY donors
    ADD CONSTRAINT donors_pkey PRIMARY KEY (id);


--
-- Name: dosage_frequency_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dosage_frequencies
    ADD CONSTRAINT dosage_frequency_pkey PRIMARY KEY (id);


--
-- Name: dosage_units_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY dosage_units
    ADD CONSTRAINT dosage_units_pkey PRIMARY KEY (id);


--
-- Name: egimen_product_dosage_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT egimen_product_dosage_pkey PRIMARY KEY (id);


--
-- Name: elmis_help_document_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY elmis_help_document
    ADD CONSTRAINT elmis_help_document_pkey PRIMARY KEY (id);


--
-- Name: elmis_help_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY elmis_help
    ADD CONSTRAINT elmis_help_pkey PRIMARY KEY (id);


--
-- Name: elmis_help_topic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT elmis_help_topic_pkey PRIMARY KEY (id);


--
-- Name: elmis_help_topic_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY elmis_help_topic_roles
    ADD CONSTRAINT elmis_help_topic_roles_pkey PRIMARY KEY (id);


--
-- Name: email_attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY email_attachments
    ADD CONSTRAINT email_attachments_pkey PRIMARY KEY (id);


--
-- Name: email_notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY email_notifications
    ADD CONSTRAINT email_notifications_pkey PRIMARY KEY (id);


--
-- Name: emergency_requisitions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY emergency_requisitions
    ADD CONSTRAINT emergency_requisitions_pkey PRIMARY KEY (id);


--
-- Name: epi_inventory_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY epi_inventory_line_items
    ADD CONSTRAINT epi_inventory_line_items_pkey PRIMARY KEY (id);


--
-- Name: epi_use_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_pkey PRIMARY KEY (id);


--
-- Name: equipment_cce_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_pkey PRIMARY KEY (equipmentid);


--
-- Name: equipment_contract_service_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_contract_service_types
    ADD CONSTRAINT equipment_contract_service_types_pkey PRIMARY KEY (id);


--
-- Name: equipment_inventory_status_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT equipment_inventory_status_history_pkey PRIMARY KEY (id);


--
-- Name: equipment_maintenance_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_pkey PRIMARY KEY (id);


--
-- Name: equipment_maintenance_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_pkey PRIMARY KEY (id);


--
-- Name: equipment_operational_status_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_operational_status
    ADD CONSTRAINT equipment_operational_status_pkey PRIMARY KEY (id);


--
-- Name: equipment_service_contract_equipments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_service_contract_equipment_types
    ADD CONSTRAINT equipment_service_contract_equipments_pkey PRIMARY KEY (id);


--
-- Name: equipment_service_contract_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_service_contract_facilities
    ADD CONSTRAINT equipment_service_contract_facilities_pkey PRIMARY KEY (id);


--
-- Name: equipment_service_contracts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_service_contracts
    ADD CONSTRAINT equipment_service_contracts_pkey PRIMARY KEY (id);


--
-- Name: equipment_service_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_service_types
    ADD CONSTRAINT equipment_service_types_pkey PRIMARY KEY (id);


--
-- Name: equipment_service_vendor_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_service_vendor_users
    ADD CONSTRAINT equipment_service_vendor_users_pkey PRIMARY KEY (id);


--
-- Name: equipment_service_vendors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_service_vendors
    ADD CONSTRAINT equipment_service_vendors_pkey PRIMARY KEY (id);


--
-- Name: equipment_status_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_pkey PRIMARY KEY (id);


--
-- Name: equipment_types_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_types
    ADD CONSTRAINT equipment_types_code_key UNIQUE (code);


--
-- Name: equipment_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_types
    ADD CONSTRAINT equipment_types_pkey PRIMARY KEY (id);


--
-- Name: equipments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipments
    ADD CONSTRAINT equipments_pkey PRIMARY KEY (id);


--
-- Name: facilities_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_code_key UNIQUE (code);


--
-- Name: facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_pkey PRIMARY KEY (id);


--
-- Name: facility_approved_products_facilitytypeid_programproductid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_approved_products
    ADD CONSTRAINT facility_approved_products_facilitytypeid_programproductid_key UNIQUE (facilitytypeid, programproductid);


--
-- Name: facility_approved_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_approved_products
    ADD CONSTRAINT facility_approved_products_pkey PRIMARY KEY (id);


--
-- Name: facility_demographic_estimates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_pkey PRIMARY KEY (id);


--
-- Name: facility_ftp_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_ftp_details
    ADD CONSTRAINT facility_ftp_details_pkey PRIMARY KEY (id);


--
-- Name: facility_mappings_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_mappings
    ADD CONSTRAINT facility_mappings_pkey PRIMARY KEY (id);


--
-- Name: facility_operators_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_operators
    ADD CONSTRAINT facility_operators_code_key UNIQUE (code);


--
-- Name: facility_operators_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_operators
    ADD CONSTRAINT facility_operators_pkey PRIMARY KEY (id);


--
-- Name: facility_program_equipments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_pkey PRIMARY KEY (id);


--
-- Name: facility_program_products_facilityid_programproductid_key1; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_facilityid_programproductid_key1 UNIQUE (facilityid, programproductid);


--
-- Name: facility_program_products_pkey1; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_pkey1 PRIMARY KEY (id);


--
-- Name: facility_types_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_types
    ADD CONSTRAINT facility_types_code_key UNIQUE (code);


--
-- Name: facility_types_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_types
    ADD CONSTRAINT facility_types_name_key UNIQUE (name);


--
-- Name: facility_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_types
    ADD CONSTRAINT facility_types_pkey PRIMARY KEY (id);


--
-- Name: facility_visits_distributionid_facilityid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_visits
    ADD CONSTRAINT facility_visits_distributionid_facilityid_key UNIQUE (distributionid, facilityid);


--
-- Name: facility_visits_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY facility_visits
    ADD CONSTRAINT facility_visits_pkey PRIMARY KEY (id);


--
-- Name: geographic_levels_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY geographic_levels
    ADD CONSTRAINT geographic_levels_code_key UNIQUE (code);


--
-- Name: geographic_levels_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY geographic_levels
    ADD CONSTRAINT geographic_levels_pkey PRIMARY KEY (id);


--
-- Name: geographic_zone_geojson_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY geographic_zone_geojson
    ADD CONSTRAINT geographic_zone_geojson_pkey PRIMARY KEY (id);


--
-- Name: geographic_zones_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY geographic_zones
    ADD CONSTRAINT geographic_zones_code_key UNIQUE (code);


--
-- Name: geographic_zones_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY geographic_zones
    ADD CONSTRAINT geographic_zones_pkey PRIMARY KEY (id);


--
-- Name: gtin_lookups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY gtin_lookups
    ADD CONSTRAINT gtin_lookups_pkey PRIMARY KEY (id);


--
-- Name: interface_apps_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY interface_apps
    ADD CONSTRAINT interface_apps_pkey PRIMARY KEY (id);


--
-- Name: interface_dataset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY interface_dataset
    ADD CONSTRAINT interface_dataset_pkey PRIMARY KEY (id);


--
-- Name: isa_coefficients_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY isa_coefficients
    ADD CONSTRAINT isa_coefficients_pkey PRIMARY KEY (id);


--
-- Name: losses_adjustments_types_description_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY losses_adjustments_types
    ADD CONSTRAINT losses_adjustments_types_description_key UNIQUE (description);


--
-- Name: losses_adjustments_types_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY losses_adjustments_types
    ADD CONSTRAINT losses_adjustments_types_name_key UNIQUE (name);


--
-- Name: lots_on_hand_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY lots_on_hand
    ADD CONSTRAINT lots_on_hand_pkey PRIMARY KEY (id);


--
-- Name: lots_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY lots
    ADD CONSTRAINT lots_pkey PRIMARY KEY (id);


--
-- Name: manufacturers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY manufacturers
    ADD CONSTRAINT manufacturers_pkey PRIMARY KEY (id);


--
-- Name: master_rnr_column_options_masterrnrcolumnid_rnroptionid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY master_rnr_column_options
    ADD CONSTRAINT master_rnr_column_options_masterrnrcolumnid_rnroptionid_key UNIQUE (masterrnrcolumnid, rnroptionid);


--
-- Name: master_rnr_column_options_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY master_rnr_column_options
    ADD CONSTRAINT master_rnr_column_options_pkey PRIMARY KEY (id);


--
-- Name: master_rnr_columns_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY master_rnr_columns
    ADD CONSTRAINT master_rnr_columns_name_key UNIQUE (name);


--
-- Name: master_rnr_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY master_rnr_columns
    ADD CONSTRAINT master_rnr_columns_pkey PRIMARY KEY (id);


--
-- Name: mos_adjustment_basis_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mos_adjustment_basis
    ADD CONSTRAINT mos_adjustment_basis_pkey PRIMARY KEY (id);


--
-- Name: mos_adjustment_facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mos_adjustment_facilities
    ADD CONSTRAINT mos_adjustment_facilities_pkey PRIMARY KEY (id);


--
-- Name: mos_adjustment_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_pkey PRIMARY KEY (id);


--
-- Name: mos_adjustment_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY mos_adjustment_types
    ADD CONSTRAINT mos_adjustment_types_pkey PRIMARY KEY (id);


--
-- Name: opened_vial_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY child_coverage_opened_vial_line_items
    ADD CONSTRAINT opened_vial_line_items_pkey PRIMARY KEY (id);


--
-- Name: order_file_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY order_file_columns
    ADD CONSTRAINT order_file_columns_pkey PRIMARY KEY (id);


--
-- Name: order_quantity_adjustment_factors_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY order_quantity_adjustment_factors
    ADD CONSTRAINT order_quantity_adjustment_factors_pkey PRIMARY KEY (id);


--
-- Name: order_quantity_adjustment_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_pkey PRIMARY KEY (id);


--
-- Name: order_quantity_adjustment_types_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY order_quantity_adjustment_types
    ADD CONSTRAINT order_quantity_adjustment_types_pkey PRIMARY KEY (id);


--
-- Name: orders_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_id_key UNIQUE (id);


--
-- Name: patient_quantification_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY patient_quantification_line_items
    ADD CONSTRAINT patient_quantification_line_items_pkey PRIMARY KEY (id);


--
-- Name: period_short_names_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY period_short_names
    ADD CONSTRAINT period_short_names_pkey PRIMARY KEY (id);


--
-- Name: pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_lots_on_hand_vvm
    ADD CONSTRAINT pkey PRIMARY KEY (id);


--
-- Name: pod_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pod_line_items
    ADD CONSTRAINT pod_line_items_pkey PRIMARY KEY (id);


--
-- Name: pod_orderid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_orderid_key UNIQUE (orderid);


--
-- Name: pod_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_pkey PRIMARY KEY (id);


--
-- Name: price_schedules_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY price_schedules
    ADD CONSTRAINT price_schedules_code_key UNIQUE (code);


--
-- Name: price_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY price_schedules
    ADD CONSTRAINT price_schedules_pkey PRIMARY KEY (id);


--
-- Name: processing_periods_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY processing_periods
    ADD CONSTRAINT processing_periods_pkey PRIMARY KEY (id);


--
-- Name: processing_schedules_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY processing_schedules
    ADD CONSTRAINT processing_schedules_code_key UNIQUE (code);


--
-- Name: processing_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY processing_schedules
    ADD CONSTRAINT processing_schedules_pkey PRIMARY KEY (id);


--
-- Name: product_categories_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_categories
    ADD CONSTRAINT product_categories_code_key UNIQUE (code);


--
-- Name: product_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_categories
    ADD CONSTRAINT product_categories_pkey PRIMARY KEY (id);


--
-- Name: product_forms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_forms
    ADD CONSTRAINT product_forms_pkey PRIMARY KEY (id);


--
-- Name: product_groups_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_groups
    ADD CONSTRAINT product_groups_code_key UNIQUE (code);


--
-- Name: product_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_groups
    ADD CONSTRAINT product_groups_pkey PRIMARY KEY (id);


--
-- Name: product_price_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_price_schedules
    ADD CONSTRAINT product_price_schedules_pkey PRIMARY KEY (id);


--
-- Name: product_price_schedules_pricescheduleid_productid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_price_schedules
    ADD CONSTRAINT product_price_schedules_pricescheduleid_productid_key UNIQUE (pricescheduleid, productid);


--
-- Name: product_short_names_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY product_short_names
    ADD CONSTRAINT product_short_names_pkey PRIMARY KEY (id);


--
-- Name: products_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_code_key UNIQUE (code);


--
-- Name: products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id);


--
-- Name: program_equipment_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_type_products
    ADD CONSTRAINT program_equipment_products_pkey PRIMARY KEY (id);


--
-- Name: program_equipments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY equipment_type_programs
    ADD CONSTRAINT program_equipments_pkey PRIMARY KEY (id);


--
-- Name: program_product_price_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY program_product_price_history
    ADD CONSTRAINT program_product_price_history_pkey PRIMARY KEY (id);


--
-- Name: program_products_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_pkey PRIMARY KEY (id);


--
-- Name: program_products_productid_programid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_productid_programid_key UNIQUE (productid, programid);


--
-- Name: program_regimen_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY program_regimen_columns
    ADD CONSTRAINT program_regimen_columns_pkey PRIMARY KEY (id);


--
-- Name: program_rnr_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY program_rnr_columns
    ADD CONSTRAINT program_rnr_columns_pkey PRIMARY KEY (id);


--
-- Name: program_rnr_columns_programid_mastercolumnid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY program_rnr_columns
    ADD CONSTRAINT program_rnr_columns_programid_mastercolumnid_key UNIQUE (programid, mastercolumnid);


--
-- Name: programs_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY programs
    ADD CONSTRAINT programs_code_key UNIQUE (code);


--
-- Name: programs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY programs
    ADD CONSTRAINT programs_pkey PRIMARY KEY (id);


--
-- Name: programs_supported_facilityid_programid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY programs_supported
    ADD CONSTRAINT programs_supported_facilityid_programid_key UNIQUE (facilityid, programid);


--
-- Name: programs_supported_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY programs_supported
    ADD CONSTRAINT programs_supported_pkey PRIMARY KEY (id);


--
-- Name: refrigerator_problems_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY refrigerator_problems
    ADD CONSTRAINT refrigerator_problems_pkey PRIMARY KEY (id);


--
-- Name: refrigerators_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_pkey PRIMARY KEY (id);


--
-- Name: regimen_categories_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimen_categories
    ADD CONSTRAINT regimen_categories_code_key UNIQUE (code);


--
-- Name: regimen_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimen_categories
    ADD CONSTRAINT regimen_categories_pkey PRIMARY KEY (id);


--
-- Name: regimen_constituents_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimen_combination_constituents
    ADD CONSTRAINT regimen_constituents_pkey PRIMARY KEY (id);


--
-- Name: regimen_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimen_line_items
    ADD CONSTRAINT regimen_line_items_pkey PRIMARY KEY (id);


--
-- Name: regimen_product_combo_id_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimen_product_combinations
    ADD CONSTRAINT regimen_product_combo_id_pkey PRIMARY KEY (id);


--
-- Name: regimens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY regimens
    ADD CONSTRAINT regimens_pkey PRIMARY KEY (id);


--
-- Name: report_rights_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY report_rights
    ADD CONSTRAINT report_rights_pkey PRIMARY KEY (id);


--
-- Name: report_templates_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY templates
    ADD CONSTRAINT report_templates_pkey PRIMARY KEY (id);


--
-- Name: requisition_group_members_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_group_members
    ADD CONSTRAINT requisition_group_members_pkey PRIMARY KEY (id);


--
-- Name: requisition_group_members_requisitiongroupid_facilityid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_group_members
    ADD CONSTRAINT requisition_group_members_requisitiongroupid_facilityid_key UNIQUE (requisitiongroupid, facilityid);


--
-- Name: requisition_group_program_sche_requisitiongroupid_programid_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_sche_requisitiongroupid_programid_key UNIQUE (requisitiongroupid, programid);


--
-- Name: requisition_group_program_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_pkey PRIMARY KEY (id);


--
-- Name: requisition_groups_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_groups
    ADD CONSTRAINT requisition_groups_code_key UNIQUE (code);


--
-- Name: requisition_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_groups
    ADD CONSTRAINT requisition_groups_pkey PRIMARY KEY (id);


--
-- Name: requisition_line_item_losses_adjustments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_line_item_losses_adjustments
    ADD CONSTRAINT requisition_line_item_losses_adjustments_pkey PRIMARY KEY (requisitionlineitemid, type);


--
-- Name: requisition_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_line_items
    ADD CONSTRAINT requisition_line_items_pkey PRIMARY KEY (id);


--
-- Name: requisition_status_changes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_pkey PRIMARY KEY (id);


--
-- Name: requisitions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_pkey PRIMARY KEY (id);


--
-- Name: rights_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY rights
    ADD CONSTRAINT rights_pkey PRIMARY KEY (name);


--
-- Name: roles_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- Name: roles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: shipment_file_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY shipment_file_columns
    ADD CONSTRAINT shipment_file_columns_pkey PRIMARY KEY (id);


--
-- Name: shipment_file_info_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY shipment_file_info
    ADD CONSTRAINT shipment_file_info_pkey PRIMARY KEY (id);


--
-- Name: shipment_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY shipment_line_items
    ADD CONSTRAINT shipment_line_items_pkey PRIMARY KEY (id);


--
-- Name: signatures_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY signatures
    ADD CONSTRAINT signatures_pkey PRIMARY KEY (id);


--
-- Name: sms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sms
    ADD CONSTRAINT sms_pkey PRIMARY KEY (id);


--
-- Name: stock_adjustment_reasons_programs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_adjustment_reasons_programs
    ADD CONSTRAINT stock_adjustment_reasons_programs_pkey PRIMARY KEY (id);


--
-- Name: stock_adjustment_reasons_programs_program_reason_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_adjustment_reasons_programs
    ADD CONSTRAINT stock_adjustment_reasons_programs_program_reason_key UNIQUE (programcode, reasonname);


--
-- Name: stock_card_entry_key_values_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_card_entry_key_values
    ADD CONSTRAINT stock_card_entry_key_values_pkey PRIMARY KEY (stockcardentryid, keycolumn);


--
-- Name: stock_card_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT stock_card_line_items_pkey PRIMARY KEY (id);


--
-- Name: stock_cards_facility_product_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_cards
    ADD CONSTRAINT stock_cards_facility_product_key UNIQUE (facilityid, productid);


--
-- Name: stock_cards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_cards
    ADD CONSTRAINT stock_cards_pkey PRIMARY KEY (id);


--
-- Name: stock_movement_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_movement_line_items
    ADD CONSTRAINT stock_movement_line_items_pkey PRIMARY KEY (id);


--
-- Name: stock_movement_lots_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_movement_lots
    ADD CONSTRAINT stock_movement_lots_pkey PRIMARY KEY (id);


--
-- Name: stock_movements_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY stock_movements
    ADD CONSTRAINT stock_movements_pkey PRIMARY KEY (id);


--
-- Name: supervisory_nodes_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY supervisory_nodes
    ADD CONSTRAINT supervisory_nodes_code_key UNIQUE (code);


--
-- Name: supervisory_nodes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY supervisory_nodes
    ADD CONSTRAINT supervisory_nodes_pkey PRIMARY KEY (id);


--
-- Name: supply_lines_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_pkey PRIMARY KEY (id);


--
-- Name: template_parameters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT template_parameters_pkey PRIMARY KEY (id);


--
-- Name: uc_productgroupid_facilityvisitid_epi_use_line_items; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT uc_productgroupid_facilityvisitid_epi_use_line_items UNIQUE (productgroupid, facilityvisitid);


--
-- Name: uc_programproductid_facilityvisitid_epi_inventory_line_items; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY epi_inventory_line_items
    ADD CONSTRAINT uc_programproductid_facilityvisitid_epi_inventory_line_items UNIQUE (programproductid, facilityvisitid);


--
-- Name: uc_refrigeratorid_facilityvisitid_refrigerator_readings; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY refrigerator_readings
    ADD CONSTRAINT uc_refrigeratorid_facilityvisitid_refrigerator_readings UNIQUE (refrigeratorid, facilityvisitid);


--
-- Name: uc_serialnumber_facilityid_refrigerators; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT uc_serialnumber_facilityid_refrigerators UNIQUE (serialnumber, facilityid);


--
-- Name: uc_vaccination_coverage_vaccination_products; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY coverage_target_group_products
    ADD CONSTRAINT uc_vaccination_coverage_vaccination_products UNIQUE (targetgroupentity);


--
-- Name: unique_fulfillment_role_assignments; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT unique_fulfillment_role_assignments UNIQUE (userid, roleid, facilityid);


--
-- Name: unique_role_assignment; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT unique_role_assignment UNIQUE (userid, roleid, programid, supervisorynodeid);


--
-- Name: unique_role_right; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY role_rights
    ADD CONSTRAINT unique_role_right UNIQUE (roleid, rightname);


--
-- Name: unique_supply_line; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT unique_supply_line UNIQUE (supervisorynodeid, programid);


--
-- Name: user_password_reset_tokens_userid_token_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_password_reset_tokens
    ADD CONSTRAINT user_password_reset_tokens_userid_token_key UNIQUE (userid, token);


--
-- Name: user_preference_master_key_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_preference_master
    ADD CONSTRAINT user_preference_master_key_key UNIQUE (key);


--
-- Name: user_preference_master_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_preference_master
    ADD CONSTRAINT user_preference_master_pkey PRIMARY KEY (id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: vacc_distributin_line_item_losts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_distribution_line_item_lots
    ADD CONSTRAINT vacc_distributin_line_item_losts_pkey PRIMARY KEY (id);


--
-- Name: vacc_distribution_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_distribution_line_items
    ADD CONSTRAINT vacc_distribution_line_items_pkey PRIMARY KEY (id);


--
-- Name: vacc_inventory_distribution_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_inventory_distribution_pkey PRIMARY KEY (id);


--
-- Name: vaccination_adult_coverage_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccination_adult_coverage_line_items
    ADD CONSTRAINT vaccination_adult_coverage_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccination_child_coverage_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccination_child_coverage_line_items
    ADD CONSTRAINT vaccination_child_coverage_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccination_full_coverages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY full_coverages
    ADD CONSTRAINT vaccination_full_coverages_pkey PRIMARY KEY (id);


--
-- Name: vaccine_discarding_reasons_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_discarding_reasons
    ADD CONSTRAINT vaccine_discarding_reasons_pkey PRIMARY KEY (id);


--
-- Name: vaccine_diseases_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_diseases
    ADD CONSTRAINT vaccine_diseases_name_key UNIQUE (name);


--
-- Name: vaccine_diseases_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_diseases
    ADD CONSTRAINT vaccine_diseases_pkey PRIMARY KEY (id);


--
-- Name: vaccine_doses_name_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_doses
    ADD CONSTRAINT vaccine_doses_name_key UNIQUE (name);


--
-- Name: vaccine_doses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_doses
    ADD CONSTRAINT vaccine_doses_pkey PRIMARY KEY (id);


--
-- Name: vaccine_inventory_config_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_inventory_product_configurations
    ADD CONSTRAINT vaccine_inventory_config_pkey PRIMARY KEY (id);


--
-- Name: vaccine_ivd_tab_visibilities_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_ivd_tab_visibilities
    ADD CONSTRAINT vaccine_ivd_tab_visibilities_pkey PRIMARY KEY (id);


--
-- Name: vaccine_ivd_tab_visibilities_tab_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_ivd_tab_visibilities
    ADD CONSTRAINT vaccine_ivd_tab_visibilities_tab_key UNIQUE (tab);


--
-- Name: vaccine_ivd_tabs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_ivd_tabs
    ADD CONSTRAINT vaccine_ivd_tabs_pkey PRIMARY KEY (tab);


--
-- Name: vaccine_logistics_master_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_logistics_master_columns
    ADD CONSTRAINT vaccine_logistics_master_columns_pkey PRIMARY KEY (id);


--
-- Name: vaccine_order_requisition_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_order_requisition_line_items
    ADD CONSTRAINT vaccine_order_requisition_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_order_requisition_master_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_order_requisition_master_columns
    ADD CONSTRAINT vaccine_order_requisition_master_columns_pkey PRIMARY KEY (id);


--
-- Name: vaccine_order_requisition_status_changes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_order_requisition_status_changes
    ADD CONSTRAINT vaccine_order_requisition_status_changes_pkey PRIMARY KEY (id);


--
-- Name: vaccine_order_requisitions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT vaccine_order_requisitions_pkey PRIMARY KEY (id);


--
-- Name: vaccine_product_doses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_pkey PRIMARY KEY (id);


--
-- Name: vaccine_program_logistics_columns_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_program_logistics_columns
    ADD CONSTRAINT vaccine_program_logistics_columns_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_adverse_effect_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_adverse_effect_line_items
    ADD CONSTRAINT vaccine_report_adverse_effect_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_campaign_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_campaign_line_items
    ADD CONSTRAINT vaccine_report_campaign_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_cold_chain_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_cold_chain_line_items
    ADD CONSTRAINT vaccine_report_cold_chain_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_coverage_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_disease_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_disease_line_items
    ADD CONSTRAINT vaccine_report_disease_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_logistics_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_status_changes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_status_changes
    ADD CONSTRAINT vaccine_report_status_changes_pkey PRIMARY KEY (id);


--
-- Name: vaccine_report_vitamin_supplementation_line_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_line_items_pkey PRIMARY KEY (id);


--
-- Name: vaccine_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_pkey PRIMARY KEY (id);


--
-- Name: vaccine_vitamin_supplementation_age_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_vitamin_supplementation_age_groups
    ADD CONSTRAINT vaccine_vitamin_supplementation_age_groups_pkey PRIMARY KEY (id);


--
-- Name: vaccine_vitamins_code_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_vitamins
    ADD CONSTRAINT vaccine_vitamins_code_key UNIQUE (code);


--
-- Name: vaccine_vitamins_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY vaccine_vitamins
    ADD CONSTRAINT vaccine_vitamins_pkey PRIMARY KEY (id);


--
-- Name: var_details_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY var_details
    ADD CONSTRAINT var_details_pkey PRIMARY KEY (id);


--
-- Name: var_item_alarms_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY var_item_alarms
    ADD CONSTRAINT var_item_alarms_pkey PRIMARY KEY (id);


--
-- Name: var_item_partials_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY var_item_partials
    ADD CONSTRAINT var_item_partials_pkey PRIMARY KEY (id);


--
-- Name: var_items_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY var_items
    ADD CONSTRAINT var_items_pkey PRIMARY KEY (id);


--
-- Name: dw_orders_index_facility; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_facility ON dw_orders USING btree (facilityid);


--
-- Name: dw_orders_index_period; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_period ON dw_orders USING btree (periodid);


--
-- Name: dw_orders_index_product; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_product ON dw_orders USING btree (productid);


--
-- Name: dw_orders_index_prog; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_prog ON dw_orders USING btree (programid);


--
-- Name: dw_orders_index_schedule; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_schedule ON dw_orders USING btree (scheduleid);


--
-- Name: dw_orders_index_status; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_status ON dw_orders USING btree (status);


--
-- Name: dw_orders_index_zone; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX dw_orders_index_zone ON dw_orders USING btree (geographiczoneid);


--
-- Name: i_comments_rnrid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_comments_rnrid ON comments USING btree (rnrid);


--
-- Name: i_delivery_zone_members_facilityid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_delivery_zone_members_facilityid ON delivery_zone_members USING btree (facilityid);


--
-- Name: i_delivery_zone_program_schedules_deliveryzoneid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_delivery_zone_program_schedules_deliveryzoneid ON delivery_zone_program_schedules USING btree (deliveryzoneid);


--
-- Name: i_delivery_zone_warehouses_deliveryzoneid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_delivery_zone_warehouses_deliveryzoneid ON delivery_zone_warehouses USING btree (deliveryzoneid);


--
-- Name: i_dw_orders_modifieddate; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_dw_orders_modifieddate ON dw_orders USING btree (modifieddate);


--
-- Name: i_dw_orders_stockedoutinpast; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_dw_orders_stockedoutinpast ON dw_orders USING btree (stockedoutinpast);


--
-- Name: i_email_attachment_emailid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_email_attachment_emailid ON email_attachments_relation USING btree (emailid);


--
-- Name: i_facility_approved_product_programproductid_facilitytypeid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_facility_approved_product_programproductid_facilitytypeid ON facility_approved_products USING btree (programproductid, facilitytypeid);


--
-- Name: i_facility_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_facility_name ON facilities USING btree (name);


--
-- Name: i_processing_period_startdate_enddate; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_processing_period_startdate_enddate ON processing_periods USING btree (startdate, enddate);


--
-- Name: i_program_product_price_history_programproductid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_program_product_price_history_programproductid ON program_product_price_history USING btree (programproductid);


--
-- Name: i_program_product_programid_productid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_program_product_programid_productid ON program_products USING btree (programid, productid);


--
-- Name: i_program_regimens_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX i_program_regimens_name ON program_regimen_columns USING btree (programid, name);


--
-- Name: i_program_supported_facilityid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_program_supported_facilityid ON programs_supported USING btree (facilityid);


--
-- Name: i_regimens_code_programid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX i_regimens_code_programid ON regimens USING btree (code, programid);


--
-- Name: i_reported_figures; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_reported_figures ON requisition_line_items USING btree ((((((((COALESCE(stockinhand, 0) + COALESCE(beginningbalance, 0)) + COALESCE(quantitydispensed, 0)) + COALESCE(quantityreceived, 0)) + COALESCE(stockoutdays, 0)) + abs(COALESCE(totallossesandadjustments, 0))) > 0)));


--
-- Name: INDEX i_reported_figures; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON INDEX i_reported_figures IS ' used to manage data in data warehouse';


--
-- Name: i_requisition_group_member_facilityid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_group_member_facilityid ON requisition_group_members USING btree (facilityid);


--
-- Name: i_requisition_group_program_schedules_requisitiongroupid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_group_program_schedules_requisitiongroupid ON requisition_group_program_schedules USING btree (requisitiongroupid);


--
-- Name: i_requisition_group_supervisorynodeid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_group_supervisorynodeid ON requisition_groups USING btree (supervisorynodeid);


--
-- Name: i_requisition_line_item_losses_adjustments_lineitemid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_line_item_losses_adjustments_lineitemid ON requisition_line_item_losses_adjustments USING btree (requisitionlineitemid);


--
-- Name: i_requisition_line_items_modifieddate; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_line_items_modifieddate ON requisition_line_items USING btree (modifieddate);


--
-- Name: INDEX i_requisition_line_items_modifieddate; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON INDEX i_requisition_line_items_modifieddate IS 'used to manage data in data warehouse';


--
-- Name: i_requisition_line_items_rnrid_fullsupply_f; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_line_items_rnrid_fullsupply_f ON requisition_line_items USING btree (rnrid) WHERE (fullsupply = false);


--
-- Name: i_requisition_line_items_rnrid_fullsupply_t; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_line_items_rnrid_fullsupply_t ON requisition_line_items USING btree (rnrid) WHERE (fullsupply = true);


--
-- Name: i_requisition_line_items_skipped_f; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisition_line_items_skipped_f ON requisition_line_items USING btree (rnrid) WHERE (skipped = false);


--
-- Name: INDEX i_requisition_line_items_skipped_f; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON INDEX i_requisition_line_items_skipped_f IS 'used to manage data in data warehouse';


--
-- Name: i_requisitions_programid_supervisorynodeid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisitions_programid_supervisorynodeid ON requisitions USING btree (programid, supervisorynodeid);


--
-- Name: i_requisitions_status; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_requisitions_status ON requisitions USING btree (lower((status)::text));


--
-- Name: i_supervisory_node_parentid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_supervisory_node_parentid ON supervisory_nodes USING btree (parentid);


--
-- Name: i_users_firstname_lastname_email; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX i_users_firstname_lastname_email ON users USING btree (lower((firstname)::text), lower((lastname)::text), lower((email)::text));


--
-- Name: mos_adjustment_basis_name_key; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX mos_adjustment_basis_name_key ON mos_adjustment_basis USING btree (name);


--
-- Name: mos_adjustment_types_name_key; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX mos_adjustment_types_name_key ON mos_adjustment_types USING btree (name);


--
-- Name: program_id_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX program_id_index ON program_rnr_columns USING btree (programid);


--
-- Name: requisition_status_changes_rnrid_and_status; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX requisition_status_changes_rnrid_and_status ON requisition_status_changes USING btree (rnrid, status);


--
-- Name: uc_budget_line_items_facilityid_programid_periodid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_budget_line_items_facilityid_programid_periodid ON budget_line_items USING btree (facilityid, programid, periodid);


--
-- Name: uc_delivery_zones_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_delivery_zones_lower_code ON delivery_zones USING btree (lower((code)::text));


--
-- Name: uc_dosage_units_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_dosage_units_lower_code ON dosage_units USING btree (lower((code)::text));


--
-- Name: uc_dz_program_period; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_dz_program_period ON distributions USING btree (deliveryzoneid, programid, periodid);


--
-- Name: uc_facilities_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_facilities_lower_code ON facilities USING btree (lower((code)::text));


--
-- Name: uc_facility_mappedid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_facility_mappedid ON facility_mappings USING btree (mappedid);


--
-- Name: INDEX uc_facility_mappedid; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON INDEX uc_facility_mappedid IS 'Unique code required for mapped id';


--
-- Name: uc_facility_operators_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_facility_operators_lower_code ON facility_operators USING btree (lower((code)::text));


--
-- Name: uc_facility_types_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_facility_types_lower_code ON facility_types USING btree (lower((code)::text));


--
-- Name: uc_geographic_levels_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_geographic_levels_lower_code ON geographic_levels USING btree (lower((code)::text));


--
-- Name: uc_geographic_zones_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_geographic_zones_lower_code ON geographic_zones USING btree (lower((code)::text));


--
-- Name: uc_mos_adjustment_facilities_facility; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_mos_adjustment_facilities_facility ON mos_adjustment_facilities USING btree (typeid, facilityid);


--
-- Name: uc_mos_adjustment_products_product; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_mos_adjustment_products_product ON mos_adjustment_products USING btree (typeid, basisid, productid);


--
-- Name: uc_order_quantity_adjustment_factors_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_order_quantity_adjustment_factors_name ON order_quantity_adjustment_factors USING btree (name);


--
-- Name: uc_order_quantity_adjustment_products_product; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_order_quantity_adjustment_products_product ON order_quantity_adjustment_products USING btree (facilityid, productid, typeid, factorid);


--
-- Name: INDEX uc_order_quantity_adjustment_products_product; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON INDEX uc_order_quantity_adjustment_products_product IS 'One adjustment rule per facility per product';


--
-- Name: uc_order_quantity_adjustment_types_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_order_quantity_adjustment_types_name ON order_quantity_adjustment_types USING btree (name);


--
-- Name: uc_processing_period_name_scheduleid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_processing_period_name_scheduleid ON processing_periods USING btree (lower((name)::text), scheduleid, date_part('year'::text, startdate));


--
-- Name: uc_processing_schedules_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_processing_schedules_lower_code ON processing_schedules USING btree (lower((code)::text));


--
-- Name: uc_product_categories_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_product_categories_lower_code ON product_categories USING btree (lower((code)::text));


--
-- Name: uc_product_forms_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_product_forms_lower_code ON product_forms USING btree (lower((code)::text));


--
-- Name: uc_product_groups_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_product_groups_lower_code ON product_groups USING btree (lower((code)::text));


--
-- Name: uc_products_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_products_lower_code ON products USING btree (lower((code)::text));


--
-- Name: uc_programs_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_programs_lower_code ON programs USING btree (lower((code)::text));


--
-- Name: uc_report_templates_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_report_templates_name ON templates USING btree (lower((name)::text));


--
-- Name: uc_requisition_groups_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_requisition_groups_lower_code ON requisition_groups USING btree (lower((code)::text));


--
-- Name: uc_roles_lower_name; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_roles_lower_name ON roles USING btree (lower((name)::text));


--
-- Name: uc_supervisory_nodes_lower_code; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_supervisory_nodes_lower_code ON supervisory_nodes USING btree (lower((code)::text));


--
-- Name: uc_users_email; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_users_email ON users USING btree (lower((email)::text));


--
-- Name: uc_users_employeeid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_users_employeeid ON users USING btree (lower((employeeid)::text));


--
-- Name: uc_users_username; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX uc_users_username ON users USING btree (lower((username)::text));


--
-- Name: unique_donor_code_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_donor_code_index ON donors USING btree (code);


--
-- Name: unique_equipment_type_code_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_equipment_type_code_index ON equipment_types USING btree (code);


--
-- Name: unique_index_district_demographic_estimates; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_index_district_demographic_estimates ON district_demographic_estimates USING btree (districtid, year, programid, demographicestimateid);


--
-- Name: unique_index_facility_demographic_estimates; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_index_facility_demographic_estimates ON facility_demographic_estimates USING btree (facilityid, year, programid, demographicestimateid);


--
-- Name: unique_index_regular_rnr; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_index_regular_rnr ON requisitions USING btree (facilityid, programid, periodid) WHERE (emergency = false);


--
-- Name: unique_program_equipment_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_program_equipment_index ON equipment_type_programs USING btree (programid, equipmenttypeid);


--
-- Name: unique_program_equipment_product_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX unique_program_equipment_product_index ON equipment_type_products USING btree (programequipmenttypeid, productid);


--
-- Name: adult_coverage_opened_vial_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY adult_coverage_opened_vial_line_items
    ADD CONSTRAINT adult_coverage_opened_vial_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: budget_line_items_budgetfileid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_budgetfileid_fkey FOREIGN KEY (budgetfileid) REFERENCES budget_file_info(id);


--
-- Name: budget_line_items_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: budget_line_items_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: budget_line_items_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: combo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_combination_constituents
    ADD CONSTRAINT combo_id_fkey FOREIGN KEY (productcomboid) REFERENCES regimen_product_combinations(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: comments_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id);


--
-- Name: comments_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: comments_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: coverage_product_vials_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY coverage_product_vials
    ADD CONSTRAINT coverage_product_vials_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code);


--
-- Name: coverage_vaccination_products_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY coverage_target_group_products
    ADD CONSTRAINT coverage_vaccination_products_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code);


--
-- Name: defalut_regimen_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_combination_constituents
    ADD CONSTRAINT defalut_regimen_product_id_fkey FOREIGN KEY (defaultdosageid) REFERENCES regimen_constituents_dosages(id);


--
-- Name: delivery_zone_members_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_members
    ADD CONSTRAINT delivery_zone_members_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id);


--
-- Name: delivery_zone_members_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_members
    ADD CONSTRAINT delivery_zone_members_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: delivery_zone_program_schedules_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id);


--
-- Name: delivery_zone_program_schedules_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: delivery_zone_program_schedules_scheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_scheduleid_fkey FOREIGN KEY (scheduleid) REFERENCES processing_schedules(id);


--
-- Name: delivery_zone_warehouses_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_warehouses
    ADD CONSTRAINT delivery_zone_warehouses_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id);


--
-- Name: delivery_zone_warehouses_warehouseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_warehouses
    ADD CONSTRAINT delivery_zone_warehouses_warehouseid_fkey FOREIGN KEY (warehouseid) REFERENCES facilities(id);


--
-- Name: distributions_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id);


--
-- Name: distributions_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id);


--
-- Name: distributions_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: distributions_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: distributions_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: district_demographic_estimates_demographicestimateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_demographicestimateid_fkey FOREIGN KEY (demographicestimateid) REFERENCES demographic_estimate_categories(id);


--
-- Name: district_demographic_estimates_districtid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_districtid_fkey FOREIGN KEY (districtid) REFERENCES geographic_zones(id);


--
-- Name: district_demographic_estimates_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: dosage_frequency_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT dosage_frequency_id_fkey FOREIGN KEY (dosagefrequencyid) REFERENCES dosage_frequencies(id);


--
-- Name: dosage_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT dosage_unit_id_fkey FOREIGN KEY (dosageunitid) REFERENCES dosage_units(id);


--
-- Name: elmis_help_helptopicid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help
    ADD CONSTRAINT elmis_help_helptopicid_fkey FOREIGN KEY (helptopicid) REFERENCES elmis_help_topic(id);


--
-- Name: elmis_help_topic_parent_help_topic_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT elmis_help_topic_parent_help_topic_id_fkey FOREIGN KEY (parent_help_topic_id) REFERENCES elmis_help_topic(id);


--
-- Name: elmis_help_topic_roles_help_topic_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic_roles
    ADD CONSTRAINT elmis_help_topic_roles_help_topic_id_fkey FOREIGN KEY (help_topic_id) REFERENCES elmis_help_topic(id);


--
-- Name: elmis_help_topic_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic_roles
    ADD CONSTRAINT elmis_help_topic_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES roles(id);


--
-- Name: email_attachments_relation_attachmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY email_attachments_relation
    ADD CONSTRAINT email_attachments_relation_attachmentid_fkey FOREIGN KEY (attachmentid) REFERENCES email_attachments(id);


--
-- Name: email_attachments_relation_emailid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY email_attachments_relation
    ADD CONSTRAINT email_attachments_relation_emailid_fkey FOREIGN KEY (emailid) REFERENCES email_notifications(id);


--
-- Name: epi_inventory_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_inventory_line_items
    ADD CONSTRAINT epi_inventory_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: epi_inventory_line_items_programproductid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_inventory_line_items
    ADD CONSTRAINT epi_inventory_line_items_programproductid_fkey FOREIGN KEY (programproductid) REFERENCES program_products(id);


--
-- Name: epi_use_line_items_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id);


--
-- Name: epi_use_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: epi_use_line_items_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: epi_use_line_items_productgroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_productgroupid_fkey FOREIGN KEY (productgroupid) REFERENCES product_groups(id);


--
-- Name: equipment_cce_designation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_designation_fkey FOREIGN KEY (designationid) REFERENCES equipment_cold_chain_equipment_designations(id);


--
-- Name: equipment_cce_donor_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_donor_fkey FOREIGN KEY (donorid) REFERENCES donors(id);


--
-- Name: equipment_cce_equipment_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_equipment_fkey FOREIGN KEY (equipmentid) REFERENCES equipments(id);


--
-- Name: equipment_cce_psq_status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_psq_status_fkey FOREIGN KEY (pqsstatusid) REFERENCES equipment_cold_chain_equipment_pqs_status(id);


--
-- Name: equipment_contract_service_types_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_contract_service_types
    ADD CONSTRAINT equipment_contract_service_types_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id);


--
-- Name: equipment_contract_service_types_servicetypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_contract_service_types
    ADD CONSTRAINT equipment_contract_service_types_servicetypeid_fkey FOREIGN KEY (servicetypeid) REFERENCES equipment_service_types(id);


--
-- Name: equipment_energy_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipments
    ADD CONSTRAINT equipment_energy_type_fkey FOREIGN KEY (energytypeid) REFERENCES equipment_energy_types(id);


--
-- Name: equipment_maintenance_logs_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id);


--
-- Name: equipment_maintenance_logs_equipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_equipmentid_fkey FOREIGN KEY (equipmentid) REFERENCES equipments(id);


--
-- Name: equipment_maintenance_logs_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: equipment_maintenance_logs_requestid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_requestid_fkey FOREIGN KEY (requestid) REFERENCES equipment_maintenance_requests(id);


--
-- Name: equipment_maintenance_logs_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: equipment_maintenance_logs_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id);


--
-- Name: equipment_maintenance_requests_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: equipment_maintenance_requests_inventoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_inventoryid_fkey FOREIGN KEY (inventoryid) REFERENCES equipment_inventories(id);


--
-- Name: equipment_maintenance_requests_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: equipment_maintenance_requests_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id);


--
-- Name: equipment_service_contract_equipments_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_equipment_types
    ADD CONSTRAINT equipment_service_contract_equipments_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id);


--
-- Name: equipment_service_contract_equipments_equipmenttypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_equipment_types
    ADD CONSTRAINT equipment_service_contract_equipments_equipmenttypeid_fkey FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types(id);


--
-- Name: equipment_service_contract_facilities_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_facilities
    ADD CONSTRAINT equipment_service_contract_facilities_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id);


--
-- Name: equipment_service_contract_facilities_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_facilities
    ADD CONSTRAINT equipment_service_contract_facilities_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: equipment_service_contracts_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contracts
    ADD CONSTRAINT equipment_service_contracts_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id);


--
-- Name: equipment_service_vendor_users_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_vendor_users
    ADD CONSTRAINT equipment_service_vendor_users_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: equipment_service_vendor_users_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_vendor_users
    ADD CONSTRAINT equipment_service_vendor_users_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id);


--
-- Name: equipment_status_line_items_equipmentinventoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_equipmentinventoryid_fkey FOREIGN KEY (equipmentinventoryid) REFERENCES equipment_inventories(id);


--
-- Name: equipment_status_line_items_inventorystatusid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_inventorystatusid_fkey FOREIGN KEY (inventorystatusid) REFERENCES equipment_inventory_statuses(id);


--
-- Name: equipment_status_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: equipments_equipmenttypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipments
    ADD CONSTRAINT equipments_equipmenttypeid_fkey FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types(id);


--
-- Name: facilities_geographiczoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_geographiczoneid_fkey FOREIGN KEY (geographiczoneid) REFERENCES geographic_zones(id);


--
-- Name: facilities_operatedbyid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_operatedbyid_fkey FOREIGN KEY (operatedbyid) REFERENCES facility_operators(id);


--
-- Name: facilities_parentfacilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_parentfacilityid_fkey FOREIGN KEY (parentfacilityid) REFERENCES facilities(id);


--
-- Name: facilities_pricescheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_pricescheduleid_fkey FOREIGN KEY (pricescheduleid) REFERENCES price_schedules(id);


--
-- Name: facilities_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_typeid_fkey FOREIGN KEY (typeid) REFERENCES facility_types(id);


--
-- Name: facility_approved_products_facilitytypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_approved_products
    ADD CONSTRAINT facility_approved_products_facilitytypeid_fkey FOREIGN KEY (facilitytypeid) REFERENCES facility_types(id);


--
-- Name: facility_approved_products_programproductid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_approved_products
    ADD CONSTRAINT facility_approved_products_programproductid_fkey FOREIGN KEY (programproductid) REFERENCES program_products(id);


--
-- Name: facility_demographic_estimates_demographicestimateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_demographicestimateid_fkey FOREIGN KEY (demographicestimateid) REFERENCES demographic_estimate_categories(id);


--
-- Name: facility_demographic_estimates_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_demographic_estimates_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_cards
    ADD CONSTRAINT facility_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT facility_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_ftp_details_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_ftp_details
    ADD CONSTRAINT facility_ftp_details_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_mappings_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_mappings
    ADD CONSTRAINT facility_mappings_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_mappings_interfaceid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_mappings
    ADD CONSTRAINT facility_mappings_interfaceid_fkey FOREIGN KEY (interfaceid) REFERENCES interface_apps(id);


--
-- Name: facility_program_equipments_equipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_equipmentid_fkey FOREIGN KEY (equipmentid) REFERENCES equipments(id);


--
-- Name: facility_program_equipments_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_program_equipments_primarydonorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_primarydonorid_fkey FOREIGN KEY (primarydonorid) REFERENCES donors(id);


--
-- Name: facility_program_equipments_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: facility_program_products_facilityid_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_facilityid_fkey1 FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: facility_program_products_isacoefficientsid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_isacoefficientsid_fkey FOREIGN KEY (isacoefficientsid) REFERENCES isa_coefficients(id);


--
-- Name: facility_program_products_programproductid_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_programproductid_fkey1 FOREIGN KEY (programproductid) REFERENCES program_products(id);


--
-- Name: facility_visits_distributionid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_visits
    ADD CONSTRAINT facility_visits_distributionid_fkey FOREIGN KEY (distributionid) REFERENCES distributions(id);


--
-- Name: facility_visits_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_visits
    ADD CONSTRAINT facility_visits_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: fk_foreign_users_modifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT fk_foreign_users_modifier FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: fk_foreing_users_creator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT fk_foreing_users_creator FOREIGN KEY (created_by) REFERENCES users(id);


--
-- Name: fk_user_help_modifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help
    ADD CONSTRAINT fk_user_help_modifier FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: from_facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movements
    ADD CONSTRAINT from_facility_fkey FOREIGN KEY (fromfacilityid) REFERENCES facilities(id);


--
-- Name: fulfillment_role_assignments_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT fulfillment_role_assignments_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: fulfillment_role_assignments_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT fulfillment_role_assignments_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id);


--
-- Name: fulfillment_role_assignments_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT fulfillment_role_assignments_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: full_coverages_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY full_coverages
    ADD CONSTRAINT full_coverages_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: geographic_zones_levelid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_zones
    ADD CONSTRAINT geographic_zones_levelid_fkey FOREIGN KEY (levelid) REFERENCES geographic_levels(id);


--
-- Name: geographic_zones_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_zones
    ADD CONSTRAINT geographic_zones_parentid_fkey FOREIGN KEY (parentid) REFERENCES geographic_zones(id);


--
-- Name: interface_dataset_interfaceid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interface_dataset
    ADD CONSTRAINT interface_dataset_interfaceid_fkey FOREIGN KEY (interfaceid) REFERENCES interface_apps(id);


--
-- Name: inventory_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT inventory_fkey FOREIGN KEY (inventoryid) REFERENCES equipment_inventories(id);


--
-- Name: isa_coefficients_populationsource_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY isa_coefficients
    ADD CONSTRAINT isa_coefficients_populationsource_fkey FOREIGN KEY (populationsource) REFERENCES demographic_estimate_categories(id);


--
-- Name: losses_adjustments_types_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT losses_adjustments_types_fkey FOREIGN KEY (adjustmenttype) REFERENCES losses_adjustments_types(name);


--
-- Name: lot_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots_on_hand
    ADD CONSTRAINT lot_fkey FOREIGN KEY (lotid) REFERENCES lots(id);


--
-- Name: lot_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_items
    ADD CONSTRAINT lot_fkey FOREIGN KEY (lotid) REFERENCES lots(id);


--
-- Name: lot_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_lots
    ADD CONSTRAINT lot_fkey FOREIGN KEY (lotid) REFERENCES lots(id);


--
-- Name: lot_on_hand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT lot_on_hand_fkey FOREIGN KEY (lotonhandid) REFERENCES lots_on_hand(id);


--
-- Name: master_rnr_column_options_masterrnrcolumnid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_rnr_column_options
    ADD CONSTRAINT master_rnr_column_options_masterrnrcolumnid_fkey FOREIGN KEY (masterrnrcolumnid) REFERENCES master_rnr_columns(id);


--
-- Name: master_rnr_column_options_rnroptionid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_rnr_column_options
    ADD CONSTRAINT master_rnr_column_options_rnroptionid_fkey FOREIGN KEY (rnroptionid) REFERENCES configurable_rnr_options(id);


--
-- Name: mos_adjustment_facilities_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_facilities
    ADD CONSTRAINT mos_adjustment_facilities_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: mos_adjustment_facilities_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_facilities
    ADD CONSTRAINT mos_adjustment_facilities_typeid_fkey FOREIGN KEY (typeid) REFERENCES mos_adjustment_products(id);


--
-- Name: mos_adjustment_products_basisid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_basisid_fkey FOREIGN KEY (basisid) REFERENCES mos_adjustment_basis(id);


--
-- Name: mos_adjustment_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: mos_adjustment_products_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_typeid_fkey FOREIGN KEY (typeid) REFERENCES mos_adjustment_types(id);


--
-- Name: not_functional_reason_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT not_functional_reason_fkey FOREIGN KEY (notfunctionalstatusid) REFERENCES equipment_operational_status(id);


--
-- Name: opened_vial_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY child_coverage_opened_vial_line_items
    ADD CONSTRAINT opened_vial_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: order_quantity_adjustment_products_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: order_quantity_adjustment_products_factorsid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_factorsid_fkey FOREIGN KEY (factorid) REFERENCES order_quantity_adjustment_factors(id);


--
-- Name: order_quantity_adjustment_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: order_quantity_adjustment_products_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_typeid_fkey FOREIGN KEY (typeid) REFERENCES order_quantity_adjustment_types(id);


--
-- Name: orders_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id);


--
-- Name: orders_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_id_fkey FOREIGN KEY (id) REFERENCES requisitions(id);


--
-- Name: orders_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: orders_shipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_shipmentid_fkey FOREIGN KEY (shipmentid) REFERENCES shipment_file_info(id);


--
-- Name: orders_supplylineid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_supplylineid_fkey FOREIGN KEY (supplylineid) REFERENCES supply_lines(id);


--
-- Name: patient_quantification_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY patient_quantification_line_items
    ADD CONSTRAINT patient_quantification_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: period_short_names_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY period_short_names
    ADD CONSTRAINT period_short_names_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: pod_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: pod_line_items_podid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod_line_items
    ADD CONSTRAINT pod_line_items_podid_fkey FOREIGN KEY (podid) REFERENCES pod(id);


--
-- Name: pod_line_items_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod_line_items
    ADD CONSTRAINT pod_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code);


--
-- Name: pod_orderid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_orderid_fkey FOREIGN KEY (orderid) REFERENCES orders(id);


--
-- Name: pod_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: pod_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: processing_periods_scheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY processing_periods
    ADD CONSTRAINT processing_periods_scheduleid_fkey FOREIGN KEY (scheduleid) REFERENCES processing_schedules(id);


--
-- Name: product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_cards
    ADD CONSTRAINT product_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots
    ADD CONSTRAINT product_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: product_price_schedules_pricescheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_price_schedules
    ADD CONSTRAINT product_price_schedules_pricescheduleid_fkey FOREIGN KEY (pricescheduleid) REFERENCES price_schedules(id);


--
-- Name: product_price_schedules_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_price_schedules
    ADD CONSTRAINT product_price_schedules_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: product_short_names_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_short_names
    ADD CONSTRAINT product_short_names_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: products_dosageunitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_dosageunitid_fkey FOREIGN KEY (dosageunitid) REFERENCES dosage_units(id);


--
-- Name: products_formid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_formid_fkey FOREIGN KEY (formid) REFERENCES product_forms(id);


--
-- Name: products_productgroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_productgroupid_fkey FOREIGN KEY (productgroupid) REFERENCES product_groups(id);


--
-- Name: program_equipment_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_products
    ADD CONSTRAINT program_equipment_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: program_equipment_products_programequipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_products
    ADD CONSTRAINT program_equipment_products_programequipmentid_fkey FOREIGN KEY (programequipmenttypeid) REFERENCES equipment_type_programs(id);


--
-- Name: program_equipments_equipmenttypeid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_programs
    ADD CONSTRAINT program_equipments_equipmenttypeid FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types(id);


--
-- Name: program_equipments_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_programs
    ADD CONSTRAINT program_equipments_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: program_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_adjustment_reasons_programs
    ADD CONSTRAINT program_fkey FOREIGN KEY (programcode) REFERENCES programs(code);


--
-- Name: program_product_price_history_programproductid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_product_price_history
    ADD CONSTRAINT program_product_price_history_programproductid_fkey FOREIGN KEY (programproductid) REFERENCES program_products(id);


--
-- Name: program_products_isacoefficientsid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_isacoefficientsid_fkey FOREIGN KEY (isacoefficientsid) REFERENCES isa_coefficients(id);


--
-- Name: program_products_productcategoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_productcategoryid_fkey FOREIGN KEY (productcategoryid) REFERENCES product_categories(id);


--
-- Name: program_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: program_products_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: program_regimen_columns_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_regimen_columns
    ADD CONSTRAINT program_regimen_columns_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: program_rnr_columns_mastercolumnid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_rnr_columns
    ADD CONSTRAINT program_rnr_columns_mastercolumnid_fkey FOREIGN KEY (mastercolumnid) REFERENCES master_rnr_columns(id);


--
-- Name: program_rnr_columns_rnroptionid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_rnr_columns
    ADD CONSTRAINT program_rnr_columns_rnroptionid_fkey FOREIGN KEY (rnroptionid) REFERENCES configurable_rnr_options(id);


--
-- Name: programs_supported_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY programs_supported
    ADD CONSTRAINT programs_supported_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: programs_supported_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY programs_supported
    ADD CONSTRAINT programs_supported_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: refrigerator_problems_readingid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_problems
    ADD CONSTRAINT refrigerator_problems_readingid_fkey FOREIGN KEY (readingid) REFERENCES refrigerator_readings(id);


--
-- Name: refrigerator_readings_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_readings
    ADD CONSTRAINT refrigerator_readings_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: refrigerator_readings_refrigeratorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_readings
    ADD CONSTRAINT refrigerator_readings_refrigeratorid_fkey FOREIGN KEY (refrigeratorid) REFERENCES refrigerators(id);


--
-- Name: refrigerators_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id);


--
-- Name: refrigerators_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: refrigerators_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: regimen_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_product_combinations
    ADD CONSTRAINT regimen_id_fkey FOREIGN KEY (regimenid) REFERENCES regimens(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: regimen_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_line_items
    ADD CONSTRAINT regimen_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: regimens_categoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimens
    ADD CONSTRAINT regimens_categoryid_fkey FOREIGN KEY (categoryid) REFERENCES regimen_categories(id);


--
-- Name: regimens_product_dosage_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT regimens_product_dosage_fkey FOREIGN KEY (regimenproductid) REFERENCES regimen_combination_constituents(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: regimens_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimens
    ADD CONSTRAINT regimens_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: report_rights_rightname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY report_rights
    ADD CONSTRAINT report_rights_rightname_fkey FOREIGN KEY (rightname) REFERENCES rights(name);


--
-- Name: report_rights_templateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY report_rights
    ADD CONSTRAINT report_rights_templateid_fkey FOREIGN KEY (templateid) REFERENCES templates(id);


--
-- Name: requisition_group_members_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_members
    ADD CONSTRAINT requisition_group_members_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: requisition_group_members_requisitiongroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_members
    ADD CONSTRAINT requisition_group_members_requisitiongroupid_fkey FOREIGN KEY (requisitiongroupid) REFERENCES requisition_groups(id);


--
-- Name: requisition_group_program_schedules_dropofffacilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_dropofffacilityid_fkey FOREIGN KEY (dropofffacilityid) REFERENCES facilities(id);


--
-- Name: requisition_group_program_schedules_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: requisition_group_program_schedules_requisitiongroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_requisitiongroupid_fkey FOREIGN KEY (requisitiongroupid) REFERENCES requisition_groups(id);


--
-- Name: requisition_group_program_schedules_scheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_scheduleid_fkey FOREIGN KEY (scheduleid) REFERENCES processing_schedules(id);


--
-- Name: requisition_groups_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_groups
    ADD CONSTRAINT requisition_groups_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id);


--
-- Name: requisition_line_item_losses_adjustm_requisitionlineitemid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_item_losses_adjustments
    ADD CONSTRAINT requisition_line_item_losses_adjustm_requisitionlineitemid_fkey FOREIGN KEY (requisitionlineitemid) REFERENCES requisition_line_items(id);


--
-- Name: requisition_line_item_losses_adjustments_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_item_losses_adjustments
    ADD CONSTRAINT requisition_line_item_losses_adjustments_type_fkey FOREIGN KEY (type) REFERENCES losses_adjustments_types(name);


--
-- Name: requisition_line_items_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_items
    ADD CONSTRAINT requisition_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code);


--
-- Name: requisition_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_items
    ADD CONSTRAINT requisition_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: requisition_signatures_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_signatures
    ADD CONSTRAINT requisition_signatures_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: requisition_signatures_signatureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_signatures
    ADD CONSTRAINT requisition_signatures_signatureid_fkey FOREIGN KEY (signatureid) REFERENCES signatures(id);


--
-- Name: requisition_status_changes_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id);


--
-- Name: requisition_status_changes_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id);


--
-- Name: requisition_status_changes_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id);


--
-- Name: requisitions_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: requisitions_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: requisitions_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: requisitions_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id);


--
-- Name: role_assignments_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id);


--
-- Name: role_assignments_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: role_assignments_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id);


--
-- Name: role_assignments_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id);


--
-- Name: role_assignments_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: role_rights_rightname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_rights
    ADD CONSTRAINT role_rights_rightname_fkey FOREIGN KEY (rightname) REFERENCES rights(name);


--
-- Name: role_rights_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_rights
    ADD CONSTRAINT role_rights_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id);


--
-- Name: shipment_line_items_orderid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_line_items
    ADD CONSTRAINT shipment_line_items_orderid_fkey FOREIGN KEY (orderid) REFERENCES orders(id);


--
-- Name: shipment_line_items_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_line_items
    ADD CONSTRAINT shipment_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code);


--
-- Name: status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT status_fkey FOREIGN KEY (statusid) REFERENCES equipment_operational_status(id);


--
-- Name: stock_adjustment_reason_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_adjustment_reasons_programs
    ADD CONSTRAINT stock_adjustment_reason_fkey FOREIGN KEY (reasonname) REFERENCES losses_adjustments_types(name);


--
-- Name: stock_card_entry_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entry_key_values
    ADD CONSTRAINT stock_card_entry_fkey FOREIGN KEY (stockcardentryid) REFERENCES stock_card_entries(id);


--
-- Name: stock_card_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots_on_hand
    ADD CONSTRAINT stock_card_fkey FOREIGN KEY (stockcardid) REFERENCES stock_cards(id);


--
-- Name: stock_card_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT stock_card_fkey FOREIGN KEY (stockcardid) REFERENCES stock_cards(id);


--
-- Name: stock_movement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT stock_movement_fkey FOREIGN KEY (stockmovementid) REFERENCES stock_movements(id);


--
-- Name: stock_movement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_items
    ADD CONSTRAINT stock_movement_fkey FOREIGN KEY (stockmovementid) REFERENCES stock_movements(id);


--
-- Name: stock_movement_line_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_lots
    ADD CONSTRAINT stock_movement_line_item_fkey FOREIGN KEY (stockmovementlineitemid) REFERENCES stock_movement_line_items(id);


--
-- Name: stock_movement_line_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_item_extra_fields
    ADD CONSTRAINT stock_movement_line_item_fkey FOREIGN KEY (stockmovementlineitemid) REFERENCES stock_movement_line_items(id);


--
-- Name: supervisory_nodes_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supervisory_nodes
    ADD CONSTRAINT supervisory_nodes_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: supervisory_nodes_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supervisory_nodes
    ADD CONSTRAINT supervisory_nodes_parentid_fkey FOREIGN KEY (parentid) REFERENCES supervisory_nodes(id);


--
-- Name: supply_lines_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_parentid_fkey FOREIGN KEY (parentid) REFERENCES supply_lines(id);


--
-- Name: supply_lines_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: supply_lines_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id);


--
-- Name: supply_lines_supplyingfacilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_supplyingfacilityid_fkey FOREIGN KEY (supplyingfacilityid) REFERENCES facilities(id);


--
-- Name: template_parameters_templateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT template_parameters_templateid_fkey FOREIGN KEY (templateid) REFERENCES templates(id);


--
-- Name: to_facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movements
    ADD CONSTRAINT to_facility_fkey FOREIGN KEY (tofacilityid) REFERENCES facilities(id);


--
-- Name: user_password_reset_tokens_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_password_reset_tokens
    ADD CONSTRAINT user_password_reset_tokens_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: user_preference_roles_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preference_roles
    ADD CONSTRAINT user_preference_roles_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id);


--
-- Name: user_preference_roles_userpreferencekey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preference_roles
    ADD CONSTRAINT user_preference_roles_userpreferencekey_fkey FOREIGN KEY (userpreferencekey) REFERENCES user_preference_master(key);


--
-- Name: user_preferences_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preferences
    ADD CONSTRAINT user_preferences_userid_fkey FOREIGN KEY (userid) REFERENCES users(id);


--
-- Name: user_preferences_userpreferencekey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preferences
    ADD CONSTRAINT user_preferences_userpreferencekey_fkey FOREIGN KEY (userpreferencekey) REFERENCES user_preference_master(key);


--
-- Name: users_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: users_supervisorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_supervisorid_fkey FOREIGN KEY (supervisorid) REFERENCES users(id);


--
-- Name: vacc_distribution_line_items_distribution_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_items
    ADD CONSTRAINT vacc_distribution_line_items_distribution_fkey FOREIGN KEY (distributionid) REFERENCES vaccine_distributions(id);


--
-- Name: vacc_distribution_lotid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_item_lots
    ADD CONSTRAINT vacc_distribution_lotid_fkey FOREIGN KEY (lotid) REFERENCES lots(id);


--
-- Name: vacc_distribution_lots_line_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_item_lots
    ADD CONSTRAINT vacc_distribution_lots_line_item_fkey FOREIGN KEY (distributionlineitemid) REFERENCES vaccine_distribution_line_items(id);


--
-- Name: vacc_distributions_frofacility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_distributions_frofacility_fkey FOREIGN KEY (fromfacilityid) REFERENCES facilities(id);


--
-- Name: vacc_distributions_line_items_product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_items
    ADD CONSTRAINT vacc_distributions_line_items_product_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vacc_distributions_period_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_distributions_period_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: vacc_distributions_tofacility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_distributions_tofacility_fkey FOREIGN KEY (tofacilityid) REFERENCES facilities(id);


--
-- Name: vaccination_adult_coverage_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccination_adult_coverage_line_items
    ADD CONSTRAINT vaccination_adult_coverage_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: vaccination_child_coverage_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccination_child_coverage_line_items
    ADD CONSTRAINT vaccination_child_coverage_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id);


--
-- Name: vaccine_adjustment_reasons_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_lots_on_hand_adjustments
    ADD CONSTRAINT vaccine_adjustment_reasons_fkey FOREIGN KEY (adjustmentreason) REFERENCES losses_adjustments_types(name);


--
-- Name: vaccine_inventory_config_product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_inventory_product_configurations
    ADD CONSTRAINT vaccine_inventory_config_product_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vaccine_inventory_product_configurations_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_inventory_product_configurations
    ADD CONSTRAINT vaccine_inventory_product_configurations_fkey FOREIGN KEY (denominatorestimatecategoryid) REFERENCES demographic_estimate_categories(id);


--
-- Name: vaccine_ivd_tab_visibilities_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_ivd_tab_visibilities
    ADD CONSTRAINT vaccine_ivd_tab_visibilities_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: vaccine_order_requisition_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisition_line_items
    ADD CONSTRAINT vaccine_order_requisition_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vaccine_order_requisitions_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT vaccine_order_requisitions_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: vaccine_order_requisitions_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT vaccine_order_requisitions_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: vaccine_product_doses_denominatorestimatecategoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_denominatorestimatecategoryid_fkey FOREIGN KEY (denominatorestimatecategoryid) REFERENCES demographic_estimate_categories(id);


--
-- Name: vaccine_product_doses_doseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_doseid_fkey FOREIGN KEY (doseid) REFERENCES vaccine_doses(id);


--
-- Name: vaccine_product_doses_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vaccine_product_doses_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: vaccine_program_logistics_columns_mastercolumnid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_program_logistics_columns
    ADD CONSTRAINT vaccine_program_logistics_columns_mastercolumnid_fkey FOREIGN KEY (mastercolumnid) REFERENCES vaccine_logistics_master_columns(id);


--
-- Name: vaccine_program_logistics_columns_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_program_logistics_columns
    ADD CONSTRAINT vaccine_program_logistics_columns_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: vaccine_report_adverse_effect_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_adverse_effect_line_items
    ADD CONSTRAINT vaccine_report_adverse_effect_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vaccine_report_adverse_effect_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_adverse_effect_line_items
    ADD CONSTRAINT vaccine_report_adverse_effect_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_campaign_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_campaign_line_items
    ADD CONSTRAINT vaccine_report_campaign_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_cold_chain_line_items_equipmentinventoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_cold_chain_line_items
    ADD CONSTRAINT vaccine_report_cold_chain_line_items_equipmentinventoryid_fkey FOREIGN KEY (equipmentinventoryid) REFERENCES equipment_inventories(id);


--
-- Name: vaccine_report_cold_chain_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_cold_chain_line_items
    ADD CONSTRAINT vaccine_report_cold_chain_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_coverage_line_items_doseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_doseid_fkey FOREIGN KEY (doseid) REFERENCES vaccine_doses(id);


--
-- Name: vaccine_report_coverage_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vaccine_report_coverage_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_disease_line_items_diseaseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_disease_line_items
    ADD CONSTRAINT vaccine_report_disease_line_items_diseaseid_fkey FOREIGN KEY (diseaseid) REFERENCES vaccine_diseases(id);


--
-- Name: vaccine_report_disease_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_disease_line_items
    ADD CONSTRAINT vaccine_report_disease_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_logistics_line_items_discardingreasonid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_discardingreasonid_fkey FOREIGN KEY (discardingreasonid) REFERENCES vaccine_discarding_reasons(id);


--
-- Name: vaccine_report_logistics_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id);


--
-- Name: vaccine_report_logistics_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_status_changes_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_status_changes
    ADD CONSTRAINT vaccine_report_status_changes_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_report_vitamin_supplementation_l_vitaminagegroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_l_vitaminagegroupid_fkey FOREIGN KEY (vitaminagegroupid) REFERENCES vaccine_vitamin_supplementation_age_groups(id);


--
-- Name: vaccine_report_vitamin_supplementation_li_vaccinevitaminid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_li_vaccinevitaminid_fkey FOREIGN KEY (vaccinevitaminid) REFERENCES vaccine_vitamins(id);


--
-- Name: vaccine_report_vitamin_supplementation_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id);


--
-- Name: vaccine_reports_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);


--
-- Name: vaccine_reports_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id);


--
-- Name: vaccine_reports_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id);


--
-- Name: vaccine_vvm_lots_on_hand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_lots_on_hand_vvm
    ADD CONSTRAINT vaccine_vvm_lots_on_hand_fkey FOREIGN KEY (lotonhandid) REFERENCES lots_on_hand(id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

