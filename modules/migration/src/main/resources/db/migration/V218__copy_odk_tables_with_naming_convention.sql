/* ODK Account Table */
CREATE TABLE odk_account ( LIKE odkaccount INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES );
INSERT INTO odk_account
SELECT *
FROM odkaccount ;

/* ODK Submission Table*/

CREATE TABLE odk_submission ( LIKE odksubmission INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES );
INSERT INTO odk_submission
SELECT *
FROM odksubmission ;

ALTER TABLE odk_submission
ADD CONSTRAINT odk_account_id_fkey FOREIGN KEY (odkaccountid)
REFERENCES odk_account (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

/* ODK Submission Data*/
CREATE TABLE odk_submission_data ( LIKE odksubmissiondata INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES );
INSERT INTO odk_submission_data
SELECT *
FROM odksubmissiondata ;

ALTER TABLE odk_submission_data
ADD CONSTRAINT odk_submission_id_fkey FOREIGN KEY (odksubmissionid)
REFERENCES odk_submission (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;
/*ODK XForms */
CREATE TABLE odk_xform ( LIKE odkxform INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES );
INSERT INTO odk_xform
SELECT *
FROM odkxform ;

/* Finally remove the unused tables */

DROP TABLE  IF EXISTS  odkaccount CASCADE;
DROP TABLE  IF EXISTS  odksubmission CASCADE;
DROP TABLE  IF EXISTS  odksubmissiondata CASCADE;
DROP TABLE  IF EXISTS  odkxform CASCADE;













