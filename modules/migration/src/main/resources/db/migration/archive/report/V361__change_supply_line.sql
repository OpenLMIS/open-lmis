ALTER TABLE supply_lines
  ALTER COLUMN supervisoryNodeId DROP NOT NULL;

ALTER TABLE supply_lines
  ADD parentId INTEGER NULL REFERENCES supply_lines(id);