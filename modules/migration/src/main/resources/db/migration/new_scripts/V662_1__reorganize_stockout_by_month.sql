DROP TYPE IF EXISTS stock_out_event CASCADE;
DROP TYPE IF EXISTS MONTH_OVERLAP_OF_STOCKOUT CASCADE;
-- lines above will drop the type, and functions/views that use it

CREATE TYPE MONTH_OVERLAP_OF_STOCKOUT AS (resolved_date DATE, overlapped_month DATE, overlap_duration INT, is_resolved BOOLEAN);

CREATE OR REPLACE FUNCTION months_of(INTERVAL)
  RETURNS INT AS $$
BEGIN
  RETURN extract(years FROM $1) :: INT * 12 + EXTRACT(MONTH FROM $1) :: INT;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION months_between(DATE, DATE)
  RETURNS INT AS $$
BEGIN
  RETURN abs(months_of(age(date_trunc('month', $1), date_trunc('month', $2))));
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION extract_days(DATERANGE)
  RETURNS INT AS
$$
BEGIN
  RETURN (date_trunc('day', UPPER($1)) :: DATE - date_trunc('day', LOWER($1)) :: DATE) + 1;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION last_day_of_month(DATE)
  RETURNS DATE AS
$$
BEGIN
  RETURN (to_char(($1 + INTERVAL '1 month'), 'YYYY-MM') || '-01') :: DATE - 1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION calculate_one_month_overlap(occuredDate     DATE, resolvedDate DATE, firstDayOfMonth DATE,
                                                       lastDayOfMonthy DATE)
  RETURNS INT AS $$
DECLARE
  overlap_duration INT;
BEGIN

  IF occuredDate = resolvedDate
  THEN
    RETURN 0;
  END IF;

  overlap_duration := extract_days(
      DATERANGE(occuredDate, resolvedDate) * DATERANGE(firstDayOfMonth, lastDayOfMonthy));
  IF overlap_duration IS NULL
  THEN
    RETURN 1;
  ELSE
    RETURN overlap_duration;
  END IF;

END
$$ LANGUAGE plpgsql;

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

  FOR m IN 1..months + 1
  LOOP
    firstDayOfMonth := date_trunc('month', occured_date + (m - 1 || ' month') :: INTERVAL);
    lastDayOfMonthy:=last_day_of_month(firstDayOfMonth);

    overlap_duration := calculate_one_month_overlap(occured_date, resolvedDate, firstDayOfMonth, lastDayOfMonthy);

    RETURN NEXT (resolvedDate, firstDayOfMonth, overlap_duration, is_resolved);
  END LOOP;
END;
$event$ LANGUAGE plpgsql;

-- CREATE OR REPLACE VIEW vw_stockouts AS
--   SELECT
--     facilities.name             AS facility_name,
--     facilities.code             AS facility_code,
--     zone.name                   AS district_name,
--     zone.code                   AS district_code,
--     parent_zone.name            AS province_name,
--     parent_zone.code            AS province_code,
--     products.code               AS drug_code,
--     products.primaryname        AS drug_name,
--     programs.name               AS program,
--     stock_card_entries.occurred AS stockout_date,
--     (calculate_each_month_duration(stock_cards.id, stock_card_entries.occurred,
--                                    stockcardentryid)).*
--   FROM facilities
--     JOIN geographic_zones AS zone ON facilities.geographiczoneid = zone.id
--     JOIN geographic_zones AS parent_zone ON zone.parentid = parent_zone.id
--     JOIN stock_cards ON facilities.id = stock_cards.facilityid
--     JOIN products ON stock_cards.productid = products.id
--     JOIN program_products ON products.id = program_products.productid
--     JOIN programs ON program_products.programid = programs.id
--     JOIN stock_card_entries ON stock_cards.id = stock_card_entries.stockcardid
--     JOIN stock_card_entry_key_values ON stock_card_entries.id = stock_card_entry_key_values.stockcardentryid
--   WHERE keycolumn = 'soh' AND valuecolumn = '0' AND stock_card_entries.quantity != 0
--   ORDER BY facility_code, drug_code, stockout_date, overlapped_month, stockcardentryid;