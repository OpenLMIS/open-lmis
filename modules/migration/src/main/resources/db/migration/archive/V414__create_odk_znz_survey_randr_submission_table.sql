DROP TABLE IF EXISTS odk_znz_survey_randr_submission;
CREATE TABLE odk_znz_survey_randr_submission
(
  id                                   SERIAL   NOT NULL,
  odksubmissionid                      INTEGER  NOT NULL,
  randravailable                       SMALLINT NOT NULL,
  openingendingbalancesequal           SMALLINT NOT NULL,
  endingbalancecorrespondstoledger     SMALLINT NOT NULL,
  consumptionestimationcorrectlyfilled SMALLINT NOT NULL,
  stockoutadjustmentcorrect            SMALLINT NOT NULL,
  quantityrequiredcorrectlyfilled      SMALLINT NOT NULL,
  columnofcostsfilledcorrectly         SMALLINT NOT NULL,
  randrformsfilled                     SMALLINT NOT NULL,
  totalpercentage                      REAL     NOT NULL,
  comment                              TEXT,
  createdby                            INTEGER,
  createddate                          TIMESTAMP DEFAULT now(),
  modifiedby                           INTEGER,
  modifieddate                         TIMESTAMP DEFAULT now()

);

ALTER TABLE odk_znz_survey_randr_submission
ADD CONSTRAINT odk_znz_survey_randr_submission_pkey
PRIMARY KEY (id);

ALTER TABLE odk_znz_survey_randr_submission
ADD CONSTRAINT odk_znz_survey_randr_submission_odksubmission_id_fkey FOREIGN KEY (odksubmissionid)
REFERENCES odk_submission (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

COMMIT;