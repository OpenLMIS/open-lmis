delete from rights where name = 'MANAGE_REQUISITION_GROUP';

INSERT INTO rights(name, righttype, description) VALUES
 ('MANAGE_REQUISITION_GROUP','ADMIN','Permission to manage requisition groups.');