DROP TABLE IF EXISTS master_template_column_rules;
CREATE TABLE master_template_column_rules (
    column_name VARCHAR(200) REFERENCES Master_RnR_Template(column_name),
    dependent_column_name VARCHAR(200) REFERENCES Master_RnR_Template(column_name),
    UNIQUE (column_name, dependent_column_name)
);
