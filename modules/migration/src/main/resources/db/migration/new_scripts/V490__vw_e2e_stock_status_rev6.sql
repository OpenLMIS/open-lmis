-- View: vw_e2e_stock_status  

DROP VIEW IF EXISTS vw_e2e_stock_status_fill_rates;
DROP VIEW IF EXISTS vw_e2e_stock_status;

CREATE OR REPLACE VIEW vw_e2e_stock_status AS 
SELECT
	d.facilityId,
	d.facilityName,
  d.geographiczoneid,
	d.geographiczonename AS districtName,
	d.programCode,
	d.programName,
	date_part(
		'year' :: TEXT,
		periodstartdate
	) AS reportYear,
	date_part(
		'month' :: TEXT,
		periodstartdate
	) AS reportMonth,
	(
		date_part(
			'month' :: TEXT,
			periodstartdate
		) / 4 :: DOUBLE PRECISION
	) :: INTEGER + 1 AS reportQuarter,
	d.createddate :: DATE reportedDate,
        d.periodId,
	d.processingperiodname periodName,
	d.productId,
	d.productCode,
	d.productprimaryname productName,
	d.openingBalance,
	d.quantityreceived AS received,
	d.dispensed AS issues,
	d.adjustment,
	d.soh stockOnHand,
	d.amc,
	d.mos,
	d.stocking stockStatus,
	d.stockOutDays,
	d.quantityOrdered,
	d.quantityShipped AS quantitySupplied,
	d.dateOrdered,
	d.dateShipped AS dateSupplied,
	d.rmnch
	
FROM
	dw_orders d;

ALTER TABLE vw_e2e_stock_status
  OWNER TO postgres;
