/* ODK Account */
DROP TABLE IF EXISTS odk_account;
CREATE TABLE odk_account
(
  id            serial       NOT NULL,
  userid        integer,
  deviceid     varchar(30)   NOT NULL,
  simserial    varchar(30),
  phonenumber  varchar(15),
  subscriberid  varchar(20),
  odkusername	 varchar(20),
  odkemail      varchar(30),
  active        boolean       NOT NULL,
  comment       text,
  createdby     integer,
  createddate   timestamp    DEFAULT now(),
  modifiedby    integer,
  modifieddate  timestamp    DEFAULT now()
);

ALTER TABLE odk_account
ADD CONSTRAINT odk_account_pkey
PRIMARY KEY (id);



/* ODK Submission */

DROP TABLE IF EXISTS odk_submission;
CREATE TABLE odk_submission
(
  id            serial       NOT NULL,
  odkaccountid   int,
  formbuildid   varchar(40)   NOT NULL,
  instanceid    varchar(45)   NOT NULL,
  active        boolean       NOT NULL,
  comment       text,
  createdby     integer,
  createddate   timestamp    DEFAULT now(),
  modifiedby    integer,
  modifieddate  timestamp    DEFAULT now()
);

ALTER TABLE odk_submission
ADD CONSTRAINT odk_submission_pkey
PRIMARY KEY (id);

ALTER TABLE odk_submission
ADD CONSTRAINT odk_account_id_fkey FOREIGN KEY (odkaccountid)
REFERENCES odk_account (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

/* ODK Submission Data*/

DROP TABLE IF EXISTS odk_submission_data;
CREATE TABLE odk_submission_data
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

ALTER TABLE odk_submission_data
ADD CONSTRAINT odk_submission_data_pkey
PRIMARY KEY (id);

ALTER TABLE odk_submission_data
ADD CONSTRAINT odk_submission_id_fkey FOREIGN KEY (odksubmissionid)
REFERENCES odk_submission (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

/* ODK XForms*/
DROP TABLE IF EXISTS odk_xform;
CREATE TABLE odk_xform
(
  id            serial       NOT NULL,
  formid        varchar(50)  NOT NULL,
  name          varchar(50)  NOT NULL,
  version       varchar(10)  NOT NULL,
  hash          varchar(50)  ,
  descriptiontext varchar(400),
  downloadurl   varchar(150) NOT NULL,
  xmlstring     text NOT NULL,
  active        boolean       NOT NULL,
  createdby     integer,
  createddate   timestamp    DEFAULT now(),
  modifiedby    integer,
  modifieddate  timestamp    DEFAULT now()
);

ALTER TABLE odk_xform
ADD CONSTRAINT odk_xform_pkey
PRIMARY KEY (id);





