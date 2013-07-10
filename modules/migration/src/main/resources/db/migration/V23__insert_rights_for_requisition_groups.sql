delete from rights where name = 'MANAGE_REQUISITION_GROUP';

INSERT INTO rights(name, adminRight,description) VALUES
 ('MANAGE_REQUISITION_GROUP',TRUE,'Permission to manage requisition groups.');