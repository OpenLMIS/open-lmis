delete from rights where name = 'MANAGE_SEASONALITY_RATIONING';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('MANAGE_SEASONALITY_RATIONING','ADMIN','right.admin.seasonality.rationing','Permission to manage seasonality rationing ');