insert into Master_RnR_Template(
    column_name, description, column_position,
    column_label, default_value, data_source, formula,
    column_indicator, is_used, is_visible)
    values ('foo','foo is a column', 1,
    'Foo', 'foo','Derived','a+b+c',
    'F', false, false);

insert into Master_RnR_Template(
    column_name, description, column_position,
    column_label, default_value, data_source, formula,
    column_indicator, is_used, is_visible)
    values ('bar','bar is not foo', 1,
    'Bar', 'bar','Derived','a+b+c',
    'B', true, false);
