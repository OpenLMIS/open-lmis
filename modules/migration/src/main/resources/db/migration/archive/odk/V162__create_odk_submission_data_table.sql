DROP TABLE IF EXISTS odksubmissiondata;
CREATE TABLE odksubmissiondata
(
   id            serial       NOT NULL,
   odksubmissionid integer    NOT NULL,
   facilityid    integer      NOT NULL,
   gpslatitude   numeric(20,10),
   gpslongitude  numeric(20,10),
   gpsaltitude   numeric(20,10),
   gpsaccuracy   numeric(20,10),
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