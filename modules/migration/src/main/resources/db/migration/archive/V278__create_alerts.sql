
-- DROP TABLE alerts;
DROP TABLE IF EXISTS alerts;

CREATE TABLE alerts
(
  alerttype character varying(50),
  display_section character varying(50),
  email boolean,
  sms boolean,
  detail_table character varying(50),
  sms_msg_template_key character varying(250),
  email_msg_template_key character varying(250),
  CONSTRAINT alerts_pk PRIMARY KEY (alerttype)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE alerts
  OWNER TO postgres;
