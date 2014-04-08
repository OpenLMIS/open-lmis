DROP TABLE IF EXISTS odksubmissiondata;
CREATE TABLE odksubmissiondata
(
   id            serial       NOT NULL,
   odksubmissionid integer    NOT NULL,
   facilityid    integer      NOT NULL,
   gpslatitude   double precision,
   gpslongitude  double precision,
   gpsaltitude   double precision,
   gpsaccuracy   numeric(2,2),
   firstpicture  bytea,
   secondpicture bytea,
   thirdpicture  bytea,
   fourthpicture bytea,
   fifthpicture  bytea,
   comment       text,
   createdby     integer,
   createddate   timestamp    DEFAULT now(),
   modifiedby    integer,
   modifieddate  timestamp    DEFAULT now()

);

ALTER TABLE odksubmissiondata
ADD CONSTRAINT odksubmissiondata_pkey
PRIMARY KEY (id);

ALTER TABLE odksubmissiondata
  ADD CONSTRAINT odksubmission_id_fkey FOREIGN KEY (odksubmissionid)
  REFERENCES odksubmission (id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

COMMIT;