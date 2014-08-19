ALTER TABLE regimen_line_items
ADD skipped BOOLEAN DEFAULT(false) NOT NULL;

INSERT INTO master_regimen_columns
(name, label, visible, datatype)
    values
      ('skipped','Skip', true, 'Boolean');

-- INSERT INTO program_regimen_columns
-- (id, programId, name, label, visible, dataType, createdBy, createdDate, modifiedBy, modifiedDate)
--     values
--       (0, 2, 'skipped', 'Skip', true, 'Boolean', 2, NOW(), 2, NOW());

