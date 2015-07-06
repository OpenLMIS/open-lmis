delete from rights where name = 'VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT';

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT','REPORT','Permission to view cold chain equipment list Report', 'right.report.coldchain.equipment');

