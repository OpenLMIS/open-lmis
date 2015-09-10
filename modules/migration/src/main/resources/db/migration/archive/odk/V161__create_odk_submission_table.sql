DROP TABLE IF EXISTS odksubmission;
CREATE TABLE odksubmission
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

ALTER TABLE odksubmission
   ADD CONSTRAINT odksubmission_pkey
   PRIMARY KEY (id);

ALTER TABLE odksubmission
ADD CONSTRAINT odkaccount_id_fkey FOREIGN KEY (odkaccountid)
REFERENCES odkaccount (id)
ON UPDATE NO ACTION
ON DELETE NO ACTION;

COMMIT;

CREATE UNIQUE INDEX odksubmission_instance_id ON odksubmission (instanceid);
COMMIT;

