DROP FUNCTION IF EXISTS fn_e2e_pipeline(character varying, integer, integer, character varying);
DROP FUNCTION IF EXISTS fn_e2e_pipeline(integer, integer, integer);
CREATE OR REPLACE FUNCTION fn_e2e_pipeline(in_program integer, in_year integer, in_period integer)
  RETURNS TABLE(num integer, line text) AS
$BODY$

DECLARE
_Query VARCHAR;
qrow                 RECORD;
finalQuery            VARCHAR;
v_check_id integer;
rec RECORD;
where_str text;
n integer;
v_startdate date;
v_enddate date;
v_interval integer;
BEGIN

EXECUTE 'CREATE TEMP TABLE _data (
num integer,			
line text
) ON COMMIT DROP';



  -- add year filter
  where_str = ' where reportyear = ' || in_year;

 -- add period filter
 select id into v_check_id from processing_periods where id =  in_period limit 1;
 v_check_id = COALESCE(v_check_id,0);
 where_str =  where_str || ' and periodid = '|| in_period;

-- add program filter
select id into v_check_id from programs where id =  in_program limit 1;
v_check_id = COALESCE(v_check_id,0);
-- elmis program
where_str =  where_str || ' and programid = '|| in_program;


--TODO: implement multiple programs
select p.startdate::date,
 p.enddate::date,
 p.numberofmonths 
into v_startdate,
     v_enddate,
     v_interval 
from processing_periods p
where p.id = in_period;



_Query :=

'select distinct 
  productcode,
  product,
  dispensingunit
from vw_e2e_pipeline ' 
|| where_str;


n = 1;
insert into _data values(n, '<Export_File>');
n = n + 1;
insert into _data values(n, '<File_Header>');
n = n + 1;
insert into _data values(n, '<System_Name>Essential Medicine Logistics Improvement Program</System_Name>');
n = n + 1;
insert into _data values(n, '<dtmDataExported>'||now()::timestamp(0)||'</dtmDataExported>');
n = n + 1;
insert into _data values(n, '<dtmStart>'||v_startdate||'</dtmStart>');
n = n + 1;
insert into _data values(n, '<dtmEnd>'||v_enddate||'</dtmEnd>');
n = n + 1;
insert into _data values(n, '<dblDataInterval>'||v_interval||'</dblDataInterval>');
n = n + 1;
insert into _data values(n, '<SourceName>eLMIS</SourceName>');
n = n + 1;
insert into _data values(n, '</File_Header>');

n = n + 1;
insert into _data values(n, '<Products>');



FOR qrow IN EXECUTE _Query
LOOP
 n = n + 1;
 insert into _data values(n, '<Product>');
 n = n + 1;
 insert into _data values(n, '<strProductID>'||qrow.productcode||'</strProductID>');
 n = n + 1; 
 insert into _data values(n, '<strName>'||qrow.product||'</strName>');
 n = n + 1; 
 insert into _data values(n, '<strDose>'||qrow.dispensingunit||'</strDose>');
 n = n + 1; 
 insert into _data values(n, '<lngCYP>0</lngCYP>');
 n = n + 1; 
 insert into _data values(n, '</Product>'); 
END LOOP;
n = n + 1;
insert into _data values(n, '</Products>');



_Query :=

'select  
productcode,
startdate,
round(sum(COALESCE(dispensed))/3) dispensed,
round(sum(COALESCE(adjustment))/3) adjustment
from vw_e2e_pipeline ' || where_str || ' group by productcode,startdate';


n = n + 1;
insert into _data values(n+1, '<Records>');

FOR qrow IN EXECUTE _Query
LOOP
 n = n + 1;
 insert into _data values(n, '<Record>');
 n = n + 1;
 insert into _data values(n, '<strProductID>'||qrow.productcode||'</strProductID>');
 n = n + 1; 
 insert into _data values(n, '<dtmPeriod>'||qrow.startdate||'</dtmPeriod>');
 n = n + 1; 
 insert into _data values(n, '<lngConsumption>'||COALESCE(qrow.dispensed,0)||'</lngConsumption>');
 n = n + 1; 
 insert into _data values(n, '<lngAdjustments>'||COALESCE(qrow.adjustment,0)||'</lngAdjustments>');
 n = n + 1; 
 insert into _data values(n, '</Record>'); 
END LOOP;

n = n + 1;
insert into _data values(n, '</Records>');


n = n + 1;
insert into _data values(n, '</Export_File>');

  
finalQuery := 'select num, line FROM  _data order by num';

RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_e2e_pipeline(integer, integer, integer)
  OWNER TO openlmis;
