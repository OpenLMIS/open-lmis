CREATE OR REPLACE FUNCTION calculate_each_month_duration(stockcard_id      INTEGER, occured_date DATE,
                                                         stockout_entry_id INTEGER)
  RETURNS SETOF MONTH_OVERLAP_OF_STOCKOUT AS
$event$
DECLARE
  firstDayOfMonth  DATE;
  lastDayOfMonthy  DATE;
  overlap_duration INT;
  resolvedDate     DATE;
  event            MONTH_OVERLAP_OF_STOCKOUT;
  months           INT;
  is_resolved      BOOLEAN;
BEGIN

  SELECT occurred
  FROM stock_card_entry_key_values
    JOIN stock_card_entries ON stock_card_entry_key_values.stockcardentryid = stock_card_entries.id
  WHERE stock_card_entries.stockcardid = stockcard_id AND
        keycolumn = 'soh' AND valuecolumn != '0' AND
        stock_card_entry_key_values.stockcardentryid > stockout_entry_id
  ORDER BY stock_card_entry_key_values.stockcardentryid ASC
  LIMIT 1
  INTO resolvedDate;

  IF resolvedDate IS NULL
  THEN
    resolvedDate=current_date;
    is_resolved=FALSE;
  ELSE
    is_resolved=TRUE;
  END IF;

  SELECT months_between(occured_date, resolvedDate)
  INTO months;

  FOR m IN 0..months
  LOOP
    firstDayOfMonth := date_trunc('month', occured_date + (m - 1 || ' month') :: INTERVAL) + INTERVAL '20 days';
    lastDayOfMonthy := firstDayOfMonth + INTERVAL '1 month' - INTERVAL '1 day';

    overlap_duration := calculate_one_month_overlap(occured_date, resolvedDate, firstDayOfMonth, lastDayOfMonthy);

    RETURN NEXT (resolvedDate, firstDayOfMonth, overlap_duration, is_resolved);
  END LOOP;
END;
$event$ LANGUAGE plpgsql;
