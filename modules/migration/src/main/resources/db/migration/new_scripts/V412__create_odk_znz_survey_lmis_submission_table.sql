DROP TABLE IF EXISTS odk_znz_survey_lmis_submission;
CREATE TABLE odk_znz_survey_lmis_submission
(
  id                                    SERIAL   NOT NULL,
  odksubmissionid                       INTEGER  NOT NULL,
  storeledgeravailable                  SMALLINT NOT NULL,
  storeledgersinstoreroom               SMALLINT NOT NULL,
  bincardsavailable                     SMALLINT NOT NULL,
  bincardskeptwithproducts              SMALLINT NOT NULL,
  endingbalancesequaltostocks           SMALLINT NOT NULL,
  lossesadjustmentscorrectlyfilled      SMALLINT NOT NULL,
  ledgersbincardsfilledcorrectly        SMALLINT NOT NULL,
  physicalstockcountsexercisesconducted SMALLINT NOT NULL,
  ddravailable                          SMALLINT NOT NULL,
  invoiceskeptinfile                    SMALLINT NOT NULL,
  lastsupervisionvisitinfile            SMALLINT NOT NULL,
  totalpercentage                       REAL     NOT NULL,
  comment                               TEXT,
  createdby                             INTEGER,
  createddate                           TIMESTAMP DEFAULT now(),
  modifiedby                            INTEGER,
  modifieddate                          TIMESTAMP DEFAULT now()

);

ALTER TABLE odk_znz_survey_lmis_submission
ADD CONSTRAINT odk_znz_survey_lmis_submission_pkey
PRIMARY KEY (id);

ALTER TABLE odk_znz_survey_lmis_submission
ADD CONSTRAINT odk_znz_survey_lmis_submission_odksubmission_id_fkey FOREIGN KEY (odksubmissionid)
REFERENCES odk_submission (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

COMMIT;