CREATE OR REPLACE VIEW vw_stock_movements_integration AS
SELECT f.code AS facilitycode,
  f.name AS facilityname,
  p.code AS productcode,
  p.primaryname AS productfullname,
  p.strength AS productstrength,
  movement.occurred,
  movement.referencenumber,
  movement.notes,
  movement.modifieddate,
  types.category,
  lots.lotnumber,
  lotitem.quantity,
  lots.expirationdate
FROM stock_cards
JOIN stock_card_entries movement ON (stock_cards.id = movement.stockcardid)
JOIN stock_card_entry_lot_items lotitem ON (movement.id = lotitem.stockcardentryid)
JOIN lots ON (lots.id = lotitem.lotid)
JOIN products p ON (stock_cards.productid = p.id)
JOIN facilities f ON (stock_cards.facilityid = f.id)
JOIN losses_adjustments_types types ON ((types.name)::text = movement.adjustmenttype);