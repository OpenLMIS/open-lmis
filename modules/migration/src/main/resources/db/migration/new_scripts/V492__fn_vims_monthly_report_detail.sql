-- Function: fn_vims_monthly_report_detail(integer, integer, integer)

DROP FUNCTION IF EXISTS fn_vims_monthly_report_detail(integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_vims_monthly_report_detail(IN in_programid integer, IN in_geographiczoneid integer, IN in_periodid integer)
  RETURNS TABLE(programid integer, geographiczoneid integer, district character varying, periodid integer, periodname character varying, year integer, productid integer, productname character varying, productgroupcode character varying, productcategorycode character varying, cov_child_num_infants_fixed integer, cov_child_num_infants_mobile integer, cov_child_num_other integer, cov_child_num_monthly integer, cov_child_num_cumulative integer, cov_child_num_drop_outs integer, cov_child_num_perf_class character varying, cov_women_num_pregnant_fixed integer, cov_women_num_pregnant_mobile integer, cov_women_num_other integer, cov_women_num_monthly integer, cov_women_num_cumulative integer, cov_women_num_drop_outs integer, cov_women_num_perf_class character varying, cov_girls_num_adole_fixed integer, cov_girls_num_adole_mobile integer, cov_girls_num_other integer, cov_girls_num_monthly integer, cov_girls_num_cumulative integer, cov_girls_num_drop_outs integer, cov_girls_num_perf_class character varying, cov_vitamin_num_less_than_year_count integer, cov_vitamin_num_more_than_year_count integer, cov_vitamin_num_partum_count integer, cov_vitamin_num_less_than_year_monthly integer, cov_vitamin_num_more_than_year_monthly integer, cov_vitamin_num_partum_monthly integer, cov_vitamin_num_less_than_year_cumulative integer, cov_vitamin_num_more_than_year_cumulative integer, cov_vitamin_num_partum_cumulative integer, ss_received integer, ss_onhand integer, ss_vvm integer, ss_freezing integer, ss_expired integer, ss_opened integer, ss_wastage_rate integer, svl_0_11_months_cases integer, svl_0_11_months_deaths integer, svl_12_59_months_cases integer, svl_12_59_months_deaths integer, svl_5_15_years_cases integer, svl_5_15_years_deaths integer, svl_15_plus_years_cases integer, svl_15_plus_years_deaths integer, svl_status_vaccinated integer, svl_status_unvaccinated integer, svl_status_unknown integer) AS
$BODY$
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

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_vims_monthly_report_detail(integer, integer, integer)
  OWNER TO postgres;
