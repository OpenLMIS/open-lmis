delete from rights where name = 'DELETE_REQUISITION';

INSERT INTO rights(name, righttype, description) VALUES
 ('DELETE_REQUISITION','REQUISITION','Permission to delete requisitions');