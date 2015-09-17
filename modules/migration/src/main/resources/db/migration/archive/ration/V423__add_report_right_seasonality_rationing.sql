delete from rights where name = 'VIEW_SEASONALITY_RATIONING_REPORT';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('VIEW_SEASONALITY_RATIONING_REPORT','REPORT','right.report.seasonality.rationing','Permission to view seasonality rationing Report');