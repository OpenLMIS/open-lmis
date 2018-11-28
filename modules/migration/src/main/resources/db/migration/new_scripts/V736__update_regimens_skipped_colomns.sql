BEGIN;

UPDATE regimens SET skipped = true WHERE id IN (244, 246, 248, 250);

COMMIT;