CREATE OR REPLACE FUNCTION set_value(id INTEGER, key TEXT)
  RETURNS TEXT AS $BODY$
DECLARE
  value TEXT;
BEGIN
  SELECT valuecolumn
  FROM stock_card_entry_key_values
  WHERE keycolumn = key AND stockcardentryid = id
  INTO value;

  RETURN value;
END
$BODY$
LANGUAGE 'plpgsql';

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.adjustmenttype AS reason,
    movement.type AS adjustmenttype,
    movement.referencenumber AS documentnumber,
    movement.createddate AS createddate,
    movement.quantity AS quantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirydates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
);