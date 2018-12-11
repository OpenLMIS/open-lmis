BEGIN;
ALTER TABLE program_data_form_basic_items ADD COLUMN expirationdate varchar(20);
COMMIT;