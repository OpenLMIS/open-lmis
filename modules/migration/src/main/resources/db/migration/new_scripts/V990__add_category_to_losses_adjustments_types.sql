-- add a category text field for enum storage

ALTER TABLE losses_adjustments_types ADD COLUMN category text;
UPDATE losses_adjustments_types SET category = 'DEFAULT';