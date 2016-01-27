delete from rights where name = 'MANAGE_REQ_GRP_PROG_SCHEDULE';

INSERT INTO rights(name, righttype, description) VALUES
 ('MANAGE_REQ_GRP_PROG_SCHEDULE','ADMIN','Permission to manage requisition groups programs schedule.');