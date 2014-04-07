DROP TABLE IF EXISTS odkaccount;
CREATE TABLE odkaccount
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

ALTER TABLE odkaccount
ADD CONSTRAINT odkaccount_pkey
PRIMARY KEY (id);

