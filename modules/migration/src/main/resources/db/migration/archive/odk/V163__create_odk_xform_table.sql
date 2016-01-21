DROP TABLE IF EXISTS odkxform;
CREATE TABLE odkxform
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

ALTER TABLE odkxform
ADD CONSTRAINT odkxform_pkey
PRIMARY KEY (id);

