CREATE TABLE equipment_maintenance_logs(
  id                        SERIAL PRIMARY KEY ,
  userId                    INTEGER NOT NULL REFERENCES users(id),
  vendorId                  INTEGER NOT NULL REFERENCES equipment_service_vendors(id),
  maintenanceDate           DATE,
  servicePerformed          VARCHAR (2000),
  finding                   VARCHAR (2000),
  recommendation            VARCHAR (2000),
  requestId                 INTEGER NULL REFERENCES equipment_maintenance_requests(id),
  nextVisitDate             DATE,

  createdBy                 INTEGER,
  createdDate               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy                INTEGER,
  modifiedDate              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);