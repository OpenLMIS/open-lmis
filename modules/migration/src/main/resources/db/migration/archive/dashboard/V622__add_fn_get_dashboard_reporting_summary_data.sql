DROP FUNCTION IF EXISTS fn_get_dashboard_reporting_summary_data(integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_get_dashboard_reporting_summary_data
 (IN in_programid integer,
  IN in_periodid integer,
  IN in_userid integer default 0,
  IN in_geographiczoneid integer default 0 )
  RETURNS TABLE(
   total integer,
   expected integer,
   ever integer,
   period integer,
   late integer) AS
$BODY$
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
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;