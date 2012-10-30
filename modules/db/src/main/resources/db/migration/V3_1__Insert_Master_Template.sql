-- insert into Open_LMIS_User (user_name, password, role) values('Admin123', 'Admin123','ADMIN');
-- insert into Open_LMIS_User (user_name, password, role) values('User123', 'User123','USER');
insert into Master_Program_Template(
    field_name, description,   field_position,  field_label,  default_value,     data_source, formula,  field_indicator, isUsed, isVisible)

    values ('Medicine_Name','First test medicine',1,'Medicine Name', 'M','Derived','a+b+c', 'X', true,false);




