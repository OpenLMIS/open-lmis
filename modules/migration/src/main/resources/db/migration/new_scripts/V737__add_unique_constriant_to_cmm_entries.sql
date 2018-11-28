BEGIN;

ALTER TABLE cmm_entries ADD CONSTRAINT cmm_entries_facilityid_productcode_periodbegin_key UNIQUE (facilityid, productcode, periodbegin);

COMMIT;