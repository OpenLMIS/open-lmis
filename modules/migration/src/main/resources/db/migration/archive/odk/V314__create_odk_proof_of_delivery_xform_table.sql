DROP TABLE IF EXISTS odk_proof_of_delivery_xform;
CREATE TABLE odk_proof_of_delivery_xform
(
   id            serial  NOT NULL,
   odkxformid    integer NOT NULL,
   facilityid    integer NOT NULL,
   programid     integer NOT NULL,
   districtid    integer NOT NULL,
   periodid      integer NOT NULL,
   rnrid         integer NOT NULL,
   active        boolean,
   comment       text,
   createdby     integer,
   createddate   timestamp    DEFAULT now(),
   modifiedby    integer,
   modifieddate  timestamp    DEFAULT now()

);

ALTER TABLE odk_proof_of_delivery_xform
ADD CONSTRAINT odk_proof_of_delivery_xform_pkey
PRIMARY KEY (id);

ALTER TABLE odk_proof_of_delivery_xform
  ADD CONSTRAINT odk_proof_of_delivery_odk_xform_id_fkey FOREIGN KEY (odkxformid)
  REFERENCES odk_xform (id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE odk_proof_of_delivery_xform
  ADD CONSTRAINT odk_proof_of_delivery_facility_id_fkey FOREIGN KEY (facilityid)
  REFERENCES facilities (id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE odk_proof_of_delivery_xform
  ADD CONSTRAINT odk_proof_of_delivery_program_id_fkey FOREIGN KEY (programid)
  REFERENCES programs (id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE odk_proof_of_delivery_xform
  ADD CONSTRAINT odk_proof_of_delivery_district_id_fkey FOREIGN KEY (districtid)
  REFERENCES geographic_zones(id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE odk_proof_of_delivery_xform
  ADD CONSTRAINT odk_proof_of_delivery_period_id_fkey FOREIGN KEY (periodid)
  REFERENCES processing_periods(id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE odk_proof_of_delivery_xform
ADD CONSTRAINT odk_proof_of_delivery_rnr_id_fkey FOREIGN KEY (rnrid)
REFERENCES requisitions(id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

COMMIT;