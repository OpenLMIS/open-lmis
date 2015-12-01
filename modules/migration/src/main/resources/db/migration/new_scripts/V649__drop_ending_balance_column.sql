ALTER TABLE vaccine_report_logistics_line_items
DROP COLUMN endingBalance;

DELETE FROM vaccine_logistics_master_columns where name = 'endingBalance';

DELETE from vaccine_program_logistics_columns where label = 'Ending Balance';