delete from rights where name = 'VIEW_TIMELINESS_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_TIMELINESS_REPORT','REPORT','Permission to view Timeliness Report');