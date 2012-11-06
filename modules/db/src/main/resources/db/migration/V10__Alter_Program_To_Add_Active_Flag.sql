alter table PROGRAM add PRIMARY KEY (id);
alter table PROGRAM add column active boolean;
update PROGRAM set active='true';
