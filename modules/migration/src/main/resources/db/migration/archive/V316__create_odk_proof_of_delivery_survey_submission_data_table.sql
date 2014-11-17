DROP TABLE IF EXISTS odk_proof_of_delivery_submission_data;
CREATE TABLE odk_proof_of_delivery_submission_data
(
   id            serial  NOT NULL,
   rnrid         integer NOT NULL,
   productid     integer NOT NULL,
   productcode   text NOT NULL, -- have this for the sake of ease when getting the xml submission
   quantityreceived integer,
   allquantitydelivered boolean NOT NULL,
   discrepancyAmount integer,
   commentforshortfallitem text,
   firstpicture  bytea,
   secondpicture bytea,
   thirdpicture  bytea,
   receivedby text NOT NULL,
   active        boolean,
   comment       text,
   createdby     integer,
   createddate   timestamp    DEFAULT now(),
   modifiedby    integer,
   modifieddate  timestamp    DEFAULT now()

);


ALTER TABLE odk_proof_of_delivery_submission_data
ADD CONSTRAINT odk_proof_of_delivery_submission_data_pkey
PRIMARY KEY (id);

ALTER TABLE odk_proof_of_delivery_submission_data
  ADD CONSTRAINT odk_proof_of_delivery_submission_data_product_id_fkey FOREIGN KEY (productid)
  REFERENCES products (id)
  ON UPDATE NO ACTION
  ON DELETE NO ACTION;

ALTER TABLE odk_proof_of_delivery_submission_data
ADD CONSTRAINT odk_proof_of_delivery_submission_data_rnr_id_fkey FOREIGN KEY (rnrid)
REFERENCES requisitions(id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

COMMIT;