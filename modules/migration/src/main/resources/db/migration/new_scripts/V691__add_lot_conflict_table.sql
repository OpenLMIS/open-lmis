DROP TABLE IF EXISTS facility_lot_conflicts;

CREATE TABLE facility_lot_conflicts(
  id INTEGER NOT NULL PRIMARY KEY,
  facilityid INTEGER NOT NULL REFERENCES facilities(id),
  productid INTEGER NOT NULL REFERENCES products(id),
  lotnumber TEXT,
  expirationdate TIMESTAMP WITH TIME ZONE,
  lotid INTEGER REFERENCES lots(id),
  createdby INTEGER ,
  createddate TIMESTAMP WITH TIME ZONE DEFAULT now(),
  modifiedby INTEGER ,
  modifieddate TIMESTAMP WITH TIME ZONE DEFAULT now(),
)