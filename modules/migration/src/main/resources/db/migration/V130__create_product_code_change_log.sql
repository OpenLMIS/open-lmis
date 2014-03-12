CREATE TABLE product_code_change_log
(
  program character varying(4),
  old_code character varying(12),
  new_code character varying(12),
  product character varying(200),
  unit character varying(200),
  changeddate timestamp,
  migrated boolean DEFAULT 'N'
)
WITH (
  OIDS=FALSE
);
ALTER TABLE product_code_change_log
  OWNER TO postgres;



