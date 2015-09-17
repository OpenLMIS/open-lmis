-- replace with new function
DROP FUNCTION IF EXISTS fn_getstockstatusgraphdata(integer, integer, integer, character varying);
DROP FUNCTION IF EXISTS fn_get_elmis_stock_status_data(integer, integer, integer, character varying);
CREATE OR REPLACE FUNCTION fn_get_elmis_stock_status_data (
	IN in_programid INTEGER,
	IN in_geographiczoneid INTEGER,
	IN in_periodid INTEGER,
	IN in_productid character varying
) RETURNS TABLE (
	productid INTEGER,
	productname TEXT,
	periodid INTEGER,
	periodname TEXT,
	periodyear INTEGER,
	quantityonhand INTEGER,
	quantityconsumed INTEGER,
	amc INTEGER
) AS 

$BODY$ 
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
	$BODY$ 
	LANGUAGE plpgsql 
	VOLATILE COST 100 ROWS 1000;

ALTER FUNCTION fn_get_elmis_stock_status_data(
	INTEGER,
	INTEGER,
	INTEGER,
	character varying
) OWNER TO postgres;
