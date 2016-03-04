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
v_highest_parent_name character varying(250);

v_this_parent_id integer;
v_this_parent_name character varying(250);

v_current_parent_id integer;
v_current_parent_name character varying(250);

v_parent_geographizone_name character varying(250);


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
v_supplying_facility_name character varying(50);
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
-- Name: fn_save_user_preference(integer, integer, integer, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE OR REPLACE FUNCTION fn_save_user_preference(in_userid integer, in_programid integer, in_facilityid integer, in_productid character varying)
  RETURNS character varying AS
$BODY$
DECLARE msg character varying(2000);
        DECLARE msg2 character varying(2000);
        v_scheduleid integer;
        v_periodid integer;
        v_zoneid integer;
BEGIN
  msg := 'ERROR';
  select  u.scheduleid, u.periodid, u.geographiczoneid into v_scheduleid, v_periodid, v_zoneid from fn_get_user_default_settings(in_programid,in_facilityid) u;
  msg := fn_set_user_preference(in_userid, 'DEFAULT_PROGRAM', in_programid::text);
  msg := fn_set_user_preference(in_userid, 'DEFAULT_SCHEDULE', v_scheduleid::text);
  msg := fn_set_user_preference(in_userid, 'DEFAULT_PERIOD',   v_periodid::text);
  msg := fn_set_user_preference(in_userid, 'DEFAULT_GEOGRAPHIC_ZONE',  v_zoneid::text);
  msg := fn_set_user_preference(in_userid, 'DEFAULT_FACILITY',  in_facilityid::text);
  msg := fn_set_user_preference(in_userid, 'DEFAULT_PRODUCTS',  in_productid::text);
  RETURN msg;
  EXCEPTION WHEN OTHERS THEN
  return SQLERRM;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;


ALTER FUNCTION fn_save_user_preference(integer, integer, integer, character varying) OWNER TO postgres;

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
    id serial NOT NULL,
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
    userpreferencekey character varying(50) NOT NULL,
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


ALTER TABLE ONLY email_attachments_relation
    ADD CONSTRAINT email_attachments_relation_pkey PRIMARY KEY (emailid, attachmentid);


ALTER TABLE ONLY fulfillment_role_assignments
ADD CONSTRAINT fulfillment_role_assignments_pkey PRIMARY KEY (userid, roleid, facilityid);


ALTER TABLE ONLY losses_adjustments_types
ADD CONSTRAINT losses_adjustments_types_pkey PRIMARY KEY (name);


ALTER TABLE ONLY master_regimen_columns
ADD CONSTRAINT master_regimen_columns_pkey PRIMARY KEY (name);


ALTER TABLE ONLY orders
ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


ALTER TABLE ONLY requisition_signatures
ADD CONSTRAINT requisition_signatures_pkey PRIMARY KEY (rnrid, signatureid);


ALTER TABLE ONLY role_assignments
ADD CONSTRAINT role_assignments_pkey PRIMARY KEY (id);


ALTER TABLE ONLY role_rights
ADD CONSTRAINT role_rights_pkey PRIMARY KEY (roleid, rightname);


ALTER TABLE ONLY stock_movement_line_item_extra_fields
ADD CONSTRAINT stock_movement_line_item_extra_fields_pkey PRIMARY KEY (id);


ALTER TABLE ONLY user_password_reset_tokens
ADD CONSTRAINT user_password_reset_tokens_pkey PRIMARY KEY (userid, token);


ALTER TABLE ONLY user_preference_roles
ADD CONSTRAINT user_preference_roles_pkey PRIMARY KEY (roleid, userpreferencekey);


ALTER TABLE ONLY user_preferences
ADD CONSTRAINT user_preferences_pkey PRIMARY KEY (userid, userpreferencekey);


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
    ADD CONSTRAINT adult_coverage_opened_vial_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: budget_line_items_budgetfileid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_budgetfileid_fkey FOREIGN KEY (budgetfileid) REFERENCES budget_file_info(id) DEFERRABLE;


--
-- Name: budget_line_items_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: budget_line_items_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: budget_line_items_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY budget_line_items
    ADD CONSTRAINT budget_line_items_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: combo_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_combination_constituents
    ADD CONSTRAINT combo_id_fkey FOREIGN KEY (productcomboid) REFERENCES regimen_product_combinations(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE;


--
-- Name: comments_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id) DEFERRABLE;


--
-- Name: comments_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: comments_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comments_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: coverage_product_vials_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY coverage_product_vials
    ADD CONSTRAINT coverage_product_vials_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code) DEFERRABLE;


--
-- Name: coverage_vaccination_products_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY coverage_target_group_products
    ADD CONSTRAINT coverage_vaccination_products_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code) DEFERRABLE;


--
-- Name: defalut_regimen_product_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_combination_constituents
    ADD CONSTRAINT defalut_regimen_product_id_fkey FOREIGN KEY (defaultdosageid) REFERENCES regimen_constituents_dosages(id) DEFERRABLE;


--
-- Name: delivery_zone_members_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_members
    ADD CONSTRAINT delivery_zone_members_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id) DEFERRABLE;


--
-- Name: delivery_zone_members_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_members
    ADD CONSTRAINT delivery_zone_members_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: delivery_zone_program_schedules_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id) DEFERRABLE;


--
-- Name: delivery_zone_program_schedules_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: delivery_zone_program_schedules_scheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_program_schedules
    ADD CONSTRAINT delivery_zone_program_schedules_scheduleid_fkey FOREIGN KEY (scheduleid) REFERENCES processing_schedules(id) DEFERRABLE;


--
-- Name: delivery_zone_warehouses_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_warehouses
    ADD CONSTRAINT delivery_zone_warehouses_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id) DEFERRABLE;


--
-- Name: delivery_zone_warehouses_warehouseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY delivery_zone_warehouses
    ADD CONSTRAINT delivery_zone_warehouses_warehouseid_fkey FOREIGN KEY (warehouseid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: distributions_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id) DEFERRABLE;


--
-- Name: distributions_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id) DEFERRABLE;


--
-- Name: distributions_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: distributions_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: distributions_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY distributions
    ADD CONSTRAINT distributions_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: district_demographic_estimates_demographicestimateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_demographicestimateid_fkey FOREIGN KEY (demographicestimateid) REFERENCES demographic_estimate_categories(id) DEFERRABLE;


--
-- Name: district_demographic_estimates_districtid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_districtid_fkey FOREIGN KEY (districtid) REFERENCES geographic_zones(id) DEFERRABLE;


--
-- Name: district_demographic_estimates_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY district_demographic_estimates
    ADD CONSTRAINT district_demographic_estimates_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: dosage_frequency_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT dosage_frequency_id_fkey FOREIGN KEY (dosagefrequencyid) REFERENCES dosage_frequencies(id) DEFERRABLE;


--
-- Name: dosage_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT dosage_unit_id_fkey FOREIGN KEY (dosageunitid) REFERENCES dosage_units(id) DEFERRABLE;


--
-- Name: elmis_help_helptopicid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help
    ADD CONSTRAINT elmis_help_helptopicid_fkey FOREIGN KEY (helptopicid) REFERENCES elmis_help_topic(id) DEFERRABLE;


--
-- Name: elmis_help_topic_parent_help_topic_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT elmis_help_topic_parent_help_topic_id_fkey FOREIGN KEY (parent_help_topic_id) REFERENCES elmis_help_topic(id) DEFERRABLE;


--
-- Name: elmis_help_topic_roles_help_topic_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic_roles
    ADD CONSTRAINT elmis_help_topic_roles_help_topic_id_fkey FOREIGN KEY (help_topic_id) REFERENCES elmis_help_topic(id) DEFERRABLE;


--
-- Name: elmis_help_topic_roles_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic_roles
    ADD CONSTRAINT elmis_help_topic_roles_role_id_fkey FOREIGN KEY (role_id) REFERENCES roles(id) DEFERRABLE;


--
-- Name: email_attachments_relation_attachmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY email_attachments_relation
    ADD CONSTRAINT email_attachments_relation_attachmentid_fkey FOREIGN KEY (attachmentid) REFERENCES email_attachments(id) DEFERRABLE;


--
-- Name: email_attachments_relation_emailid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY email_attachments_relation
    ADD CONSTRAINT email_attachments_relation_emailid_fkey FOREIGN KEY (emailid) REFERENCES email_notifications(id) DEFERRABLE;


--
-- Name: epi_inventory_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_inventory_line_items
    ADD CONSTRAINT epi_inventory_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: epi_inventory_line_items_programproductid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_inventory_line_items
    ADD CONSTRAINT epi_inventory_line_items_programproductid_fkey FOREIGN KEY (programproductid) REFERENCES program_products(id) DEFERRABLE;


--
-- Name: epi_use_line_items_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id) DEFERRABLE;


--
-- Name: epi_use_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: epi_use_line_items_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: epi_use_line_items_productgroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY epi_use_line_items
    ADD CONSTRAINT epi_use_line_items_productgroupid_fkey FOREIGN KEY (productgroupid) REFERENCES product_groups(id) DEFERRABLE;


--
-- Name: equipment_cce_designation_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_designation_fkey FOREIGN KEY (designationid) REFERENCES equipment_cold_chain_equipment_designations(id) DEFERRABLE;


--
-- Name: equipment_cce_donor_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_donor_fkey FOREIGN KEY (donorid) REFERENCES donors(id) DEFERRABLE;


--
-- Name: equipment_cce_equipment_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_equipment_fkey FOREIGN KEY (equipmentid) REFERENCES equipments(id) DEFERRABLE;


--
-- Name: equipment_cce_psq_status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_cold_chain_equipments
    ADD CONSTRAINT equipment_cce_psq_status_fkey FOREIGN KEY (pqsstatusid) REFERENCES equipment_cold_chain_equipment_pqs_status(id) DEFERRABLE;


--
-- Name: equipment_contract_service_types_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_contract_service_types
    ADD CONSTRAINT equipment_contract_service_types_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id) DEFERRABLE;


--
-- Name: equipment_contract_service_types_servicetypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_contract_service_types
    ADD CONSTRAINT equipment_contract_service_types_servicetypeid_fkey FOREIGN KEY (servicetypeid) REFERENCES equipment_service_types(id) DEFERRABLE;


--
-- Name: equipment_energy_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipments
    ADD CONSTRAINT equipment_energy_type_fkey FOREIGN KEY (energytypeid) REFERENCES equipment_energy_types(id) DEFERRABLE;


--
-- Name: equipment_maintenance_logs_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id) DEFERRABLE;


--
-- Name: equipment_maintenance_logs_equipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_equipmentid_fkey FOREIGN KEY (equipmentid) REFERENCES equipments(id) DEFERRABLE;


--
-- Name: equipment_maintenance_logs_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: equipment_maintenance_logs_requestid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_requestid_fkey FOREIGN KEY (requestid) REFERENCES equipment_maintenance_requests(id) DEFERRABLE;


--
-- Name: equipment_maintenance_logs_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: equipment_maintenance_logs_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_logs
    ADD CONSTRAINT equipment_maintenance_logs_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id) DEFERRABLE;


--
-- Name: equipment_maintenance_requests_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: equipment_maintenance_requests_inventoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_inventoryid_fkey FOREIGN KEY (inventoryid) REFERENCES equipment_inventories(id) DEFERRABLE;


--
-- Name: equipment_maintenance_requests_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: equipment_maintenance_requests_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_maintenance_requests
    ADD CONSTRAINT equipment_maintenance_requests_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id) DEFERRABLE;


--
-- Name: equipment_service_contract_equipments_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_equipment_types
    ADD CONSTRAINT equipment_service_contract_equipments_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id) DEFERRABLE;


--
-- Name: equipment_service_contract_equipments_equipmenttypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_equipment_types
    ADD CONSTRAINT equipment_service_contract_equipments_equipmenttypeid_fkey FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types(id) DEFERRABLE;


--
-- Name: equipment_service_contract_facilities_contractid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_facilities
    ADD CONSTRAINT equipment_service_contract_facilities_contractid_fkey FOREIGN KEY (contractid) REFERENCES equipment_service_contracts(id) DEFERRABLE;


--
-- Name: equipment_service_contract_facilities_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contract_facilities
    ADD CONSTRAINT equipment_service_contract_facilities_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: equipment_service_contracts_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_contracts
    ADD CONSTRAINT equipment_service_contracts_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id) DEFERRABLE;


--
-- Name: equipment_service_vendor_users_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_vendor_users
    ADD CONSTRAINT equipment_service_vendor_users_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: equipment_service_vendor_users_vendorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_service_vendor_users
    ADD CONSTRAINT equipment_service_vendor_users_vendorid_fkey FOREIGN KEY (vendorid) REFERENCES equipment_service_vendors(id) DEFERRABLE;


--
-- Name: equipment_status_line_items_equipmentinventoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_equipmentinventoryid_fkey FOREIGN KEY (equipmentinventoryid) REFERENCES equipment_inventories(id) DEFERRABLE;


--
-- Name: equipment_status_line_items_inventorystatusid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_inventorystatusid_fkey FOREIGN KEY (inventorystatusid) REFERENCES equipment_inventory_statuses(id) DEFERRABLE;


--
-- Name: equipment_status_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_status_line_items
    ADD CONSTRAINT equipment_status_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: equipments_equipmenttypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipments
    ADD CONSTRAINT equipments_equipmenttypeid_fkey FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types(id) DEFERRABLE;


--
-- Name: facilities_geographiczoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_geographiczoneid_fkey FOREIGN KEY (geographiczoneid) REFERENCES geographic_zones(id) DEFERRABLE;


--
-- Name: facilities_operatedbyid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_operatedbyid_fkey FOREIGN KEY (operatedbyid) REFERENCES facility_operators(id) DEFERRABLE;


--
-- Name: facilities_parentfacilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_parentfacilityid_fkey FOREIGN KEY (parentfacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facilities_pricescheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_pricescheduleid_fkey FOREIGN KEY (pricescheduleid) REFERENCES price_schedules(id) DEFERRABLE;


--
-- Name: facilities_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facilities
    ADD CONSTRAINT facilities_typeid_fkey FOREIGN KEY (typeid) REFERENCES facility_types(id) DEFERRABLE;


--
-- Name: facility_approved_products_facilitytypeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_approved_products
    ADD CONSTRAINT facility_approved_products_facilitytypeid_fkey FOREIGN KEY (facilitytypeid) REFERENCES facility_types(id) DEFERRABLE;


--
-- Name: facility_approved_products_programproductid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_approved_products
    ADD CONSTRAINT facility_approved_products_programproductid_fkey FOREIGN KEY (programproductid) REFERENCES program_products(id) DEFERRABLE;


--
-- Name: facility_demographic_estimates_demographicestimateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_demographicestimateid_fkey FOREIGN KEY (demographicestimateid) REFERENCES demographic_estimate_categories(id) DEFERRABLE;


--
-- Name: facility_demographic_estimates_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_demographic_estimates_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_demographic_estimates
    ADD CONSTRAINT facility_demographic_estimates_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_cards
    ADD CONSTRAINT facility_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT facility_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_ftp_details_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_ftp_details
    ADD CONSTRAINT facility_ftp_details_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_mappings_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_mappings
    ADD CONSTRAINT facility_mappings_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_mappings_interfaceid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_mappings
    ADD CONSTRAINT facility_mappings_interfaceid_fkey FOREIGN KEY (interfaceid) REFERENCES interface_apps(id) DEFERRABLE;


--
-- Name: facility_program_equipments_equipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_equipmentid_fkey FOREIGN KEY (equipmentid) REFERENCES equipments(id) DEFERRABLE;


--
-- Name: facility_program_equipments_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_program_equipments_primarydonorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_primarydonorid_fkey FOREIGN KEY (primarydonorid) REFERENCES donors(id) DEFERRABLE;


--
-- Name: facility_program_equipments_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventories
    ADD CONSTRAINT facility_program_equipments_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: facility_program_products_facilityid_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_facilityid_fkey1 FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: facility_program_products_isacoefficientsid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_isacoefficientsid_fkey FOREIGN KEY (isacoefficientsid) REFERENCES isa_coefficients(id) DEFERRABLE;


--
-- Name: facility_program_products_programproductid_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_program_products
    ADD CONSTRAINT facility_program_products_programproductid_fkey1 FOREIGN KEY (programproductid) REFERENCES program_products(id) DEFERRABLE;


--
-- Name: facility_visits_distributionid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_visits
    ADD CONSTRAINT facility_visits_distributionid_fkey FOREIGN KEY (distributionid) REFERENCES distributions(id) DEFERRABLE;


--
-- Name: facility_visits_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY facility_visits
    ADD CONSTRAINT facility_visits_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: fk_foreign_users_modifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT fk_foreign_users_modifier FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: fk_foreing_users_creator; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help_topic
    ADD CONSTRAINT fk_foreing_users_creator FOREIGN KEY (created_by) REFERENCES users(id) DEFERRABLE;


--
-- Name: fk_user_help_modifier; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY elmis_help
    ADD CONSTRAINT fk_user_help_modifier FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: from_facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movements
    ADD CONSTRAINT from_facility_fkey FOREIGN KEY (fromfacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: fulfillment_role_assignments_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT fulfillment_role_assignments_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: fulfillment_role_assignments_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT fulfillment_role_assignments_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id) DEFERRABLE;


--
-- Name: fulfillment_role_assignments_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY fulfillment_role_assignments
    ADD CONSTRAINT fulfillment_role_assignments_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: full_coverages_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY full_coverages
    ADD CONSTRAINT full_coverages_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: geographic_zones_levelid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_zones
    ADD CONSTRAINT geographic_zones_levelid_fkey FOREIGN KEY (levelid) REFERENCES geographic_levels(id) DEFERRABLE;


--
-- Name: geographic_zones_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY geographic_zones
    ADD CONSTRAINT geographic_zones_parentid_fkey FOREIGN KEY (parentid) REFERENCES geographic_zones(id) DEFERRABLE;


--
-- Name: interface_dataset_interfaceid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interface_dataset
    ADD CONSTRAINT interface_dataset_interfaceid_fkey FOREIGN KEY (interfaceid) REFERENCES interface_apps(id) DEFERRABLE;


--
-- Name: inventory_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT inventory_fkey FOREIGN KEY (inventoryid) REFERENCES equipment_inventories(id) DEFERRABLE;


--
-- Name: isa_coefficients_populationsource_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY isa_coefficients
    ADD CONSTRAINT isa_coefficients_populationsource_fkey FOREIGN KEY (populationsource) REFERENCES demographic_estimate_categories(id) DEFERRABLE;


--
-- Name: losses_adjustments_types_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT losses_adjustments_types_fkey FOREIGN KEY (adjustmenttype) REFERENCES losses_adjustments_types(name) DEFERRABLE;


--
-- Name: lot_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots_on_hand
    ADD CONSTRAINT lot_fkey FOREIGN KEY (lotid) REFERENCES lots(id) DEFERRABLE;


--
-- Name: lot_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_items
    ADD CONSTRAINT lot_fkey FOREIGN KEY (lotid) REFERENCES lots(id) DEFERRABLE;


--
-- Name: lot_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_lots
    ADD CONSTRAINT lot_fkey FOREIGN KEY (lotid) REFERENCES lots(id) DEFERRABLE;


--
-- Name: lot_on_hand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT lot_on_hand_fkey FOREIGN KEY (lotonhandid) REFERENCES lots_on_hand(id) DEFERRABLE;


--
-- Name: master_rnr_column_options_masterrnrcolumnid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_rnr_column_options
    ADD CONSTRAINT master_rnr_column_options_masterrnrcolumnid_fkey FOREIGN KEY (masterrnrcolumnid) REFERENCES master_rnr_columns(id) DEFERRABLE;


--
-- Name: master_rnr_column_options_rnroptionid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY master_rnr_column_options
    ADD CONSTRAINT master_rnr_column_options_rnroptionid_fkey FOREIGN KEY (rnroptionid) REFERENCES configurable_rnr_options(id) DEFERRABLE;


--
-- Name: mos_adjustment_facilities_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_facilities
    ADD CONSTRAINT mos_adjustment_facilities_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: mos_adjustment_facilities_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_facilities
    ADD CONSTRAINT mos_adjustment_facilities_typeid_fkey FOREIGN KEY (typeid) REFERENCES mos_adjustment_products(id) DEFERRABLE;


--
-- Name: mos_adjustment_products_basisid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_basisid_fkey FOREIGN KEY (basisid) REFERENCES mos_adjustment_basis(id) DEFERRABLE;


--
-- Name: mos_adjustment_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: mos_adjustment_products_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY mos_adjustment_products
    ADD CONSTRAINT mos_adjustment_products_typeid_fkey FOREIGN KEY (typeid) REFERENCES mos_adjustment_types(id) DEFERRABLE;


--
-- Name: not_functional_reason_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT not_functional_reason_fkey FOREIGN KEY (notfunctionalstatusid) REFERENCES equipment_operational_status(id) DEFERRABLE;


--
-- Name: opened_vial_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY child_coverage_opened_vial_line_items
    ADD CONSTRAINT opened_vial_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: order_quantity_adjustment_products_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: order_quantity_adjustment_products_factorsid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_factorsid_fkey FOREIGN KEY (factorid) REFERENCES order_quantity_adjustment_factors(id) DEFERRABLE;


--
-- Name: order_quantity_adjustment_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: order_quantity_adjustment_products_typeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY order_quantity_adjustment_products
    ADD CONSTRAINT order_quantity_adjustment_products_typeid_fkey FOREIGN KEY (typeid) REFERENCES order_quantity_adjustment_types(id) DEFERRABLE;


--
-- Name: orders_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id) DEFERRABLE;


--
-- Name: orders_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_id_fkey FOREIGN KEY (id) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: orders_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: orders_shipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_shipmentid_fkey FOREIGN KEY (shipmentid) REFERENCES shipment_file_info(id) DEFERRABLE;


--
-- Name: orders_supplylineid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY orders
    ADD CONSTRAINT orders_supplylineid_fkey FOREIGN KEY (supplylineid) REFERENCES supply_lines(id) DEFERRABLE;


--
-- Name: patient_quantification_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY patient_quantification_line_items
    ADD CONSTRAINT patient_quantification_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: period_short_names_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY period_short_names
    ADD CONSTRAINT period_short_names_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: pod_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: pod_line_items_podid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod_line_items
    ADD CONSTRAINT pod_line_items_podid_fkey FOREIGN KEY (podid) REFERENCES pod(id) DEFERRABLE;


--
-- Name: pod_line_items_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod_line_items
    ADD CONSTRAINT pod_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code) DEFERRABLE;


--
-- Name: pod_orderid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_orderid_fkey FOREIGN KEY (orderid) REFERENCES orders(id) DEFERRABLE;


--
-- Name: pod_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: pod_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pod
    ADD CONSTRAINT pod_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: processing_periods_scheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY processing_periods
    ADD CONSTRAINT processing_periods_scheduleid_fkey FOREIGN KEY (scheduleid) REFERENCES processing_schedules(id) DEFERRABLE;


--
-- Name: product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_cards
    ADD CONSTRAINT product_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots
    ADD CONSTRAINT product_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: product_price_schedules_pricescheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_price_schedules
    ADD CONSTRAINT product_price_schedules_pricescheduleid_fkey FOREIGN KEY (pricescheduleid) REFERENCES price_schedules(id) DEFERRABLE;


--
-- Name: product_price_schedules_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_price_schedules
    ADD CONSTRAINT product_price_schedules_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: product_short_names_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY product_short_names
    ADD CONSTRAINT product_short_names_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: products_dosageunitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_dosageunitid_fkey FOREIGN KEY (dosageunitid) REFERENCES dosage_units(id) DEFERRABLE;


--
-- Name: products_formid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_formid_fkey FOREIGN KEY (formid) REFERENCES product_forms(id) DEFERRABLE;


--
-- Name: products_productgroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_productgroupid_fkey FOREIGN KEY (productgroupid) REFERENCES product_groups(id) DEFERRABLE;


--
-- Name: program_equipment_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_products
    ADD CONSTRAINT program_equipment_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: program_equipment_products_programequipmentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_products
    ADD CONSTRAINT program_equipment_products_programequipmentid_fkey FOREIGN KEY (programequipmenttypeid) REFERENCES equipment_type_programs(id) DEFERRABLE;


--
-- Name: program_equipments_equipmenttypeid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_programs
    ADD CONSTRAINT program_equipments_equipmenttypeid FOREIGN KEY (equipmenttypeid) REFERENCES equipment_types(id) DEFERRABLE;


--
-- Name: program_equipments_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_type_programs
    ADD CONSTRAINT program_equipments_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: program_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_adjustment_reasons_programs
    ADD CONSTRAINT program_fkey FOREIGN KEY (programcode) REFERENCES programs(code) DEFERRABLE;


--
-- Name: program_product_price_history_programproductid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_product_price_history
    ADD CONSTRAINT program_product_price_history_programproductid_fkey FOREIGN KEY (programproductid) REFERENCES program_products(id) DEFERRABLE;


--
-- Name: program_products_isacoefficientsid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_isacoefficientsid_fkey FOREIGN KEY (isacoefficientsid) REFERENCES isa_coefficients(id) DEFERRABLE;


--
-- Name: program_products_productcategoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_productcategoryid_fkey FOREIGN KEY (productcategoryid) REFERENCES product_categories(id) DEFERRABLE;


--
-- Name: program_products_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: program_products_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_products
    ADD CONSTRAINT program_products_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: program_regimen_columns_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_regimen_columns
    ADD CONSTRAINT program_regimen_columns_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: program_rnr_columns_mastercolumnid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_rnr_columns
    ADD CONSTRAINT program_rnr_columns_mastercolumnid_fkey FOREIGN KEY (mastercolumnid) REFERENCES master_rnr_columns(id) DEFERRABLE;


--
-- Name: program_rnr_columns_rnroptionid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY program_rnr_columns
    ADD CONSTRAINT program_rnr_columns_rnroptionid_fkey FOREIGN KEY (rnroptionid) REFERENCES configurable_rnr_options(id) DEFERRABLE;


--
-- Name: programs_supported_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY programs_supported
    ADD CONSTRAINT programs_supported_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: programs_supported_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY programs_supported
    ADD CONSTRAINT programs_supported_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: refrigerator_problems_readingid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_problems
    ADD CONSTRAINT refrigerator_problems_readingid_fkey FOREIGN KEY (readingid) REFERENCES refrigerator_readings(id) DEFERRABLE;


--
-- Name: refrigerator_readings_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_readings
    ADD CONSTRAINT refrigerator_readings_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: refrigerator_readings_refrigeratorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerator_readings
    ADD CONSTRAINT refrigerator_readings_refrigeratorid_fkey FOREIGN KEY (refrigeratorid) REFERENCES refrigerators(id) DEFERRABLE;


--
-- Name: refrigerators_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id) DEFERRABLE;


--
-- Name: refrigerators_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: refrigerators_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY refrigerators
    ADD CONSTRAINT refrigerators_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: regimen_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_product_combinations
    ADD CONSTRAINT regimen_id_fkey FOREIGN KEY (regimenid) REFERENCES regimens(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE;


--
-- Name: regimen_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_line_items
    ADD CONSTRAINT regimen_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: regimens_categoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimens
    ADD CONSTRAINT regimens_categoryid_fkey FOREIGN KEY (categoryid) REFERENCES regimen_categories(id) DEFERRABLE;


--
-- Name: regimens_product_dosage_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimen_constituents_dosages
    ADD CONSTRAINT regimens_product_dosage_fkey FOREIGN KEY (regimenproductid) REFERENCES regimen_combination_constituents(id) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE;


--
-- Name: regimens_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY regimens
    ADD CONSTRAINT regimens_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: report_rights_rightname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY report_rights
    ADD CONSTRAINT report_rights_rightname_fkey FOREIGN KEY (rightname) REFERENCES rights(name) DEFERRABLE;


--
-- Name: report_rights_templateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY report_rights
    ADD CONSTRAINT report_rights_templateid_fkey FOREIGN KEY (templateid) REFERENCES templates(id) DEFERRABLE;


--
-- Name: requisition_group_members_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_members
    ADD CONSTRAINT requisition_group_members_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: requisition_group_members_requisitiongroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_members
    ADD CONSTRAINT requisition_group_members_requisitiongroupid_fkey FOREIGN KEY (requisitiongroupid) REFERENCES requisition_groups(id) DEFERRABLE;


--
-- Name: requisition_group_program_schedules_dropofffacilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_dropofffacilityid_fkey FOREIGN KEY (dropofffacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: requisition_group_program_schedules_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: requisition_group_program_schedules_requisitiongroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_requisitiongroupid_fkey FOREIGN KEY (requisitiongroupid) REFERENCES requisition_groups(id) DEFERRABLE;


--
-- Name: requisition_group_program_schedules_scheduleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_group_program_schedules
    ADD CONSTRAINT requisition_group_program_schedules_scheduleid_fkey FOREIGN KEY (scheduleid) REFERENCES processing_schedules(id) DEFERRABLE;


--
-- Name: requisition_groups_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_groups
    ADD CONSTRAINT requisition_groups_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id) DEFERRABLE;


--
-- Name: requisition_line_item_losses_adjustm_requisitionlineitemid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_item_losses_adjustments
    ADD CONSTRAINT requisition_line_item_losses_adjustm_requisitionlineitemid_fkey FOREIGN KEY (requisitionlineitemid) REFERENCES requisition_line_items(id) DEFERRABLE;


--
-- Name: requisition_line_item_losses_adjustments_type_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_item_losses_adjustments
    ADD CONSTRAINT requisition_line_item_losses_adjustments_type_fkey FOREIGN KEY (type) REFERENCES losses_adjustments_types(name) DEFERRABLE;


--
-- Name: requisition_line_items_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_items
    ADD CONSTRAINT requisition_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code) DEFERRABLE;


--
-- Name: requisition_line_items_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_line_items
    ADD CONSTRAINT requisition_line_items_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: requisition_signatures_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_signatures
    ADD CONSTRAINT requisition_signatures_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: requisition_signatures_signatureid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_signatures
    ADD CONSTRAINT requisition_signatures_signatureid_fkey FOREIGN KEY (signatureid) REFERENCES signatures(id) DEFERRABLE;


--
-- Name: requisition_status_changes_createdby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_createdby_fkey FOREIGN KEY (createdby) REFERENCES users(id) DEFERRABLE;


--
-- Name: requisition_status_changes_modifiedby_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_modifiedby_fkey FOREIGN KEY (modifiedby) REFERENCES users(id) DEFERRABLE;


--
-- Name: requisition_status_changes_rnrid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisition_status_changes
    ADD CONSTRAINT requisition_status_changes_rnrid_fkey FOREIGN KEY (rnrid) REFERENCES requisitions(id) DEFERRABLE;


--
-- Name: requisitions_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: requisitions_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: requisitions_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: requisitions_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY requisitions
    ADD CONSTRAINT requisitions_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id) DEFERRABLE;


--
-- Name: role_assignments_deliveryzoneid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_deliveryzoneid_fkey FOREIGN KEY (deliveryzoneid) REFERENCES delivery_zones(id) DEFERRABLE;


--
-- Name: role_assignments_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: role_assignments_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id) DEFERRABLE;


--
-- Name: role_assignments_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id) DEFERRABLE;


--
-- Name: role_assignments_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_assignments
    ADD CONSTRAINT role_assignments_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: role_rights_rightname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_rights
    ADD CONSTRAINT role_rights_rightname_fkey FOREIGN KEY (rightname) REFERENCES rights(name) DEFERRABLE;


--
-- Name: role_rights_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY role_rights
    ADD CONSTRAINT role_rights_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id) DEFERRABLE;


--
-- Name: shipment_line_items_orderid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_line_items
    ADD CONSTRAINT shipment_line_items_orderid_fkey FOREIGN KEY (orderid) REFERENCES orders(id) DEFERRABLE;


--
-- Name: shipment_line_items_productcode_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY shipment_line_items
    ADD CONSTRAINT shipment_line_items_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code) DEFERRABLE;


--
-- Name: status_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY equipment_inventory_statuses
    ADD CONSTRAINT status_fkey FOREIGN KEY (statusid) REFERENCES equipment_operational_status(id) DEFERRABLE;


--
-- Name: stock_adjustment_reason_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_adjustment_reasons_programs
    ADD CONSTRAINT stock_adjustment_reason_fkey FOREIGN KEY (reasonname) REFERENCES losses_adjustments_types(name) DEFERRABLE;


--
-- Name: stock_card_entry_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entry_key_values
    ADD CONSTRAINT stock_card_entry_fkey FOREIGN KEY (stockcardentryid) REFERENCES stock_card_entries(id) DEFERRABLE;


--
-- Name: stock_card_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY lots_on_hand
    ADD CONSTRAINT stock_card_fkey FOREIGN KEY (stockcardid) REFERENCES stock_cards(id) DEFERRABLE;


--
-- Name: stock_card_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT stock_card_fkey FOREIGN KEY (stockcardid) REFERENCES stock_cards(id) DEFERRABLE;


--
-- Name: stock_movement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_card_entries
    ADD CONSTRAINT stock_movement_fkey FOREIGN KEY (stockmovementid) REFERENCES stock_movements(id) DEFERRABLE;


--
-- Name: stock_movement_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_items
    ADD CONSTRAINT stock_movement_fkey FOREIGN KEY (stockmovementid) REFERENCES stock_movements(id) DEFERRABLE;


--
-- Name: stock_movement_line_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_lots
    ADD CONSTRAINT stock_movement_line_item_fkey FOREIGN KEY (stockmovementlineitemid) REFERENCES stock_movement_line_items(id) DEFERRABLE;


--
-- Name: stock_movement_line_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movement_line_item_extra_fields
    ADD CONSTRAINT stock_movement_line_item_fkey FOREIGN KEY (stockmovementlineitemid) REFERENCES stock_movement_line_items(id) DEFERRABLE;


--
-- Name: supervisory_nodes_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supervisory_nodes
    ADD CONSTRAINT supervisory_nodes_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: supervisory_nodes_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supervisory_nodes
    ADD CONSTRAINT supervisory_nodes_parentid_fkey FOREIGN KEY (parentid) REFERENCES supervisory_nodes(id) DEFERRABLE;


--
-- Name: supply_lines_parentid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_parentid_fkey FOREIGN KEY (parentid) REFERENCES supply_lines(id) DEFERRABLE;


--
-- Name: supply_lines_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: supply_lines_supervisorynodeid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_supervisorynodeid_fkey FOREIGN KEY (supervisorynodeid) REFERENCES supervisory_nodes(id) DEFERRABLE;


--
-- Name: supply_lines_supplyingfacilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY supply_lines
    ADD CONSTRAINT supply_lines_supplyingfacilityid_fkey FOREIGN KEY (supplyingfacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: template_parameters_templateid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT template_parameters_templateid_fkey FOREIGN KEY (templateid) REFERENCES templates(id) DEFERRABLE;


--
-- Name: to_facility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY stock_movements
    ADD CONSTRAINT to_facility_fkey FOREIGN KEY (tofacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: user_password_reset_tokens_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_password_reset_tokens
    ADD CONSTRAINT user_password_reset_tokens_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: user_preference_roles_roleid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preference_roles
    ADD CONSTRAINT user_preference_roles_roleid_fkey FOREIGN KEY (roleid) REFERENCES roles(id) DEFERRABLE;


--
-- Name: user_preference_roles_userpreferencekey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preference_roles
    ADD CONSTRAINT user_preference_roles_userpreferencekey_fkey FOREIGN KEY (userpreferencekey) REFERENCES user_preference_master(key) DEFERRABLE;


--
-- Name: user_preferences_userid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preferences
    ADD CONSTRAINT user_preferences_userid_fkey FOREIGN KEY (userid) REFERENCES users(id) DEFERRABLE;


--
-- Name: user_preferences_userpreferencekey_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_preferences
    ADD CONSTRAINT user_preferences_userpreferencekey_fkey FOREIGN KEY (userpreferencekey) REFERENCES user_preference_master(key) DEFERRABLE;


--
-- Name: users_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: users_supervisorid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_supervisorid_fkey FOREIGN KEY (supervisorid) REFERENCES users(id) DEFERRABLE;


--
-- Name: vacc_distribution_line_items_distribution_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_items
    ADD CONSTRAINT vacc_distribution_line_items_distribution_fkey FOREIGN KEY (distributionid) REFERENCES vaccine_distributions(id) DEFERRABLE;


--
-- Name: vacc_distribution_lotid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_item_lots
    ADD CONSTRAINT vacc_distribution_lotid_fkey FOREIGN KEY (lotid) REFERENCES lots(id) DEFERRABLE;


--
-- Name: vacc_distribution_lots_line_item_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_item_lots
    ADD CONSTRAINT vacc_distribution_lots_line_item_fkey FOREIGN KEY (distributionlineitemid) REFERENCES vaccine_distribution_line_items(id) DEFERRABLE;


--
-- Name: vacc_distributions_frofacility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_distributions_frofacility_fkey FOREIGN KEY (fromfacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: vacc_distributions_line_items_product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distribution_line_items
    ADD CONSTRAINT vacc_distributions_line_items_product_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vacc_distributions_period_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_distributions_period_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: vacc_distributions_tofacility_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_distributions
    ADD CONSTRAINT vacc_distributions_tofacility_fkey FOREIGN KEY (tofacilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: vaccination_adult_coverage_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccination_adult_coverage_line_items
    ADD CONSTRAINT vaccination_adult_coverage_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: vaccination_child_coverage_line_items_facilityvisitid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccination_child_coverage_line_items
    ADD CONSTRAINT vaccination_child_coverage_line_items_facilityvisitid_fkey FOREIGN KEY (facilityvisitid) REFERENCES facility_visits(id) DEFERRABLE;


--
-- Name: vaccine_adjustment_reasons_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_lots_on_hand_adjustments
    ADD CONSTRAINT vaccine_adjustment_reasons_fkey FOREIGN KEY (adjustmentreason) REFERENCES losses_adjustments_types(name) DEFERRABLE;


--
-- Name: vaccine_inventory_config_product_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_inventory_product_configurations
    ADD CONSTRAINT vaccine_inventory_config_product_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vaccine_inventory_product_configurations_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_inventory_product_configurations
    ADD CONSTRAINT vaccine_inventory_product_configurations_fkey FOREIGN KEY (denominatorestimatecategoryid) REFERENCES demographic_estimate_categories(id) DEFERRABLE;


--
-- Name: vaccine_ivd_tab_visibilities_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_ivd_tab_visibilities
    ADD CONSTRAINT vaccine_ivd_tab_visibilities_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: vaccine_order_requisition_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisition_line_items
    ADD CONSTRAINT vaccine_order_requisition_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vaccine_order_requisitions_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT vaccine_order_requisitions_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: vaccine_order_requisitions_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_order_requisitions
    ADD CONSTRAINT vaccine_order_requisitions_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: vaccine_product_doses_denominatorestimatecategoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_denominatorestimatecategoryid_fkey FOREIGN KEY (denominatorestimatecategoryid) REFERENCES demographic_estimate_categories(id) DEFERRABLE;


--
-- Name: vaccine_product_doses_doseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_doseid_fkey FOREIGN KEY (doseid) REFERENCES vaccine_doses(id) DEFERRABLE;


--
-- Name: vaccine_product_doses_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vaccine_product_doses_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_product_doses
    ADD CONSTRAINT vaccine_product_doses_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: vaccine_program_logistics_columns_mastercolumnid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_program_logistics_columns
    ADD CONSTRAINT vaccine_program_logistics_columns_mastercolumnid_fkey FOREIGN KEY (mastercolumnid) REFERENCES vaccine_logistics_master_columns(id) DEFERRABLE;


--
-- Name: vaccine_program_logistics_columns_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_program_logistics_columns
    ADD CONSTRAINT vaccine_program_logistics_columns_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: vaccine_report_adverse_effect_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_adverse_effect_line_items
    ADD CONSTRAINT vaccine_report_adverse_effect_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vaccine_report_adverse_effect_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_adverse_effect_line_items
    ADD CONSTRAINT vaccine_report_adverse_effect_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_campaign_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_campaign_line_items
    ADD CONSTRAINT vaccine_report_campaign_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_cold_chain_line_items_equipmentinventoryid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_cold_chain_line_items
    ADD CONSTRAINT vaccine_report_cold_chain_line_items_equipmentinventoryid_fkey FOREIGN KEY (equipmentinventoryid) REFERENCES equipment_inventories(id) DEFERRABLE;


--
-- Name: vaccine_report_cold_chain_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_cold_chain_line_items
    ADD CONSTRAINT vaccine_report_cold_chain_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_coverage_line_items_doseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_doseid_fkey FOREIGN KEY (doseid) REFERENCES vaccine_doses(id) DEFERRABLE;


--
-- Name: vaccine_report_coverage_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vaccine_report_coverage_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_coverage_line_items
    ADD CONSTRAINT vaccine_report_coverage_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_disease_line_items_diseaseid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_disease_line_items
    ADD CONSTRAINT vaccine_report_disease_line_items_diseaseid_fkey FOREIGN KEY (diseaseid) REFERENCES vaccine_diseases(id) DEFERRABLE;


--
-- Name: vaccine_report_disease_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_disease_line_items
    ADD CONSTRAINT vaccine_report_disease_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_logistics_line_items_discardingreasonid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_discardingreasonid_fkey FOREIGN KEY (discardingreasonid) REFERENCES vaccine_discarding_reasons(id) DEFERRABLE;


--
-- Name: vaccine_report_logistics_line_items_productid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_productid_fkey FOREIGN KEY (productid) REFERENCES products(id) DEFERRABLE;


--
-- Name: vaccine_report_logistics_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_logistics_line_items
    ADD CONSTRAINT vaccine_report_logistics_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_status_changes_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_status_changes
    ADD CONSTRAINT vaccine_report_status_changes_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_report_vitamin_supplementation_l_vitaminagegroupid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_l_vitaminagegroupid_fkey FOREIGN KEY (vitaminagegroupid) REFERENCES vaccine_vitamin_supplementation_age_groups(id) DEFERRABLE;


--
-- Name: vaccine_report_vitamin_supplementation_li_vaccinevitaminid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_li_vaccinevitaminid_fkey FOREIGN KEY (vaccinevitaminid) REFERENCES vaccine_vitamins(id) DEFERRABLE;


--
-- Name: vaccine_report_vitamin_supplementation_line_items_reportid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_report_vitamin_supplementation_line_items
    ADD CONSTRAINT vaccine_report_vitamin_supplementation_line_items_reportid_fkey FOREIGN KEY (reportid) REFERENCES vaccine_reports(id) DEFERRABLE;


--
-- Name: vaccine_reports_facilityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id) DEFERRABLE;


--
-- Name: vaccine_reports_periodid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_periodid_fkey FOREIGN KEY (periodid) REFERENCES processing_periods(id) DEFERRABLE;


--
-- Name: vaccine_reports_programid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_reports
    ADD CONSTRAINT vaccine_reports_programid_fkey FOREIGN KEY (programid) REFERENCES programs(id) DEFERRABLE;


--
-- Name: vaccine_vvm_lots_on_hand_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY vaccine_lots_on_hand_vvm
    ADD CONSTRAINT vaccine_vvm_lots_on_hand_fkey FOREIGN KEY (lotonhandid) REFERENCES lots_on_hand(id) DEFERRABLE;


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

--
-- Data for Name: rights; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('CONFIGURE_RNR', 'ADMIN', 'Permission to create and edit r&r template for any program', '2016-02-03 14:37:33.428452', 1, 'right.configure.rnr');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_FACILITY', 'ADMIN', 'Permission to manage facilities(crud)', '2016-02-03 14:37:33.428452', 2, 'right.manage.facility');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_ROLE', 'ADMIN', 'Permission to create and edit roles in the system', '2016-02-03 14:37:33.428452', 5, 'right.manage.role');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SCHEDULE', 'ADMIN', 'Permission to create and edit schedules in the system', '2016-02-03 14:37:33.428452', 6, 'right.manage.schedule');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_USER', 'ADMIN', 'Permission to create and view users', '2016-02-03 14:37:33.428452', 7, 'right.manage.user');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('UPLOADS', 'ADMIN', 'Permission to upload', '2016-02-03 14:37:33.428452', 21, 'right.upload');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_REQUISITION', 'REQUISITION', 'Permission to view requisition', '2016-02-03 14:37:33.428452', 16, 'right.view.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('CREATE_REQUISITION', 'REQUISITION', 'Permission to create, edit, submit and recall requisitions', '2016-02-03 14:37:33.428452', 15, 'right.create.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('AUTHORIZE_REQUISITION', 'REQUISITION', 'Permission to edit, authorize and recall requisitions', '2016-02-03 14:37:33.428452', 13, 'right.authorize.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('APPROVE_REQUISITION', 'REQUISITION', 'Permission to approve requisitions', '2016-02-03 14:37:33.428452', 12, 'right.approve.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('CONVERT_TO_ORDER', 'FULFILLMENT', 'Permission to convert requisitions to order', '2016-02-03 14:37:33.428452', 14, 'right.convert.to.order');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_ORDER', 'FULFILLMENT', 'Permission to view orders', '2016-02-03 14:37:33.428452', 17, 'right.view.order');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_PROGRAM_PRODUCT', 'ADMIN', 'Permission to manage program products', '2016-02-03 14:37:33.428452', 3, 'right.manage.program.product');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_DISTRIBUTION', 'ALLOCATION', 'Permission to manage an distribution', '2016-02-03 14:37:33.428452', 9, 'right.manage.distribution');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('SYSTEM_SETTINGS', 'ADMIN', 'Permission to configure Electronic Data Interchange (EDI)', '2016-02-03 14:37:33.428452', 18, 'right.system.settings');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_REGIMEN_TEMPLATE', 'ADMIN', 'Permission to manage a regimen template', '2016-02-03 14:37:33.428452', 4, 'right.manage.regimen.template');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('FACILITY_FILL_SHIPMENT', 'FULFILLMENT', 'Permission to fill shipment data for facility', '2016-02-03 14:37:33.428452', 19, 'right.fulfillment.fill.shipment');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_POD', 'FULFILLMENT', 'Permission to manage proof of delivery', '2016-02-03 14:37:33.428452', 20, 'right.fulfillment.manage.pod');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_GEOGRAPHIC_ZONE', 'ADMIN', 'Permission to manage geographic zones', '2016-02-03 14:37:34.828934', 23, 'right.manage.geo.zone');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SUPPLY_LINE', 'ADMIN', 'Permission to manage supply lines', '2016-02-03 14:37:34.841957', 25, 'right.manage.supply.line');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_FACILITY_APPROVED_PRODUCT', 'ADMIN', 'Permission to manage facility approved products', '2016-02-03 14:37:34.854567', 26, 'right.manage.facility.approved.products');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_REPORT', 'REPORTING', 'Permission to manage reports', '2016-02-03 14:37:33.428452', 10, 'right.manage.report');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_ORDER_REPORT', 'REPORT', 'Permission to view Order Report', '2016-02-03 14:37:35.39466', NULL, 'right.report.order');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_RNR_FEEDBACK_REPORT', 'REPORT', 'Permission to view Report and Requisition Feedback Report.', '2016-02-03 14:37:35.477029', NULL, 'right.report.rnr.feedback');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_ADJUSTMENT_SUMMARY_REPORT', 'REPORT', 'Permission to view adjustment summary Report', '2016-02-03 14:37:35.273896', NULL, 'right.report.adjustment');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_REPORTING_RATE_REPORT', 'REPORT', 'Permission to view Reporting Rate Report', '2016-02-03 14:37:33.67177', NULL, 'right.report.reporting.rate');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('ACCESS_ILS_GATEWAY', 'ADMIN', 'Permission to access the ILS Gateway.', '2016-02-03 14:37:35.499313', 38, 'right.ils');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_DONOR', 'ADMIN', 'Permission to manage donors.', '2016-02-03 14:37:36.1332', 42, 'right.manage.donor');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_PRODUCT', 'ADMIN', 'Permission to manage products.', '2016-02-03 14:37:35.399887', 27, 'right.manage.products');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_REQUISITION_GROUP', 'ADMIN', 'Permission to manage requisition groups.', '2016-02-03 14:37:35.388873', 24, 'right.manage.requisition.group');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SUPERVISORY_NODE', 'ADMIN', 'Permission to manage supervisory nodes.', '2016-02-03 14:37:35.411088', 8, 'right.manage.supervisory.node');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_AVERAGE_CONSUMPTION_REPORT', 'REPORT', 'Permission to view avergae consumption Report', '2016-02-03 14:37:35.266075', NULL, 'right.report.average.consumption');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_SUPPLY_STATUS_REPORT', 'REPORT', 'Permission to view supply status by facility report', '2016-02-03 14:37:35.352732', NULL, 'right.report.supply.status');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_CONSUMPTION_REPORT', 'REPORT', 'Permission to view Consumption Report', '2016-02-03 14:37:33.67177', NULL, 'right.report.consumption');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_DISTRICT_CONSUMPTION_REPORT', 'REPORT', 'Permission to view district consumption comparison report', '2016-02-03 14:37:35.371168', NULL, 'right.report.district.consumption');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_MAILING_LABEL_REPORT', 'REPORT', 'Permission to view Mailing labels for Facilities', '2016-02-03 14:37:33.67177', NULL, 'right.report.mailing.label');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_STOCKED_OUT_REPORT', 'REPORT', 'Permission to view stocked out commodity Report', '2016-02-03 14:37:35.27938', NULL, 'right.report.stocked.out');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_FACILITY_REPORT', 'REPORT', 'Permission to view Facility List Report', '2016-02-03 14:37:33.67177', NULL, 'right.report.facility');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_SUMMARY_REPORT', 'REPORT', 'Permission to view Summary Report', '2016-02-03 14:37:33.679038', NULL, 'right.report.summary');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_ORDER_FILL_RATE_REPORT', 'REPORT', 'Permission to view Order Fill Rate Report', '2016-02-03 14:37:35.568227', NULL, 'right.report.order.fillrate');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_REGIMEN_SUMMARY_REPORT', 'REPORT', 'Permission to view Regimen Summary Report.', '2016-02-03 14:37:35.592945', NULL, 'right.report.regimen');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT', 'REPORT', 'Permission to view District Financial Summary Report', '2016-02-03 14:37:35.61753', NULL, 'right.report.district.financial');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_DASHBOARD_POC', 'REPORT', 'Permission to view dashboard poc', '2016-02-03 14:37:35.645254', NULL, 'right.report.dashboard');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_USER_SUMMARY_REPORT', 'REPORT', 'Permission to view user summary Report', '2016-02-03 14:37:35.828583', NULL, 'right.report.user.summary');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_STOCK_IMBALANCE_REPORT', 'ADMIN', 'Permission to view Stock Imbalance Report.', '2016-02-03 14:37:35.416782', NULL, 'right.report.stock.imbalance');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_DEMOGRAPHIC_PARAMETERS', 'ADMIN', 'Permission to manage demographic parameters', '2016-02-03 14:37:38.032809', 60, 'right.manage.demographic.parameters');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_LAB_EQUIPMENT_LIST_REPORT', 'REPORT', 'Permission to view lab equipment list Report', '2016-02-03 14:37:36.156266', NULL, 'right.report.lab.equipment');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_LAB_EQUIPMENTS_BY_FUNDING_SOURCE', 'REPORT', 'Permission to view lab equipment list by funding source Report', '2016-02-03 14:37:36.185272', NULL, 'right.report.equipment.funding');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_ORDER_FILL_RATE_SUMMARY_REPORT', 'REPORT', 'Permission to view order fill rate summary Report.', '2016-02-03 14:37:36.745599', NULL, 'right.report.fill.rate');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_LAB_EQUIPMENTS_BY_LOCATION_REPORT', 'REPORT', 'Permission to view lab equipments by location Report', '2016-02-03 14:37:36.846671', NULL, 'right.report.lab.equipment.by.location');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_TIMELINESS_REPORT', 'REPORT', 'Permission to view Timeliness Report', '2016-02-03 14:37:37.2451', NULL, 'right.report.timeliness');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_SEASONALITY_RATIONING_REPORT', 'REPORT', 'Permission to view seasonality rationing Report', '2016-02-03 14:37:37.304588', NULL, 'right.report.seasonality.rationing');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_PIPELINE_EXPORT', 'REPORT', 'Permission to view Pipeline export Report', '2016-02-03 14:37:37.435553', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_CCE_STORAGE_CAPACITY_REPORT', 'REPORT', 'Permission to view CCE Storage Capacity Report', '2016-02-03 14:37:38.097477', NULL, 'right.report.cce.storage.capacity');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT', 'REPORT', 'Permission to view cold chain equipment list Report', '2016-02-03 14:37:38.125762', NULL, 'right.report.coldchain.equipment');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_REPAIR_MANAGEMENT_REPORT', 'REPORT', 'Permission to view Repair Management Report', '2016-02-03 14:37:38.247552', NULL, 'right.report.repair.management');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY', 'REPORT', 'Permission to View Replacement Plan Summary Report', '2016-02-03 14:37:38.282044', NULL, 'right.report.vaccine.replacement.plan.summary');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_CUSTOM_REPORTS', 'REPORT', 'Permission to manage custom reports', '2016-02-03 14:37:38.39212', NULL, 'right.report.manage.custom.report');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('ACCESS_NEW_DASHBOARD', 'REPORT', 'Permission to access new dashboard', '2016-02-03 14:37:38.448606', 13, 'right.dashboard');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_VACCINE_DISEASE_LIST', 'ADMIN', 'Permission to manage vaccine disease list', '2016-02-03 14:37:38.564711', 200, 'right.admin.vaccine.disease');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SETTING', 'ADMIN', 'Permission to configure settings.', '2016-02-03 14:37:35.433777', 30, 'right.admin.configuration');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SUPPLYLINE', 'ADMIN', 'Permission to create and edit Supply Line', '2016-02-03 14:37:35.378187', 31, 'right.manage.supplyline');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_ELMIS_INTERFACE', 'ADMIN', 'Permission to manage ELMIS interface apps setting', '2016-02-03 14:37:38.477623', 32, 'right.admin.elmis.interface');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('CONFIGURE_HELP_CONTENT', 'ADMIN', 'Permission to Configure Help Content', '2016-02-03 14:37:37.442008', 33, 'right.admin.help.content');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_EQUIPMENT_SETTINGS', 'ADMIN', 'Permission to manage equipment settings', '2016-02-03 14:37:35.840828', 40, 'right.admin.equipment');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('SERVICE_VENDOR_RIGHT', 'ADMIN', 'Permission to use system as service Vendor', '2016-02-03 14:37:36.107454', 41, 'right.manage.service.vendor');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SEASONALITY_RATIONING', 'ADMIN', 'Permission to manage seasonality rationing ', '2016-02-03 14:37:37.311228', 50, 'right.admin.seasonality.rationing');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('DELETE_REQUISITION', 'REQUISITION', 'Permission to delete requisitions', '2016-02-03 14:37:35.630223', 17, 'right.delete.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_EQUIPMENT_INVENTORY', 'REQUISITION', 'Permission to manage equipment inventory for each facility', '2016-02-03 14:37:35.840828', 20, 'right.manage.equipment.inventory');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_DEMOGRAPHIC_ESTIMATES', 'REQUISITION', 'Permission to manage demographic estimates', '2016-02-03 14:37:38.032809', 30, 'right.manage.demographic.estimates');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('FINALIZE_DEMOGRAPHIC_ESTIMATES', 'REQUISITION', 'Permission to finalize demographic estimates', '2016-02-03 14:37:38.575701', 31, 'right.demographic.estimate.finalize');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('UNLOCK_FINALIZED_DEMOGRAPHIC_ESTIMATES', 'REQUISITION', 'Permission to unlock finalized demographic estimates', '2016-02-03 14:37:38.575701', 32, 'right.demographic.estimate.unlock.finalized');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('CREATE_IVD', 'REQUISITION', 'Permission to create ivd form', '2016-02-03 14:37:38.593536', 50, 'right.vaccine.create.ivd');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('APPROVE_IVD', 'REQUISITION', 'Permission to Approve ivd form', '2016-02-03 14:37:38.593536', 51, 'right.vaccine.approve.ivd');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_IVD', 'REQUISITION', 'Permission to view ivd reports', '2016-02-03 14:37:38.593536', 52, 'right.vaccine.view.ivd');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_STOCK_ON_HAND', 'REQUISITION', 'Permission to view stock on hand', '2016-02-03 14:37:38.902874', NULL, 'right.view.stock.on.hand');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_VACCINE_PRODUCTS_CONFIGURATION', 'ADMIN', 'Permission to manage vaccine product configuration', '2016-02-03 14:37:38.902874', NULL, 'right.manage.vaccine.product.configuration');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_ORDER_REQUISITION', 'REQUISITION', 'Permission to view Order Requisition', '2016-02-03 14:37:38.932105', NULL, 'right.view.order.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_PENDING_REQUEST', 'REQUISITION', 'Permission to View Pending Request', '2016-02-03 14:37:38.932105', NULL, 'right.view.pending.request');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('CREATE_ORDER_REQUISITION', 'REQUISITION', 'Permission to Create Requisition', '2016-02-03 14:37:38.932105', NULL, 'right.create.order.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MASS_DISTRIBUTION', 'REQUISITION', 'Permission to do mass distribution', '2016-02-03 14:37:39.399291', NULL, 'right.mass.distribution');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_SUPERVISED_EQUIPMENTS', 'REQUISITION', 'Permission to manage equipment inventory for supervised facility', '2016-02-03 14:37:39.399291', NULL, 'right.manage.supervised.equipments');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_STOCK', 'REQUISITION', 'Permission to manage stock (issue/receive/adjust)', '2016-02-03 14:37:39.427575', NULL, 'right.manage.stock');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_REQUISITION_REPORT', 'REPORT', 'Permission to View Requisitions Report', '2016-02-03 14:37:39.49164', NULL, 'right.report.requisition');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_STOCK_ON_HAND_REPORT', 'REPORT', 'Permission to View Stock On Hand Report', '2016-02-03 14:37:39.49164', NULL, 'right.report.stockonhand');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Print Order Requisition', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Print Issue report', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('vims_distribution', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('print_vaccine_Order_Requisition', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Facilities Missing Supporting Requisition Group', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Facilities Missing Create Requisition Role', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Facilities Missing Authorize Requisition Role', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Supervisory Nodes Missing Approve Requisition Role', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Requisition Groups Missing Supply Line', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Order Routing Inconsistencies', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('Delivery Zones Missing Manage Distribution Role', 'REPORTING', NULL, '2016-02-03 14:37:39.641001', NULL, NULL);
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_CUSTOM_REPORTS', 'REPORT', 'Permission to view Custom Reports in Reports menu', '2016-02-04 14:56:56.057737', NULL, 'right.reports.custom');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_FACILITY_INTEGRATION', 'ADMIN', 'Permission to view facility integration section', '2016-02-04 14:56:56.057737', NULL, 'right.view.facility.integration');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_PRODUCT_RATIONING', 'ADMIN', 'Permission to view product rationing column', '2016-02-04 14:56:56.057737', NULL, 'right.view.product.rationing');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_EQUIPMENT_SERVICING', 'ADMIN', 'Permission to manage service vendors and contracts', '2016-02-04 14:56:56.057737', NULL, 'right.manage.equipment.servicing');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('VIEW_HELP', 'ADMIN', 'Permission to view help link', '2016-02-04 14:56:56.057737', NULL, 'right.view.help');
INSERT INTO rights (name, righttype, description, createddate, displayorder, displaynamekey) VALUES ('MANAGE_IVD_TEMPLATES', 'REQUISITION', 'Permission to manage IVD tabs in R&R template', '2016-02-04 14:56:56.057737', NULL, 'right.manage.ivd.templates');


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO roles (id, name, description, createdby, createddate, modifiedby, modifieddate) VALUES (1, 'Admin', 'Admin', NULL, '2016-02-03 14:37:33.439729', NULL, '2016-02-03 14:37:33.439729');


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('roles_id_seq', 1, true);


--
-- Data for Name: role_rights; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'UPLOADS', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'MANAGE_FACILITY', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'MANAGE_ROLE', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'MANAGE_PROGRAM_PRODUCT', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'MANAGE_SCHEDULE', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'CONFIGURE_RNR', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'MANAGE_USER', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'SYSTEM_SETTINGS', NULL, '2016-02-03 14:37:33.439729');
INSERT INTO role_rights (roleid, rightname, createdby, createddate) VALUES (1, 'MANAGE_REGIMEN_TEMPLATE', NULL, '2016-02-03 14:37:33.439729');


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO users (id, username, password, firstname, lastname, employeeid, restrictlogin, jobtitle, primarynotificationmethod, officephone, cellphone, email, supervisorid, facilityid, verified, active, createdby, createddate, modifiedby, modifieddate, ismobileuser) VALUES (1, 'Admin123', 'TQskzK3iiLfbRVHeM1muvBCiiKriibfl6lh8ipo91hb74G3OvsybvkzpPI4S3KIeWTXAiiwlUU0iiSxWii4wSuS8mokSAieie', 'John', 'Doe', NULL, false, NULL, NULL, NULL, NULL, 'John_Doe@openlmis.com', NULL, NULL, true, true, NULL, '2016-02-03 14:37:33.439729', NULL, '2016-02-03 14:37:33.439729', false);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('users_id_seq', 1, true);


--
-- Data for Name: role_assignments; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO role_assignments (userid, roleid, programid, supervisorynodeid, deliveryzoneid) VALUES (1, 1, NULL, NULL, NULL);


--
-- Name: role_assignments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('role_assignments_id_seq', 1, true);


INSERT INTO templates (id, name, data, createdby, createddate, type, description) VALUES (2, 'Print POD', '\xaced0005737200286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a61737065725265706f727400000000000027d80200034c000b636f6d70696c65446174617400164c6a6176612f696f2f53657269616c697a61626c653b4c0011636f6d70696c654e616d655375666669787400124c6a6176612f6c616e672f537472696e673b4c000d636f6d70696c6572436c61737371007e00027872002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655265706f727400000000000027d802002a49001950534555444f5f53455249414c5f56455253494f4e5f55494449000c626f74746f6d4d617267696e49000b636f6c756d6e436f756e7449000d636f6c756d6e53706163696e6749000b636f6c756d6e57696474685a001069676e6f7265506167696e6174696f6e5a00136973466c6f6174436f6c756d6e466f6f7465725a0010697353756d6d6172794e6577506167655a0020697353756d6d6172795769746850616765486561646572416e64466f6f7465725a000e69735469746c654e65775061676549000a6c6566744d617267696e42000b6f7269656e746174696f6e49000a7061676548656967687449000970616765576964746842000a7072696e744f7264657249000b72696768744d617267696e490009746f704d617267696e42000e7768656e4e6f44617461547970654c000a6261636b67726f756e647400244c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5242616e643b4c000f636f6c756d6e446972656374696f6e7400334c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f52756e446972656374696f6e456e756d3b4c000c636f6c756d6e466f6f74657271007e00044c000c636f6c756d6e48656164657271007e00045b000864617461736574737400285b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52446174617365743b4c000c64656661756c745374796c657400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525374796c653b4c000664657461696c71007e00044c000d64657461696c53656374696f6e7400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5253656374696f6e3b4c0012666f726d6174466163746f7279436c61737371007e00024c000a696d706f72747353657474000f4c6a6176612f7574696c2f5365743b4c00086c616e677561676571007e00024c000e6c61737450616765466f6f74657271007e00044c000b6d61696e446174617365747400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52446174617365743b4c00046e616d6571007e00024c00066e6f4461746171007e00044c00106f7269656e746174696f6e56616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4f7269656e746174696f6e456e756d3b4c000a70616765466f6f74657271007e00044c000a7061676548656164657271007e00044c000f7072696e744f7264657256616c75657400314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5072696e744f72646572456e756d3b5b00067374796c65737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525374796c653b4c000773756d6d61727971007e00045b000974656d706c6174657374002f5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525265706f727454656d706c6174653b4c00057469746c6571007e00044c00137768656e4e6f446174615479706556616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5768656e4e6f4461746154797065456e756d3b78700000c3540000000000000001000000000000034a00010000000000000000000002530000034a000000000000000000007372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736542616e6400000000000027d802000749001950534555444f5f53455249414c5f56455253494f4e5f5549444900066865696768745a000e697353706c6974416c6c6f7765644c00137072696e745768656e45787072657373696f6e74002a4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5245787072657373696f6e3b4c000d70726f706572746965734d617074002d4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250726f706572746965734d61703b4c000973706c6974547970657400104c6a6176612f6c616e672f427974653b4c000e73706c69745479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f53706c697454797065456e756d3b787200336e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365456c656d656e7447726f757000000000000027d80200024c00086368696c6472656e7400104c6a6176612f7574696c2f4c6973743b4c000c656c656d656e7447726f757074002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52456c656d656e7447726f75703b7870737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a6578700000000077040000000078700000c35400000000017070707e72002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e53706c697454797065456e756d00000000000000001200007872000e6a6176612e6c616e672e456e756d00000000000000001200007870740007535452455443487e7200316e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e52756e446972656374696f6e456e756d00000000000000001200007871007e001d7400034c545270707070707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736553656374696f6e00000000000027d80200015b000562616e64737400255b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5242616e643b7870757200255b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5242616e643b95dd7eec8cca85350200007870000000017371007e00117371007e001a00000001770400000001737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655375627265706f727400000000000027d80200084c0014636f6e6e656374696f6e45787072657373696f6e71007e00124c001464617461536f7572636545787072657373696f6e71007e00124c000a65787072657373696f6e71007e00124c000c69735573696e6743616368657400134c6a6176612f6c616e672f426f6f6c65616e3b5b000a706172616d65746572737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525375627265706f7274506172616d657465723b4c0017706172616d65746572734d617045787072657373696f6e71007e00125b000c72657475726e56616c7565737400355b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525375627265706f727452657475726e56616c75653b4c000b72756e546f426f74746f6d71007e002b7872002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365456c656d656e7400000000000027d802001b49001950534555444f5f53455249414c5f56455253494f4e5f5549444900066865696768745a001769735072696e74496e466972737457686f6c6542616e645a001569735072696e74526570656174656456616c7565735a001a69735072696e745768656e44657461696c4f766572666c6f77735a0015697352656d6f76654c696e655768656e426c616e6b42000c706f736974696f6e5479706542000b7374726574636854797065490005776964746849000178490001794c00096261636b636f6c6f727400104c6a6176612f6177742f436f6c6f723b4c001464656661756c745374796c6550726f76696465727400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5244656661756c745374796c6550726f76696465723b4c000c656c656d656e7447726f757071007e00184c0009666f7265636f6c6f7271007e002f4c00036b657971007e00024c00046d6f646571007e00144c00096d6f646556616c756574002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4d6f6465456e756d3b4c000b706172656e745374796c6571007e00074c0018706172656e745374796c654e616d655265666572656e636571007e00024c0011706f736974696f6e5479706556616c75657400334c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f506f736974696f6e54797065456e756d3b4c00137072696e745768656e45787072657373696f6e71007e00124c00157072696e745768656e47726f75704368616e6765737400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5247726f75703b4c000d70726f706572746965734d617071007e00135b001370726f706572747945787072657373696f6e737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250726f706572747945787072657373696f6e3b4c0010737472657463685479706556616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5374726574636854797065456e756d3b4c0004757569647400104c6a6176612f7574696c2f555549443b78700000c354000000320001000000000000034bffffffff000000017071007e001071007e00287070707070707e7200316e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e506f736974696f6e54797065456e756d00000000000000001200007871007e001d7400134649585f52454c41544956455f544f5f544f50707070707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5374726574636854797065456e756d00000000000000001200007871007e001d74000a4e4f5f535452455443487372000e6a6176612e7574696c2e55554944bc9903f7986d852f0200024a000c6c65617374536967426974734a000b6d6f7374536967426974737870a29bbc0f9ddc781691c47d3bfabe42d7737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736545787072657373696f6e00000000000027d802000449000269645b00066368756e6b737400305b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5245787072657373696f6e4368756e6b3b4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e0002787000000020757200305b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5245787072657373696f6e4368756e6b3b6d59cfde694ba355020000787000000001737200366e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736545787072657373696f6e4368756e6b00000000000027d8020002420004747970654c00047465787471007e00027870027400115245504f52545f434f4e4e454354494f4e7070707371007e0040000000217571007e0043000000037371007e0045017400234a6173706572436f6d70696c654d616e616765722e636f6d70696c655265706f7274287371007e00450274000d7375627265706f72745f6469727371007e00450174001c202b2022706f644c696e654974656d5072696e742e6a72786d6c2229707070757200335b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525375627265706f7274506172616d657465723b5b039ca387c0be42020000787000000004737200396e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655375627265706f7274506172616d6574657200000000000027d8020000787200376e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736544617461736574506172616d6574657200000000000027d80200024c000a65787072657373696f6e71007e00124c00046e616d6571007e000278707371007e00400000001c7571007e0043000000017371007e004502740006706f645f69647070740006706f645f69647371007e00527371007e00400000001d7571007e0043000000017371007e004502740009696d6167655f6469727070740009696d6167655f6469727371007e00527371007e00400000001e7571007e0043000000017371007e00450274000d5245504f52545f4c4f43414c45707074000d5245504f52545f4c4f43414c457371007e00527371007e00400000001f7571007e0043000000017371007e0045027400165245504f52545f5245534f555243455f42554e444c4570707400165245504f52545f5245534f555243455f42554e444c4570707078700000c35400000033017070707070707400046a6176617371007e00117371007e001a000000017704000000017372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365427265616b00000000000027d802000349001950534555444f5f53455249414c5f56455253494f4e5f554944420004747970654c00097479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f427265616b54797065456e756d3b7871007e002e0000c354000000010001000000000000034a00000000000000037071007e001071007e006d70707070707071007e00397070707071007e003c7371007e003eb1195b40b96e24dd20d9368b1c7647aa0000c354007e72002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e427265616b54797065456e756d00000000000000001200007871007e001d7400045041474578700000c3540000000801707070707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654461746173657400000000000027d802001149001950534555444f5f53455249414c5f56455253494f4e5f5549445a000669734d61696e4200177768656e5265736f757263654d697373696e67547970655b00066669656c64737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a524669656c643b4c001066696c74657245787072657373696f6e71007e00125b000667726f7570737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5247726f75703b4c00046e616d6571007e00025b000a706172616d657465727374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52506172616d657465723b4c000d70726f706572746965734d617071007e00134c000571756572797400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5251756572793b4c000e7265736f7572636542756e646c6571007e00024c000e7363726970746c6574436c61737371007e00025b000a7363726970746c65747374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525363726970746c65743b5b000a736f72744669656c647374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52536f72744669656c643b4c00047575696471007e00365b00097661726961626c65737400295b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525661726961626c653b4c001c7768656e5265736f757263654d697373696e675479706556616c756574003e4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5768656e5265736f757263654d697373696e6754797065456e756d3b78700000c3540100757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a524669656c643b023cdfc74e2af27002000078700000000e7372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654669656c6400000000000027d80200054c000b6465736372697074696f6e71007e00024c00046e616d6571007e00024c000d70726f706572746965734d617071007e00134c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e000278707074000c7265636569766564646174657372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5250726f706572746965734d617000000000000027d80200034c00046261736571007e00134c000e70726f706572746965734c69737471007e00174c000d70726f706572746965734d617074000f4c6a6176612f7574696c2f4d61703b787070707074000e6a6176612e7574696c2e44617465707371007e00827074000a726563656976656462797371007e00857070707400106a6176612e6c616e672e537472696e67707371007e00827074000b64656c69766572656462797371007e00857070707400106a6176612e6c616e672e537472696e67707371007e0082707400076f7264657269647371007e008570707074000e6a6176612e6c616e672e4c6f6e67707371007e00827074000b63726561746564646174657371007e008570707074000e6a6176612e7574696c2e44617465707371007e008270740008666163696c6974797371007e00857070707400106a6176612e6c616e672e537472696e67707371007e008270740004747970657371007e00857070707400106a6176612e6c616e672e537472696e67707371007e00827074000e737570706c79696e676465706f747371007e00857070707400106a6176612e6c616e672e537472696e67707371007e00827074000770726f6772616d7371007e00857070707400106a6176612e6c616e672e537472696e67707371007e0082707400097374617274646174657371007e008570707074000e6a6176612e7574696c2e44617465707371007e008270740007656e64646174657371007e008570707074000e6a6176612e7574696c2e44617465707371007e008270740015746f74616c7175616e7469747972657475726e65647371007e00857070707400116a6176612e6c616e672e496e7465676572707371007e008270740014746f74616c7175616e74697479736869707065647371007e00857070707400116a6176612e6c616e672e496e7465676572707371007e008270740015746f74616c7175616e7469747972656365697665647371007e00857070707400116a6176612e6c616e672e496e74656765727070757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5247726f75703b40a35f7a4cfd78ea0200007870000000017372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736547726f757000000000000027d802001049001950534555444f5f53455249414c5f56455253494f4e5f55494442000e666f6f746572506f736974696f6e5a0019697352657072696e744865616465724f6e45616368506167655a001169735265736574506167654e756d6265725a0010697353746172744e6577436f6c756d6e5a000e697353746172744e6577506167655a000c6b656570546f6765746865724900176d696e486569676874546f53746172744e6577506167654c000d636f756e745661726961626c657400284c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525661726961626c653b4c000a65787072657373696f6e71007e00124c0013666f6f746572506f736974696f6e56616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f466f6f746572506f736974696f6e456e756d3b4c000b67726f7570466f6f74657271007e00044c001267726f7570466f6f74657253656374696f6e71007e00084c000b67726f757048656164657271007e00044c001267726f757048656164657253656374696f6e71007e00084c00046e616d6571007e000278700000c354000000000000000000007372002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655661726961626c6500000000000027d802001149001950534555444f5f53455249414c5f56455253494f4e5f55494442000b63616c63756c6174696f6e42000d696e6372656d656e74547970655a000f697353797374656d446566696e65644200097265736574547970654c001063616c63756c6174696f6e56616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f43616c63756c6174696f6e456e756d3b4c000a65787072657373696f6e71007e00124c000e696e6372656d656e7447726f757071007e00334c0012696e6372656d656e745479706556616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f496e6372656d656e7454797065456e756d3b4c001b696e6372656d656e746572466163746f7279436c6173734e616d6571007e00024c001f696e6372656d656e746572466163746f7279436c6173735265616c4e616d6571007e00024c0016696e697469616c56616c756545787072657373696f6e71007e00124c00046e616d6571007e00024c000a726573657447726f757071007e00334c000e72657365745479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f526573657454797065456e756d3b4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e00027870000077ee000001007e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e43616c63756c6174696f6e456e756d00000000000000001200007871007e001d740005434f554e547371007e00400000000b7571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e74656765722831297070707e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e496e6372656d656e7454797065456e756d00000000000000001200007871007e001d7400044e4f4e4570707371007e00400000000c7571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c4865616465725f434f554e5471007e00c27e72002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e526573657454797065456e756d00000000000000001200007871007e001d74000547524f55507400116a6176612e6c616e672e496e746567657270707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e466f6f746572506f736974696f6e456e756d00000000000000001200007871007e001d7400064e4f524d414c707371007e002370707371007e00237571007e0026000000027371007e00117371007e001a00000002770400000002737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365546578744669656c6400000000000027d802001549001950534555444f5f53455249414c5f56455253494f4e5f55494449000d626f6f6b6d61726b4c6576656c42000e6576616c756174696f6e54696d6542000f68797065726c696e6b54617267657442000d68797065726c696e6b547970655a0015697353747265746368576974684f766572666c6f774c0014616e63686f724e616d6545787072657373696f6e71007e00124c000f6576616c756174696f6e47726f757071007e00334c00136576616c756174696f6e54696d6556616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4576616c756174696f6e54696d65456e756d3b4c000a65787072657373696f6e71007e00124c001968797065726c696e6b416e63686f7245787072657373696f6e71007e00124c001768797065726c696e6b5061676545787072657373696f6e71007e00125b001368797065726c696e6b506172616d65746572737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5248797065726c696e6b506172616d657465723b4c001c68797065726c696e6b5265666572656e636545787072657373696f6e71007e00124c001a68797065726c696e6b546f6f6c74697045787072657373696f6e71007e00124c001768797065726c696e6b5768656e45787072657373696f6e71007e00124c000f6973426c616e6b5768656e4e756c6c71007e002b4c000a6c696e6b54617267657471007e00024c00086c696e6b5479706571007e00024c00077061747465726e71007e00024c00117061747465726e45787072657373696f6e71007e0012787200326e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736554657874456c656d656e7400000000000027d802002549001950534555444f5f53455249414c5f56455253494f4e5f5549444c0006626f7264657271007e00144c000b626f72646572436f6c6f7271007e002f4c000c626f74746f6d426f7264657271007e00144c0011626f74746f6d426f72646572436f6c6f7271007e002f4c000d626f74746f6d50616464696e677400134c6a6176612f6c616e672f496e74656765723b4c0008666f6e744e616d6571007e00024c0008666f6e7453697a6571007e00e74c0013686f72697a6f6e74616c416c69676e6d656e7471007e00144c0018686f72697a6f6e74616c416c69676e6d656e7456616c75657400364c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f486f72697a6f6e74616c416c69676e456e756d3b4c00066973426f6c6471007e002b4c000869734974616c696371007e002b4c000d6973506466456d62656464656471007e002b4c000f6973537472696b655468726f75676871007e002b4c000c69735374796c65645465787471007e002b4c000b6973556e6465726c696e6571007e002b4c000a6c656674426f7264657271007e00144c000f6c656674426f72646572436f6c6f7271007e002f4c000b6c65667450616464696e6771007e00e74c00076c696e65426f787400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a524c696e65426f783b4c000b6c696e6553706163696e6771007e00144c00106c696e6553706163696e6756616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e6553706163696e67456e756d3b4c00066d61726b757071007e00024c000770616464696e6771007e00e74c00097061726167726170687400294c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525061726167726170683b4c000b706466456e636f64696e6771007e00024c000b706466466f6e744e616d6571007e00024c000b7269676874426f7264657271007e00144c00107269676874426f72646572436f6c6f7271007e002f4c000c726967687450616464696e6771007e00e74c0008726f746174696f6e71007e00144c000d726f746174696f6e56616c756574002f4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f526f746174696f6e456e756d3b4c0009746f70426f7264657271007e00144c000e746f70426f72646572436f6c6f7271007e002f4c000a746f7050616464696e6771007e00e74c0011766572746963616c416c69676e6d656e7471007e00144c0016766572746963616c416c69676e6d656e7456616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f566572746963616c416c69676e456e756d3b7871007e002e0000c3540000002d000100000000000002d80000000d000000057071007e001071007e00e170707070707071007e0039707070707e71007e003b74001a52454c41544956455f544f5f54414c4c4553545f4f424a4543547371007e003e8f61a735ab2074b7212194e972ca43210000c354707070707074000953616e735365726966737200116a6176612e6c616e672e496e746567657212e2a0a4f781873802000149000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b02000078700000001870707070707070737200116a6176612e6c616e672e426f6f6c65616ecd207280d59cfaee0200015a000576616c75657870007070707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654c696e65426f7800000000000027d802000b4c000d626f74746f6d50616464696e6771007e00e74c0009626f74746f6d50656e74002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f626173652f4a52426f7850656e3b4c000c626f78436f6e7461696e657274002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52426f78436f6e7461696e65723b4c000b6c65667450616464696e6771007e00e74c00076c65667450656e71007e00f94c000770616464696e6771007e00e74c000370656e71007e00f94c000c726967687450616464696e6771007e00e74c0008726967687450656e71007e00f94c000a746f7050616464696e6771007e00e74c0006746f7050656e71007e00f9787070737200336e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78426f74746f6d50656e00000000000027d80200007872002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f7850656e00000000000027d80200014c00076c696e65426f7871007e00e97872002a6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736550656e00000000000027d802000649001950534555444f5f53455249414c5f56455253494f4e5f5549444c00096c696e65436f6c6f7271007e002f4c00096c696e655374796c6571007e00144c000e6c696e655374796c6556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e655374796c65456e756d3b4c00096c696e6557696474687400114c6a6176612f6c616e672f466c6f61743b4c000c70656e436f6e7461696e657274002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250656e436f6e7461696e65723b78700000c3547070707071007e00fb71007e00fb71007e00ee70737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f784c65667450656e00000000000027d80200007871007e00fd0000c3547070707071007e00fb71007e00fb707371007e00fd0000c3547070707071007e00fb71007e00fb70737200326e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78526967687450656e00000000000027d80200007871007e00fd0000c3547070707071007e00fb71007e00fb70737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78546f7050656e00000000000027d80200007871007e00fd0000c3547070707071007e00fb71007e00fb70707070737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736550617261677261706800000000000027d802000a4c000f66697273744c696e65496e64656e7471007e00e74c000a6c656674496e64656e7471007e00e74c000b6c696e6553706163696e6771007e00ea4c000f6c696e6553706163696e6753697a6571007e01004c0012706172616772617068436f6e7461696e65727400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52506172616772617068436f6e7461696e65723b4c000b7269676874496e64656e7471007e00e74c000c73706163696e67416674657271007e00e74c000d73706163696e674265666f726571007e00e74c000c74616253746f70576964746871007e00e74c000874616253746f707371007e001778707070707071007e00ee707070707070707070707070707070707e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e566572746963616c416c69676e456e756d00000000000000001200007871007e001d7400064d4944444c450000c354000000000000000170707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4576616c756174696f6e54696d65456e756d00000000000000001200007871007e001d7400034e4f577371007e00400000000d7571007e0043000000057371007e0045017400046d7367287371007e00450574001b6c6162656c2e70726f6f662e6f662e64656c69766572792e666f727371007e0045017400022c207371007e00450374000770726f6772616d7371007e00450174000129707070707070707070707070707371007e00e30000c3540000001600010000000000000039000002f20000000a7071007e001071007e00e17070707070707e71007e0038740005464c4f41547070707071007e003c7371007e003e89ab02f2dda79bb52dd094dce4b543c00000c354707070707074000953616e7353657269667371007e00f300000008707e7200346e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e486f72697a6f6e74616c416c69676e456e756d00000000000000001200007871007e001d74000552494748547070707070707070707371007e00f8707371007e00fc0000c3547070707071007e012871007e012871007e011f707371007e01030000c3547070707071007e012871007e0128707371007e00fd0000c3547070707071007e012871007e0128707371007e01060000c3547070707071007e012871007e0128707371007e01080000c3547070707071007e012871007e0128707070707371007e010a7070707071007e011f70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000000e7571007e0043000000017371007e0045017400146e6577206a6176612e7574696c2e446174652829707070707070707070707074000a64642f4d4d2f797979797078700000c3540000003201707070707371007e00117371007e001a00000015770400000015737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736552656374616e676c6500000000000027d80200014c000672616469757371007e00e7787200356e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736547726170686963456c656d656e7400000000000027d802000549001950534555444f5f53455249414c5f56455253494f4e5f5549444c000466696c6c71007e00144c000966696c6c56616c756574002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f46696c6c456e756d3b4c00076c696e6550656e7400234c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250656e3b4c000370656e71007e00147871007e002e0000c354000000320001000000000000032f0000000b000000007071007e001071007e01347070707e7200296e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4d6f6465456e756d00000000000000001200007871007e001d74000b5452414e53504152454e54707071007e00397070707071007e00ef7371007e003ea3af16f19527213476726b22ed2c4378000077ee70707371007e00fe0000c3547070707372000f6a6176612e6c616e672e466c6f6174daedc9a2db3cf0ec02000146000576616c75657871007e00f43f80000071007e013a70707371007e00e30000c3540000000a000100000000000000480000001c000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e86d00d0bb793f7dffbc318b89cbc4b440000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e014471007e014471007e0142707371007e01030000c3547070707071007e014471007e0144707371007e00fd0000c3547070707071007e014471007e0144707371007e01060000c3547070707071007e014471007e0144707371007e01080000c3547070707071007e014471007e0144707070707371007e010a7070707071007e014270707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000000f7571007e0043000000017371007e00450574000e6c6162656c2e6f726465722e6e6f707070707070707070707070707371007e00e30000c3540000000a000100000000000000480000001c000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eb25101f05f2ae792290c6493c0dd41f40000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e015171007e015171007e014f707371007e01030000c3547070707071007e015171007e0151707371007e00fd0000c3547070707071007e015171007e0151707371007e01060000c3547070707071007e015171007e0151707371007e01080000c3547070707071007e015171007e0151707070707371007e010a7070707071007e014f70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000107571007e0043000000017371007e00450574001e6c6162656c2e666163696c6974792e7265706f7274696e67506572696f64707070707070707070707070707371007e00e30000c3540000000a000100000000000000480000010e000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003ead2d6244c73ce77b7c75268cc1bf46450000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e015e71007e015e71007e015c707371007e01030000c3547070707071007e015e71007e015e707371007e00fd0000c3547070707071007e015e71007e015e707371007e01060000c3547070707071007e015e71007e015e707371007e01080000c3547070707071007e015e71007e015e707070707371007e010a7070707071007e015c70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000117571007e0043000000017371007e00450574000e6c6162656c2e666163696c697479707070707070707070707070707371007e00e30000c3540000000a000100000000000000480000010e000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eae168411f352b0ea2b080c4f486c46c40000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e016b71007e016b71007e0169707371007e01030000c3547070707071007e016b71007e016b707371007e00fd0000c3547070707071007e016b71007e016b707371007e01060000c3547070707071007e016b71007e016b707371007e01080000c3547070707071007e016b71007e016b707070707371007e010a7070707071007e016970707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000127571007e0043000000017371007e0045057400156c6162656c2e737570706c79696e672e6465706f74707070707070707070707070707371007e00e30000c3540000000a0001000000000000004800000239000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eb45d23fd4e1c18f3bada7198edba48840000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e017871007e017871007e0176707371007e01030000c3547070707071007e017871007e0178707371007e00fd0000c3547070707071007e017871007e0178707371007e01060000c3547070707071007e017871007e0178707371007e01080000c3547070707071007e017871007e0178707070707371007e010a7070707071007e017670707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000137571007e0043000000017371007e0045057400116865616465722e6f726465722e64617465707070707070707070707070707371007e00e30000c3540000000a0001000000000000002f0000006d000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eacfb8e262215827d219fe37117e14ab40000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e018571007e018571007e0183707371007e01030000c3547070707071007e018571007e0185707371007e00fd0000c3547070707071007e018571007e0185707371007e01060000c3547070707071007e018571007e0185707371007e01080000c3547070707071007e018571007e0185707070707371007e010a7070707071007e018370707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000147571007e0043000000017371007e0045037400076f726465726964707070707070707070707070707371007e00e30000c3540000000a000100000000000000350000006d000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e8cb4014dd4704077c2d7f24d862a4da90000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e019271007e019271007e0190707371007e01030000c3547070707071007e019271007e0192707371007e00fd0000c3547070707071007e019271007e0192707371007e01060000c3547070707071007e019271007e0192707371007e01080000c3547070707071007e019271007e0192707070707371007e010a7070707071007e0190707070707070707070707070707070707e71007e010d740006424f54544f4d0000c3540000000000000001707071007e01117371007e0040000000157571007e0043000000017371007e004503740009737461727464617465707070707070707070707074000a64642f4d4d2f79797979707371007e00e30000c3540000000a000100000000000000c80000015f000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003ea3577ac00bbc91aea24da1db27e04a370000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01a271007e01a271007e01a0707371007e01030000c3547070707071007e01a271007e01a2707371007e00fd0000c3547070707071007e01a271007e01a2707371007e01060000c3547070707071007e01a271007e01a2707371007e01080000c3547070707071007e01a271007e01a2707070707371007e010a7070707071007e01a070707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000167571007e0043000000017371007e004503740008666163696c697479707070707070707070707070707371007e00e30000c3540000000a000100000000000000c80000015f000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003ea7518bc5e1bf144552daec0bf903438c0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01af71007e01af71007e01ad707371007e01030000c3547070707071007e01af71007e01af707371007e00fd0000c3547070707071007e01af71007e01af707371007e01060000c3547070707071007e01af71007e01af707371007e01080000c3547070707071007e01af71007e01af707070707371007e010a7070707071007e01ad70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000177571007e0043000000017371007e00450374000e737570706c79696e676465706f74707070707070707070707070707371007e00e30000c3540000000a0001000000000000009a00000289000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e9f1d48fc20f45b884ce36984d86c47fd0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01bc71007e01bc71007e01ba707371007e01030000c3547070707071007e01bc71007e01bc707371007e00fd0000c3547070707071007e01bc71007e01bc707371007e01060000c3547070707071007e01bc71007e01bc707371007e01080000c3547070707071007e01bc71007e01bc707070707371007e010a7070707071007e01ba70707070707070707070707070707070700000c354000000000000000170707e71007e01107400065245504f52547371007e0040000000187571007e0043000000017371007e00450374000b6372656174656464617465707070707070707071007e00f7707074000a64642f4d4d2f79797979707371007e00e30000c3540000000a00010000000000000032000000a2000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e9651d969df25e43c68a067965a0f47080000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01cc71007e01cc71007e01ca707371007e01030000c3547070707071007e01cc71007e01cc707371007e00fd0000c3547070707071007e01cc71007e01cc707371007e01060000c3547070707071007e01cc71007e01cc707371007e01080000c3547070707071007e01cc71007e01cc707070707371007e010a7070707071007e01ca7070707070707070707070707070707071007e01990000c3540000000000000001707071007e01117371007e0040000000197571007e0043000000017371007e004503740007656e6464617465707070707070707070707074000a64642f4d4d2f7979797970737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655374617469635465787400000000000027d80200014c00047465787471007e00027871007e00e60000c3540000000a000100000000000000030000009b000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003ea6c006ed6ba3b5a2b237397537c94d210000c35470707070707071007e0124707e71007e012574000643454e5445527070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01dd71007e01dd71007e01d9707371007e01030000c3547070707071007e01dd71007e01dd707371007e00fd0000c3547070707071007e01dd71007e01dd707371007e01060000c3547070707071007e01dd71007e01dd707371007e01080000c3547070707071007e01dd71007e01dd707070707371007e010a7070707071007e01d970707070707070707070707070707070707400012d7371007e00e30000c3540000000a0001000000000000004800000239000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e919858c70250dcc4b0ae376069db4e0a0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01e771007e01e771007e01e5707371007e01030000c3547070707071007e01e771007e01e7707371007e00fd0000c3547070707071007e01e771007e01e7707371007e01060000c3547070707071007e01e771007e01e7707371007e01080000c3547070707071007e01e771007e01e7707070707371007e010a7070707071007e01e570707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000001a7571007e0043000000017371007e0045057400146865616465722e74656d706c6174652e74797065707070707070707070707070707371007e00e30000c3540000000a0001000000000000003f0000028a000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003ea680158271d559e5eae60c5bba1a4dbc0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e01f471007e01f471007e01f2707371007e01030000c3547070707071007e01f471007e01f4707371007e00fd0000c3547070707071007e01f471007e01f4707371007e01060000c3547070707071007e01f471007e01f4707371007e01080000c3547070707071007e01f471007e01f4707070707371007e010a7070707071007e01f270707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000001b7571007e0043000000017371007e00450374000474797065707070707070707070707070707371007e01d80000c3540000000a0001000000000000000f0000005e000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eb766ac094d88b863c764bfc6a3c64fa80000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e020171007e020171007e01ff707371007e01030000c3547070707071007e020171007e0201707371007e00fd0000c3547070707071007e020171007e0201707371007e01060000c3547070707071007e020171007e0201707371007e01080000c3547070707071007e020171007e0201707070707371007e010a7070707071007e01ff707070707070707070707070707070707e71007e010d740003544f5074000520203a20207371007e01d80000c3540000000a0001000000000000000f0000005e000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e8d4ac4dc283fdc3289a0a3ff0b1a4c830000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e020d71007e020d71007e020b707371007e01030000c3547070707071007e020d71007e020d707371007e00fd0000c3547070707071007e020d71007e020d707371007e01060000c3547070707071007e020d71007e020d707371007e01080000c3547070707071007e020d71007e020d707070707371007e010a7070707071007e020b7070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000150000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003ea0f0f90fa79d63f10e1207277fe64fb80000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e021771007e021771007e0215707371007e01030000c3547070707071007e021771007e0217707371007e00fd0000c3547070707071007e021771007e0217707371007e01060000c3547070707071007e021771007e0217707371007e01080000c3547070707071007e021771007e0217707070707371007e010a7070707071007e02157070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000150000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eb10418c61404d5460ddeb01912d84bb90000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e022171007e022171007e021f707371007e01030000c3547070707071007e022171007e0221707371007e00fd0000c3547070707071007e022171007e0221707371007e01060000c3547070707071007e022171007e0221707371007e01080000c3547070707071007e022171007e0221707070707371007e010a7070707071007e021f7070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f0000027a000000027071007e001071007e013470707070707071007e00397070707071007e003c7371007e003e8767216237b2425f05863133e0ec4c590000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e022b71007e022b71007e0229707371007e01030000c3547070707071007e022b71007e022b707371007e00fd0000c3547070707071007e022b71007e022b707371007e01060000c3547070707071007e022b71007e022b707371007e01080000c3547070707071007e022b71007e022b707070707371007e010a7070707071007e02297070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f0000027b000000157071007e001071007e013470707070707071007e00397070707071007e003c7371007e003eb06173d47b9ae71a125d4c267ced4a320000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e023571007e023571007e0233707371007e01030000c3547070707071007e023571007e0235707371007e00fd0000c3547070707071007e023571007e0235707371007e01060000c3547070707071007e023571007e0235707371007e01080000c3547070707071007e023571007e0235707070707371007e010a7070707071007e02337070707070707070707070707070707071007e020874000520203a202078700000c3540000003201707070707400064865616465727400077265706f7274317572002a5b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a52506172616d657465723b22000c8d2ac36021020000787000000016737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365506172616d6574657200000000000027d80200095a000e6973466f7250726f6d7074696e675a000f697353797374656d446566696e65644c001664656661756c7456616c756545787072657373696f6e71007e00124c000b6465736372697074696f6e71007e00024c00046e616d6571007e00024c000e6e6573746564547970654e616d6571007e00024c000d70726f706572746965734d617071007e00134c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e000278700101707074000e5245504f52545f434f4e54455854707371007e00857070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e5265706f7274436f6e74657874707371007e0241010170707400155245504f52545f504152414d45544552535f4d4150707371007e008570707074000d6a6176612e7574696c2e4d6170707371007e02410101707074000d4a41535045525f5245504f5254707371007e00857070707400286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a61737065725265706f7274707371007e0241010170707400115245504f52545f434f4e4e454354494f4e707371007e00857070707400136a6176612e73716c2e436f6e6e656374696f6e707371007e0241010170707400105245504f52545f4d41585f434f554e54707371007e008570707071007e00da707371007e0241010170707400125245504f52545f444154415f534f55524345707371007e00857070707400286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5244617461536f75726365707371007e0241010170707400105245504f52545f5343524950544c4554707371007e008570707074002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5241627374726163745363726970746c6574707371007e02410101707074000d5245504f52545f4c4f43414c45707371007e00857070707400106a6176612e7574696c2e4c6f63616c65707371007e0241010170707400165245504f52545f5245534f555243455f42554e444c45707371007e00857070707400186a6176612e7574696c2e5265736f7572636542756e646c65707371007e0241010170707400105245504f52545f54494d455f5a4f4e45707371007e00857070707400126a6176612e7574696c2e54696d655a6f6e65707371007e0241010170707400155245504f52545f464f524d41545f464143544f5259707371007e008570707074002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e7574696c2e466f726d6174466163746f7279707371007e0241010170707400135245504f52545f434c4153535f4c4f41444552707371007e00857070707400156a6176612e6c616e672e436c6173734c6f61646572707371007e02410101707074001a5245504f52545f55524c5f48414e444c45525f464143544f5259707371007e00857070707400206a6176612e6e65742e55524c53747265616d48616e646c6572466163746f7279707371007e0241010170707400145245504f52545f46494c455f5245534f4c564552707371007e008570707074002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e7574696c2e46696c655265736f6c766572707371007e0241010170707400105245504f52545f54454d504c41544553707371007e00857070707400146a6176612e7574696c2e436f6c6c656374696f6e707371007e02410101707074000b534f52545f4649454c4453707371007e008570707074000e6a6176612e7574696c2e4c697374707371007e02410101707074000646494c544552707371007e00857070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e4461746173657446696c746572707371007e0241010170707400125245504f52545f5649525455414c495a4552707371007e00857070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525669727475616c697a6572707371007e02410101707074001449535f49474e4f52455f504147494e4154494f4e707371007e00857070707400116a6176612e6c616e672e426f6f6c65616e707371007e024100007371007e0040000000007070707074000d7375627265706f72745f646972707371007e00857070707400106a6176612e6c616e672e537472696e67707371007e024100007371007e00400000000170707070740009696d6167655f646972707371007e00857070707400106a6176612e6c616e672e537472696e67707371007e024100007371007e00400000000270707070740006706f645f6964707371007e00857070707400116a6176612e6c616e672e496e7465676572707371007e0085707371007e001a0000000377040000000374000c697265706f72742e7a6f6f6d740009697265706f72742e78740009697265706f72742e7978737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f400000000000037708000000040000000371007e02a07400013071007e029e740003312e3571007e029f74000130787372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365517565727900000000000027d80200025b00066368756e6b7374002b5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5251756572794368756e6b3b4c00086c616e677561676571007e000278707572002b5b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5251756572794368756e6b3b409f00a1e8ba34a4020000787000000008737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736551756572794368756e6b00000000000027d8020003420004747970654c00047465787471007e00025b0006746f6b656e737400135b4c6a6176612f6c616e672f537472696e673b78700174004753454c454354202853454c4543542073756d287175616e7469747972657475726e6564292046524f4d20706f645f6c696e655f6974656d7320574845524520706f646964203d20707371007e02ab02740006706f645f6964707371007e02ab017400672920415320746f74616c7175616e7469747972657475726e65642c0a2020202020202020202020202853454c4543542073756d287175616e7469747973686970706564292046524f4d20706f645f6c696e655f6974656d7320574845524520706f646964203d20707371007e02ab02740006706f645f6964707371007e02ab017400672920415320746f74616c7175616e74697479736869707065642c0a2020202020202020202020202853454c4543542073756d287175616e746974797265636569766564292046524f4d20706f645f6c696e655f6974656d7320574845524520706f646964203d20707371007e02ab02740006706f645f6964707371007e02ab017403322920415320746f74616c7175616e7469747972656365697665642c0a202020202020202020202020702e7265636569766564646174652c20702e726563656976656462792c20702e64656c69766572656462792c20702e6f7264657269642c206f2e63726561746564646174652c2028662e636f6465207c7c2027202d2027207c7c20662e6e616d652920617320666163696c6974792c0a2020202020202020202020202043415345205748454e20722e656d657267656e6379203d2074727565205448454e2027456d657267656e6379270a20202020202020202020202020454c53452027526567756c61722720454e4420617320747970652c0a2020202020202020202020202073662e6e616d6520617320737570706c79696e676465706f742c2070676d2e6e616d652061732070726f6772616d2c2070702e7374617274646174652c2070702e656e64646174650a2020202020202020202020202046524f4d20706f64207020696e6e6572206a6f696e206f7264657273206f206f6e20702e6f726465726964203d206f2e69640a202020202020202020202020202020494e4e4552204a4f494e20666163696c69746965732066206f6e20702e666163696c6974796964203d20662e69640a202020202020202020202020202020494e4e4552204a4f494e20737570706c795f6c696e65732073206f6e206f2e737570706c796c696e656964203d20732e69640a202020202020202020202020202020494e4e4552204a4f494e20666163696c6974696573207366206f6e20732e737570706c79696e67666163696c6974796964203d2073662e69640a202020202020202020202020202020494e4e4552204a4f494e207265717569736974696f6e732072206f6e206f2e6964203d20722e69640a202020202020202020202020202020494e4e4552204a4f494e2070726f6772616d732070676d206f6e20702e70726f6772616d6964203d2070676d2e69640a202020202020202020202020202020494e4e4552204a4f494e2070726f63657373696e675f706572696f6473207070206f6e20702e706572696f646964203d2070702e69640a20202020202020202020202020574845524520702e6964203d20707371007e02ab02740006706f645f69647074000373716c707070707371007e003eb3d554b1aefe96cea0a4e8610726422f757200295b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525661726961626c653b62e6837c982cb7440200007870000000067371007e00c3000077ee000001007e71007e00c874000653595354454d707071007e00d070707371007e0040000000037571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e7465676572283129707074000b504147455f4e554d424552707e71007e00d77400065245504f525471007e00da707371007e00c3000077ee0000010071007e02c2707071007e00d070707371007e0040000000047571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e7465676572283129707074000d434f4c554d4e5f4e554d424552707e71007e00d77400045041474571007e00da707371007e00c3000077ee0000010071007e00c97371007e0040000000057571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e00d070707371007e0040000000067571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c5245504f52545f434f554e547071007e02c971007e00da707371007e00c3000077ee0000010071007e00c97371007e0040000000077571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e00d070707371007e0040000000087571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e7465676572283029707074000a504147455f434f554e547071007e02d171007e00da707371007e00c3000077ee0000010071007e00c97371007e0040000000097571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e00d070707371007e00400000000a7571007e0043000000017371007e0045017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c434f4c554d4e5f434f554e54707e71007e00d7740006434f4c554d4e71007e00da7071007e00c77e72003c6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5768656e5265736f757263654d697373696e6754797065456e756d00000000000000001200007871007e001d7400044e554c4c71007e023e7371007e00117371007e001a0000000077040000000078700000c3540000009901707070707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4f7269656e746174696f6e456e756d00000000000000001200007871007e001d7400094c414e4453434150457371007e00117371007e001a000000027704000000027371007e00e30000c3540000000b000100000000000000ac0000027c000000047071007e001071007e02fb70707070707071007e00397070707071007e003c7371007e003e843f39a0c2e70009c6758d1a4ca348990000c35470707070707071007e01247071007e01267070707070707070707371007e00f8707371007e00fc0000c3547070707071007e02ff71007e02ff71007e02fd707371007e01030000c3547070707071007e02ff71007e02ff707371007e00fd0000c3547070707071007e02ff71007e02ff707371007e01060000c3547070707071007e02ff71007e02ff707371007e01080000c3547070707071007e02ff71007e02ff707070707371007e010a7070707071007e02fd70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000227571007e0043000000057371007e0045017400046d7367287371007e00450574000d6c6162656c2e706167652e6f667371007e0045017400022c207371007e00450474000b504147455f4e554d4245527371007e004501740001297070707070707070707070740000707371007e00e30000c3540000000b0001000000000000001300000329000000047071007e001071007e02fb70707070707071007e00397070707071007e003c7371007e003ebbf9a5527705d801477ffc9d22bf46f50000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e031571007e031571007e0313707371007e01030000c3547070707071007e031571007e0315707371007e00fd0000c3547070707071007e031571007e0315707371007e01060000c3547070707071007e031571007e0315707371007e01080000c3547070707071007e031571007e0315707070707371007e010a7070707071007e031370707070707070707070707070707070700000c3540000000000000000707071007e01c37371007e0040000000237571007e0043000000027371007e004501740006222022202b207371007e00450474000b504147455f4e554d4245527070707070707070707070707078700000c354000000160170707070707e72002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5072696e744f72646572456e756d00000000000000001200007871007e001d740008564552544943414c757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525374796c653bd49cc311d90572350200007870000000087372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655374796c65000000000000271102003a49001950534555444f5f53455249414c5f56455253494f4e5f5549445a0009697344656661756c744c00096261636b636f6c6f7271007e002f4c0006626f7264657271007e00144c000b626f72646572436f6c6f7271007e002f4c000c626f74746f6d426f7264657271007e00144c0011626f74746f6d426f72646572436f6c6f7271007e002f4c000d626f74746f6d50616464696e6771007e00e75b0011636f6e646974696f6e616c5374796c65737400315b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52436f6e646974696f6e616c5374796c653b4c001464656661756c745374796c6550726f766964657271007e00304c000466696c6c71007e00144c000966696c6c56616c756571007e01384c0008666f6e744e616d6571007e00024c0008666f6e7453697a6571007e00e74c0009666f7265636f6c6f7271007e002f4c0013686f72697a6f6e74616c416c69676e6d656e7471007e00144c0018686f72697a6f6e74616c416c69676e6d656e7456616c756571007e00e84c000f6973426c616e6b5768656e4e756c6c71007e002b4c00066973426f6c6471007e002b4c000869734974616c696371007e002b4c000d6973506466456d62656464656471007e002b4c000f6973537472696b655468726f75676871007e002b4c000c69735374796c65645465787471007e002b4c000b6973556e6465726c696e6571007e002b4c000a6c656674426f7264657271007e00144c000f6c656674426f72646572436f6c6f7271007e002f4c000b6c65667450616464696e6771007e00e74c00076c696e65426f7871007e00e94c00076c696e6550656e71007e01394c000b6c696e6553706163696e6771007e00144c00106c696e6553706163696e6756616c756571007e00ea4c00066d61726b757071007e00024c00046d6f646571007e00144c00096d6f646556616c756571007e00314c00046e616d6571007e00024c000770616464696e6771007e00e74c000970617261677261706871007e00eb4c000b706172656e745374796c6571007e00074c0018706172656e745374796c654e616d655265666572656e636571007e00024c00077061747465726e71007e00024c000b706466456e636f64696e6771007e00024c000b706466466f6e744e616d6571007e00024c000370656e71007e00144c000c706f736974696f6e5479706571007e00144c000672616469757371007e00e74c000b7269676874426f7264657271007e00144c00107269676874426f72646572436f6c6f7271007e002f4c000c726967687450616464696e6771007e00e74c0008726f746174696f6e71007e00144c000d726f746174696f6e56616c756571007e00ec4c000a7363616c65496d61676571007e00144c000f7363616c65496d61676556616c75657400314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5363616c65496d616765456e756d3b4c000b737472657463685479706571007e00144c0009746f70426f7264657271007e00144c000e746f70426f72646572436f6c6f7271007e002f4c000a746f7050616464696e6771007e00e74c0011766572746963616c416c69676e6d656e7471007e00144c0016766572746963616c416c69676e6d656e7456616c756571007e00ed78700000c35400707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e032b71007e032b71007e032a707371007e01030000c3547070707071007e032b71007e032b707371007e00fd0000c3547372000e6a6176612e6177742e436f6c6f7201a51783108f337502000546000666616c70686149000576616c75654c0002637374001b4c6a6176612f6177742f636f6c6f722f436f6c6f7253706163653b5b00096672676276616c75657400025b465b00066676616c756571007e0331787000000000ff00000070707070707371007e01403f80000071007e032b71007e032b707371007e01060000c3547070707071007e032b71007e032b707371007e01080000c3547070707071007e032b71007e032b7371007e00fe0000c3547070707071007e032a70707070707400057461626c65707371007e010a7070707071007e032a70707070707070707070707070707070707070707070707070707371007e03270000c354007371007e032f00000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e033b71007e033b71007e0339707371007e01030000c3547070707071007e033b71007e033b707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f00000071007e033b71007e033b707371007e01060000c3547070707071007e033b71007e033b707371007e01080000c3547070707071007e033b71007e033b7371007e00fe0000c3547070707071007e0339707070707e71007e013b7400064f50415155457400087461626c655f5448707371007e010a7070707071007e033970707070707070707070707070707070707070707070707070707371007e03270000c354007371007e032f00000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e034a71007e034a71007e0348707371007e01030000c3547070707071007e034a71007e034a707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f00000071007e034a71007e034a707371007e01060000c3547070707071007e034a71007e034a707371007e01080000c3547070707071007e034a71007e034a7371007e00fe0000c3547070707071007e03487070707071007e03447400087461626c655f4348707371007e010a7070707071007e034870707070707070707070707070707070707070707070707070707371007e03270000c354007371007e032f00000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e035771007e035771007e0355707371007e01030000c3547070707071007e035771007e0357707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f00000071007e035771007e0357707371007e01060000c3547070707071007e035771007e0357707371007e01080000c3547070707071007e035771007e03577371007e00fe0000c3547070707071007e03557070707071007e03447400087461626c655f5444707371007e010a7070707071007e035570707070707070707070707070707070707070707070707070707371007e03270000c35400707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e036371007e036371007e0362707371007e01030000c3547070707071007e036371007e0363707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f80000071007e036371007e0363707371007e01060000c3547070707071007e036371007e0363707371007e01080000c3547070707071007e036371007e03637371007e00fe0000c3547070707071007e036270707070707400077461626c652031707371007e010a7070707071007e036270707070707070707070707070707070707070707070707070707371007e03270000c354007371007e032f00000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e037071007e037071007e036e707371007e01030000c3547070707071007e037071007e0370707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f00000071007e037071007e0370707371007e01060000c3547070707071007e037071007e0370707371007e01080000c3547070707071007e037071007e03707371007e00fe0000c3547070707071007e036e7070707071007e034474000a7461626c6520315f5448707371007e010a7070707071007e036e70707070707070707070707070707070707070707070707070707371007e03270000c354007371007e032f00000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e037d71007e037d71007e037b707371007e01030000c3547070707071007e037d71007e037d707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f00000071007e037d71007e037d707371007e01060000c3547070707071007e037d71007e037d707371007e01080000c3547070707071007e037d71007e037d7371007e00fe0000c3547070707071007e037b7070707071007e034474000a7461626c6520315f4348707371007e010a7070707071007e037b70707070707070707070707070707070707070707070707070707371007e03270000c354007371007e032f00000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e038a71007e038a71007e0388707371007e01030000c3547070707071007e038a71007e038a707371007e00fd0000c3547371007e032f00000000ff00000070707070707371007e01403f00000071007e038a71007e038a707371007e01060000c3547070707071007e038a71007e038a707371007e01080000c3547070707071007e038a71007e038a7371007e00fe0000c3547070707071007e03887070707071007e034474000a7461626c6520315f5444707371007e010a7070707071007e038870707070707070707070707070707070707070707070707070707371007e00117371007e001a000000157704000000157371007e01360000c3540000007b000100000000000000c50000000d000000207071007e001071007e039570707071007e013c707071007e00397070707071007e00ef7371007e003e9be2cb5c14a2f2acccb8fa435e6f4783000077ee70707371007e00fe0000c3547070707371007e01400000000071007e039770707371007e00e30000c3540000003e000100000000000000720000000c000000207071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003ea97f5841d2d27c2574639613be3b46ce0000c3547070707070707371007e00f30000000e70707070707070707070707371007e00f87371007e00f3000000057371007e00fc0000c3547070707071007e039e71007e039e71007e039b71007e039f7371007e01030000c3547070707071007e039e71007e039e707371007e00fd0000c3547070707071007e039e71007e039e71007e039f7371007e01060000c3547070707071007e039e71007e039e7371007e00f3000000147371007e01080000c3547070707071007e039e71007e039e707070707371007e010a7070707071007e039b7070707070707070707070707070707071007e02080000c3540000000000000001707071007e01117371007e0040000000247571007e0043000000017371007e00450574000d6c6162656c2e73756d6d617279707070707070707070707070707371007e00e30000c35400000016000100000000000000640000000d0000005e7071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e8deebd742234878d6fe8bbadbec44b6a0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e03ad71007e03ad71007e03ab707371007e01030000c3547070707071007e03ad71007e03ad707371007e00fd0000c3547070707071007e03ad71007e03ad707371007e01060000c3547070707071007e03ad71007e03ad707371007e01080000c3547070707071007e03ad71007e03ad707070707371007e010a7070707071007e03ab7070707070707070707070707070707071007e02080000c3540000000000000001707071007e01117371007e0040000000257571007e0043000000017371007e0045057400196c6162656c2e746f74616c2e736869707065642e7061636b73707070707070707070707070707371007e00e30000c35400000015000100000000000000640000000d000000727071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e9c543ba0037dfbcdb61f59bb8282415f0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e03ba71007e03ba71007e03b8707371007e01030000c3547070707071007e03ba71007e03ba707371007e00fd0000c3547070707071007e03ba71007e03ba707371007e01060000c3547070707071007e03ba71007e03ba707371007e01080000c3547070707071007e03ba71007e03ba707070707371007e010a7070707071007e03b87070707070707070707070707070707071007e02080000c3540000000000000001707071007e01117371007e0040000000267571007e0043000000027371007e00450574001a6c6162656c2e746f74616c2e72656365697665642e7061636b737371007e004501740005202b202222707070707070707070707070707371007e00e30000c35400000014000100000000000000640000000d000000867071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e9e1fcb501f4287e124a5d9528d524f230000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e03c971007e03c971007e03c7707371007e01030000c3547070707071007e03c971007e03c9707371007e00fd0000c3547070707071007e03c971007e03c9707371007e01060000c3547070707071007e03c971007e03c9707371007e01080000c3547070707071007e03c971007e03c9707070707371007e010a7070707071007e03c77070707070707070707070707070707071007e02080000c3540000000000000001707071007e01117371007e0040000000277571007e0043000000027371007e00450574001a6c6162656c2e746f74616c2e72657475726e65642e7061636b737371007e004501740005202b202222707070707070707070707070707371007e00e30000c3540000000a000100000000000000640000000d000000c67071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e9e118a432990a0edcb6c632b896242790000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e03d871007e03d871007e03d6707371007e01030000c3547070707071007e03d871007e03d8707371007e00fd0000c3547070707071007e03d871007e03d8707371007e01060000c3547070707071007e03d871007e03d8707371007e01080000c3547070707071007e03d871007e03d8707070707371007e010a7070707071007e03d670707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000287571007e0043000000017371007e0045057400106c6162656c2e72656365697665644279707070707070707070707070707371007e00e30000c3540000000a000100000000000000640000000d000000b17071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e89e8d6de02eda420ae8968315346451d0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e03e571007e03e571007e03e3707371007e01030000c3547070707071007e03e571007e03e5707371007e00fd0000c3547070707071007e03e571007e03e5707371007e01060000c3547070707071007e03e571007e03e5707371007e01080000c3547070707071007e03e571007e03e5707070707371007e010a7070707071007e03e370707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000297571007e0043000000017371007e0045057400116c6162656c2e64656c6976657265644279707070707070707070707070707371007e00e30000c354000000150001000000000000004e000000840000005e7071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e91db4045e7c9608f827c0b6fda7d4f200000c35470707070707071007e01247071007e01267070707070707070707371007e00f8707371007e00fc0000c3547070707071007e03f271007e03f271007e03f0707371007e01030000c3547070707071007e03f271007e03f2707371007e00fd0000c3547070707071007e03f271007e03f2707371007e01060000c3547070707071007e03f271007e03f2707371007e01080000c3547070707071007e03f271007e03f2707070707371007e010a7070707071007e03f070707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000002a7571007e0043000000017371007e004503740014746f74616c7175616e746974797368697070656470707070707070707371007e00f601707070707371007e00e30000c354000000150001000000000000004e00000084000000867071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003eba5f7f70a1a867845788e28184fc479f0000c35470707070707071007e01247071007e01267070707070707070707371007e00f8707371007e00fc0000c3547070707071007e040071007e040071007e03fe707371007e01030000c3547070707071007e040071007e0400707371007e00fd0000c3547070707071007e040071007e0400707371007e01060000c3547070707071007e040071007e0400707371007e01080000c3547070707071007e040071007e0400707070707371007e010a7070707071007e03fe70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000002b7571007e0043000000017371007e004503740015746f74616c7175616e7469747972657475726e6564707070707070707071007e03fd707070707371007e00e30000c354000000150001000000000000004e00000084000000727071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e9e8d4a3c6cd7b01b5d5d4b0258184b500000c35470707070707071007e01247071007e01267070707070707070707371007e00f8707371007e00fc0000c3547070707071007e040d71007e040d71007e040b707371007e01030000c3547070707071007e040d71007e040d707371007e00fd0000c3547070707071007e040d71007e040d707371007e01060000c3547070707071007e040d71007e040d707371007e01080000c3547070707071007e040d71007e040d707070707371007e010a7070707071007e040b70707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000002c7571007e0043000000017371007e004503740015746f74616c7175616e746974797265636569766564707070707070707071007e03fd707070707371007e00e30000c3540000000a0001000000000000005300000234000000b17071007e001071007e039570707070707071007e00397070707071007e003c7371007e003ea34ae8de6047ea3030bcea009bc342c30000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e041a71007e041a71007e0418707371007e01030000c3547070707071007e041a71007e041a707371007e00fd0000c3547070707071007e041a71007e041a707371007e01060000c3547070707071007e041a71007e041a707371007e01080000c3547070707071007e041a71007e041a707070707371007e010a7070707071007e041870707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000002d7571007e0043000000017371007e00450374000c726563656976656464617465707070707070707071007e03fd707074000a64642f4d4d2f79797979707371007e00e30000c3540000000a00010000000000000064000001c3000000b17071007e001071007e039570707070707071007e00397070707071007e003c7371007e003e99cbddf1806e00c2bf42eae8e94249360000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e042871007e042871007e0426707371007e01030000c3547070707071007e042871007e0428707371007e00fd0000c3547070707071007e042871007e0428707371007e01060000c3547070707071007e042871007e0428707371007e01080000c3547070707071007e042871007e0428707070707371007e010a7070707071007e042670707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000002e7571007e0043000000017371007e0045057400126c6162656c2e726563656976656444617465707070707070707070707070707371007e00e30000c3540000000a000100000000000000df00000084000000b17071007e001071007e039570707070707071007e00397070707071007e003c7371007e003ea254b405a06feb6228c285558f6c47e50000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e043571007e043571007e0433707371007e01030000c3547070707071007e043571007e0435707371007e00fd0000c3547070707071007e043571007e0435707371007e01060000c3547070707071007e043571007e0435707371007e01080000c3547070707071007e043571007e0435707070707371007e010a7070707071007e043370707070707070707070707070707070700000c3540000000000000001707071007e01117371007e00400000002f7571007e0043000000017371007e00450374000b64656c6976657265646279707070707070707071007e03fd707070707371007e00e30000c3540000000a000100000000000000df00000084000000c67071007e001071007e039570707070707071007e00397070707071007e003c7371007e003ebdcb26d4e495b5ab6d0d6e5b1fea45ca0000c35470707070707071007e012470707070707070707070707371007e00f8707371007e00fc0000c3547070707071007e044271007e044271007e0440707371007e01030000c3547070707071007e044271007e0442707371007e00fd0000c3547070707071007e044271007e0442707371007e01060000c3547070707071007e044271007e0442707371007e01080000c3547070707071007e044271007e0442707070707371007e010a7070707071007e044070707070707070707070707070707070700000c3540000000000000001707071007e01117371007e0040000000307571007e0043000000017371007e00450374000a72656365697665646279707070707070707071007e03fd707070707371007e01d80000c3540000000a0001000000000000000f000000730000005e7071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e9d74654bc5b18537849cafd2798f4c730000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e044f71007e044f71007e044d707371007e01030000c3547070707071007e044f71007e044f707371007e00fd0000c3547070707071007e044f71007e044f707371007e01060000c3547070707071007e044f71007e044f707371007e01080000c3547070707071007e044f71007e044f707070707371007e010a7070707071007e044d7070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000073000000737071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003e9139c1ea93bd22fb35baa8ac7bac4f680000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e045971007e045971007e0457707371007e01030000c3547070707071007e045971007e0459707371007e00fd0000c3547070707071007e045971007e0459707371007e01060000c3547070707071007e045971007e0459707371007e01080000c3547070707071007e045971007e0459707070707371007e010a7070707071007e04577070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000073000000877071007e001071007e039570707070707071007e00397070707071007e00ef7371007e003eb2ba2e0b065ca72b5244f766554842070000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e046371007e046371007e0461707371007e01030000c3547070707071007e046371007e0463707371007e00fd0000c3547070707071007e046371007e0463707371007e01060000c3547070707071007e046371007e0463707371007e01080000c3547070707071007e046371007e0463707070707371007e010a7070707071007e04617070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000226000000b17071007e001071007e039570707070707071007e00397070707071007e003c7371007e003e8acdec1e3b1410f52794c49c51724d3c0000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e046d71007e046d71007e046b707371007e01030000c3547070707071007e046d71007e046d707371007e00fd0000c3547070707071007e046d71007e046d707371007e01060000c3547070707071007e046d71007e046d707371007e01080000c3547070707071007e046d71007e046d707070707371007e010a7070707071007e046b7070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000073000000b17071007e001071007e039570707070707071007e00397070707071007e003c7371007e003eb8d931410ecc1f87f3a22e73997247910000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e047771007e047771007e0475707371007e01030000c3547070707071007e047771007e0477707371007e00fd0000c3547070707071007e047771007e0477707371007e01060000c3547070707071007e047771007e0477707371007e01080000c3547070707071007e047771007e0477707070707371007e010a7070707071007e04757070707070707070707070707070707071007e020874000520203a20207371007e01d80000c3540000000a0001000000000000000f00000073000000c67071007e001071007e039570707070707071007e00397070707071007e003c7371007e003e8baade2e2e2fa551bb511410183e46600000c35470707070707071007e01247071007e01db7070707070707070707371007e00f8707371007e00fc0000c3547070707071007e048171007e048171007e047f707371007e01030000c3547070707071007e048171007e0481707371007e00fd0000c3547070707071007e048171007e0481707371007e01060000c3547070707071007e048171007e0481707371007e01080000c3547070707071007e048171007e0481707070707371007e010a7070707071007e047f7070707070707070707070707070707071007e020874000520203a20207371007e01360000c3540000002e0001000000000000032f0000000dffffff4f7071007e001071007e039570707071007e013c707071007e00397070707071007e00ef7371007e003e8ec07188f5893e33fadb0c71bb5d4dd3000077ee70707371007e00fe0000c3547070707371007e01400000000071007e0489707078700000c354000000d9017070707070707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5768656e4e6f4461746154797065456e756d00000000000000001200007871007e001d74000f4e4f5f444154415f53454354494f4e737200366e65742e73662e6a61737065727265706f7274732e656e67696e652e64657369676e2e4a525265706f7274436f6d70696c654461746100000000000027d80200034c001363726f7373746162436f6d70696c654461746171007e00864c001264617461736574436f6d70696c654461746171007e00864c00166d61696e44617461736574436f6d70696c654461746171007e000178707371007e02a13f4000000000001077080000001000000000787371007e02a13f400000000000107708000000100000000078757200025b42acf317f8060854e002000078700000284fcafebabe0000002e016301001c7265706f7274315f313339353231343030323630315f38363836363407000101002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a524576616c7561746f72070003010017706172616d657465725f5245504f52545f4c4f43414c450100324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465723b010017706172616d657465725f4a41535045525f5245504f525401001c706172616d657465725f5245504f52545f5649525455414c495a455201001a706172616d657465725f5245504f52545f54494d455f5a4f4e45010015706172616d657465725f534f52545f4649454c445301001e706172616d657465725f5245504f52545f46494c455f5245534f4c564552010017706172616d657465725f7375627265706f72745f646972010010706172616d657465725f706f645f696401001a706172616d657465725f5245504f52545f5343524950544c455401001f706172616d657465725f5245504f52545f504152414d45544552535f4d415001001b706172616d657465725f5245504f52545f434f4e4e454354494f4e010018706172616d657465725f5245504f52545f434f4e5445585401001d706172616d657465725f5245504f52545f434c4153535f4c4f41444552010024706172616d657465725f5245504f52545f55524c5f48414e444c45525f464143544f525901001c706172616d657465725f5245504f52545f444154415f534f5552434501001e706172616d657465725f49535f49474e4f52455f504147494e4154494f4e010010706172616d657465725f46494c54455201001f706172616d657465725f5245504f52545f464f524d41545f464143544f525901001a706172616d657465725f5245504f52545f4d41585f434f554e5401001a706172616d657465725f5245504f52545f54454d504c41544553010020706172616d657465725f5245504f52545f5245534f555243455f42554e444c45010013706172616d657465725f696d6167655f64697201001b6669656c645f746f74616c7175616e74697479726563656976656401002e4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c4669656c643b0100146669656c645f737570706c79696e676465706f7401000f6669656c645f73746172746461746501000e6669656c645f666163696c69747901000d6669656c645f70726f6772616d0100106669656c645f726563656976656462790100126669656c645f72656365697665646461746501001a6669656c645f746f74616c7175616e746974797368697070656401001b6669656c645f746f74616c7175616e7469747972657475726e65640100116669656c645f637265617465646461746501000d6669656c645f6f7264657269640100116669656c645f64656c697665726564627901000d6669656c645f656e646461746501000a6669656c645f747970650100147661726961626c655f504147455f4e554d4245520100314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c653b0100167661726961626c655f434f4c554d4e5f4e554d4245520100157661726961626c655f5245504f52545f434f554e540100137661726961626c655f504147455f434f554e540100157661726961626c655f434f4c554d4e5f434f554e540100157661726961626c655f4865616465725f434f554e540100063c696e69743e010003282956010004436f64650c003200330a000400350c0005000609000200370c0007000609000200390c00080006090002003b0c00090006090002003d0c000a0006090002003f0c000b000609000200410c000c000609000200430c000d000609000200450c000e000609000200470c000f000609000200490c00100006090002004b0c00110006090002004d0c00120006090002004f0c0013000609000200510c0014000609000200530c0015000609000200550c0016000609000200570c0017000609000200590c00180006090002005b0c00190006090002005d0c001a0006090002005f0c001b000609000200610c001c001d09000200630c001e001d09000200650c001f001d09000200670c0020001d09000200690c0021001d090002006b0c0022001d090002006d0c0023001d090002006f0c0024001d09000200710c0025001d09000200730c0026001d09000200750c0027001d09000200770c0028001d09000200790c0029001d090002007b0c002a001d090002007d0c002b002c090002007f0c002d002c09000200810c002e002c09000200830c002f002c09000200850c0030002c09000200870c0031002c090002008901000f4c696e654e756d6265725461626c6501000e637573746f6d697a6564496e6974010030284c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b295601000a696e6974506172616d73010012284c6a6176612f7574696c2f4d61703b29560c008e008f0a0002009001000a696e69744669656c64730c0092008f0a00020093010008696e6974566172730c0095008f0a0002009601000d5245504f52545f4c4f43414c4508009801000d6a6176612f7574696c2f4d617007009a010003676574010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f4f626a6563743b0c009c009d0b009b009e0100306e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465720700a001000d4a41535045525f5245504f52540800a20100125245504f52545f5649525455414c495a45520800a40100105245504f52545f54494d455f5a4f4e450800a601000b534f52545f4649454c44530800a80100145245504f52545f46494c455f5245534f4c5645520800aa01000d7375627265706f72745f6469720800ac010006706f645f69640800ae0100105245504f52545f5343524950544c45540800b00100155245504f52545f504152414d45544552535f4d41500800b20100115245504f52545f434f4e4e454354494f4e0800b401000e5245504f52545f434f4e544558540800b60100135245504f52545f434c4153535f4c4f414445520800b801001a5245504f52545f55524c5f48414e444c45525f464143544f52590800ba0100125245504f52545f444154415f534f555243450800bc01001449535f49474e4f52455f504147494e4154494f4e0800be01000646494c5445520800c00100155245504f52545f464f524d41545f464143544f52590800c20100105245504f52545f4d41585f434f554e540800c40100105245504f52545f54454d504c415445530800c60100165245504f52545f5245534f555243455f42554e444c450800c8010009696d6167655f6469720800ca010015746f74616c7175616e7469747972656365697665640800cc01002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c4669656c640700ce01000e737570706c79696e676465706f740800d00100097374617274646174650800d2010008666163696c6974790800d401000770726f6772616d0800d601000a726563656976656462790800d801000c7265636569766564646174650800da010014746f74616c7175616e74697479736869707065640800dc010015746f74616c7175616e7469747972657475726e65640800de01000b63726561746564646174650800e00100076f7264657269640800e201000b64656c69766572656462790800e4010007656e64646174650800e6010004747970650800e801000b504147455f4e554d4245520800ea01002f6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c650700ec01000d434f4c554d4e5f4e554d4245520800ee01000c5245504f52545f434f554e540800f001000a504147455f434f554e540800f201000c434f4c554d4e5f434f554e540800f401000c4865616465725f434f554e540800f60100086576616c756174650100152849294c6a6176612f6c616e672f4f626a6563743b01000a457863657074696f6e730100136a6176612f6c616e672f5468726f7761626c650700fb0100116a6176612f6c616e672f496e74656765720700fd010004284929560c003200ff0a00fe010001001b6c6162656c2e70726f6f662e6f662e64656c69766572792e666f72080102010003737472010026284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e673b0c010401050a0002010601000867657456616c756501001428294c6a6176612f6c616e672f4f626a6563743b0c010801090a00cf010a0100106a6176612f6c616e672f537472696e6707010c0100036d7367010038284c6a6176612f6c616e672f537472696e673b4c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e673b0c010e010f0a0002011001000e6a6176612f7574696c2f446174650701120a0113003501000e6c6162656c2e6f726465722e6e6f08011501001e6c6162656c2e666163696c6974792e7265706f7274696e67506572696f6408011701000e6c6162656c2e666163696c6974790801190100156c6162656c2e737570706c79696e672e6465706f7408011b0100116865616465722e6f726465722e6461746508011d01000e6a6176612f6c616e672f4c6f6e6707011f0100146865616465722e74656d706c6174652e747970650801210a00a1010a0100106a6176612f7574696c2f4c6f63616c650701240100186a6176612f7574696c2f5265736f7572636542756e646c650701260100136a6176612f73716c2f436f6e6e656374696f6e0701280100166a6176612f6c616e672f537472696e6742756666657207012a01000776616c75654f66010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e673b0c012c012d0a010d012e010015284c6a6176612f6c616e672f537472696e673b29560c003201300a012b0131010016706f644c696e654974656d5072696e742e6a72786d6c080133010006617070656e6401002c284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e674275666665723b0c013501360a012b0137010008746f537472696e6701001428294c6a6176612f6c616e672f537472696e673b0c0139013a0a012b013b0100306e65742f73662f6a61737065727265706f7274732f656e67696e652f4a6173706572436f6d70696c654d616e6167657207013d01000d636f6d70696c655265706f727401003e284c6a6176612f6c616e672f537472696e673b294c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a61737065725265706f72743b0c013f01400a013e014101000d6c6162656c2e706167652e6f660801430a00ed010a0100012008014601002c284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e674275666665723b0c013501480a012b014901000d6c6162656c2e73756d6d61727908014b0100196c6162656c2e746f74616c2e736869707065642e7061636b7308014d01001a6c6162656c2e746f74616c2e72656365697665642e7061636b7308014f01001a6c6162656c2e746f74616c2e72657475726e65642e7061636b730801510100106c6162656c2e726563656976656442790801530100116c6162656c2e64656c69766572656442790801550100126c6162656c2e72656365697665644461746508015701000b6576616c756174654f6c6401000b6765744f6c6456616c75650c015a01090a00cf015b0a00ed015b0100116576616c75617465457374696d61746564010011676574457374696d6174656456616c75650c015f01090a00ed016001000a536f7572636546696c650021000200040000002a00020005000600000002000700060000000200080006000000020009000600000002000a000600000002000b000600000002000c000600000002000d000600000002000e000600000002000f0006000000020010000600000002001100060000000200120006000000020013000600000002001400060000000200150006000000020016000600000002001700060000000200180006000000020019000600000002001a000600000002001b000600000002001c001d00000002001e001d00000002001f001d000000020020001d000000020021001d000000020022001d000000020023001d000000020024001d000000020025001d000000020026001d000000020027001d000000020028001d000000020029001d00000002002a001d00000002002b002c00000002002d002c00000002002e002c00000002002f002c000000020030002c000000020031002c00000008000100320033000100340000019b00020001000000d72ab700362a01b500382a01b5003a2a01b5003c2a01b5003e2a01b500402a01b500422a01b500442a01b500462a01b500482a01b5004a2a01b5004c2a01b5004e2a01b500502a01b500522a01b500542a01b500562a01b500582a01b5005a2a01b5005c2a01b5005e2a01b500602a01b500622a01b500642a01b500662a01b500682a01b5006a2a01b5006c2a01b5006e2a01b500702a01b500722a01b500742a01b500762a01b500782a01b5007a2a01b5007c2a01b5007e2a01b500802a01b500822a01b500842a01b500862a01b500882a01b5008ab100000001008b000000b2002c00000012000400190009001a000e001b0013001c0018001d001d001e0022001f00270020002c00210031002200360023003b00240040002500450026004a0027004f0028005400290059002a005e002b0063002c0068002d006d002e0072002f00770030007c00310081003200860033008b00340090003500950036009a0037009f003800a4003900a9003a00ae003b00b3003c00b8003d00bd003e00c2003f00c7004000cc004100d1004200d600120001008c008d000100340000003400020004000000102a2bb700912a2cb700942a2db70097b100000001008b0000001200040000004e0005004f000a0050000f00510002008e008f00010034000001fd000300020000018d2a2b1299b9009f0200c000a1c000a1b500382a2b12a3b9009f0200c000a1c000a1b5003a2a2b12a5b9009f0200c000a1c000a1b5003c2a2b12a7b9009f0200c000a1c000a1b5003e2a2b12a9b9009f0200c000a1c000a1b500402a2b12abb9009f0200c000a1c000a1b500422a2b12adb9009f0200c000a1c000a1b500442a2b12afb9009f0200c000a1c000a1b500462a2b12b1b9009f0200c000a1c000a1b500482a2b12b3b9009f0200c000a1c000a1b5004a2a2b12b5b9009f0200c000a1c000a1b5004c2a2b12b7b9009f0200c000a1c000a1b5004e2a2b12b9b9009f0200c000a1c000a1b500502a2b12bbb9009f0200c000a1c000a1b500522a2b12bdb9009f0200c000a1c000a1b500542a2b12bfb9009f0200c000a1c000a1b500562a2b12c1b9009f0200c000a1c000a1b500582a2b12c3b9009f0200c000a1c000a1b5005a2a2b12c5b9009f0200c000a1c000a1b5005c2a2b12c7b9009f0200c000a1c000a1b5005e2a2b12c9b9009f0200c000a1c000a1b500602a2b12cbb9009f0200c000a1c000a1b50062b100000001008b0000005e0017000000590012005a0024005b0036005c0048005d005a005e006c005f007e00600090006100a2006200b4006300c6006400d8006500ea006600fc0067010e0068012000690132006a0144006b0156006c0168006d017a006e018c006f00020092008f000100340000014d00030002000000fd2a2b12cdb9009f0200c000cfc000cfb500642a2b12d1b9009f0200c000cfc000cfb500662a2b12d3b9009f0200c000cfc000cfb500682a2b12d5b9009f0200c000cfc000cfb5006a2a2b12d7b9009f0200c000cfc000cfb5006c2a2b12d9b9009f0200c000cfc000cfb5006e2a2b12dbb9009f0200c000cfc000cfb500702a2b12ddb9009f0200c000cfc000cfb500722a2b12dfb9009f0200c000cfc000cfb500742a2b12e1b9009f0200c000cfc000cfb500762a2b12e3b9009f0200c000cfc000cfb500782a2b12e5b9009f0200c000cfc000cfb5007a2a2b12e7b9009f0200c000cfc000cfb5007c2a2b12e9b9009f0200c000cfc000cfb5007eb100000001008b0000003e000f0000007700120078002400790036007a0048007b005a007c006c007d007e007e0090007f00a2008000b4008100c6008200d8008300ea008400fc008500020095008f000100340000009d000300020000006d2a2b12ebb9009f0200c000edc000edb500802a2b12efb9009f0200c000edc000edb500822a2b12f1b9009f0200c000edc000edb500842a2b12f3b9009f0200c000edc000edb500862a2b12f5b9009f0200c000edc000edb500882a2b12f7b9009f0200c000edc000edb5008ab100000001008b0000001e00070000008d0012008e0024008f0036009000480091005a0092006c0093000100f800f9000200fa00000004000100fc003400000522000400030000037e014d1baa000003790000000000000030000000d1000000d6000000db000000e0000000ec000000f800000104000001100000011c0000012800000134000001400000014c00000158000001710000017c00000187000001920000019d000001a8000001b3000001c1000001cf000001dd000001eb000001f90000020700000212000002200000022e0000023c0000024a00000258000002660000028a000002a3000002c1000002cc000002d7000002ef00000307000003120000031d0000032b000003390000034700000355000003600000036e014da702a6014da702a1014da7029cbb00fe5904b701014da70290bb00fe5904b701014da70284bb00fe5904b701014da70278bb00fe5903b701014da7026cbb00fe5904b701014da70260bb00fe5903b701014da70254bb00fe5904b701014da70248bb00fe5903b701014da7023cbb00fe5904b701014da70230bb00fe5903b701014da702242a2a130103b601072ab4006cb6010bc0010db601114da7020bbb011359b701144da702002a130116b601074da701f52a130118b601074da701ea2a13011ab601074da701df2a13011cb601074da701d42a13011eb601074da701c92ab40078b6010bc001204da701bb2ab40068b6010bc001134da701ad2ab4006ab6010bc0010d4da7019f2ab40066b6010bc0010d4da701912ab40076b6010bc001134da701832ab4007cb6010bc001134da701752a130122b601074da7016a2ab4007eb6010bc0010d4da7015c2ab40046b60123c000fe4da7014e2ab40062b60123c0010d4da701402ab40038b60123c001254da701322ab40060b60123c001274da701242ab4004cb60123c001294da70116bb012b592ab40044b60123c0010db8012fb70132130134b60138b6013cb801424da700f22a2a130144b601072ab40080b60145c000feb601114da700d9bb012b59130147b701322ab40080b60145c000feb6014ab6013c4da700bb2a13014cb601074da700b02a13014eb601074da700a5bb012b592a130150b60107b8012fb70132b6013c4da7008dbb012b592a130152b60107b8012fb70132b6013c4da700752a130154b601074da7006a2a130156b601074da7005f2ab40072b6010bc000fe4da700512ab40074b6010bc000fe4da700432ab40064b6010bc000fe4da700352ab40070b6010bc001134da700272a130158b601074da7001c2ab4007ab6010bc0010d4da7000e2ab4006eb6010bc0010d4d2cb000000001008b0000019200640000009b0002009d00d400a100d600a200d900a600db00a700de00ab00e000ac00e300b000ec00b100ef00b500f800b600fb00ba010400bb010700bf011000c0011300c4011c00c5011f00c9012800ca012b00ce013400cf013700d3014000d4014300d8014c00d9014f00dd015800de015b00e2017100e3017400e7017c00e8017f00ec018700ed018a00f1019200f2019500f6019d00f701a000fb01a800fc01ab010001b3010101b6010501c1010601c4010a01cf010b01d2010f01dd011001e0011401eb011501ee011901f9011a01fc011e0207011f020a01230212012402150128022001290223012d022e012e02310132023c0133023f0137024a0138024d013c0258013d025b01410266014202690146028a0147028d014b02a3014c02a6015002c1015102c4015502cc015602cf015a02d7015b02da015f02ef016002f2016403070165030a01690312016a0315016e031d016f03200173032b0174032e017803390179033c017d0347017e034a01820355018303580187036001880363018c036e018d03710191037c01990001015900f9000200fa00000004000100fc003400000522000400030000037e014d1baa000003790000000000000030000000d1000000d6000000db000000e0000000ec000000f800000104000001100000011c0000012800000134000001400000014c00000158000001710000017c00000187000001920000019d000001a8000001b3000001c1000001cf000001dd000001eb000001f90000020700000212000002200000022e0000023c0000024a00000258000002660000028a000002a3000002c1000002cc000002d7000002ef00000307000003120000031d0000032b000003390000034700000355000003600000036e014da702a6014da702a1014da7029cbb00fe5904b701014da70290bb00fe5904b701014da70284bb00fe5904b701014da70278bb00fe5903b701014da7026cbb00fe5904b701014da70260bb00fe5903b701014da70254bb00fe5904b701014da70248bb00fe5903b701014da7023cbb00fe5904b701014da70230bb00fe5903b701014da702242a2a130103b601072ab4006cb6015cc0010db601114da7020bbb011359b701144da702002a130116b601074da701f52a130118b601074da701ea2a13011ab601074da701df2a13011cb601074da701d42a13011eb601074da701c92ab40078b6015cc001204da701bb2ab40068b6015cc001134da701ad2ab4006ab6015cc0010d4da7019f2ab40066b6015cc0010d4da701912ab40076b6015cc001134da701832ab4007cb6015cc001134da701752a130122b601074da7016a2ab4007eb6015cc0010d4da7015c2ab40046b60123c000fe4da7014e2ab40062b60123c0010d4da701402ab40038b60123c001254da701322ab40060b60123c001274da701242ab4004cb60123c001294da70116bb012b592ab40044b60123c0010db8012fb70132130134b60138b6013cb801424da700f22a2a130144b601072ab40080b6015dc000feb601114da700d9bb012b59130147b701322ab40080b6015dc000feb6014ab6013c4da700bb2a13014cb601074da700b02a13014eb601074da700a5bb012b592a130150b60107b8012fb70132b6013c4da7008dbb012b592a130152b60107b8012fb70132b6013c4da700752a130154b601074da7006a2a130156b601074da7005f2ab40072b6015cc000fe4da700512ab40074b6015cc000fe4da700432ab40064b6015cc000fe4da700352ab40070b6015cc001134da700272a130158b601074da7001c2ab4007ab6015cc0010d4da7000e2ab4006eb6015cc0010d4d2cb000000001008b000001920064000001a2000201a400d401a800d601a900d901ad00db01ae00de01b200e001b300e301b700ec01b800ef01bc00f801bd00fb01c1010401c2010701c6011001c7011301cb011c01cc011f01d0012801d1012b01d5013401d6013701da014001db014301df014c01e0014f01e4015801e5015b01e9017101ea017401ee017c01ef017f01f3018701f4018a01f8019201f9019501fd019d01fe01a0020201a8020301ab020701b3020801b6020c01c1020d01c4021101cf021201d2021601dd021701e0021b01eb021c01ee022001f9022101fc022502070226020a022a0212022b0215022f0220023002230234022e023502310239023c023a023f023e024a023f024d024302580244025b0248026602490269024d028a024e028d025202a3025302a6025702c1025802c4025c02cc025d02cf026102d7026202da026602ef026702f2026b0307026c030a02700312027103150275031d02760320027a032b027b032e027f03390280033c028403470285034a02890355028a0358028e0360028f03630293036e029403710298037c02a00001015e00f9000200fa00000004000100fc003400000522000400030000037e014d1baa000003790000000000000030000000d1000000d6000000db000000e0000000ec000000f800000104000001100000011c0000012800000134000001400000014c00000158000001710000017c00000187000001920000019d000001a8000001b3000001c1000001cf000001dd000001eb000001f90000020700000212000002200000022e0000023c0000024a00000258000002660000028a000002a3000002c1000002cc000002d7000002ef00000307000003120000031d0000032b000003390000034700000355000003600000036e014da702a6014da702a1014da7029cbb00fe5904b701014da70290bb00fe5904b701014da70284bb00fe5904b701014da70278bb00fe5903b701014da7026cbb00fe5904b701014da70260bb00fe5903b701014da70254bb00fe5904b701014da70248bb00fe5903b701014da7023cbb00fe5904b701014da70230bb00fe5903b701014da702242a2a130103b601072ab4006cb6010bc0010db601114da7020bbb011359b701144da702002a130116b601074da701f52a130118b601074da701ea2a13011ab601074da701df2a13011cb601074da701d42a13011eb601074da701c92ab40078b6010bc001204da701bb2ab40068b6010bc001134da701ad2ab4006ab6010bc0010d4da7019f2ab40066b6010bc0010d4da701912ab40076b6010bc001134da701832ab4007cb6010bc001134da701752a130122b601074da7016a2ab4007eb6010bc0010d4da7015c2ab40046b60123c000fe4da7014e2ab40062b60123c0010d4da701402ab40038b60123c001254da701322ab40060b60123c001274da701242ab4004cb60123c001294da70116bb012b592ab40044b60123c0010db8012fb70132130134b60138b6013cb801424da700f22a2a130144b601072ab40080b60161c000feb601114da700d9bb012b59130147b701322ab40080b60161c000feb6014ab6013c4da700bb2a13014cb601074da700b02a13014eb601074da700a5bb012b592a130150b60107b8012fb70132b6013c4da7008dbb012b592a130152b60107b8012fb70132b6013c4da700752a130154b601074da7006a2a130156b601074da7005f2ab40072b6010bc000fe4da700512ab40074b6010bc000fe4da700432ab40064b6010bc000fe4da700352ab40070b6010bc001134da700272a130158b601074da7001c2ab4007ab6010bc0010d4da7000e2ab4006eb6010bc0010d4d2cb000000001008b000001920064000002a9000202ab00d402af00d602b000d902b400db02b500de02b900e002ba00e302be00ec02bf00ef02c300f802c400fb02c8010402c9010702cd011002ce011302d2011c02d3011f02d7012802d8012b02dc013402dd013702e1014002e2014302e6014c02e7014f02eb015802ec015b02f0017102f1017402f5017c02f6017f02fa018702fb018a02ff0192030001950304019d030501a0030901a8030a01ab030e01b3030f01b6031301c1031401c4031801cf031901d2031d01dd031e01e0032201eb032301ee032701f9032801fc032c0207032d020a03310212033202150336022003370223033b022e033c02310340023c0341023f0345024a0346024d034a0258034b025b034f0266035002690354028a0355028d035902a3035a02a6035e02c1035f02c4036302cc036402cf036802d7036902da036d02ef036e02f2037203070373030a0377031203780315037c031d037d03200381032b0382032e038603390387033c038b0347038c034a03900355039103580395036003960363039a036e039b0371039f037c03a7000101620000000200017400155f313339353231343030323630315f3836383636347400326e65742e73662e6a61737065727265706f7274732e656e67696e652e64657369676e2e4a524a61766163436f6d70696c6572', NULL, '2016-02-03 14:37:34.906545', 'Print', NULL);
INSERT INTO templates (id, name, data, createdby, createddate, type, description) VALUES (3, 'Print Order Requisition', '\xaced0005737200286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a61737065725265706f727400000000000027d80200034c000b636f6d70696c65446174617400164c6a6176612f696f2f53657269616c697a61626c653b4c0011636f6d70696c654e616d655375666669787400124c6a6176612f6c616e672f537472696e673b4c000d636f6d70696c6572436c61737371007e00027872002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655265706f727400000000000027d802002a49001950534555444f5f53455249414c5f56455253494f4e5f55494449000c626f74746f6d4d617267696e49000b636f6c756d6e436f756e7449000d636f6c756d6e53706163696e6749000b636f6c756d6e57696474685a001069676e6f7265506167696e6174696f6e5a00136973466c6f6174436f6c756d6e466f6f7465725a0010697353756d6d6172794e6577506167655a0020697353756d6d6172795769746850616765486561646572416e64466f6f7465725a000e69735469746c654e65775061676549000a6c6566744d617267696e42000b6f7269656e746174696f6e49000a7061676548656967687449000970616765576964746842000a7072696e744f7264657249000b72696768744d617267696e490009746f704d617267696e42000e7768656e4e6f44617461547970654c000a6261636b67726f756e647400244c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5242616e643b4c000f636f6c756d6e446972656374696f6e7400334c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f52756e446972656374696f6e456e756d3b4c000c636f6c756d6e466f6f74657271007e00044c000c636f6c756d6e48656164657271007e00045b000864617461736574737400285b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52446174617365743b4c000c64656661756c745374796c657400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525374796c653b4c000664657461696c71007e00044c000d64657461696c53656374696f6e7400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5253656374696f6e3b4c0012666f726d6174466163746f7279436c61737371007e00024c000a696d706f72747353657474000f4c6a6176612f7574696c2f5365743b4c00086c616e677561676571007e00024c000e6c61737450616765466f6f74657271007e00044c000b6d61696e446174617365747400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52446174617365743b4c00046e616d6571007e00024c00066e6f4461746171007e00044c00106f7269656e746174696f6e56616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4f7269656e746174696f6e456e756d3b4c000a70616765466f6f74657271007e00044c000a7061676548656164657271007e00044c000f7072696e744f7264657256616c75657400314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5072696e744f72646572456e756d3b5b00067374796c65737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525374796c653b4c000773756d6d61727971007e00045b000974656d706c6174657374002f5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525265706f727454656d706c6174653b4c00057469746c6571007e00044c00137768656e4e6f446174615479706556616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5768656e4e6f4461746154797065456e756d3b78700000c3540000000000000001000000000000034a00010000000000000000000002530000034a00000000000000000000707e7200316e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e52756e446972656374696f6e456e756d00000000000000001200007872000e6a6176612e6c616e672e456e756d000000000000000012000078707400034c54527070757200285b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a52446174617365743b4c1a3698cdac9c440200007870000000017372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654461746173657400000000000027d802001149001950534555444f5f53455249414c5f56455253494f4e5f5549445a000669734d61696e4200177768656e5265736f757263654d697373696e67547970655b00066669656c64737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a524669656c643b4c001066696c74657245787072657373696f6e74002a4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5245787072657373696f6e3b5b000667726f7570737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5247726f75703b4c00046e616d6571007e00025b000a706172616d657465727374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52506172616d657465723b4c000d70726f706572746965734d617074002d4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250726f706572746965734d61703b4c000571756572797400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5251756572793b4c000e7265736f7572636542756e646c6571007e00024c000e7363726970746c6574436c61737371007e00025b000a7363726970746c65747374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525363726970746c65743b5b000a736f72744669656c647374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52536f72744669656c643b4c0004757569647400104c6a6176612f7574696c2f555549443b5b00097661726961626c65737400295b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525661726961626c653b4c001c7768656e5265736f757263654d697373696e675479706556616c756574003e4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5768656e5265736f757263654d697373696e6754797065456e756d3b78700000c354000070707074000f5461626c65204461746173657420317572002a5b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a52506172616d657465723b22000c8d2ac36021020000787000000011737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365506172616d6574657200000000000027d80200095a000e6973466f7250726f6d7074696e675a000f697353797374656d446566696e65644c001664656661756c7456616c756545787072657373696f6e71007e00194c000b6465736372697074696f6e71007e00024c00046e616d6571007e00024c000e6e6573746564547970654e616d6571007e00024c000d70726f706572746965734d617071007e001c4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e000278700101707074000e5245504f52545f434f4e54455854707372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5250726f706572746965734d617000000000000027d80200034c00046261736571007e001c4c000e70726f706572746965734c6973747400104c6a6176612f7574696c2f4c6973743b4c000d70726f706572746965734d617074000f4c6a6176612f7574696c2f4d61703b78707070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e5265706f7274436f6e74657874707371007e0027010170707400155245504f52545f504152414d45544552535f4d4150707371007e002a70707074000d6a6176612e7574696c2e4d6170707371007e00270101707074000d4a41535045525f5245504f5254707371007e002a7070707400286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a61737065725265706f7274707371007e0027010170707400115245504f52545f434f4e4e454354494f4e707371007e002a7070707400136a6176612e73716c2e436f6e6e656374696f6e707371007e0027010170707400105245504f52545f4d41585f434f554e54707371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e0027010170707400125245504f52545f444154415f534f55524345707371007e002a7070707400286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5244617461536f75726365707371007e0027010170707400105245504f52545f5343524950544c4554707371007e002a70707074002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5241627374726163745363726970746c6574707371007e00270101707074000d5245504f52545f4c4f43414c45707371007e002a7070707400106a6176612e7574696c2e4c6f63616c65707371007e0027010170707400165245504f52545f5245534f555243455f42554e444c45707371007e002a7070707400186a6176612e7574696c2e5265736f7572636542756e646c65707371007e0027010170707400105245504f52545f54494d455f5a4f4e45707371007e002a7070707400126a6176612e7574696c2e54696d655a6f6e65707371007e0027010170707400155245504f52545f464f524d41545f464143544f5259707371007e002a70707074002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e7574696c2e466f726d6174466163746f7279707371007e0027010170707400135245504f52545f434c4153535f4c4f41444552707371007e002a7070707400156a6176612e6c616e672e436c6173734c6f61646572707371007e00270101707074001a5245504f52545f55524c5f48414e444c45525f464143544f5259707371007e002a7070707400206a6176612e6e65742e55524c53747265616d48616e646c6572466163746f7279707371007e0027010170707400145245504f52545f46494c455f5245534f4c564552707371007e002a70707074002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e7574696c2e46696c655265736f6c766572707371007e0027010170707400105245504f52545f54454d504c41544553707371007e002a7070707400146a6176612e7574696c2e436f6c6c656374696f6e707371007e00270101707074000b534f52545f4649454c4453707371007e002a70707074000e6a6176612e7574696c2e4c697374707371007e00270101707074000646494c544552707371007e002a7070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e4461746173657446696c746572707371007e002a70707070707070707372000e6a6176612e7574696c2e55554944bc9903f7986d852f0200024a000c6c65617374536967426974734a000b6d6f73745369674269747378708f0ea80773ab7ae1bbcb761587a94cf2757200295b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525661726961626c653b62e6837c982cb7440200007870000000057372002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655661726961626c6500000000000027d802001149001950534555444f5f53455249414c5f56455253494f4e5f55494442000b63616c63756c6174696f6e42000d696e6372656d656e74547970655a000f697353797374656d446566696e65644200097265736574547970654c001063616c63756c6174696f6e56616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f43616c63756c6174696f6e456e756d3b4c000a65787072657373696f6e71007e00194c000e696e6372656d656e7447726f75707400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5247726f75703b4c0012696e6372656d656e745479706556616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f496e6372656d656e7454797065456e756d3b4c001b696e6372656d656e746572466163746f7279436c6173734e616d6571007e00024c001f696e6372656d656e746572466163746f7279436c6173735265616c4e616d6571007e00024c0016696e697469616c56616c756545787072657373696f6e71007e00194c00046e616d6571007e00024c000a726573657447726f757071007e00764c000e72657365745479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f526573657454797065456e756d3b4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e00027870000077ee000001007e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e43616c63756c6174696f6e456e756d00000000000000001200007871007e001274000653595354454d70707e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e496e6372656d656e7454797065456e756d00000000000000001200007871007e00127400044e4f4e457070737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736545787072657373696f6e00000000000027d802000449000269645b00066368756e6b737400305b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5245787072657373696f6e4368756e6b3b4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e0002787000000000757200305b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5245787072657373696f6e4368756e6b3b6d59cfde694ba355020000787000000001737200366e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736545787072657373696f6e4368756e6b00000000000027d8020002420004747970654c00047465787471007e00027870017400186e6577206a6176612e6c616e672e496e7465676572283129707074000b504147455f4e554d424552707e72002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e526573657454797065456e756d00000000000000001200007871007e00127400065245504f525471007e003e707371007e0074000077ee0000010071007e007b707071007e007e70707371007e0080000000017571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283129707074000d434f4c554d4e5f4e554d424552707e71007e00897400045041474571007e003e707371007e0074000077ee000001007e71007e007a740005434f554e547371007e0080000000027571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000037571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c5245504f52545f434f554e547071007e008a71007e003e707371007e0074000077ee0000010071007e00957371007e0080000000047571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000057571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000a504147455f434f554e547071007e009271007e003e707371007e0074000077ee0000010071007e00957371007e0080000000067571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000077571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c434f4c554d4e5f434f554e54707e71007e0089740006434f4c554d4e71007e003e707e72003c6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5768656e5265736f757263654d697373696e6754797065456e756d00000000000000001200007871007e00127400044e554c4c70707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736553656374696f6e00000000000027d80200015b000562616e64737400255b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5242616e643b7870757200255b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5242616e643b95dd7eec8cca85350200007870000000017372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736542616e6400000000000027d802000749001950534555444f5f53455249414c5f56455253494f4e5f5549444900066865696768745a000e697353706c6974416c6c6f7765644c00137072696e745768656e45787072657373696f6e71007e00194c000d70726f706572746965734d617071007e001c4c000973706c6974547970657400104c6a6176612f6c616e672f427974653b4c000e73706c69745479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f53706c697454797065456e756d3b787200336e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365456c656d656e7447726f757000000000000027d80200024c00086368696c6472656e71007e002b4c000c656c656d656e7447726f757074002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52456c656d656e7447726f75703b7870737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a65787000000008770400000008737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655374617469635465787400000000000027d80200014c00047465787471007e0002787200326e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736554657874456c656d656e7400000000000027d802002549001950534555444f5f53455249414c5f56455253494f4e5f5549444c0006626f7264657271007e00bf4c000b626f72646572436f6c6f727400104c6a6176612f6177742f436f6c6f723b4c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e677400134c6a6176612f6c616e672f496e74656765723b4c0008666f6e744e616d6571007e00024c0008666f6e7453697a6571007e00c94c0013686f72697a6f6e74616c416c69676e6d656e7471007e00bf4c0018686f72697a6f6e74616c416c69676e6d656e7456616c75657400364c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f486f72697a6f6e74616c416c69676e456e756d3b4c00066973426f6c647400134c6a6176612f6c616e672f426f6f6c65616e3b4c000869734974616c696371007e00cb4c000d6973506466456d62656464656471007e00cb4c000f6973537472696b655468726f75676871007e00cb4c000c69735374796c65645465787471007e00cb4c000b6973556e6465726c696e6571007e00cb4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f787400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a524c696e65426f783b4c000b6c696e6553706163696e6771007e00bf4c00106c696e6553706163696e6756616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e6553706163696e67456e756d3b4c00066d61726b757071007e00024c000770616464696e6771007e00c94c00097061726167726170687400294c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525061726167726170683b4c000b706466456e636f64696e6771007e00024c000b706466466f6e744e616d6571007e00024c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c0008726f746174696f6e71007e00bf4c000d726f746174696f6e56616c756574002f4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f526f746174696f6e456e756d3b4c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c94c0011766572746963616c416c69676e6d656e7471007e00bf4c0016766572746963616c416c69676e6d656e7456616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f566572746963616c416c69676e456e756d3b7872002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365456c656d656e7400000000000027d802001b49001950534555444f5f53455249414c5f56455253494f4e5f5549444900066865696768745a001769735072696e74496e466972737457686f6c6542616e645a001569735072696e74526570656174656456616c7565735a001a69735072696e745768656e44657461696c4f766572666c6f77735a0015697352656d6f76654c696e655768656e426c616e6b42000c706f736974696f6e5479706542000b7374726574636854797065490005776964746849000178490001794c00096261636b636f6c6f7271007e00c84c001464656661756c745374796c6550726f76696465727400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5244656661756c745374796c6550726f76696465723b4c000c656c656d656e7447726f757071007e00c24c0009666f7265636f6c6f7271007e00c84c00036b657971007e00024c00046d6f646571007e00bf4c00096d6f646556616c756574002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4d6f6465456e756d3b4c000b706172656e745374796c6571007e00074c0018706172656e745374796c654e616d655265666572656e636571007e00024c0011706f736974696f6e5479706556616c75657400334c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f506f736974696f6e54797065456e756d3b4c00137072696e745768656e45787072657373696f6e71007e00194c00157072696e745768656e47726f75704368616e67657371007e00764c000d70726f706572746965734d617071007e001c5b001370726f706572747945787072657373696f6e737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250726f706572747945787072657373696f6e3b4c0010737472657463685479706556616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5374726574636854797065456e756d3b4c00047575696471007e002078700000c354000000100001000000000000032f0000000b000000017372000e6a6176612e6177742e436f6c6f7201a51783108f337502000546000666616c70686149000576616c75654c0002637374001b4c6a6176612f6177742f636f6c6f722f436f6c6f7253706163653b5b00096672676276616c75657400025b465b00066676616c756571007e00da787000000000fff4f4f470707071007e001071007e00c37070707e7200296e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4d6f6465456e756d00000000000000001200007871007e00127400064f504151554570707e7200316e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e506f736974696f6e54797065456e756d00000000000000001200007871007e00127400134649585f52454c41544956455f544f5f544f507371007e0080000000347571007e0083000000027371007e00850474000c5245504f52545f434f554e547371007e0085017400072532203d3d203070707070707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5374726574636854797065456e756d00000000000000001200007871007e001274000a4e4f5f535452455443487371007e0070b3d90ea0f9d2f8790b32f97431ce4dd10000c3547070707070707070707070707070707070707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654c696e65426f7800000000000027d802000b4c000d626f74746f6d50616464696e6771007e00c94c0009626f74746f6d50656e74002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f626173652f4a52426f7850656e3b4c000c626f78436f6e7461696e657274002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52426f78436f6e7461696e65723b4c000b6c65667450616464696e6771007e00c94c00076c65667450656e71007e00ed4c000770616464696e6771007e00c94c000370656e71007e00ed4c000c726967687450616464696e6771007e00c94c0008726967687450656e71007e00ed4c000a746f7050616464696e6771007e00c94c0006746f7050656e71007e00ed787070737200336e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78426f74746f6d50656e00000000000027d80200007872002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f7850656e00000000000027d80200014c00076c696e65426f7871007e00cc7872002a6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736550656e00000000000027d802000649001950534555444f5f53455249414c5f56455253494f4e5f5549444c00096c696e65436f6c6f7271007e00c84c00096c696e655374796c6571007e00bf4c000e6c696e655374796c6556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e655374796c65456e756d3b4c00096c696e6557696474687400114c6a6176612f6c616e672f466c6f61743b4c000c70656e436f6e7461696e657274002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250656e436f6e7461696e65723b78700000c3547070707071007e00ef71007e00ef71007e00d770737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f784c65667450656e00000000000027d80200007871007e00f10000c3547070707071007e00ef71007e00ef707371007e00f10000c3547070707071007e00ef71007e00ef70737200326e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78526967687450656e00000000000027d80200007871007e00f10000c3547070707071007e00ef71007e00ef70737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78546f7050656e00000000000027d80200007871007e00f10000c3547070707071007e00ef71007e00ef70707070737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736550617261677261706800000000000027d802000a4c000f66697273744c696e65496e64656e7471007e00c94c000a6c656674496e64656e7471007e00c94c000b6c696e6553706163696e6771007e00cd4c000f6c696e6553706163696e6753697a6571007e00f44c0012706172616772617068436f6e7461696e65727400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52506172616772617068436f6e7461696e65723b4c000b7269676874496e64656e7471007e00c94c000c73706163696e67416674657271007e00c94c000d73706163696e674265666f726571007e00c94c000c74616253746f70576964746871007e00c94c000874616253746f707371007e002b78707070707071007e00d770707070707070707070707070707070707400007372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654c696e6500000000000027d802000349001950534555444f5f53455249414c5f56455253494f4e5f554944420009646972656374696f6e4c000e646972656374696f6e56616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e65446972656374696f6e456e756d3b787200356e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736547726170686963456c656d656e7400000000000027d802000549001950534555444f5f53455249414c5f56455253494f4e5f5549444c000466696c6c71007e00bf4c000966696c6c56616c756574002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f46696c6c456e756d3b4c00076c696e6550656e7400234c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250656e3b4c000370656e71007e00bf7871007e00d10000c354000000010001000000000000032f0000000d000000117071007e001071007e00c37371007e00d800000000ff3d9297707070707070707071007e00e07371007e00800000003570707070707071007e00e97371007e0070bfe78f35bf02676c826bc27dc9f149cb000077ee70707371007e00f20000c3547070707372000f6a6176612e6c616e672e466c6f6174daedc9a2db3cf0ec02000146000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b02000078703f00000071007e0107700000c354007e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4c696e65446972656374696f6e456e756d00000000000000001200007871007e0012740008544f505f444f574e737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365546578744669656c6400000000000027d802001549001950534555444f5f53455249414c5f56455253494f4e5f55494449000d626f6f6b6d61726b4c6576656c42000e6576616c756174696f6e54696d6542000f68797065726c696e6b54617267657442000d68797065726c696e6b547970655a0015697353747265746368576974684f766572666c6f774c0014616e63686f724e616d6545787072657373696f6e71007e00194c000f6576616c756174696f6e47726f757071007e00764c00136576616c756174696f6e54696d6556616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4576616c756174696f6e54696d65456e756d3b4c000a65787072657373696f6e71007e00194c001968797065726c696e6b416e63686f7245787072657373696f6e71007e00194c001768797065726c696e6b5061676545787072657373696f6e71007e00195b001368797065726c696e6b506172616d65746572737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5248797065726c696e6b506172616d657465723b4c001c68797065726c696e6b5265666572656e636545787072657373696f6e71007e00194c001a68797065726c696e6b546f6f6c74697045787072657373696f6e71007e00194c001768797065726c696e6b5768656e45787072657373696f6e71007e00194c000f6973426c616e6b5768656e4e756c6c71007e00cb4c000a6c696e6b54617267657471007e00024c00086c696e6b5479706571007e00024c00077061747465726e71007e00024c00117061747465726e45787072657373696f6e71007e00197871007e00c70000c354000000110001000000000000007200000036000000007071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e0070a8a83cb2644861a38defe30eb64e47350000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e011771007e011771007e0115707371007e00f70000c3547070707071007e011771007e0117707371007e00f10000c3547070707071007e011771007e0117707371007e00fa0000c3547070707071007e011771007e0117707371007e00fc0000c3547070707071007e011771007e0117707070707371007e00fe7070707071007e011570707070707070707070707070707070700000c354000000000000000070707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4576616c756174696f6e54696d65456e756d00000000000000001200007871007e00127400034e4f577371007e0080000000367571007e0083000000017371007e00850374000b70726f647563744e616d65707070707070707070707070707371007e01120000c354000000100001000000000000007b000000a8000000027071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e007081564241daa02c6ca295cc26fddc4b690000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e012771007e012771007e0125707371007e00f70000c3547070707071007e012771007e0127707371007e00f10000c3547070707071007e012771007e0127707371007e00fa0000c3547070707071007e012771007e0127707371007e00fc0000c3547070707071007e012771007e0127707070707371007e00fe7070707071007e012570707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000377571007e0083000000017371007e00850374000c6d6178696d756d53746f636b707070707070707070707070707371007e01120000c3540000000f0001000000000000007b00000123000000027071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e007094935448d0845df5aad03813d7e44e4e0000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e013471007e013471007e0132707371007e00f70000c3547070707071007e013471007e0134707371007e00f10000c3547070707071007e013471007e0134707371007e00fa0000c3547070707071007e013471007e0134707371007e00fc0000c3547070707071007e013471007e0134707070707371007e00fe7070707071007e013270707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000387571007e0083000000017371007e00850374000c72654f726465724c6576656c707070707070707070707070707371007e01120000c354000000100001000000000000007b0000019e000000017071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e00708b1e4dd2215ee2e696c00dd4a1ca423d0000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e014171007e014171007e013f707371007e00f70000c3547070707071007e014171007e0141707371007e00f10000c3547070707071007e014171007e0141707371007e00fa0000c3547070707071007e014171007e0141707371007e00fc0000c3547070707071007e014171007e0141707070707371007e00fe7070707071007e013f70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000397571007e0083000000017371007e00850374000b73746f636b4f6e48616e64707070707070707070707070707371007e01120000c354000000100001000000000000009f00000219000000007071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e007081e7431a00ec999f699d01bbc49a43d20000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e014e71007e014e71007e014c707371007e00f70000c3547070707071007e014e71007e014e707371007e00f10000c3547070707071007e014e71007e014e707371007e00fa0000c3547070707071007e014e71007e014e707371007e00fc0000c3547070707071007e014e71007e014e707070707371007e00fe7070707071007e014c70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e00800000003a7571007e0083000000017371007e0085037400117175616e74697479526571756573746564707070707070707070707070707371007e01120000c354000000110001000000000000002b0000000b000000007371007e00d800000000ff33333370707071007e001071007e00c37371007e00d800000000ff00000070707070707e71007e00dc74000b5452414e53504152454e54707071007e00e07070707071007e00e97371007e0070833f67e15559039f8e910cc550ce49660000c354707070707074000953616e735365726966737200116a6176612e6c616e672e496e746567657212e2a0a4f781873802000149000576616c75657871007e010d0000000a707e7200346e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e486f72697a6f6e74616c416c69676e456e756d00000000000000001200007871007e00127400044c454654737200116a6176612e6c616e672e426f6f6c65616ecd207280d59cfaee0200015a000576616c756578700071007e016671007e016671007e01667071007e01667070707371007e00ec707371007e00f00000c3547070707071007e016771007e016771007e0159707371007e00f70000c3547070707071007e016771007e0167707371007e00f10000c3547070707071007e016771007e0167707371007e00fa0000c3547070707071007e016771007e0167707371007e00fc0000c3547070707071007e016771007e016770707400046e6f6e65707371007e00fe70707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4c696e6553706163696e67456e756d00000000000000001200007871007e001274000653494e474c457071007e0159707070707074000643703132353270707070707e72002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e526f746174696f6e456e756d00000000000000001200007871007e00127400044e4f4e45707070707e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e566572746963616c416c69676e456e756d00000000000000001200007871007e00127400064d4944444c450000c3540000000000000000707071007e011f7371007e00800000003b7571007e0083000000017371007e00850474000c5245504f52545f434f554e547070707070707070707070707078700000c35400000013017070707070707400046a617661707371007e00170000c3540100757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a524669656c643b023cdfc74e2af27002000078700000000d7372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654669656c6400000000000027d80200054c000b6465736372697074696f6e71007e00024c00046e616d6571007e00024c000d70726f706572746965734d617071007e001c4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e000278707074000c666163696c6974794e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400096f72646572646174657371007e002a70707074000e6a6176612e7574696c2e44617465707371007e01817074000b70726f6772616d4e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400097374617274646174657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e018170740007656e64646174657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000966697273744e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400086c6173744e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000b70726f647563744e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000f70726f6475637443617465676f72797371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000c6d6178696d756d53746f636b7371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000c72654f726465724c6576656c7371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400117175616e746974795265717565737465647371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000b73746f636b4f6e48616e647371007e002a7070707400106a6176612e6c616e672e537472696e677070757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5247726f75703b40a35f7a4cfd78ea0200007870000000027372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736547726f757000000000000027d802001049001950534555444f5f53455249414c5f56455253494f4e5f55494442000e666f6f746572506f736974696f6e5a0019697352657072696e744865616465724f6e45616368506167655a001169735265736574506167654e756d6265725a0010697353746172744e6577436f6c756d6e5a000e697353746172744e6577506167655a000c6b656570546f6765746865724900176d696e486569676874546f53746172744e6577506167654c000d636f756e745661726961626c657400284c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525661726961626c653b4c000a65787072657373696f6e71007e00194c0013666f6f746572506f736974696f6e56616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f466f6f746572506f736974696f6e456e756d3b4c000b67726f7570466f6f74657271007e00044c001267726f7570466f6f74657253656374696f6e71007e00084c000b67726f757048656164657271007e00044c001267726f757048656164657253656374696f6e71007e00084c00046e616d6571007e000278700000c354000000000000000000007371007e0074000077ee0000010071007e00957371007e0080000000097571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e00800000000a7571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c4865616465725f434f554e5471007e01bb7e71007e008974000547524f555071007e003e70707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e466f6f746572506f736974696f6e456e756d00000000000000001200007871007e00127400064e4f524d414c707371007e00b97571007e00bc000000017371007e00be7371007e00c4000000027704000000027371007e01120000c3540000000a0001000000000000001300000329000000257071007e001071007e01cd70707070707071007e00e07070707071007e00e97371007e0070bbf9a5527705d801477ffc9d22bf46f50000c3547070707070707371007e01600000000870707070707070707070707371007e00ec707371007e00f00000c3547070707071007e01d271007e01d271007e01cf707371007e00f70000c3547070707071007e01d271007e01d2707371007e00f10000c3547070707071007e01d271007e01d2707371007e00fa0000c3547070707071007e01d271007e01d2707371007e00fc0000c3547070707071007e01d271007e01d2707070707371007e00fe7070707071007e01cf70707070707070707070707070707070700000c354000000000000000070707e71007e011e7400065245504f52547371007e00800000001c7571007e0083000000027371007e008501740006222022202b207371007e00850474000b504147455f4e554d424552707070707070707070707070707371007e01120000c3540000000a000100000000000000c700000262000000257071007e001071007e01cd70707070707071007e00e07070707071007e00e97371007e0070843f39a0c2e70009c6758d1a4ca348990000c35470707070707071007e01d1707e71007e016274000552494748547070707070707070707371007e00ec707371007e00f00000c3547070707071007e01e571007e01e571007e01e1707371007e00f70000c3547070707071007e01e571007e01e5707371007e00f10000c3547070707071007e01e571007e01e5707371007e00fa0000c3547070707071007e01e571007e01e5707371007e00fc0000c3547070707071007e01e571007e01e5707070707371007e00fe7070707071007e01e170707070707070707070707070707070700000c3540000000000000001707071007e011f7371007e00800000001d7571007e0083000000057371007e0085017400046d7367287371007e00850574000d6c6162656c2e706167652e6f667371007e0085017400022c207371007e00850474000b504147455f4e554d4245527371007e0085017400012970707070707070707070707400007078700000c3540000002f0170707070707371007e00b97571007e00bc000000027371007e00be7371007e00c4000000017704000000017371007e01120000c35400000032000100000000000002da0000000b000000007071007e001071007e01fb70707070707071007e00e0707070707e71007e00e874001a52454c41544956455f544f5f54414c4c4553545f4f424a4543547371007e00708f61a735ab2074b7212194e972ca43210000c354707070707074000953616e7353657269667371007e016000000010707e71007e016274000643454e544552707070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e020571007e020571007e01fd707371007e00f70000c3547070707071007e020571007e0205707371007e00f10000c3547070707071007e020571007e0205707371007e00fa0000c3547070707071007e020571007e0205707371007e00fc0000c3547070707071007e020571007e0205707070707371007e00fe7070707071007e01fd7070707070707070707070707070707071007e01770000c3540000000000000001707071007e011f7371007e0080000000127571007e0083000000057371007e0085017400046d7367287371007e00850574002c6c6162656c2e7469746c652e76616363696e652e6f726465722e7265717569736974696f6e2e7265706f72747371007e0085017400012c7371007e00850374000b70726f6772616d4e616d657371007e008501740001297070707070707070707070707078700000c35400000032017371007e0080000000117571007e0083000000027371007e00850474000b504147455f4e554d4245527371007e0085017400382e696e7456616c75652829203d3d20313f206e657720426f6f6c65616e287472756529203a206e657720426f6f6c65616e2866616c73652970707070707371007e00be7371007e00c4000000057704000000057372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654672616d6500000000000027d80200114c0006626f7264657271007e00bf4c000b626f72646572436f6c6f7271007e00c84c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e6771007e00c94c00086368696c6472656e71007e002b4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f7871007e00cc4c000770616464696e6771007e00c94c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c97871007e00d10000c354000000510001000000000000032f0000000b000000007371007e00d800000000ffffffff70707071007e001071007e021e7371007e00d800000000ff3d92977070707070707372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655374796c65000000000000271102003a49001950534555444f5f53455249414c5f56455253494f4e5f5549445a0009697344656661756c744c00096261636b636f6c6f7271007e00c84c0006626f7264657271007e00bf4c000b626f72646572436f6c6f7271007e00c84c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e6771007e00c95b0011636f6e646974696f6e616c5374796c65737400315b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52436f6e646974696f6e616c5374796c653b4c001464656661756c745374796c6550726f766964657271007e00d24c000466696c6c71007e00bf4c000966696c6c56616c756571007e01054c0008666f6e744e616d6571007e00024c0008666f6e7453697a6571007e00c94c0009666f7265636f6c6f7271007e00c84c0013686f72697a6f6e74616c416c69676e6d656e7471007e00bf4c0018686f72697a6f6e74616c416c69676e6d656e7456616c756571007e00ca4c000f6973426c616e6b5768656e4e756c6c71007e00cb4c00066973426f6c6471007e00cb4c000869734974616c696371007e00cb4c000d6973506466456d62656464656471007e00cb4c000f6973537472696b655468726f75676871007e00cb4c000c69735374796c65645465787471007e00cb4c000b6973556e6465726c696e6571007e00cb4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f7871007e00cc4c00076c696e6550656e71007e01064c000b6c696e6553706163696e6771007e00bf4c00106c696e6553706163696e6756616c756571007e00cd4c00066d61726b757071007e00024c00046d6f646571007e00bf4c00096d6f646556616c756571007e00d34c00046e616d6571007e00024c000770616464696e6771007e00c94c000970617261677261706871007e00ce4c000b706172656e745374796c6571007e00074c0018706172656e745374796c654e616d655265666572656e636571007e00024c00077061747465726e71007e00024c000b706466456e636f64696e6771007e00024c000b706466466f6e744e616d6571007e00024c000370656e71007e00bf4c000c706f736974696f6e5479706571007e00bf4c000672616469757371007e00c94c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c0008726f746174696f6e71007e00bf4c000d726f746174696f6e56616c756571007e00cf4c000a7363616c65496d61676571007e00bf4c000f7363616c65496d61676556616c75657400314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5363616c65496d616765456e756d3b4c000b737472657463685479706571007e00bf4c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c94c0011766572746963616c416c69676e6d656e7471007e00bf4c0016766572746963616c416c69676e6d656e7456616c756571007e00d078700000c354007371007e00d800000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e022971007e022971007e0227707371007e00f70000c3547070707071007e022971007e0229707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e022971007e0229707371007e00fa0000c3547070707071007e022971007e0229707371007e00fc0000c3547070707071007e022971007e02297371007e00f20000c3547070707071007e02277070707071007e00dd7400087461626c655f5448707371007e00fe7070707071007e022770707070707070707070707070707070707070707070707070707071007e00e07070707071007e00e97371007e0070aab0863025404974605cceed5add40d170707070707371007e00c4000000087704000000087371007e01120000c354000000140101000000000000006c00000000000000037071007e001071007e022170707070707071007e00e07070707071007e00e97371007e0070a9626dd2252913ef3d3d4b9482f74b5b0000c3547070707070707371007e01600000000c70707371007e0165017070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e023a71007e023a71007e0236707371007e00f70000c3547070707071007e023a71007e023a707371007e00f10000c3547070707071007e023a71007e023a707371007e00fa0000c3547070707071007e023a71007e023a707371007e00fc0000c3547070707071007e023a71007e023a707070707371007e00fe7070707071007e023670707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000147571007e0083000000017371007e00850574000a6c6162656c2e6e616d65707070707070707070707070707371007e01120000c354000000140101000000000000006c00000000000000177071007e001071007e022170707070707071007e00e07070707071007e00e97371007e007095e34a3d34d62cdee9ad75ffe3a04d900000c35470707070707071007e0238707071007e02397070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e024771007e024771007e0245707371007e00f70000c3547070707071007e024771007e0247707371007e00f10000c3547070707071007e024771007e0247707371007e00fa0000c3547070707071007e024771007e0247707371007e00fc0000c3547070707071007e024771007e0247707070707371007e00fe7070707071007e024570707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000157571007e0083000000017371007e00850574000a6c6162656c2e66726f6d707070707070707070707070707371007e01120000c354000000140101000000000000006c000000000000002b7071007e001071007e022170707070707071007e00e07070707071007e00e97371007e007091077b3feda429dfc33894c16bff4ce80000c35470707070707071007e0238707071007e02397070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e025471007e025471007e0252707371007e00f70000c3547070707071007e025471007e0254707371007e00f10000c3547070707071007e025471007e0254707371007e00fa0000c3547070707071007e025471007e0254707371007e00fc0000c3547070707071007e025471007e0254707070707371007e00fe7070707071007e025270707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000167571007e0083000000017371007e00850574000a6c6162656c2e64617465707070707070707070707070707371007e00c60000c354000000140001000000000000000f0000006c000000037071007e001071007e022170707070707071007e00e07070707071007e00e97371007e0070b10418c61404d5460ddeb01912d84bb90000c3547070707070707371007e01600000000d7071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e026271007e026271007e025f707371007e00f70000c3547070707071007e026271007e0262707371007e00f10000c3547070707071007e026271007e0262707371007e00fa0000c3547070707071007e026271007e0262707371007e00fc0000c3547070707071007e026271007e0262707070707371007e00fe7070707071007e025f707070707070707070707070707070707e71007e0176740003544f5074000520203a20207371007e00c60000c354000000140001000000000000000f0000006c000000177071007e001071007e022170707070707071007e00e07070707071007e00e97371007e0070af356388283b2d2c86d202711f404af90000c35470707070707071007e02617071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e026e71007e026e71007e026c707371007e00f70000c3547070707071007e026e71007e026e707371007e00f10000c3547070707071007e026e71007e026e707371007e00fa0000c3547070707071007e026e71007e026e707371007e00fc0000c3547070707071007e026e71007e026e707070707371007e00fe7070707071007e026c7070707070707070707070707070707071007e026974000520203a20207371007e00c60000c354000000140001000000000000000f0000006c0000002b7071007e001071007e022170707070707071007e00e07070707071007e00e97371007e00709b8aae125bf14b535a3b255b5a4149790000c35470707070707071007e02617071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e027871007e027871007e0276707371007e00f70000c3547070707071007e027871007e0278707371007e00f10000c3547070707071007e027871007e0278707371007e00fa0000c3547070707071007e027871007e0278707371007e00fc0000c3547070707071007e027871007e0278707070707371007e00fe7070707071007e02767070707070707070707070707070707071007e026974000520203a20207371007e01120000c35400000014010100000000000000ec0000007b000000037071007e001071007e022170707070707071007e00e07070707071007e00e97371007e0070bbf2b5ba76b472429a331883a24f45fa0000c35470707070707071007e02387070707070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e028271007e028271007e0280707371007e00f70000c3547070707071007e028271007e0282707371007e00f10000c3547070707071007e028271007e0282707371007e00fa0000c3547070707071007e028271007e0282707371007e00fc0000c3547070707071007e028271007e0282707070707371007e00fe7070707071007e028070707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000177571007e0083000000037371007e00850374000966697273744e616d657371007e0085017400072b202220222b207371007e0085037400086c6173744e616d65707070707070707070707070707371007e01120000c35400000014010100000000000000ec0000007b000000177071007e001071007e022170707070707071007e00e07070707071007e00e97371007e0070acfb8e262215827d219fe37117e14ab40000c35470707070707071007e02387070707070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e029371007e029371007e0291707371007e00f70000c3547070707071007e029371007e0293707371007e00f10000c3547070707071007e029371007e0293707371007e00fa0000c3547070707071007e029371007e0293707371007e00fc0000c3547070707071007e029371007e0293707070707371007e00fe7070707071007e02917070707070707070707070707070707071007e01770000c3540000000000000001707071007e011f7371007e0080000000187571007e0083000000017371007e00850374000c666163696c6974794e616d6570707070707070707070707070787070707371007e00ec707371007e00f00000c3547070707071007e029e71007e029e71007e0221707371007e00f70000c3547070707071007e029e71007e029e707371007e00f10000c3547070707071007e029e71007e029e707371007e00fa0000c3547070707071007e029e71007e029e707371007e00fc0000c3547070707071007e029e71007e029e707070707070707371007e01120000c35400000014010100000000000000ec000000860000002b7071007e001071007e021e7070707070707e71007e00df740005464c4f41547070707071007e00e97371007e00708d5ba56fb297c06b5bca3f3a1c7a4a3b0000c354707070707074000953616e73536572696671007e02387071007e0163707070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e02a971007e02a971007e02a4707371007e00f70000c3547070707071007e02a971007e02a9707371007e00f10000c3547070707071007e02a971007e02a9707371007e00fa0000c3547070707071007e02a971007e02a9707371007e00fc0000c3547070707071007e02a971007e02a9707070707371007e00fe7070707071007e02a47070707070707070707070707070707071007e01770000c3540000000000000001707071007e011f7371007e0080000000197571007e0083000000017371007e0085017400146e6577206a6176612e7574696c2e446174652829707070707070707070707074000a64642d4d4d2d79797979707371007e01120000c354000000120101000000000000006c0000000b0000003f7071007e001071007e021e70707070707071007e00e07070707071007e00e97371007e0070b25101f05f2ae792290c6493c0dd41f40000c35470707070707071007e0238707071007e02397070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e02b771007e02b771007e02b5707371007e00f70000c3547070707071007e02b771007e02b7707371007e00f10000c3547070707071007e02b771007e02b7707371007e00fa0000c3547070707071007e02b771007e02b7707371007e00fc0000c3547070707071007e02b771007e02b7707070707371007e00fe7070707071007e02b570707070707070707070707070707070700000c3540000000000000001707071007e011f7371007e00800000001a7571007e0083000000017371007e00850574001e6c6162656c2e666163696c6974792e7265706f7274696e67506572696f64707070707070707070707070707371007e00c60000c354000000140001000000000000000f000000770000003d7071007e001071007e021e70707070707071007e00e07070707071007e00e97371007e00708ec325ededa54c56bb710ecede6c47bc0000c35470707070707071007e02617071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e02c471007e02c471007e02c2707371007e00f70000c3547070707071007e02c471007e02c4707371007e00f10000c3547070707071007e02c471007e02c4707371007e00fa0000c3547070707071007e02c471007e02c4707371007e00fc0000c3547070707071007e02c471007e02c4707070707371007e00fe7070707071007e02c27070707070707070707070707070707071007e026974000520203a20207371007e01120000c35400000012010100000000000000ec000000860000003f7071007e001071007e021e70707070707071007e00e07070707071007e00e97371007e00708cb4014dd4704077c2d7f24d862a4da90000c35470707070707071007e02387070707070707071007e01667070707371007e00ec707371007e00f00000c3547070707071007e02ce71007e02ce71007e02cc707371007e00f70000c3547070707071007e02ce71007e02ce707371007e00f10000c3547070707071007e02ce71007e02ce707371007e00fa0000c3547070707071007e02ce71007e02ce707371007e00fc0000c3547070707071007e02ce71007e02ce707070707371007e00fe7070707071007e02cc707070707070707070707070707070707e71007e0176740006424f54544f4d0000c3540000000000000001707071007e011f7371007e00800000001b7571007e0083000000037371007e0085037400097374617274646174657371007e008501740009202b22202d2022202b7371007e008503740007656e6464617465707070707070707070707074000a64642f4d4d2f797979797078700000c35400000053017371007e0080000000137571007e0083000000027371007e00850474000b504147455f4e554d4245527371007e0085017400382e696e7456616c75652829203d3d20313f206e657720426f6f6c65616e287472756529203a206e657720426f6f6c65616e2866616c73652970707070707400064865616465727371007e01b80000c354000000000000000000007371007e0074000077ee0000010071007e00957371007e00800000000b7571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e00800000000c7571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074001e47726f75702062792050726f6475637443617465676f72795f434f554e5471007e02e771007e01c671007e003e707371007e00800000001e7571007e0083000000017371007e00850374000f70726f6475637443617465676f7279707071007e01c9707371007e00b97571007e00bc000000017371007e00be7371007e00c400000013770400000013737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736552656374616e676c6500000000000027d80200014c000672616469757371007e00c97871007e01040000c354000000b70001000000000000032d0000000d000000097071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e007082d33a204b248b0cf5679f44951b47b0000077ee70707371007e00f20000c3547070707071007e02fb70707371007e01120000c354000000150001000000000000009b0000000d000000097371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070b8cabee881686723bd180386f3a34ef60000c35470707070707071007e0238707071007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e030371007e030371007e02ff707371007e00f70000c3547070707071007e030371007e0303707371007e00f10000c3547070707071007e030371007e0303707371007e00fa0000c3547070707071007e030371007e0303707371007e00fc0000c3547070707071007e030371007e0303707070707371007e00fe7070707071007e02ff7070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e0080000000257571007e0083000000017371007e0085057400126c6162656c2e7265717565737465642e6279707070707070707070707070707371007e01020000c35400000001000100000000000000f6000000a80000001d7071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e0070967d09307996c1aa42f44448a1c84b46000077ee70707371007e00f20000c3547070707071007e030e700000c3540071007e01107371007e01120000c35400000014000100000000000000f6000000a80000000a7071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e0070ba09f8314956b372d07d6d8844e344230000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e031371007e031371007e0311707371007e00f70000c3547070707071007e031371007e0313707371007e00f10000c3547070707071007e031371007e0313707371007e00fa0000c3547070707071007e031371007e0313707371007e00fc0000c3547070707071007e031371007e0313707070707371007e00fe7070707071007e03117070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e0080000000267571007e0083000000037371007e00850374000966697273744e616d657371007e008501740006202b2220222b7371007e0085037400086c6173744e616d65707070707070707070707070707371007e01120000c354000000160001000000000000009b0000000d0000001e7371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e00709a62b80659c9bfade8295969b92249070000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e032671007e032671007e0322707371007e00f70000c3547070707071007e032671007e0326707371007e00f10000c3547070707071007e032671007e0326707371007e00fa0000c3547070707071007e032671007e0326707371007e00fc0000c3547070707071007e032671007e0326707070707371007e00fe7070707071007e03227070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e0080000000277571007e0083000000017371007e00850574000f6c6162656c2e7369676e6174757265707070707070707070707070707371007e01020000c35400000001000100000000000000f6000000a8000000337071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e0070a70de29f327d0e78225b8863a2924a41000077ee70707371007e00f20000c3547070707071007e0331700000c3540071007e01107371007e01120000c354000000140001000000000000009b0000000d000000337371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e007087c86f81d12710183d79a1aa3ae047490000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e033871007e033871007e0334707371007e00f70000c3547070707071007e033871007e0338707371007e00f10000c3547070707071007e033871007e0338707371007e00fa0000c3547070707071007e033871007e0338707371007e00fc0000c3547070707071007e033871007e0338707070707371007e00fe7070707071007e03347070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e0080000000287571007e0083000000017371007e00850574000a6c6162656c2e64617465707070707070707070707070707371007e01020000c35400000001000100000000000000f6000000a8000000467071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e00708e8f0244657b7a4b31b459bd96de4d7f000077ee70707371007e00f20000c3547070707071007e0343700000c3540071007e01107371007e01020000c35400000001000100000000000000f0000001fc0000001e7071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e00708f39da880abf4d75926ea451cf79437f000077ee70707371007e00f20000c3547070707071007e0346700000c3540071007e01107371007e01120000c35400000012000100000000000000f0000001fc000000347371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070b934edf2fd2dd3773cf77c080c3c47e30000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e034d71007e034d71007e0349707371007e00f70000c3547070707071007e034d71007e034d707371007e00f10000c3547070707071007e034d71007e034d707371007e00fa0000c3547070707071007e034d71007e034d707371007e00fc0000c3547070707071007e034d71007e034d707070707371007e00fe7070707071007e03497070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e0080000000297571007e0083000000017371007e0085057400146c6162656c2e6f6666696369616c2e7374616d70707070707070707070707070707371007e01020000c354000000010001000000000000032c00000010000000597071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e007099ce451fa8de10bccc2b2263c7f5415f000077ee70707371007e00f20000c3547070707071007e0358700000c3540071007e01107371007e01120000c3540000001400010000000000000098000000100000005a7371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e007083e787fffc74911c6c98fe739a2e4ec00000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e035f71007e035f71007e035b707371007e00f70000c3547070707071007e035f71007e035f707371007e00f10000c3547070707071007e035f71007e035f707371007e00fa0000c3547070707071007e035f71007e035f707371007e00fc0000c3547070707071007e035f71007e035f707070707371007e00fe7070707071007e035b7070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e00800000002a7571007e0083000000017371007e00850574001b6c6162656c2e666f722e6f6666696369616c2e7573652e6f6e6c79707070707070707070707070707371007e01120000c354000000140001000000000000009800000010000000757371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070a227e0ffa5c590a6ef99e0fef92a45300000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e036e71007e036e71007e036a707371007e00f70000c3547070707071007e036e71007e036e707371007e00f10000c3547070707071007e036e71007e036e707371007e00fa0000c3547070707071007e036e71007e036e707371007e00fc0000c3547070707071007e036e71007e036e707070707371007e00fe7070707071007e036a70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e00800000002b7571007e0083000000017371007e0085057400136c6162656c2e617574686f72697a65642e6279707070707070707070707070707371007e01120000c354000000140001000000000000009800000010000000897371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070936097dd4b679f0c2f9bf533abe14f340000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e037d71007e037d71007e0379707371007e00f70000c3547070707071007e037d71007e037d707371007e00f10000c3547070707071007e037d71007e037d707371007e00fa0000c3547070707071007e037d71007e037d707371007e00fc0000c3547070707071007e037d71007e037d707070707371007e00fe7070707071007e03797070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e00800000002c7571007e0083000000017371007e00850574000f6c6162656c2e7369676e6174757265707070707070707070707070707371007e01120000c3540000001400010000000000000098000000100000009d7371007e00d800000000ff3d929770707071007e001071007e02f87371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070a95da2875d0627528840d90a2f8f4c8c0000c35470707070707071007e026170707070707070707070707371007e00ec707371007e00f00000c3547070707071007e038c71007e038c71007e0388707371007e00f70000c3547070707071007e038c71007e038c707371007e00f10000c3547070707071007e038c71007e038c707371007e00fa0000c3547070707071007e038c71007e038c707371007e00fc0000c3547070707071007e038c71007e038c707070707371007e00fe7070707071007e03887070707070707070707070707070707071007e02d50000c3540000000000000000707071007e011f7371007e00800000002d7571007e0083000000017371007e00850574000a6c6162656c2e64617465707070707070707070707070707371007e01020000c35400000001000100000000000000f5000000a90000006e7071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e0070a69cac7de276551011e6f4f5b81a4f65000077ee70707371007e00f20000c3547070707071007e0397700000c3540071007e01107371007e01020000c35400000001000100000000000000f6000000a8000000877071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e00708d75fd5d1948dbdc5786abae09cd4441000077ee70707371007e00f20000c3547070707071007e039a700000c3540071007e01107371007e01020000c35400000001000100000000000000f8000000a60000009e7071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e00709e2cec59b18250a7e85e5be382e74bb2000077ee70707371007e00f20000c3547070707071007e039d700000c3540071007e01107371007e01020000c35400000001000100000000000000f8000000a6000000ae7071007e001071007e02f870707070707071007e00e07070707071007e00e97371007e0070b592e480b4736c98bc596f3819f747d6000077ee70707371007e00f20000c3547070707071007e03a0700000c3540071007e011078700000c354000000c20170707070707371007e00b97571007e00bc000000017371007e00be7371007e00c4000000077704000000077371007e00c60000c354000000140001000000000000032f0000000b000000007371007e00d800000000ff3d929770707071007e001071007e03a57371007e00d800000000ffffffff707070707071007e00dd707071007e00e070707371007e002a707371007e00c40000000177040000000174002f6e65742e73662e6a61737065727265706f7274732e6578706f72742e786c732e69676e6f72652e677261706869637378737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f400000000000037708000000040000000171007e03ac74000566616c7365787071007e00e97371007e00708bcb2b288460cec0424bfe2aed0446130000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e03b171007e03b171007e03a7707371007e00f70000c3547070707071007e03b171007e03b1707371007e00f10000c3547070707071007e03b171007e03b1707371007e00fa0000c3547070707071007e03b171007e03b1707371007e00fc0000c3547070707071007e03b171007e03b1707070707371007e00fe7070707071007e03a770707070707070707070707070707070707400007371007e01120000c354000000140001000000000000002b0000000b000000007071007e001071007e03a570707070707071007e00e07070707071007e00e97371007e007085440aa452e2bc1a9504ccc64b9f46d40000c354707070707070707071007e01637070707070707070707371007e00ec707371007e00f00000c3547070707071007e03bb71007e03bb71007e03b9707371007e00f70000c3547070707071007e03bb71007e03bb707371007e00f10000c3547070707071007e03bb71007e03bb707371007e00fa0000c3547070707071007e03bb71007e03bb707371007e00fc0000c3547070707071007e03bb71007e03bb707070707371007e00fe707371007e016000000005707071007e03b97070707070707070707070707070707071007e01770000c3540000000000000000707071007e011f7371007e00800000001f7571007e0083000000017371007e00850574000d6c6162656c2e6c696e652e6e6f707070707070707070707070707371007e01120000c354000000130001000000000000007200000036000000017371007e00d800000000ff33333370707071007e001071007e03a57371007e00d800000000ff000000707070707071007e015c707071007e00e07070707071007e00e97371007e0070b206fb110a9077bed5ba5f52522e4dc30000c354707070707074000953616e73536572696671007e01617071007e016371007e016671007e016671007e016671007e01667071007e01667070707371007e00ec707371007e00f00000c3547070707071007e03cc71007e03cc71007e03c7707371007e00f70000c3547070707071007e03cc71007e03cc707371007e00f10000c3547070707071007e03cc71007e03cc707371007e00fa0000c3547070707071007e03cc71007e03cc707371007e00fc0000c3547070707071007e03cc71007e03cc70707400046e6f6e65707371007e00fe707071007e01707071007e03c77070707070740006437031323532707070707071007e01747070707071007e01770000c3540000000000000000707071007e011f7371007e0080000000207571007e0083000000017371007e00850574000d6c6162656c2e70726f64756374707070707070707070707070707371007e01120000c354000000130001000000000000007b000000a8000000017371007e00d800000000ff33333370707071007e001071007e03a57371007e00d800000000ff000000707070707071007e015c707071007e00e07070707071007e00e97371007e00708623ed452732b0d5f339fff196f245be0000c354707070707074000953616e73536572696671007e01617071007e016371007e016671007e016671007e016671007e01667071007e01667070707371007e00ec707371007e00f00000c3547070707071007e03de71007e03de71007e03d9707371007e00f70000c3547070707071007e03de71007e03de707371007e00f10000c3547070707071007e03de71007e03de707371007e00fa0000c3547070707071007e03de71007e03de707371007e00fc0000c3547070707071007e03de71007e03de70707400046e6f6e65707371007e00fe707071007e01707071007e03d97070707070740006437031323532707070707071007e01747070707071007e01770000c3540000000000000000707071007e011f7371007e0080000000217571007e0083000000017371007e0085057400136c6162656c2e6d6178696d756d2e73746f636b707070707070707070707070707371007e01120000c354000000130001000000000000007b00000123000000007371007e00d800000000ff33333370707071007e001071007e03a57371007e00d800000000ff000000707070707071007e015c707071007e00e07070707071007e00e97371007e0070a264ef23b0f410d9967e5de0b7f441af0000c354707070707074000953616e73536572696671007e01617071007e016371007e016671007e016671007e016671007e01667071007e01667070707371007e00ec707371007e00f00000c3547070707071007e03f071007e03f071007e03eb707371007e00f70000c3547070707071007e03f071007e03f0707371007e00f10000c3547070707071007e03f071007e03f0707371007e00fa0000c3547070707071007e03f071007e03f0707371007e00fc0000c3547070707071007e03f071007e03f070707400046e6f6e65707371007e00fe707071007e01707071007e03eb7070707070740006437031323532707070707071007e01747070707071007e01770000c3540000000000000000707071007e011f7371007e0080000000227571007e0083000000017371007e0085057400136c6162656c2e72656f726465722e6c6576656c707070707070707070707070707371007e01120000c354000000130001000000000000007b0000019e000000007371007e00d800000000ff33333370707071007e001071007e03a57371007e00d800000000ff000000707070707071007e015c707071007e00e07070707071007e00e97371007e0070b6c4133c9db85870607e2e6657b04dca0000c354707070707074000953616e73536572696671007e01617071007e016371007e016671007e016671007e016671007e01667071007e01667070707371007e00ec707371007e00f00000c3547070707071007e040271007e040271007e03fd707371007e00f70000c3547070707071007e040271007e0402707371007e00f10000c3547070707071007e040271007e0402707371007e00fa0000c3547070707071007e040271007e0402707371007e00fc0000c3547070707071007e040271007e040270707400046e6f6e65707371007e00fe707071007e01707071007e03fd7070707070740006437031323532707070707071007e01747070707071007e01770000c3540000000000000000707071007e011f7371007e0080000000237571007e0083000000017371007e00850574001a6c696e6b2e76616363696e652e73746f636b2e6f6e2e68616e64707070707070707070707070707371007e01120000c354000000130001000000000000009f00000219000000017371007e00d800000000ff33333370707071007e001071007e03a57371007e00d800000000ff000000707070707071007e015c707071007e00e07070707071007e00e97371007e0070a862e3d96339d460990a518e4e724caa0000c354707070707074000953616e73536572696671007e01617071007e016371007e016671007e016671007e016671007e01667071007e01667070707371007e00ec707371007e00f00000c3547070707071007e041471007e041471007e040f707371007e00f70000c3547070707071007e041471007e0414707371007e00f10000c3547070707071007e041471007e0414707371007e00fa0000c3547070707071007e041471007e0414707371007e00fc0000c3547070707071007e041471007e041470707400046e6f6e65707371007e00fe707071007e01707071007e040f7070707070740006437031323532707070707071007e01747070707071007e01770000c3540000000000000000707071007e011f7371007e0080000000247571007e0083000000017371007e0085057400186c6162656c2e7175616e746974792e7265717565737465647070707070707070707070707078700000c35400000014017070707074001847726f75702062792050726f6475637443617465676f72797400077265706f7274317571007e0025000000167371007e00270101707071007e0029707371007e002a70707071007e002e707371007e00270101707071007e0030707371007e002a70707071007e0032707371007e00270101707071007e0034707371007e002a70707071007e0036707371007e00270101707071007e0038707371007e002a70707071007e003a707371007e00270101707071007e003c707371007e002a70707071007e003e707371007e00270101707071007e0040707371007e002a70707071007e0042707371007e00270101707071007e0044707371007e002a70707071007e0046707371007e00270101707071007e0048707371007e002a70707071007e004a707371007e00270101707071007e004c707371007e002a70707071007e004e707371007e00270101707071007e0050707371007e002a70707071007e0052707371007e00270101707071007e0054707371007e002a70707071007e0056707371007e00270101707071007e0058707371007e002a70707071007e005a707371007e00270101707071007e005c707371007e002a70707071007e005e707371007e00270101707071007e0060707371007e002a70707071007e0062707371007e00270101707071007e0064707371007e002a70707071007e0066707371007e00270101707071007e0068707371007e002a70707071007e006a707371007e00270101707071007e006c707371007e002a70707071007e006e707371007e0027010170707400125245504f52545f5649525455414c495a4552707371007e002a7070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525669727475616c697a6572707371007e00270101707074001449535f49474e4f52455f504147494e4154494f4e707371007e002a7070707400116a6176612e6c616e672e426f6f6c65616e707371007e0027010070707400084f524445525f4944707371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e002700007371007e00800000000070707070740009696d6167655f646972707371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e00270100707074000d4f50455241544f525f4e414d45707371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e002a707371007e00c40000000377040000000374000c697265706f72742e7a6f6f6d740009697265706f72742e78740009697265706f72742e79787371007e03ad3f400000000000037708000000040000000371007e045f74000334363871007e045d740003312e3571007e045e74000130787372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365517565727900000000000027d80200025b00066368756e6b7374002b5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5251756572794368756e6b3b4c00086c616e677561676571007e000278707572002b5b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5251756572794368756e6b3b409f00a1e8ba34a4020000787000000003737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736551756572794368756e6b00000000000027d8020004420004747970654c00047465787471007e00024c000e746f6b656e536570617261746f727400154c6a6176612f6c616e672f4368617261637465723b5b0006746f6b656e737400135b4c6a6176612f6c616e672f537472696e673b78700174020473656c6563740a20752e66697273744e616d652c752e6c6173744e616d652c2020662e6e616d6520666163696c6974794e616d652c702e6e616d652070726f6772616d4e616d652c70702e7374617274646174652c70702e656e64446174652c722e6f72646572646174652c0a0a206c692e6d6178696d756d53746f636b2c2072656f726465724c6576656c2c73746f636b4f6e48616e642c7175616e746974795265717565737465642c2070726f647563744e616d652c70726f6475637443617465676f72790a2066726f6d2076616363696e655f6f726465725f7265717569736974696f6e7320720a20696e6e6572206a6f696e20666163696c69746965732066206f6e20722e666163696c6974794964203d20462e69640a20696e6e6572206a6f696e2070726f63657373696e675f706572696f6473207070206f6e20722e706572696f644964203d2050502e49440a20696e6e6572206a6f696e2070726f6772616d732070206f6e20722e70726f6772616d4964203d20502e69640a20696e6e6572206a6f696e207573657273207520206f6e2020722e637265617465644279203d20752e69640a20696e6e6572206a6f696e2076616363696e655f6f726465725f7265717569736974696f6e5f6c696e655f6974656d73206c69206f6e20722e6964203d206c692e6f7264657249640a0a20574845524520722e6964203d2070707371007e0469027400084f524445525f494470707371007e04690174002020616e642070726f6475637443617465676f7279206973206e6f74204e756c6c707074000373716c707070707371007e0070b3d554b1aefe96cea0a4e8610726422f7571007e00720000000b7371007e0074000077ee0000010071007e007b707071007e007e70707371007e0080000000017571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283129707071007e00887071007e008a71007e003e707371007e0074000077ee0000010071007e007b707071007e007e70707371007e0080000000027571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283129707071007e00917071007e009271007e003e707371007e0074000077ee0000010071007e00957371007e0080000000037571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000047571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707071007e009f7071007e008a71007e003e707371007e0074000077ee0000010071007e00957371007e0080000000057571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000067571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707071007e00a97071007e009271007e003e707371007e0074000077ee0000010071007e00957371007e0080000000077571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000087571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707071007e00b37071007e00b471007e003e7071007e01bc71007e02e87371007e0074000077ee000000007e71007e007a7400074e4f5448494e477371007e00800000000d7571007e0083000000017371007e00850374000966697273744e616d6570707071007e007e70707074000a7646697273744e616d657071007e008a7400106a6176612e6c616e672e537472696e67707371007e0074000077ee0000000071007e049b7371007e00800000000e7571007e0083000000017371007e0085037400086c6173744e616d6570707071007e007e707070740009764c6173744e616d657071007e008a7400106a6176612e6c616e672e537472696e67707371007e0074000077ee0000000071007e049b7371007e00800000000f7571007e0083000000017371007e00850374000973746172746461746570707071007e007e70707074000a765374617274446174657071007e008a7400106a6176612e6c616e672e537472696e67707371007e0074000077ee0000000071007e049b7371007e0080000000107571007e0083000000017371007e008503740007656e646461746570707071007e007e70707074000876456e64446174657071007e008a7400106a6176612e6c616e672e537472696e677071007e00b771007e0422707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4f7269656e746174696f6e456e756d00000000000000001200007871007e00127400094c414e445343415045707371007e00be7371007e00c4000000057704000000057372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365496d61676500000000000027d802002b49001950534555444f5f53455249414c5f56455253494f4e5f55494449000d626f6f6b6d61726b4c6576656c42000e6576616c756174696f6e54696d6542000f68797065726c696e6b54617267657442000d68797065726c696e6b547970655a000669734c617a7942000b6f6e4572726f72547970654c0014616e63686f724e616d6545787072657373696f6e71007e00194c0006626f7264657271007e00bf4c000b626f72646572436f6c6f7271007e00c84c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e6771007e00c94c000f6576616c756174696f6e47726f757071007e00764c00136576616c756174696f6e54696d6556616c756571007e01134c000a65787072657373696f6e71007e00194c0013686f72697a6f6e74616c416c69676e6d656e7471007e00bf4c0018686f72697a6f6e74616c416c69676e6d656e7456616c756571007e00ca4c001968797065726c696e6b416e63686f7245787072657373696f6e71007e00194c001768797065726c696e6b5061676545787072657373696f6e71007e00195b001368797065726c696e6b506172616d657465727371007e01144c001c68797065726c696e6b5265666572656e636545787072657373696f6e71007e00194c001a68797065726c696e6b546f6f6c74697045787072657373696f6e71007e00194c001768797065726c696e6b5768656e45787072657373696f6e71007e00194c000c69735573696e67436163686571007e00cb4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f7871007e00cc4c000a6c696e6b54617267657471007e00024c00086c696e6b5479706571007e00024c00106f6e4572726f725479706556616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4f6e4572726f7254797065456e756d3b4c000770616464696e6771007e00c94c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c000a7363616c65496d61676571007e00bf4c000f7363616c65496d61676556616c756571007e02264c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c94c0011766572746963616c416c69676e6d656e7471007e00bf4c0016766572746963616c416c69676e6d656e7456616c756571007e00d07871007e01040000c3540000003e000100000000000000500000000b000000027071007e001071007e04bb70707070707071007e00e07070707071007e00e97371007e0070a6c1f5d3ce283103595f07640a1942cc000077ee70707371007e00f20000c3547070707071007e04bf700000c3540000000000000000007070707070707071007e011f7371007e00800000002f7571007e0083000000027371007e008502740009696d6167655f6469727371007e00850174000f2b20226c6f676f2d747a2e706e672270707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04c871007e04c871007e04bf707371007e00f70000c3547070707071007e04c871007e04c8707371007e00f10000c3547070707071007e04c871007e04c8707371007e00fa0000c3547070707071007e04c871007e04c8707371007e00fc0000c3547070707071007e04c871007e04c870707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4f6e4572726f7254797065456e756d00000000000000001200007871007e00127400054552524f5270707070707070707070707371007e04bd0000c354000000400001000000000000004a000002f2000000007071007e001071007e04bb70707070707071007e00e07070707071007e00e97371007e0070a20fd7916e3d3beac383c1bca48f40dd000077ee70707371007e00f20000c3547070707071007e04d1700000c3540000000000000000007070707070707071007e011f7371007e0080000000307571007e0083000000027371007e008502740009696d6167655f6469727371007e0085017400142b202276696d732d6c6f676f2d747a2e706e672270707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04da71007e04da71007e04d1707371007e00f70000c3547070707071007e04da71007e04da707371007e00f10000c3547070707071007e04da71007e04da707371007e00fa0000c3547070707071007e04da71007e04da707371007e00fc0000c3547070707071007e04da71007e04da707071007e04cf70707070707070707070707371007e01120000c354000000150101000000000000028100000064000000167071007e001071007e04bb7371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e00709f84b67427c46920881bfb2f2c68420a0000c35470707070707071007e02027071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e04e371007e04e371007e04e0707371007e00f70000c3547070707071007e04e371007e04e3707371007e00f10000c3547070707071007e04e371007e04e3707371007e00fa0000c3547070707071007e04e371007e04e3707371007e00fc0000c3547070707071007e04e371007e04e370707400046e6f6e65707371007e00fe707371007e016000000000707071007e04e07070707070707070707070707070707071007e02690000c3540000000000000001707071007e011f7371007e0080000000317571007e0083000000017371007e00850274000d4f50455241544f525f4e414d45707070707070707070707070707371007e01120000c354000000140101000000000000028100000064000000027071007e001071007e04bb7371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070b74a450bb0260069150ee578dd774a5e0000c35470707070707071007e02027071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e04f371007e04f371007e04f0707371007e00f70000c3547070707071007e04f371007e04f3707371007e00f10000c3547070707071007e04f371007e04f3707371007e00fa0000c3547070707071007e04f371007e04f3707371007e00fc0000c3547070707071007e04f371007e04f3707070707371007e00fe7070707071007e04f07070707070707070707070707070707071007e01770000c3540000000000000001707071007e011f7371007e0080000000327571007e0083000000057371007e0085017400046d7367287371007e0085057400246c6162656c2e7469746c652e756e697465642e72657075626c69632e74616e7a616e69617371007e0085017400022c207371007e00850374000b70726f6772616d4e616d657371007e00850174000129707070707070707070707070707371007e01120000c3540000001501010000000000000281000000640000002b7071007e001071007e04bb7371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070ac98448b9ffc09a0b7f47698146244d80000c35470707070707071007e02027071007e020371007e023970707070707070707371007e00ec707371007e00f00000c3547070707071007e050971007e050971007e0506707371007e00f70000c3547070707071007e050971007e0509707371007e00f10000c3547070707071007e050971007e0509707371007e00fa0000c3547070707071007e050971007e0509707371007e00fc0000c3547070707071007e050971007e0509707070707371007e00fe7070707071007e05067070707070707070707070707070707071007e01770000c3540000000000000001707071007e011f7371007e0080000000337571007e0083000000057371007e0085017400046d7367287371007e0085057400186c6162656c2e7469746c652e696d6d756e697a6174696f6e7371007e0085017400012c7371007e00850374000b70726f6772616d4e616d657371007e008501740001297070707070707070707070707078700000c35400000040017371007e00800000002e7571007e0083000000027371007e00850474000b504147455f4e554d4245527371007e0085017400382e696e7456616c75652829203d3d20313f206e657720426f6f6c65616e287472756529203a206e657720426f6f6c65616e2866616c73652970707070707e72002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5072696e744f72646572456e756d00000000000000001200007871007e0012740008564552544943414c757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525374796c653bd49cc311d905723502000078700000000c7371007e02240000c35400707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e052871007e052871007e0527707371007e00f70000c3547070707071007e052871007e0528707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f80000071007e052871007e0528707371007e00fa0000c3547070707071007e052871007e0528707371007e00fc0000c3547070707071007e052871007e05287371007e00f20000c3547070707071007e052770707070707400057461626c65707371007e00fe7070707071007e0527707070707070707070707070707070707070707070707070707071007e02277371007e02240000c354007371007e00d800000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e053571007e053571007e0533707371007e00f70000c3547070707071007e053571007e0535707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e053571007e0535707371007e00fa0000c3547070707071007e053571007e0535707371007e00fc0000c3547070707071007e053571007e05357371007e00f20000c3547070707071007e05337070707071007e00dd7400087461626c655f4348707371007e00fe7070707071007e053370707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e054271007e054271007e0540707371007e00f70000c3547070707071007e054271007e0542707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e054271007e0542707371007e00fa0000c3547070707071007e054271007e0542707371007e00fc0000c3547070707071007e054271007e05427371007e00f20000c3547070707071007e05407070707071007e00dd7400087461626c655f5444707371007e00fe7070707071007e054070707070707070707070707070707070707070707070707070707371007e02240000c35400707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e054e71007e054e71007e054d707371007e00f70000c3547070707071007e054e71007e054e707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f80000071007e054e71007e054e707371007e00fa0000c3547070707071007e054e71007e054e707371007e00fc0000c3547070707071007e054e71007e054e7371007e00f20000c3547070707071007e054d70707070707400077461626c652031707371007e00fe7070707071007e054d70707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e055b71007e055b71007e0559707371007e00f70000c3547070707071007e055b71007e055b707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e055b71007e055b707371007e00fa0000c3547070707071007e055b71007e055b707371007e00fc0000c3547070707071007e055b71007e055b7371007e00f20000c3547070707071007e05597070707071007e00dd74000a7461626c6520315f5448707371007e00fe7070707071007e055970707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e056871007e056871007e0566707371007e00f70000c3547070707071007e056871007e0568707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e056871007e0568707371007e00fa0000c3547070707071007e056871007e0568707371007e00fc0000c3547070707071007e056871007e05687371007e00f20000c3547070707071007e05667070707071007e00dd74000a7461626c6520315f4348707371007e00fe7070707071007e056670707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e057571007e057571007e0573707371007e00f70000c3547070707071007e057571007e0575707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e057571007e0575707371007e00fa0000c3547070707071007e057571007e0575707371007e00fc0000c3547070707071007e057571007e05757371007e00f20000c3547070707071007e05737070707071007e00dd74000a7461626c6520315f5444707371007e00fe7070707071007e057370707070707070707070707070707070707070707070707070707371007e02240000c35400707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e058171007e058171007e0580707371007e00f70000c3547070707071007e058171007e0581707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f80000071007e058171007e0581707371007e00fa0000c3547070707071007e058171007e0581707371007e00fc0000c3547070707071007e058171007e05817371007e00f20000c3547070707071007e058070707070707400077461626c652032707371007e00fe7070707071007e058070707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e058e71007e058e71007e058c707371007e00f70000c3547070707071007e058e71007e058e707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e058e71007e058e707371007e00fa0000c3547070707071007e058e71007e058e707371007e00fc0000c3547070707071007e058e71007e058e7371007e00f20000c3547070707071007e058c7070707071007e00dd74000a7461626c6520325f5448707371007e00fe7070707071007e058c70707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e059b71007e059b71007e0599707371007e00f70000c3547070707071007e059b71007e059b707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e059b71007e059b707371007e00fa0000c3547070707071007e059b71007e059b707371007e00fc0000c3547070707071007e059b71007e059b7371007e00f20000c3547070707071007e05997070707071007e00dd74000a7461626c6520325f4348707371007e00fe7070707071007e059970707070707070707070707070707070707070707070707070707371007e02240000c354007371007e00d800000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e05a871007e05a871007e05a6707371007e00f70000c3547070707071007e05a871007e05a8707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e05a871007e05a8707371007e00fa0000c3547070707071007e05a871007e05a8707371007e00fc0000c3547070707071007e05a871007e05a87371007e00f20000c3547070707071007e05a67070707071007e00dd74000a7461626c6520325f5444707371007e00fe7070707071007e05a6707070707070707070707070707070707070707070707070707070707371007e00be7371007e00c40000000077040000000078700000c3540000003201707070707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5768656e4e6f4461746154797065456e756d00000000000000001200007871007e001274000f4e4f5f444154415f53454354494f4e737200366e65742e73662e6a61737065727265706f7274732e656e67696e652e64657369676e2e4a525265706f7274436f6d70696c654461746100000000000027d80200034c001363726f7373746162436f6d70696c654461746171007e002c4c001264617461736574436f6d70696c654461746171007e002c4c00166d61696e44617461736574436f6d70696c654461746171007e000178707371007e03ad3f4000000000000077080000001000000000787371007e03ad3f4000000000000c7708000000100000000171007e0024757200025b42acf317f8060854e0020000787000000f84cafebabe0000002e009e01002e7265706f7274315f5461626c653332446174617365743332315f313434343239343830383337395f32313037323507000101002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a524576616c7561746f7207000301001b706172616d657465725f5245504f52545f434f4e4e454354494f4e0100324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465723b010010706172616d657465725f46494c544552010017706172616d657465725f4a41535045525f5245504f5254010017706172616d657465725f5245504f52545f4c4f43414c4501001a706172616d657465725f5245504f52545f54494d455f5a4f4e4501001a706172616d657465725f5245504f52545f54454d504c4154455301001a706172616d657465725f5245504f52545f4d41585f434f554e5401001a706172616d657465725f5245504f52545f5343524950544c455401001e706172616d657465725f5245504f52545f46494c455f5245534f4c56455201001f706172616d657465725f5245504f52545f464f524d41545f464143544f525901001f706172616d657465725f5245504f52545f504152414d45544552535f4d4150010020706172616d657465725f5245504f52545f5245534f555243455f42554e444c4501001c706172616d657465725f5245504f52545f444154415f534f55524345010018706172616d657465725f5245504f52545f434f4e5445585401001d706172616d657465725f5245504f52545f434c4153535f4c4f41444552010024706172616d657465725f5245504f52545f55524c5f48414e444c45525f464143544f5259010015706172616d657465725f534f52545f4649454c44530100147661726961626c655f504147455f4e554d4245520100314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c653b0100167661726961626c655f434f4c554d4e5f4e554d4245520100157661726961626c655f5245504f52545f434f554e540100137661726961626c655f504147455f434f554e540100157661726961626c655f434f4c554d4e5f434f554e540100063c696e69743e010003282956010004436f64650c001d001e0a000400200c0005000609000200220c0007000609000200240c0008000609000200260c0009000609000200280c000a0006090002002a0c000b0006090002002c0c000c0006090002002e0c000d000609000200300c000e000609000200320c000f000609000200340c0010000609000200360c0011000609000200380c00120006090002003a0c00130006090002003c0c00140006090002003e0c0015000609000200400c0016000609000200420c0017001809000200440c0019001809000200460c001a001809000200480c001b0018090002004a0c001c0018090002004c01000f4c696e654e756d6265725461626c6501000e637573746f6d697a6564496e6974010030284c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b295601000a696e6974506172616d73010012284c6a6176612f7574696c2f4d61703b29560c005100520a0002005301000a696e69744669656c64730c005500520a00020056010008696e6974566172730c005800520a000200590100115245504f52545f434f4e4e454354494f4e08005b01000d6a6176612f7574696c2f4d617007005d010003676574010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f4f626a6563743b0c005f00600b005e00610100306e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d6574657207006301000646494c54455208006501000d4a41535045525f5245504f525408006701000d5245504f52545f4c4f43414c450800690100105245504f52545f54494d455f5a4f4e4508006b0100105245504f52545f54454d504c4154455308006d0100105245504f52545f4d41585f434f554e5408006f0100105245504f52545f5343524950544c45540800710100145245504f52545f46494c455f5245534f4c5645520800730100155245504f52545f464f524d41545f464143544f52590800750100155245504f52545f504152414d45544552535f4d41500800770100165245504f52545f5245534f555243455f42554e444c450800790100125245504f52545f444154415f534f5552434508007b01000e5245504f52545f434f4e5445585408007d0100135245504f52545f434c4153535f4c4f4144455208007f01001a5245504f52545f55524c5f48414e444c45525f464143544f525908008101000b534f52545f4649454c445308008301000b504147455f4e554d42455208008501002f6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c6507008701000d434f4c554d4e5f4e554d42455208008901000c5245504f52545f434f554e5408008b01000a504147455f434f554e5408008d01000c434f4c554d4e5f434f554e5408008f0100086576616c756174650100152849294c6a6176612f6c616e672f4f626a6563743b01000a457863657074696f6e730100136a6176612f6c616e672f5468726f7761626c650700940100116a6176612f6c616e672f496e7465676572070096010004284929560c001d00980a0097009901000b6576616c756174654f6c640100116576616c75617465457374696d6174656401000a536f7572636546696c650021000200040000001600020005000600000002000700060000000200080006000000020009000600000002000a000600000002000b000600000002000c000600000002000d000600000002000e000600000002000f000600000002001000060000000200110006000000020012000600000002001300060000000200140006000000020015000600000002001600060000000200170018000000020019001800000002001a001800000002001b001800000002001c0018000000080001001d001e0001001f000000e700020001000000732ab700212a01b500232a01b500252a01b500272a01b500292a01b5002b2a01b5002d2a01b5002f2a01b500312a01b500332a01b500352a01b500372a01b500392a01b5003b2a01b5003d2a01b5003f2a01b500412a01b500432a01b500452a01b500472a01b500492a01b5004b2a01b5004db100000001004e00000062001800000012000400190009001a000e001b0013001c0018001d001d001e0022001f00270020002c00210031002200360023003b00240040002500450026004a0027004f0028005400290059002a005e002b0063002c0068002d006d002e007200120001004f00500001001f0000003400020004000000102a2bb700542a2cb700572a2db7005ab100000001004e0000001200040000003a0005003b000a003c000f003d0002005100520001001f0000018f00030002000001332a2b125cb900620200c00064c00064b500232a2b1266b900620200c00064c00064b500252a2b1268b900620200c00064c00064b500272a2b126ab900620200c00064c00064b500292a2b126cb900620200c00064c00064b5002b2a2b126eb900620200c00064c00064b5002d2a2b1270b900620200c00064c00064b5002f2a2b1272b900620200c00064c00064b500312a2b1274b900620200c00064c00064b500332a2b1276b900620200c00064c00064b500352a2b1278b900620200c00064c00064b500372a2b127ab900620200c00064c00064b500392a2b127cb900620200c00064c00064b5003b2a2b127eb900620200c00064c00064b5003d2a2b1280b900620200c00064c00064b5003f2a2b1282b900620200c00064c00064b500412a2b1284b900620200c00064c00064b50043b100000001004e0000004a00120000004500120046002400470036004800480049005a004a006c004b007e004c0090004d00a2004e00b4004f00c6005000d8005100ea005200fc0053010e005401200055013200560002005500520001001f000000190000000200000001b100000001004e0000000600010000005e0002005800520001001f00000087000300020000005b2a2b1286b900620200c00088c00088b500452a2b128ab900620200c00088c00088b500472a2b128cb900620200c00088c00088b500492a2b128eb900620200c00088c00088b5004b2a2b1290b900620200c00088c00088b5004db100000001004e0000001a0006000000660012006700240068003600690048006a005a006b000100910092000200930000000400010095001f000000eb000300030000008f014d1baa0000008a00000000000000070000002d0000003900000045000000510000005d000000690000007500000081bb00975904b7009a4da70054bb00975904b7009a4da70048bb00975904b7009a4da7003cbb00975903b7009a4da70030bb00975904b7009a4da70024bb00975903b7009a4da70018bb00975904b7009a4da7000cbb00975903b7009a4d2cb000000001004e0000004a00120000007300020075003000790039007a003c007e0045007f004800830051008400540088005d00890060008d0069008e006c00920075009300780097008100980084009c008d00a40001009b0092000200930000000400010095001f000000eb000300030000008f014d1baa0000008a00000000000000070000002d0000003900000045000000510000005d000000690000007500000081bb00975904b7009a4da70054bb00975904b7009a4da70048bb00975904b7009a4da7003cbb00975903b7009a4da70030bb00975904b7009a4da70024bb00975903b7009a4da70018bb00975904b7009a4da7000cbb00975903b7009a4d2cb000000001004e0000004a0012000000ad000200af003000b3003900b4003c00b8004500b9004800bd005100be005400c2005d00c3006000c7006900c8006c00cc007500cd007800d1008100d2008400d6008d00de0001009c0092000200930000000400010095001f000000eb000300030000008f014d1baa0000008a00000000000000070000002d0000003900000045000000510000005d000000690000007500000081bb00975904b7009a4da70054bb00975904b7009a4da70048bb00975904b7009a4da7003cbb00975903b7009a4da70030bb00975904b7009a4da70024bb00975903b7009a4da70018bb00975904b7009a4da7000cbb00975903b7009a4d2cb000000001004e0000004a0012000000e7000200e9003000ed003900ee003c00f2004500f3004800f7005100f8005400fc005d00fd0060010100690102006c0106007501070078010b0081010c00840110008d01180001009d000000020001787571007e05bc00002f61cafebabe0000002e018101001c7265706f7274315f313434343239343830383337395f32313037323507000101002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a524576616c7561746f7207000301001e706172616d657465725f49535f49474e4f52455f504147494e4154494f4e0100324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465723b01001b706172616d657465725f5245504f52545f434f4e4e454354494f4e010010706172616d657465725f46494c544552010013706172616d657465725f696d6167655f646972010017706172616d657465725f4a41535045525f5245504f5254010017706172616d657465725f5245504f52545f4c4f43414c4501001a706172616d657465725f5245504f52545f54494d455f5a4f4e4501001a706172616d657465725f5245504f52545f54454d504c4154455301001a706172616d657465725f5245504f52545f4d41585f434f554e5401001a706172616d657465725f5245504f52545f5343524950544c455401001e706172616d657465725f5245504f52545f46494c455f5245534f4c564552010012706172616d657465725f4f524445525f494401001f706172616d657465725f5245504f52545f464f524d41545f464143544f525901001f706172616d657465725f5245504f52545f504152414d45544552535f4d4150010020706172616d657465725f5245504f52545f5245534f555243455f42554e444c4501001c706172616d657465725f5245504f52545f444154415f534f55524345010018706172616d657465725f5245504f52545f434f4e5445585401001d706172616d657465725f5245504f52545f434c4153535f4c4f41444552010024706172616d657465725f5245504f52545f55524c5f48414e444c45525f464143544f525901001c706172616d657465725f5245504f52545f5649525455414c495a4552010015706172616d657465725f534f52545f4649454c4453010017706172616d657465725f4f50455241544f525f4e414d4501000e6669656c645f6c6173744e616d6501002e4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c4669656c643b0100126669656c645f72654f726465724c6576656c01000f6669656c645f6f726465726461746501000f6669656c645f7374617274646174650100116669656c645f70726f647563744e616d650100156669656c645f70726f6475637443617465676f727901000f6669656c645f66697273744e616d6501000d6669656c645f656e64646174650100176669656c645f7175616e746974795265717565737465640100116669656c645f70726f6772616d4e616d650100116669656c645f73746f636b4f6e48616e640100126669656c645f666163696c6974794e616d650100126669656c645f6d6178696d756d53746f636b0100147661726961626c655f504147455f4e554d4245520100314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c653b0100167661726961626c655f434f4c554d4e5f4e554d4245520100157661726961626c655f5245504f52545f434f554e540100137661726961626c655f504147455f434f554e540100157661726961626c655f434f4c554d4e5f434f554e540100157661726961626c655f4865616465725f434f554e540100297661726961626c655f47726f757033326279333250726f6475637443617465676f72795f434f554e540100137661726961626c655f7646697273744e616d650100127661726961626c655f764c6173744e616d650100137661726961626c655f765374617274446174650100117661726961626c655f76456e64446174650100063c696e69743e010003282956010004436f64650c003600370a000400390c00050006090002003b0c00070006090002003d0c00080006090002003f0c0009000609000200410c000a000609000200430c000b000609000200450c000c000609000200470c000d000609000200490c000e0006090002004b0c000f0006090002004d0c00100006090002004f0c0011000609000200510c0012000609000200530c0013000609000200550c0014000609000200570c0015000609000200590c00160006090002005b0c00170006090002005d0c00180006090002005f0c0019000609000200610c001a000609000200630c001b000609000200650c001c001d09000200670c001e001d09000200690c001f001d090002006b0c0020001d090002006d0c0021001d090002006f0c0022001d09000200710c0023001d09000200730c0024001d09000200750c0025001d09000200770c0026001d09000200790c0027001d090002007b0c0028001d090002007d0c0029001d090002007f0c002a002b09000200810c002c002b09000200830c002d002b09000200850c002e002b09000200870c002f002b09000200890c0030002b090002008b0c0031002b090002008d0c0032002b090002008f0c0033002b09000200910c0034002b09000200930c0035002b090002009501000f4c696e654e756d6265725461626c6501000e637573746f6d697a6564496e6974010030284c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b295601000a696e6974506172616d73010012284c6a6176612f7574696c2f4d61703b29560c009a009b0a0002009c01000a696e69744669656c64730c009e009b0a0002009f010008696e6974566172730c00a1009b0a000200a201001449535f49474e4f52455f504147494e4154494f4e0800a401000d6a6176612f7574696c2f4d61700700a6010003676574010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f4f626a6563743b0c00a800a90b00a700aa0100306e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465720700ac0100115245504f52545f434f4e4e454354494f4e0800ae01000646494c5445520800b0010009696d6167655f6469720800b201000d4a41535045525f5245504f52540800b401000d5245504f52545f4c4f43414c450800b60100105245504f52545f54494d455f5a4f4e450800b80100105245504f52545f54454d504c415445530800ba0100105245504f52545f4d41585f434f554e540800bc0100105245504f52545f5343524950544c45540800be0100145245504f52545f46494c455f5245534f4c5645520800c00100084f524445525f49440800c20100155245504f52545f464f524d41545f464143544f52590800c40100155245504f52545f504152414d45544552535f4d41500800c60100165245504f52545f5245534f555243455f42554e444c450800c80100125245504f52545f444154415f534f555243450800ca01000e5245504f52545f434f4e544558540800cc0100135245504f52545f434c4153535f4c4f414445520800ce01001a5245504f52545f55524c5f48414e444c45525f464143544f52590800d00100125245504f52545f5649525455414c495a45520800d201000b534f52545f4649454c44530800d401000d4f50455241544f525f4e414d450800d60100086c6173744e616d650800d801002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c4669656c640700da01000c72654f726465724c6576656c0800dc0100096f72646572646174650800de0100097374617274646174650800e001000b70726f647563744e616d650800e201000f70726f6475637443617465676f72790800e401000966697273744e616d650800e6010007656e64646174650800e80100117175616e746974795265717565737465640800ea01000b70726f6772616d4e616d650800ec01000b73746f636b4f6e48616e640800ee01000c666163696c6974794e616d650800f001000c6d6178696d756d53746f636b0800f201000b504147455f4e554d4245520800f401002f6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c650700f601000d434f4c554d4e5f4e554d4245520800f801000c5245504f52545f434f554e540800fa01000a504147455f434f554e540800fc01000c434f4c554d4e5f434f554e540800fe01000c4865616465725f434f554e5408010001001e47726f75702062792050726f6475637443617465676f72795f434f554e5408010201000a7646697273744e616d65080104010009764c6173744e616d6508010601000a7653746172744461746508010801000876456e644461746508010a0100086576616c756174650100152849294c6a6176612f6c616e672f4f626a6563743b01000a457863657074696f6e730100136a6176612f6c616e672f5468726f7761626c6507010f0100116a6176612f6c616e672f496e7465676572070111010004284929560c003601130a0112011401000867657456616c756501001428294c6a6176612f6c616e672f4f626a6563743b0c011601170a00db01180100106a6176612f6c616e672f537472696e6707011a0a00f70118010008696e7456616c75650100032829490c011d011e0a0112011f0100116a6176612f6c616e672f426f6f6c65616e070121010004285a29560c003601230a0122012401002c6c6162656c2e7469746c652e76616363696e652e6f726465722e7265717569736974696f6e2e7265706f7274080126010003737472010026284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e673b0c012801290a0002012a0100036d7367010038284c6a6176612f6c616e672f537472696e673b4c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e673b0c012c012d0a0002012e01000a6c6162656c2e6e616d6508013001000a6c6162656c2e66726f6d08013201000a6c6162656c2e646174650801340100166a6176612f6c616e672f537472696e6742756666657207013601000776616c75654f66010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e673b0c013801390a011b013a010015284c6a6176612f6c616e672f537472696e673b29560c0036013c0a0137013d0100012008013f010006617070656e6401002c284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e674275666665723b0c014101420a01370143010008746f537472696e6701001428294c6a6176612f6c616e672f537472696e673b0c014501460a0137014701000e6a6176612f7574696c2f446174650701490a014a003901001e6c6162656c2e666163696c6974792e7265706f7274696e67506572696f6408014c010003202d2008014e01002c284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e674275666665723b0c014101500a0137015101000d6c6162656c2e706167652e6f6608015301000d6c6162656c2e6c696e652e6e6f08015501000d6c6162656c2e70726f647563740801570100136c6162656c2e6d6178696d756d2e73746f636b0801590100136c6162656c2e72656f726465722e6c6576656c08015b01001a6c696e6b2e76616363696e652e73746f636b2e6f6e2e68616e6408015d0100186c6162656c2e7175616e746974792e72657175657374656408015f0100126c6162656c2e7265717565737465642e627908016101000f6c6162656c2e7369676e61747572650801630100146c6162656c2e6f6666696369616c2e7374616d7008016501001b6c6162656c2e666f722e6f6666696369616c2e7573652e6f6e6c790801670100136c6162656c2e617574686f72697a65642e62790801690a00ad011801000b6c6f676f2d747a2e706e6708016c01001076696d732d6c6f676f2d747a2e706e6708016e0100246c6162656c2e7469746c652e756e697465642e72657075626c69632e74616e7a616e69610801700100186c6162656c2e7469746c652e696d6d756e697a6174696f6e080172010016285a294c6a6176612f6c616e672f426f6f6c65616e3b0c013801740a0122017501000b6576616c756174654f6c6401000b6765744f6c6456616c75650c017801170a00db01790a00f701790100116576616c75617465457374696d61746564010011676574457374696d6174656456616c75650c017d01170a00f7017e01000a536f7572636546696c650021000200040000002e00020005000600000002000700060000000200080006000000020009000600000002000a000600000002000b000600000002000c000600000002000d000600000002000e000600000002000f0006000000020010000600000002001100060000000200120006000000020013000600000002001400060000000200150006000000020016000600000002001700060000000200180006000000020019000600000002001a000600000002001b000600000002001c001d00000002001e001d00000002001f001d000000020020001d000000020021001d000000020022001d000000020023001d000000020024001d000000020025001d000000020026001d000000020027001d000000020028001d000000020029001d00000002002a002b00000002002c002b00000002002d002b00000002002e002b00000002002f002b000000020030002b000000020031002b000000020032002b000000020033002b000000020034002b000000020035002b0000000800010036003700010038000001bf00020001000000eb2ab7003a2a01b5003c2a01b5003e2a01b500402a01b500422a01b500442a01b500462a01b500482a01b5004a2a01b5004c2a01b5004e2a01b500502a01b500522a01b500542a01b500562a01b500582a01b5005a2a01b5005c2a01b5005e2a01b500602a01b500622a01b500642a01b500662a01b500682a01b5006a2a01b5006c2a01b5006e2a01b500702a01b500722a01b500742a01b500762a01b500782a01b5007a2a01b5007c2a01b5007e2a01b500802a01b500822a01b500842a01b500862a01b500882a01b5008a2a01b5008c2a01b5008e2a01b500902a01b500922a01b500942a01b50096b1000000010097000000c2003000000012000400190009001a000e001b0013001c0018001d001d001e0022001f00270020002c00210031002200360023003b00240040002500450026004a0027004f0028005400290059002a005e002b0063002c0068002d006d002e0072002f00770030007c00310081003200860033008b00340090003500950036009a0037009f003800a4003900a9003a00ae003b00b3003c00b8003d00bd003e00c2003f00c7004000cc004100d1004200d6004300db004400e0004500e5004600ea0012000100980099000100380000003400020004000000102a2bb7009d2a2cb700a02a2db700a3b10000000100970000001200040000005200050053000a0054000f00550002009a009b00010038000001fd000300020000018d2a2b12a5b900ab0200c000adc000adb5003c2a2b12afb900ab0200c000adc000adb5003e2a2b12b1b900ab0200c000adc000adb500402a2b12b3b900ab0200c000adc000adb500422a2b12b5b900ab0200c000adc000adb500442a2b12b7b900ab0200c000adc000adb500462a2b12b9b900ab0200c000adc000adb500482a2b12bbb900ab0200c000adc000adb5004a2a2b12bdb900ab0200c000adc000adb5004c2a2b12bfb900ab0200c000adc000adb5004e2a2b12c1b900ab0200c000adc000adb500502a2b12c3b900ab0200c000adc000adb500522a2b12c5b900ab0200c000adc000adb500542a2b12c7b900ab0200c000adc000adb500562a2b12c9b900ab0200c000adc000adb500582a2b12cbb900ab0200c000adc000adb5005a2a2b12cdb900ab0200c000adc000adb5005c2a2b12cfb900ab0200c000adc000adb5005e2a2b12d1b900ab0200c000adc000adb500602a2b12d3b900ab0200c000adc000adb500622a2b12d5b900ab0200c000adc000adb500642a2b12d7b900ab0200c000adc000adb50066b10000000100970000005e00170000005d0012005e0024005f0036006000480061005a0062006c0063007e00640090006500a2006600b4006700c6006800d8006900ea006a00fc006b010e006c0120006d0132006e0144006f0156007001680071017a0072018c00730002009e009b000100380000013700030002000000eb2a2b12d9b900ab0200c000dbc000dbb500682a2b12ddb900ab0200c000dbc000dbb5006a2a2b12dfb900ab0200c000dbc000dbb5006c2a2b12e1b900ab0200c000dbc000dbb5006e2a2b12e3b900ab0200c000dbc000dbb500702a2b12e5b900ab0200c000dbc000dbb500722a2b12e7b900ab0200c000dbc000dbb500742a2b12e9b900ab0200c000dbc000dbb500762a2b12ebb900ab0200c000dbc000dbb500782a2b12edb900ab0200c000dbc000dbb5007a2a2b12efb900ab0200c000dbc000dbb5007c2a2b12f1b900ab0200c000dbc000dbb5007e2a2b12f3b900ab0200c000dbc000dbb50080b10000000100970000003a000e0000007b0012007c0024007d0036007e0048007f005a0080006c0081007e00820090008300a2008400b4008500c6008600d8008700ea0088000200a1009b000100380000011100030002000000cd2a2b12f5b900ab0200c000f7c000f7b500822a2b12f9b900ab0200c000f7c000f7b500842a2b12fbb900ab0200c000f7c000f7b500862a2b12fdb900ab0200c000f7c000f7b500882a2b12ffb900ab0200c000f7c000f7b5008a2a2b130101b900ab0200c000f7c000f7b5008c2a2b130103b900ab0200c000f7c000f7b5008e2a2b130105b900ab0200c000f7c000f7b500902a2b130107b900ab0200c000f7c000f7b500922a2b130109b900ab0200c000f7c000f7b500942a2b13010bb900ab0200c000f7c000f7b50096b100000001009700000032000c0000009000120091002400920036009300480094005a0095006d0096008000970093009800a6009900b9009a00cc009b0001010c010d0002010e00000004000101100038000007000003000300000504014d1baa000004ff000000000000003b000000fd000001020000010e0000011a00000126000001320000013e0000014a00000156000001620000016e0000017a0000018600000192000001a0000001ae000001bc000001ca000001f20000020b000002330000023e000002490000025400000282000002900000029b000002a6000002d4000002f20000030b00000319000003240000032f0000033a00000345000003500000035b00000366000003940000039f000003aa000003b5000003c0000003cb000003d6000003e1000004090000042a0000044b00000459000004720000048b000004a9000004ae000004bc000004ca000004d8000004e6000004f4014da70400bb01125904b701154da703f4bb01125904b701154da703e8bb01125904b701154da703dcbb01125903b701154da703d0bb01125904b701154da703c4bb01125903b701154da703b8bb01125904b701154da703acbb01125903b701154da703a0bb01125904b701154da70394bb01125903b701154da70388bb01125904b701154da7037cbb01125903b701154da703702ab40074b60119c0011b4da703622ab40068b60119c0011b4da703542ab4006eb60119c0011b4da703462ab40076b60119c0011b4da703382ab40082b6011cc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da703102a2a130127b6012b2ab4007ab60119c0011bb6012f4da702f72ab40082b6011cc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da702cf2a130131b6012b4da702c42a130133b6012b4da702b92a130135b6012b4da702aebb0137592ab40074b60119c0011bb8013bb7013e130140b601442ab40068b60119c0011bb60144b601484da702802ab4007eb60119c0011b4da70272bb014a59b7014b4da702672a13014db6012b4da7025cbb0137592ab4006eb60119c0011bb8013bb7013e13014fb601442ab40076b60119c0011bb60144b601484da7022ebb013759130140b7013e2ab40082b6011cc00112b60152b601484da702102a2a130154b6012b2ab40082b6011cc00112b6012f4da701f72ab40072b60119c0011b4da701e92a130156b6012b4da701de2a130158b6012b4da701d32a13015ab6012b4da701c82a13015cb6012b4da701bd2a13015eb6012b4da701b22a130160b6012b4da701a72a130162b6012b4da7019cbb0137592ab40074b60119c0011bb8013bb7013e130140b601442ab40068b60119c0011bb60144b601484da7016e2a130164b6012b4da701632a130135b6012b4da701582a130166b6012b4da7014d2a130168b6012b4da701422a13016ab6012b4da701372a130164b6012b4da7012c2a130135b6012b4da701212ab40082b6011cc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da700f9bb0137592ab40042b6016bc0011bb8013bb7013e13016db60144b601484da700d8bb0137592ab40042b6016bc0011bb8013bb7013e13016fb60144b601484da700b72ab40066b6016bc0011b4da700a92a2a130171b6012b2ab4007ab60119c0011bb6012f4da700902a2a130173b6012b2ab4007ab60119c0011bb6012f4da700772ab40086b6011cc00112b6012005709a000704a7000403b801764da70059014da700542ab40070b60119c0011b4da700462ab40080b60119c0011b4da700382ab4006ab60119c0011b4da7002a2ab4007cb60119c0011b4da7001c2ab40078b60119c0011b4da7000e2ab40086b6011cc001124d2cb0000000010097000001ea007a000000a3000200a5010000a9010200aa010500ae010e00af011100b3011a00b4011d00b8012600b9012900bd013200be013500c2013e00c3014100c7014a00c8014d00cc015600cd015900d1016200d2016500d6016e00d7017100db017a00dc017d00e0018600e1018900e5019200e6019500ea01a000eb01a300ef01ae00f001b100f401bc00f501bf00f901ca00fa01cd00fe01f200ff01f50103020b0104020e0108023301090236010d023e010e0241011202490113024c0117025401180257011c0282011d028501210290012202930126029b0127029e012b02a6012c02a9013002d4013102d7013502f2013602f5013a030b013b030e013f03190140031c01440324014503270149032f014a0332014e033a014f033d01530345015403480158035001590353015d035b015e035e01620366016303690167039401680397016c039f016d03a2017103aa017203ad017603b5017703b8017b03c0017c03c3018003cb018103ce018503d6018603d9018a03e1018b03e4018f04090190040c0194042a0195042d0199044b019a044e019e0459019f045c01a3047201a4047501a8048b01a9048e01ad04a901ae04ac01b204ae01b304b101b704bc01b804bf01bc04ca01bd04cd01c104d801c204db01c604e601c704e901cb04f401cc04f701d0050201d800010177010d0002010e00000004000101100038000007000003000300000504014d1baa000004ff000000000000003b000000fd000001020000010e0000011a00000126000001320000013e0000014a00000156000001620000016e0000017a0000018600000192000001a0000001ae000001bc000001ca000001f20000020b000002330000023e000002490000025400000282000002900000029b000002a6000002d4000002f20000030b00000319000003240000032f0000033a00000345000003500000035b00000366000003940000039f000003aa000003b5000003c0000003cb000003d6000003e1000004090000042a0000044b00000459000004720000048b000004a9000004ae000004bc000004ca000004d8000004e6000004f4014da70400bb01125904b701154da703f4bb01125904b701154da703e8bb01125904b701154da703dcbb01125903b701154da703d0bb01125904b701154da703c4bb01125903b701154da703b8bb01125904b701154da703acbb01125903b701154da703a0bb01125904b701154da70394bb01125903b701154da70388bb01125904b701154da7037cbb01125903b701154da703702ab40074b6017ac0011b4da703622ab40068b6017ac0011b4da703542ab4006eb6017ac0011b4da703462ab40076b6017ac0011b4da703382ab40082b6017bc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da703102a2a130127b6012b2ab4007ab6017ac0011bb6012f4da702f72ab40082b6017bc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da702cf2a130131b6012b4da702c42a130133b6012b4da702b92a130135b6012b4da702aebb0137592ab40074b6017ac0011bb8013bb7013e130140b601442ab40068b6017ac0011bb60144b601484da702802ab4007eb6017ac0011b4da70272bb014a59b7014b4da702672a13014db6012b4da7025cbb0137592ab4006eb6017ac0011bb8013bb7013e13014fb601442ab40076b6017ac0011bb60144b601484da7022ebb013759130140b7013e2ab40082b6017bc00112b60152b601484da702102a2a130154b6012b2ab40082b6017bc00112b6012f4da701f72ab40072b6017ac0011b4da701e92a130156b6012b4da701de2a130158b6012b4da701d32a13015ab6012b4da701c82a13015cb6012b4da701bd2a13015eb6012b4da701b22a130160b6012b4da701a72a130162b6012b4da7019cbb0137592ab40074b6017ac0011bb8013bb7013e130140b601442ab40068b6017ac0011bb60144b601484da7016e2a130164b6012b4da701632a130135b6012b4da701582a130166b6012b4da7014d2a130168b6012b4da701422a13016ab6012b4da701372a130164b6012b4da7012c2a130135b6012b4da701212ab40082b6017bc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da700f9bb0137592ab40042b6016bc0011bb8013bb7013e13016db60144b601484da700d8bb0137592ab40042b6016bc0011bb8013bb7013e13016fb60144b601484da700b72ab40066b6016bc0011b4da700a92a2a130171b6012b2ab4007ab6017ac0011bb6012f4da700902a2a130173b6012b2ab4007ab6017ac0011bb6012f4da700772ab40086b6017bc00112b6012005709a000704a7000403b801764da70059014da700542ab40070b6017ac0011b4da700462ab40080b6017ac0011b4da700382ab4006ab6017ac0011b4da7002a2ab4007cb6017ac0011b4da7001c2ab40078b6017ac0011b4da7000e2ab40086b6017bc001124d2cb0000000010097000001ea007a000001e1000201e3010001e7010201e8010501ec010e01ed011101f1011a01f2011d01f6012601f7012901fb013201fc01350200013e020101410205014a0206014d020a0156020b0159020f0162021001650214016e021501710219017a021a017d021e0186021f01890223019202240195022801a0022901a3022d01ae022e01b1023201bc023301bf023701ca023801cd023c01f2023d01f50241020b0242020e0246023302470236024b023e024c0241025002490251024c0255025402560257025a0282025b0285025f0290026002930264029b0265029e026902a6026a02a9026e02d4026f02d7027302f2027402f50278030b0279030e027d0319027e031c02820324028303270287032f02880332028c033a028d033d02910345029203480296035002970353029b035b029c035e02a0036602a1036902a5039402a6039702aa039f02ab03a202af03aa02b003ad02b403b502b503b802b903c002ba03c302be03cb02bf03ce02c303d602c403d902c803e102c903e402cd040902ce040c02d2042a02d3042d02d7044b02d8044e02dc045902dd045c02e1047202e2047502e6048b02e7048e02eb04a902ec04ac02f004ae02f104b102f504bc02f604bf02fa04ca02fb04cd02ff04d8030004db030404e6030504e9030904f4030a04f7030e050203160001017c010d0002010e00000004000101100038000007000003000300000504014d1baa000004ff000000000000003b000000fd000001020000010e0000011a00000126000001320000013e0000014a00000156000001620000016e0000017a0000018600000192000001a0000001ae000001bc000001ca000001f20000020b000002330000023e000002490000025400000282000002900000029b000002a6000002d4000002f20000030b00000319000003240000032f0000033a00000345000003500000035b00000366000003940000039f000003aa000003b5000003c0000003cb000003d6000003e1000004090000042a0000044b00000459000004720000048b000004a9000004ae000004bc000004ca000004d8000004e6000004f4014da70400bb01125904b701154da703f4bb01125904b701154da703e8bb01125904b701154da703dcbb01125903b701154da703d0bb01125904b701154da703c4bb01125903b701154da703b8bb01125904b701154da703acbb01125903b701154da703a0bb01125904b701154da70394bb01125903b701154da70388bb01125904b701154da7037cbb01125903b701154da703702ab40074b60119c0011b4da703622ab40068b60119c0011b4da703542ab4006eb60119c0011b4da703462ab40076b60119c0011b4da703382ab40082b6017fc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da703102a2a130127b6012b2ab4007ab60119c0011bb6012f4da702f72ab40082b6017fc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da702cf2a130131b6012b4da702c42a130133b6012b4da702b92a130135b6012b4da702aebb0137592ab40074b60119c0011bb8013bb7013e130140b601442ab40068b60119c0011bb60144b601484da702802ab4007eb60119c0011b4da70272bb014a59b7014b4da702672a13014db6012b4da7025cbb0137592ab4006eb60119c0011bb8013bb7013e13014fb601442ab40076b60119c0011bb60144b601484da7022ebb013759130140b7013e2ab40082b6017fc00112b60152b601484da702102a2a130154b6012b2ab40082b6017fc00112b6012f4da701f72ab40072b60119c0011b4da701e92a130156b6012b4da701de2a130158b6012b4da701d32a13015ab6012b4da701c82a13015cb6012b4da701bd2a13015eb6012b4da701b22a130160b6012b4da701a72a130162b6012b4da7019cbb0137592ab40074b60119c0011bb8013bb7013e130140b601442ab40068b60119c0011bb60144b601484da7016e2a130164b6012b4da701632a130135b6012b4da701582a130166b6012b4da7014d2a130168b6012b4da701422a13016ab6012b4da701372a130164b6012b4da7012c2a130135b6012b4da701212ab40082b6017fc00112b6012004a0000ebb01225904b70125a7000bbb01225903b701254da700f9bb0137592ab40042b6016bc0011bb8013bb7013e13016db60144b601484da700d8bb0137592ab40042b6016bc0011bb8013bb7013e13016fb60144b601484da700b72ab40066b6016bc0011b4da700a92a2a130171b6012b2ab4007ab60119c0011bb6012f4da700902a2a130173b6012b2ab4007ab60119c0011bb6012f4da700772ab40086b6017fc00112b6012005709a000704a7000403b801764da70059014da700542ab40070b60119c0011b4da700462ab40080b60119c0011b4da700382ab4006ab60119c0011b4da7002a2ab4007cb60119c0011b4da7001c2ab40078b60119c0011b4da7000e2ab40086b6017fc001124d2cb0000000010097000001ea007a0000031f0002032101000325010203260105032a010e032b0111032f011a0330011d033401260335012903390132033a0135033e013e033f01410343014a0344014d0348015603490159034d0162034e01650352016e035301710357017a0358017d035c0186035d01890361019203620195036601a0036701a3036b01ae036c01b1037001bc037101bf037501ca037601cd037a01f2037b01f5037f020b0380020e03840233038502360389023e038a0241038e0249038f024c03930254039402570398028203990285039d0290039e029303a2029b03a3029e03a702a603a802a903ac02d403ad02d703b102f203b202f503b6030b03b7030e03bb031903bc031c03c0032403c1032703c5032f03c6033203ca033a03cb033d03cf034503d0034803d4035003d5035303d9035b03da035e03de036603df036903e3039403e4039703e8039f03e903a203ed03aa03ee03ad03f203b503f303b803f703c003f803c303fc03cb03fd03ce040103d6040203d9040603e1040703e4040b0409040c040c0410042a0411042d0415044b0416044e041a0459041b045c041f0472042004750424048b0425048e042904a9042a04ac042e04ae042f04b1043304bc043404bf043804ca043904cd043d04d8043e04db044204e6044304e9044704f4044804f7044c05020454000101800000000200017400155f313434343239343830383337395f3231303732357400326e65742e73662e6a61737065727265706f7274732e656e67696e652e64657369676e2e4a524a61766163436f6d70696c6572', NULL, '2016-02-03 14:37:39.164251', 'Print', NULL);
INSERT INTO templates (id, name, data, createdby, createddate, type, description) VALUES (4, 'Print Issue report', '\xaced0005737200286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a61737065725265706f727400000000000027d80200034c000b636f6d70696c65446174617400164c6a6176612f696f2f53657269616c697a61626c653b4c0011636f6d70696c654e616d655375666669787400124c6a6176612f6c616e672f537472696e673b4c000d636f6d70696c6572436c61737371007e00027872002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655265706f727400000000000027d802002a49001950534555444f5f53455249414c5f56455253494f4e5f55494449000c626f74746f6d4d617267696e49000b636f6c756d6e436f756e7449000d636f6c756d6e53706163696e6749000b636f6c756d6e57696474685a001069676e6f7265506167696e6174696f6e5a00136973466c6f6174436f6c756d6e466f6f7465725a0010697353756d6d6172794e6577506167655a0020697353756d6d6172795769746850616765486561646572416e64466f6f7465725a000e69735469746c654e65775061676549000a6c6566744d617267696e42000b6f7269656e746174696f6e49000a7061676548656967687449000970616765576964746842000a7072696e744f7264657249000b72696768744d617267696e490009746f704d617267696e42000e7768656e4e6f44617461547970654c000a6261636b67726f756e647400244c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5242616e643b4c000f636f6c756d6e446972656374696f6e7400334c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f52756e446972656374696f6e456e756d3b4c000c636f6c756d6e466f6f74657271007e00044c000c636f6c756d6e48656164657271007e00045b000864617461736574737400285b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52446174617365743b4c000c64656661756c745374796c657400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525374796c653b4c000664657461696c71007e00044c000d64657461696c53656374696f6e7400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5253656374696f6e3b4c0012666f726d6174466163746f7279436c61737371007e00024c000a696d706f72747353657474000f4c6a6176612f7574696c2f5365743b4c00086c616e677561676571007e00024c000e6c61737450616765466f6f74657271007e00044c000b6d61696e446174617365747400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52446174617365743b4c00046e616d6571007e00024c00066e6f4461746171007e00044c00106f7269656e746174696f6e56616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4f7269656e746174696f6e456e756d3b4c000a70616765466f6f74657271007e00044c000a7061676548656164657271007e00044c000f7072696e744f7264657256616c75657400314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5072696e744f72646572456e756d3b5b00067374796c65737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525374796c653b4c000773756d6d61727971007e00045b000974656d706c6174657374002f5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525265706f727454656d706c6174653b4c00057469746c6571007e00044c00137768656e4e6f446174615479706556616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5768656e4e6f4461746154797065456e756d3b78700000c3540000000000000001000000000000034a00010000000000000000000002530000034a00000000000000000000707e7200316e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e52756e446972656374696f6e456e756d00000000000000001200007872000e6a6176612e6c616e672e456e756d000000000000000012000078707400034c54527070757200285b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a52446174617365743b4c1a3698cdac9c440200007870000000017372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654461746173657400000000000027d802001149001950534555444f5f53455249414c5f56455253494f4e5f5549445a000669734d61696e4200177768656e5265736f757263654d697373696e67547970655b00066669656c64737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a524669656c643b4c001066696c74657245787072657373696f6e74002a4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5245787072657373696f6e3b5b000667726f7570737400265b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5247726f75703b4c00046e616d6571007e00025b000a706172616d657465727374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52506172616d657465723b4c000d70726f706572746965734d617074002d4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250726f706572746965734d61703b4c000571756572797400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5251756572793b4c000e7265736f7572636542756e646c6571007e00024c000e7363726970746c6574436c61737371007e00025b000a7363726970746c65747374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525363726970746c65743b5b000a736f72744669656c647374002a5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52536f72744669656c643b4c0004757569647400104c6a6176612f7574696c2f555549443b5b00097661726961626c65737400295b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525661726961626c653b4c001c7768656e5265736f757263654d697373696e675479706556616c756574003e4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5768656e5265736f757263654d697373696e6754797065456e756d3b78700000c354000070707074000f5461626c65204461746173657420317572002a5b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a52506172616d657465723b22000c8d2ac36021020000787000000011737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365506172616d6574657200000000000027d80200095a000e6973466f7250726f6d7074696e675a000f697353797374656d446566696e65644c001664656661756c7456616c756545787072657373696f6e71007e00194c000b6465736372697074696f6e71007e00024c00046e616d6571007e00024c000e6e6573746564547970654e616d6571007e00024c000d70726f706572746965734d617071007e001c4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e000278700101707074000e5245504f52545f434f4e54455854707372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5250726f706572746965734d617000000000000027d80200034c00046261736571007e001c4c000e70726f706572746965734c6973747400104c6a6176612f7574696c2f4c6973743b4c000d70726f706572746965734d617074000f4c6a6176612f7574696c2f4d61703b78707070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e5265706f7274436f6e74657874707371007e0027010170707400155245504f52545f504152414d45544552535f4d4150707371007e002a70707074000d6a6176612e7574696c2e4d6170707371007e00270101707074000d4a41535045525f5245504f5254707371007e002a7070707400286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a61737065725265706f7274707371007e0027010170707400115245504f52545f434f4e4e454354494f4e707371007e002a7070707400136a6176612e73716c2e436f6e6e656374696f6e707371007e0027010170707400105245504f52545f4d41585f434f554e54707371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e0027010170707400125245504f52545f444154415f534f55524345707371007e002a7070707400286e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5244617461536f75726365707371007e0027010170707400105245504f52545f5343524950544c4554707371007e002a70707074002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5241627374726163745363726970746c6574707371007e00270101707074000d5245504f52545f4c4f43414c45707371007e002a7070707400106a6176612e7574696c2e4c6f63616c65707371007e0027010170707400165245504f52545f5245534f555243455f42554e444c45707371007e002a7070707400186a6176612e7574696c2e5265736f7572636542756e646c65707371007e0027010170707400105245504f52545f54494d455f5a4f4e45707371007e002a7070707400126a6176612e7574696c2e54696d655a6f6e65707371007e0027010170707400155245504f52545f464f524d41545f464143544f5259707371007e002a70707074002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e7574696c2e466f726d6174466163746f7279707371007e0027010170707400135245504f52545f434c4153535f4c4f41444552707371007e002a7070707400156a6176612e6c616e672e436c6173734c6f61646572707371007e00270101707074001a5245504f52545f55524c5f48414e444c45525f464143544f5259707371007e002a7070707400206a6176612e6e65742e55524c53747265616d48616e646c6572466163746f7279707371007e0027010170707400145245504f52545f46494c455f5245534f4c564552707371007e002a70707074002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e7574696c2e46696c655265736f6c766572707371007e0027010170707400105245504f52545f54454d504c41544553707371007e002a7070707400146a6176612e7574696c2e436f6c6c656374696f6e707371007e00270101707074000b534f52545f4649454c4453707371007e002a70707074000e6a6176612e7574696c2e4c697374707371007e00270101707074000646494c544552707371007e002a7070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e4461746173657446696c746572707371007e002a70707070707070707372000e6a6176612e7574696c2e55554944bc9903f7986d852f0200024a000c6c65617374536967426974734a000b6d6f73745369674269747378708f0ea80773ab7ae1bbcb761587a94cf2757200295b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525661726961626c653b62e6837c982cb7440200007870000000057372002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655661726961626c6500000000000027d802001149001950534555444f5f53455249414c5f56455253494f4e5f55494442000b63616c63756c6174696f6e42000d696e6372656d656e74547970655a000f697353797374656d446566696e65644200097265736574547970654c001063616c63756c6174696f6e56616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f43616c63756c6174696f6e456e756d3b4c000a65787072657373696f6e71007e00194c000e696e6372656d656e7447726f75707400254c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5247726f75703b4c0012696e6372656d656e745479706556616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f496e6372656d656e7454797065456e756d3b4c001b696e6372656d656e746572466163746f7279436c6173734e616d6571007e00024c001f696e6372656d656e746572466163746f7279436c6173735265616c4e616d6571007e00024c0016696e697469616c56616c756545787072657373696f6e71007e00194c00046e616d6571007e00024c000a726573657447726f757071007e00764c000e72657365745479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f526573657454797065456e756d3b4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e00027870000077ee000001007e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e43616c63756c6174696f6e456e756d00000000000000001200007871007e001274000653595354454d70707e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e496e6372656d656e7454797065456e756d00000000000000001200007871007e00127400044e4f4e457070737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736545787072657373696f6e00000000000027d802000449000269645b00066368756e6b737400305b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5245787072657373696f6e4368756e6b3b4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e0002787000000000757200305b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5245787072657373696f6e4368756e6b3b6d59cfde694ba355020000787000000001737200366e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736545787072657373696f6e4368756e6b00000000000027d8020002420004747970654c00047465787471007e00027870017400186e6577206a6176612e6c616e672e496e7465676572283129707074000b504147455f4e554d424552707e72002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e526573657454797065456e756d00000000000000001200007871007e00127400065245504f525471007e003e707371007e0074000077ee0000010071007e007b707071007e007e70707371007e0080000000017571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283129707074000d434f4c554d4e5f4e554d424552707e71007e00897400045041474571007e003e707371007e0074000077ee000001007e71007e007a740005434f554e547371007e0080000000027571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000037571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c5245504f52545f434f554e547071007e008a71007e003e707371007e0074000077ee0000010071007e00957371007e0080000000047571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000057571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000a504147455f434f554e547071007e009271007e003e707371007e0074000077ee0000010071007e00957371007e0080000000067571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000077571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c434f4c554d4e5f434f554e54707e71007e0089740006434f4c554d4e71007e003e707e72003c6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5768656e5265736f757263654d697373696e6754797065456e756d00000000000000001200007871007e00127400044e554c4c70707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736553656374696f6e00000000000027d80200015b000562616e64737400255b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5242616e643b7870757200255b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5242616e643b95dd7eec8cca85350200007870000000017372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736542616e6400000000000027d802000749001950534555444f5f53455249414c5f56455253494f4e5f5549444900066865696768745a000e697353706c6974416c6c6f7765644c00137072696e745768656e45787072657373696f6e71007e00194c000d70726f706572746965734d617071007e001c4c000973706c6974547970657400104c6a6176612f6c616e672f427974653b4c000e73706c69745479706556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f53706c697454797065456e756d3b787200336e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365456c656d656e7447726f757000000000000027d80200024c00086368696c6472656e71007e002b4c000c656c656d656e7447726f757074002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52456c656d656e7447726f75703b7870737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a65787000000008770400000008737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655374617469635465787400000000000027d80200014c00047465787471007e0002787200326e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736554657874456c656d656e7400000000000027d802002549001950534555444f5f53455249414c5f56455253494f4e5f5549444c0006626f7264657271007e00bf4c000b626f72646572436f6c6f727400104c6a6176612f6177742f436f6c6f723b4c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e677400134c6a6176612f6c616e672f496e74656765723b4c0008666f6e744e616d6571007e00024c0008666f6e7453697a6571007e00c94c0013686f72697a6f6e74616c416c69676e6d656e7471007e00bf4c0018686f72697a6f6e74616c416c69676e6d656e7456616c75657400364c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f486f72697a6f6e74616c416c69676e456e756d3b4c00066973426f6c647400134c6a6176612f6c616e672f426f6f6c65616e3b4c000869734974616c696371007e00cb4c000d6973506466456d62656464656471007e00cb4c000f6973537472696b655468726f75676871007e00cb4c000c69735374796c65645465787471007e00cb4c000b6973556e6465726c696e6571007e00cb4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f787400274c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a524c696e65426f783b4c000b6c696e6553706163696e6771007e00bf4c00106c696e6553706163696e6756616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e6553706163696e67456e756d3b4c00066d61726b757071007e00024c000770616464696e6771007e00c94c00097061726167726170687400294c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525061726167726170683b4c000b706466456e636f64696e6771007e00024c000b706466466f6e744e616d6571007e00024c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c0008726f746174696f6e71007e00bf4c000d726f746174696f6e56616c756574002f4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f526f746174696f6e456e756d3b4c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c94c0011766572746963616c416c69676e6d656e7471007e00bf4c0016766572746963616c416c69676e6d656e7456616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f566572746963616c416c69676e456e756d3b7872002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365456c656d656e7400000000000027d802001b49001950534555444f5f53455249414c5f56455253494f4e5f5549444900066865696768745a001769735072696e74496e466972737457686f6c6542616e645a001569735072696e74526570656174656456616c7565735a001a69735072696e745768656e44657461696c4f766572666c6f77735a0015697352656d6f76654c696e655768656e426c616e6b42000c706f736974696f6e5479706542000b7374726574636854797065490005776964746849000178490001794c00096261636b636f6c6f7271007e00c84c001464656661756c745374796c6550726f76696465727400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5244656661756c745374796c6550726f76696465723b4c000c656c656d656e7447726f757071007e00c24c0009666f7265636f6c6f7271007e00c84c00036b657971007e00024c00046d6f646571007e00bf4c00096d6f646556616c756574002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4d6f6465456e756d3b4c000b706172656e745374796c6571007e00074c0018706172656e745374796c654e616d655265666572656e636571007e00024c0011706f736974696f6e5479706556616c75657400334c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f506f736974696f6e54797065456e756d3b4c00137072696e745768656e45787072657373696f6e71007e00194c00157072696e745768656e47726f75704368616e67657371007e00764c000d70726f706572746965734d617071007e001c5b001370726f706572747945787072657373696f6e737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250726f706572747945787072657373696f6e3b4c0010737472657463685479706556616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5374726574636854797065456e756d3b4c00047575696471007e002078700000c354000000100001000000000000032f0000000b000000017372000e6a6176612e6177742e436f6c6f7201a51783108f337502000546000666616c70686149000576616c75654c0002637374001b4c6a6176612f6177742f636f6c6f722f436f6c6f7253706163653b5b00096672676276616c75657400025b465b00066676616c756571007e00da787000000000fff4f4f470707071007e001071007e00c37070707e7200296e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4d6f6465456e756d00000000000000001200007871007e00127400064f504151554570707e7200316e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e506f736974696f6e54797065456e756d00000000000000001200007871007e00127400134649585f52454c41544956455f544f5f544f507371007e00800000002f7571007e0083000000027371007e00850474000c5245504f52545f434f554e547371007e0085017400072532203d3d203070707070707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5374726574636854797065456e756d00000000000000001200007871007e001274000a4e4f5f535452455443487371007e0070b3d90ea0f9d2f8790b32f97431ce4dd10000c3547070707070707070707070707070707070707372002e6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654c696e65426f7800000000000027d802000b4c000d626f74746f6d50616464696e6771007e00c94c0009626f74746f6d50656e74002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f626173652f4a52426f7850656e3b4c000c626f78436f6e7461696e657274002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52426f78436f6e7461696e65723b4c000b6c65667450616464696e6771007e00c94c00076c65667450656e71007e00ed4c000770616464696e6771007e00c94c000370656e71007e00ed4c000c726967687450616464696e6771007e00c94c0008726967687450656e71007e00ed4c000a746f7050616464696e6771007e00c94c0006746f7050656e71007e00ed787070737200336e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78426f74746f6d50656e00000000000027d80200007872002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f7850656e00000000000027d80200014c00076c696e65426f7871007e00cc7872002a6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736550656e00000000000027d802000649001950534555444f5f53455249414c5f56455253494f4e5f5549444c00096c696e65436f6c6f7271007e00c84c00096c696e655374796c6571007e00bf4c000e6c696e655374796c6556616c75657400304c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e655374796c65456e756d3b4c00096c696e6557696474687400114c6a6176612f6c616e672f466c6f61743b4c000c70656e436f6e7461696e657274002c4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250656e436f6e7461696e65723b78700000c3547070707071007e00ef71007e00ef71007e00d770737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f784c65667450656e00000000000027d80200007871007e00f10000c3547070707071007e00ef71007e00ef707371007e00f10000c3547070707071007e00ef71007e00ef70737200326e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78526967687450656e00000000000027d80200007871007e00f10000c3547070707071007e00ef71007e00ef70737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365426f78546f7050656e00000000000027d80200007871007e00f10000c3547070707071007e00ef71007e00ef70707070737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736550617261677261706800000000000027d802000a4c000f66697273744c696e65496e64656e7471007e00c94c000a6c656674496e64656e7471007e00c94c000b6c696e6553706163696e6771007e00cd4c000f6c696e6553706163696e6753697a6571007e00f44c0012706172616772617068436f6e7461696e65727400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52506172616772617068436f6e7461696e65723b4c000b7269676874496e64656e7471007e00c94c000c73706163696e67416674657271007e00c94c000d73706163696e674265666f726571007e00c94c000c74616253746f70576964746871007e00c94c000874616253746f707371007e002b78707070707071007e00d770707070707070707070707070707070707400007372002b6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654c696e6500000000000027d802000349001950534555444f5f53455249414c5f56455253494f4e5f554944420009646972656374696f6e4c000e646972656374696f6e56616c75657400344c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4c696e65446972656374696f6e456e756d3b787200356e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736547726170686963456c656d656e7400000000000027d802000549001950534555444f5f53455249414c5f56455253494f4e5f5549444c000466696c6c71007e00bf4c000966696c6c56616c756574002b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f46696c6c456e756d3b4c00076c696e6550656e7400234c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5250656e3b4c000370656e71007e00bf7871007e00d10000c354000000010001000000000000032f0000000d000000117071007e001071007e00c37371007e00d800000000ff3d9297707070707070707071007e00e07371007e00800000003070707070707071007e00e97371007e0070bfe78f35bf02676c826bc27dc9f149cb000077ee70707371007e00f20000c3547070707372000f6a6176612e6c616e672e466c6f6174daedc9a2db3cf0ec02000146000576616c7565787200106a6176612e6c616e672e4e756d62657286ac951d0b94e08b02000078703f00000071007e0107700000c354007e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4c696e65446972656374696f6e456e756d00000000000000001200007871007e0012740008544f505f444f574e737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365546578744669656c6400000000000027d802001549001950534555444f5f53455249414c5f56455253494f4e5f55494449000d626f6f6b6d61726b4c6576656c42000e6576616c756174696f6e54696d6542000f68797065726c696e6b54617267657442000d68797065726c696e6b547970655a0015697353747265746368576974684f766572666c6f774c0014616e63686f724e616d6545787072657373696f6e71007e00194c000f6576616c756174696f6e47726f757071007e00764c00136576616c756174696f6e54696d6556616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4576616c756174696f6e54696d65456e756d3b4c000a65787072657373696f6e71007e00194c001968797065726c696e6b416e63686f7245787072657373696f6e71007e00194c001768797065726c696e6b5061676545787072657373696f6e71007e00195b001368797065726c696e6b506172616d65746572737400335b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5248797065726c696e6b506172616d657465723b4c001c68797065726c696e6b5265666572656e636545787072657373696f6e71007e00194c001a68797065726c696e6b546f6f6c74697045787072657373696f6e71007e00194c001768797065726c696e6b5768656e45787072657373696f6e71007e00194c000f6973426c616e6b5768656e4e756c6c71007e00cb4c000a6c696e6b54617267657471007e00024c00086c696e6b5479706571007e00024c00077061747465726e71007e00024c00117061747465726e45787072657373696f6e71007e00197871007e00c70000c354000000110001000000000000007200000036000000007071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e0070a8a83cb2644861a38defe30eb64e47350000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e011771007e011771007e0115707371007e00f70000c3547070707071007e011771007e0117707371007e00f10000c3547070707071007e011771007e0117707371007e00fa0000c3547070707071007e011771007e0117707371007e00fc0000c3547070707071007e011771007e0117707070707371007e00fe7070707071007e011570707070707070707070707070707070700000c354000000000000000070707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4576616c756174696f6e54696d65456e756d00000000000000001200007871007e00127400034e4f577371007e0080000000317571007e0083000000017371007e00850374000b70726f647563744e616d65707070707070707070707070707371007e01120000c354000000100001000000000000007b000000a8000000027071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e007081564241daa02c6ca295cc26fddc4b690000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e012771007e012771007e0125707371007e00f70000c3547070707071007e012771007e0127707371007e00f10000c3547070707071007e012771007e0127707371007e00fa0000c3547070707071007e012771007e0127707371007e00fc0000c3547070707071007e012771007e0127707070707371007e00fe7070707071007e012570707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000327571007e0083000000017371007e00850374000b62617463684e756d626572707070707070707070707070707371007e01120000c354000000100001000000000000007b00000124000000017071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e00708b1e4dd2215ee2e696c00dd4a1ca423d0000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e013471007e013471007e0132707371007e00f70000c3547070707071007e013471007e0134707371007e00f10000c3547070707071007e013471007e0134707371007e00fa0000c3547070707071007e013471007e0134707371007e00fc0000c3547070707071007e013471007e0134707070707371007e00fe7070707071007e013270707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000337571007e0083000000017371007e00850374000e65787069726174696f6e44617465707070707070707070707070707371007e01120000c35400000010000100000000000000740000019f000000007071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e007081e7431a00ec999f699d01bbc49a43d20000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e014171007e014171007e013f707371007e00f70000c3547070707071007e014171007e0141707371007e00f10000c3547070707071007e014171007e0141707371007e00fa0000c3547070707071007e014171007e0141707371007e00fc0000c3547070707071007e014171007e0141707070707371007e00fe7070707071007e013f70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000347571007e0083000000017371007e0085037400117175616e74697479526571756573746564707070707070707070707070707371007e01120000c354000000110001000000000000002b0000000b000000007371007e00d800000000ff33333370707071007e001071007e00c37371007e00d800000000ff00000070707070707e71007e00dc74000b5452414e53504152454e54707071007e00e07070707071007e00e97371007e0070833f67e15559039f8e910cc550ce49660000c354707070707074000953616e735365726966737200116a6176612e6c616e672e496e746567657212e2a0a4f781873802000149000576616c75657871007e010d0000000a707e7200346e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e486f72697a6f6e74616c416c69676e456e756d00000000000000001200007871007e00127400044c454654737200116a6176612e6c616e672e426f6f6c65616ecd207280d59cfaee0200015a000576616c756578700071007e015971007e015971007e01597071007e01597070707371007e00ec707371007e00f00000c3547070707071007e015a71007e015a71007e014c707371007e00f70000c3547070707071007e015a71007e015a707371007e00f10000c3547070707071007e015a71007e015a707371007e00fa0000c3547070707071007e015a71007e015a707371007e00fc0000c3547070707071007e015a71007e015a70707400046e6f6e65707371007e00fe70707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4c696e6553706163696e67456e756d00000000000000001200007871007e001274000653494e474c457071007e014c707070707074000643703132353270707070707e72002d6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e526f746174696f6e456e756d00000000000000001200007871007e00127400044e4f4e45707070707e7200326e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e566572746963616c416c69676e456e756d00000000000000001200007871007e00127400064d4944444c450000c3540000000000000000707071007e011f7371007e0080000000357571007e0083000000017371007e00850474000c5245504f52545f434f554e54707070707070707070707070707371007e01120000c354000000100001000000000000005800000213000000007071007e001071007e00c370707070707071007e00e07070707071007e00e97371007e0070a2fa2fec6858939af04da71d50734ec50000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e017271007e017271007e0170707371007e00f70000c3547070707071007e017271007e0172707371007e00f10000c3547070707071007e017271007e0172707371007e00fa0000c3547070707071007e017271007e0172707371007e00fc0000c3547070707071007e017271007e0172707070707371007e00fe7070707071007e017070707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000367571007e0083000000017371007e0085037400087175616e746974797070707070707070707070707078700000c35400000013017070707070707400046a617661707371007e00170000c3540100757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a524669656c643b023cdfc74e2af27002000078700000000d7372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654669656c6400000000000027d80200054c000b6465736372697074696f6e71007e00024c00046e616d6571007e00024c000d70726f706572746965734d617071007e001c4c000e76616c7565436c6173734e616d6571007e00024c001276616c7565436c6173735265616c4e616d6571007e000278707074000966697273744e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400086c6173744e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000b70726f647563744e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000b73746f636b4f6e48616e647371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e01817074000e746f466163696c6974794e616d657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400096973737565446174657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000c6973737565566f75636865727371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000b62617463684e756d6265727371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e01817074000e65787069726174696f6e446174657371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e0181707400087175616e746974797371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e0181707400117175616e746974795265717565737465647371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e0181707400036761707371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e01817074000f70726f6475637443617465676f72797371007e002a7070707400106a6176612e6c616e672e537472696e677070757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5247726f75703b40a35f7a4cfd78ea0200007870000000027372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736547726f757000000000000027d802001049001950534555444f5f53455249414c5f56455253494f4e5f55494442000e666f6f746572506f736974696f6e5a0019697352657072696e744865616465724f6e45616368506167655a001169735265736574506167654e756d6265725a0010697353746172744e6577436f6c756d6e5a000e697353746172744e6577506167655a000c6b656570546f6765746865724900176d696e486569676874546f53746172744e6577506167654c000d636f756e745661726961626c657400284c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a525661726961626c653b4c000a65787072657373696f6e71007e00194c0013666f6f746572506f736974696f6e56616c75657400354c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f466f6f746572506f736974696f6e456e756d3b4c000b67726f7570466f6f74657271007e00044c001267726f7570466f6f74657253656374696f6e71007e00084c000b67726f757048656164657271007e00044c001267726f757048656164657253656374696f6e71007e00084c00046e616d6571007e000278700000c354000000000000000000007371007e0074000077ee0000010071007e00957371007e0080000000097571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e00800000000a7571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074000c4865616465725f434f554e5471007e01bb7e71007e008974000547524f555071007e003e70707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e466f6f746572506f736974696f6e456e756d00000000000000001200007871007e00127400064e4f524d414c707371007e00b97571007e00bc000000017371007e00be7371007e00c4000000027704000000027371007e01120000c3540000000a0001000000000000001300000329000000257071007e001071007e01cd70707070707071007e00e07070707071007e00e97371007e0070bbf9a5527705d801477ffc9d22bf46f50000c3547070707070707371007e01530000000870707070707070707070707371007e00ec707371007e00f00000c3547070707071007e01d271007e01d271007e01cf707371007e00f70000c3547070707071007e01d271007e01d2707371007e00f10000c3547070707071007e01d271007e01d2707371007e00fa0000c3547070707071007e01d271007e01d2707371007e00fc0000c3547070707071007e01d271007e01d2707070707371007e00fe7070707071007e01cf70707070707070707070707070707070700000c354000000000000000070707e71007e011e7400065245504f52547371007e0080000000187571007e0083000000027371007e008501740006222022202b207371007e00850474000b504147455f4e554d424552707070707070707070707070707371007e01120000c3540000000a000100000000000000c700000262000000257071007e001071007e01cd70707070707071007e00e07070707071007e00e97371007e0070843f39a0c2e70009c6758d1a4ca348990000c35470707070707071007e01d1707e71007e015574000552494748547070707070707070707371007e00ec707371007e00f00000c3547070707071007e01e571007e01e571007e01e1707371007e00f70000c3547070707071007e01e571007e01e5707371007e00f10000c3547070707071007e01e571007e01e5707371007e00fa0000c3547070707071007e01e571007e01e5707371007e00fc0000c3547070707071007e01e571007e01e5707070707371007e00fe7070707071007e01e170707070707070707070707070707070700000c3540000000000000001707071007e011f7371007e0080000000197571007e0083000000057371007e0085017400046d7367287371007e00850574000d6c6162656c2e706167652e6f667371007e0085017400022c207371007e00850474000b504147455f4e554d4245527371007e0085017400012970707070707070707070707400007078700000c3540000002f0170707070707371007e00b97571007e00bc000000027371007e00be7371007e00c4000000017704000000017371007e01120000c35400000032000100000000000002da0000000b000000007071007e001071007e01fb70707070707071007e00e0707070707e71007e00e874001a52454c41544956455f544f5f54414c4c4553545f4f424a4543547371007e00708f61a735ab2074b7212194e972ca43210000c354707070707074000953616e7353657269667371007e015300000010707e71007e015574000643454e544552707070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e020571007e020571007e01fd707371007e00f70000c3547070707071007e020571007e0205707371007e00f10000c3547070707071007e020571007e0205707371007e00fa0000c3547070707071007e020571007e0205707371007e00fc0000c3547070707071007e020571007e0205707070707371007e00fe7070707071007e01fd7070707070707070707070707070707071007e016a0000c3540000000000000001707071007e011f7371007e0080000000107571007e0083000000017371007e00850574001a6c6162656c2e76616363696e652e646973747269627574696f6e7070707070707070707070707078700000c35400000032017371007e00800000000f7571007e0083000000027371007e00850474000b504147455f4e554d4245527371007e0085017400382e696e7456616c75652829203d3d20313f206e657720426f6f6c65616e287472756529203a206e657720426f6f6c65616e2866616c73652970707070707371007e00be7371007e00c4000000027704000000027372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173654672616d6500000000000027d80200114c0006626f7264657271007e00bf4c000b626f72646572436f6c6f7271007e00c84c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e6771007e00c94c00086368696c6472656e71007e002b4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f7871007e00cc4c000770616464696e6771007e00c94c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c97871007e00d10000c354000000510001000000000000032f0000000b000000007371007e00d800000000ffffffff70707071007e001071007e02167371007e00d800000000ff3d92977070707070707372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a52426173655374796c65000000000000271102003a49001950534555444f5f53455249414c5f56455253494f4e5f5549445a0009697344656661756c744c00096261636b636f6c6f7271007e00c84c0006626f7264657271007e00bf4c000b626f72646572436f6c6f7271007e00c84c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e6771007e00c95b0011636f6e646974696f6e616c5374796c65737400315b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a52436f6e646974696f6e616c5374796c653b4c001464656661756c745374796c6550726f766964657271007e00d24c000466696c6c71007e00bf4c000966696c6c56616c756571007e01054c0008666f6e744e616d6571007e00024c0008666f6e7453697a6571007e00c94c0009666f7265636f6c6f7271007e00c84c0013686f72697a6f6e74616c416c69676e6d656e7471007e00bf4c0018686f72697a6f6e74616c416c69676e6d656e7456616c756571007e00ca4c000f6973426c616e6b5768656e4e756c6c71007e00cb4c00066973426f6c6471007e00cb4c000869734974616c696371007e00cb4c000d6973506466456d62656464656471007e00cb4c000f6973537472696b655468726f75676871007e00cb4c000c69735374796c65645465787471007e00cb4c000b6973556e6465726c696e6571007e00cb4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f7871007e00cc4c00076c696e6550656e71007e01064c000b6c696e6553706163696e6771007e00bf4c00106c696e6553706163696e6756616c756571007e00cd4c00066d61726b757071007e00024c00046d6f646571007e00bf4c00096d6f646556616c756571007e00d34c00046e616d6571007e00024c000770616464696e6771007e00c94c000970617261677261706871007e00ce4c000b706172656e745374796c6571007e00074c0018706172656e745374796c654e616d655265666572656e636571007e00024c00077061747465726e71007e00024c000b706466456e636f64696e6771007e00024c000b706466466f6e744e616d6571007e00024c000370656e71007e00bf4c000c706f736974696f6e5479706571007e00bf4c000672616469757371007e00c94c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c0008726f746174696f6e71007e00bf4c000d726f746174696f6e56616c756571007e00cf4c000a7363616c65496d61676571007e00bf4c000f7363616c65496d61676556616c75657400314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f5363616c65496d616765456e756d3b4c000b737472657463685479706571007e00bf4c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c94c0011766572746963616c416c69676e6d656e7471007e00bf4c0016766572746963616c416c69676e6d656e7456616c756571007e00d078700000c354007371007e00d800000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e022171007e022171007e021f707371007e00f70000c3547070707071007e022171007e0221707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e022171007e0221707371007e00fa0000c3547070707071007e022171007e0221707371007e00fc0000c3547070707071007e022171007e02217371007e00f20000c3547070707071007e021f7070707071007e00dd7400087461626c655f5448707371007e00fe7070707071007e021f70707070707070707070707070707070707070707070707070707071007e00e07070707071007e00e97371007e0070aab0863025404974605cceed5add40d170707070707371007e00c4000000087704000000087371007e01120000c354000000140101000000000000006c000000040000000e7071007e001071007e021970707070707071007e00e07070707071007e00e97371007e0070a9626dd2252913ef3d3d4b9482f74b5b0000c3547070707070707371007e01530000000c70707371007e0158017070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e023271007e023271007e022e707371007e00f70000c3547070707071007e023271007e0232707371007e00f10000c3547070707071007e023271007e0232707371007e00fa0000c3547070707071007e023271007e0232707371007e00fc0000c3547070707071007e023271007e0232707070707371007e00fe7070707071007e022e70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000127571007e0083000000017371007e0085057400226c6162656c2e76616363696e652e73746f636b2e64697374726962757465642e746f707070707070707070707070707371007e01120000c354000000140101000000000000006c00000004000000227071007e001071007e021970707070707071007e00e07070707071007e00e97371007e007095e34a3d34d62cdee9ad75ffe3a04d900000c35470707070707071007e0230707071007e02317070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e023f71007e023f71007e023d707371007e00f70000c3547070707071007e023f71007e023f707371007e00f10000c3547070707071007e023f71007e023f707371007e00fa0000c3547070707071007e023f71007e023f707371007e00fc0000c3547070707071007e023f71007e023f707070707371007e00fe7070707071007e023d70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000137571007e0083000000017371007e0085057400186c6162656c2e76616363696e652e69737375652e64617465707070707070707070707070707371007e01120000c354000000140101000000000000006c00000004000000367071007e001071007e021970707070707071007e00e07070707071007e00e97371007e007091077b3feda429dfc33894c16bff4ce80000c35470707070707071007e0230707071007e02317070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e024c71007e024c71007e024a707371007e00f70000c3547070707071007e024c71007e024c707371007e00f10000c3547070707071007e024c71007e024c707371007e00fa0000c3547070707071007e024c71007e024c707371007e00fc0000c3547070707071007e024c71007e024c707070707371007e00fe7070707071007e024a70707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000147571007e0083000000017371007e0085057400216c6162656c2e76616363696e652e73746f636b2e69737375652e766f7563686572707070707070707070707070707371007e00c60000c354000000140001000000000000000f000000700000000e7071007e001071007e021970707070707071007e00e07070707071007e00e97371007e0070b10418c61404d5460ddeb01912d84bb90000c3547070707070707371007e01530000000d7071007e020371007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e025a71007e025a71007e0257707371007e00f70000c3547070707071007e025a71007e025a707371007e00f10000c3547070707071007e025a71007e025a707371007e00fa0000c3547070707071007e025a71007e025a707371007e00fc0000c3547070707071007e025a71007e025a707070707371007e00fe7070707071007e0257707070707070707070707070707070707e71007e0169740003544f5074000520203a20207371007e00c60000c354000000140001000000000000000f00000070000000227071007e001071007e021970707070707071007e00e07070707071007e00e97371007e0070af356388283b2d2c86d202711f404af90000c35470707070707071007e02597071007e020371007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e026671007e026671007e0264707371007e00f70000c3547070707071007e026671007e0266707371007e00f10000c3547070707071007e026671007e0266707371007e00fa0000c3547070707071007e026671007e0266707371007e00fc0000c3547070707071007e026671007e0266707070707371007e00fe7070707071007e02647070707070707070707070707070707071007e026174000520203a20207371007e00c60000c354000000140001000000000000000f00000070000000367071007e001071007e021970707070707071007e00e07070707071007e00e97371007e00709b8aae125bf14b535a3b255b5a4149790000c35470707070707071007e02597071007e020371007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e027071007e027071007e026e707371007e00f70000c3547070707071007e027071007e0270707371007e00f10000c3547070707071007e027071007e0270707371007e00fa0000c3547070707071007e027071007e0270707371007e00fc0000c3547070707071007e027071007e0270707070707371007e00fe7070707071007e026e7070707070707070707070707070707071007e026174000520203a20207371007e01120000c35400000014010100000000000000ec0000007f0000000e7071007e001071007e021970707070707071007e00e07070707071007e00e97371007e0070bbf2b5ba76b472429a331883a24f45fa0000c35470707070707071007e02307070707070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e027a71007e027a71007e0278707371007e00f70000c3547070707071007e027a71007e027a707371007e00f10000c3547070707071007e027a71007e027a707371007e00fa0000c3547070707071007e027a71007e027a707371007e00fc0000c3547070707071007e027a71007e027a707070707371007e00fe7070707071007e027870707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000157571007e0083000000017371007e00850374000e746f466163696c6974794e616d65707070707070707070707070707371007e01120000c35400000014010100000000000000ec0000007f000000227071007e001071007e021970707070707071007e00e07070707071007e00e97371007e0070acfb8e262215827d219fe37117e14ab40000c35470707070707071007e02307070707070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e028771007e028771007e0285707371007e00f70000c3547070707071007e028771007e0287707371007e00f10000c3547070707071007e028771007e0287707371007e00fa0000c3547070707071007e028771007e0287707371007e00fc0000c3547070707071007e028771007e0287707070707371007e00fe7070707071007e02857070707070707070707070707070707071007e016a0000c3540000000000000001707071007e011f7371007e0080000000167571007e0083000000017371007e008503740009697373756544617465707070707070707070707074000a64642d4d4d2d7979797970787070707371007e00ec707371007e00f00000c3547070707071007e029371007e029371007e0219707371007e00f70000c3547070707071007e029371007e0293707371007e00f10000c3547070707071007e029371007e0293707371007e00fa0000c3547070707071007e029371007e0293707371007e00fc0000c3547070707071007e029371007e0293707070707070707371007e01120000c35400000014010100000000000000ec0000008a000000367071007e001071007e02167070707070707e71007e00df740005464c4f41547070707071007e00e97371007e00708d5ba56fb297c06b5bca3f3a1c7a4a3b0000c354707070707074000953616e73536572696671007e02307071007e0156707070707071007e01597070707371007e00ec707371007e00f00000c3547070707071007e029e71007e029e71007e0299707371007e00f70000c3547070707071007e029e71007e029e707371007e00f10000c3547070707071007e029e71007e029e707371007e00fa0000c3547070707071007e029e71007e029e707371007e00fc0000c3547070707071007e029e71007e029e707070707371007e00fe7070707071007e02997070707070707070707070707070707071007e016a0000c3540000000000000001707071007e011f7371007e0080000000177571007e0083000000017371007e00850374000c6973737565566f7563686572707070707070707070707074000a64642d4d4d2d797979797078700000c35400000053017371007e0080000000117571007e0083000000027371007e00850474000b504147455f4e554d4245527371007e0085017400382e696e7456616c75652829203d3d20313f206e657720426f6f6c65616e287472756529203a206e657720426f6f6c65616e2866616c73652970707070707400064865616465727371007e01b80000c354000000000000000000007371007e0074000077ee0000010071007e00957371007e00800000000b7571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e00800000000c7571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707074001e47726f75702062792050726f6475637443617465676f72795f434f554e5471007e02b171007e01c671007e003e707371007e00800000001a7571007e0083000000017371007e00850374000f70726f6475637443617465676f7279707071007e01c9707371007e00b97571007e00bc000000017371007e00be7371007e00c400000010770400000010737200306e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736552656374616e676c6500000000000027d80200014c000672616469757371007e00c97871007e01040000c354000000ab0001000000000000032d0000000d000000157071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e007082d33a204b248b0cf5679f44951b47b0000077ee70707371007e00f20000c3547070707071007e02c570707371007e01120000c3540000001500010000000000000098000000120000001f7371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070b8cabee881686723bd180386f3a34ef60000c35470707070707071007e0230707071007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e02cd71007e02cd71007e02c9707371007e00f70000c3547070707071007e02cd71007e02cd707371007e00f10000c3547070707071007e02cd71007e02cd707371007e00fa0000c3547070707071007e02cd71007e02cd707371007e00fc0000c3547070707071007e02cd71007e02cd707070707371007e00fe7070707071007e02c9707070707070707070707070707070707e71007e0169740006424f54544f4d0000c3540000000000000000707071007e011f7371007e0080000000217571007e0083000000017371007e0085057400156c6162656c2e69737375696e672e6f666669636572707070707070707070707070707371007e01020000c354000000010001000000000000007d000000aa000000337071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e0070967d09307996c1aa42f44448a1c84b46000077ee70707371007e00f20000c3547070707071007e02da700000c3540071007e01107371007e01120000c35400000014000100000000000000f6000000aa000000207071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e0070ba09f8314956b372d07d6d8844e344230000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e02df71007e02df71007e02dd707371007e00f70000c3547070707071007e02df71007e02df707371007e00f10000c3547070707071007e02df71007e02df707371007e00fa0000c3547070707071007e02df71007e02df707371007e00fc0000c3547070707071007e02df71007e02df707070707371007e00fe7070707071007e02dd7070707070707070707070707070707071007e02d40000c3540000000000000000707071007e011f7371007e0080000000227571007e0083000000037371007e00850374000966697273744e616d657371007e008501740006202b2220222b7371007e0085037400086c6173744e616d65707070707070707070707070707371007e01120000c3540000001600010000000000000098000000120000003d7371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e00709a62b80659c9bfade8295969b92249070000c35470707070707071007e025970707070707070707070707371007e00ec707371007e00f00000c3547070707071007e02f271007e02f271007e02ee707371007e00f70000c3547070707071007e02f271007e02f2707371007e00f10000c3547070707071007e02f271007e02f2707371007e00fa0000c3547070707071007e02f271007e02f2707371007e00fc0000c3547070707071007e02f271007e02f2707070707371007e00fe7070707071007e02ee7070707070707070707070707070707071007e02d40000c3540000000000000000707071007e011f7371007e0080000000237571007e0083000000017371007e00850574000f6c6162656c2e7369676e6174757265707070707070707070707070707371007e01020000c354000000010001000000000000007b000000aa000000507071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e0070a70de29f327d0e78225b8863a2924a41000077ee70707371007e00f20000c3547070707071007e02fd700000c3540071007e01107371007e01020000c354000000010001000000000000007b000001fc0000003b7071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e00708f39da880abf4d75926ea451cf79437f000077ee70707371007e00f20000c3547070707071007e0300700000c3540071007e01107371007e01120000c3540000001200010000000000000091000001fc0000003e7371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070b934edf2fd2dd3773cf77c080c3c47e30000c35470707070707071007e025970707070707070707070707371007e00ec707371007e00f00000c3547070707071007e030771007e030771007e0303707371007e00f70000c3547070707071007e030771007e0307707371007e00f10000c3547070707071007e030771007e0307707371007e00fa0000c3547070707071007e030771007e0307707371007e00fc0000c3547070707071007e030771007e0307707070707371007e00fe7070707071007e03037070707070707070707070707070707071007e02d40000c3540000000000000000707071007e011f7371007e0080000000247571007e0083000000017371007e0085057400106c6162656c2e64657369676e61746564707070707070707070707070707371007e01120000c354000000140001000000000000009800000010000000757371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070a227e0ffa5c590a6ef99e0fef92a45300000c35470707070707071007e025970707070707070707070707371007e00ec707371007e00f00000c3547070707071007e031671007e031671007e0312707371007e00f70000c3547070707071007e031671007e0316707371007e00f10000c3547070707071007e031671007e0316707371007e00fa0000c3547070707071007e031671007e0316707371007e00fc0000c3547070707071007e031671007e0316707070707371007e00fe7070707071007e031270707070707070707070707070707070700000c3540000000000000000707071007e011f7371007e0080000000257571007e0083000000017371007e0085057400176c6162656c2e726563656976696e672e6f666669636572707070707070707070707070707371007e01120000c354000000140001000000000000009800000010000000897371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070936097dd4b679f0c2f9bf533abe14f340000c35470707070707071007e025970707070707070707070707371007e00ec707371007e00f00000c3547070707071007e032571007e032571007e0321707371007e00f70000c3547070707071007e032571007e0325707371007e00f10000c3547070707071007e032571007e0325707371007e00fa0000c3547070707071007e032571007e0325707371007e00fc0000c3547070707071007e032571007e0325707070707371007e00fe7070707071007e03217070707070707070707070707070707071007e02d40000c3540000000000000000707071007e011f7371007e0080000000267571007e0083000000017371007e00850574000f6c6162656c2e7369676e6174757265707070707070707070707070707371007e01120000c3540000001400010000000000000098000000100000009d7371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070a95da2875d0627528840d90a2f8f4c8c0000c35470707070707071007e025970707070707070707070707371007e00ec707371007e00f00000c3547070707071007e033471007e033471007e0330707371007e00f70000c3547070707071007e033471007e0334707371007e00f10000c3547070707071007e033471007e0334707371007e00fa0000c3547070707071007e033471007e0334707371007e00fc0000c3547070707071007e033471007e0334707070707371007e00fe7070707071007e03307070707070707070707070707070707071007e02d40000c3540000000000000000707071007e011f7371007e0080000000277571007e0083000000017371007e00850574000a6c6162656c2e64617465707070707070707070707070707371007e01020000c354000000010001000000000000007b000000a8000000877071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e00708d75fd5d1948dbdc5786abae09cd4441000077ee70707371007e00f20000c3547070707071007e033f700000c3540071007e01107371007e01020000c354000000010001000000000000007b000000a60000009e7071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e00709e2cec59b18250a7e85e5be382e74bb2000077ee70707371007e00f20000c3547070707071007e0342700000c3540071007e01107371007e01020000c354000000010001000000000000007b000000a6000000ae7071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e0070b592e480b4736c98bc596f3819f747d6000077ee70707371007e00f20000c3547070707071007e0345700000c3540071007e01107371007e01120000c3540000001200010000000000000091000001fc0000009c7371007e00d800000000ff3d929770707071007e001071007e02c27371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e007087ac415958f5541b7a716745680a45190000c35470707070707071007e025970707070707070707070707371007e00ec707371007e00f00000c3547070707071007e034c71007e034c71007e0348707371007e00f70000c3547070707071007e034c71007e034c707371007e00f10000c3547070707071007e034c71007e034c707371007e00fa0000c3547070707071007e034c71007e034c707371007e00fc0000c3547070707071007e034c71007e034c707070707371007e00fe7070707071007e03487070707070707070707070707070707071007e02d40000c3540000000000000000707071007e011f7371007e0080000000287571007e0083000000017371007e0085057400106c6162656c2e64657369676e61746564707070707070707070707070707371007e01020000c354000000010001000000000000007b000001fc000000957071007e001071007e02c270707070707071007e00e07070707071007e00e97371007e00709d46146fc9becb2adf8aefe7bb1648ba000077ee70707371007e00f20000c3547070707071007e0357700000c3540071007e011078700000c354000000c20170707070707371007e00b97571007e00bc000000017371007e00be7371007e00c4000000077704000000077371007e00c60000c354000000140001000000000000032f0000000b000000007371007e00d800000000ff3d929770707071007e001071007e035c7371007e00d800000000ffffffff707070707071007e00dd707071007e00e070707371007e002a707371007e00c40000000177040000000174002f6e65742e73662e6a61737065727265706f7274732e6578706f72742e786c732e69676e6f72652e677261706869637378737200116a6176612e7574696c2e486173684d61700507dac1c31660d103000246000a6c6f6164466163746f724900097468726573686f6c6478703f400000000000037708000000040000000171007e036374000566616c7365787071007e00e97371007e00708bcb2b288460cec0424bfe2aed0446130000c3547070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e036871007e036871007e035e707371007e00f70000c3547070707071007e036871007e0368707371007e00f10000c3547070707071007e036871007e0368707371007e00fa0000c3547070707071007e036871007e0368707371007e00fc0000c3547070707071007e036871007e0368707070707371007e00fe7070707071007e035e70707070707070707070707070707070707400007371007e01120000c354000000140001000000000000002b0000000b000000007071007e001071007e035c70707070707071007e00e07070707071007e00e97371007e007085440aa452e2bc1a9504ccc64b9f46d40000c354707070707070707071007e01567070707070707070707371007e00ec707371007e00f00000c3547070707071007e037271007e037271007e0370707371007e00f70000c3547070707071007e037271007e0372707371007e00f10000c3547070707071007e037271007e0372707371007e00fa0000c3547070707071007e037271007e0372707371007e00fc0000c3547070707071007e037271007e0372707070707371007e00fe707371007e015300000005707071007e03707070707070707070707070707070707071007e016a0000c3540000000000000000707071007e011f7371007e00800000001b7571007e0083000000017371007e00850574000d6c6162656c2e6c696e652e6e6f707070707070707070707070707371007e01120000c354000000130001000000000000007200000036000000017371007e00d800000000ff33333370707071007e001071007e035c7371007e00d800000000ff000000707070707071007e014f707071007e00e07070707071007e00e97371007e0070b206fb110a9077bed5ba5f52522e4dc30000c354707070707074000953616e73536572696671007e01547071007e015671007e015971007e015971007e015971007e01597071007e01597070707371007e00ec707371007e00f00000c3547070707071007e038371007e038371007e037e707371007e00f70000c3547070707071007e038371007e0383707371007e00f10000c3547070707071007e038371007e0383707371007e00fa0000c3547070707071007e038371007e0383707371007e00fc0000c3547070707071007e038371007e038370707400046e6f6e65707371007e00fe707071007e01637071007e037e7070707070740006437031323532707070707071007e01677070707071007e016a0000c3540000000000000000707071007e011f7371007e00800000001c7571007e0083000000017371007e00850574000d6c6162656c2e70726f64756374707070707070707070707070707371007e01120000c354000000130001000000000000007b000000a8000000017371007e00d800000000ff33333370707071007e001071007e035c7371007e00d800000000ff000000707070707071007e014f707071007e00e07070707071007e00e97371007e00708623ed452732b0d5f339fff196f245be0000c354707070707074000953616e73536572696671007e01547071007e015671007e015971007e015971007e015971007e01597071007e01597070707371007e00ec707371007e00f00000c3547070707071007e039571007e039571007e0390707371007e00f70000c3547070707071007e039571007e0395707371007e00f10000c3547070707071007e039571007e0395707371007e00fa0000c3547070707071007e039571007e0395707371007e00fc0000c3547070707071007e039571007e039570707400046e6f6e65707371007e00fe707071007e01637071007e03907070707070740006437031323532707070707071007e01677070707071007e016a0000c3540000000000000000707071007e011f7371007e00800000001d7571007e0083000000017371007e0085057400136865616465722e62617463682e6e756d626572707070707070707070707070707371007e01120000c354000000130001000000000000007b00000124000000007371007e00d800000000ff33333370707071007e001071007e035c7371007e00d800000000ff000000707070707071007e014f707071007e00e07070707071007e00e97371007e0070b6c4133c9db85870607e2e6657b04dca0000c354707070707074000953616e73536572696671007e01547071007e015671007e015971007e015971007e015971007e01597071007e01597070707371007e00ec707371007e00f00000c3547070707071007e03a771007e03a771007e03a2707371007e00f70000c3547070707071007e03a771007e03a7707371007e00f10000c3547070707071007e03a771007e03a7707371007e00fa0000c3547070707071007e03a771007e03a7707371007e00fc0000c3547070707071007e03a771007e03a770707400046e6f6e65707371007e00fe707071007e01637071007e03a27070707070740006437031323532707070707071007e01677070707071007e016a0000c3540000000000000000707071007e011f7371007e00800000001e7571007e0083000000017371007e0085057400106c6162656c2e65787069727944617465707070707070707070707070707371007e01120000c35400000013000100000000000000740000019f000000007371007e00d800000000ff33333370707071007e001071007e035c7371007e00d800000000ff000000707070707071007e014f707071007e00e07070707071007e00e97371007e0070a862e3d96339d460990a518e4e724caa0000c354707070707074000953616e73536572696671007e01547071007e015671007e015971007e015971007e015971007e01597071007e01597070707371007e00ec707371007e00f00000c3547070707071007e03b971007e03b971007e03b4707371007e00f70000c3547070707071007e03b971007e03b9707371007e00f10000c3547070707071007e03b971007e03b9707371007e00fa0000c3547070707071007e03b971007e03b9707371007e00fc0000c3547070707071007e03b971007e03b970707400046e6f6e65707371007e00fe707071007e01637071007e03b47070707070740006437031323532707070707071007e01677070707071007e016a0000c3540000000000000000707071007e011f7371007e00800000001f7571007e0083000000017371007e0085057400166865616465722e646f7365732e726571756573746564707070707070707070707070707371007e01120000c354000000130001000000000000005800000213000000007371007e00d800000000ff33333370707071007e001071007e035c7371007e00d800000000ff000000707070707071007e014f707071007e00e07070707071007e00e97371007e0070babd6296d6ce3bb20e4e994883af4cb00000c354707070707074000953616e73536572696671007e01547071007e015671007e015971007e015971007e015971007e01597071007e01597070707371007e00ec707371007e00f00000c3547070707071007e03cb71007e03cb71007e03c6707371007e00f70000c3547070707071007e03cb71007e03cb707371007e00f10000c3547070707071007e03cb71007e03cb707371007e00fa0000c3547070707071007e03cb71007e03cb707371007e00fc0000c3547070707071007e03cb71007e03cb70707400046e6f6e65707371007e00fe707071007e01637071007e03c67070707070740006437031323532707070707071007e01677070707071007e016a0000c3540000000000000000707071007e011f7371007e0080000000207571007e0083000000017371007e0085057400136865616465722e646f7365732e6973737565647070707070707070707070707078700000c35400000014017070707074001847726f75702062792050726f6475637443617465676f72797400077265706f7274317571007e0025000000177371007e00270101707071007e0029707371007e002a70707071007e002e707371007e00270101707071007e0030707371007e002a70707071007e0032707371007e00270101707071007e0034707371007e002a70707071007e0036707371007e00270101707071007e0038707371007e002a70707071007e003a707371007e00270101707071007e003c707371007e002a70707071007e003e707371007e00270101707071007e0040707371007e002a70707071007e0042707371007e00270101707071007e0044707371007e002a70707071007e0046707371007e00270101707071007e0048707371007e002a70707071007e004a707371007e00270101707071007e004c707371007e002a70707071007e004e707371007e00270101707071007e0050707371007e002a70707071007e0052707371007e00270101707071007e0054707371007e002a70707071007e0056707371007e00270101707071007e0058707371007e002a70707071007e005a707371007e00270101707071007e005c707371007e002a70707071007e005e707371007e00270101707071007e0060707371007e002a70707071007e0062707371007e00270101707071007e0064707371007e002a70707071007e0066707371007e00270101707071007e0068707371007e002a70707071007e006a707371007e00270101707071007e006c707371007e002a70707071007e006e707371007e0027010170707400125245504f52545f5649525455414c495a4552707371007e002a7070707400296e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525669727475616c697a6572707371007e00270101707074001449535f49474e4f52455f504147494e4154494f4e707371007e002a7070707400116a6176612e6c616e672e426f6f6c65616e707371007e0027010070707400084f524445525f4944707371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e002700007371007e00800000000070707070740009696d6167655f646972707371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e00270100707074000d4f50455241544f525f4e414d45707371007e002a7070707400106a6176612e6c616e672e537472696e67707371007e00270100707074000849535355455f4944707371007e002a7070707400116a6176612e6c616e672e496e7465676572707371007e002a707371007e00c40000000377040000000374000c697265706f72742e7a6f6f6d740009697265706f72742e78740009697265706f72742e79787371007e03643f400000000000037708000000040000000371007e041a74000333373271007e0418740003312e3571007e041974000130787372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365517565727900000000000027d80200025b00066368756e6b7374002b5b4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f4a5251756572794368756e6b3b4c00086c616e677561676571007e000278707572002b5b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a5251756572794368756e6b3b409f00a1e8ba34a4020000787000000002737200316e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a524261736551756572794368756e6b00000000000027d8020004420004747970654c00047465787471007e00024c000e746f6b656e536570617261746f727400154c6a6176612f6c616e672f4368617261637465723b5b0006746f6b656e737400135b4c6a6176612f6c616e672f537472696e673b78700174031253454c4543542066322e6e616d6520746f466163696c6974794e616d652c6973737565446174652c6973737565566f75636865722c702e7072696d6172796e616d652070726f647563744e616d652c6c6f744e756d6265722062617463684e756d6265722c7175616e746974794f6e48616e642073746f636b4f6e48616e642c0a65787069726174696f6e446174652c646f736573526571756573746564207175616e746974795265717565737465642c7175616e746974792c6761702c20752e66697273744e616d652c6c6173744e616d652c70632e6e616d652070726f6475637443617465676f72790a46524f4d2073746f636b5f6d6f76656d656e747320736d0a494e4e4552204a4f494e2073746f636b5f6d6f76656d656e745f6c696e655f6974656d73206c69206f6e20736d2e6964203d206c692e73746f636b6d6f76656d656e7449640a696e6e6572206a6f696e2073746f636b5f6d6f76656d656e745f6c696e655f6974656d5f65787472615f6669656c6473206566206f6e206c692e6964203d2065662e73746f636b6d6f76656d656e746c696e656974656d69640a494e4e4552204a4f494e204c4f5453204c204f4e206c692e6c6f744964203d204c2e49440a494e4e4552204a4f494e20666163696c69746965732066206f6e20736d2e66726f6d466163696c6974794964203d20462e69640a696e6e6572206a6f696e20466163696c6974696573206632206f6e20736d2e746f466163696c6974794964203d2046322e69640a696e6e6572206a6f696e2070726f64756374732070206f6e2065662e70726f647563744964203d20702e69640a696e6e6572206a6f696e2055736572732075206f6e20736d2e637265617465646279203d20752e69640a4c454654204a4f494e2070726f6772616d5f70726f6475637473207070206f6e206c2e70726f647563744964203d2070702e70726f6475637449640a4c654654206a6f696e2070726f647563745f63617465676f72696573207063206f6e2070702e70726f6475637443617465676f72794964203d2070632e69640a574845524520736d2e6964203d2070707371007e04240274000849535355455f4944707074000373716c707070707371007e0070b3d554b1aefe96cea0a4e8610726422f7571007e0072000000097371007e0074000077ee0000010071007e007b707071007e007e70707371007e0080000000017571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283129707071007e00887071007e008a71007e003e707371007e0074000077ee0000010071007e007b707071007e007e70707371007e0080000000027571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283129707071007e00917071007e009271007e003e707371007e0074000077ee0000010071007e00957371007e0080000000037571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000047571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707071007e009f7071007e008a71007e003e707371007e0074000077ee0000010071007e00957371007e0080000000057571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000067571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707071007e00a97071007e009271007e003e707371007e0074000077ee0000010071007e00957371007e0080000000077571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e746567657228312970707071007e007e70707371007e0080000000087571007e0083000000017371007e0085017400186e6577206a6176612e6c616e672e496e7465676572283029707071007e00b37071007e00b471007e003e7071007e01bc71007e02b27371007e0074000077ee000000007e71007e007a7400074e4f5448494e477371007e00800000000d7571007e0083000000017371007e00850374000966697273744e616d6570707071007e007e70707074000a7646697273744e616d657071007e008a7400106a6176612e6c616e672e537472696e67707371007e0074000077ee0000000071007e04547371007e00800000000e7571007e0083000000017371007e0085037400086c6173744e616d6570707071007e007e707070740009764c6173744e616d657071007e008a7400106a6176612e6c616e672e537472696e677071007e00b771007e03d9707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4f7269656e746174696f6e456e756d00000000000000001200007871007e00127400094c414e445343415045707371007e00be7371007e00c4000000057704000000057372002c6e65742e73662e6a61737065727265706f7274732e656e67696e652e626173652e4a5242617365496d61676500000000000027d802002b49001950534555444f5f53455249414c5f56455253494f4e5f55494449000d626f6f6b6d61726b4c6576656c42000e6576616c756174696f6e54696d6542000f68797065726c696e6b54617267657442000d68797065726c696e6b547970655a000669734c617a7942000b6f6e4572726f72547970654c0014616e63686f724e616d6545787072657373696f6e71007e00194c0006626f7264657271007e00bf4c000b626f72646572436f6c6f7271007e00c84c000c626f74746f6d426f7264657271007e00bf4c0011626f74746f6d426f72646572436f6c6f7271007e00c84c000d626f74746f6d50616464696e6771007e00c94c000f6576616c756174696f6e47726f757071007e00764c00136576616c756174696f6e54696d6556616c756571007e01134c000a65787072657373696f6e71007e00194c0013686f72697a6f6e74616c416c69676e6d656e7471007e00bf4c0018686f72697a6f6e74616c416c69676e6d656e7456616c756571007e00ca4c001968797065726c696e6b416e63686f7245787072657373696f6e71007e00194c001768797065726c696e6b5061676545787072657373696f6e71007e00195b001368797065726c696e6b506172616d657465727371007e01144c001c68797065726c696e6b5265666572656e636545787072657373696f6e71007e00194c001a68797065726c696e6b546f6f6c74697045787072657373696f6e71007e00194c001768797065726c696e6b5768656e45787072657373696f6e71007e00194c000c69735573696e67436163686571007e00cb4c000a6c656674426f7264657271007e00bf4c000f6c656674426f72646572436f6c6f7271007e00c84c000b6c65667450616464696e6771007e00c94c00076c696e65426f7871007e00cc4c000a6c696e6b54617267657471007e00024c00086c696e6b5479706571007e00024c00106f6e4572726f725479706556616c75657400324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f747970652f4f6e4572726f7254797065456e756d3b4c000770616464696e6771007e00c94c000b7269676874426f7264657271007e00bf4c00107269676874426f72646572436f6c6f7271007e00c84c000c726967687450616464696e6771007e00c94c000a7363616c65496d61676571007e00bf4c000f7363616c65496d61676556616c756571007e021e4c0009746f70426f7264657271007e00bf4c000e746f70426f72646572436f6c6f7271007e00c84c000a746f7050616464696e6771007e00c94c0011766572746963616c416c69676e6d656e7471007e00bf4c0016766572746963616c416c69676e6d656e7456616c756571007e00d07871007e01040000c3540000003e000100000000000000500000000b000000027071007e001071007e046670707070707071007e00e07070707071007e00e97371007e0070a6c1f5d3ce283103595f07640a1942cc000077ee70707371007e00f20000c3547070707071007e046a700000c3540000000000000000007070707070707071007e011f7371007e00800000002a7571007e0083000000027371007e008502740009696d6167655f6469727371007e00850174000f2b20226c6f676f2d747a2e706e672270707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e047371007e047371007e046a707371007e00f70000c3547070707071007e047371007e0473707371007e00f10000c3547070707071007e047371007e0473707371007e00fa0000c3547070707071007e047371007e0473707371007e00fc0000c3547070707071007e047371007e047370707e7200306e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e4f6e4572726f7254797065456e756d00000000000000001200007871007e00127400054552524f5270707070707070707070707371007e04680000c354000000400001000000000000004a000002f2000000007071007e001071007e046670707070707071007e00e07070707071007e00e97371007e0070a20fd7916e3d3beac383c1bca48f40dd000077ee70707371007e00f20000c3547070707071007e047c700000c3540000000000000000007070707070707071007e011f7371007e00800000002b7571007e0083000000027371007e008502740009696d6167655f6469727371007e0085017400142b202276696d732d6c6f676f2d747a2e706e672270707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e048571007e048571007e047c707371007e00f70000c3547070707071007e048571007e0485707371007e00f10000c3547070707071007e048571007e0485707371007e00fa0000c3547070707071007e048571007e0485707371007e00fc0000c3547070707071007e048571007e0485707071007e047a70707070707070707070707371007e01120000c354000000150101000000000000028100000064000000167071007e001071007e04667371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e00709f84b67427c46920881bfb2f2c68420a0000c35470707070707071007e02027071007e020371007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e048e71007e048e71007e048b707371007e00f70000c3547070707071007e048e71007e048e707371007e00f10000c3547070707071007e048e71007e048e707371007e00fa0000c3547070707071007e048e71007e048e707371007e00fc0000c3547070707071007e048e71007e048e70707400046e6f6e65707371007e00fe707371007e015300000000707071007e048b7070707070707070707070707070707071007e02610000c3540000000000000001707071007e011f7371007e00800000002c7571007e0083000000017371007e00850274000d4f50455241544f525f4e414d45707070707070707070707070707371007e01120000c354000000140101000000000000028100000064000000027071007e001071007e04667371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070b74a450bb0260069150ee578dd774a5e0000c35470707070707071007e02027071007e020371007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e049e71007e049e71007e049b707371007e00f70000c3547070707071007e049e71007e049e707371007e00f10000c3547070707071007e049e71007e049e707371007e00fa0000c3547070707071007e049e71007e049e707371007e00fc0000c3547070707071007e049e71007e049e707070707371007e00fe7070707071007e049b7070707070707070707070707070707071007e016a0000c3540000000000000001707071007e011f7371007e00800000002d7571007e0083000000017371007e0085057400246c6162656c2e7469746c652e756e697465642e72657075626c69632e74616e7a616e6961707070707070707070707070707371007e01120000c3540000001501010000000000000281000000640000002b7071007e001071007e04667371007e00d800000000ff3d9297707070707070707071007e00e07070707071007e00e97371007e0070ac98448b9ffc09a0b7f47698146244d80000c35470707070707071007e02027071007e020371007e023170707070707070707371007e00ec707371007e00f00000c3547070707071007e04ac71007e04ac71007e04a9707371007e00f70000c3547070707071007e04ac71007e04ac707371007e00f10000c3547070707071007e04ac71007e04ac707371007e00fa0000c3547070707071007e04ac71007e04ac707371007e00fc0000c3547070707071007e04ac71007e04ac707070707371007e00fe7070707071007e04a97070707070707070707070707070707071007e016a0000c3540000000000000001707071007e011f7371007e00800000002e7571007e0083000000017371007e0085057400186c6162656c2e7469746c652e696d6d756e697a6174696f6e7070707070707070707070707078700000c35400000040017371007e0080000000297571007e0083000000027371007e00850474000b504147455f4e554d4245527371007e0085017400382e696e7456616c75652829203d3d20313f206e657720426f6f6c65616e287472756529203a206e657720426f6f6c65616e2866616c73652970707070707e72002f6e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5072696e744f72646572456e756d00000000000000001200007871007e0012740008564552544943414c757200265b4c6e65742e73662e6a61737065727265706f7274732e656e67696e652e4a525374796c653bd49cc311d905723502000078700000000c7371007e021c0000c35400707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04c371007e04c371007e04c2707371007e00f70000c3547070707071007e04c371007e04c3707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f80000071007e04c371007e04c3707371007e00fa0000c3547070707071007e04c371007e04c3707371007e00fc0000c3547070707071007e04c371007e04c37371007e00f20000c3547070707071007e04c270707070707400057461626c65707371007e00fe7070707071007e04c2707070707070707070707070707070707070707070707070707071007e021f7371007e021c0000c354007371007e00d800000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04d071007e04d071007e04ce707371007e00f70000c3547070707071007e04d071007e04d0707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e04d071007e04d0707371007e00fa0000c3547070707071007e04d071007e04d0707371007e00fc0000c3547070707071007e04d071007e04d07371007e00f20000c3547070707071007e04ce7070707071007e00dd7400087461626c655f4348707371007e00fe7070707071007e04ce70707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04dd71007e04dd71007e04db707371007e00f70000c3547070707071007e04dd71007e04dd707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e04dd71007e04dd707371007e00fa0000c3547070707071007e04dd71007e04dd707371007e00fc0000c3547070707071007e04dd71007e04dd7371007e00f20000c3547070707071007e04db7070707071007e00dd7400087461626c655f5444707371007e00fe7070707071007e04db70707070707070707070707070707070707070707070707070707371007e021c0000c35400707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04e971007e04e971007e04e8707371007e00f70000c3547070707071007e04e971007e04e9707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f80000071007e04e971007e04e9707371007e00fa0000c3547070707071007e04e971007e04e9707371007e00fc0000c3547070707071007e04e971007e04e97371007e00f20000c3547070707071007e04e870707070707400077461626c652031707371007e00fe7070707071007e04e870707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e04f671007e04f671007e04f4707371007e00f70000c3547070707071007e04f671007e04f6707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e04f671007e04f6707371007e00fa0000c3547070707071007e04f671007e04f6707371007e00fc0000c3547070707071007e04f671007e04f67371007e00f20000c3547070707071007e04f47070707071007e00dd74000a7461626c6520315f5448707371007e00fe7070707071007e04f470707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e050371007e050371007e0501707371007e00f70000c3547070707071007e050371007e0503707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e050371007e0503707371007e00fa0000c3547070707071007e050371007e0503707371007e00fc0000c3547070707071007e050371007e05037371007e00f20000c3547070707071007e05017070707071007e00dd74000a7461626c6520315f4348707371007e00fe7070707071007e050170707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e051071007e051071007e050e707371007e00f70000c3547070707071007e051071007e0510707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e051071007e0510707371007e00fa0000c3547070707071007e051071007e0510707371007e00fc0000c3547070707071007e051071007e05107371007e00f20000c3547070707071007e050e7070707071007e00dd74000a7461626c6520315f5444707371007e00fe7070707071007e050e70707070707070707070707070707070707070707070707070707371007e021c0000c35400707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e051c71007e051c71007e051b707371007e00f70000c3547070707071007e051c71007e051c707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f80000071007e051c71007e051c707371007e00fa0000c3547070707071007e051c71007e051c707371007e00fc0000c3547070707071007e051c71007e051c7371007e00f20000c3547070707071007e051b70707070707400077461626c652032707371007e00fe7070707071007e051b70707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000fff0f8ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e052971007e052971007e0527707371007e00f70000c3547070707071007e052971007e0529707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e052971007e0529707371007e00fa0000c3547070707071007e052971007e0529707371007e00fc0000c3547070707071007e052971007e05297371007e00f20000c3547070707071007e05277070707071007e00dd74000a7461626c6520325f5448707371007e00fe7070707071007e052770707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000ffbfe1ff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e053671007e053671007e0534707371007e00f70000c3547070707071007e053671007e0536707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e053671007e0536707371007e00fa0000c3547070707071007e053671007e0536707371007e00fc0000c3547070707071007e053671007e05367371007e00f20000c3547070707071007e05347070707071007e00dd74000a7461626c6520325f4348707371007e00fe7070707071007e053470707070707070707070707070707070707070707070707070707371007e021c0000c354007371007e00d800000000ffffffff7070707070707070707070707070707070707070707070707070707371007e00ec707371007e00f00000c3547070707071007e054371007e054371007e0541707371007e00f70000c3547070707071007e054371007e0543707371007e00f10000c3547371007e00d800000000ff00000070707070707371007e010c3f00000071007e054371007e0543707371007e00fa0000c3547070707071007e054371007e0543707371007e00fc0000c3547070707071007e054371007e05437371007e00f20000c3547070707071007e05417070707071007e00dd74000a7461626c6520325f5444707371007e00fe7070707071007e0541707070707070707070707070707070707070707070707070707070707371007e00be7371007e00c40000000077040000000078700000c3540000003201707070707e7200336e65742e73662e6a61737065727265706f7274732e656e67696e652e747970652e5768656e4e6f4461746154797065456e756d00000000000000001200007871007e001274000f4e4f5f444154415f53454354494f4e737200366e65742e73662e6a61737065727265706f7274732e656e67696e652e64657369676e2e4a525265706f7274436f6d70696c654461746100000000000027d80200034c001363726f7373746162436f6d70696c654461746171007e002c4c001264617461736574436f6d70696c654461746171007e002c4c00166d61696e44617461736574436f6d70696c654461746171007e000178707371007e03643f4000000000000077080000001000000000787371007e03643f4000000000000c7708000000100000000171007e0024757200025b42acf317f8060854e0020000787000000f84cafebabe0000002e009e01002e7265706f7274315f5461626c653332446174617365743332315f313434343635363531303730345f32393837313007000101002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a524576616c7561746f7207000301001b706172616d657465725f5245504f52545f434f4e4e454354494f4e0100324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465723b010010706172616d657465725f46494c544552010017706172616d657465725f4a41535045525f5245504f5254010017706172616d657465725f5245504f52545f4c4f43414c4501001a706172616d657465725f5245504f52545f54494d455f5a4f4e4501001a706172616d657465725f5245504f52545f54454d504c4154455301001a706172616d657465725f5245504f52545f4d41585f434f554e5401001a706172616d657465725f5245504f52545f5343524950544c455401001e706172616d657465725f5245504f52545f46494c455f5245534f4c56455201001f706172616d657465725f5245504f52545f464f524d41545f464143544f525901001f706172616d657465725f5245504f52545f504152414d45544552535f4d4150010020706172616d657465725f5245504f52545f5245534f555243455f42554e444c4501001c706172616d657465725f5245504f52545f444154415f534f55524345010018706172616d657465725f5245504f52545f434f4e5445585401001d706172616d657465725f5245504f52545f434c4153535f4c4f41444552010024706172616d657465725f5245504f52545f55524c5f48414e444c45525f464143544f5259010015706172616d657465725f534f52545f4649454c44530100147661726961626c655f504147455f4e554d4245520100314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c653b0100167661726961626c655f434f4c554d4e5f4e554d4245520100157661726961626c655f5245504f52545f434f554e540100137661726961626c655f504147455f434f554e540100157661726961626c655f434f4c554d4e5f434f554e540100063c696e69743e010003282956010004436f64650c001d001e0a000400200c0005000609000200220c0007000609000200240c0008000609000200260c0009000609000200280c000a0006090002002a0c000b0006090002002c0c000c0006090002002e0c000d000609000200300c000e000609000200320c000f000609000200340c0010000609000200360c0011000609000200380c00120006090002003a0c00130006090002003c0c00140006090002003e0c0015000609000200400c0016000609000200420c0017001809000200440c0019001809000200460c001a001809000200480c001b0018090002004a0c001c0018090002004c01000f4c696e654e756d6265725461626c6501000e637573746f6d697a6564496e6974010030284c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b295601000a696e6974506172616d73010012284c6a6176612f7574696c2f4d61703b29560c005100520a0002005301000a696e69744669656c64730c005500520a00020056010008696e6974566172730c005800520a000200590100115245504f52545f434f4e4e454354494f4e08005b01000d6a6176612f7574696c2f4d617007005d010003676574010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f4f626a6563743b0c005f00600b005e00610100306e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d6574657207006301000646494c54455208006501000d4a41535045525f5245504f525408006701000d5245504f52545f4c4f43414c450800690100105245504f52545f54494d455f5a4f4e4508006b0100105245504f52545f54454d504c4154455308006d0100105245504f52545f4d41585f434f554e5408006f0100105245504f52545f5343524950544c45540800710100145245504f52545f46494c455f5245534f4c5645520800730100155245504f52545f464f524d41545f464143544f52590800750100155245504f52545f504152414d45544552535f4d41500800770100165245504f52545f5245534f555243455f42554e444c450800790100125245504f52545f444154415f534f5552434508007b01000e5245504f52545f434f4e5445585408007d0100135245504f52545f434c4153535f4c4f4144455208007f01001a5245504f52545f55524c5f48414e444c45525f464143544f525908008101000b534f52545f4649454c445308008301000b504147455f4e554d42455208008501002f6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c6507008701000d434f4c554d4e5f4e554d42455208008901000c5245504f52545f434f554e5408008b01000a504147455f434f554e5408008d01000c434f4c554d4e5f434f554e5408008f0100086576616c756174650100152849294c6a6176612f6c616e672f4f626a6563743b01000a457863657074696f6e730100136a6176612f6c616e672f5468726f7761626c650700940100116a6176612f6c616e672f496e7465676572070096010004284929560c001d00980a0097009901000b6576616c756174654f6c640100116576616c75617465457374696d6174656401000a536f7572636546696c650021000200040000001600020005000600000002000700060000000200080006000000020009000600000002000a000600000002000b000600000002000c000600000002000d000600000002000e000600000002000f000600000002001000060000000200110006000000020012000600000002001300060000000200140006000000020015000600000002001600060000000200170018000000020019001800000002001a001800000002001b001800000002001c0018000000080001001d001e0001001f000000e700020001000000732ab700212a01b500232a01b500252a01b500272a01b500292a01b5002b2a01b5002d2a01b5002f2a01b500312a01b500332a01b500352a01b500372a01b500392a01b5003b2a01b5003d2a01b5003f2a01b500412a01b500432a01b500452a01b500472a01b500492a01b5004b2a01b5004db100000001004e00000062001800000012000400190009001a000e001b0013001c0018001d001d001e0022001f00270020002c00210031002200360023003b00240040002500450026004a0027004f0028005400290059002a005e002b0063002c0068002d006d002e007200120001004f00500001001f0000003400020004000000102a2bb700542a2cb700572a2db7005ab100000001004e0000001200040000003a0005003b000a003c000f003d0002005100520001001f0000018f00030002000001332a2b125cb900620200c00064c00064b500232a2b1266b900620200c00064c00064b500252a2b1268b900620200c00064c00064b500272a2b126ab900620200c00064c00064b500292a2b126cb900620200c00064c00064b5002b2a2b126eb900620200c00064c00064b5002d2a2b1270b900620200c00064c00064b5002f2a2b1272b900620200c00064c00064b500312a2b1274b900620200c00064c00064b500332a2b1276b900620200c00064c00064b500352a2b1278b900620200c00064c00064b500372a2b127ab900620200c00064c00064b500392a2b127cb900620200c00064c00064b5003b2a2b127eb900620200c00064c00064b5003d2a2b1280b900620200c00064c00064b5003f2a2b1282b900620200c00064c00064b500412a2b1284b900620200c00064c00064b50043b100000001004e0000004a00120000004500120046002400470036004800480049005a004a006c004b007e004c0090004d00a2004e00b4004f00c6005000d8005100ea005200fc0053010e005401200055013200560002005500520001001f000000190000000200000001b100000001004e0000000600010000005e0002005800520001001f00000087000300020000005b2a2b1286b900620200c00088c00088b500452a2b128ab900620200c00088c00088b500472a2b128cb900620200c00088c00088b500492a2b128eb900620200c00088c00088b5004b2a2b1290b900620200c00088c00088b5004db100000001004e0000001a0006000000660012006700240068003600690048006a005a006b000100910092000200930000000400010095001f000000eb000300030000008f014d1baa0000008a00000000000000070000002d0000003900000045000000510000005d000000690000007500000081bb00975904b7009a4da70054bb00975904b7009a4da70048bb00975904b7009a4da7003cbb00975903b7009a4da70030bb00975904b7009a4da70024bb00975903b7009a4da70018bb00975904b7009a4da7000cbb00975903b7009a4d2cb000000001004e0000004a00120000007300020075003000790039007a003c007e0045007f004800830051008400540088005d00890060008d0069008e006c00920075009300780097008100980084009c008d00a40001009b0092000200930000000400010095001f000000eb000300030000008f014d1baa0000008a00000000000000070000002d0000003900000045000000510000005d000000690000007500000081bb00975904b7009a4da70054bb00975904b7009a4da70048bb00975904b7009a4da7003cbb00975903b7009a4da70030bb00975904b7009a4da70024bb00975903b7009a4da70018bb00975904b7009a4da7000cbb00975903b7009a4d2cb000000001004e0000004a0012000000ad000200af003000b3003900b4003c00b8004500b9004800bd005100be005400c2005d00c3006000c7006900c8006c00cc007500cd007800d1008100d2008400d6008d00de0001009c0092000200930000000400010095001f000000eb000300030000008f014d1baa0000008a00000000000000070000002d0000003900000045000000510000005d000000690000007500000081bb00975904b7009a4da70054bb00975904b7009a4da70048bb00975904b7009a4da7003cbb00975903b7009a4da70030bb00975904b7009a4da70024bb00975903b7009a4da70018bb00975904b7009a4da7000cbb00975903b7009a4d2cb000000001004e0000004a0012000000e7000200e9003000ed003900ee003c00f2004500f3004800f7005100f8005400fc005d00fd0060010100690102006c0106007501070078010b0081010c00840110008d01180001009d000000020001787571007e055700002c29cafebabe0000002e017501001c7265706f7274315f313434343635363531303730345f32393837313007000101002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a524576616c7561746f7207000301001e706172616d657465725f49535f49474e4f52455f504147494e4154494f4e0100324c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465723b01001b706172616d657465725f5245504f52545f434f4e4e454354494f4e010010706172616d657465725f46494c544552010013706172616d657465725f696d6167655f646972010017706172616d657465725f4a41535045525f5245504f5254010017706172616d657465725f5245504f52545f4c4f43414c4501001a706172616d657465725f5245504f52545f54494d455f5a4f4e4501001a706172616d657465725f5245504f52545f54454d504c41544553010012706172616d657465725f49535355455f494401001a706172616d657465725f5245504f52545f4d41585f434f554e5401001a706172616d657465725f5245504f52545f5343524950544c455401001e706172616d657465725f5245504f52545f46494c455f5245534f4c564552010012706172616d657465725f4f524445525f494401001f706172616d657465725f5245504f52545f464f524d41545f464143544f525901001f706172616d657465725f5245504f52545f504152414d45544552535f4d4150010020706172616d657465725f5245504f52545f5245534f555243455f42554e444c4501001c706172616d657465725f5245504f52545f444154415f534f55524345010018706172616d657465725f5245504f52545f434f4e5445585401001d706172616d657465725f5245504f52545f434c4153535f4c4f41444552010024706172616d657465725f5245504f52545f55524c5f48414e444c45525f464143544f525901001c706172616d657465725f5245504f52545f5649525455414c495a4552010015706172616d657465725f534f52545f4649454c4453010017706172616d657465725f4f50455241544f525f4e414d450100126669656c645f6973737565566f756368657201002e4c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c4669656c643b01000e6669656c645f6c6173744e616d6501000e6669656c645f7175616e746974790100146669656c645f746f466163696c6974794e616d650100116669656c645f70726f647563744e616d650100156669656c645f70726f6475637443617465676f727901000f6669656c645f66697273744e616d650100176669656c645f7175616e746974795265717565737465640100096669656c645f6761700100116669656c645f73746f636b4f6e48616e6401000f6669656c645f6973737565446174650100116669656c645f62617463684e756d6265720100146669656c645f65787069726174696f6e446174650100147661726961626c655f504147455f4e554d4245520100314c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c653b0100167661726961626c655f434f4c554d4e5f4e554d4245520100157661726961626c655f5245504f52545f434f554e540100137661726961626c655f504147455f434f554e540100157661726961626c655f434f4c554d4e5f434f554e540100157661726961626c655f4865616465725f434f554e540100297661726961626c655f47726f757033326279333250726f6475637443617465676f72795f434f554e540100137661726961626c655f7646697273744e616d650100127661726961626c655f764c6173744e616d650100063c696e69743e010003282956010004436f64650c003500360a000400380c00050006090002003a0c00070006090002003c0c00080006090002003e0c0009000609000200400c000a000609000200420c000b000609000200440c000c000609000200460c000d000609000200480c000e0006090002004a0c000f0006090002004c0c00100006090002004e0c0011000609000200500c0012000609000200520c0013000609000200540c0014000609000200560c0015000609000200580c00160006090002005a0c00170006090002005c0c00180006090002005e0c0019000609000200600c001a000609000200620c001b000609000200640c001c000609000200660c001d001e09000200680c001f001e090002006a0c0020001e090002006c0c0021001e090002006e0c0022001e09000200700c0023001e09000200720c0024001e09000200740c0025001e09000200760c0026001e09000200780c0027001e090002007a0c0028001e090002007c0c0029001e090002007e0c002a001e09000200800c002b002c09000200820c002d002c09000200840c002e002c09000200860c002f002c09000200880c0030002c090002008a0c0031002c090002008c0c0032002c090002008e0c0033002c09000200900c0034002c090002009201000f4c696e654e756d6265725461626c6501000e637573746f6d697a6564496e6974010030284c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b4c6a6176612f7574696c2f4d61703b295601000a696e6974506172616d73010012284c6a6176612f7574696c2f4d61703b29560c009700980a0002009901000a696e69744669656c64730c009b00980a0002009c010008696e6974566172730c009e00980a0002009f01001449535f49474e4f52455f504147494e4154494f4e0800a101000d6a6176612f7574696c2f4d61700700a3010003676574010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f4f626a6563743b0c00a500a60b00a400a70100306e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c506172616d657465720700a90100115245504f52545f434f4e4e454354494f4e0800ab01000646494c5445520800ad010009696d6167655f6469720800af01000d4a41535045525f5245504f52540800b101000d5245504f52545f4c4f43414c450800b30100105245504f52545f54494d455f5a4f4e450800b50100105245504f52545f54454d504c415445530800b701000849535355455f49440800b90100105245504f52545f4d41585f434f554e540800bb0100105245504f52545f5343524950544c45540800bd0100145245504f52545f46494c455f5245534f4c5645520800bf0100084f524445525f49440800c10100155245504f52545f464f524d41545f464143544f52590800c30100155245504f52545f504152414d45544552535f4d41500800c50100165245504f52545f5245534f555243455f42554e444c450800c70100125245504f52545f444154415f534f555243450800c901000e5245504f52545f434f4e544558540800cb0100135245504f52545f434c4153535f4c4f414445520800cd01001a5245504f52545f55524c5f48414e444c45525f464143544f52590800cf0100125245504f52545f5649525455414c495a45520800d101000b534f52545f4649454c44530800d301000d4f50455241544f525f4e414d450800d501000c6973737565566f75636865720800d701002c6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c4669656c640700d90100086c6173744e616d650800db0100087175616e746974790800dd01000e746f466163696c6974794e616d650800df01000b70726f647563744e616d650800e101000f70726f6475637443617465676f72790800e301000966697273744e616d650800e50100117175616e746974795265717565737465640800e70100036761700800e901000b73746f636b4f6e48616e640800eb0100096973737565446174650800ed01000b62617463684e756d6265720800ef01000e65787069726174696f6e446174650800f101000b504147455f4e554d4245520800f301002f6e65742f73662f6a61737065727265706f7274732f656e67696e652f66696c6c2f4a5246696c6c5661726961626c650700f501000d434f4c554d4e5f4e554d4245520800f701000c5245504f52545f434f554e540800f901000a504147455f434f554e540800fb01000c434f4c554d4e5f434f554e540800fd01000c4865616465725f434f554e540800ff01001e47726f75702062792050726f6475637443617465676f72795f434f554e5408010101000a7646697273744e616d65080103010009764c6173744e616d650801050100086576616c756174650100152849294c6a6176612f6c616e672f4f626a6563743b01000a457863657074696f6e730100136a6176612f6c616e672f5468726f7761626c6507010a0100116a6176612f6c616e672f496e746567657207010c010004284929560c0035010e0a010d010f01000867657456616c756501001428294c6a6176612f6c616e672f4f626a6563743b0c011101120a00da01130100106a6176612f6c616e672f537472696e670701150a00f60113010008696e7456616c75650100032829490c011801190a010d011a0100116a6176612f6c616e672f426f6f6c65616e07011c010004285a29560c0035011e0a011d011f01001a6c6162656c2e76616363696e652e646973747269627574696f6e080121010003737472010026284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e673b0c012301240a000201250100226c6162656c2e76616363696e652e73746f636b2e64697374726962757465642e746f0801270100186c6162656c2e76616363696e652e69737375652e646174650801290100216c6162656c2e76616363696e652e73746f636b2e69737375652e766f756368657208012b0100166a6176612f6c616e672f537472696e6742756666657207012d0100012008012f010015284c6a6176612f6c616e672f537472696e673b29560c003501310a012e0132010006617070656e6401002c284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e674275666665723b0c013401350a012e0136010008746f537472696e6701001428294c6a6176612f6c616e672f537472696e673b0c013801390a012e013a01000d6c6162656c2e706167652e6f6608013c0100036d7367010038284c6a6176612f6c616e672f537472696e673b4c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e673b0c013e013f0a0002014001000d6c6162656c2e6c696e652e6e6f08014201000d6c6162656c2e70726f647563740801440100136865616465722e62617463682e6e756d6265720801460100106c6162656c2e657870697279446174650801480100166865616465722e646f7365732e72657175657374656408014a0100136865616465722e646f7365732e69737375656408014c0100156c6162656c2e69737375696e672e6f66666963657208014e01000776616c75654f66010026284c6a6176612f6c616e672f4f626a6563743b294c6a6176612f6c616e672f537472696e673b0c015001510a0116015201002c284c6a6176612f6c616e672f537472696e673b294c6a6176612f6c616e672f537472696e674275666665723b0c013401540a012e015501000f6c6162656c2e7369676e61747572650801570100106c6162656c2e64657369676e617465640801590100176c6162656c2e726563656976696e672e6f66666963657208015b01000a6c6162656c2e6461746508015d0a00aa011301000b6c6f676f2d747a2e706e6708016001001076696d732d6c6f676f2d747a2e706e670801620100246c6162656c2e7469746c652e756e697465642e72657075626c69632e74616e7a616e69610801640100186c6162656c2e7469746c652e696d6d756e697a6174696f6e080166010016285a294c6a6176612f6c616e672f426f6f6c65616e3b0c015001680a011d016901000b6576616c756174654f6c6401000b6765744f6c6456616c75650c016c01120a00da016d0a00f6016d0100116576616c75617465457374696d61746564010011676574457374696d6174656456616c75650c017101120a00f6017201000a536f7572636546696c650021000200040000002d00020005000600000002000700060000000200080006000000020009000600000002000a000600000002000b000600000002000c000600000002000d000600000002000e000600000002000f0006000000020010000600000002001100060000000200120006000000020013000600000002001400060000000200150006000000020016000600000002001700060000000200180006000000020019000600000002001a000600000002001b000600000002001c000600000002001d001e00000002001f001e000000020020001e000000020021001e000000020022001e000000020023001e000000020024001e000000020025001e000000020026001e000000020027001e000000020028001e000000020029001e00000002002a001e00000002002b002c00000002002d002c00000002002e002c00000002002f002c000000020030002c000000020031002c000000020032002c000000020033002c000000020034002c0000000800010035003600010037000001b600020001000000e62ab700392a01b5003b2a01b5003d2a01b5003f2a01b500412a01b500432a01b500452a01b500472a01b500492a01b5004b2a01b5004d2a01b5004f2a01b500512a01b500532a01b500552a01b500572a01b500592a01b5005b2a01b5005d2a01b5005f2a01b500612a01b500632a01b500652a01b500672a01b500692a01b5006b2a01b5006d2a01b5006f2a01b500712a01b500732a01b500752a01b500772a01b500792a01b5007b2a01b5007d2a01b5007f2a01b500812a01b500832a01b500852a01b500872a01b500892a01b5008b2a01b5008d2a01b5008f2a01b500912a01b50093b1000000010094000000be002f00000012000400190009001a000e001b0013001c0018001d001d001e0022001f00270020002c00210031002200360023003b00240040002500450026004a0027004f0028005400290059002a005e002b0063002c0068002d006d002e0072002f00770030007c00310081003200860033008b00340090003500950036009a0037009f003800a4003900a9003a00ae003b00b3003c00b8003d00bd003e00c2003f00c7004000cc004100d1004200d6004300db004400e0004500e50012000100950096000100370000003400020004000000102a2bb7009a2a2cb7009d2a2db700a0b10000000100940000001200040000005100050052000a0053000f00540002009700980001003700000213000300020000019f2a2b12a2b900a80200c000aac000aab5003b2a2b12acb900a80200c000aac000aab5003d2a2b12aeb900a80200c000aac000aab5003f2a2b12b0b900a80200c000aac000aab500412a2b12b2b900a80200c000aac000aab500432a2b12b4b900a80200c000aac000aab500452a2b12b6b900a80200c000aac000aab500472a2b12b8b900a80200c000aac000aab500492a2b12bab900a80200c000aac000aab5004b2a2b12bcb900a80200c000aac000aab5004d2a2b12beb900a80200c000aac000aab5004f2a2b12c0b900a80200c000aac000aab500512a2b12c2b900a80200c000aac000aab500532a2b12c4b900a80200c000aac000aab500552a2b12c6b900a80200c000aac000aab500572a2b12c8b900a80200c000aac000aab500592a2b12cab900a80200c000aac000aab5005b2a2b12ccb900a80200c000aac000aab5005d2a2b12ceb900a80200c000aac000aab5005f2a2b12d0b900a80200c000aac000aab500612a2b12d2b900a80200c000aac000aab500632a2b12d4b900a80200c000aac000aab500652a2b12d6b900a80200c000aac000aab50067b10000000100940000006200180000005c0012005d0024005e0036005f00480060005a0061006c0062007e00630090006400a2006500b4006600c6006700d8006800ea006900fc006a010e006b0120006c0132006d0144006e0156006f01680070017a0071018c0072019e00730002009b0098000100370000013700030002000000eb2a2b12d8b900a80200c000dac000dab500692a2b12dcb900a80200c000dac000dab5006b2a2b12deb900a80200c000dac000dab5006d2a2b12e0b900a80200c000dac000dab5006f2a2b12e2b900a80200c000dac000dab500712a2b12e4b900a80200c000dac000dab500732a2b12e6b900a80200c000dac000dab500752a2b12e8b900a80200c000dac000dab500772a2b12eab900a80200c000dac000dab500792a2b12ecb900a80200c000dac000dab5007b2a2b12eeb900a80200c000dac000dab5007d2a2b12f0b900a80200c000dac000dab5007f2a2b12f2b900a80200c000dac000dab50081b10000000100940000003a000e0000007b0012007c0024007d0036007e0048007f005a0080006c0081007e00820090008300a2008400b4008500c6008600d8008700ea00880002009e009800010037000000e300030002000000a72a2b12f4b900a80200c000f6c000f6b500832a2b12f8b900a80200c000f6c000f6b500852a2b12fab900a80200c000f6c000f6b500872a2b12fcb900a80200c000f6c000f6b500892a2b12feb900a80200c000f6c000f6b5008b2a2b130100b900a80200c000f6c000f6b5008d2a2b130102b900a80200c000f6c000f6b5008f2a2b130104b900a80200c000f6c000f6b500912a2b130106b900a80200c000f6c000f6b50093b10000000100940000002a000a0000009000120091002400920036009300480094005a0095006d0096008000970093009800a6009900010107010800020109000000040001010b00370000061d0003000300000449014d1baa000004440000000000000036000000e9000000ee000000fa00000106000001120000011e0000012a00000136000001420000014e0000015a00000166000001720000017e0000018c0000019a000001c2000001cd000001f5000002000000020b000002160000022400000232000002400000025e0000027700000285000002900000029b000002a6000002b1000002bc000002c7000002d2000003000000030b00000316000003210000032c00000337000003420000036a0000038b000003ac000003ba000003c5000003d0000003ee000003f3000004010000040f0000041d0000042b00000439014da70359bb010d5904b701104da7034dbb010d5904b701104da70341bb010d5904b701104da70335bb010d5903b701104da70329bb010d5904b701104da7031dbb010d5903b701104da70311bb010d5904b701104da70305bb010d5903b701104da702f9bb010d5904b701104da702edbb010d5903b701104da702e1bb010d5904b701104da702d5bb010d5903b701104da702c92ab40075b60114c001164da702bb2ab4006bb60114c001164da702ad2ab40083b60117c0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da702852a130122b601264da7027a2ab40083b60117c0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da702522a130128b601264da702472a13012ab601264da7023c2a13012cb601264da702312ab4006fb60114c001164da702232ab4007db60114c001164da702152ab40069b60114c001164da70207bb012e59130130b701332ab40083b60117c0010db60137b6013b4da701e92a2a13013db601262ab40083b60117c0010db601414da701d02ab40073b60114c001164da701c22a130143b601264da701b72a130145b601264da701ac2a130147b601264da701a12a130149b601264da701962a13014bb601264da7018b2a13014db601264da701802a13014fb601264da70175bb012e592ab40075b60114c00116b80153b70133130130b601562ab4006bb60114c00116b60156b6013b4da701472a130158b601264da7013c2a13015ab601264da701312a13015cb601264da701262a130158b601264da7011b2a13015eb601264da701102a13015ab601264da701052ab40083b60117c0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da700ddbb012e592ab40041b6015fc00116b80153b70133130161b60156b6013b4da700bcbb012e592ab40041b6015fc00116b80153b70133130163b60156b6013b4da7009b2ab40067b6015fc001164da7008d2a130165b601264da700822a130167b601264da700772ab40087b60117c0010db6011b05709a000704a7000403b8016a4da70059014da700542ab40071b60114c001164da700462ab4007fb60114c001164da700382ab40081b60114c001164da7002a2ab40077b60114c0010d4da7001c2ab40087b60117c0010d4da7000e2ab4006db60114c0010d4d2cb0000000010094000001c20070000000a1000200a300ec00a700ee00a800f100ac00fa00ad00fd00b1010600b2010900b6011200b7011500bb011e00bc012100c0012a00c1012d00c5013600c6013900ca014200cb014500cf014e00d0015100d4015a00d5015d00d9016600da016900de017200df017500e3017e00e4018100e8018c00e9018f00ed019a00ee019d00f201c200f301c500f701cd00f801d000fc01f500fd01f801010200010202030106020b0107020e010b0216010c021901100224011102270115023201160235011a0240011b0243011f025e01200261012402770125027a01290285012a0288012e0290012f02930133029b0134029e013802a6013902a9013d02b1013e02b4014202bc014302bf014702c7014802ca014c02d2014d02d501510300015203030156030b0157030e015b0316015c031901600321016103240165032c0166032f016a0337016b033a016f0342017003450174036a0175036d0179038b017a038e017e03ac017f03af018303ba018403bd018803c5018903c8018d03d0018e03d3019203ee019303f1019703f3019803f6019c0401019d040401a1040f01a2041201a6041d01a7042001ab042b01ac042e01b0043901b1043c01b5044701bd0001016b010800020109000000040001010b00370000061d0003000300000449014d1baa000004440000000000000036000000e9000000ee000000fa00000106000001120000011e0000012a00000136000001420000014e0000015a00000166000001720000017e0000018c0000019a000001c2000001cd000001f5000002000000020b000002160000022400000232000002400000025e0000027700000285000002900000029b000002a6000002b1000002bc000002c7000002d2000003000000030b00000316000003210000032c00000337000003420000036a0000038b000003ac000003ba000003c5000003d0000003ee000003f3000004010000040f0000041d0000042b00000439014da70359bb010d5904b701104da7034dbb010d5904b701104da70341bb010d5904b701104da70335bb010d5903b701104da70329bb010d5904b701104da7031dbb010d5903b701104da70311bb010d5904b701104da70305bb010d5903b701104da702f9bb010d5904b701104da702edbb010d5903b701104da702e1bb010d5904b701104da702d5bb010d5903b701104da702c92ab40075b6016ec001164da702bb2ab4006bb6016ec001164da702ad2ab40083b6016fc0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da702852a130122b601264da7027a2ab40083b6016fc0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da702522a130128b601264da702472a13012ab601264da7023c2a13012cb601264da702312ab4006fb6016ec001164da702232ab4007db6016ec001164da702152ab40069b6016ec001164da70207bb012e59130130b701332ab40083b6016fc0010db60137b6013b4da701e92a2a13013db601262ab40083b6016fc0010db601414da701d02ab40073b6016ec001164da701c22a130143b601264da701b72a130145b601264da701ac2a130147b601264da701a12a130149b601264da701962a13014bb601264da7018b2a13014db601264da701802a13014fb601264da70175bb012e592ab40075b6016ec00116b80153b70133130130b601562ab4006bb6016ec00116b60156b6013b4da701472a130158b601264da7013c2a13015ab601264da701312a13015cb601264da701262a130158b601264da7011b2a13015eb601264da701102a13015ab601264da701052ab40083b6016fc0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da700ddbb012e592ab40041b6015fc00116b80153b70133130161b60156b6013b4da700bcbb012e592ab40041b6015fc00116b80153b70133130163b60156b6013b4da7009b2ab40067b6015fc001164da7008d2a130165b601264da700822a130167b601264da700772ab40087b6016fc0010db6011b05709a000704a7000403b8016a4da70059014da700542ab40071b6016ec001164da700462ab4007fb6016ec001164da700382ab40081b6016ec001164da7002a2ab40077b6016ec0010d4da7001c2ab40087b6016fc0010d4da7000e2ab4006db6016ec0010d4d2cb0000000010094000001c20070000001c6000201c800ec01cc00ee01cd00f101d100fa01d200fd01d6010601d7010901db011201dc011501e0011e01e1012101e5012a01e6012d01ea013601eb013901ef014201f0014501f4014e01f5015101f9015a01fa015d01fe016601ff016902030172020401750208017e02090181020d018c020e018f0212019a0213019d021701c2021801c5021c01cd021d01d0022101f5022201f80226020002270203022b020b022c020e02300216023102190235022402360227023a0232023b0235023f0240024002430244025e0245026102490277024a027a024e0285024f028802530290025402930258029b0259029e025d02a6025e02a9026202b1026302b4026702bc026802bf026c02c7026d02ca027102d2027202d50276030002770303027b030b027c030e02800316028103190285032102860324028a032c028b032f028f03370290033a02940342029503450299036a029a036d029e038b029f038e02a303ac02a403af02a803ba02a903bd02ad03c502ae03c802b203d002b303d302b703ee02b803f102bc03f302bd03f602c1040102c2040402c6040f02c7041202cb041d02cc042002d0042b02d1042e02d5043902d6043c02da044702e200010170010800020109000000040001010b00370000061d0003000300000449014d1baa000004440000000000000036000000e9000000ee000000fa00000106000001120000011e0000012a00000136000001420000014e0000015a00000166000001720000017e0000018c0000019a000001c2000001cd000001f5000002000000020b000002160000022400000232000002400000025e0000027700000285000002900000029b000002a6000002b1000002bc000002c7000002d2000003000000030b00000316000003210000032c00000337000003420000036a0000038b000003ac000003ba000003c5000003d0000003ee000003f3000004010000040f0000041d0000042b00000439014da70359bb010d5904b701104da7034dbb010d5904b701104da70341bb010d5904b701104da70335bb010d5903b701104da70329bb010d5904b701104da7031dbb010d5903b701104da70311bb010d5904b701104da70305bb010d5903b701104da702f9bb010d5904b701104da702edbb010d5903b701104da702e1bb010d5904b701104da702d5bb010d5903b701104da702c92ab40075b60114c001164da702bb2ab4006bb60114c001164da702ad2ab40083b60173c0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da702852a130122b601264da7027a2ab40083b60173c0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da702522a130128b601264da702472a13012ab601264da7023c2a13012cb601264da702312ab4006fb60114c001164da702232ab4007db60114c001164da702152ab40069b60114c001164da70207bb012e59130130b701332ab40083b60173c0010db60137b6013b4da701e92a2a13013db601262ab40083b60173c0010db601414da701d02ab40073b60114c001164da701c22a130143b601264da701b72a130145b601264da701ac2a130147b601264da701a12a130149b601264da701962a13014bb601264da7018b2a13014db601264da701802a13014fb601264da70175bb012e592ab40075b60114c00116b80153b70133130130b601562ab4006bb60114c00116b60156b6013b4da701472a130158b601264da7013c2a13015ab601264da701312a13015cb601264da701262a130158b601264da7011b2a13015eb601264da701102a13015ab601264da701052ab40083b60173c0010db6011b04a0000ebb011d5904b70120a7000bbb011d5903b701204da700ddbb012e592ab40041b6015fc00116b80153b70133130161b60156b6013b4da700bcbb012e592ab40041b6015fc00116b80153b70133130163b60156b6013b4da7009b2ab40067b6015fc001164da7008d2a130165b601264da700822a130167b601264da700772ab40087b60173c0010db6011b05709a000704a7000403b8016a4da70059014da700542ab40071b60114c001164da700462ab4007fb60114c001164da700382ab40081b60114c001164da7002a2ab40077b60114c0010d4da7001c2ab40087b60173c0010d4da7000e2ab4006db60114c0010d4d2cb0000000010094000001c20070000002eb000202ed00ec02f100ee02f200f102f600fa02f700fd02fb010602fc010903000112030101150305011e03060121030a012a030b012d030f01360310013903140142031501450319014e031a0151031e015a031f015d03230166032401690328017203290175032d017e032e01810332018c0333018f0337019a0338019d033c01c2033d01c5034101cd034201d0034601f5034701f8034b0200034c02030350020b0351020e0355021603560219035a0224035b0227035f02320360023503640240036502430369025e036a0261036e0277036f027a03730285037402880378029003790293037d029b037e029e038202a6038302a9038702b1038802b4038c02bc038d02bf039102c7039202ca039602d2039702d5039b0300039c030303a0030b03a1030e03a5031603a6031903aa032103ab032403af032c03b0032f03b4033703b5033a03b9034203ba034503be036a03bf036d03c3038b03c4038e03c803ac03c903af03cd03ba03ce03bd03d203c503d303c803d703d003d803d303dc03ee03dd03f103e103f303e203f603e6040103e7040403eb040f03ec041203f0041d03f1042003f5042b03f6042e03fa043903fb043c03ff04470407000101740000000200017400155f313434343635363531303730345f3239383731307400326e65742e73662e6a61737065727265706f7274732e656e67696e652e64657369676e2e4a524a61766163436f6d70696c6572', NULL, '2016-02-03 14:37:39.192081', 'print', NULL);


--
-- Data for Name: user_preference_master; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (1, 'DEFAULT_PROGRAM', 'Default Program', 'DEFAULTS', 1, 1, 'Sets the default program for user (applies on dashboard)', 'program', 'single-select', 'int', '1', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (2, 'DEFAULT_SCHEDULE', 'Default Schedule', 'DEFAULTS', 1, 2, 'Sets the default schedule for user (applies on dashboard)', 'processing_schedule', 'single-select', 'int', '1', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (3, 'DEFAULT_FACILITY', 'Default Facility', 'DEFAULTS', 1, 3, 'Sets the default facility for user (applies on dashboard)', 'facility', 'single-select', 'int', '1', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (4, 'DEFAULT_SUPERVISORY_NODE', 'Default Supervisory Node', 'DEFAULTS', 1, 4, 'Sets the default facility for user (applies on dashboard)', 'supervisory_node', 'single-select', 'int', '1', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (5, 'DEFAULT_REQUISITION_GROUP', 'Default Requisition Group', 'DEFAULTS', 1, 5, 'Sets the default requisition group for user (applies on dashboard)', 'facility', 'single-select', 'int', '1', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (6, 'DEFAULT_PRODUCT', 'Default Product', 'DEFAULTS', 1, 6, 'Sets the default program for user (applies on dashboard)', 'product', 'single-select', 'int', '1', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (7, 'DEFAULT_PRODUCTS', 'Default Indicator Products', 'DEFAULTS', 1, 7, 'Sets the default program for user (applies on dashboard)', 'product', 'multi-select', 'csv', '1,2,3', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (8, 'ALERT_EMAIL_OVER_DUE_REQUISITION', 'Email Notifications on overdue requisition', 'ALERT_EMAIL', 1, 1, 'Send email notifications when rnr is overdue', 'none', 'checkbox', 'boolean', 'true', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');
INSERT INTO user_preference_master (id, key, name, groupname, groupdisplayorder, displayorder, description, entitytype, inputtype, datatype, defaultvalue, isactive, createdby, createddate, modifiedby, modifieddate) VALUES (9, 'ALERT_SMS_NOTIFICATION_OVERDUE_REQUISITION', 'SMS Notifications on overdue requisition', 'ALERT_SMS', 1, 2, 'Send email notifications when rnr is overdue', 'none', 'checkbox', 'boolean', 'true', true, NULL, '2016-02-03 14:37:35.742327', NULL, '2016-02-03 14:37:35.742327');


--
-- Name: user_preference_master_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('user_preference_master_id_seq', 9, true);


--
-- Data for Name: configuration_settings; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (17, 'KANNEL_SETTINGS', 'http://localhost:13013/cgi-bin/sendsms?username=root&password=8819rukia', 'Kannel URL', NULL, 'Notification - SMS', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (18, 'SMS_ENABLED', 'false', 'Is SMS Enabled', '', 'Notification - SMS', 29, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (29, 'REQUISITION_REJECTED_EMAIL_MESSAGE_TEMPLATE', 'Hi, Please note that your rnr for period {quarter_name, year} not approved. Thank you.', 'Requisition Rejected Notification Email template', 'This template is used when sending SMS notification when there is rejected requisition', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (8, 'EMAIL_SUBJECT_APPROVAL', 'Your Action is Required', 'Requisition Approval Notification Subject', NULL, 'Notification - Email', 9, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (21, 'ALERT_STOCKEDOUT_HIDDEN_COLUMNS', 'id,alertsummaryid,facilityid', 'Alert Stockedout hidden columns', 'Columns to hide when rendering data on UI', 'Notification - Email', 32, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (1, 'COUNTRY', 'Tanzania', 'Country Name', NULL, 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (2, 'LOGO_FILE_NAME', 'logo_tz.png', 'Main Logo File Name', NULL, 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (3, 'OPERATOR_LOGO_FILE_NAME', 'operator_logo_1.png', 'Operator Logo File Name', NULL, 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (5, 'MONTHS', 'Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sep,Oct,Nov,Dec', 'Months', NULL, 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (6, 'REPORT_MESSAGE_WHEN_NO_DATA', 'no data found', 'Reports (No Data Messages)', NULL, 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (10, 'INDICATOR_PRODUCTS', 'Indicator Products', 'Indicator Products', NULL, 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (13, 'ORDER_REPORT_ADDRESS', 'To The Managing Director
Medical Stores Ltd.
Lusaka
Fax numbers : 01-241193,01-246288', 'Order Report Address', '', 'Report Labels', 13, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (14, 'ORDER_REPORT_TITLE', 'Issue Voucher for Medical Supplies (PH81-N/E)', 'Order Report Title', '', 'Report Labels', 14, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (15, 'ORDER_SUMMARY_SHOW_SIGNATURE_SPACE_FOR_CUSTOMER', 'True', 'Order Report: Show Signature space for customers', '', 'Report Labels', 14, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (16, 'ORDER_SUMMARY_SHOW_DISCREPANCY_SECTION', 'True', 'Order Report: Show space for explanations of discrepancies', '', 'Report Labels', 15, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (4, 'START_YEAR', '2010', 'eLMIS Start Year', NULL, 'R & R', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (11, 'DEFAULT_ZERO', 'false', 'Enable RnR to fill zero by default', 'Fill RnR with 0 values when RnR is initated.', 'R & R', 1, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (12, 'RNR_PRINT_REPEAT_CURRENCY_SYMBOL', 'true', 'Repeat currency symbol on print rnr rows', '', 'R & R', 11, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (23, 'LATE_REPORTING_DAYS', '10', 'Number of days to track late reporting', 'Number of days to track late reporting.', 'R & R', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (38, 'CSV_LINE_SEPARATOR', '\r\n', 'Line Separator', '', 'Order Export', 50, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (39, 'CSV_APPLY_QUOTES', 'Apply Quotes', 'Apply Quotes to wrap fields', 'true', 'Order Export', 51, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (30, 'SUBMIT_RNR_REMINDER_SMS_MESSAGE_TEMPLATE', 'This a friendly reminder that your RnR for period { quarter_name, year} is due on {due_date}. Thank you.', 'Submit RnR Reminder Notification SMS template', 'This template is used when sending SMS notification of RnR due date ', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (32, 'STOCK_STATUS_SMS_MESSAGE_TEMPLATE', 'Please note that the following products are currently stocked out. Thank you.', 'Stocked out Notification SMS template', 'This template is used when sending SMS notification of stocked out product ', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (34, 'RATIONING_SMS_MESSAGE_TEMPLATE', 'Please note that the following commodity is rationed. Thank you.', 'Rationing Notification SMS template', 'This template is used when sending SMS notification of rationing ', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (20, 'LATE_RNR_NOTIFICATION_SMS_TEMPLATE', 'Dear {name}, Please submit RnR for this period {facility_name}', 'Late RnR Email Notification Template', '', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (24, 'PRODUCT_RECALLED_SMS_MESSAGE_TEMPLATE', 'Please note that the following products have been recalled. Thank you.', 'Product Recalled Notification SMS template', 'This template is used when sending SMS notification when a product is recalled', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (26, 'REQUISITION_PENDING_SMS_MESSAGE_TEMPLATE', 'Please note that your rnr for period {quarter_name, year} is being processed. Thank you.', 'Requisition Pending Notification SMS template', 'This template is used when sending SMS notification when there is pending requisition', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (28, 'REQUISITION_REJECTED_SMS_MESSAGE_TEMPLATE', 'Please note that your rnr for period {quarter_name, year} not approved. Thank you.', 'Requisition Rejected Notification SMS template', 'This template is used when sending SMS notification when there is rejected requisition', 'Notification - SMS', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (35, 'RATIONING_EMAIL_MESSAGE_TEMPLATE', 'Hi, Please note that the following commodity is rationed. Thank you.', 'Rationing Notification Email template', 'This template is used when sending Email notification of rationing', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (31, 'SUBMIT_RNR_REMINDER_EMAIL_MESSAGE_TEMPLATE', 'Hi, This a friendly reminder that your RnR for period { quarter_name, year} is due on {due_date}. Thank you.', 'Submit RnR Reminder Notification Email template', 'This template is used when sending Email notification of RnR due date ', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (33, 'STOCK_STATUS_EMAIL_MESSAGE_TEMPLATE', 'Hi, Please note that the following products are currently stocked out. Thank you.', 'Stocked out Notification Email template', 'This template is used when sending Email notification of stocked out product ', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (7, 'EMAIL_TEMPLATE_APPROVAL', 'Dear {approver_name} This is to inform you that {facility_name} has completed its Report and Requisition for the Period {period} and requires your approval. Please login to approve / reject it. Thank you.', 'Requisition Approval Notification email template', 'This template is used when sending email notification to approvers.<br />Please use the following as place holders. {approver_name}, {facility_name}, {period}', 'Notification - Email', 10, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (19, 'LATE_RNR_NOTIFICATION_EMAIL_TEMPLATE', 'Dear {name}

{facility_name} facility has not reported for {period}. Please submit RnR for this period.

Thank you', 'Late RnR Email Notification Template', '', 'Notification - Email', 30, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (25, 'PRODUCT_RECALLED_EMAIL_MESSAGE_TEMPLATE', 'Hi,  Please note that the following products have been recalled. Thank you.', 'Product Recalled Notification Email template', 'This template is used when sending Email notification when a product is recalled', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (27, 'REQUISITION_PENDING_EMAIL_MESSAGE_TEMPLATE', 'Hi, Please note that your rnr for period {quarter_name, year} is being processed. Thank you.', 'Requisition Pending Notification Email template', 'This template is used when sending Email notification when there is pending requisition', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (42, 'LATE_RNR_SUPERVISOR_NOTIFICATION_EMAIL_TEMPLATE', 'Dear {name}

Please find attached list of facilities that did not report in your district.', 'Late RnR Email for supervisor Notification Template', '', 'Notification - Email', 31, 'TEXT_AREA', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (37, 'OPERATOR_NAME', 'Ministry of Health and Social Welfare', 'Reporting header main title: Name of the organization', '', 'Report Labels', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (44, 'ENABLE_GOOGLE_ANALYTICS', 'false', 'Enable Google Analytics', '', 'Analytics', 1, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (45, 'GOOGLE_ANALYTICS_TRACKING_CODE', 'UA-49644602-3', 'Google Analytics Tracking Code', '', 'Analytics', 2, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (49, 'LOCAL_ORDER_EXPORT_DIRECTORY', './local-order-ftp-data', 'Local Directory To Export Order', 'Local Directory to Export Order', 'Order Export', 4, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (50, 'USE_FTP_TO_SEND_ORDERS', 'true', 'Use FTP to Send Orders', 'Use FTP To Send Orders', 'Order Export', 5, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (52, 'VENDOR_MAINTENANCE_REQUEST_EMAIL_TEMPLATE', '<p>Dear ${model.user.firstName} ${model.user.lastName}</p><p>Please note that there is a pending maintenance request for your organization on behalf of <b><i>${model.facility.name}</i></b> in <b><i>${model.facility.geographicZone.name}</i></b>.&nbsp;</p><table><tbody><tr><td>Equipment</td><td><b>${model.equipment.equipment.name}</b></td></tr><tr><td>Model</td><td><b>${model.equipment.model}</b></td></tr><tr><td>Request Reason</td><td>${model.request.reason}</td></tr><tr><td>Requested Date</td><td>${model.request.requestDate}</td></tr><tr><td>Recommended Date</td><td>${model.request.recommendedDate}</td></tr><tr><td>Comment</td><td>${model.request.comment}</td></tr></tbody></table><p><span><br>Thank you for your timely response.</span><br></p><p style="text-align: center;"><span style="color: #dfdfdf;">This email is sent to you because you were registered in the system as a representative to ${model.vendor.name}.&nbsp;</span></p>', 'Pending maintenance notification template', 'This template is used when sending Email notification when there is pending maintenance request', 'Notification - Email', 40, 'HTML', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (22, 'SMS_TEST_PHONE_NO', '', 'SMS Testing Phone Number', NULL, 'R & R', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (47, 'DISTRICT_REPORTING_CUT_OFF_DATE', '14', 'Cut-off date to track distict timeliness reporting', 'Cut-off date to track distict timeliness reporting.', 'R & R', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (48, 'MSD_ZONE_REPORTING_CUT_OFF_DATE', '21', 'Cut-off date to track MSD Zone Timeliness Reporting', 'Cut-off date to track MSD Zone Timeliness Reporting.', 'R & R', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (46, 'RNR_COPY_SKIPPED_FROM_PREVIOUS_RNR', 'true', 'Copy Skipped field from Previous R & R', '', 'R & R', 51, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (53, 'UNSCHEDULED_REPORTING_CUT_OFF_DATE', '30', 'Cut-off date to track unscheduled reporting', 'Cut-off date to track unscheduled reporting.', 'R & R', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (54, 'VACCINE_REPORT_VACCINE_CATEGORY_CODE', 'Vaccine', 'Vaccine Product Category Code', 'Use this code to filter data from data source', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (55, 'VACCINE_REPORT_VITAMINS_CATEGORY_CODE', 'vit', 'Syringes Product Category Code', 'Use this code to filter data from data source', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (56, 'VACCINE_REPORT_SYRINGES_CATEGORY_CODE', 'Syringes and safety boxes', 'Syringes Product Category Code', 'Use this code to filter data from data source', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (57, 'EQUIPMENT_REPLACEMENT_YEAR', '11', 'Standard Years For Equipment Replacement', 'Standard Years For Equipment Replacement.', 'GENERAL', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (58, 'YEAR_OF_EQUIPMENT_REPLACEMENT', '5', 'Number Of Years For Equipment Plan', 'Number Of Years For Equipment Plan.', 'GENERAL', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (59, 'STATIC_PAGE_FOOTER_STATUS', 'true', 'Static Page Footer Enabled', '', 'GENERAL', 59, 'BOOLEAN', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (60, 'PROGRAM_VIEWABLE_MAX_LAST_PERIODS', '4', 'Program Viewable max last periods', 'Used to limit maximum number of last periods to show when program is selected on dashboard page', 'Dashboard', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (61, 'VACCINE_DEMOGRAPHIC_ESTIMATE_COHORT_ID', '4', 'Surving Infants estimate id', 'Used in vaccine reports for calculating coverage', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (62, 'VACCINE_DROPOUT_DTP', 'V010', 'DTP product code', 'Used in vaccine reports for calculating dropout rate', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (63, 'VACCINE_DROPOUT_BCG', 'V001', 'BCG product code', 'Used in vaccine reports for calculating dropout rate', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (64, 'VACCINE_DEMOGRAPHIC_ESTIMATE_COHORT_ID', 'V009', 'MR product code', 'Used in vaccine reports for calculating dropout rate', 'VACCINE', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (65, 'DASHBOARD_SLIDES_TRANSITION_INTERVAL_MILLISECOND', '20000', 'Dashboard slide transition interval in millisecond', 'Dashboard slide transition interval in millisecond', 'Dashboard', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (66, 'LOGIN_SUCCESS_DEFAULT_LANDING_PAGE', '/public/pages/dashboard/index.html#/dashboard', 'Configure Default Home Page', '', 'Dashboard', 1, 'TEXT', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (67, 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_ESS_MEDS', '<h2><strong>Dados de requisição Via Classica anexados </strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisição Via Classica para o SIMAM.</p></div>', 'Requisition authorization mail template for via', 'Requisition authorization mail templatefor via', 'Notification - Email', 40, 'HTML', NULL, true);
INSERT INTO configuration_settings (id, key, value, name, description, groupname, displayorder, valuetype, valueoptions, isconfigurable) VALUES (68, 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MMIA', '<h2><strong>Relatorio de MMIA anexado</strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos anexados, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do  SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisição Via Classica para o SIMAM.</p></div>', 'Requisition authorization mail  template for mmia', 'Requisition authorization mail template for mmia', 'Notification - Email', 40, 'HTML', NULL, true);


--
-- Name: configuration_settings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('configuration_settings_id_seq', 68, true);


