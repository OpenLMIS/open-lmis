-- Index: i_mv_user_geographic_zones_userid
DROP INDEX IF EXISTS i_mv_user_geographic_zones_userid;

-- Table: mv_user_geographic_zones
 DROP TABLE IF EXISTS mv_user_geographic_zones;

CREATE TABLE mv_user_geographic_zones
(
  userid integer NOT NULL,
  supervisorynodeid integer,
  geographiczoneid integer NOT NULL,
  levelid integer NOT NULL,
  programid integer NOT NULL,
  modifieddate timestamp without time zone DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE mv_user_geographic_zones
  OWNER TO postgres;

-- Index: i_mv_user_geographic_zones_userid
CREATE INDEX i_mv_user_geographic_zones_userid
  ON mv_user_geographic_zones
  USING btree
  (userid);

