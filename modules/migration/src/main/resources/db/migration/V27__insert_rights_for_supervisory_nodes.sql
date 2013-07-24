delete from rights where name = 'MANAGE_SUPERVISORY_NODE';

INSERT INTO rights(name, righttype, description) VALUES
 ('MANAGE_SUPERVISORY_NODE','ADMIN','Permission to manage supervisory nodes.');