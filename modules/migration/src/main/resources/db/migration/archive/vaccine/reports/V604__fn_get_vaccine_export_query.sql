-- Function: fn_vaccine_export_query(integer, integer, integer)

DROP FUNCTION IF EXISTS fn_get_vaccine_export_query(integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_get_vaccine_export_query(in_program integer, in_period integer, in_zone integer)
  RETURNS character varying AS
$BODY$
DECLARE

q VARCHAR;
q2 VARCHAR;
r RECORD;
t_year integer;
t_month integer;
t_startdate date;
t_delim VARCHAR;
t_str VARCHAR;
t_str_2 VARCHAR;
t_str_3 VARCHAR;
BEGIN

select extract(month from startdate), extract(year from startdate) into t_month, t_year from processing_periods where id = in_period;
t_month = COALESCE(t_month,0);
t_year = COALESCE(t_year,0);
t_delim = ',';

q = 
'select 
zoneid,
''TZ'' country_code,
(select name from geographic_zones where id = zoneid) district_name,
(select name from geographic_zones where id = (select parentid from geographic_zones where id = zoneid) limit 1) province_name,
(select round(sum(target_value_annual)::double precision / 12)
 from vw_vaccine_target_population 
where category_id = (select COALESCE(value::int,0) from configuration_settings where key = ''VACCINE_DEMOGRAPHIC_ESTIMATE_COHORT_ID'')
and geographic_zone_id = zoneid 
and year = (select extract(year from startdate) from processing_periods where id = '||in_period||')) monthly_cohort,
(SELECT
	COUNT (facilityid)
FROM
vaccine_reports vr
JOIN facilities f ON vr.facilityid = f. ID
WHERE
programid = '||in_program   ||' 
AND periodid = '||in_period ||' 
and f.geographiczoneid = zoneid
) numhfreportsincluded,
(SELECT
	COUNT (facilityid)
FROM
	vaccine_reports vr
JOIN processing_periods pp ON vr.periodid = pp. ID
JOIN facilities f ON vr.facilityid = f. ID
WHERE
programid = '||in_program   ||' 
AND periodid = '||in_period ||' 
and f.geographiczoneid = zoneid
AND ((CAST(vr.createddate AS DATE)) - (CAST(pp.startdate AS DATE)) ) <= 10) numhfreportstimely,
0 timeliness_of_reports,
( select sum(outreachimmunizationsessions) 
FROM
	vaccine_reports vr
JOIN processing_periods pp ON vr.periodid = pp. ID
JOIN facilities f ON vr.facilityid = f. ID
WHERE
programid = '||in_program   ||' 
AND periodid = '||in_period ||' 
and f.geographiczoneid = zoneid) outreach_sessions,';


q2 = 'select
 p.id, p.primaryname, vd.doseid,
lower(replace(replace(primaryname,'' '',''_''),''-'',''_'') || ''_'' || doseid ||''_L1'') product
from products p
join program_products pp on pp.productid = p.ID
join vaccine_product_doses vd on vd.productid = p.id
JOIN product_categories pg ON pp.productcategoryid = pg. ID
WHERE
	pg.code = (
		SELECT VALUE
		FROM
			configuration_settings
		WHERE
			KEY = ''VACCINE_REPORT_VACCINE_CATEGORY_CODE''
	)
and pp.programid = '||in_program||' 
order by 1,2,3';

t_str_2 = '';
-- add column set 1. This set is available in the databbase
FOR r IN EXECUTE q2
LOOP
 t_str_2 = t_str_2 || r.product || t_delim;
END LOOP;
q = q || t_str_2;

t_str_3 = '';

-- add GE columns. This set is not available from database. add as null columns
FOR r IN EXECUTE q2
LOOP
 t_str_3 = t_str_3 || ' null ' ||replace(r.product,'l1','g1') || t_delim;
END LOOP;
q = q || t_str_3;

-- add month and year
q = q || '(select extract(month from (select startdate from processing_periods where id = '||in_period||'))) report_month, ' 
      || '(select extract(year from (select startdate from processing_periods where id = '||in_period||')))  report_year ';

t_str = 
'from (
select * from crosstab(''select geographiczoneid, c.productid, 
sum(( COALESCE(regularmale,0) + COALESCE(regularfemale,0) + COALESCE(outreachmale,0) + COALESCE(outreachfemale,0) ))::int total
  from vaccine_report_coverage_line_items c
join vaccine_reports r on r.id = c.reportid
join facilities f on f.id = r.facilityid
join vw_districts vd on vd.district_id = f.geographiczoneid
where r.periodid = '||in_period ||' and r.programid = '|| in_program ||'
and (
	vd.parent = '|| in_zone ||' 
	OR vd.district_id = '|| in_zone ||' 
	OR vd.region_id = '|| in_zone ||' 
	OR vd.zone_id = '|| in_zone ||' 
) 
group by geographiczoneid,c.productid, c.doseid
order by 1,2,c.doseid'') as ct (
zoneid int,';

q = q || t_str;

-- add colum  set 1. This set is available in the database
q = q || regexp_replace(replace(t_str_2,',',' int,'), ',$', '');


q = q || ')) a';

RETURN q;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;