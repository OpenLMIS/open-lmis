delete from rights where name = 'VIEW_STOCKED_OUT_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_STOCKED_OUT_REPORT','REPORT','Permission to view stocked out commodity Report');