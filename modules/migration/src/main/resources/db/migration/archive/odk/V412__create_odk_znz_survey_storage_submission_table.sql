DROP TABLE IF EXISTS odk_znz_survey_storage_submission;
CREATE TABLE odk_znz_survey_storage_submission
(
  id                                SERIAL   NOT NULL,
  odksubmissionid                   INTEGER  NOT NULL,
  facilityid                        INTEGER  NOT NULL,
  adequatestoragespace              SMALLINT NOT NULL,
  adequateshelves                   SMALLINT NOT NULL,
  storeroomclean                    SMALLINT NOT NULL,
  productsarrangedappropriately     SMALLINT NOT NULL,
  productsstoredissued              SMALLINT NOT NULL,
  medicinesstoredseparately         SMALLINT NOT NULL,
  coldchainfollowed                 SMALLINT NOT NULL,
  productsfreefromdusts             SMALLINT NOT NULL,
  productsfreefrommoisture          SMALLINT NOT NULL,
  productsfreefromsunlight          SMALLINT NOT NULL,
  storeroompreventedfrominfestation SMALLINT NOT NULL,
  adequatesecurity                  SMALLINT NOT NULL,
  fireextinguisheravailable         SMALLINT NOT NULL,
  storeroomconditionconductive      SMALLINT NOT NULL,
  controlforunauthorizedpersonnel   SMALLINT NOT NULL,
  totalpercentage                   REAL     NOT NULL,
  gpslatitude                       NUMERIC(20, 10),
  gpslongitude                      NUMERIC(20, 10),
  gpsaltitude                       NUMERIC(20, 10),
  gpsaccuracy                       NUMERIC(20, 10),
  firstpicture                      BYTEA,
  secondpicture                     BYTEA,
  thirdpicture                      BYTEA,
  comment                           TEXT,
  createdby                         INTEGER,
  createddate                       TIMESTAMP DEFAULT now(),
  modifiedby                        INTEGER,
  modifieddate                      TIMESTAMP DEFAULT now()

);

ALTER TABLE odk_znz_survey_storage_submission
ADD CONSTRAINT odk_znz_survey_storage_submission_pkey
PRIMARY KEY (id);

ALTER TABLE odk_znz_survey_storage_submission
ADD CONSTRAINT odk_znz_survey_storage_submission_facility_id_fkey FOREIGN KEY (facilityid)
REFERENCES facilities (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

ALTER TABLE odk_znz_survey_storage_submission
ADD CONSTRAINT odk_znz_survey_storage_submission_odksubmission_id_fkey FOREIGN KEY (odksubmissionid)
REFERENCES odk_submission (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

COMMIT;