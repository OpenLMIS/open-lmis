DROP TABLE IF EXISTS lot_conflicts;

CREATE TABLE lot_conflicts(
  id serial NOT NULL,
  expirationdate TIMESTAMP WITH TIME ZONE,
  lotid INTEGER REFERENCES lots(id),
  createdby INTEGER ,
  createddate TIMESTAMP WITH TIME ZONE DEFAULT now(),
  modifiedby INTEGER ,
  modifieddate TIMESTAMP WITH TIME ZONE DEFAULT now()
)