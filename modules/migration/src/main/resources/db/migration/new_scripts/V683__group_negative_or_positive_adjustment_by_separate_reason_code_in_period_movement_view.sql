CREATE OR REPLACE FUNCTION records_group_by_reason(cardid INTEGER, periodStart TIMESTAMP, periodEnd TIMESTAMP)
  RETURNS TABLE(reason_code TEXT, occurrences BIGINT, total_quantity BIGINT) AS $BODY$
BEGIN
  RETURN QUERY (SELECT
                        (SELECT CASE
                                WHEN adjustmenttype IN ('PUB_PHARMACY',
                                                        'MATERNITY',
                                                        'GENERAL_WARD',
                                                        'ACC_EMERGENCY',
                                                        'LABORATORY',
                                                        'UATS',
                                                        'PNCTL',
                                                        'PAV',
                                                        'DENTAL_WARD',
                                                        'UNPACK_KIT')
                                  THEN 'CONSUMPTION'
                                ELSE adjustmenttype
                                END AS reason) :: TEXT AS reason_code,
                        count(adjustmenttype)          AS occurrences,
                        abs(sum(quantity))             AS total_quantity
                      FROM stock_card_entries
                      WHERE stockcardid = cardid AND occurred >= periodStart AND occurred <= periodEnd
                      GROUP BY reason_code) ;
END
$BODY$
LANGUAGE 'plpgsql';
