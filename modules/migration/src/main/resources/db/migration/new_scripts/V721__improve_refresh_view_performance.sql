CREATE INDEX occurred_idx ON stock_card_entries(occurred);
CREATE INDEX stock_card_entries_stockcardid_idx ON stock_card_entries(stockcardid);

BEGIN TRANSACTION;

DROP TABLE IF EXISTS cmm_entries_tmp;
CREATE TABLE IF NOT EXISTS cmm_entries_tmp (LIKE cmm_entries INCLUDING DEFAULTS);
ALTER TABLE cmm_entries_tmp ADD stockcardid INTEGER;
INSERT INTO cmm_entries_tmp
  SELECT cmm.*,cards.id FROM cmm_entries cmm
    LEFT JOIN stock_cards cards
      ON cmm.facilityid = cards.facilityid
         AND (SELECT id FROM products WHERE code = cmm.productcode) = cards.productid;

CREATE INDEX cmm_entries_tmp_stockcardid_idx ON cmm_entries_tmp(stockcardid);
ALTER TABLE cmm_entries_tmp ADD CONSTRAINT cmm_entries_tmp_facilityid_fkey FOREIGN KEY (facilityid) REFERENCES facilities(id);
ALTER TABLE cmm_entries_tmp ADD CONSTRAINT cmm_entries_tmp_productcode_fkey FOREIGN KEY (productcode) REFERENCES products(code);
ALTER TABLE cmm_entries_tmp ADD CONSTRAINT cmm_entries_tmp_stockcardid_fkey FOREIGN KEY (stockcardid) REFERENCES stock_cards(id);

ALTER TABLE cmm_entries_tmp ADD PRIMARY KEY (id);

ALTER TABLE cmm_entries RENAME TO cmm_entries_bak;
ALTER TABLE cmm_entries_tmp RENAME TO cmm_entries;

CREATE OR REPLACE FUNCTION cmm_of(cardid INTEGER, periodStart TIMESTAMP, periodEnd_P TIMESTAMP)
  RETURNS DOUBLE PRECISION AS $$
DECLARE
  cmm DOUBLE PRECISION;
BEGIN
  cmm = (SELECT cmmvalue
         FROM cmm_entries
         WHERE stockcardid = cardid
               AND cmm_entries.periodbegin = periodStart :: DATE
               AND cmm_entries.periodend = periodEnd_P :: DATE);

  IF (cmm = -1)
  THEN
    RETURN NULL;
  ELSE
    RETURN cmm;
  END IF;

END
$$
LANGUAGE 'plpgsql';
END TRANSACTION;