DROP TABLE IF EXISTS odk_xform_survey_type;
CREATE TABLE odk_xform_survey_type
(
  id                                 serial       NOT NULL,
  survey_name                        varchar(400)  NOT NULL,
  number_of_questions                integer NOT NULL,
  active                             boolean NOT NULL,
  comment TEXT,
  createdby                         integer,
  createddate                       timestamp    DEFAULT now(),
  modifiedby                        integer,
  modifieddate                      timestamp    DEFAULT now()
);

ALTER TABLE odk_xform_survey_type
ADD CONSTRAINT odk_xform_survey_type_pkey
PRIMARY KEY (id);