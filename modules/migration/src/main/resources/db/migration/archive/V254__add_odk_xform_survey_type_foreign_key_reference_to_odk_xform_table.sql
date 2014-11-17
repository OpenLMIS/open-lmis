ALTER  TABLE odk_xform
ALTER COLUMN odk_xform_survey_type_id SET NOT NULL;

ALTER TABLE odk_xform
ADD CONSTRAINT odk_xform_odk_xform_survey_type_fk
FOREIGN KEY (odk_xform_survey_type_id)
REFERENCES odk_xform_survey_type(id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

