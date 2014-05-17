CREATE TABLE equipment_service_vendor_users(
  id                        SERIAL PRIMARY KEY ,
  userId                    INTEGER NOT NULL REFERENCES users(id),
  vendorId                  INTEGER NOT NULL REFERENCES equipment_service_vendors(id),


  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);