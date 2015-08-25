-- Function: fn_vims_monthly_report_summary(integer, integer, integer)

DROP FUNCTION IF EXISTS fn_vims_monthly_report_summary(integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_vims_monthly_report_summary(IN in_programid integer, IN in_geographiczoneid integer, IN in_periodid integer)
  RETURNS TABLE(programid integer, geographiczoneid integer, district character varying, periodid integer, periodname character varying, year integer, demo_population integer, demo_surviving_infants_0_11_annual integer, demo_surviving_infants_0_11_monthly integer, demo_population_pregnant integer, demo_population_new_born integer, compl_num_facilities integer, compl_num_vaccine_units integer, compl_num_reports_received integer, compl_num_reports_online integer, compl_num_outreach_sessions integer, iec_num_sessions integer, iec_num_participants integer, iec_num_radio_spots integer, iec_num_home_visits integer, aefi_num_cases integer, wastage_num_safety_boxes_used integer, wastage_num_safety_boxes_disposed integer, cca_num_reported_temp_status integer, cca_num_temp_2_c integer, cca_num_temp_8_c integer, cca_min_temp integer, cca_max_temp integer, cca_num_temp_low_alarm integer, cca_num_temp_high_alarm integer, ccb_min_temp integer, ccb_max_temp integer, ccb_num_temp_low_alarm integer, ccb_num_temp_high_alarm integer) AS
$BODY$
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

$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_vims_monthly_report_summary(integer, integer, integer)
  OWNER TO postgres;
