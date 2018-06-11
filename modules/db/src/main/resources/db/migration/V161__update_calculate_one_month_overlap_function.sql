CREATE OR REPLACE FUNCTION calculate_one_month_overlap(occuredDate     DATE, resolvedDate DATE, firstDayOfMonth DATE,
                                                       lastDayOfMonthy DATE)
  RETURNS INT AS $$
DECLARE
  overlap_duration INT;
BEGIN

  IF occuredDate >= resolvedDate
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