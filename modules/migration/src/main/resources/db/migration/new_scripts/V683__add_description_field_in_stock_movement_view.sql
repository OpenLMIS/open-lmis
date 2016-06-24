DROP VIEW vw_stock_movements;

CREATE OR REPLACE VIEW vw_stock_movements AS (
  SELECT
    movement.id AS id,
    movement.adjustmenttype AS reason,
    types.category AS adjustmenttype,
    types.description AS description,
    movement.referencenumber AS documentnumber,
    movement.occurred AS movementdate,
    movement.quantity AS quantity,
    movement.requestedquantity as requestedquantity,
    stock_cards.totalquantityonhand AS totalquantityonhand,
    p.primaryname AS primaryname,
    p.code AS productcode,
    f.name AS facilityname,
    f.code AS facilitycode,
    set_value(movement.id, 'signature') AS signature,
    set_value(movement.id, 'soh') AS soh,
    set_value(movement.id, 'expirationdates') AS expirationdates

  FROM stock_cards
    JOIN stock_card_entries AS movement ON stock_cards.id = movement.stockcardid
    JOIN products AS p ON stock_cards.productid = p.id
    JOIN facilities AS f ON stock_cards.facilityid = f.id
    JOIN losses_adjustments_types AS types ON types.name = movement.adjustmenttype
);