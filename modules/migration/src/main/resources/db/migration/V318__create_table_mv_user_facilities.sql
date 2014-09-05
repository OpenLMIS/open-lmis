-- Index: i_mv_user_facilities_userid
DROP INDEX IF EXISTS i_mv_user_facilities_userid;
DROP VIEW IF EXISTS vw_user_facilities_2;

-- Table: mv_user_facilities
DROP TABLE IF EXISTS mv_user_facilities;

CREATE TABLE mv_user_facilities
(
  facility_id integer NOT NULL,
  district_id integer NOT NULL,
  requisition_group_id integer NOT NULL,
  user_id integer NOT NULL,
  program_id integer NOT NULL,
  modifieddate timestamp without time zone DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mv_user_facilities
  OWNER TO postgres;

-- Index: i_mv_user_facilities_userid

CREATE INDEX i_mv_user_facilities_userid
  ON mv_user_facilities
  USING btree
  (user_id);
