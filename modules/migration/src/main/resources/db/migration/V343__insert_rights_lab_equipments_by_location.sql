delete from rights where name = 'VIEW_LAB_EQUIPMENTS_BY_LOCATION_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_LAB_EQUIPMENTS_BY_LOCATION_REPORT','REPORT','Permission to view lab equipments by location Report');