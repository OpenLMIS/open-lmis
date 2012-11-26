DROP TABLE IF EXISTS master_template_column_cyclic_dependency;
CREATE TABLE master_template_column_cyclic_dependency (
    column_name VARCHAR(200) REFERENCES Master_RnR_Template(column_name),
    dependent_column_name VARCHAR(200) REFERENCES Master_RnR_Template(column_name),
    UNIQUE (column_name, dependent_column_name)
);
